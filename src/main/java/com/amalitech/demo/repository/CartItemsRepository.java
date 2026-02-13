package com.amalitech.demo.repository;

import com.amalitech.demo.models.CartItems;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<CartItems> findByProductIdAndCartId(Long productId, Long cartId);
    
    @EntityGraph(attributePaths = {"product", "product.category"})
    List<CartItems> findByCartId(Long cartId);
}

