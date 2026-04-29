package com.shopswing.model;

import java.sql.Timestamp;

/**
 * CartItem model representing an item in a user's shopping cart.
 * Includes joined product details for display convenience.
 */
public class CartItem {
    private int id;
    private int userId;
    private int productId;
    private int quantity;
    private Timestamp addedAt;

    // Joined fields from products table (for display)
    private String productName;
    private double productPrice;
    private String productBrand;
    private String productImageUrl;
    private String categoryName;

    // Default constructor
    public CartItem() {}

    // Parameterized constructor
    public CartItem(int id, int userId, int productId, int quantity, Timestamp addedAt) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }

    public String getProductBrand() { return productBrand; }
    public void setProductBrand(String productBrand) { this.productBrand = productBrand; }

    public String getProductImageUrl() { return productImageUrl; }
    public void setProductImageUrl(String productImageUrl) { this.productImageUrl = productImageUrl; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    /**
     * Returns subtotal for this cart item (price * quantity)
     */
    public double getSubtotal() {
        return productPrice * quantity;
    }

    /**
     * Returns formatted subtotal string
     */
    public String getFormattedSubtotal() {
        return String.format("Rs %,.0f", getSubtotal());
    }

    @Override
    public String toString() {
        return "CartItem{userId=" + userId + ", productId=" + productId + ", qty=" + quantity + "}";
    }
}
