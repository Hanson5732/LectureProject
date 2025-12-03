package com.estate.lectureproject.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// 拦截所有后台页面和后台 API
@WebFilter(urlPatterns = {"/admin/*", "/api/admin/*"})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // 1. 检查是否登录
        if (session == null || session.getAttribute("username") == null) {
            // 如果是 API 请求，返回 401 JSON
            if (req.getRequestURI().startsWith(req.getContextPath() + "/api/")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\": \"Unauthorized\"}");
            } else {
                // 如果是页面请求，重定向到登录页
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            }
            return;
        }

        // 2. 检查是否为管理员
        String role = (String) session.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            // 权限不足 (403 Forbidden)
            if (req.getRequestURI().startsWith(req.getContextPath() + "/api/")) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Admin only");
            } else {
                resp.getWriter().write("<h1>403 Access Denied</h1><p>You do not have permission to access this page.</p><a href='index.jsp'>Go Home</a>");
            }
            return;
        }

        // 3. 验证通过，放行
        chain.doFilter(request, response);
    }
}