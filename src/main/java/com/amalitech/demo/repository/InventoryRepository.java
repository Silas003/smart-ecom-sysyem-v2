package com.amalitech.demo.repository;

import com.amalitech.demo.models.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    List<Inventory> findByProductIdIn(Collection<Long> productIds);

    List<Inventory> findAllByOrderByProduct_IdAsc();

    @Query("SELECT i FROM Inventory i JOIN FETCH i.product WHERE i.product.id = :productId ")
    Inventory findByProduct_Id(Long productId);
}
