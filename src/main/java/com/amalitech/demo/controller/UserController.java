package com.amalitech.demo.controller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.models.User;
import com.amalitech.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
@Tag(name = "Users", description = "APIs to manage users")
public class UserController {
    private UserService userService;
    public UserController(
            UserService userService
    ){
        this.userService = userService;
    }
    @GetMapping("/")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public ResponseEntity<ResponseDto> getAllUsers(){
        List<User> users = userService.getAllUsers();
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"users retrieved",users);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "Retrieve a single user by its id")
    public ResponseEntity<ResponseDto> getUserById(@PathVariable Long id){
            User user = userService.getUserById(id);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"user retrieved",user);

        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's data")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable Long id, @RequestBody @Valid User user){
        User updatedUser = userService.updateUser(id, user);
        ResponseDto responseDto = new ResponseDto(HttpStatus.ACCEPTED,"user updated succesfully",updatedUser);

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by id")
    public ResponseEntity<ResponseDto> deleteUser(@PathVariable Long id) {
        ResponseDto responseDto = new ResponseDto(HttpStatus.NO_CONTENT,"user deletion success",null);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseDto);
    }

    @PostMapping("/create_user")
    @Operation(summary = "Create user", description = "Create a new user")
    public ResponseEntity<ResponseDto> createUser(@RequestBody @Valid User user) {
        User newUser = userService.createUser(user);
        ResponseDto responseDto = new ResponseDto(HttpStatus.CREATED,"users retrieved",newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
