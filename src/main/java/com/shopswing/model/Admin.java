package com.shopswing.model;

import java.sql.Timestamp;

/**
 * Admin model representing an administrator account.
 */
public class Admin {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String role;        // super_admin, admin, moderator
    private Timestamp createdAt;

    // Default constructor
    public Admin() {}

    // Parameterized constructor
    public Admin(int id, String username, String email,
                 String passwordHash, String role, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Admin{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
