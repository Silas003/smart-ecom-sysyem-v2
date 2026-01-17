package com.amalitech.demo.controller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.UserRequest;
import com.amalitech.demo.models.User;
import com.amalitech.demo.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/users")
public class UserController {
    private UserService userService;
    public UserController(
            UserService userService
    ){
        this.userService = userService;
    }
    @GetMapping("/")
    public ResponseEntity<ResponseDto> getAllUsers(){
        List<User> users = userService.getAllUsers();
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"users retrieved",users);
        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getUserById(@PathVariable Long id){
            User user = userService.getUserById(id);
        ResponseDto responseDto = new ResponseDto(HttpStatus.OK,"user retrieved",user);

        return  ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest){
        User updatedUser = userService.updateUser(id, userRequest);
        ResponseDto responseDto = new ResponseDto(HttpStatus.ACCEPTED,"user updated ",updatedUser);

        return  ResponseEntity.status(HttpStatus.ACCEPTED).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/create_user")
    public ResponseEntity<ResponseDto> createUser(@RequestBody @Valid UserRequest userRequest) {
        User newUser = userService.createUser(userRequest);
        ResponseDto responseDto = new ResponseDto(HttpStatus.CREATED,"user retrieved",newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}
