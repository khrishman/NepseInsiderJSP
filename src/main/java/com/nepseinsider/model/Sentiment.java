package com.nepseinsider.model;

import java.sql.Timestamp;

/**
 * Sentiment — a single user's bullish/bearish vote on a stock.
 * One row per (stock_symbol, user_id).
 */
public class Sentiment {

    public static final String BULLISH = "BULLISH";
    public static final String BEARISH = "BEARISH";

    private int       id;
    private String    stockSymbol;
    private int       userId;
    private String    sentiment;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Sentiment() {}

    public int       getId()                   { return id; }
    public void      setId(int id)             { this.id = id; }

    public String    getStockSymbol()          { return stockSymbol; }
    public void      setStockSymbol(String s)  { this.stockSymbol = s; }

    public int       getUserId()               { return userId; }
    public void      setUserId(int u)          { this.userId = u; }

    public String    getSentiment()            { return sentiment; }
    public void      setSentiment(String s)    { this.sentiment = s; }

    public Timestamp getCreatedAt()            { return createdAt; }
    public void      setCreatedAt(Timestamp t) { this.createdAt = t; }

    public Timestamp getUpdatedAt()            { return updatedAt; }
    public void      setUpdatedAt(Timestamp t) { this.updatedAt = t; }

    /**
     * Aggregated bullish/bearish counts for a stock (or for the whole site).
     */
    public static class Summary {
        private String stockSymbol;
        private int    bullish;
        private int    bearish;

        public Summary(String symbol, int bull, int bear) {
            this.stockSymbol = symbol;
            this.bullish     = bull;
            this.bearish     = bear;
        }

        public String getStockSymbol() { return stockSymbol; }
        public int    getBullish()     { return bullish; }
        public int    getBearish()     { return bearish; }
        public int    getTotal()       { return bullish + bearish; }

        public double getBullishPct() {
            int t = getTotal();
            return t == 0 ? 50.0 : (bullish * 100.0) / t;
        }
        public double getBearishPct() {
            int t = getTotal();
            return t == 0 ? 50.0 : (bearish * 100.0) / t;
        }
    }
}
