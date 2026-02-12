package com.amalitech.demo.graphqlcontroller;

import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Tag(name = "GraphQL - Users", description = "GraphQL queries and mutations for users")
public class UserGraphqlController {

        private final UserService userService;

        public UserGraphqlController(UserService userService) {
                this.userService = userService;
        }

        // Admin-only user listing
        @PreAuthorize("hasRole('admin')")
        @QueryMapping
    @Operation(summary = "List users (GraphQL)", description = "List all users via GraphQL query")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = UserResponse.class))))
    })
        public List<UserResponse> users(@Argument int page, @Argument int size) {
                var p = userService.getAllUsers(page,size);
                var items = p.getContent();
                return items;
        }

        // Admin-only user lookup
        @PreAuthorize("hasRole('admin')")
        @QueryMapping
    @Operation(summary = "Get user by id (GraphQL)", description = "Retrieve a single user by id via GraphQL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public UserResponse userById(@Argument Long id) {
        return userService.getUserById(id);
    }

        // Public registration
        @MutationMapping
    @Operation(summary = "Create user (GraphQL)", description = "Create a new user via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    public void createUser(@Argument("input")  UserRequest request) {
        userService.createUser(request);
    }

        // Admin-only updates
        @PreAuthorize("hasRole('admin')")
        @MutationMapping
    @Operation(summary = "Update user (GraphQL)", description = "Update an existing user via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
        public UserResponse updateUser(@Argument  Long id, @Argument("input") UserRequest request) {
        return userService.updateUser(id, request);
    }

        // Admin-only deletion
        @PreAuthorize("hasRole('admin')")
        @MutationMapping
    @Operation(summary = "Delete user (GraphQL)", description = "Delete a user by id via GraphQL mutation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public Boolean deleteUser(@Argument Long id) {
        userService.deleteUser(id);
        return true;
    }
}
