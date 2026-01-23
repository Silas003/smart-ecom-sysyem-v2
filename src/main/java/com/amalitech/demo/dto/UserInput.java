package com.amalitech.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserInput {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @Email
    private String email;

    @NotNull
    @NotBlank
    private String password;

    private String userRole;
}
