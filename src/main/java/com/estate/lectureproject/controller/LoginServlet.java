package com.estate.lectureproject.controller;

import com.estate.lectureproject.dao.RefreshTokenDAO;
import com.estate.lectureproject.dao.UserDAO;
import com.estate.lectureproject.entity.User;
import com.estate.lectureproject.utils.ConfigUtil;
import com.estate.lectureproject.utils.JwtUtil;
import com.estate.lectureproject.utils.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserDAO userDAO;
    private RefreshTokenDAO refreshTokenDAO;
    private JwtUtil jwtUtil;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
        this.refreshTokenDAO = new RefreshTokenDAO();
        this.jwtUtil = new JwtUtil();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // ... (省略用户名密码非空检查) ...

        try {
            // 1. 验证用户
            User user = userDAO.findByUsername(username);
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户名或密码无效。");
                return;
            }

            // 2. 验证密码 (使用你 User.java 中的 getPassword())
            boolean passwordMatch = PasswordUtil.checkPassword(password, user.getPassword());
            if (!passwordMatch) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户名或密码无效。");
                return;
            }

            // 3. 生成 Access Token
            String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());

            // 4. [更新] 生成 Refresh Token (JWT)
            JwtUtil.TokenWithJti refreshToken = jwtUtil.generateRefreshToken(user.getId());

            // 5. [更新] 将 Refresh Token 的 JTI 存入数据库
            refreshTokenDAO.createRefreshToken(user.getId(), refreshToken.jti);

            // 6. 将 Refresh Token (JWT 字符串) 存入 HttpOnly Cookie
            int maxAgeInSeconds = ConfigUtil.getIntProperty("jwt.refresh.expiration.days", 7) * 24 * 60 * 60;

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken.tokenString);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setMaxAge(maxAgeInSeconds);
            refreshCookie.setPath("/");
            response.addCookie(refreshCookie);

            // 7. 返回 Access Token
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"accessToken\": \"" + accessToken + "\", " +
                            "\"message\": \"登录成功\", " +
                            "\"role\": \"" + user.getRole() + "\"}"
            );

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "数据库错误。");
        }
    }
}