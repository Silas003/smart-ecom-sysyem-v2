package com.amalitech.demo.repository;

import com.amalitech.demo.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    boolean findByProductId(Long productId);
}
