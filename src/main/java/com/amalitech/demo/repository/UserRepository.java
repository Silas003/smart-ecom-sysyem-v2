package com.amalitech.demo.repository;

import com.amalitech.demo.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.username = ?1, u.email = ?2, u.password = ?3, u.userRole = ?4 where u.id = ?5")
    int update(String username, String email, String password, String userRole, Long id);
}
