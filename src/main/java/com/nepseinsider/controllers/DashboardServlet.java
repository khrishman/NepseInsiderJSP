package com.nepseinsider.controllers;

import com.nepseinsider.service.StockService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * User dashboard — shows personalized welcome and stock overview.
 * Protected by AuthFilter.
 */
@WebServlet(urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("totalStocks", stockService.getTotalStocks());
        request.setAttribute("stocks", stockService.getAllStocks());
        request.getRequestDispatcher("/WEB-INF/pages/dashboard.jsp").forward(request, response);
    }
}
