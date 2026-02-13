package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.CartItems;

import java.util.List;
import java.util.Optional;


public interface CartItemsDao {
    Optional<CartItems> findById(Long id);
    Optional<CartItems> findByProductIdAndCartId(Long productId, Long cartId);
    List<CartItems> findByCartId(Long cartId);
    long save(CartItems cartItems);
    void update(CartItems cartItems);
    void deleteById(Long id);
    void deleteAllByCartId(Long cartId);
}
