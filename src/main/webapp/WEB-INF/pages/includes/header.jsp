<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle != null ? pageTitle : 'NepseInsider'}"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/home" class="logo">NepseInsider</a>
    <ul>
        <li><a href="${pageContext.request.contextPath}/home">Home</a></li>
        <li><a href="${pageContext.request.contextPath}/stocks">Stocks</a></li>
        <c:choose>
            <c:when test="${sessionScope.user == null}">
                <li><a href="${pageContext.request.contextPath}/login">Login</a></li>
                <li><a href="${pageContext.request.contextPath}/register">Register</a></li>
            </c:when>
            <c:otherwise>
                <c:if test="${sessionScope.user.admin}">
                    <li><a href="${pageContext.request.contextPath}/admin/dashboard">Admin Panel</a></li>
                </c:if>
                <c:if test="${!sessionScope.user.admin}">
                    <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                </c:if>
                <li><a href="${pageContext.request.contextPath}/profile">Profile</a></li>
                <li><a href="${pageContext.request.contextPath}/logout">Logout (<c:out value="${sessionScope.user.username}"/>)</a></li>
            </c:otherwise>
        </c:choose>
    </ul>
</nav>
<div class="container">
