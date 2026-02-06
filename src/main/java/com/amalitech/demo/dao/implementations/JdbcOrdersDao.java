package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.dao.interfaces.OrdersDao;
import com.amalitech.demo.dao.interfaces.OrderItemDao;
import com.amalitech.demo.dao.interfaces.InventoryDao;
import com.amalitech.demo.dao.interfaces.ProductDao;
import com.amalitech.demo.models.Orders;
import com.amalitech.demo.models.OrderItem;
import com.amalitech.demo.models.User;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.dto.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@AllArgsConstructor
@Repository
public class JdbcOrdersDao implements OrdersDao {
    private final DataSource dataSource;
    private final OrderItemDao orderItemDao;
    private final InventoryDao inventoryDao;
    private final ProductDao productDao;

    @Override
    public Optional<Orders> findById(Long id) {
        String sql = "SELECT id, user_id, total_amount, status, created_at FROM orders WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Orders o = mapOrderRow(rs);
                    List<OrderItem> items = orderItemDao.findByOrderId(o.getId());
                    o.setItems(items);
                    return Optional.of(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Orders> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, total_amount, status, created_at FROM orders WHERE user_id = ?";
        List<Orders> list = new ArrayList<>();
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders o = mapOrderRow(rs);
                    List<OrderItem> items = orderItemDao.findByOrderId(o.getId());
                    o.setItems(items);
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Orders> findAll(int limit, int offset) {
        String sql = "SELECT id, user_id, total_amount, status, created_at FROM orders ORDER BY id LIMIT ? OFFSET ?";
        List<Orders> list = new ArrayList<>();
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders o = mapOrderRow(rs);
                    List<OrderItem> items = orderItemDao.findByOrderId(o.getId());
                    o.setItems(items);
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public long save(Orders order) throws SQLException {
        // Use DataSourceUtils to get connection from Spring's transaction if one exists
        // DataSourceUtils will participate in the existing transaction managed by @Transactional
        Connection conn = DataSourceUtils.getConnection(dataSource);
        return save(order, conn);
    }

    @Override
    public long save(Orders orders, Connection conn) throws SQLException {
        String orderSql = "INSERT INTO orders(user_id, total_amount, status, created_at) VALUES(?, ?, ?, ?)";
        long orderId;

        try (PreparedStatement ps = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orders.getUser().getId());
            ps.setDouble(2, orders.getTotalAmount());
            ps.setString(3, orders.getStatus().name());

            Timestamp now = Timestamp.valueOf(
                orders.getCreatedAt() != null ? orders.getCreatedAt() : java.time.LocalDateTime.now()
            );
            ps.setTimestamp(4, now);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    orderId = keys.getLong(2);
                    orders.setId(orderId);
                    orders.setCreatedAt(now.toLocalDateTime());
                } else {
                    throw new SQLException("Failed to retrieve order ID after insert");
                }
            }
        }

        // 2. If there are order items, save them and update inventory/product
        if (orders.getItems() != null && !orders.getItems().isEmpty()) {
            // Set the order reference for each item and save individually to get IDs
            for (OrderItem item : orders.getItems()) {
                item.setOrder(orders);
                // Save each item to get the generated ID
                long itemId = orderItemDao.save(item, conn);
                item.setId(itemId);
            }

            // 3. Update inventory and product stock for each item using DAO methods
            for (OrderItem item : orders.getItems()) {
                // Fetch current inventory
                Inventory inventory = inventoryDao.findByProductId(item.getProduct().getId())
                    .orElseThrow(() -> new SQLException("Inventory not found for product ID: " + item.getProduct().getId()));

                // Reduce stock quantity
                inventory.setStockQuantity(inventory.getStockQuantity() - item.getQuantity());

                // Update inventory using DAO method
                inventoryDao.update(inventory, conn);

                // Fetch current product
                Product product = productDao.findById(item.getProduct().getId())
                    .orElseThrow(() -> new SQLException("Product not found with ID: " + item.getProduct().getId()));

                // Reduce product stock quantity
                product.setStockQuantity(product.getStockQuantity() - item.getQuantity());

                // Update product using DAO method
                productDao.update(product, conn);
            }
        }

        return orderId;
    }

    @Override
    public void update(Orders orders) throws SQLException {
        String sql = "UPDATE orders SET user_id = ?, total_amount = ?, status = ? WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orders.getUser().getId());
            ps.setDouble(2, orders.getTotalAmount());
            ps.setString(3, orders.getStatus().name());
            ps.setLong(4, orders.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Orders mapOrderRow(ResultSet rs) throws SQLException {
        Orders order = new Orders();
        order.setId(rs.getLong("id"));
        User u = new User();
        u.setId(rs.getLong("user_id"));
        order.setUser(u);
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setCreatedAt(rs.getTimestamp("created_at"). toLocalDateTime());
        return order;
    }
}
