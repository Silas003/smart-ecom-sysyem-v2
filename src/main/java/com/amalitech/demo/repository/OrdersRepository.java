package com.amalitech.demo.repository;

import com.amalitech.demo.models.Orders;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    @EntityGraph(value = "orders-with-items-and-user", type = EntityGraph.EntityGraphType.FETCH)
    List<Orders> findByUserId(Long userId);

    @Query("SELECT SUM(o.totalAmount) FROM Orders o WHERE o.status = 'delivered' AND o.createdAt BETWEEN :start AND :end")
    Double calculateTotalRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
