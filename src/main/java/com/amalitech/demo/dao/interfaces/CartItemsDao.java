// ...existing code...
package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.CartItems;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemsDao {
    Optional<CartItems> findById(Long id);
    boolean existsByProductIdAndCartId(Long productId, Long cartId);
    Optional<CartItems> findByProductIdAndCartId(Long productId, Long cartId);
    long save(CartItems cartItems);
    void update(CartItems cartItems);
    void deleteById(Long id);
}
