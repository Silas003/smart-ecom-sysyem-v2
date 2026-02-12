package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.dao.interfaces.ReviewsDao;
import com.amalitech.demo.dao.interfaces.UserDao;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Reviews;
import com.amalitech.demo.models.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class JdbcReviewsDao implements ReviewsDao {
    private final DataSource dataSource;
    private UserDao userDao;

    @Override
    public Optional<Reviews> findById(Long id) {
        String sql = "SELECT id, stars, description, user_id, product_id,created_at FROM reviews WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Reviews> findAll() {
        String sql = "SELECT id, stars, description, user_id, product_id,created_at FROM reviews ORDER BY id DESC";
        List<Reviews> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Reviews> findByProductIdOrderByIdDesc(Long productId) {
        String sql = "SELECT id, stars, description, user_id, product_id,created_at FROM reviews WHERE product_id = ? ORDER BY id DESC";
        List<Reviews> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public List<Reviews> findByUserIdOrderByIdDesc(Long userId) {
        String sql = "SELECT id, stars, description, user_id, product_id,created_at FROM reviews WHERE user_id = ? ORDER BY id DESC";
        List<Reviews> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public long save(Reviews reviews) {
        String sql = "INSERT INTO reviews(stars, description, user_id, product_id) VALUES(?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            long generatedId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, reviews.getRating());
                ps.setString(2, reviews.getDescription());
                ps.setLong(3, reviews.getUser().getId());
                ps.setLong(4, reviews.getProduct().getId());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) generatedId = keys.getLong(2);
                }
            }

            conn.commit();
            return generatedId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Failed to rollback review save", rollbackEx);
                }
            }
            throw new RuntimeException("Failed to save review", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Failed to rollback review deletion", rollbackEx);
                }
            }
            throw new RuntimeException("Failed to delete review", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private Reviews mapRow(ResultSet rs) throws SQLException {
        Reviews r = new Reviews();
        r.setId(rs.getLong("id"));
        r.setRating(rs.getInt("stars"));
        r.setDescription(rs.getString("description"));
        User u = userDao.findById(rs.getLong("user_id")).orElseThrow(() -> new RuntimeException("User not found for review"));
        r.setUser(u);
        Product p = new Product();
        p.setId(rs.getLong("product_id"));
        r.setProduct(p);
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return r;
    }
}
