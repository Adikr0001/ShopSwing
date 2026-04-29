package com.shopswing.dao;

import com.shopswing.model.Product;
import com.shopswing.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product operations.
 * All queries join with categories to get category name.
 */
public class ProductDAO {

    private static final String SELECT_WITH_CATEGORY =
        "SELECT p.*, c.name AS category_name FROM products p " +
        "JOIN categories c ON p.category_id = c.id";

    /**
     * Returns all products.
     */
    public List<Product> getAllProducts() {
        return queryProducts(SELECT_WITH_CATEGORY + " ORDER BY p.id", null);
    }

    /**
     * Returns products filtered by category name.
     */
    public List<Product> getProductsByCategory(String categoryName) {
        String sql = SELECT_WITH_CATEGORY + " WHERE c.name = ? ORDER BY p.id";
        return queryProducts(sql, ps -> ps.setString(1, categoryName));
    }

    /**
     * Returns products matching a search query (name, brand, or description).
     */
    public List<Product> searchProducts(String query) {
        String sql = SELECT_WITH_CATEGORY +
            " WHERE p.name LIKE ? OR p.brand LIKE ? OR p.description LIKE ? OR c.name LIKE ?" +
            " ORDER BY p.id";
        String like = "%" + query + "%";
        return queryProducts(sql, ps -> {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
        });
    }

    /**
     * Returns products filtered by category AND search query.
     */
    public List<Product> getProductsByCategoryAndSearch(String categoryName, String query) {
        String sql = SELECT_WITH_CATEGORY +
            " WHERE c.name = ? AND (p.name LIKE ? OR p.brand LIKE ? OR p.description LIKE ?)" +
            " ORDER BY p.id";
        String like = "%" + query + "%";
        return queryProducts(sql, ps -> {
            ps.setString(1, categoryName);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
        });
    }

    /**
     * Returns products sorted by a given field.
     * @param sortBy one of: price_asc, price_desc, name_asc, name_desc, rating_desc
     */
    public List<Product> getAllProductsSorted(String sortBy) {
        String orderClause;
        switch (sortBy != null ? sortBy : "") {
            case "price_asc":   orderClause = "p.price ASC"; break;
            case "price_desc":  orderClause = "p.price DESC"; break;
            case "name_asc":    orderClause = "p.name ASC"; break;
            case "name_desc":   orderClause = "p.name DESC"; break;
            case "rating_desc": orderClause = "p.rating DESC"; break;
            default:            orderClause = "p.id ASC"; break;
        }
        return queryProducts(SELECT_WITH_CATEGORY + " ORDER BY " + orderClause, null);
    }

    /**
     * Gets a single product by ID.
     */
    public Product getProductById(int id) {
        String sql = SELECT_WITH_CATEGORY + " WHERE p.id = ?";
        List<Product> list = queryProducts(sql, ps -> ps.setInt(1, id));
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Returns the total count of products.
     */
    public int getProductCount() {
        String sql = "SELECT COUNT(*) FROM products";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return 0;
    }

    // ── Internal helper ──────────────────────────────────────

    @FunctionalInterface
    private interface ParamSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    private List<Product> queryProducts(String sql, ParamSetter setter) {
        List<Product> list = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            if (setter != null) setter.set(ps);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list;
    }

    /**
     * Adds a new product to the database.
     */
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products (name, category_id, price, description, brand, rating, stock, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getDescription());
            ps.setString(5, p.getBrand());
            ps.setDouble(6, p.getRating());
            ps.setInt(7, p.getStock());
            ps.setString(8, p.getImageUrl());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    /**
     * Updates an existing product.
     */
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET name=?, category_id=?, price=?, description=?, brand=?, rating=?, stock=?, image_url=? WHERE id=?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setDouble(3, p.getPrice());
            ps.setString(4, p.getDescription());
            ps.setString(5, p.getBrand());
            ps.setDouble(6, p.getRating());
            ps.setInt(7, p.getStock());
            ps.setString(8, p.getImageUrl());
            ps.setInt(9, p.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    /**
     * Deletes a product by ID.
     */
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return false;
    }

    private Product extractProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("category_name"));
        p.setPrice(rs.getDouble("price"));
        p.setDescription(rs.getString("description"));
        p.setBrand(rs.getString("brand"));
        p.setRating(rs.getDouble("rating"));
        p.setStock(rs.getInt("stock"));
        p.setImageUrl(rs.getString("image_url"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
