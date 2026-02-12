package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.models.Cart;

import java.util.Optional;

public interface CartDao {
    Optional<Cart> findById(Long id);
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    boolean existsByUserIdAndStatus(Long userId, CartStatus status);
    long save(Cart cart);
    void update(Cart cart);
    void deleteById(Long id);
}
