package com.shopswing.model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Order model representing a customer's placed order.
 */
public class Order {
    private int id;
    private int userId;
    private double totalAmount;
    private String shippingAddress;
    private String status;          // Placed, Processing, Shipped, Delivered, Cancelled
    private String paymentMethod;
    private Timestamp createdAt;

    // Joined fields
    private String username;
    private int itemCount;
    private List<OrderItem> items;

    // Default constructor
    public Order() {}

    // Parameterized constructor
    public Order(int id, int userId, double totalAmount, String shippingAddress,
                 String status, String paymentMethod, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.shippingAddress = shippingAddress;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    /**
     * Returns formatted total amount string
     */
    public String getFormattedTotal() {
        return String.format("Rs %,.2f", totalAmount);
    }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", total=" + totalAmount + ", status='" + status + "'}";
    }
}
