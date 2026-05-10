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
 * LiveStockServlet
 * --------------------------------------------------------------------
 *  GET /live-stocks            -> list of all NEPSE stocks
 *  GET /live-stocks?q=NABIL    -> filtered by symbol substring
 *
 * Forwards to /WEB-INF/pages/live-stocks.jsp.
 *
 * NOTE: This intentionally uses a NEW URL "/live-stocks" so it does not
 * conflict with your existing StockListServlet on "/stocks".
 */
@WebServlet("/live-stocks")
public class LiveStockServlet extends HttpServlet {

    private static final String UPSTREAM =
            "https://nepse-40e276f8ff9b.herokuapp.com/api/stocks";

    private static volatile String cachedJson = null;
    private static volatile long   cachedAt   = 0L;
    private static final   long    TTL_MS     = 30_000L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String q = req.getParameter("q");
        if (q != null) q = q.trim().toUpperCase();

        List<Map<String, Object>> stocks = parseStocks(getStocksJson());

        if (q != null && !q.isEmpty()) {
            final String needle = q;
            List<Map<String, Object>> filtered = new ArrayList<>();
            for (Map<String, Object> s : stocks) {
                String sym = String.valueOf(s.get("symbol"));
                if (sym.contains(needle)) filtered.add(s);
            }
            stocks = filtered;
        }

        req.setAttribute("stocks", stocks);
        req.setAttribute("query", q == null ? "" : q);
        req.setAttribute("totalCount", stocks.size());
        req.getRequestDispatcher("/WEB-INF/pages/live-stocks.jsp").forward(req, resp);
    }

    /* ---------- upstream + cache ---------- */
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
            System.err.println("[LiveStockServlet] " + e.getMessage());
        }
        return cachedJson;
    }

    /* ---------- minimal JSON parser (no external libraries) ---------- */
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
            String objStr = body.substring(objStart, i);
            Map<String, Object> obj = parseFlatObject(objStr);
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
