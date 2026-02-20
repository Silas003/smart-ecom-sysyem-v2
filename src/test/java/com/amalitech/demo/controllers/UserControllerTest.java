package com.amalitech.demo.controllers;


import com.amalitech.demo.dto.Provider;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.restcontroller.UserController;
import com.amalitech.demo.security.JwtAuthenticationFilter;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(roles= {"admin", "customer"})
    void shouldReturnUserById() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "customer", Provider.local.name());

        when(userService.getUserById(anyLong())).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("Alice"));
    }

    @Test
    @WithMockUser(roles= {"admin"})
    void shouldReturnUserList() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "customer",Provider.local.name());
        int pageNumber = 1;
        int pageSize = 10;
        List<UserResponse> userResponses = new ArrayList<>(Arrays.asList(userResponse, userResponse, userResponse));
        // return a proper Page using PageImpl
        Page<UserResponse> page = new org.springframework.data.domain.PageImpl<>(
                userResponses,
                PageRequest.of(Math.max(0, pageNumber - 1), pageSize),
                userResponses.size()
        );
        when(userService.getAllUsers(pageNumber,pageSize)).thenReturn(page);

        mockMvc.perform(get("/api/v1/users")
                        .param("page","1")
                        .param("size","10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("users retrieved"))
                .andExpect(jsonPath("$.data.content[0].username").value("Alice"));
    }

    @Test
    void shouldReturnUserNotFoundError() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "customer",Provider.local.name());
        when(userService.getUserById(anyLong())).thenThrow(new EntityNotFoundException("user not found"));

        mockMvc.perform(get("/api/v1/users/5"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user not found"));
    }

    @Test
    @WithMockUser(roles= {"admin", "customer"})
    void shouldReturnUserAfterUpdate() throws Exception {

        UserResponse userResponse =
                new UserResponse(1L, "Alice", "a@gmail.com", "admin",Provider.local.name());

        // prepare UpdateUserRequest JSON payload
        String payload = "{\"username\":\"AliceUpdated\",\"email\":\"alice.updated@gmail.com\",\"password\":\"P@ssw0rd1\",\"userRole\":\"admin\"}";

        when(userService.updateUser(eq(1L), any())).thenReturn(userResponse);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user updated"))
                .andExpect(jsonPath("$.data.id").value(1));

    }

    @Test
    void shouldThrowResourceNotFound() throws Exception {
        mockMvc.perform(get("/api/user/"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Resource Not Found"));

    }

    @Test
    @WithMockUser(roles= {"admin"})
    void shouldReturnNoContentAfterDelete() throws Exception {

        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/v1/users/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldCreateUser() throws Exception {
        // prepare valid user payload (meets validation)
        String payload = "{\"username\":\"alice081\",\"email\":\"alice@example.com\",\"password\":\"P@ssw0rd1\",\"userRole\":\"customer\"}";

        // userService.createUser is void; stub to do nothing
        org.mockito.Mockito.doNothing().when(userService).createUser(any());

        mockMvc.perform(post("/api/v1/users")
                        .contentType("application/json")
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("user created"));

    }
}
