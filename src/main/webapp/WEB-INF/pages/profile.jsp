<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Profile | NepseInsider" scope="request"/>
<jsp:include page="includes/header.jsp"/>

<h1>My Profile</h1>

<c:if test="${not empty error}">
    <div class="alert alert-error"><c:out value="${error}"/></div>
</c:if>
<c:if test="${not empty success}">
    <div class="alert alert-success"><c:out value="${success}"/></div>
</c:if>

<div class="card" style="max-width:560px;">
    <h2>Update Profile</h2>
    <form action="${pageContext.request.contextPath}/profile" method="post">
        <input type="hidden" name="action" value="updateProfile">
        <div class="form-group">
            <label>Username (read-only)</label>
            <input type="text" value="<c:out value='${sessionScope.user.username}'/>" disabled>
        </div>
        <div class="form-group">
            <label for="fullName">Full Name</label>
            <input type="text" id="fullName" name="fullName"
                   value="<c:out value='${sessionScope.user.fullName}'/>" required>
        </div>
        <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" name="email"
                   value="<c:out value='${sessionScope.user.email}'/>" required>
        </div>
        <div class="form-group">
            <label for="phone">Phone</label>
            <input type="text" id="phone" name="phone"
                   value="<c:out value='${sessionScope.user.phone}'/>">
        </div>
        <button type="submit" class="btn btn-primary">Save Changes</button>
    </form>
</div>

<div class="card" style="max-width:560px;">
    <h2>Change Password</h2>
    <form action="${pageContext.request.contextPath}/profile" method="post">
        <input type="hidden" name="action" value="changePassword">
        <div class="form-group">
            <label for="oldPassword">Current Password</label>
            <input type="password" id="oldPassword" name="oldPassword" required>
        </div>
        <div class="form-group">
            <label for="newPassword">New Password (min 6 characters)</label>
            <input type="password" id="newPassword" name="newPassword" required>
        </div>
        <div class="form-group">
            <label for="confirmPassword">Confirm New Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
        </div>
        <button type="submit" class="btn btn-primary">Change Password</button>
    </form>
</div>

<jsp:include page="includes/footer.jsp"/>
