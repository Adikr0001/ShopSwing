package com.shopswing.model;

import java.sql.Timestamp;

/**
 * Product model representing an item in the catalog.
 */
public class Product {
    private int id;
    private String name;
    private int categoryId;
    private String categoryName;  // joined from categories table
    private double price;
    private String description;
    private String brand;
    private double rating;
    private int stock;
    private String imageUrl;
    private Timestamp createdAt;

    // Default constructor
    public Product() {}

    // Full constructor
    public Product(int id, String name, int categoryId, String categoryName,
                   double price, String description, String brand,
                   double rating, int stock, String imageUrl, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.rating = rating;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /**
     * Returns formatted price string like "Rs 89,999"
     */
    public String getFormattedPrice() {
        return String.format("Rs %,.0f", price);
    }

    /**
     * Returns integer star count for display (1-5)
     */
    public int getStarCount() {
        return (int) Math.round(rating);
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', price=" + price + "}";
    }
}
