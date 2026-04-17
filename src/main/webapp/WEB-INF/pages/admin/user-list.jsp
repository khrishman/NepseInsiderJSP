<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage Users | Admin" scope="request"/>
<jsp:include page="../includes/header.jsp"/>

<h1>Manage Users</h1>

<div class="card">
    <div class="table-wrap">
        <table>
            <thead>
                <tr>
                    <th>ID</th><th>Username</th><th>Full Name</th>
                    <th>Email</th><th>Phone</th><th>Role</th><th>Status</th><th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td>${u.id}</td>
                        <td><c:out value="${u.username}"/></td>
                        <td><c:out value="${u.fullName}"/></td>
                        <td><c:out value="${u.email}"/></td>
                        <td><c:out value="${u.phone}"/></td>
                        <td><c:out value="${u.role}"/></td>
                        <td>
                            <span class="badge ${u.status == 'ACTIVE' ? 'badge-bullish' : 'badge-bearish'}">
                                <c:out value="${u.status}"/>
                            </span>
                        </td>
                        <td class="action-cell">
                            <c:if test="${u.role != 'ADMIN'}">
                                <c:choose>
                                    <c:when test="${u.status == 'ACTIVE'}">
                                        <a href="${pageContext.request.contextPath}/admin/users?action=updateStatus&id=${u.id}&status=SUSPENDED"
                                           class="btn btn-sm btn-warning"
                                           onclick="return confirm('Suspend user ${u.username}?');">Suspend</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/admin/users?action=updateStatus&id=${u.id}&status=ACTIVE"
                                           class="btn btn-sm btn-primary">Activate</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../includes/footer.jsp"/>
