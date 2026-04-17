<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%-- Welcome file — redirects to home page via HomeServlet --%>
<% response.sendRedirect(request.getContextPath() + "/home"); %>
