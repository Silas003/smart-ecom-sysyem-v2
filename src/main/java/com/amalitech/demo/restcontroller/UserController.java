package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.services.UserService;
import com.amalitech.demo.services.interfaces.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
            @ApiResponse(responseCode = "200", description = "Users retrieved")
    })
    public ResponseDto<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseDto<>(HttpStatus.OK,"users retrieved",users);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by id", description = "Retrieve a single user by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved"),
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
            @ApiResponse(responseCode = "200", description = "User updated"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<UserResponse> updateUser(@Parameter(description = "ID of the user to update", required = true) @PathVariable Long id, @RequestBody @Valid UserRequest userRequest){
        UserResponse updatedUser = userService.updateUser(id, userRequest);
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
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public ResponseDto<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse newUser = userService.createUser(userRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"user retrieved",newUser);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Login user", description = "Authenticate user with credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseDto<UserResponse> loginUser(@RequestBody UserRequest user) {
        UserResponse userResponse = userService.loginUser(user);

        return new ResponseDto<>(HttpStatus.OK,"user login successful",userResponse);
    }
}
