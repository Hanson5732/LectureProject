package com.estate.lectureproject.filter;

import com.estate.lectureproject.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// 拦截所有 /api/ 路径下的请求
@WebFilter("/api/*")
public class AuthFilter implements Filter {

    private JwtUtil jwtUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.jwtUtil = new JwtUtil();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. 从请求头获取 Authorization
        String authHeader = httpRequest.getHeader("Authorization");

        // 2. 检查 "Bearer " 令牌
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权: 缺少或无效的Token");
            return;
        }

        // 3. 提取 token 字符串
        String token = authHeader.substring(7); // 移除 "Bearer "

        try {
            // 4. 验证 Token
            Jws<Claims> claims = jwtUtil.validateTokenAndGetClaims(token);

            // 5. [重要] 将用户信息存入 request，以便后续的 Servlet 使用
            request.setAttribute("userId", claims.getBody().get("userId", Long.class));
            request.setAttribute("username", claims.getBody().getSubject());
            request.setAttribute("role", claims.getBody().get("role", String.class));

            // 6. 验证通过，放行请求
            chain.doFilter(request, response);

        } catch (JwtException e) {
            // 验证失败 (例如: 令牌过期, 签名无效)
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权: " + e.getMessage());
        }
    }

    @Override
    public void destroy() {
        // 清理资源 (如果需要)
    }
}