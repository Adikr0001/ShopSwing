package com.shopswing.dao;

import com.shopswing.model.Product;
import com.shopswing.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Data Access Object for Product operations.
 * All queries join with categories to get category name.
 */
public class ProductDAO {

    private static final String SELECT_WITH_CATEGORY =
        "SELECT p.*, c.name AS category_name FROM products p " +
        "JOIN categories c ON p.category_id = c.id";
    private static final List<Product> FALLBACK_PRODUCTS = Collections.unmodifiableList(Arrays.asList(
        fallbackProduct(1001, "Apple iPhone 15 Pro", 1, "Electronics", 99999, "Apple", 4.9, "Titanium design with pro-grade cameras and A17 chip.", "https://images.pexels.com/photos/607812/pexels-photo-607812.jpeg"),
        fallbackProduct(1002, "Samsung Galaxy S24 Ultra", 1, "Electronics", 89999, "Samsung", 4.8, "Flagship Android phone with AI features and S-Pen.", "https://images.pexels.com/photos/1092644/pexels-photo-1092644.jpeg"),
        fallbackProduct(1003, "Sony WH-1000XM5", 1, "Electronics", 29990, "Sony", 4.7, "Premium wireless noise cancelling headphones.", "https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg"),
        fallbackProduct(1004, "Dell XPS 15", 1, "Electronics", 149999, "Dell", 4.6, "Premium laptop with OLED display and creator performance.", "https://images.pexels.com/photos/18105/pexels-photo.jpg"),
        fallbackProduct(1005, "Canon Mirrorless Camera", 1, "Electronics", 67990, "Canon", 4.6, "Compact camera with 4K video and fast autofocus.", "https://images.pexels.com/photos/90946/pexels-photo-90946.jpeg"),
        fallbackProduct(1006, "PlayStation 5 Slim", 1, "Electronics", 44990, "Sony", 4.9, "Next-gen gaming console with 1TB SSD storage.", "https://images.pexels.com/photos/1298601/pexels-photo-1298601.jpeg"),
        fallbackProduct(1007, "Razer Blade 16", 1, "Electronics", 359999, "Razer", 4.8, "High-end gaming laptop with OLED 240Hz display.", "https://images.pexels.com/photos/7915576/pexels-photo-7915576.jpeg"),
        fallbackProduct(1008, "Apple Watch Series", 1, "Electronics", 42999, "Apple", 4.7, "Fitness, health, and communication from your wrist.", "https://images.pexels.com/photos/437037/pexels-photo-437037.jpeg"),
        fallbackProduct(1009, "Nike Air Max 270", 2, "Clothing", 9995, "Nike", 4.5, "Lightweight everyday sneakers with max cushioning.", "https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg"),
        fallbackProduct(1010, "Classic Denim Jacket", 2, "Clothing", 3499, "Levi's", 4.4, "Everyday rugged denim jacket for all seasons.", "https://images.pexels.com/photos/1040945/pexels-photo-1040945.jpeg"),
        fallbackProduct(1011, "Cotton Hoodie", 2, "Clothing", 2499, "H&M", 4.3, "Soft fleece hoodie with relaxed fit.", "https://images.pexels.com/photos/6311392/pexels-photo-6311392.jpeg"),
        fallbackProduct(1012, "Atomic Habits", 3, "Books", 499, "James Clear", 4.9, "Bestselling self-improvement book on habit building.", "https://images.pexels.com/photos/159711/books-bookstore-book-reading-159711.jpeg"),
        fallbackProduct(1013, "Clean Code", 3, "Books", 1299, "Robert C Martin", 4.8, "Practical guide for writing maintainable code.", "https://images.pexels.com/photos/590493/pexels-photo-590493.jpeg"),
        fallbackProduct(1014, "Home Coffee Maker", 4, "Home & Garden", 8999, "Philips", 4.5, "Fresh brewed coffee at home in minutes.", "https://images.pexels.com/photos/324028/pexels-photo-324028.jpeg"),
        fallbackProduct(1015, "Robot Vacuum Cleaner", 4, "Home & Garden", 22999, "Xiaomi", 4.4, "Automated floor cleaning with smart mapping.", "https://images.pexels.com/photos/4108717/pexels-photo-4108717.jpeg"),
        fallbackProduct(1016, "Adjustable Dumbbell Set", 5, "Sports", 4599, "PowerMax", 4.4, "Space-saving dumbbells for home workouts.", "https://images.pexels.com/photos/416778/pexels-photo-416778.jpeg"),
        fallbackProduct(1017, "Premium Yoga Mat", 5, "Sports", 1999, "Boldfit", 4.3, "Anti-slip yoga mat with extra cushioning.", "https://images.pexels.com/photos/3822622/pexels-photo-3822622.jpeg"),
        fallbackProduct(1018, "Protein Powder 2kg", 5, "Sports", 3499, "ON", 4.6, "Whey protein for recovery and muscle building.", "https://images.pexels.com/photos/4397840/pexels-photo-4397840.jpeg"),
        fallbackProduct(1019, "Hydrating Face Cleanser", 6, "Beauty", 699, "Cetaphil", 4.5, "Gentle daily cleanser for sensitive skin.", "https://images.pexels.com/photos/3736397/pexels-photo-3736397.jpeg"),
        fallbackProduct(1020, "Vitamin C Serum", 6, "Beauty", 899, "Minimalist", 4.6, "Brightening serum for even skin tone.", "https://images.pexels.com/photos/6621332/pexels-photo-6621332.jpeg")
    ));

    /**
     * Returns all products.
     */
    public List<Product> getAllProducts() {
        List<Product> products = queryProducts(SELECT_WITH_CATEGORY + " ORDER BY p.id", null);
        return products.isEmpty() ? new ArrayList<>(FALLBACK_PRODUCTS) : products;
    }

    /**
     * Returns products filtered by category name.
     */
    public List<Product> getProductsByCategory(String categoryName) {
        String sql = SELECT_WITH_CATEGORY + " WHERE c.name = ? ORDER BY p.id";
        List<Product> products = queryProducts(sql, ps -> ps.setString(1, categoryName));
        if (!products.isEmpty()) return products;
        List<Product> filtered = new ArrayList<>();
        for (Product p : FALLBACK_PRODUCTS) {
            if (p.getCategoryName().equalsIgnoreCase(categoryName)) filtered.add(p);
        }
        return filtered;
    }

    /**
     * Returns products matching a search query (name, brand, or description).
     */
    public List<Product> searchProducts(String query) {
        String sql = SELECT_WITH_CATEGORY +
            " WHERE p.name LIKE ? OR p.brand LIKE ? OR p.description LIKE ? OR c.name LIKE ?" +
            " ORDER BY p.id";
        String like = "%" + query + "%";
        List<Product> products = queryProducts(sql, ps -> {
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
        });
        if (!products.isEmpty()) return products;
        String queryLower = query.toLowerCase();
        List<Product> filtered = new ArrayList<>();
        for (Product p : FALLBACK_PRODUCTS) {
            if (p.getName().toLowerCase().contains(queryLower)
                || p.getBrand().toLowerCase().contains(queryLower)
                || p.getDescription().toLowerCase().contains(queryLower)
                || p.getCategoryName().toLowerCase().contains(queryLower)) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    /**
     * Returns products filtered by category AND search query.
     */
    public List<Product> getProductsByCategoryAndSearch(String categoryName, String query) {
        String sql = SELECT_WITH_CATEGORY +
            " WHERE c.name = ? AND (p.name LIKE ? OR p.brand LIKE ? OR p.description LIKE ?)" +
            " ORDER BY p.id";
        String like = "%" + query + "%";
        List<Product> products = queryProducts(sql, ps -> {
            ps.setString(1, categoryName);
            ps.setString(2, like);
            ps.setString(3, like);
            ps.setString(4, like);
        });
        if (!products.isEmpty()) return products;
        String queryLower = query.toLowerCase();
        List<Product> filtered = new ArrayList<>();
        for (Product p : FALLBACK_PRODUCTS) {
            if (p.getCategoryName().equalsIgnoreCase(categoryName)
                && (p.getName().toLowerCase().contains(queryLower)
                || p.getBrand().toLowerCase().contains(queryLower)
                || p.getDescription().toLowerCase().contains(queryLower))) {
                filtered.add(p);
            }
        }
        return filtered;
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
        List<Product> products = queryProducts(SELECT_WITH_CATEGORY + " ORDER BY " + orderClause, null);
        if (!products.isEmpty()) return products;

        List<Product> fallback = new ArrayList<>(FALLBACK_PRODUCTS);
        switch (sortBy != null ? sortBy : "") {
            case "price_asc":
                fallback.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
                break;
            case "price_desc":
                fallback.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
                break;
            case "name_asc":
                fallback.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
                break;
            case "name_desc":
                fallback.sort((a, b) -> b.getName().compareToIgnoreCase(a.getName()));
                break;
            case "rating_desc":
                fallback.sort((a, b) -> Double.compare(b.getRating(), a.getRating()));
                break;
            default:
                break;
        }
        return fallback;
    }

    /**
     * Gets a single product by ID.
     */
    public Product getProductById(int id) {
        String sql = SELECT_WITH_CATEGORY + " WHERE p.id = ?";
        List<Product> list = queryProducts(sql, ps -> ps.setInt(1, id));
        if (!list.isEmpty()) return list.get(0);
        for (Product p : FALLBACK_PRODUCTS) {
            if (p.getId() == id) return p;
        }
        return null;
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
        return FALLBACK_PRODUCTS.size();
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
        String sql = "INSERT INTO products (name, category_id, price, description, brand, rating, stock, image_url, specifications) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            ps.setString(9, p.getSpecifications());
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
        String sql = "UPDATE products SET name=?, category_id=?, price=?, description=?, brand=?, rating=?, stock=?, image_url=?, specifications=? WHERE id=?";
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
            ps.setString(9, p.getSpecifications());
            ps.setInt(10, p.getId());
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
        p.setSpecifications(rs.getString("specifications"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }

    private static Product fallbackProduct(int id, String name, int categoryId, String categoryName, double price,
                                           String brand, double rating, String description, String imageUrl) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setCategoryId(categoryId);
        p.setCategoryName(categoryName);
        p.setPrice(price);
        p.setBrand(brand);
        p.setRating(rating);
        p.setDescription(description);
        p.setImageUrl(imageUrl);
        p.setStock(25);
        p.setSpecifications("");
        return p;
    }
}
