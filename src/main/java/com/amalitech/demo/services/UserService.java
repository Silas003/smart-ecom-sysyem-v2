package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRequest;
import com.amalitech.demo.dto.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.utils.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(UserRequest userRequest) {
        if( userRepository.findByEmail(userRequest.getEmail()) != null || userRepository.findByUsername(userRequest.getUsername()) != null){
            throw new IllegalArgumentException("User with given email or username already exists");
        }
        User user = userMapper.toEntity(userRequest);
        String password = PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(password);
        return userMapper.toResponse(userRepository.save(user));
    }

    public UserResponse getUserById(Long id) {
       User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
       return userMapper.toResponse(user);
    }
    public User getUserByIdForReview(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

    }

    public List<UserResponse> getAllUsers() {
        return userMapper.toResponse(userRepository.findAll());
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setUserRole(userRequest.getUserRole());
        User resUser= userRepository.save(existingUser);
        return userMapper.toResponse(resUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        userRepository.delete(existingUser);
    }
}
