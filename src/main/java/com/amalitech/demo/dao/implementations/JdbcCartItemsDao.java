package com.amalitech.demo.dao.implementations;

import com.amalitech.demo.dao.interfaces.CartItemsDao;
import com.amalitech.demo.models.CartItems;
import com.amalitech.demo.models.Cart;
import com.amalitech.demo.models.Product;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class JdbcCartItemsDao implements CartItemsDao {
    private final DataSource dataSource;

    @Override
    public Optional<CartItems> findById(Long id) {
        String sql = "SELECT id, quantity, cart_id, product_id, unit_price, total_price FROM cart_items WHERE id = ?";
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
    public boolean existsByProductIdAndCartId(Long productId, Long cartId) {
        String sql = "SELECT 1 FROM cart_items WHERE product_id = ? AND cart_id = ? LIMIT 1";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setLong(2, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CartItems> findByProductIdAndCartId(Long productId, Long cartId) {
        String sql = "SELECT id, quantity, cart_id, product_id, unit_price, total_price FROM cart_items WHERE product_id = ? AND cart_id = ? LIMIT 1";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            ps.setLong(2, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public long save(CartItems cartItems) {
        String sql = "INSERT INTO cart_items(quantity, cart_id, product_id, unit_price, total_price) VALUES(?, ?, ?, ?, ?)";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cartItems.getQuantity());
            ps.setLong(2, cartItems.getCart().getId());
            ps.setLong(3, cartItems.getProduct().getId());
            ps.setDouble(4, cartItems.getUnitPrice());
            ps.setDouble(5, cartItems.getTotalPrice());
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
    public void update(CartItems cartItems) {
        String sql = "UPDATE cart_items SET quantity = ?, cart_id = ?, product_id = ?, unit_price = ?, total_price = ? WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartItems.getQuantity());
            ps.setLong(2, cartItems.getCart().getId());
            ps.setLong(3, cartItems.getProduct().getId());
            ps.setDouble(4, cartItems.getUnitPrice());
            ps.setDouble(5, cartItems.getTotalPrice());
            ps.setLong(6, cartItems.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        try (Connection conn = DataSourceUtils.getConnection(dataSource);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException  e) {
            throw new RuntimeException(e);
        }
    }

    private CartItems mapRow(ResultSet rs) throws SQLException {
        CartItems ci = new CartItems();
        ci.setId(rs.getLong("id"));
        ci.setQuantity(rs.getInt("quantity"));
        Cart cart = new Cart();
        cart.setId(rs.getLong("cart_id"));
        ci.setCart(cart);
        Product p = new Product();
        p.setId(rs.getLong("product_id"));
        ci.setProduct(p);
        ci.setUnitPrice(rs.getDouble("unit_price"));
        ci.setTotalPrice(rs.getDouble("total_price"));
        return ci;
    }
}
