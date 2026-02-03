// ...existing code...
package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.Orders;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.sql.Connection;
import java.sql.SQLException;

@Repository
public interface OrdersDao {
    Optional<Orders> findById(Long id);
    List<Orders> findByUserId(Long userId);
    List<Orders> findAll(int limit, int offset);
    long save(Orders orders) throws SQLException; // convenience method that manages its own connection
    long save(Orders orders, Connection conn) throws SQLException; // transactional overload
    void update(Orders orders) throws SQLException;
    void deleteById(Long id) throws SQLException;
}
