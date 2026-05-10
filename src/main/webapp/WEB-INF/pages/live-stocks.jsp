<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Live Stocks | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<section class="card">
    <div class="page-header">
        <div>
            <h1>📡 Live NEPSE Stocks</h1>
            <p style="color:#6c7886; font-size:14px; margin:0;">
                Real-time prices fetched from NEPSE.
                <c:if test="${not empty query}">
                    Filtering by <strong><c:out value="${query}"/></strong> &mdash;
                    <a href="${pageContext.request.contextPath}/live-stocks">clear</a>
                </c:if>
                <c:if test="${empty query}">
                    Showing <strong>${totalCount}</strong> listings, refreshed every 30 s.
                </c:if>
            </p>
        </div>
    </div>

    <form method="get" action="${pageContext.request.contextPath}/live-stocks" class="search-bar">
        <input type="text" name="q"
               placeholder="Search by symbol (e.g. NABIL, NHPC, API)…"
               value="<c:out value='${query}'/>"
               autofocus>
        <button type="submit" class="btn btn-primary">Search</button>
    </form>

    <c:choose>
        <c:when test="${empty stocks}">
            <p style="text-align:center; color:#94a0b0; padding:40px; font-style:italic;">
                <c:choose>
                    <c:when test="${not empty query}">
                        No stocks matching <strong><c:out value="${query}"/></strong>.
                    </c:when>
                    <c:otherwise>
                        Live data is loading. Refresh the page in a moment.
                    </c:otherwise>
                </c:choose>
            </p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                    <tr>
                        <th>Symbol</th>
                        <th style="text-align:right;">Price (Rs.)</th>
                        <th style="text-align:right;">Open</th>
                        <th style="text-align:right;">High</th>
                        <th style="text-align:right;">Low</th>
                        <th style="text-align:right;">Change %</th>
                        <th style="text-align:right;">Volume</th>
                        <th style="text-align:center;">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="s" items="${stocks}">
                        <tr>
                            <td><strong><c:out value="${s.symbol}"/></strong></td>
                            <td style="text-align:right; font-variant-numeric:tabular-nums;">
                                <fmt:formatNumber value="${s.ltp}" pattern="#,##0.00"/>
                            </td>
                            <td style="text-align:right; color:#6c7886;">
                                <fmt:formatNumber value="${s.open}" pattern="#,##0.00"/>
                            </td>
                            <td style="text-align:right; color:#16a34a;">
                                <fmt:formatNumber value="${s.high}" pattern="#,##0.00"/>
                            </td>
                            <td style="text-align:right; color:#dc2626;">
                                <fmt:formatNumber value="${s.low}" pattern="#,##0.00"/>
                            </td>
                            <td style="text-align:right;"
                                class="${s.percent_change >= 0 ? 'change-up' : 'change-down'}">
                                <fmt:formatNumber value="${s.percent_change}" pattern="+0.00;-0.00"/>%
                            </td>
                            <td style="text-align:right; color:#6c7886;">
                                <fmt:formatNumber value="${s.volume}" pattern="#,##0"/>
                            </td>
                            <td style="text-align:center;">
                                <a class="btn btn-primary btn-sm"
                                   href="${pageContext.request.contextPath}/live-stock?symbol=${s.symbol}">
                                    View →
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<jsp:include page="includes/footer.jsp"/>
