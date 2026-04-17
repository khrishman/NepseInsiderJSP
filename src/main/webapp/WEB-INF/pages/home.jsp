<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Home | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<div class="hero">
    <h1>Welcome to NepseInsider</h1>
    <p>Your gateway to Nepal Stock Exchange (NEPSE) insights. Browse listed companies,
       view prices, and track the market at a glance.</p>
</div>

<div class="stats-grid">
    <div class="stat-card">
        <h3>Listed Stocks</h3>
        <div class="value">${totalStocks}</div>
    </div>
    <div class="stat-card">
        <h3>Market</h3>
        <div class="value">NEPSE</div>
    </div>
    <div class="stat-card">
        <h3>Platform</h3>
        <div class="value">NepseInsider</div>
    </div>
</div>

<div class="card">
    <h2>Latest Stocks</h2>
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Symbol</th>
                    <th>Company</th>
                    <th>Sector</th>
                    <th>Price (Rs.)</th>
                    <th>Change %</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${stocks}" varStatus="st">
                    <c:if test="${st.index < 5}">
                        <tr>
                            <td><strong><c:out value="${s.symbol}"/></strong></td>
                            <td><c:out value="${s.companyName}"/></td>
                            <td><c:out value="${s.categoryName}"/></td>
                            <td><fmt:formatNumber value="${s.currentPrice}" pattern="#,##0.00"/></td>
                            <td class="${s.changePercent >= 0 ? 'change-up' : 'change-down'}">
                                <fmt:formatNumber value="${s.changePercent}" pattern="0.00"/>%
                            </td>
                        </tr>
                    </c:if>
                </c:forEach>
            </tbody>
        </table>
    </div>
    <p style="margin-top:14px;">
        <a href="${pageContext.request.contextPath}/stocks">View all stocks &rarr;</a>
    </p>
</div>

<jsp:include page="includes/footer.jsp"/>
