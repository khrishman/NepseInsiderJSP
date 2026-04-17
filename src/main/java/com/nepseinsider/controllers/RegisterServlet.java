package com.nepseinsider.controllers;

import com.nepseinsider.model.User;
import com.nepseinsider.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Handles user registration with validation against duplicates.
 */
@WebServlet(urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = safe(request.getParameter("username"));
        String password = safe(request.getParameter("password"));
        String confirm  = safe(request.getParameter("confirmPassword"));
        String email    = safe(request.getParameter("email"));
        String fullName = safe(request.getParameter("fullName"));
        String phone    = safe(request.getParameter("phone"));

        // Basic validation
        String error = validate(username, password, confirm, email, fullName);
        if (error != null) {
            request.setAttribute("error", error);
            preserveInput(request, username, email, fullName, phone);
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        // Duplicate checks (unique identifier verification)
        if (userService.isUsernameTaken(username)) {
            request.setAttribute("error", "Username already taken");
            preserveInput(request, username, email, fullName, phone);
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }
        if (userService.isEmailTaken(email)) {
            request.setAttribute("error", "Email already registered");
            preserveInput(request, username, email, fullName, phone);
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPhone(phone);

        if (userService.register(user)) {
            request.setAttribute("success", "Registration successful! Please login.");
            request.getRequestDispatcher("/WEB-INF/pages/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Something went wrong. Please try again.");
            request.getRequestDispatcher("/WEB-INF/pages/register.jsp").forward(request, response);
        }
    }

    private String validate(String username, String password, String confirm,
                            String email, String fullName) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || fullName.isEmpty())
            return "All required fields must be filled";
        if (username.length() < 3) return "Username must be at least 3 characters";
        if (password.length() < 6) return "Password must be at least 6 characters";
        if (!password.equals(confirm)) return "Passwords do not match";
        if (!email.matches("^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) return "Invalid email format";
        return null;
    }

    private void preserveInput(HttpServletRequest r, String u, String e, String n, String p) {
        r.setAttribute("username", u);
        r.setAttribute("email", e);
        r.setAttribute("fullName", n);
        r.setAttribute("phone", p);
    }

    private String safe(String s) { return s == null ? "" : s.trim(); }
}
