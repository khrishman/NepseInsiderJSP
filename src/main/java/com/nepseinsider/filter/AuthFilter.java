package com.nepseinsider.filter;

import com.nepseinsider.model.User;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Redirect Management Filter.
 * Protects /dashboard, /profile, and /admin/* routes.
 * - If not logged in → redirect to /login.
 * - If non-admin tries /admin/* → redirect to /dashboard.
 */
@WebFilter(urlPatterns = {"/dashboard", "/profile", "/admin/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest)  req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI()
                             .substring(request.getContextPath().length());

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Not logged in — redirect to login
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Admin-only section — block regular users
        if (path.startsWith("/admin/") && !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        chain.doFilter(req, res);
    }
}
