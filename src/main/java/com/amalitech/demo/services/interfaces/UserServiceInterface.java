package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.UserLoginRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserServiceInterface {
    void createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    User getUserByIdForReview(Long id);

    Page<UserResponse> getAllUsers(int pageNumber, int pageSize);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    UserResponse loginUser(UserLoginRequest userRequest);
}
