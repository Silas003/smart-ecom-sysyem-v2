package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.dao.interfaces.OrdersDao;
import com.amalitech.demo.dao.interfaces.OrderItemDao;
import com.amalitech.demo.models.Orders;
import com.amalitech.demo.models.OrderItem;
import com.amalitech.demo.models.User;
import com.amalitech.demo.dto.OrderStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

@AllArgsConstructor
@Repository
public class JdbcOrdersDao implements OrdersDao {
    private final DataSource dataSource;
    private final OrderItemDao orderItemDao;

    @Override
    public Optional<Orders> findById(Long id) {
        String sql = "SELECT id, user_id, total_amount, status, created_at FROM orders WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
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
    public long save(Orders orders) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            try {
                conn.setAutoCommit(false);
                long id = save(orders, conn);
                conn.commit();
                return id;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long save(Orders orders, Connection conn) throws SQLException {
        String sql = "INSERT INTO orders(user_id, total_amount, status) VALUES(?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, orders.getUser().getId());
            ps.setDouble(2, orders.getTotalAmount());
            ps.setString(3, orders.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long orderId = keys.getLong(1);
                    orders.setId(orderId);
                    // save items
                    if (orders.getItems() != null && !orders.getItems().isEmpty()) {
                        for (OrderItem item : orders.getItems()) {
                            item.setOrder(orders);
                            orderItemDao.save(item);
                        }
                    }
                    return orderId;
                }
            }
        }
        return -1;
    }

    @Override
    public void update(Orders orders) throws SQLException {
        String sql = "UPDATE orders SET user_id = ?, total_amount = ?, status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Orders mapOrderRow(ResultSet rs) throws SQLException {
        Orders o = new Orders();
        o.setId(rs.getLong("id"));
        User u = new User();
        u.setId(rs.getLong("user_id"));
        o.setUser(u);
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setStatus(OrderStatus.valueOf(rs.getString("status")));
        return o;
    }
}
