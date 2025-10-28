package com.estate.lectureproject.controller;

import com.estate.lectureproject.dao.RefreshTokenDAO;
import com.estate.lectureproject.dao.UserDAO;
import com.estate.lectureproject.entity.RefreshToken;
import com.estate.lectureproject.entity.User;
import com.estate.lectureproject.utils.ConfigUtil;
import com.estate.lectureproject.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/refresh-token") // 确保此路径不被 AuthFilter 拦截
public class RefreshTokenServlet extends HttpServlet {

    private RefreshTokenDAO refreshTokenDAO;
    private UserDAO userDAO;
    private JwtUtil jwtUtil;

    @Override
    public void init() throws ServletException {
        this.refreshTokenDAO = new RefreshTokenDAO();
        this.userDAO = new UserDAO();
        this.jwtUtil = new JwtUtil();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String oldRefreshTokenString = null;

        // 1. 从 Cookie 中获取 Refresh Token (JWT)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    oldRefreshTokenString = cookie.getValue();
                    break;
                }
            }
        }

        if (oldRefreshTokenString == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未找到刷新令牌。");
            return;
        }

        try {
            // 2. [更新] 验证 Refresh Token (JWT)
            Jws<Claims> claims;
            try {
                claims = jwtUtil.validateRefreshTokenAndGetClaims(oldRefreshTokenString);
            } catch (JwtException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "刷新令牌无效或已过期。");
                return;
            }

            // 3. [更新] 从 JWT 中提取 JTI 和 UserID
            String oldJti = claims.getBody().getId();
            long userId = claims.getBody().get("userId", Long.class);

            // 4. [更新] 检查 JTI 是否在数据库中 (防止重放)
            RefreshToken rt = refreshTokenDAO.findToken(oldJti);
            if (rt == null || rt.getUserId() != userId) {
                // 令牌(JTI)不在数据库中，或用户ID不匹配
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "刷新令牌已被吊销或无效。");
                return;
            }

            // 5. [安全实践: 令牌轮换] 立即删除旧的 JTI
            refreshTokenDAO.deleteToken(oldJti);

            // 6. 获取用户信息
            User user = userDAO.findById(userId);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "关联用户不存在。");
                return;
            }

            // 7. 生成新的 Access Token
            String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());

            // 8. [安全实践: 令牌轮换] 生成新的 Refresh Token (JWT 和 JTI)
            JwtUtil.TokenWithJti newRefreshToken = jwtUtil.generateRefreshToken(user.getId());

            // 9. 存储新的 JTI
            refreshTokenDAO.createRefreshToken(user.getId(), newRefreshToken.jti);

            // 10. 设置新的 HttpOnly Cookie
            int maxAgeInSeconds = ConfigUtil.getIntProperty("jwt.refresh.expiration.days", 7) * 24 * 60 * 60;
            Cookie newRefreshCookie = new Cookie("refreshToken", newRefreshToken.tokenString);
            newRefreshCookie.setHttpOnly(true);
            newRefreshCookie.setMaxAge(maxAgeInSeconds);
            newRefreshCookie.setPath("/");
            response.addCookie(newRefreshCookie);

            // 11. 返回新的 Access Token
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"accessToken\": \"" + newAccessToken + "\"}"
            );

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "数据库错误。");
        }
    }
}