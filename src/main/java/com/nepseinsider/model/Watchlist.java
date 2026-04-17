package com.nepseinsider.model;

import java.sql.Timestamp;

/**
 * Watchlist model (POJO).
 * Reserved for Phase 2 — schema and model ready so backend can extend cleanly.
 */
public class Watchlist {
    private int id;
    private int userId;
    private int stockId;
    private Timestamp addedAt;

    public Watchlist() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public Timestamp getAddedAt() { return addedAt; }
    public void setAddedAt(Timestamp addedAt) { this.addedAt = addedAt; }
}
