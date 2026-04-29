package com.shopswing.dao;

import com.shopswing.model.Admin;
import com.shopswing.utils.DBConnection;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AdminDAO {

    public Admin loginAdmin(String username, String passwordHash) {
        String sql = "SELECT * FROM admins WHERE username = ? AND password_hash = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setEmail(rs.getString("email"));
                admin.setRole(rs.getString("role"));
                admin.setCreatedAt(rs.getTimestamp("created_at"));
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            // Total Products
            PreparedStatement ps1 = conn.prepareStatement("SELECT COUNT(*) FROM products");
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) stats.put("totalProducts", rs1.getInt(1));

            // Total Users
            PreparedStatement ps2 = conn.prepareStatement("SELECT COUNT(*) FROM users");
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) stats.put("totalUsers", rs2.getInt(1));

            // Total Orders
            PreparedStatement ps3 = conn.prepareStatement("SELECT COUNT(*) FROM orders");
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) stats.put("totalOrders", rs3.getInt(1));

            // Total Revenue (as int for simplicity in stats)
            PreparedStatement ps4 = conn.prepareStatement("SELECT SUM(total_amount) FROM orders WHERE status != 'Cancelled'");
            ResultSet rs4 = ps4.executeQuery();
            if (rs4.next()) stats.put("totalRevenue", rs4.getInt(1));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return stats;
    }
    
    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}
