package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.dao.interfaces.CartDao;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.models.User;
import com.amalitech.demo.dto.CartStatus;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class JdbcCartDao implements CartDao {
    private final DataSource dataSource;

    @Override
    public Optional<Cart> findById(Long id) {
        String sql = "SELECT id, user_id, status FROM carts WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException  e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status) {
        String sql = "SELECT id, user_id, status FROM carts WHERE user_id = ? AND status = ? LIMIT 1";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, status == null ? null : status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByUserIdAndStatus(Long userId, CartStatus status) {
        String sql = "SELECT 1 FROM carts WHERE user_id = ? AND status = ? LIMIT 1";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setString(2, status == null ? null : status.name());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException  e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long save(Cart cart) {
        String sql = "INSERT INTO carts(user_id, status) VALUES(?, ?)";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, cart.getUser().getId());
            ps.setString(2, cart.getStatus() == null ? null : cart.getStatus().name());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    @Override
    public void update(Cart cart) {
        String sql = "UPDATE carts SET user_id = ?, status = ? WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, cart.getUser().getId());
            ps.setString(2, cart.getStatus() == null ? null : cart.getStatus().name());
            ps.setLong(3, cart.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM carts WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Cart mapRow(ResultSet rs) throws SQLException {
        Cart c = new Cart();
        c.setId(rs.getLong("id"));
        User u = new User();
        u.setId(rs.getLong("user_id"));
        c.setUser(u);
        String status = rs.getString("status");
        if (status != null) c.setStatus(CartStatus.valueOf(status));
        return c;
    }
}
