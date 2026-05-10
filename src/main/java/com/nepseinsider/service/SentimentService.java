package com.nepseinsider.service;

import com.nepseinsider.config.DBConfig;
import com.nepseinsider.model.Sentiment;

import java.sql.*;

/**
 * SentimentService — bullish/bearish votes.
 * One vote per (user, stock); subsequent votes overwrite via
 * INSERT ... ON DUPLICATE KEY UPDATE.
 */
public class SentimentService {

    /** Cast or update a user's vote on a stock. */
    public boolean vote(String symbol, int userId, String sentiment) {
        if (!Sentiment.BULLISH.equals(sentiment) && !Sentiment.BEARISH.equals(sentiment)) {
            return false;
        }
        String sql =
                "INSERT INTO sentiments (stock_symbol, user_id, sentiment) " +
                        "VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE sentiment = VALUES(sentiment)";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol);
            ps.setInt   (2, userId);
            ps.setString(3, sentiment);
            return ps.executeUpdate() >= 1;
        } catch (SQLException e) {
            System.err.println("[SentimentService.vote] " + e.getMessage());
            return false;
        }
    }

    /** Remove a user's vote on a stock (toggle off). */
    public boolean removeVote(String symbol, int userId) {
        String sql = "DELETE FROM sentiments WHERE stock_symbol = ? AND user_id = ?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol);
            ps.setInt   (2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /** Get this user's current vote on this stock, or null if none. */
    public String getUserVote(String symbol, int userId) {
        String sql = "SELECT sentiment FROM sentiments " +
                "WHERE  stock_symbol = ? AND user_id = ?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol);
            ps.setInt   (2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (SQLException e) {
            return null;
        }
    }

    /** Aggregate counts for a single stock. */
    public Sentiment.Summary getStockSummary(String symbol) {
        String sql =
                "SELECT " +
                        "  SUM(CASE WHEN sentiment='BULLISH' THEN 1 ELSE 0 END) AS bull, " +
                        "  SUM(CASE WHEN sentiment='BEARISH' THEN 1 ELSE 0 END) AS bear " +
                        "FROM sentiments WHERE stock_symbol = ?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Sentiment.Summary(symbol, rs.getInt("bull"), rs.getInt("bear"));
                }
            }
        } catch (SQLException e) {
            System.err.println("[SentimentService.getStockSummary] " + e.getMessage());
        }
        return new Sentiment.Summary(symbol, 0, 0);
    }

    /** Aggregate counts across the whole site (for the home Community Pulse). */
    public Sentiment.Summary getGlobalSummary() {
        String sql =
                "SELECT " +
                        "  SUM(CASE WHEN sentiment='BULLISH' THEN 1 ELSE 0 END) AS bull, " +
                        "  SUM(CASE WHEN sentiment='BEARISH' THEN 1 ELSE 0 END) AS bear " +
                        "FROM sentiments";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Sentiment.Summary("ALL", rs.getInt("bull"), rs.getInt("bear"));
            }
        } catch (SQLException e) {
            System.err.println("[SentimentService.getGlobalSummary] " + e.getMessage());
        }
        return new Sentiment.Summary("ALL", 0, 0);
    }
}
