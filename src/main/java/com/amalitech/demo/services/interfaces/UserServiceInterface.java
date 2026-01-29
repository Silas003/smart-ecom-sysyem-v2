package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.models.User;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserServiceInterface {
    @Transactional(propagation = Propagation.REQUIRED)
    UserResponse createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    User getUserByIdForReview(Long id);

    List<UserResponse> getAllUsers();

    @Transactional(propagation = Propagation.MANDATORY)
    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    UserResponse loginUser(UserRequest userRequest);
}
