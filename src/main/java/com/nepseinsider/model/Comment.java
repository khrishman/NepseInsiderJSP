package com.nepseinsider.model;

import java.sql.Timestamp;

/**
 * Comment — a user-posted message about a specific stock.
 * `username` is populated via JOIN when reading; it is not stored
 * in the comments table itself.
 */
public class Comment {

    private int       id;
    private String    stockSymbol;
    private int       userId;
    private String    username;
    private String    commentText;
    private Timestamp createdAt;

    public Comment() {}

    public int       getId()           { return id; }
    public void      setId(int id)     { this.id = id; }

    public String    getStockSymbol()  { return stockSymbol; }
    public void      setStockSymbol(String s) { this.stockSymbol = s; }

    public int       getUserId()       { return userId; }
    public void      setUserId(int u)  { this.userId = u; }

    public String    getUsername()     { return username; }
    public void      setUsername(String u) { this.username = u; }

    public String    getCommentText()  { return commentText; }
    public void      setCommentText(String t) { this.commentText = t; }

    public Timestamp getCreatedAt()    { return createdAt; }
    public void      setCreatedAt(Timestamp ts) { this.createdAt = ts; }
}
