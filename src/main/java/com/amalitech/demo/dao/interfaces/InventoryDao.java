package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.Inventory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface InventoryDao {
    Optional<Inventory> findById(Long id);
    Optional<Inventory> findByProductId(Long productId);
    boolean existsByProductId(Long productId);
    List<Inventory> findAll();
    long save(Inventory inventory);
    void update(Inventory inventory);
    void update(Inventory inventory, Connection conn) throws SQLException;
    void deleteById(Long id);
}
