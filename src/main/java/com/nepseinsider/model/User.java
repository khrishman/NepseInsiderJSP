package com.nepseinsider.model;

import java.sql.Timestamp;

/**
 * User model (POJO).
 * Represents a row in the `users` table.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phone;
    private String role;       // "ADMIN" or "USER"
    private String status;     // "ACTIVE", "INACTIVE", "SUSPENDED"
    private Timestamp createdAt;
    private Timestamp lastLogin;

    public User() {}

    // ----- Getters & Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }

    // ----- Helpers -----
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
