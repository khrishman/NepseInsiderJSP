<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${symbol} | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<c:choose>

    <c:when test="${not empty notFoundSymbol}">
        <section class="card" style="text-align:center; padding:48px;">
            <h1>Stock not found</h1>
            <p style="color:#6c7886;">
                No live data for <strong><c:out value="${notFoundSymbol}"/></strong>.
                It may not be listed on NEPSE, or the API may be temporarily unavailable.
            </p>
            <a href="${pageContext.request.contextPath}/live-stocks" class="btn btn-primary"
               style="margin-top:16px;">&larr; Back to Live Stocks</a>
        </section>
    </c:when>

    <c:otherwise>

        <%-- HEADER CARD --%>
        <section class="card" style="margin-bottom:16px;">
            <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:16px;">
                <div>
                    <a href="${pageContext.request.contextPath}/live-stocks"
                       class="btn btn-secondary btn-sm" style="margin-bottom:14px;">&larr; Back to Live Stocks</a>
                    <h1 style="margin:0; font-size:32px;"><c:out value="${stock.symbol}"/></h1>
                    <p style="color:#6c7886; margin:6px 0 0; font-size:14px;">
                        Live data from NEPSE &middot; auto-refreshing
                    </p>
                </div>
                <div style="text-align:right;">
                    <c:set var="pc" value="${stock['percent_change']}"/>
                    <c:set var="pcUp" value="${pc >= 0}"/>
                    <div style="font-size:32px; font-weight:800; font-variant-numeric:tabular-nums;
                            color: ${pcUp ? '#16a34a' : '#dc2626'};">
                        Rs. <fmt:formatNumber value="${stock['ltp']}" pattern="#,##0.00"/>
                    </div>
                    <div style="font-size:15px; font-weight:700; color: ${pcUp ? '#16a34a' : '#dc2626'};">
                            ${pcUp ? '&#9650;' : '&#9660;'}
                        <fmt:formatNumber value="${pc}" pattern="+0.00;-0.00"/>%
                    </div>
                </div>
            </div>
        </section>

        <%-- KPI ROW --%>
        <section style="display:grid; grid-template-columns:repeat(auto-fit, minmax(180px, 1fr));
                gap:14px; margin-bottom:20px;">
            <div class="stat-tile">
                <div class="stat-label">OPEN</div>
                <div class="stat-value" style="font-size:24px;">
                    <fmt:formatNumber value="${stock['open']}" pattern="#,##0.00"/>
                </div>
                <div class="stat-foot">Today's opening price</div>
            </div>
            <div class="stat-tile stat-up">
                <div class="stat-label">HIGH</div>
                <div class="stat-value" style="font-size:24px;">
                    <fmt:formatNumber value="${stock['high']}" pattern="#,##0.00"/>
                </div>
                <div class="stat-foot">Day high</div>
            </div>
            <div class="stat-tile stat-down">
                <div class="stat-label">LOW</div>
                <div class="stat-value" style="font-size:24px;">
                    <fmt:formatNumber value="${stock['low']}" pattern="#,##0.00"/>
                </div>
                <div class="stat-foot">Day low</div>
            </div>
            <div class="stat-tile">
                <div class="stat-label">VOLUME</div>
                <div class="stat-value" style="font-size:24px;">
                    <fmt:formatNumber value="${stock['volume']}" pattern="#,##0"/>
                </div>
                <div class="stat-foot">Shares traded today</div>
            </div>
        </section>

        <%-- VOTING + COMMENTS --%>
        <section style="display:grid; grid-template-columns:1fr 1fr; gap:16px; margin-bottom:24px;"
                 class="detail-grid">

            <div class="vote-widget">
                <h3>What do you think about <c:out value="${symbol}"/>?</h3>
                <p>Cast your sentiment. One vote per user; you can change your mind any time.</p>

                <c:choose>
                    <c:when test="${sessionScope.user == null}">
                        <div class="comment-login-prompt">
                            <a href="${pageContext.request.contextPath}/login">Log in</a>
                            to vote on this stock.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="vote-buttons">
                            <button type="button" class="vote-btn bullish" id="voteBull"
                                    onclick="vote('BULLISH')">&#9650; BULLISH</button>
                            <button type="button" class="vote-btn bearish" id="voteBear"
                                    onclick="vote('BEARISH')">&#9660; BEARISH</button>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="meter-bar">
                    <div class="meter-fill bullish" id="voteBullFill" style="width:50%"></div>
                    <div class="meter-fill bearish" id="voteBearFill" style="width:50%"></div>
                </div>
                <div class="meter-legend">
                    <span><span class="dot bull"></span> <span id="voteBullLbl">50% Bullish</span></span>
                    <span><span class="dot bear"></span> <span id="voteBearLbl">50% Bearish</span></span>
                </div>
                <p class="vote-result" id="voteMeta">Loading sentiment...</p>
            </div>

            <div class="comments-card">
                <div class="comments-head">
                    <h3>Discussion</h3>
                    <span class="comments-count" id="commentsCount">- comments</span>
                </div>

                <c:choose>
                    <c:when test="${sessionScope.user == null}">
                        <div class="comment-login-prompt">
                            <a href="${pageContext.request.contextPath}/login">Log in</a>
                            to join the discussion.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="comment-form">
                    <textarea id="commentText"
                              placeholder="Share your view..."
                              maxlength="1000"></textarea>
                            <button type="button" class="btn btn-primary" onclick="postComment()">Post</button>
                        </div>
                        <div id="commentError"
                             style="display:none; color:#dc2626; font-size:13px; margin-bottom:10px;"></div>
                    </c:otherwise>
                </c:choose>

                <ul class="comment-list" id="commentList">
                    <li class="comment-empty">Loading comments...</li>
                </ul>
            </div>
        </section>

        <script type="text/javascript">
            window.NEPSE_CTX = "${pageContext.request.contextPath}";
        </script>
        <script type="text/javascript">
            (function () {
                "use strict";

                var SYMBOL = "";
                var match  = window.location.search.match(/[?&]symbol=([^&]+)/);
                if (match) SYMBOL = decodeURIComponent(match[1]).toUpperCase();

                var CTX = window.NEPSE_CTX || "";

                window.SYMBOL = SYMBOL;
                window.CTX    = CTX;

                function $(id) { return document.getElementById(id); }
                function escapeHtml(s) {
                    return String(s).replace(/[&<>"']/g, function (c) {
                        var m = { "&":"&amp;", "<":"&lt;", ">":"&gt;", '"':"&quot;", "'":"&#39;" };
                        return m[c];
                    });
                }
                function timeAgo(iso) {
                    if (!iso) return "";
                    var d = new Date(String(iso).replace(" ", "T"));
                    if (isNaN(d.getTime())) return iso;
                    var s = Math.floor((Date.now() - d.getTime()) / 1000);
                    if (s < 60)    return s + "s ago";
                    if (s < 3600)  return Math.floor(s/60) + "m ago";
                    if (s < 86400) return Math.floor(s/3600) + "h ago";
                    return Math.floor(s/86400) + "d ago";
                }

                // POST helper — sends as application/x-www-form-urlencoded so the
                // Java Servlet can read parameters via request.getParameter().
                // (FormData would send multipart/form-data which Servlets don't parse
                // by default unless annotated with @MultipartConfig.)
                function postForm(url, params) {
                    var body = new URLSearchParams();
                    Object.keys(params).forEach(function (k) {
                        body.append(k, params[k]);
                    });
                    return fetch(url, {
                        method: "POST",
                        headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
                        body: body.toString()
                    });
                }

                function refreshSentiment() {
                    if (!SYMBOL) return;
                    fetch(CTX + "/sentiment?symbol=" + encodeURIComponent(SYMBOL),
                        { cache: "no-store" })
                        .then(function (r) { return r.json(); })
                        .then(function (d) {
                            if (!d || d.error) return;
                            var bullPct = +d.bullishPct || 50;
                            var bearPct = +d.bearishPct || 50;
                            $("voteBullFill").style.width = bullPct + "%";
                            $("voteBearFill").style.width = bearPct + "%";
                            $("voteBullLbl").textContent = bullPct.toFixed(0) + "% Bullish";
                            $("voteBearLbl").textContent = bearPct.toFixed(0) + "% Bearish";
                            var total = d.total || 0;
                            $("voteMeta").textContent = total === 0
                                ? "No votes yet - be the first."
                                : "Based on " + total + " vote" + (total === 1 ? "" : "s") + ".";
                            var bull = $("voteBull"), bear = $("voteBear");
                            if (bull) bull.classList.toggle("active", d.myVote === "BULLISH");
                            if (bear) bear.classList.toggle("active", d.myVote === "BEARISH");
                        })
                        .catch(function () {});
                }

                window.vote = function (side) {
                    if (!SYMBOL) { alert("Stock symbol missing from URL."); return; }
                    postForm(CTX + "/sentiment", { symbol: SYMBOL, vote: side })
                        .then(function (r) { return r.json(); })
                        .then(function (d) {
                            if (d.error) { alert(d.error); return; }
                            refreshSentiment();
                        })
                        .catch(function () { alert("Could not record vote."); });
                };

                function refreshComments() {
                    if (!SYMBOL) return;
                    fetch(CTX + "/comments?symbol=" + encodeURIComponent(SYMBOL) + "&limit=50",
                        { cache: "no-store" })
                        .then(function (r) { return r.json(); })
                        .then(function (d) {
                            var list = (d && d.comments) || [];
                            $("commentsCount").textContent =
                                list.length + " comment" + (list.length === 1 ? "" : "s");
                            var ul = $("commentList");
                            if (list.length === 0) {
                                ul.innerHTML =
                                    '<li class="comment-empty">No comments yet. Start the conversation!</li>';
                                return;
                            }
                            ul.innerHTML = list.map(function (c) {
                                return '<li class="comment-item">' +
                                    '<div class="comment-meta">' +
                                    '<span class="comment-author">@' + escapeHtml(c.username) + '</span>' +
                                    '<span class="comment-time">' + timeAgo(c.createdAt) + '</span>' +
                                    '</div>' +
                                    '<div class="comment-body">' + escapeHtml(c.text) + '</div>' +
                                    '</li>';
                            }).join("");
                        })
                        .catch(function () {
                            $("commentList").innerHTML =
                                '<li class="comment-empty">Could not load comments.</li>';
                        });
                }

                window.postComment = function () {
                    var ta = $("commentText");
                    var err = $("commentError");
                    var text = (ta.value || "").trim();
                    err.style.display = "none";

                    if (!SYMBOL)            { err.textContent = "Stock symbol missing from URL."; err.style.display = "block"; return; }
                    if (!text)              { err.textContent = "Comment cannot be empty.";       err.style.display = "block"; return; }
                    if (text.length > 1000) { err.textContent = "Comment too long (max 1000).";   err.style.display = "block"; return; }

                    postForm(CTX + "/comments", { symbol: SYMBOL, text: text })
                        .then(function (r) { return r.json(); })
                        .then(function (d) {
                            if (d.error) { err.textContent = d.error; err.style.display = "block"; return; }
                            ta.value = "";
                            refreshComments();
                        })
                        .catch(function () {
                            err.textContent = "Could not post comment.";
                            err.style.display = "block";
                        });
                };

                refreshSentiment();
                refreshComments();
                setInterval(refreshSentiment, 15000);
                setInterval(refreshComments,  20000);
            })();
        </script>

        <style>
            @media (max-width: 800px) {
                .detail-grid { grid-template-columns: 1fr !important; }
            }
        </style>

    </c:otherwise>
</c:choose>

<jsp:include page="includes/footer.jsp"/>
