package com.nepseinsider.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LiveStockDetailServlet
 * --------------------------------------------------------------------
 *  GET /live-stock?symbol=NABIL
 *
 * Fetches one stock from the NEPSE API, forwards to live-stock.jsp.
 * The JSP renders the stock card + voting widget + comments thread.
 *
 * Uses a NEW URL "/live-stock" so it does not conflict with your
 * existing StockDetailServlet at "/stock-detail".
 */
@WebServlet("/live-stock")
public class LiveStockDetailServlet extends HttpServlet {

    private static final String UPSTREAM =
            "https://nepse-40e276f8ff9b.herokuapp.com/api/stocks";

    private static volatile String cachedJson = null;
    private static volatile long   cachedAt   = 0L;
    private static final   long    TTL_MS     = 30_000L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String symbol = req.getParameter("symbol");
        if (symbol == null || symbol.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/live-stocks");
            return;
        }
        symbol = symbol.trim().toUpperCase();

        Map<String, Object> stock = findStock(symbol);
        if (stock == null) {
            req.setAttribute("notFoundSymbol", symbol);
            req.getRequestDispatcher("/WEB-INF/pages/live-stock.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("stock", stock);
        req.setAttribute("symbol", symbol);
        req.getRequestDispatcher("/WEB-INF/pages/live-stock.jsp").forward(req, resp);
    }

    /* ---------- helpers ---------- */
    private Map<String, Object> findStock(String symbol) {
        for (Map<String, Object> s : parseStocks(getStocksJson())) {
            if (symbol.equals(String.valueOf(s.get("symbol")))) return s;
        }
        return null;
    }

    private String getStocksJson() {
        long now = System.currentTimeMillis();
        if (cachedJson != null && (now - cachedAt) < TTL_MS) return cachedJson;
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(UPSTREAM).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "NepseInsiderJSP/1.0");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(12000);
            if (conn.getResponseCode() != 200) { conn.disconnect(); return cachedJson; }
            StringBuilder sb = new StringBuilder(64 * 1024);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                char[] buf = new char[4096]; int n;
                while ((n = br.read(buf)) != -1) sb.append(buf, 0, n);
            }
            conn.disconnect();
            cachedJson = sb.toString();
            cachedAt   = now;
        } catch (Exception e) {
            System.err.println("[LiveStockDetailServlet] " + e.getMessage());
        }
        return cachedJson;
    }

    private List<Map<String, Object>> parseStocks(String json) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (json == null) return result;
        int dataIdx = json.indexOf("\"data\"");
        if (dataIdx < 0) return result;
        int arrStart = json.indexOf('[', dataIdx);
        int arrEnd   = json.lastIndexOf(']');
        if (arrStart < 0 || arrEnd <= arrStart) return result;
        String body = json.substring(arrStart + 1, arrEnd);
        int i = 0, n = body.length();
        while (i < n) {
            while (i < n && body.charAt(i) != '{') i++;
            if (i >= n) break;
            int objStart = i, depth = 0;
            for (; i < n; i++) {
                char c = body.charAt(i);
                if (c == '{') depth++;
                else if (c == '}') { depth--; if (depth == 0) { i++; break; } }
            }
            Map<String, Object> obj = parseFlatObject(body.substring(objStart, i));
            if (!obj.isEmpty()) result.add(obj);
        }
        return result;
    }

    private Map<String, Object> parseFlatObject(String objStr) {
        Map<String, Object> out = new HashMap<>();
        int s = objStr.indexOf('{');
        int e = objStr.lastIndexOf('}');
        if (s < 0 || e <= s) return out;
        String inner = objStr.substring(s + 1, e);

        int p = 0, len = inner.length();
        while (p < len) {
            int k1 = inner.indexOf('"', p);     if (k1 < 0) break;
            int k2 = inner.indexOf('"', k1 + 1); if (k2 < 0) break;
            String key = inner.substring(k1 + 1, k2);

            int colon = inner.indexOf(':', k2); if (colon < 0) break;
            int v1 = inner.indexOf('"', colon); if (v1 < 0) break;
            int v2 = inner.indexOf('"', v1 + 1); if (v2 < 0) break;
            String val = inner.substring(v1 + 1, v2);

            switch (key) {
                case "ltp": case "open": case "high": case "low":
                case "percent_change": case "volume":
                    try { out.put(key, Double.parseDouble(val.replace(",", ""))); }
                    catch (Exception ex) { out.put(key, 0.0); }
                    break;
                default:
                    out.put(key, val);
            }
            p = v2 + 1;
        }
        return out;
    }
}
