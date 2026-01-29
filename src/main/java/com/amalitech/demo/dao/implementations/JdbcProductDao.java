package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.dao.interfaces.ProductDao;
import com.amalitech.demo.models.Product;
import com.amalitech.demo.models.Category;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class JdbcProductDao implements ProductDao {
    private final DataSource dataSource;

    @Override
    public Optional<Product> findById(Long id) {
        String sql = "SELECT id, name, price, stock_quantity, category_id FROM products WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
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
    public Optional<Product> findByName(String name) {
        String sql = "SELECT id, name, price, stock_quantity, category_id FROM products WHERE name = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByName(String name) {
        String sql = "SELECT 1 FROM products WHERE name = ? LIMIT 1";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId, int limit, int offset) {
        String sql = "SELECT id, name, price, stock_quantity, category_id FROM products WHERE category_id = ? ORDER BY id LIMIT ? OFFSET ?";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public long countByCategoryId(Long categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public List<Product> findAll(int limit, int offset) {
        String sql = "SELECT id, name, price, stock_quantity, category_id FROM products ORDER BY id LIMIT ? OFFSET ?";
        List<Product> list = new ArrayList<>();
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public long countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    @Override
    public long save(Product product) {
        String sql = "INSERT INTO products(name, price, stock_quantity, category_id) VALUES(?, ?, ?, ?)";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStockQuantity());
            ps.setLong(4, product.getCategory().getId());
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
    public void update(Product product) {
        String sql = "UPDATE products SET name = ?, price = ?, stock_quantity = ?, category_id = ? WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setDouble(2, product.getPrice());
            ps.setInt(3, product.getStockQuantity());
            ps.setLong(4, product.getCategory().getId());
            ps.setLong(5, product.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getLong("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setStockQuantity(rs.getInt("stock_quantity"));
        long categoryId = rs.getLong("category_id");
        Category c = new Category();
        c.setId(categoryId);
        p.setCategory(c);
        return p;
    }
}
