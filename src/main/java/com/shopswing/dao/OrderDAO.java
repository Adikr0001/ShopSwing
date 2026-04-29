package com.shopswing.dao;

import com.shopswing.model.CartItem;
import com.shopswing.model.Order;
import com.shopswing.model.OrderItem;
import com.shopswing.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Order operations.
 */
public class OrderDAO {

    /**
     * Places a new order from the user's cart items.
     * Inserts into orders + order_items, then clears the cart.
     *
     * @return the new order ID, or -1 on failure
     */
    public int placeOrder(int userId, List<CartItem> cartItems,
                          String shippingAddress, String paymentMethod) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  // transaction

            // Calculate total
            double total = 0;
            for (CartItem item : cartItems) {
                total += item.getProductPrice() * item.getQuantity();
            }

            // Insert order
            String orderSql = "INSERT INTO orders (user_id, total_amount, shipping_address, status, payment_method) VALUES (?, ?, ?, 'Placed', ?)";
            PreparedStatement ps1 = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            ps1.setInt(1, userId);
            ps1.setDouble(2, total);
            ps1.setString(3, shippingAddress);
            ps1.setString(4, paymentMethod != null ? paymentMethod : "Cash on Delivery");
            ps1.executeUpdate();

            ResultSet keys = ps1.getGeneratedKeys();
            if (!keys.next()) {
                conn.rollback();
                return -1;
            }
            int orderId = keys.getInt(1);

            // Insert order items
            String itemSql = "INSERT INTO order_items (order_id, product_id, product_name, price, quantity) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps2 = conn.prepareStatement(itemSql);
            for (CartItem item : cartItems) {
                ps2.setInt(1, orderId);
                ps2.setInt(2, item.getProductId());
                ps2.setString(3, item.getProductName());
                ps2.setDouble(4, item.getProductPrice());
                ps2.setInt(5, item.getQuantity());
                ps2.addBatch();
            }
            ps2.executeBatch();

            // Clear cart
            String clearSql = "DELETE FROM cart WHERE user_id = ?";
            PreparedStatement ps3 = conn.prepareStatement(clearSql);
            ps3.setInt(1, userId);
            ps3.executeUpdate();

            conn.commit();
            return orderId;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return -1;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
            }
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Returns all orders for a user, newest first.
     */
    public List<Order> getOrdersByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, (SELECT COUNT(*) FROM order_items WHERE order_id = o.id) AS item_count " +
                     "FROM orders o WHERE o.user_id = ? ORDER BY o.created_at DESC";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = extractOrder(rs);
                o.setItemCount(rs.getInt("item_count"));
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list;
    }

    /**
     * Returns a single order by ID (with items).
     */
    public Order getOrderById(int orderId) {
        String sql = "SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order o = extractOrder(rs);
                o.setUsername(rs.getString("username"));
                o.setItems(getOrderItems(orderId));
                return o;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    /**
     * Returns items for a specific order.
     */
    public List<OrderItem> getOrderItems(int orderId) {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getInt("id"));
                item.setOrderId(rs.getInt("order_id"));
                item.setProductId(rs.getInt("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setPrice(rs.getDouble("price"));
                item.setQuantity(rs.getInt("quantity"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list;
    }

    private Order extractOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setShippingAddress(rs.getString("shipping_address"));
        o.setStatus(rs.getString("status"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        return o;
    }
}
