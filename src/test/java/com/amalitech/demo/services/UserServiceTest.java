package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserFound() {
        // Arrange
        User mockUser = new User();
        mockUser.setUsername("username");
        mockUser.setEmail("email@gmail.com");
        mockUser.setPassword("Testpassword");

        UserResponse expectedResponse = new UserResponse(
                1L,
                "username",
                "email@gmail.com",
                "USER"
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userMapper.toResponse(mockUser)).thenReturn(expectedResponse);

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("username", result.username());
        assertEquals("email@gmail.com", result.email());
        assertEquals("USER", result.userRole());

        // Verify interactions
        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toResponse(mockUser);
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.getUserById(999L);
        });

        verify(userRepository, times(1)).findById(999L);

    }

    @Test
    void shouldReturnListOfUsers(){
        User  user = new User();
        user.setUsername("username");
        user.setPassword("Testpassword");
        user.setUserRole(UserRole.customer);
        user.setEmail("email@gmail.com");

        when(userRepository.findAll()).thenReturn(List.of(user));
        UserResponse expectedResponse = new UserResponse(
                1L,
                "username",
                "email@gmail.com",
                "USER"
        );
        when(userMapper.toResponse(List.of(user))).thenReturn(List.of(expectedResponse));

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnVoidAfterDelete(){
        User  user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository, times(1)).findById(1L);
    }
}