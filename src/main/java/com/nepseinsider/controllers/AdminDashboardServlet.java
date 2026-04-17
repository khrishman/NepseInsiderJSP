package com.nepseinsider.controllers;

import com.nepseinsider.service.StockService;
import com.nepseinsider.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Admin dashboard with system stats.
 * Protected by AuthFilter (admin-only).
 */
@WebServlet(urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    private final StockService stockService = new StockService();
    private final UserService  userService  = new UserService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("totalStocks", stockService.getTotalStocks());
        request.setAttribute("totalUsers",  userService.getAllUsers().size());
        request.getRequestDispatcher("/WEB-INF/pages/admin/dashboard.jsp").forward(request, response);
    }
}
