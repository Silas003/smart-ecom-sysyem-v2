package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.OrderItem;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface OrderItemDao {
    Optional<OrderItem> findById(Long id);
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByOrderId(Long orderId, String sortBy, String direction);
    long save(OrderItem item);
    void update(OrderItem item);
    void deleteById(Long id);
    // Connection-aware variants for transactional callers (e.g. OrdersDao.save)
    long save(OrderItem item, Connection conn);
}
