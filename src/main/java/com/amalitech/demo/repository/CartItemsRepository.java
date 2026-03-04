package com.amalitech.demo.repository;

import com.amalitech.demo.models.CartItems;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.ScopedValue;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItems, Long> {
    @EntityGraph(attributePaths = {"product", "product.category"})
    Optional<CartItems> findByProductIdAndCartId(Long productId, Long cartId);
    
    @EntityGraph(attributePaths = {"product", "product.category"})
    List<CartItems> findByCartId(Long cartId);

    @Query(value = "SELECT * FROM cart_items WHERE cart_id = :cartId AND product_id = :productId", nativeQuery = true)
    Optional<CartItems> findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    @Query("SELECT ci FROM CartItems ci " +
            "JOIN FETCH ci.cart c " +
            "JOIN FETCH c.user u " +
            "WHERE ci.id = :cartItemId")
    Optional<CartItems> findByIdWithCartAndUser(@Param("cartItemId") Long cartItemId);
}

