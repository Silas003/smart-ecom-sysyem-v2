package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.dto.request.UserLoginRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.LoginResponse;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.exceptions.UserExists;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.services.interfaces.UserServiceInterface;
import com.amalitech.demo.utils.PasswordUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    @CacheEvict(value = {"user"}, allEntries = true)
    @Transactional
    public void createUser(UserRequest userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail()) || userRepository.existsByUsername(userRequest.getUsername())) {
            throw new UserExists("User with given email or username already exists");
        }
        User user = userMapper.toEntity(userRequest);
        String password = PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(password);
        userRepository.save(user);
    }

    @Override
    @Cacheable(value = "user", key = "#id")
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public User getUserByIdForReview(Long id) {
        // no cache to avoid confusion with DTO vs entity
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    @Cacheable(value = "users", keyGenerator = "userKeyGenerator")
    public Page<UserResponse> getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("username").ascending());
        Page<User> page = userRepository.findAll(pageable);
        List<User> content = page.getContent();

        long total = page.getTotalElements();
        return new PageImpl<>(userMapper.toResponse(content), pageable, total);
    }

    @Override
    @Caching(put = {@CachePut(value = "user", key = "#id"),}, evict = {@CacheEvict(value = "users", allEntries = true)}

    )
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

        String password = PasswordUtils.hashPassword(userRequest.getPassword());

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(password);
        existingUser.setUserRole(UserRole.valueOf(userRequest.getUserRole()));
        userRepository.save(existingUser);
        return userMapper.toResponse(existingUser);
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "user", key = "#id"), @CacheEvict(value = "users", allEntries = true)})
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("user not found"));

        userRepository.deleteById(existingUser.getId());
    }

    @CachePut(value = "userCount", key = "'totalUsers'")
    @Override
    public Page<UserResponse> getInactiveUsers(LocalDateTime date, Pageable pageable) {
        return userRepository.findInactiveUsers(date, pageable).map(userMapper::toResponse);
    }

    @Override
    public LoginResponse refreshToken(String refreshToken, HttpServletResponse response) {
        log.info("[REFRESH] Attempting to refresh token");
        try {
            String subject = jwtService.extractSubject(refreshToken);
            User user = userRepository.findByEmail(subject).orElseThrow(() -> new EntityNotFoundException("User not found"));

            Map<String, String> newTokens = jwtService.generateToken(user);
            String newAccessToken = newTokens.get("access");
            String newRefreshToken = newTokens.get("refresh");

            setCookie(newRefreshToken, response);
            log.info("[REFRESH] Token refreshed successfully for user: {}", subject);

            return new LoginResponse(newAccessToken, userMapper.toResponse(user));
        } catch (Exception e) {
            log.error("[REFRESH] Token refresh failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @CachePut(value = "user", key = "result.id")
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public LoginResponse loginUser(UserLoginRequest userRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.email(), userRequest.password()));
            if (authentication.isAuthenticated()) {
                User user = (User) authentication.getPrincipal();
                Map<String, String> tokens = jwtService.generateToken(user);
                String accessToken = tokens.get("access");
                String refreshToken = tokens.get("refresh");

                setCookie(refreshToken, response);

                return new LoginResponse(accessToken, userMapper.toResponse(user));
            } else {
                throw new IllegalArgumentException("Invalid credentials");
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void setCookie(String token, HttpServletResponse response) {
        if (response == null || token == null || token.isEmpty()) {
            return;
        }
        try {
            Cookie refreshTokenCookie = new Cookie("refresh", token);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60);
            refreshTokenCookie.setAttribute("SameSite", "Strict");

            response.addCookie(refreshTokenCookie);
            log.debug("[COOKIE] Refresh token cookie set successfully");
        } catch (Exception e) {
            log.error("[COOKIE] Failed to set refresh token cookie: {}", e.getMessage(), e);
        }
    }

    /**
     * Clears the refresh token cookie on logout
     * Used to remove the refresh token from client storage
     */
    public void clearRefreshCookie(HttpServletResponse response) {
        if (response == null) {
            return;
        }
        try {
            Cookie refreshTokenCookie = new Cookie("refresh", null);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(0);

            response.addCookie(refreshTokenCookie);
        } catch (Exception e) {
        }
    }

}
