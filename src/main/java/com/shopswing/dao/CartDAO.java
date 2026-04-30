package com.shopswing.dao;

import com.shopswing.model.CartItem;
import com.shopswing.model.Product;
import com.shopswing.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Access Object for Cart operations.
 */
public class CartDAO {
    private static final Map<Integer, Map<Integer, Integer>> FALLBACK_CARTS = new ConcurrentHashMap<>();
    private final ProductDAO productDAO = new ProductDAO();

    /**
     * Adds a product to the user's cart. If already in cart, increments quantity.
     */
    public void addToCart(int userId, int productId) {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, 1) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + 1";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Map<Integer, Integer> userCart = FALLBACK_CARTS.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
            userCart.merge(productId, 1, Integer::sum);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Updates the quantity of a cart item.
     * If quantity <= 0, removes the item.
     */
    public void updateQuantity(int userId, int productId, int quantity) {
        if (quantity <= 0) {
            removeFromCart(userId, productId);
            return;
        }
        String sql = "UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Map<Integer, Integer> userCart = FALLBACK_CARTS.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
            userCart.put(productId, quantity);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Removes a product from the user's cart.
     */
    public void removeFromCart(int userId, int productId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            Map<Integer, Integer> userCart = FALLBACK_CARTS.get(userId);
            if (userCart != null) {
                userCart.remove(productId);
            }
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Returns all cart items for a user, with product details joined.
     */
    public List<CartItem> getCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT c.*, p.name AS product_name, p.price AS product_price, " +
                     "p.brand AS product_brand, p.image_url AS product_image_url, " +
                     "cat.name AS category_name " +
                     "FROM cart c " +
                     "JOIN products p ON c.product_id = p.id " +
                     "JOIN categories cat ON p.category_id = cat.id " +
                     "WHERE c.user_id = ? ORDER BY c.added_at";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setId(rs.getInt("id"));
                item.setUserId(rs.getInt("user_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setAddedAt(rs.getTimestamp("added_at"));
                item.setProductName(rs.getString("product_name"));
                item.setProductPrice(rs.getDouble("product_price"));
                item.setProductBrand(rs.getString("product_brand"));
                item.setProductImageUrl(rs.getString("product_image_url"));
                item.setCategoryName(rs.getString("category_name"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        if (!list.isEmpty()) return list;
        return getFallbackCartItems(userId);
    }

    /**
     * Returns the total number of items in the user's cart.
     */
    public int getCartCount(int userId) {
        String sql = "SELECT COUNT(*) FROM cart WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        Map<Integer, Integer> userCart = FALLBACK_CARTS.getOrDefault(userId, Collections.emptyMap());
        int total = 0;
        for (Integer qty : userCart.values()) {
            total += qty;
        }
        return total;
    }

    /**
     * Clears the entire cart for a user (used after placing an order).
     */
    public void clearCart(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            FALLBACK_CARTS.remove(userId);
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Gets the current quantity of a specific product in the user's cart.
     * Returns 0 if not in cart.
     */
    public int getItemQuantity(int userId, int productId) {
        String sql = "SELECT quantity FROM cart WHERE user_id = ? AND product_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("quantity");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        Map<Integer, Integer> userCart = FALLBACK_CARTS.getOrDefault(userId, Collections.emptyMap());
        return userCart.getOrDefault(productId, 0);
    }

    private List<CartItem> getFallbackCartItems(int userId) {
        List<CartItem> list = new ArrayList<>();
        Map<Integer, Integer> userCart = FALLBACK_CARTS.getOrDefault(userId, Collections.emptyMap());
        for (Map.Entry<Integer, Integer> entry : userCart.entrySet()) {
            Product p = productDAO.getProductById(entry.getKey());
            if (p == null) continue;
            CartItem item = new CartItem();
            item.setId(0);
            item.setUserId(userId);
            item.setProductId(p.getId());
            item.setQuantity(entry.getValue());
            item.setAddedAt(new Timestamp(System.currentTimeMillis()));
            item.setProductName(p.getName());
            item.setProductPrice(p.getPrice());
            item.setProductBrand(p.getBrand());
            item.setProductImageUrl(p.getImageUrl());
            item.setCategoryName(p.getCategoryName());
            list.add(item);
        }
        return list;
    }
}
