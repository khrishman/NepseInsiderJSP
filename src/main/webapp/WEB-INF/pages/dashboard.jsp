<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<h1>Hello, <c:out value="${sessionScope.user.fullName}"/></h1>
<p style="margin-bottom:20px;">Welcome back to your NepseInsider dashboard.</p>

<div class="stats-grid">
    <div class="stat-card">
        <h3>Total Stocks</h3>
        <div class="value">${totalStocks}</div>
    </div>
    <div class="stat-card">
        <h3>Your Role</h3>
        <div class="value" style="font-size:20px;"><c:out value="${sessionScope.user.role}"/></div>
    </div>
    <div class="stat-card">
        <h3>Watchlist <small>(Phase 2)</small></h3>
        <div class="value">0</div>
    </div>
</div>

<div class="card">
    <h2>All Stocks</h2>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Symbol</th><th>Company</th><th>Sector</th>
                    <th>Price (Rs.)</th><th>Change %</th><th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${stocks}">
                    <tr>
                        <td><strong><c:out value="${s.symbol}"/></strong></td>
                        <td><c:out value="${s.companyName}"/></td>
                        <td><c:out value="${s.categoryName}"/></td>
                        <td><fmt:formatNumber value="${s.currentPrice}" pattern="#,##0.00"/></td>
                        <td class="${s.changePercent >= 0 ? 'change-up' : 'change-down'}">
                            <fmt:formatNumber value="${s.changePercent}" pattern="0.00"/>%
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/stock-detail?id=${s.id}"
                               class="btn btn-sm btn-primary">View</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="includes/footer.jsp"/>
