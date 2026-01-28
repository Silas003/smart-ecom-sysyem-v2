// ...existing code...
package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.config.DatabaseConfig;
import com.amalitech.demo.dao.interfaces.OrderItemDao;
import com.amalitech.demo.models.OrderItem;
import com.amalitech.demo.models.Orders;
import com.amalitech.demo.models.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class JdbcOrderItemDao implements OrderItemDao {
    private final DatabaseConfig databaseConfig;

    @Override
    public Optional<OrderItem> findById(Long id) {
        String sql = "SELECT id, order_id, product_id, quantity, unit_price, total_price FROM order_items WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        String sql = "SELECT id, order_id, product_id, quantity, unit_price, total_price FROM order_items WHERE order_id = ?";
        List<OrderItem> list = new ArrayList<>();
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public long save(OrderItem item) {
        String sql = "INSERT INTO order_items(order_id, product_id, quantity, unit_price, total_price) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, item.getOrder().getId());
            ps.setLong(2, item.getProduct().getId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());
            ps.setDouble(5, item.getTotalPrice());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    @Override
    public void saveAll(List<OrderItem> items) {
        String sql = "INSERT INTO order_items(order_id, product_id, quantity, unit_price, total_price) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderItem i : items) {
                ps.setLong(1, i.getOrder().getId());
                ps.setLong(2, i.getProduct().getId());
                ps.setInt(3, i.getQuantity());
                ps.setDouble(4, i.getUnitPrice());
                ps.setDouble(5, i.getTotalPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(OrderItem item) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ?, unit_price = ?, total_price = ? WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, item.getOrder().getId());
            ps.setLong(2, item.getProduct().getId());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getUnitPrice());
            ps.setDouble(5, item.getTotalPrice());
            ps.setLong(6, item.getId());
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private OrderItem mapRow(ResultSet rs) throws SQLException {
        OrderItem oi = new OrderItem();
        oi.setId(rs.getLong("id"));
        Orders o = new Orders();
        o.setId(rs.getLong("order_id"));
        oi.setOrder(o);
        Product p = new Product();
        p.setId(rs.getLong("product_id"));
        oi.setProduct(p);
        oi.setQuantity(rs.getInt("quantity"));
        oi.setUnitPrice(rs.getDouble("unit_price"));
        oi.setTotalPrice(rs.getDouble("total_price"));
        return oi;
    }
}
