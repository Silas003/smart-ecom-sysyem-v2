// ...existing code...
package com.amalitech.demo.dao.interfaces;

import com.amalitech.demo.models.OrderItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemDao {
    Optional<OrderItem> findById(Long id);
    List<OrderItem> findByOrderId(Long orderId);
    long save(OrderItem item);
    void saveAll(List<OrderItem> items);
    void update(OrderItem item);
    void deleteById(Long id);
}
