package com.amalitech.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {
    @Size(min=5, message="Username must be at least 5 characters long")
    @NotBlank(message="Username cannot be blank")
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank(message="Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message="User role cannot be blank")
    private String userRole;
}
