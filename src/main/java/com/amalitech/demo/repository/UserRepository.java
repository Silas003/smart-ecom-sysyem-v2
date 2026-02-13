package com.amalitech.demo.repository;

import com.amalitech.demo.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findAll(Pageable pageable);

    @Query("SELECT u FROM User u WHERE NOT EXISTS (SELECT o FROM Orders o WHERE o.user = u AND o.createdAt > :date)")
    Page<User> findInactiveUsers(@Param("date") LocalDateTime date, Pageable pageable);
}

