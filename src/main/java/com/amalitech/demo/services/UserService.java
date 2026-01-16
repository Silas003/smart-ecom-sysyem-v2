package com.amalitech.demo.services;

import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static  UserRepository userRepository = null;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public static User createUser(User user) {
        if( userRepository.findByEmail(user.getEmail()) != null|| userRepository.findByUsername(user.getUsername()) != null){
            throw new IllegalArgumentException("User with given email or username already exists");
        }
        return userRepository.save(user);
    }
    public static User getUserById(Long id) {
       return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public static List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());
        existingUser.setPassword(user.getPassword());
        existingUser.setUserRole(user.getUserRole());

        return userRepository.save(existingUser);
    }
}
