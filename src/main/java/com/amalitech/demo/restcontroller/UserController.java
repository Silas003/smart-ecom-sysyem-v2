package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.request.UpdateUserRequest;
import com.amalitech.demo.dto.request.UserLoginRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.LoginResponse;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.security.JwtUtil;
import com.amalitech.demo.security.TokenBlacklistService;
import com.amalitech.demo.services.interfaces.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api/v1/users")
@AllArgsConstructor
@Tag(name = "Users", description = "User account management endpoints")
public class UserController {
    private final JwtService jwtService;
    private UserServiceInterface userService;
    private TokenBlacklistService tokenBlacklistService;

    @PreAuthorize("hasRole('admin')") // Only allow admins to access this endpoint
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    })
    public ResponseDto<Page<UserResponse>> getAllUsers(@RequestParam int page, @RequestParam int size) {
        Page<UserResponse> users = userService.getAllUsers(page, size);
        return new ResponseDto<>(HttpStatus.OK, "users retrieved", users);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by id", description = "Retrieve a single user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseDto<UserResponse> getUserById(@Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return new ResponseDto<>(HttpStatus.OK, "user retrieved", user);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user", description = "Update an existing user's data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<UserResponse> updateUser(@Parameter(description = "ID of the user to update", required = true) @PathVariable Long id, @RequestBody @Valid UpdateUserRequest userRequest) {
        // map UpdateUserRequest to UserRequest for service
        UserRequest ur = new UserRequest(
                userRequest.getUsername() == null ? "" : userRequest.getUsername(),
                userRequest.getEmail() == null ? "" : userRequest.getEmail(),
                userRequest.getPassword() == null ? "" : userRequest.getPassword(),
                userRequest.getUserRole() == null ? null : userRequest.getUserRole(),
                ""
        );
        UserResponse updatedUser = userService.updateUser(id, ur);
        return new ResponseDto<>(HttpStatus.OK, "user updated", updatedUser);

    }

    @PreAuthorize("hasRole('admin')") // Only allow admins to delete users
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID of the user to delete", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping()
    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        userService.createUser(userRequest);
        return new ResponseDto<>(HttpStatus.CREATED, "user created", null);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login user", description = "Authenticate user with credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseDto<LoginResponse> loginUser(@RequestBody @Valid UserLoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = userService.loginUser(request, response);
        return new ResponseDto<>(HttpStatus.OK, "user login successful", loginResponse);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Logout user", description = "Revoke the current JWT token and invalidate session")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful"),
            @ApiResponse(responseCode = "401", description = "No valid token found")
    })
    public ResponseDto<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        String token = JwtUtil.extractTokenFromRequest(request);

        if (token == null || token.isEmpty()) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST, "No token found in request", null);
        }

        try {
            tokenBlacklistService.blacklistToken(token);

            userService.clearRefreshCookie(response);

            return new ResponseDto<>(HttpStatus.OK, "Successfully logged out", "Token revoked and cookies cleared");
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR, "Logout failed", e.getMessage());
        }
    }


    @PreAuthorize("hasRole('admin')")
    @GetMapping("/inactive")
    @Operation(summary = "Get inactive users", description = "Retrieve users who haven't placed orders since a specific date")
    public ResponseDto<Page<UserResponse>> getInactiveUsers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<UserResponse> users = userService.getInactiveUsers(since, pageable);
        return new ResponseDto<>(HttpStatus.OK, "inactive users retrieved", users);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Refresh JWT token", description = "Generate a new JWT token using refresh token from HttpOnly cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing refresh token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseDto<LoginResponse> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        jakarta.servlet.http.Cookie[] cookies = request.getCookies();
        String refreshToken = null;

        if (cookies != null) {
            for (jakarta.servlet.http.Cookie cookie : cookies) {
                if ("refresh".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED, "Refresh token not found in cookies", null);
        }

        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            return new ResponseDto<>(HttpStatus.UNAUTHORIZED, "Refresh token has been revoked", null);
        }

        try {
            LoginResponse loginResponse = userService.refreshToken(refreshToken, response);
            return new ResponseDto<>(HttpStatus.OK, "Token refreshed successfully", loginResponse);
        } catch (IllegalArgumentException e) {
            return new ResponseDto<>(HttpStatus.BAD_REQUEST, "Invalid or expired refresh token", null);
        } catch (Exception e) {
            return new ResponseDto<>(HttpStatus.INTERNAL_SERVER_ERROR, "Error refreshing token", null);
        }
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    @Operation()
    public ResponseDto<UserResponse> getCurrentUser(HttpServletRequest request) {
        String token = JwtUtil.extractTokenFromRequest(request);
        if (token != null) {
            String email = jwtService.extractSubject(token);
            UserResponse user = userService.getCurrentUser(email);
            return new ResponseDto<>(HttpStatus.OK, "User retrieved successfully", user);
        }
        return null;
    }
}
