package com.nepseinsider.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ApiProxyServlet
 * --------------------------------------------------------------------
 * Server-side proxy for the live NEPSE API.
 *   GET /api-proxy/stocks  ->  https://nepse-40e276f8ff9b.herokuapp.com/api/stocks
 *
 * Solves browser CORS and adds a 30-second in-memory cache so repeated
 * page loads don't hammer the upstream service.
 */
@WebServlet("/api-proxy/*")
public class ApiProxyServlet extends HttpServlet {

    private static final String UPSTREAM_BASE =
            "https://nepse-40e276f8ff9b.herokuapp.com/api/";
    private static final long   CACHE_TTL_MS  = 30_000L;

    private static final Map<String, CacheEntry> CACHE = new ConcurrentHashMap<>();

    private static final class CacheEntry {
        final String body;
        final long   savedAt;
        CacheEntry(String body, long savedAt) {
            this.body = body; this.savedAt = savedAt;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            sendError(resp, 400, "endpoint required, e.g. /api-proxy/stocks");
            return;
        }
        String endpoint = path.substring(1).replaceAll("[^A-Za-z0-9_-]", "");
        if (endpoint.isEmpty()) {
            sendError(resp, 400, "invalid endpoint");
            return;
        }

        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "public, max-age=30");

        String body = getCachedOrFetch(endpoint);
        if (body == null) {
            sendError(resp, 502, "Upstream NEPSE API unreachable.");
            return;
        }
        try (PrintWriter out = resp.getWriter()) { out.write(body); }
    }

    private String getCachedOrFetch(String endpoint) {
        long now = System.currentTimeMillis();
        CacheEntry e = CACHE.get(endpoint);
        if (e != null && (now - e.savedAt) < CACHE_TTL_MS) return e.body;

        try {
            String fresh = fetchUpstream(endpoint);
            if (fresh != null && !fresh.isEmpty()) {
                CACHE.put(endpoint, new CacheEntry(fresh, now));
                return fresh;
            }
        } catch (Exception ex) {
            System.err.println("[ApiProxy] " + ex.getMessage());
        }
        return e == null ? null : e.body;   // graceful stale fallback
    }

    private String fetchUpstream(String endpoint) throws IOException {
        URL url = new URL(UPSTREAM_BASE + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "NepseInsiderJSP/1.0");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(12000);

        int status = conn.getResponseCode();
        if (status != 200) { conn.disconnect(); throw new IOException("Upstream HTTP " + status); }

        StringBuilder sb = new StringBuilder(64 * 1024);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            char[] buf = new char[4096]; int n;
            while ((n = br.read(buf)) != -1) sb.append(buf, 0, n);
        } finally { conn.disconnect(); }
        return sb.toString();
    }

    private void sendError(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.write("{\"error\":\"" + msg.replace("\"", "\\\"") + "\"}");
        }
    }
}
