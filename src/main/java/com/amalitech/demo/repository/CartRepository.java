package com.amalitech.demo.repository;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    boolean existsByUserIdAndStatus(Long userId, CartStatus status);
}

