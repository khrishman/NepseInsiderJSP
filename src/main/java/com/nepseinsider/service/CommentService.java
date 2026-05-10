package com.nepseinsider.service;

import com.nepseinsider.config.DBConfig;
import com.nepseinsider.model.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CommentService — JDBC operations for stock comments.
 * NOTE: users PK column is `user_id`, not `id`.
 */
public class CommentService {

    /** Insert a new comment. */
    public boolean addComment(String symbol, int userId, String text) {
        String sql = "INSERT INTO comments (stock_symbol, user_id, comment_text) " +
                "VALUES (?, ?, ?)";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol);
            ps.setInt   (2, userId);
            ps.setString(3, text);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CommentService.addComment] " + e.getMessage());
            return false;
        }
    }

    /** Get comments for a single stock, newest first. */
    public List<Comment> getCommentsForStock(String symbol, int limit) {
        String sql = "SELECT c.id, c.stock_symbol, c.user_id, c.comment_text, " +
                "       c.created_at, u.username " +
                "FROM   comments c " +
                "JOIN   users    u ON u.user_id = c.user_id " +
                "WHERE  c.stock_symbol = ? " +
                "ORDER  BY c.created_at DESC " +
                "LIMIT  ?";
        return runQuery(sql, ps -> {
            ps.setString(1, symbol);
            ps.setInt   (2, limit);
        });
    }

    /** Get the N most recent comments across all stocks. */
    public List<Comment> getRecentComments(int limit) {
        String sql = "SELECT c.id, c.stock_symbol, c.user_id, c.comment_text, " +
                "       c.created_at, u.username " +
                "FROM   comments c " +
                "JOIN   users    u ON u.user_id = c.user_id " +
                "ORDER  BY c.created_at DESC " +
                "LIMIT  ?";
        return runQuery(sql, ps -> ps.setInt(1, limit));
    }

    /** Delete a comment — only if owned by this user. */
    public boolean deleteComment(int commentId, int userId) {
        String sql = "DELETE FROM comments WHERE id = ? AND user_id = ?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[CommentService.deleteComment] " + e.getMessage());
            return false;
        }
    }

    /** Total comments for a single stock. */
    public int countForStock(String symbol) {
        String sql = "SELECT COUNT(*) FROM comments WHERE stock_symbol = ?";
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, symbol);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            return 0;
        }
    }

    // ---------- helpers ----------
    @FunctionalInterface
    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Comment> runQuery(String sql, PsBinder binder) {
        List<Comment> list = new ArrayList<>();
        try (Connection c = DBConfig.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Comment cmt = new Comment();
                    cmt.setId         (rs.getInt      ("id"));
                    cmt.setStockSymbol(rs.getString   ("stock_symbol"));
                    cmt.setUserId     (rs.getInt      ("user_id"));
                    cmt.setUsername   (rs.getString   ("username"));
                    cmt.setCommentText(rs.getString   ("comment_text"));
                    cmt.setCreatedAt  (rs.getTimestamp("created_at"));
                    list.add(cmt);
                }
            }
        } catch (SQLException e) {
            System.err.println("[CommentService.query] " + e.getMessage());
        }
        return list;
    }
}
