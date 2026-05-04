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

/**
 * ApiProxyServlet
 * ----------------------------------------------------------------------
 * Server-side proxy for the live NEPSE API. Solves two problems:
 *   1. Avoids browser CORS errors (JS can't hit the Heroku endpoint
 *      directly from localhost).
 *   2. Caches the upstream response for {@link #CACHE_TTL_MS} so we
 *      don't hammer the public API on every page refresh.
 *
 * Endpoint: GET /api-proxy/stocks
 * Returns:  application/json (the upstream JSON, untouched)
 */
@WebServlet("/api-proxy/stocks")
public class ApiProxyServlet extends HttpServlet {

    private static final String UPSTREAM_URL =
            "https://nepse-40e276f8ff9b.herokuapp.com/api/stocks";

    /** How long to keep the cached response before refetching. */
    private static final long CACHE_TTL_MS = 30_000L;

    /** In-memory cache (single instance, thread-safe via volatile). */
    private static volatile String  cachedBody  = null;
    private static volatile long    cachedAt    = 0L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("Cache-Control", "public, max-age=30");

        String body = getCachedOrFetch();

        if (body == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_GATEWAY);
            try (PrintWriter out = resp.getWriter()) {
                out.write("{\"error\":\"Upstream NEPSE API is unreachable.\"}");
            }
            return;
        }

        try (PrintWriter out = resp.getWriter()) {
            out.write(body);
        }
    }

    /**
     * Returns the cached upstream body if still fresh, otherwise refetches.
     * On fetch failure, returns the stale cache if we have one (graceful
     * degradation), or null.
     */
    private String getCachedOrFetch() {
        long now = System.currentTimeMillis();
        if (cachedBody != null && (now - cachedAt) < CACHE_TTL_MS) {
            return cachedBody;
        }

        try {
            String fresh = fetchUpstream();
            if (fresh != null && !fresh.isEmpty()) {
                cachedBody = fresh;
                cachedAt   = now;
            }
            return cachedBody;
        } catch (Exception e) {
            // Network failure — fall back to whatever we last had
            System.err.println("[ApiProxy] Upstream fetch failed: " + e.getMessage());
            return cachedBody;
        }
    }

    /**
     * Performs the actual HTTPS request to the NEPSE API.
     * Uses HttpURLConnection (built into the JDK — no extra libraries).
     */
    private String fetchUpstream() throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(UPSTREAM_URL).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "NepseInsiderJSP/1.0");
        conn.setConnectTimeout(8000);
        conn.setReadTimeout(12000);

        int status = conn.getResponseCode();
        if (status != 200) {
            conn.disconnect();
            throw new IOException("Upstream returned HTTP " + status);
        }

        StringBuilder sb = new StringBuilder(64 * 1024);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            char[] buf = new char[4096];
            int n;
            while ((n = br.read(buf)) != -1) {
                sb.append(buf, 0, n);
            }
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }
}
