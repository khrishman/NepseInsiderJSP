-- =====================================================
-- NepseInsider Database Schema
-- Phase 1 MVP + Phase 2 placeholders
-- =====================================================
DROP DATABASE IF EXISTS nepseinsider_db;
CREATE DATABASE nepseinsider_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE nepseinsider_db;

-- ---------- Users ----------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password      VARCHAR(255) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE,
    full_name     VARCHAR(100) NOT NULL,
    phone         VARCHAR(15),
    role          ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    status        ENUM('ACTIVE','INACTIVE','SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login    TIMESTAMP NULL
);

-- ---------- Categories (stock sectors) ----------
CREATE TABLE categories (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(80) NOT NULL UNIQUE,
    description   VARCHAR(255),
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- Stocks ----------
CREATE TABLE stocks (
    stock_id       INT AUTO_INCREMENT PRIMARY KEY,
    symbol         VARCHAR(20)  NOT NULL UNIQUE,
    company_name   VARCHAR(150) NOT NULL,
    category_id    INT NOT NULL,
    current_price  DECIMAL(15,2) NOT NULL DEFAULT 0,
    previous_price DECIMAL(15,2) NOT NULL DEFAULT 0,
    market_cap     DECIMAL(20,2) NOT NULL DEFAULT 0,
    volume         BIGINT NOT NULL DEFAULT 0,
    year_listed    INT,
    sentiment      ENUM('BULLISH','BEARISH','NEUTRAL') DEFAULT 'NEUTRAL',
    description    TEXT,
    created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT
);

-- ---------- Watchlist (Phase 2 ready) ----------
CREATE TABLE watchlist (
    watchlist_id  INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    stock_id      INT NOT NULL,
    added_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uniq_user_stock (user_id, stock_id),
    FOREIGN KEY (user_id)  REFERENCES users(user_id)  ON DELETE CASCADE,
    FOREIGN KEY (stock_id) REFERENCES stocks(stock_id) ON DELETE CASCADE
);

-- =====================================================
-- Seed Data
-- =====================================================

-- Default admin (password: admin123) and sample users
INSERT INTO users (username, password, email, full_name, phone, role) VALUES
('admin', 'admin123', 'admin@nepseinsider.com', 'System Administrator', '9800000000', 'ADMIN'),
('john',  'john123',  'john@email.com',  'John Doe',   '9811111111', 'USER'),
('ram',   'ram123',   'ram@email.com',   'Ram Sharma', '9822222222', 'USER');

-- NEPSE sectors
INSERT INTO categories (name, description) VALUES
('Commercial Banks',  'A-class commercial banks'),
('Development Banks', 'B-class development banks'),
('Finance Companies', 'C-class financial institutions'),
('Hydropower',        'Electricity generation companies'),
('Life Insurance',    'Life insurance companies'),
('Non-Life Insurance','General insurance companies'),
('Hotels',            'Tourism and hospitality sector'),
('Manufacturing',     'Manufacturing and processing');

-- Sample stocks
INSERT INTO stocks (symbol, company_name, category_id, current_price, previous_price, market_cap, volume, year_listed, sentiment, description) VALUES
('NABIL', 'Nabil Bank Limited',              1, 565.00,   558.00,   108000000000, 125000, 1984, 'BULLISH', 'One of the largest private-sector banks in Nepal.'),
('NIMB',  'Nepal Investment Mega Bank',      1, 225.50,   230.00,   95000000000,  85000,  1986, 'NEUTRAL', 'Leading commercial bank formed after a mega-merger.'),
('NICA',  'NIC Asia Bank Limited',           1, 420.00,   415.00,   75000000000,  95000,  1998, 'BULLISH', 'Dynamic commercial bank with wide digital reach.'),
('UPPER', 'Upper Tamakoshi Hydropower',      4, 285.00,   290.00,   30500000000,  42000,  2011, 'BEARISH', '456 MW flagship hydropower project of Nepal.'),
('API',   'API Power Company Limited',       4, 195.75,   192.00,   12000000000,  28000,  2014, 'BULLISH', 'Run-of-river hydropower operator.'),
('NLIC',  'Nepal Life Insurance Company',    5, 590.00,   580.00,   45000000000,  15000,  2001, 'BULLISH', 'Largest life insurance company in Nepal.'),
('SHL',   'Soaltee Hotel Limited',           7, 310.00,   305.00,   10000000000,  8000,   1971, 'NEUTRAL', 'Premier 5-star hotel in Kathmandu.'),
('UNL',   'Unilever Nepal Limited',          8, 38000.00, 37500.00, 52000000000,  500,    1994, 'BULLISH', 'Leading FMCG manufacturer in Nepal.');
