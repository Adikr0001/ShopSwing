package com.shopswing.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection utility for MySQL.
 * Provides a static method to get a connection to the shopswing_db database.
 * 
 * CONFIGURATION:
 *   - Update DB_URL, DB_USER, DB_PASSWORD below to match your MySQL setup.
 *   - Default: localhost:3306, user=root, password=root
 */
public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/shopswing_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    // Load MySQL JDBC driver once at class load time
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    /**
     * Returns a new connection to the MySQL database.
     * Caller is responsible for closing the connection.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    /**
     * Quietly closes a connection (null-safe).
     * 
     * @param conn the connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests the database connection and prints the result.
     * Can be run standalone for verification.
     */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("SUCCESS: Connected to shopswing_db database!");
                System.out.println("Database: " + conn.getCatalog());
                System.out.println("URL: " + DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("FAILED: Could not connect to database.");
            System.err.println("Error: " + e.getMessage());
            System.err.println("\nMake sure:");
            System.err.println("  1. MySQL is running on localhost:3306");
            System.err.println("  2. Database 'shopswing_db' exists");
            System.err.println("  3. User/password are correct (default: root/root)");
        }
    }
}
