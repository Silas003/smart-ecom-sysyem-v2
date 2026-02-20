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
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final JwtService jwtService;


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
    public LoginResponse refreshToken(String refreshToken) {
        String subject = jwtService.extractSubject(refreshToken);
        User user = userRepository.findByEmail(subject).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new LoginResponse(jwtService.generateToken(user), userMapper.toResponse(user));
    }

    @Override
    @CachePut(value = "user", key = "result.id")
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    @Override
    public LoginResponse loginUser(UserLoginRequest userRequest) {
        String email = userRequest.email();
        String password = userRequest.password();
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            boolean authenticated = PasswordUtils.verifyPassword(password, user.getPassword());
            if (!authenticated) {
                throw new IllegalArgumentException("Invalid credentials");
            } else {
                UserResponse userResponse = userMapper.toResponse(user);
                Map<String, String> token = jwtService.generateToken(user);
                return new LoginResponse(token, userResponse);
            }
        } else {
            throw new IllegalArgumentException("User with given email does not exist");
        }
    }
}
