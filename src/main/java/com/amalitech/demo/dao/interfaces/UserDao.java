package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<User> findById(Long id);
    List<User> findAll(int pageNumber, int pageSize);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    long save(User user); // returns generated id
    void update(User user);
    void deleteById(Long id);
}
