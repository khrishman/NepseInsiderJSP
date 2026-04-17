<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<div class="card form-card">
    <h2>Login</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>
    <c:if test="${not empty success}">
        <div class="alert alert-success"><c:out value="${success}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label for="username">Username</label>
            <input type="text" id="username" name="username" required autofocus>
        </div>
        <div class="form-group">
            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn btn-primary btn-block">Login</button>
    </form>
    <p class="form-footer">
        Don't have an account?
        <a href="${pageContext.request.contextPath}/register">Register here</a>
    </p>
</div>

<jsp:include page="includes/footer.jsp"/>
