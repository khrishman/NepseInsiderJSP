package com.nepseinsider.controllers;

import com.nepseinsider.model.Stock;
import com.nepseinsider.service.StockService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Displays detailed information for a specific stock.
 */
@WebServlet(urlPatterns = {"/stock-detail"})
public class StockDetailServlet extends HttpServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            Stock stock = stockService.getStockById(id);
            if (stock == null) {
                response.sendRedirect(request.getContextPath() + "/stocks");
                return;
            }
            request.setAttribute("stock", stock);
            request.getRequestDispatcher("/WEB-INF/pages/stock-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/stocks");
        }
    }
}
