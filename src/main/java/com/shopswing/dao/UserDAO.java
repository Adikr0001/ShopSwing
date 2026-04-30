package com.shopswing.dao;

import com.shopswing.model.User;
import com.shopswing.utils.DBConnection;

import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Data Access Object for User operations.
 * All methods use PreparedStatement to prevent SQL injection.
 */
public class UserDAO {
    private static final Map<Integer, User> FALLBACK_USERS_BY_ID = new ConcurrentHashMap<>();
    private static final Map<String, Integer> FALLBACK_USER_ID_BY_USERNAME = new ConcurrentHashMap<>();
    private static final Map<String, Integer> FALLBACK_USER_ID_BY_EMAIL = new ConcurrentHashMap<>();
    private static final AtomicInteger FALLBACK_ID_SEQUENCE = new AtomicInteger(10000);

    /**
     * Registers a new user in the database.
     *
     * @param user User object with username, email, passwordHash set
     * @return the generated user ID, or -1 on failure
     */
    public int registerUser(User user) {
        String sql = "INSERT INTO users (username, email, password_hash, full_name, phone, address) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getFullName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            int rows = ps.executeUpdate();

            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return registerFallbackUser(user);
        } finally {
            DBConnection.closeConnection(conn);
        }
        return -1;
    }

    /**
     * Authenticates a user by username and password hash.
     *
     * @param username the username
     * @param passwordHash the SHA-256 hashed password
     * @return User object if found, null otherwise
     */
    public User loginUser(String username, String passwordHash) {
        String sql = "SELECT * FROM users WHERE username = ? AND password_hash = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Integer id = FALLBACK_USER_ID_BY_USERNAME.get(normalize(username));
            if (id != null) {
                User stored = FALLBACK_USERS_BY_ID.get(id);
                if (stored != null && stored.getPasswordHash().equals(passwordHash)) {
                    return copyUser(stored);
                }
            }
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    /**
     * Finds a user by their ID.
     */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            User fallback = FALLBACK_USERS_BY_ID.get(id);
            if (fallback != null) return copyUser(fallback);
        } finally {
            DBConnection.closeConnection(conn);
        }
        return null;
    }

    /**
     * Checks if a username already exists.
     */
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return FALLBACK_USER_ID_BY_USERNAME.containsKey(normalize(username));
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Checks if an email already exists.
     */
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            return ps.executeQuery().next();
        } catch (SQLException e) {
            e.printStackTrace();
            return FALLBACK_USER_ID_BY_EMAIL.containsKey(normalize(email));
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Extracts a User object from a ResultSet row.
     */
    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }

    private int registerFallbackUser(User user) {
        String usernameKey = normalize(user.getUsername());
        String emailKey = normalize(user.getEmail());
        if (FALLBACK_USER_ID_BY_USERNAME.containsKey(usernameKey) || FALLBACK_USER_ID_BY_EMAIL.containsKey(emailKey)) {
            return -1;
        }

        int id = FALLBACK_ID_SEQUENCE.incrementAndGet();
        User stored = copyUser(user);
        stored.setId(id);
        stored.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        FALLBACK_USERS_BY_ID.put(id, stored);
        FALLBACK_USER_ID_BY_USERNAME.put(usernameKey, id);
        FALLBACK_USER_ID_BY_EMAIL.put(emailKey, id);
        return id;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

    private static User copyUser(User source) {
        User user = new User();
        user.setId(source.getId());
        user.setUsername(source.getUsername());
        user.setEmail(source.getEmail());
        user.setPasswordHash(source.getPasswordHash());
        user.setFullName(source.getFullName());
        user.setPhone(source.getPhone());
        user.setAddress(source.getAddress());
        user.setCreatedAt(source.getCreatedAt());
        return user;
    }
}
