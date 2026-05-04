<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Home | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<%-- =========================================================
     LIVE TICKER TAPE  (auto-populated by home-live.js)
     ========================================================= --%>
<div class="ticker-strip" id="tickerStrip">
    <div class="ticker-track" id="tickerTrack">
        <span class="ticker-loading">⏳ Connecting to NEPSE live feed...</span>
    </div>
</div>

<%-- =========================================================
     HERO  — premium chart background + search + headline
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
            Track <strong id="heroStockCount">346</strong> NEPSE-listed companies with
            live prices, sector trends, community sentiment, and personal watchlists.
            Built for investors who want signal &mdash; not noise.
        </p>

        <%-- Inline search (CoinMarketCap-style) --%>
        <form action="${pageContext.request.contextPath}/stocks" method="get" class="hero-search">
            <span class="hero-search-icon">🔍</span>
            <input type="text" name="q" placeholder="Search stocks by symbol or company name..."
                   autocomplete="off">
            <button type="submit" class="btn btn-primary">Search</button>
        </form>

        <div class="hero-cta">
            <a href="${pageContext.request.contextPath}/stocks"
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
     LIVE MARKET STATS  (powered by home-live.js)
     ========================================================= --%>
<section class="market-stats">
    <div class="stat-tile">
        <div class="stat-label">Listed Companies</div>
        <div class="stat-value" id="statCount">—</div>
        <div class="stat-foot">Tracked in real time</div>
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
        <div class="stat-label">💹 Total Volume</div>
        <div class="stat-value" id="statVolume">—</div>
        <div class="stat-foot">Shares traded today</div>
    </div>
</section>

<%-- =========================================================
     TOP MOVERS — three live columns
     ========================================================= --%>
<section class="movers-grid">

    <div class="mover-card">
        <div class="mover-head">
            <h3>🚀 Top Gainers</h3>
            <span class="badge badge-bullish">LIVE</span>
        </div>
        <table class="mover-table" id="topGainers">
            <tbody>
            <tr><td colspan="3" class="loading-cell">Loading live data...</td></tr>
            </tbody>
        </table>
    </div>

    <div class="mover-card">
        <div class="mover-head">
            <h3>📉 Top Losers</h3>
            <span class="badge badge-bearish">LIVE</span>
        </div>
        <table class="mover-table" id="topLosers">
            <tbody>
            <tr><td colspan="3" class="loading-cell">Loading live data...</td></tr>
            </tbody>
        </table>
    </div>

    <div class="mover-card">
        <div class="mover-head">
            <h3>🔥 Most Traded</h3>
            <span class="badge badge-neutral">BY VOLUME</span>
        </div>
        <table class="mover-table" id="mostTraded">
            <tbody>
            <tr><td colspan="3" class="loading-cell">Loading live data...</td></tr>
            </tbody>
        </table>
    </div>

</section>

<%-- =========================================================
     COMMUNITY PULSE  (placeholder — wired in next phase)
     ========================================================= --%>
<section class="pulse-section">
    <div class="pulse-card">
        <h2 class="section-title-left">💬 Community Pulse</h2>
        <p class="section-sub-left">
            Real-time sentiment from NepseInsider investors.
            <em>Comments &amp; voting coming online soon.</em>
        </p>
        <div class="pulse-stats">
            <div class="pulse-meter">
                <div class="meter-bar">
                    <div class="meter-fill bullish" style="width:62%"></div>
                    <div class="meter-fill bearish" style="width:38%"></div>
                </div>
                <div class="meter-legend">
                    <span><span class="dot bull"></span> 62% Bullish</span>
                    <span><span class="dot bear"></span> 38% Bearish</span>
                </div>
            </div>
            <p class="pulse-note">
                Based on community votes across all NEPSE-listed stocks. Sample data shown
                until the comments system goes live.
            </p>
        </div>
    </div>

    <div class="pulse-card">
        <h2 class="section-title-left">📰 Why NepseInsider</h2>
        <p class="section-sub-left">A focused window into Nepal's stock market.</p>
        <ul class="why-list">
            <li><strong>Live API data</strong> — every price you see is fetched live from NEPSE.</li>
            <li><strong>Built for Nepal</strong> — sectors, symbols, and currency that locals trust.</li>
            <li><strong>No paywalls, no clutter</strong> — clean data, fast pages, zero ads.</li>
            <li><strong>Your data stays yours</strong> — secure sessions, role-based access.</li>
        </ul>
    </div>
</section>

<%-- =========================================================
     FINAL CTA
     ========================================================= --%>
<c:if test="${sessionScope.user == null}">
    <section class="cta-band">
        <h2>Ready to track Nepal's markets like a pro?</h2>
        <p>Free forever. No credit card. Just NEPSE, made readable.</p>
        <a href="${pageContext.request.contextPath}/register"
           class="btn btn-primary btn-lg">Get Started Free →</a>
    </section>
</c:if>

<%-- Animation + Live data scripts --%>
<script src="${pageContext.request.contextPath}/js/chart-bg.js"></script>
<script src="${pageContext.request.contextPath}/js/home-live.js"></script>

<jsp:include page="includes/footer.jsp"/>
