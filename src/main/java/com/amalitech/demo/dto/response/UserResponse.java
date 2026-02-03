package com.amalitech.demo.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User response model")
public record UserResponse(
        Long id,
        String username,
        String email,
        String userRole) {
}
