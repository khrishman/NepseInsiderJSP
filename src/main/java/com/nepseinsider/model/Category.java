package com.nepseinsider.model;

/**
 * Category model (POJO).
 * Represents a sector/category in the `categories` table.
 */
public class Category {
    private int id;
    private String name;
    private String description;
    private boolean active;

    public Category() {}

    public Category(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
