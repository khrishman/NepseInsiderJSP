<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="${stock.symbol} | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<a href="${pageContext.request.contextPath}/stocks" class="btn btn-secondary btn-sm">&larr; Back to Stocks</a>

<div class="card" style="margin-top:16px;">
    <h1><c:out value="${stock.symbol}"/> &mdash; <c:out value="${stock.companyName}"/></h1>
    <p><strong>Sector:</strong> <c:out value="${stock.categoryName}"/></p>
    <p><strong>Listed:</strong> <c:out value="${stock.yearListed}"/></p>

    <c:set var="sentClass" value="badge-neutral"/>
    <c:if test="${stock.sentiment == 'BULLISH'}"><c:set var="sentClass" value="badge-bullish"/></c:if>
    <c:if test="${stock.sentiment == 'BEARISH'}"><c:set var="sentClass" value="badge-bearish"/></c:if>
    <p style="margin-top:8px;">
        <span class="badge ${sentClass}"><c:out value="${stock.sentiment}"/></span>
    </p>
</div>

<div class="stats-grid">
    <div class="stat-card">
        <h3>Current Price</h3>
        <div class="value">Rs. <fmt:formatNumber value="${stock.currentPrice}" pattern="#,##0.00"/></div>
    </div>
    <div class="stat-card">
        <h3>Previous Price</h3>
        <div class="value">Rs. <fmt:formatNumber value="${stock.previousPrice}" pattern="#,##0.00"/></div>
    </div>
    <div class="stat-card">
        <h3>Change</h3>
        <div class="value ${stock.changePercent >= 0 ? 'change-up' : 'change-down'}">
            <fmt:formatNumber value="${stock.changePercent}" pattern="0.00"/>%
        </div>
    </div>
    <div class="stat-card">
        <h3>Volume</h3>
        <div class="value"><fmt:formatNumber value="${stock.volume}" pattern="#,##0"/></div>
    </div>
    <div class="stat-card">
        <h3>Market Cap</h3>
        <div class="value">Rs. <fmt:formatNumber value="${stock.marketCap}" pattern="#,##0"/></div>
    </div>
</div>

<c:if test="${not empty stock.description}">
    <div class="card">
        <h2>About</h2>
        <p><c:out value="${stock.description}"/></p>
    </div>
</c:if>

<jsp:include page="includes/footer.jsp"/>
