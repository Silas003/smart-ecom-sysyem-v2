package com.amalitech.demo.repository;

import com.amalitech.demo.models.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemsRepository extends JpaRepository<CartItems,Long> {

    boolean existsByProduct_IdAndCart_Id(Long productId, Long cartId);
    Optional<CartItems> findByProduct_IdAndCart_Id(Long productId, Long cartId);
}
