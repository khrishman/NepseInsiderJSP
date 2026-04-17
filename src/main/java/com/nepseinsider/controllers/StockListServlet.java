package com.nepseinsider.controllers;

import com.nepseinsider.model.Stock;
import com.nepseinsider.service.StockService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Public stock listing + search.
 * Accessible to all — no login required.
 */
@WebServlet(urlPatterns = {"/stocks"})
public class StockListServlet extends HttpServlet {

    private final StockService stockService = new StockService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");

        List<Stock> stocks;
        if (keyword != null && !keyword.trim().isEmpty()) {
            stocks = stockService.searchStocks(keyword.trim());
            request.setAttribute("keyword", keyword);
        } else {
            stocks = stockService.getAllStocks();
        }

        request.setAttribute("stocks", stocks);
        request.getRequestDispatcher("/WEB-INF/pages/stocks.jsp").forward(request, response);
    }
}
