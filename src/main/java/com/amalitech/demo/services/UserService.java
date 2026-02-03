package com.amalitech.demo.services;

import com.amalitech.demo.dto.UserRole;
import com.amalitech.demo.dto.request.UserRequest;
import com.amalitech.demo.dto.response.UserResponse;
import com.amalitech.demo.exceptions.EntityNotFoundException;
import com.amalitech.demo.mapper.UserMapper;
import com.amalitech.demo.models.User;
import com.amalitech.demo.dao.interfaces.UserDao;
import com.amalitech.demo.services.interfaces.UserServiceInterface;
import com.amalitech.demo.utils.PasswordUtils;
import com.amalitech.demo.utils.Sorter;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Service
public class UserService implements UserServiceInterface {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final Sorter<User> sorter;

    @Override
    public void createUser(UserRequest userRequest) {
        // perform uniqueness checks using DAO
        if(userDao.existsByEmail(userRequest.getEmail()) || userDao.existsByUsername(userRequest.getUsername())){
            throw new IllegalArgumentException("User with given email or username already exists");
        }
        User user = userMapper.toEntity(userRequest);
        String password = PasswordUtils.hashPassword(user.getPassword());
        user.setPassword(password);
        userDao.save(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
       User user = userDao.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
       return userMapper.toResponse(user);
    }
    @Override
    public User getUserByIdForReview(Long id) {
        return userDao.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));

    }


    @Override
    public Page<UserResponse> getAllUsers(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        int offset = pageNumber * pageSize;
        List<User> content = userDao.findAll(pageSize, offset);
        if (content == null) content = List.of();

        // apply merge-sort by username (default)
        if (!content.isEmpty()) {
            Comparator<User> cmp = Comparator.comparing(User::getUsername, Comparator.nullsLast(String::compareToIgnoreCase));
            content = sorter.sort(content, cmp);
        }

        long total = content.size();
        return new PageImpl<>(userMapper.toResponse(content), pageable, total);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String password = PasswordUtils.hashPassword(userRequest.getPassword());

        existingUser.setUsername(userRequest.getUsername());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(password);
        existingUser.setUserRole(UserRole.valueOf(userRequest.getUserRole()));
        userDao.update(existingUser);
        return userMapper.toResponse(existingUser);
    }

    @Override
    public void deleteUser(Long id) {
        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        userDao.deleteById(existingUser.getId());
    }

    @Override
    public UserResponse loginUser(UserRequest userRequest) {
        String email = userRequest.getEmail();
        String password = userRequest.getPassword();
        User user = userDao.findByEmail(email).orElse(null);
        if(user != null){
          boolean authenticated =   PasswordUtils.verifyPassword(password, user.getPassword());
            if(!authenticated){
                throw new IllegalArgumentException("Invalid credentials");
            }
            else {
                return userMapper.toResponse(user);
            }
        }else{
            throw new IllegalArgumentException("User with given email does not exist");
        }
    }
}
