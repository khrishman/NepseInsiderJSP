package com.nepseinsider.controllers;

import com.nepseinsider.model.Stock;
import com.nepseinsider.service.CategoryService;
import com.nepseinsider.service.StockService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Admin stock management — full CRUD operations.
 * GET: list / new form / edit form / delete
 * POST: create / update
 */
@WebServlet(urlPatterns = {"/admin/stocks"})
public class AdminStockServlet extends HttpServlet {

    private final StockService stockService = new StockService();
    private final CategoryService categoryService = new CategoryService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "new":
                request.setAttribute("categories", categoryService.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/pages/admin/stock-form.jsp")
                       .forward(request, response);
                break;

            case "edit":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Stock stock = stockService.getStockById(id);
                    if (stock == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/stocks");
                        return;
                    }
                    request.setAttribute("stock", stock);
                    request.setAttribute("categories", categoryService.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/pages/admin/stock-form.jsp")
                           .forward(request, response);
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/stocks");
                }
                break;

            case "delete":
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    stockService.deleteStock(id);
                } catch (NumberFormatException ignored) {}
                response.sendRedirect(request.getContextPath() + "/admin/stocks");
                break;

            default: // list
                request.setAttribute("stocks", stockService.getAllStocks());
                request.getRequestDispatcher("/WEB-INF/pages/admin/stock-list.jsp")
                       .forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        try {
            Stock stock = new Stock();
            String idStr = request.getParameter("id");
            if (idStr != null && !idStr.isEmpty()) stock.setId(Integer.parseInt(idStr));

            stock.setSymbol(request.getParameter("symbol").trim().toUpperCase());
            stock.setCompanyName(request.getParameter("companyName").trim());
            stock.setCategoryId(Integer.parseInt(request.getParameter("categoryId")));
            stock.setCurrentPrice(parseDouble(request.getParameter("currentPrice")));
            stock.setPreviousPrice(parseDouble(request.getParameter("previousPrice")));
            stock.setMarketCap(parseDouble(request.getParameter("marketCap")));
            stock.setVolume(parseLong(request.getParameter("volume")));
            stock.setYearListed(Integer.parseInt(request.getParameter("yearListed")));
            stock.setSentiment(request.getParameter("sentiment"));
            stock.setDescription(request.getParameter("description"));

            boolean success;
            if ("create".equals(action)) {
                // Duplicate symbol check
                if (stockService.isSymbolTaken(stock.getSymbol())) {
                    request.setAttribute("error", "Stock symbol already exists");
                    request.setAttribute("stock", stock);
                    request.setAttribute("categories", categoryService.getAllCategories());
                    request.getRequestDispatcher("/WEB-INF/pages/admin/stock-form.jsp")
                           .forward(request, response);
                    return;
                }
                success = stockService.addStock(stock);
            } else {
                success = stockService.updateStock(stock);
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/stocks");
            } else {
                request.setAttribute("error", "Something went wrong. Please try again.");
                request.setAttribute("stock", stock);
                request.setAttribute("categories", categoryService.getAllCategories());
                request.getRequestDispatcher("/WEB-INF/pages/admin/stock-form.jsp")
                       .forward(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Please enter valid numbers for numeric fields");
            request.setAttribute("categories", categoryService.getAllCategories());
            request.getRequestDispatcher("/WEB-INF/pages/admin/stock-form.jsp")
                   .forward(request, response);
        }
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    private long parseLong(String s) {
        try { return Long.parseLong(s); } catch (Exception e) { return 0L; }
    }
}
