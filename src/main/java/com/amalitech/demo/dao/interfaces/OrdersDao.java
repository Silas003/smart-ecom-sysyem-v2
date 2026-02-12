package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.Orders;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface OrdersDao {
    Optional<Orders> findById(Long id);
    List<Orders> findByUserId(Long userId);
    List<Orders> findAll(int limit, int offset);
    long save(Orders order) throws SQLException; // convenience method that manages its own connection
    long save(Orders orders, Connection conn) throws SQLException; // transactional overload
    void update(Orders orders) throws SQLException;
    void deleteById(Long id) throws SQLException;
}
