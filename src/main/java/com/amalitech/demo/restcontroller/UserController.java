package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.request.UpdateUserRequest;
import com.amalitech.demo.dto.request.UserLoginRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.services.interfaces.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(value = "/api/v1/users")
@AllArgsConstructor
@Tag(name = "Users", description = "User account management endpoints")
public class UserController {
    private UserServiceInterface userService;


    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    })
    public ResponseDto<Page<UserResponse>> getAllUsers(@RequestParam int page,@RequestParam int size){
        Page<UserResponse> users = userService.getAllUsers(page,size);
        return new ResponseDto<>(HttpStatus.OK,"users retrieved",users);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by id", description = "Retrieve a single user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseDto<UserResponse> getUserById(@Parameter(description = "ID of the user to retrieve", required = true) @PathVariable Long id){
            UserResponse user = userService.getUserById(id);
            return new ResponseDto<>(HttpStatus.OK,"user retrieved",user);

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
    public ResponseDto<UserResponse> updateUser(@Parameter(description = "ID of the user to update", required = true) @PathVariable Long id, @RequestBody @Valid UpdateUserRequest userRequest){
        // map UpdateUserRequest to UserRequest for service
        UserRequest ur = new UserRequest(
                userRequest.getUsername() == null ? "" : userRequest.getUsername(),
                userRequest.getEmail() == null ? "" : userRequest.getEmail(),
                userRequest.getPassword() == null ? "" : userRequest.getPassword(),
                userRequest.getUserRole() == null ? null : userRequest.getUserRole()
        );
        UserResponse updatedUser = userService.updateUser(id, ur);
        return new ResponseDto<>(HttpStatus.OK,"user updated",updatedUser);

    }

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

    @PostMapping("/create_user")
    @Operation(summary = "Create user", description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseDto<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        userService.createUser(userRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"user created",null);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login user", description = "Authenticate user with credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseDto<UserResponse> loginUser(@RequestBody UserLoginRequest request) {
        UserResponse userResponse = userService.loginUser(request);

        return new ResponseDto<>(HttpStatus.OK,"user login successful",userResponse);
    }
}
