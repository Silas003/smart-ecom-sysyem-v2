package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserGraphqlController {

    private final UserService userService;

    public UserGraphqlController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<UserResponse> users() {
        return userService.getAllUsers();
    }

    @QueryMapping
    public UserResponse userById(@Argument Long id) {
        return userService.getUserById(id);
    }

    @MutationMapping
    public UserResponse createUser(@Argument("input")  UserRequest request) {
        return userService.createUser(request);
    }

    @MutationMapping
    public UserResponse updateUser(@Argument  Long id, @Argument("input") UserRequest request) {
        return userService.updateUser(id, request);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        userService.deleteUser(id);
        return true;
    }
}
