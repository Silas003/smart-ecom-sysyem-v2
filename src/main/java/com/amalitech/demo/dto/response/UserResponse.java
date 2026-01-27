package com.amalitech.demo.dto.response;


public record UserResponse(
        Long id,
        String username,
        String email,
        String userRole) {
}
