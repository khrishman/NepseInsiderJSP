package com.nepseinsider.controllers;

import com.nepseinsider.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Admin user management — view all users, activate/suspend accounts.
 */
@WebServlet(urlPatterns = {"/admin/users"})
public class AdminUserServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("updateStatus".equals(action)) {
            try {
                int userId = Integer.parseInt(request.getParameter("id"));
                String status = request.getParameter("status");
                userService.updateUserStatus(userId, status);
            } catch (NumberFormatException ignored) {}
            response.sendRedirect(request.getContextPath() + "/admin/users");
            return;
        }

        request.setAttribute("users", userService.getAllUsers());
        request.getRequestDispatcher("/WEB-INF/pages/admin/user-list.jsp")
               .forward(request, response);
    }
}
