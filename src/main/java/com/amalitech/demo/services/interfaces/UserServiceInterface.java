package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.models.User;

import java.util.List;

public interface UserServiceInterface {
    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    User getUserByIdForReview(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    UserResponse loginUser(UserRequest userRequest);
}
