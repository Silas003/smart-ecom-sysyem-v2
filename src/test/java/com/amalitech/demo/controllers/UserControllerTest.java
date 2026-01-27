package com.amalitech.demo.controllers;


import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.restcontroller.UserController;
import com.amalitech.demo.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserService userService;


    @Test
    void shouldReturnUserById() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "customer");

        when(userService.getUserById(anyLong())).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("Alice"));
    }

    @Test
    void shouldReturnUserList() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "customer");

        List<UserResponse> userResponses = new ArrayList<>(Arrays.asList(userResponse, userResponse, userResponse));
        when(userService.getAllUsers()).thenReturn(userResponses);

        mockMvc.perform(get("/api/v1/users/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("users retrieved"))
                .andExpect(jsonPath("$.data[0].username").value("Alice"));
    }

    @Test
    void shouldReturnUserNotFoundError() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "customer");
        when(userService.getUserById(anyLong())).thenThrow(new EntityNotFoundException("user not found"));

        mockMvc.perform(get("/api/v1/users/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user not found"));
    }

    @Test
    void shouldReturnUserAfterUpdate() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "admin");

        UserRequest userRequest = new UserRequest("Alice", "a@gmail.com", "Testpassword", "admin");
        when(userService.updateUser(eq(1L), any(UserRequest.class)))
                .thenReturn(userResponse);

        ObjectMapper objectMapper = new ObjectMapper();

//        mockMvc.perform(put("/api/v1/users/1")
//                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
//                        .content(objectMapper.writeValueAsString(userRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("user updated"));

    }

    @Test
    void shouldThrowResourceNotFound() throws Exception {
        mockMvc.perform(get("/api/user/"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource Not Found"));

    }

    @Test
    void shouldReturnNoContentAfterDelete() throws Exception {

        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/v1/users/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateUser() throws Exception {
        UserResponse userResponse =
                new UserResponse(1L, "Alice", "", "customer");

//        String
    }
}