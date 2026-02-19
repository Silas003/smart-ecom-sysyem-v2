package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.UserLoginRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.LoginResponse;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface UserServiceInterface {
    void createUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    User getUserByIdForReview(Long id);

    Page<UserResponse> getAllUsers(int pageNumber, int pageSize);

    UserResponse updateUser(Long id, UserRequest userRequest);

    void deleteUser(Long id);

    LoginResponse loginUser(UserLoginRequest userRequest);

    Page<UserResponse> getInactiveUsers(LocalDateTime date, Pageable pageable);

    LoginResponse refreshToken(String refreshToken);
}
