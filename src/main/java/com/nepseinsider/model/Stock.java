package com.nepseinsider.model;

import java.sql.Timestamp;

/**
 * Stock model (POJO).
 * Represents a row in the `stocks` table, with a joined category name.
 */
public class Stock {
    private int id;
    private String symbol;
    private String companyName;
    private int categoryId;
    private String categoryName;     // Joined field from categories table
    private double currentPrice;
    private double previousPrice;
    private double marketCap;
    private long volume;
    private int yearListed;
    private String sentiment;        // BULLISH / BEARISH / NEUTRAL
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Stock() {}

    /**
     * Computes percentage change between current and previous price.
     */
    public double getChangePercent() {
        if (previousPrice == 0) return 0.0;
        return ((currentPrice - previousPrice) / previousPrice) * 100.0;
    }

    // ----- Getters & Setters -----
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(double currentPrice) { this.currentPrice = currentPrice; }

    public double getPreviousPrice() { return previousPrice; }
    public void setPreviousPrice(double previousPrice) { this.previousPrice = previousPrice; }

    public double getMarketCap() { return marketCap; }
    public void setMarketCap(double marketCap) { this.marketCap = marketCap; }

    public long getVolume() { return volume; }
    public void setVolume(long volume) { this.volume = volume; }

    public int getYearListed() { return yearListed; }
    public void setYearListed(int yearListed) { this.yearListed = yearListed; }

    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
