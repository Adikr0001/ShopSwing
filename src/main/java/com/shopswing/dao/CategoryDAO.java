package com.shopswing.dao;

import com.shopswing.model.Category;
import com.shopswing.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Category operations.
 */
public class CategoryDAO {
    private static final List<Category> FALLBACK_CATEGORIES = createFallbackCategories();

    /**
     * Returns all categories ordered by name.
     */
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Category(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        return list.isEmpty() ? new ArrayList<>(FALLBACK_CATEGORIES) : list;
    }

    /**
     * Gets a category by ID.
     */
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Category(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.closeConnection(conn);
        }
        for (Category category : FALLBACK_CATEGORIES) {
            if (category.getId() == id) return category;
        }
        return null;
    }

    private static List<Category> createFallbackCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Electronics", "Phones, laptops, gadgets"));
        categories.add(new Category(2, "Clothing", "Fashion and apparel"));
        categories.add(new Category(3, "Books", "Books and learning"));
        categories.add(new Category(4, "Home & Garden", "Home essentials"));
        categories.add(new Category(5, "Sports", "Fitness and outdoor"));
        categories.add(new Category(6, "Beauty", "Beauty and personal care"));
        return categories;
    }
}
