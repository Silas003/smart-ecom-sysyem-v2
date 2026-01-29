package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.dao.interfaces.InventoryDao;
import com.amalitech.demo.models.Inventory;
import com.amalitech.demo.models.Product;
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
public class JdbcInventoryDao implements InventoryDao {
    private final DataSource dataSource;

    @Override
    public Optional<Inventory> findById(Long id) {
        String sql = "SELECT inventory_id, product_id, quantity_in_stock, quantity_reserved, stock_status, version FROM inventory WHERE inventory_id = ?";
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
    public Optional<Inventory> findByProductId(Long productId) {
        String sql = "SELECT inventory_id, product_id, quantity_in_stock, quantity_reserved, stock_status, version FROM inventory WHERE product_id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByProductId(Long productId) {
        String sql = "SELECT 1 FROM inventory WHERE product_id = ? LIMIT 1";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Inventory> findAll() {
        String sql = "SELECT inventory_id, product_id, quantity_in_stock, quantity_reserved, stock_status, version FROM inventory";
        List<Inventory> list = new ArrayList<>();
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public long save(Inventory inventory) {
        String sql = "INSERT INTO inventory(product_id, quantity_in_stock, quantity_reserved, stock_status, version) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, inventory.getProduct().getId());
            ps.setInt(2, inventory.getStockQuantity());
            ps.setInt(3, inventory.getReservedQuantity());
            ps.setString(4, inventory.getStockStatus());
            ps.setLong(5, inventory.getVersion());
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
    public void update(Inventory inventory) {
        String sql = "UPDATE inventory SET quantity_in_stock = ?, quantity_reserved = ?, stock_status = ?, version = ? WHERE inventory_id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventory.getStockQuantity());
            ps.setInt(2, inventory.getReservedQuantity());
            ps.setString(3, inventory.getStockStatus());
            ps.setLong(4, inventory.getVersion());
            ps.setLong(5, inventory.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM inventory WHERE inventory_id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Inventory mapRow(ResultSet rs) throws SQLException {
        Inventory inv = new Inventory();
        inv.setId(rs.getLong("inventory_id"));
        Product p = new Product();
        p.setId(rs.getLong("product_id"));
        inv.setProduct(p);
        inv.setStockQuantity(rs.getInt("quantity_in_stock"));
        inv.setReservedQuantity(rs.getInt("quantity_reserved"));
        inv.setStockStatus(rs.getString("stock_status"));
        inv.setVersion(rs.getLong("version"));
        return inv;
    }
}
