package com.amalitech.demo.repository;

import com.amalitech.demo.models.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    Optional<CartItems> findByProductIdAndCartId(Long productId, Long cartId);
    List<CartItems> findByCartId(Long cartId);
}

