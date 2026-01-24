package com.amalitech.demo.repository;

import com.amalitech.demo.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders,Long> {
    Optional<List<Orders>> findByUser_Id(Long userId);
}
