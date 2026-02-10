package com.amalitech.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import com.amalitech.demo.validation.StrongPassword;
import com.amalitech.demo.validation.UniqueUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "User request payload for create/login operations")
@UniqueUser
public class UserRequest {
    @Size(min=5, message="Username must be at least 5 characters long")
    @NotBlank(message="Username cannot be blank")
    @Schema(description = "Desired username", example = "alice01")
    private String username;

    @Email
    @NotBlank
    @Schema(description = "User email address", example = "alice@example.com")
    private String email;

    @NotBlank(message="Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @StrongPassword
    @Schema(description = "Plain-text password (will be hashed)", example = "P@ssw0rd123")
    private String password;

    @NotBlank(message="User role cannot be blank")
    @Schema(description = "Role for the user: admin | customer | seller", example = "customer")
    private String userRole;

}
