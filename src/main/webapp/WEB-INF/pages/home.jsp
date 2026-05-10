<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Home | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<%-- Sector indices ticker --%>
<div class="ticker-strip" id="tickerStrip">
    <div class="ticker-track" id="tickerTrack">
        <span class="ticker-loading">Loading sector indices…</span>
    </div>
</div>

<%-- =========================================================
     HERO
     ========================================================= --%>
<section class="hero-stage">
    <canvas id="chartBg" class="hero-chart" aria-hidden="true"></canvas>
    <div class="hero-overlay" aria-hidden="true"></div>
    <div class="hero-grid-decoration" aria-hidden="true"></div>

    <div class="hero-inner">
        <span class="hero-badge"><span class="dot"></span> Live NEPSE Insights Platform</span>

        <h1 class="hero-title">
            Nepal's Stock Market,<br>
            <span class="text-gradient">Decoded in Real Time.</span>
        </h1>

        <p class="hero-subtitle">
            Track every NEPSE-listed stock with live prices,
            spot the day's biggest movers, and see what fellow investors
            think before you place your next trade.
        </p>

        <form action="${pageContext.request.contextPath}/stocks" method="get" class="hero-search">
            <span class="hero-search-icon">🔍</span>
            <input type="text" name="q" placeholder="Search stocks by symbol (e.g. NABIL, NHPC, API)…"
                   autocomplete="off">
            <button type="submit" class="btn btn-primary">Search</button>
        </form>

        <div class="hero-cta">
            <a href="${pageContext.request.contextPath}/live-stocks"
               class="btn btn-ghost">Browse All Stocks</a>
            <c:if test="${sessionScope.user == null}">
                <a href="${pageContext.request.contextPath}/register"
                   class="btn btn-ghost">Create Free Account</a>
            </c:if>
            <c:if test="${sessionScope.user != null}">
                <a href="${pageContext.request.contextPath}/watchlist"
                   class="btn btn-ghost">⭐ My Watchlist</a>
            </c:if>
        </div>
    </div>
</section>

<%-- =========================================================
     LIVE MARKET STATS
     ========================================================= --%>
<section class="market-stats">
    <div class="stat-tile">
        <div class="stat-label">Listed Companies</div>
        <div class="stat-value" id="statCount">—</div>
        <div class="stat-foot">Common stocks, live count</div>
    </div>
    <div class="stat-tile stat-up">
        <div class="stat-label">📈 Gainers</div>
        <div class="stat-value" id="statGainers">—</div>
        <div class="stat-foot">Stocks closing positive</div>
    </div>
    <div class="stat-tile stat-down">
        <div class="stat-label">📉 Losers</div>
        <div class="stat-value" id="statLosers">—</div>
        <div class="stat-foot">Stocks closing negative</div>
    </div>
    <div class="stat-tile">
        <div class="stat-label">💹 Market Turnover</div>
        <div class="stat-value" id="statVolume">—</div>
        <div class="stat-foot">Total rupees traded today</div>
    </div>
</section>

<%-- =========================================================
     TOP MOVERS
     ========================================================= --%>
<section class="movers-grid">
    <div class="mover-card">
        <div class="mover-head">
            <h3>🚀 Top Gainers</h3>
            <span class="badge badge-bullish">LIVE</span>
        </div>
        <table class="mover-table" id="topGainers">
            <tbody><tr><td colspan="3" class="loading-cell">Loading…</td></tr></tbody>
        </table>
    </div>

    <div class="mover-card">
        <div class="mover-head">
            <h3>📉 Top Losers</h3>
            <span class="badge badge-bearish">LIVE</span>
        </div>
        <table class="mover-table" id="topLosers">
            <tbody><tr><td colspan="3" class="loading-cell">Loading…</td></tr></tbody>
        </table>
    </div>

    <div class="mover-card">
        <div class="mover-head">
            <h3>🔥 Most Traded</h3>
            <span class="badge badge-neutral">BY VOLUME</span>
        </div>
        <table class="mover-table" id="mostTraded">
            <tbody><tr><td colspan="3" class="loading-cell">Loading…</td></tr></tbody>
        </table>
    </div>
</section>

<%-- =========================================================
     COMMUNITY PULSE  (real votes from /sentiment endpoint)
     ========================================================= --%>
<section class="pulse-section">
    <div class="pulse-card">
        <div class="pulse-head">
            <h2 class="section-title-left">💬 Community Pulse</h2>
            <span class="badge badge-bullish">LIVE</span>
        </div>
        <p class="section-sub-left">
            What does the NepseInsider community think? Browse to any stock,
            tap <strong>Bullish</strong> or <strong>Bearish</strong>, and your
            vote shows up here in real time.
        </p>

        <div class="pulse-meter">
            <div class="meter-bar">
                <div class="meter-fill bullish" id="pulseBull" style="width:50%"></div>
                <div class="meter-fill bearish" id="pulseBear" style="width:50%"></div>
            </div>
            <div class="meter-legend">
                <span><span class="dot bull"></span> <span id="pulseBullLbl">50% Bullish</span></span>
                <span><span class="dot bear"></span> <span id="pulseBearLbl">50% Bearish</span></span>
            </div>
        </div>
        <p class="pulse-note" id="pulseMeta">
            Be the first to cast your sentiment on a stock.
        </p>

        <c:if test="${sessionScope.user == null}">
            <a href="${pageContext.request.contextPath}/login"
               class="btn btn-primary btn-sm" style="margin-top:8px;">
                Log in to vote &rarr;
            </a>
        </c:if>
    </div>

    <div class="pulse-card">
        <h2 class="section-title-left">📰 Why NepseInsider</h2>
        <p class="section-sub-left">A simpler way to follow Nepal's stock market.</p>
        <ul class="why-list">
            <li><strong>Every NEPSE stock, one place</strong> — prices, volumes,
                and percentage moves at a glance.</li>
            <li><strong>Catch the day's movers</strong> — top gainers, top losers,
                and most-traded stocks updated live.</li>
            <li><strong>Save your favourites</strong> — build a personal watchlist
                and check in whenever you want.</li>
            <li><strong>Crowd wisdom</strong> — see what fellow investors think
                before you commit your money.</li>
            <li><strong>Free, forever</strong> — no fees, no premium tiers, no ads.</li>
        </ul>
    </div>
</section>

<%-- =========================================================
     CTA  (guests only)
     ========================================================= --%>
<c:if test="${sessionScope.user == null}">
    <section class="cta-band">
        <h2>Join the conversation. Vote on the next big stock.</h2>
        <p>Free forever. No credit card. Just NEPSE, made readable.</p>
        <a href="${pageContext.request.contextPath}/register"
           class="btn btn-primary btn-lg">Get Started Free →</a>
    </section>
</c:if>

<%-- Scripts --%>
<script src="${pageContext.request.contextPath}/js/chart-bg.js"></script>
<script src="${pageContext.request.contextPath}/js/home-live.js"></script>

<jsp:include page="includes/footer.jsp"/>
