<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Dashboard | NepseInsider" scope="request"/>
<jsp:include page="../includes/header.jsp"/>

<h1>Admin Dashboard</h1>
<p>Welcome, <c:out value="${sessionScope.user.fullName}"/>.</p>

<div class="stats-grid">
    <div class="stat-card">
        <h3>Total Stocks</h3>
        <div class="value">${totalStocks}</div>
    </div>
    <div class="stat-card">
        <h3>Total Users</h3>
        <div class="value">${totalUsers}</div>
    </div>
</div>

<div class="card">
    <h2>Quick Actions</h2>
    <div class="action-links">
        <a href="${pageContext.request.contextPath}/admin/stocks" class="btn btn-primary">Manage Stocks</a>
        <a href="${pageContext.request.contextPath}/admin/stocks?action=new" class="btn btn-primary">Add New Stock</a>
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage Users</a>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
