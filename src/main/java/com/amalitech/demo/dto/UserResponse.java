package com.amalitech.demo.dto;



public record UserResponse(
        Long id,
        String username,
        String email,
        String userRole) {
}
