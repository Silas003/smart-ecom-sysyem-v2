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
import com.amalitech.demo.utils.Sorter;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final Sorter<User> sorter;
    private final JwtService jwtService;

    @Override
    @CacheEvict(value = {"user"}, allEntries = true)
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
    public Page<UserResponse> getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("username").ascending());
        Page<User> page = userRepository.findAll(pageable);
        List<User> content = page.getContent();

        long total = page.getTotalElements();
        return new PageImpl<>(userMapper.toResponse(content), pageable, total);
    }

    @Override
    @CachePut(value = "user", key = "#id")
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String password = PasswordUtils.hashPassword(userRequest.getPassword());

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(password);
        existingUser.setUserRole(UserRole.valueOf(userRequest.getUserRole()));
        userRepository.save(existingUser);
        return userMapper.toResponse(existingUser);
    }

    @Override
    @CacheEvict(value = "user", key = "#id")
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        userRepository.deleteById(existingUser.getId());
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
                String token = jwtService.generateToken(user);
                return new LoginResponse(token, userResponse);
            }
        } else {
            throw new IllegalArgumentException("User with given email does not exist");
        }
    }
}
