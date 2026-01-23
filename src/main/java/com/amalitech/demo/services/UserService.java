package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRequest;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
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

    public User createUser(UserRequest userRequest) {
        if( userRepository.findByEmail(userRequest.getEmail()) != null || userRepository.findByUsername(userRequest.getUsername()) != null){
            throw new IllegalArgumentException("User with given email or username already exists");
        }
        User user = userMapper.toEntity(userRequest);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
       return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(userRequest.getPassword());
        existingUser.setUserRole(userRequest.getUserRole());

        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        userRepository.delete(existingUser);
    }
}
