<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Stocks | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<div class="card">
    <h2>All Listed Stocks</h2>

    <!-- Search Bar -->
    <form action="${pageContext.request.contextPath}/stocks" method="get" class="search-bar">
        <input type="text" name="keyword"
               placeholder="Search by symbol or company name..."
               value="<c:out value='${keyword}'/>">
        <button type="submit" class="btn btn-primary">Search</button>
        <c:if test="${not empty keyword}">
            <a href="${pageContext.request.contextPath}/stocks" class="btn btn-secondary">Clear</a>
        </c:if>
    </form>

    <c:choose>
        <c:when test="${empty stocks}">
            <p>No stocks found.</p>
        </c:when>
        <c:otherwise>
            <div class="table-wrap">
                <table>
                    <thead>
                        <tr>
                            <th>Symbol</th><th>Company</th><th>Sector</th>
                            <th>Price (Rs.)</th><th>Change %</th><th>Volume</th><th>Action</th>
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
                                <td><fmt:formatNumber value="${s.volume}" pattern="#,##0"/></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/stock-detail?id=${s.id}"
                                       class="btn btn-sm btn-primary">View</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="includes/footer.jsp"/>
