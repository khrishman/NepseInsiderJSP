<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<div class="card form-card" style="max-width:520px;">
    <h2>Create an Account</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error"><c:out value="${error}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
        <div class="form-group">
            <label for="fullName">Full Name *</label>
            <input type="text" id="fullName" name="fullName"
                   value="<c:out value='${fullName}'/>" required>
        </div>
        <div class="form-group">
            <label for="username">Username *</label>
            <input type="text" id="username" name="username"
                   value="<c:out value='${username}'/>" required>
        </div>
        <div class="form-group">
            <label for="email">Email *</label>
            <input type="email" id="email" name="email"
                   value="<c:out value='${email}'/>" required>
        </div>
        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone"
                   value="<c:out value='${phone}'/>">
        </div>
        <div class="form-group">
            <label for="password">Password * (min 6 characters)</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm Password *</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
        </div>
        <button type="submit" class="btn btn-primary btn-block">Register</button>
    </form>
    <p class="form-footer">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login">Login here</a>
    </p>
</div>

<jsp:include page="includes/footer.jsp"/>
