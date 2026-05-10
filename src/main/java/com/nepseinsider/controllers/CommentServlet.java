package com.nepseinsider.controllers;

import com.nepseinsider.model.Comment;
import com.nepseinsider.model.User;
import com.nepseinsider.service.CommentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * CommentServlet
 * --------------------------------------------------------------------
 * GET  /comments?symbol=NABIL          → JSON list of comments
 * POST /comments?symbol=NABIL&text=... → add new comment (login required)
 * POST /comments?action=delete&id=42   → delete own comment
 *
 * Returns JSON for AJAX consumers; redirects on form submissions.
 */
@WebServlet("/comments")
public class CommentServlet extends HttpServlet {

    private final CommentService service = new CommentService();

    /* ---------- GET: return comments for a stock as JSON ---------- */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String symbol = req.getParameter("symbol");
        if (symbol == null || symbol.trim().isEmpty()) {
            sendJsonError(resp, 400, "symbol is required");
            return;
        }

        int limit = 20;
        try { limit = Math.min(100, Math.max(1, Integer.parseInt(req.getParameter("limit")))); }
        catch (Exception ignored) {}

        List<Comment> comments = service.getCommentsForStock(symbol.trim(), limit);
        writeCommentsJson(resp, comments);
    }

    /* ---------- POST: add or delete ---------- */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Auth
        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            sendJsonError(resp, 401, "You must log in to comment.");
            return;
        }

        String action = req.getParameter("action");

        // ---- Delete ----
        if ("delete".equalsIgnoreCase(action)) {
            try {
                int id = Integer.parseInt(req.getParameter("id"));
                boolean ok = service.deleteComment(id, user.getId());
                if (!ok) { sendJsonError(resp, 404, "Comment not found."); return; }
                sendJsonOk(resp, "{\"deleted\":true}");
            } catch (NumberFormatException e) {
                sendJsonError(resp, 400, "Invalid comment id.");
            }
            return;
        }

        // ---- Add ----
        String symbol = req.getParameter("symbol");
        String text   = req.getParameter("text");

        if (symbol == null || symbol.trim().isEmpty()) {
            sendJsonError(resp, 400, "Stock symbol is required.");
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            sendJsonError(resp, 400, "Comment text cannot be empty.");
            return;
        }
        if (text.length() > 1000) {
            sendJsonError(resp, 400, "Comment is too long (max 1000 characters).");
            return;
        }

        boolean ok = service.addComment(symbol.trim().toUpperCase(),
                user.getId(),
                text.trim());
        if (!ok) { sendJsonError(resp, 500, "Could not save comment."); return; }
        sendJsonOk(resp, "{\"saved\":true}");
    }

    /* ---------- JSON helpers ---------- */

    private void writeCommentsJson(HttpServletResponse resp, List<Comment> list)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        StringBuilder sb = new StringBuilder("{\"comments\":[");
        for (int i = 0; i < list.size(); i++) {
            Comment c = list.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
                    .append("\"id\":").append(c.getId()).append(",")
                    .append("\"username\":\"").append(esc(c.getUsername())).append("\",")
                    .append("\"text\":\"").append(esc(c.getCommentText())).append("\",")
                    .append("\"createdAt\":\"").append(c.getCreatedAt()).append("\"")
                    .append("}");
        }
        sb.append("]}");
        try (PrintWriter out = resp.getWriter()) { out.write(sb.toString()); }
    }

    private void sendJsonOk(HttpServletResponse resp, String body) throws IOException {
        resp.setStatus(200);
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) { out.write(body); }
    }

    private void sendJsonError(HttpServletResponse resp, int status, String msg)
            throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"error\":\"" + esc(msg) + "\"}");
        }
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "");
    }
}
