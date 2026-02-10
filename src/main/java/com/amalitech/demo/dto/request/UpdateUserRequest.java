package com.amalitech.demo.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import com.amalitech.demo.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "User update payload")
public class UpdateUserRequest {
    private Long id;

    @Size(min=5, message="Username must be at least 5 characters long")
    private String username;

    @Email
    private String email;

    @StrongPassword
    private String password;

    private String userRole;

}
