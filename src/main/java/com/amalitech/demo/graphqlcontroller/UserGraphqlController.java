package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Tag(name = "GraphQL - Users", description = "GraphQL queries and mutations for users")
public class UserGraphqlController {

    private final UserService userService;

    public UserGraphqlController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    @Operation(summary = "List users (GraphQL)", description = "List all users via GraphQL query")
    public List<UserResponse> users() {
        return userService.getAllUsers();
    }

    @QueryMapping
    @Operation(summary = "Get user by id (GraphQL)", description = "Retrieve a single user by id via GraphQL")
    public UserResponse userById(@Argument Long id) {
        return userService.getUserById(id);
    }

    @MutationMapping
    @Operation(summary = "Create user (GraphQL)", description = "Create a new user via GraphQL mutation")
    public UserResponse createUser(@Argument("input")  UserRequest request) {
        return userService.createUser(request);
    }

    @MutationMapping
    @Operation(summary = "Update user (GraphQL)", description = "Update an existing user via GraphQL mutation")
    public UserResponse updateUser(@Argument  Long id, @Argument("input") UserRequest request) {
        return userService.updateUser(id, request);
    }

    @MutationMapping
    @Operation(summary = "Delete user (GraphQL)", description = "Delete a user by id via GraphQL mutation")
    public Boolean deleteUser(@Argument Long id) {
        userService.deleteUser(id);
        return true;
    }
}
