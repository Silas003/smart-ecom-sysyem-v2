package com.amalitech.demo.services;

import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.services.interfaces.UserServiceInterface;
import com.amalitech.demo.utils.PasswordUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if( userRepository.findByEmail(userRequest.getEmail()) != null || userRepository.findByUsername(userRequest.getUsername()) != null){
            throw new IllegalArgumentException("User with given email or username already exists");
        }
        User user = userMapper.toEntity(userRequest);
        String password = PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(password);
        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse getUserById(Long id) {
       User user = userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
       return userMapper.toResponse(user);
    }
    @Override
    public User getUserByIdForReview(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponse(userRepository.findAll());
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String password = PasswordUtils.hashPassword(userRequest.getPassword());

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(password);
        existingUser.setUserRole(userRequest.getUserRole());
        User resUser= userRepository.save(existingUser);
        return userMapper.toResponse(resUser);
    }

    @Override
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        userRepository.delete(existingUser);
    }

    @Override
    public UserResponse loginUser(UserRequest userRequest) {
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        User user = userRepository.findByEmail(email);
        if(user != null){
          boolean authenticated =   PasswordUtils.verifyPassword(password, user.getPassword());
            if(!authenticated){
                throw new IllegalArgumentException("Invalid credentials");
            }
            else {
                return userMapper.toResponse(user);
            }
        }else{
            throw new IllegalArgumentException("User with given email does not exist");
        }
    }
}
