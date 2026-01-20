package com.amalitech.demo.restcontroller;

import com.amalitech.demo.dto.ResponseDto;
import com.amalitech.demo.dto.UserRequest;
import com.amalitech.demo.dto.UserResponse;
import com.amalitech.demo.models.User;
import com.amalitech.demo.services.UserService;
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
    public ResponseDto<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return new ResponseDto<>(HttpStatus.OK,"users retrieved",users);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<UserResponse> getUserById(@PathVariable Long id){
            UserResponse user = userService.getUserById(id);
            return new ResponseDto<>(HttpStatus.OK,"user retrieved",user);

    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseDto<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest){
        UserResponse updatedUser = userService.updateUser(id, userRequest);
        return new ResponseDto<>(HttpStatus.ACCEPTED,"user updated ",updatedUser);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/create_user")
    public ResponseDto<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse newUser = userService.createUser(userRequest);
        return new ResponseDto<>(HttpStatus.CREATED,"user retrieved",newUser);
    }
}
