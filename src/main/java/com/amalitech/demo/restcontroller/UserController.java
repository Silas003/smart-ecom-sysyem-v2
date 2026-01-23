package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.UserRequest;
import com.amalitech.demo.dto.UserResponse;
import com.amalitech.demo.models.User;
import com.amalitech.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@AllArgsConstructor
public class UserController {
    private UserService userService;


    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public ResponseDto<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseDto<>(HttpStatus.OK,"users retrieved",users);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get user by id", description = "Retrieve a single user by its id")
    public ResponseDto<UserResponse> getUserById(@PathVariable Long id){
            UserResponse user = userService.getUserById(id);
            return new ResponseDto<>(HttpStatus.OK,"user retrieved",user);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update user", description = "Update an existing user's data")
    public ResponseDto<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest){
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return new ResponseDto<>(HttpStatus.OK,"user updated",updatedUser);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/create_user")
    @Operation(summary = "Create user", description = "Create a new user")
    public ResponseDto<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse newUser = userService.createUser(userRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"user retrieved",newUser);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<UserResponse> loginUser(@RequestBody UserRequest user) {
        UserResponse userResponse = userService.loginUser(user);

        return new ResponseDto<>(HttpStatus.OK,"user login successful",userResponse);
    }
}
