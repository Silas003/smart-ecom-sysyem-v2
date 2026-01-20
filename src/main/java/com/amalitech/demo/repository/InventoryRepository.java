package com.amalitech.demo.repository;

import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory,Long> {

    Product findByProductId(Long productId);
    boolean existsByProductId(Long productId);
}
