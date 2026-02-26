package com.amalitech.demo.repository;

import com.amalitech.demo.dto.CartStatus;
import com.amalitech.demo.models.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @EntityGraph(attributePaths = {"user"})
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);
    boolean existsByUserIdAndStatus(Long userId, CartStatus status);

    @Query("SELECT c FROM Cart c WHERE c.status = 'active' AND c.updatedAt < :date")
    Page<Cart> findAbandonedCarts(@Param("date") LocalDateTime date, Pageable pageable);
}

