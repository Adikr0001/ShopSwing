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

    private static final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.home") + "/shopswing.db";
    private static final String DB_USER = "";
    private static final String DB_PASSWORD = "";

    // Load SQLite JDBC driver once at class load time
    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("SQLite JDBC Driver not found", e);
        }
    }

    /**
     * Returns a new connection to the SQLite database.
     * Caller is responsible for closing the connection.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
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
                System.out.println("SUCCESS: Connected to shopswing.db database!");
                System.out.println("URL: " + DB_URL);
            }
        } catch (SQLException e) {
            System.err.println("FAILED: Could not connect to database.");
            System.err.println("Error: " + e.getMessage());
        }
    }
}
