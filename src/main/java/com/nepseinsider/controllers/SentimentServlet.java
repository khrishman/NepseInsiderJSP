package com.nepseinsider.controllers;

import com.nepseinsider.model.Sentiment;
import com.nepseinsider.model.User;
import com.nepseinsider.service.SentimentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * SentimentServlet
 * --------------------------------------------------------------------
 *  GET  /sentiment?symbol=NABIL    → summary for one stock (JSON)
 *  GET  /sentiment?global=true     → site-wide summary (for home pulse)
 *  POST /sentiment?symbol=...&vote=BULLISH|BEARISH   → cast vote
 *  POST /sentiment?action=remove&symbol=...          → undo vote
 */
@WebServlet("/sentiment")
public class SentimentServlet extends HttpServlet {

    private final SentimentService service = new SentimentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Site-wide summary — used by Community Pulse on the home page
        if ("true".equalsIgnoreCase(req.getParameter("global"))) {
            writeSummary(resp, service.getGlobalSummary(), null);
            return;
        }

        String symbol = req.getParameter("symbol");
        if (symbol == null || symbol.trim().isEmpty()) {
            sendError(resp, 400, "symbol is required");
            return;
        }

        Sentiment.Summary sum = service.getStockSummary(symbol.trim().toUpperCase());

        // Include caller's own vote if logged in
        String myVote = null;
        HttpSession session = req.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        if (u != null) myVote = service.getUserVote(symbol.trim().toUpperCase(), u.getId());

        writeSummary(resp, sum, myVote);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = (session == null) ? null : (User) session.getAttribute("user");
        if (user == null) {
            sendError(resp, 401, "Please log in to vote.");
            return;
        }

        String symbol = req.getParameter("symbol");
        if (symbol == null || symbol.trim().isEmpty()) {
            sendError(resp, 400, "symbol is required");
            return;
        }
        symbol = symbol.trim().toUpperCase();

        if ("remove".equalsIgnoreCase(req.getParameter("action"))) {
            service.removeVote(symbol, user.getId());
            writeSummary(resp, service.getStockSummary(symbol), null);
            return;
        }

        String vote = req.getParameter("vote");
        if (!Sentiment.BULLISH.equals(vote) && !Sentiment.BEARISH.equals(vote)) {
            sendError(resp, 400, "vote must be BULLISH or BEARISH");
            return;
        }

        boolean ok = service.vote(symbol, user.getId(), vote);
        if (!ok) { sendError(resp, 500, "Could not record vote."); return; }

        writeSummary(resp, service.getStockSummary(symbol), vote);
    }

    /* ---------- helpers ---------- */

    private void writeSummary(HttpServletResponse resp, Sentiment.Summary s, String myVote)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"symbol\":\"").append(s.getStockSymbol()).append("\",");
        sb.append("\"bullish\":").append(s.getBullish()).append(",");
        sb.append("\"bearish\":").append(s.getBearish()).append(",");
        sb.append("\"bullishPct\":").append(String.format("%.1f", s.getBullishPct())).append(",");
        sb.append("\"bearishPct\":").append(String.format("%.1f", s.getBearishPct())).append(",");
        sb.append("\"total\":").append(s.getTotal());
        if (myVote != null) sb.append(",\"myVote\":\"").append(myVote).append("\"");
        sb.append("}");
        try (PrintWriter out = resp.getWriter()) { out.write(sb.toString()); }
    }

    private void sendError(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"error\":\"" + msg.replace("\"", "\\\"") + "\"}");
        }
    }
}
