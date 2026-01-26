package com.amalitech.demo.repository;

import com.amalitech.demo.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Long> {
    Optional<List<Orders>> findByUser_Id(Long userId);

    // Fetch orders with items and product to avoid lazy-loading issues when mapping to DTOs
    @Query("select distinct o from Orders o left join fetch o.items i left join fetch i.product where o.id = :id")
    Optional<Orders> findByIdWithItemsAndProducts(@Param("id") Long id);

    @Query("select distinct o from Orders o left join fetch o.items i left join fetch i.product where o.user.id = :userId")
    Optional<List<Orders>> findByUser_IdWithItemsAndProducts(@Param("userId") Long userId);
}
