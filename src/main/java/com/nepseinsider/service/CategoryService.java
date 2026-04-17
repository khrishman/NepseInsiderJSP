package com.nepseinsider.service;

import com.nepseinsider.config.DBConfig;
import com.nepseinsider.model.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles stock category (sector) lookups.
 */
public class CategoryService {

    /** Return all active categories ordered by name. */
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE active = TRUE ORDER BY name";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("category_id"));
                c.setName(rs.getString("name"));
                c.setDescription(rs.getString("description"));
                c.setActive(rs.getBoolean("active"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
