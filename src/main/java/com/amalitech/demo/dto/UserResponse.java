package com.amalitech.demo.dto;


import lombok.Getter;


public record UserResponse(
        Long id,
        String username,
        String email,
        String userRole) {
}
