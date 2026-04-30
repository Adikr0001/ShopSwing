package com.shopswing.utils;

import com.shopswing.model.Product;
import com.shopswing.dao.ProductDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PopulateDB {

    public static void main(String[] args) {
        System.out.println("Starting Database Population...");

        try {
            ensureCategories();
            populateBaseProducts();
            populatePremiumProducts();
            System.out.println("Database Population COMPLETED successfully!");
        } catch (Exception e) {
            System.err.println("Database Population FAILED!");
            e.printStackTrace();
        }
    }

    private static void ensureCategories() throws SQLException {
        String[] categories = {"Electronics", "Clothing", "Books", "Home & Garden", "Sports", "Beauty"};
        String[] descriptions = {
            "Smartphones, laptops, headphones, cameras & gadgets",
            "Men & women fashion, shoes, accessories",
            "Bestsellers, programming, self-help & more",
            "Kitchen appliances, home decor, cleaning",
            "Fitness equipment, supplements, yoga accessories",
            "Skincare, makeup, personal care products"
        };

        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT id FROM categories WHERE name = ?";
            String insertSql = "INSERT INTO categories (name, description) VALUES (?, ?)";
            
            for (int i = 0; i < categories.length; i++) {
                PreparedStatement psCheck = conn.prepareStatement(checkSql);
                psCheck.setString(1, categories[i]);
                ResultSet rs = psCheck.executeQuery();
                if (!rs.next()) {
                    PreparedStatement psInsert = conn.prepareStatement(insertSql);
                    psInsert.setString(1, categories[i]);
                    psInsert.setString(2, descriptions[i]);
                    psInsert.executeUpdate();
                    System.out.println("Added category: " + categories[i]);
                }
            }
        }
    }

    private static void populateBaseProducts() throws SQLException {
        ProductDAO dao = new ProductDAO();
        // Base products from schema (truncated for brevity in script, but I'll add the key ones)
        Object[][] baseData = {
            {101, "Samsung Galaxy S24 Ultra", 1, 89999.00, "6.8-inch QHD+ AMOLED, 200MP camera, Snapdragon 8 Gen 3", "Samsung", 4.8, 50, "images/samsung_s24.jpg"},
            {102, "Apple iPhone 15 Pro", 1, 99999.00, "6.1-inch Super Retina XDR, A17 Pro chip, ProRAW camera", "Apple", 4.9, 30, "images/iphone15.jpg"},
            {103, "Sony WH-1000XM5 Headphones", 1, 29990.00, "Industry-leading noise cancellation, 30hr battery", "Sony", 4.7, 80, "images/sony_wh1000.jpg"},
            {104, "Dell XPS 15 Laptop", 1, 149999.00, "15.6-inch OLED 3.5K, Intel i9, 32GB RAM, RTX 4070", "Dell", 4.6, 20, "images/dell_xps15.jpg"},
            {105, "LG 55 inch OLED 4K TV", 1, 89990.00, "55-inch OLED evo, a9 AI Processor, Dolby Vision IQ", "LG", 4.7, 15, "images/lg_oled55.jpg"},
            {106, "BoAt Airdopes 141 TWS", 1, 1299.00, "Up to 42 hours total playback, Beast Mode gaming", "boAt", 4.3, 200, "images/boat_airdopes.jpg"},
            {107, "Canon EOS R50 Camera", 1, 67990.00, "24.2MP APS-C sensor, 4K video, dual pixel AF", "Canon", 4.6, 25, "images/canon_r50.jpg"},
            {108, "Apple iPad Air M2", 1, 59900.00, "11-inch Liquid Retina, M2 chip, USB-C connector", "Apple", 4.8, 40, "images/ipad_air.jpg"},
            {111, "Levis 501 Original Jeans", 2, 3999.00, "Classic straight fit, 100% cotton denim", "Levi's", 4.4, 100, "images/levis_501.jpg"},
            {112, "Nike Air Max 270", 2, 9995.00, "Max Air unit, breathable mesh upper, all-day comfort", "Nike", 4.5, 75, "images/nike_airmax.jpg"},
            {115, "Atomic Habits", 3, 499.00, "Transform your life with tiny changes - James Clear", "James Clear", 4.9, 500, "images/atomic_habits.jpg"},
            {116, "Clean Code", 3, 1299.00, "A handbook of agile software craftsmanship, R.C.Martin", "Robert C Martin", 4.8, 300, "images/clean_code.jpg"},
            {119, "Dyson V15 Detect Vacuum", 4, 52900.00, "Laser dust detection, 60 min run time, HEPA filter", "Dyson", 4.7, 18, "images/dyson_v15.jpg"},
            {126, "Sony PlayStation 5 Slim", 1, 44990.00, "Ultra-high speed SSD, integrated I/O, Ray Tracing", "Sony", 4.9, 15, "images/ps5.jpg"},
            {127, "Nintendo Switch OLED", 1, 32990.00, "7-inch OLED screen, wide adjustable stand, wired LAN port", "Nintendo", 4.8, 20, "images/switch_oled.jpg"}
        };

        for (Object[] data : baseData) {
            insertIfNotExists(data);
        }
    }

    private static void populatePremiumProducts() throws SQLException {
        Object[][] premiumData = {
            {201, "MacBook Pro M3 Max", 1, 349999.00, "16-inch Liquid Retina XDR, M3 Max chip with 16-core CPU and 40-core GPU, 64GB RAM, 2TB SSD.", "Apple", 5.0, 10, "images/macbook_m3.jpg"},
            {202, "Sony A7R V Mirrorless Camera", 1, 389999.00, "61.0MP full-frame sensor, 8K video, AI-based autofocus, 8-stop image stabilization.", "Sony", 4.9, 5, "images/sony_a7rv.jpg"},
            {203, "Rolex Submariner Date", 2, 1250000.00, "Oyster, 41 mm, Oystersteel and yellow gold, Cerachrom bezel in black ceramic.", "Rolex", 4.9, 2, "images/rolex_sub.jpg"},
            {204, "Herman Miller Embody Chair", 4, 185000.00, "The highest standard for pressure distribution, natural alignment, and support for healthy movement.", "Herman Miller", 4.8, 15, "images/herman_miller.jpg"},
            {205, "Sennheiser HE-1 Orpheus", 1, 4500000.00, "The world's best headphones. Electrostatic system with marble amplifier and gold-vaporized ceramic transducers.", "Sennheiser", 5.0, 1, "images/sennheiser_he1.jpg"},
            {206, "Air Jordan 1 Retro High", 2, 18500.00, "The sneaker that started it all. Premium leather and iconic Chicago colorway.", "Nike", 4.8, 25, "images/jordan1.jpg"},
            {207, "Dior Sauvage Elixir", 6, 14500.00, "An extraordinarily concentrated fragrance steeped in the emblematic freshness of Sauvage.", "Dior", 4.7, 50, "images/dior_sauvage.jpg"},
            {208, "Razer Blade 16 (2024)", 1, 359999.00, "World's First OLED 240Hz Display, Intel Core i9-14900HX, NVIDIA GeForce RTX 4090.", "Razer", 4.9, 8, "images/razer_blade.jpg"}
        };

        for (Object[] data : premiumData) {
            insertIfNotExists(data);
        }
    }

    private static void insertIfNotExists(Object[] data) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT id FROM products WHERE name = ?";
            PreparedStatement psCheck = conn.prepareStatement(checkSql);
            psCheck.setString(1, (String) data[1]);
            ResultSet rs = psCheck.executeQuery();
            if (!rs.next()) {
                String insertSql = "INSERT INTO products (id, name, category_id, price, description, brand, rating, stock, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement psInsert = conn.prepareStatement(insertSql);
                psInsert.setInt(1, (Integer) data[0]);
                psInsert.setString(2, (String) data[1]);
                psInsert.setInt(3, (Integer) data[2]);
                psInsert.setDouble(4, (Double) data[3]);
                psInsert.setString(5, (String) data[4]);
                psInsert.setString(6, (String) data[5]);
                psInsert.setDouble(7, (Double) data[6]);
                psInsert.setInt(8, (Integer) data[7]);
                psInsert.setString(9, (String) data[8]);
                psInsert.executeUpdate();
                System.out.println("Inserted product: " + data[1]);
            } else {
                System.out.println("Product already exists: " + data[1]);
            }
        }
    }
}
