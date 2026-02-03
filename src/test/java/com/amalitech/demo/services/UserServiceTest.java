package com.amalitech.demo.services;

import com.amalitech.demo.dao.implementations.JdbcUserDao;
import com.amalitech.demo.dao.interfaces.UserDao;
import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.utils.Sorter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Sorter sorter;

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

        when(userDao.findById(1L)).thenReturn(Optional.of(mockUser));
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
        verify(userDao, times(1)).findById(1L);
        verify(userMapper, times(1)).toResponse(mockUser);
    }

    @Test
    void shouldReturnNullWhenUserNotFound() {

        when(userDao.findById(999L)).thenReturn(Optional.empty());

      assertThrows(EntityNotFoundException.class, () -> userService.getUserById(999L));

        verify(userDao, times(1)).findById(999L);

    }

    @Test
    void shouldReturnListOfUsers(){
        User  user = new User();
        user.setUsername("username");
        user.setPassword("Testpassword");
        user.setUserRole(UserRole.customer);
        user.setEmail("email@gmail.com");
        int pageSize = 10;
        int pageNumber = 1;
        when(userDao.findAll(pageSize,pageSize*pageNumber)).thenReturn(List.of(user));
        UserResponse expectedResponse = new UserResponse(
                1L,
                "username",
                "email@gmail.com",
                "USER"
        );
        when(sorter.sort(anyList(), any())).thenReturn(List.of(user));
        when(userMapper.toResponse(List.of(user))).thenReturn(List.of(expectedResponse));

        Page<UserResponse> result = userService.getAllUsers(1,10);

        assertNotNull(result);
        verify(userDao, times(1)).findAll(pageSize,pageSize*pageNumber);
    }

    @Test
    void shouldReturnVoidAfterDelete(){
        User  user = new User();
        when(userDao.findById(1L)).thenReturn(Optional.of(user));
        userService.deleteUser(1L);
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void shouldReturnVoidAfterCreate(){
        UserRequest userRequest =  new UserRequest(
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

        when(userDao.existsByEmail(userRequest.getEmail())).thenReturn(false);
        when(userDao.existsByUsername(userRequest.getUsername())).thenReturn(false);

        when(userMapper.toEntity(userRequest)).thenReturn(user);
        when(userDao.save(user)).thenReturn(1L);

        userService.createUser(userRequest);
        verify(userDao,times(1)).existsByEmail(userRequest.getEmail());
        verify(userDao,times(1)).existsByUsername(userRequest.getUsername());
        verify(userMapper,times(1)).toEntity(userRequest);
        verify(userDao,times(1)).save(user);
    }
}