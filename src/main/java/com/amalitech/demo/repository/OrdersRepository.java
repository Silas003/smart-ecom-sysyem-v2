package com.amalitech.demo.repository;

import com.amalitech.demo.models.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserId(Long userId);

    // Native SQL example for reporting: fetch orders for a user within a date range, sorted by created_at desc
    @Query(value = "SELECT * FROM orders o WHERE o.user_id = :userId AND o.created_at BETWEEN :start AND :end ORDER BY o.created_at DESC",
            countQuery = "SELECT COUNT(*) FROM orders o WHERE o.user_id = :userId AND o.created_at BETWEEN :start AND :end",
            nativeQuery = true)
    Page<Orders> findByUserIdAndCreatedAtBetweenNative(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
