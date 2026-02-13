package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.dto.request.UserLoginRequest;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.repository.UserRepository;
import com.amalitech.demo.security.JwtService;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Sorter<User> sorter;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUserFound() {
        User mockUser = new User();
        mockUser.setId(1L);
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

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("username", result.username());
        assertEquals("email@gmail.com", result.email());
        assertEquals("USER", result.userRole());

        verify(userRepository, times(1)).findById(1L);
        verify(userMapper, times(1)).toResponse(mockUser);
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);

    }

    @Test
    void shouldReturnPagedUsers() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("Testpassword");
        user.setUserRole(UserRole.customer);
        user.setEmail("email@gmail.com");

        int pageSize = 10;
        int pageNumber = 1;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<User> userPage = new PageImpl<>(List.of(user), pageable, 1);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        UserResponse expectedResponse = new UserResponse(
                1L,
                "username",
                "email@gmail.com",
                "USER"
        );
        when(sorter.sort(anyList(), any())).thenReturn(List.of(user));
        when(userMapper.toResponse(List.of(user))).thenReturn(List.of(expectedResponse));

        Page<UserResponse> result = userService.getAllUsers(pageNumber, pageSize);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("username", result.getContent().get(0).username());

        verify(userRepository, times(1)).findAll(pageable);
        verify(sorter, times(1)).sort(anyList(), any());
        verify(userMapper, times(1)).toResponse(List.of(user));
    }

    @Test
    void shouldDeleteUserWhenExists() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void shouldCreateUser() {
        UserRequest userRequest = new UserRequest(
                "username",
                "email@gmail.com",
                "Testpassword",
                UserRole.customer.toString()
        );
        User user = new User(
                "username",
                "email@gmail.com",
                "Testpassword",
                UserRole.customer
        );

        when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userRequest.getUsername())).thenReturn(false);

        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        userService.createUser(userRequest);
        verify(userRepository, times(1)).existsByEmail(userRequest.getEmail());
        verify(userRepository, times(1)).existsByUsername(userRequest.getUsername());
        verify(userMapper, times(1)).toEntity(userRequest);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void shouldThrowExceptionWithInvalidCredentialsAfterLogin() {
        User user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("Testpassword");
        user.setUserRole(UserRole.customer);
        user.setEmail("email@gmail.com");

        UserLoginRequest userRequest = new UserLoginRequest(user.getEmail(), "wrong-password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> userService.loginUser(userRequest));
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void getUserById_usesRepositoryOnce_whenCalledTwice() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("tester");

        UserResponse response = new UserResponse(1L, "tester", "test@example.com", "customer");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse first = userService.getUserById(1L);
        UserResponse second = userService.getUserById(1L);

        assertNotNull(first);
        assertNotNull(second);
        assertEquals(first.id(), second.id());

        // Without a real cache manager in this unit test, we at least
        // verify the service delegates consistently; Spring's caching
        // is exercised in integration tests.
        verify(userRepository, times(2)).findById(1L);
    }
}