<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage Stocks | Admin" scope="request"/>
<jsp:include page="../includes/header.jsp"/>

<div class="page-header">
    <h1>Manage Stocks</h1>
    <a href="${pageContext.request.contextPath}/admin/stocks?action=new"
       class="btn btn-primary">+ Add New Stock</a>
</div>

<div class="card">
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>Symbol</th><th>Company</th><th>Sector</th>
                    <th>Price (Rs.)</th><th>Volume</th><th>Sentiment</th><th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="s" items="${stocks}">
                    <tr>
                        <td><strong><c:out value="${s.symbol}"/></strong></td>
                        <td><c:out value="${s.companyName}"/></td>
                        <td><c:out value="${s.categoryName}"/></td>
                        <td><fmt:formatNumber value="${s.currentPrice}" pattern="#,##0.00"/></td>
                        <td><fmt:formatNumber value="${s.volume}" pattern="#,##0"/></td>
                        <td>
                            <c:set var="sentClass" value="badge-neutral"/>
                            <c:if test="${s.sentiment == 'BULLISH'}"><c:set var="sentClass" value="badge-bullish"/></c:if>
                            <c:if test="${s.sentiment == 'BEARISH'}"><c:set var="sentClass" value="badge-bearish"/></c:if>
                            <span class="badge ${sentClass}"><c:out value="${s.sentiment}"/></span>
                        </td>
                        <td class="action-cell">
                            <a href="${pageContext.request.contextPath}/admin/stocks?action=edit&id=${s.id}"
                               class="btn btn-sm btn-warning">Edit</a>
                            <a href="${pageContext.request.contextPath}/admin/stocks?action=delete&id=${s.id}"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Delete stock ${s.symbol}?');">Delete</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
