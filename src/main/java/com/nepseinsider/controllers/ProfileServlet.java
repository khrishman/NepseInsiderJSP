package com.nepseinsider.controllers;

import com.nepseinsider.model.User;
import com.nepseinsider.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Handles profile view + update + password change for the logged-in user.
 */
@WebServlet(urlPatterns = {"/profile"})
public class ProfileServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User current = (session != null) ? (User) session.getAttribute("user") : null;
        if (current == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        if ("updateProfile".equals(action)) {
            current.setFullName(request.getParameter("fullName"));
            current.setEmail(request.getParameter("email"));
            current.setPhone(request.getParameter("phone"));

            if (userService.updateProfile(current)) {
                session.setAttribute("user", current);
                request.setAttribute("success", "Profile updated successfully");
            } else {
                request.setAttribute("error", "Could not update profile");
            }

        } else if ("changePassword".equals(action)) {
            String oldPwd  = request.getParameter("oldPassword");
            String newPwd  = request.getParameter("newPassword");
            String confirm = request.getParameter("confirmPassword");

            if (newPwd == null || newPwd.length() < 6) {
                request.setAttribute("error", "New password must be at least 6 characters");
            } else if (!newPwd.equals(confirm)) {
                request.setAttribute("error", "Passwords do not match");
            } else if (userService.changePassword(current.getId(), oldPwd, newPwd)) {
                request.setAttribute("success", "Password changed successfully");
            } else {
                request.setAttribute("error", "Current password is incorrect");
            }
        }

        request.getRequestDispatcher("/WEB-INF/pages/profile.jsp").forward(request, response);
    }
}
