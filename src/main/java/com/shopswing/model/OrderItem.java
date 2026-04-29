package com.shopswing.model;

/**
 * OrderItem model representing a single product line in an order.
 */
public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName;
    private double price;
    private int quantity;

    // Default constructor
    public OrderItem() {}

    // Parameterized constructor
    public OrderItem(int id, int orderId, int productId,
                     String productName, double price, int quantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Returns subtotal for this order item (price * quantity)
     */
    public double getSubtotal() {
        return price * quantity;
    }

    /**
     * Returns formatted subtotal string
     */
    public String getFormattedSubtotal() {
        return String.format("Rs %,.0f", getSubtotal());
    }

    /**
     * Returns formatted unit price string
     */
    public String getFormattedPrice() {
        return String.format("Rs %,.0f", price);
    }

    @Override
    public String toString() {
        return "OrderItem{orderId=" + orderId + ", product='" + productName + "', qty=" + quantity + "}";
    }
}
