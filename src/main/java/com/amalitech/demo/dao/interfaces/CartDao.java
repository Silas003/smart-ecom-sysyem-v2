// ...existing code...
package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.models.Cart;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface CartDao {
    Optional<Cart> findById(Long id);
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    boolean existsByUserIdAndStatus(Long userId, CartStatus status);
    long save(Cart cart);
    void update(Cart cart);
    void deleteById(Long id);
}
