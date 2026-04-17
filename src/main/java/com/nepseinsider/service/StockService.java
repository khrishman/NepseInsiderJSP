package com.nepseinsider.service;

import com.nepseinsider.config.DBConfig;
import com.nepseinsider.model.Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles stock CRUD and search operations.
 */
public class StockService {

    private static final String BASE_SELECT =
            "SELECT s.*, c.name AS category_name " +
            "FROM stocks s JOIN categories c ON s.category_id = c.category_id ";

    /** Return all stocks ordered by symbol. */
    public List<Stock> getAllStocks() {
        List<Stock> list = new ArrayList<>();
        String sql = BASE_SELECT + "ORDER BY s.symbol ASC";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapStock(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Search stocks by partial match on symbol or company name. */
    public List<Stock> searchStocks(String keyword) {
        List<Stock> list = new ArrayList<>();
        String sql = BASE_SELECT +
                "WHERE s.symbol LIKE ? OR s.company_name LIKE ? " +
                "ORDER BY s.symbol ASC";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapStock(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Stock getStockById(int stockId) {
        String sql = BASE_SELECT + "WHERE s.stock_id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, stockId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStock(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSymbolTaken(String symbol) {
        String sql = "SELECT COUNT(*) FROM stocks WHERE symbol = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, symbol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addStock(Stock s) {
        String sql = "INSERT INTO stocks (symbol, company_name, category_id, current_price, " +
                     "previous_price, market_cap, volume, year_listed, sentiment, description) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSymbol());
            ps.setString(2, s.getCompanyName());
            ps.setInt(3, s.getCategoryId());
            ps.setDouble(4, s.getCurrentPrice());
            ps.setDouble(5, s.getPreviousPrice());
            ps.setDouble(6, s.getMarketCap());
            ps.setLong(7, s.getVolume());
            ps.setInt(8, s.getYearListed());
            ps.setString(9, s.getSentiment() == null ? "NEUTRAL" : s.getSentiment());
            ps.setString(10, s.getDescription());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStock(Stock s) {
        String sql = "UPDATE stocks SET symbol = ?, company_name = ?, category_id = ?, " +
                     "current_price = ?, previous_price = ?, market_cap = ?, volume = ?, " +
                     "year_listed = ?, sentiment = ?, description = ? WHERE stock_id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.getSymbol());
            ps.setString(2, s.getCompanyName());
            ps.setInt(3, s.getCategoryId());
            ps.setDouble(4, s.getCurrentPrice());
            ps.setDouble(5, s.getPreviousPrice());
            ps.setDouble(6, s.getMarketCap());
            ps.setLong(7, s.getVolume());
            ps.setInt(8, s.getYearListed());
            ps.setString(9, s.getSentiment() == null ? "NEUTRAL" : s.getSentiment());
            ps.setString(10, s.getDescription());
            ps.setInt(11, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStock(int stockId) {
        String sql = "DELETE FROM stocks WHERE stock_id = ?";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, stockId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getTotalStocks() {
        String sql = "SELECT COUNT(*) FROM stocks";
        try (Connection con = DBConfig.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Stock mapStock(ResultSet rs) throws SQLException {
        Stock s = new Stock();
        s.setId(rs.getInt("stock_id"));
        s.setSymbol(rs.getString("symbol"));
        s.setCompanyName(rs.getString("company_name"));
        s.setCategoryId(rs.getInt("category_id"));
        s.setCategoryName(rs.getString("category_name"));
        s.setCurrentPrice(rs.getDouble("current_price"));
        s.setPreviousPrice(rs.getDouble("previous_price"));
        s.setMarketCap(rs.getDouble("market_cap"));
        s.setVolume(rs.getLong("volume"));
        s.setYearListed(rs.getInt("year_listed"));
        s.setSentiment(rs.getString("sentiment"));
        s.setDescription(rs.getString("description"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        s.setUpdatedAt(rs.getTimestamp("updated_at"));
        return s;
    }
}
