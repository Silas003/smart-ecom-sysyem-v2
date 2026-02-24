package com.amalitech.demo.services.interfaces;

import com.amalitech.demo.dto.request.UserLoginRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.LoginResponse;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.models.User;
import jakarta.servlet.http.HttpServletResponse;
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

    LoginResponse loginUser(UserLoginRequest userRequest, HttpServletResponse response);

    Page<UserResponse> getInactiveUsers(LocalDateTime date, Pageable pageable);

    LoginResponse refreshToken(String refreshToken, HttpServletResponse response);

    UserResponse getCurrentUser(String email);

    void setCookie(String token, HttpServletResponse response);

    void clearRefreshCookie(HttpServletResponse response);
}
