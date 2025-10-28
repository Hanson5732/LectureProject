package com.estate.lectureproject.controller;

import com.estate.lectureproject.dao.UserDAO;
import com.estate.lectureproject.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

/**
 * 受保护的 API 端点，用于获取当前登录用户的信息。
 * AuthFilter 会在 doGet 之前运行。
 */
@WebServlet("/api/profile")
public class ProfileServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        // [cite: hanson5732/lectureproject/LectureProject-0efab5c5dd076978425e1382cdb23f0b336a53bf/src/main/java/com/estate/lectureproject/dao/UserDAO.java]
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            // 1. 从 AuthFilter 获取 userId
            // AuthFilter 在验证 JWT 后将 userId 存入了 request 属性
            Long userId = (Long) request.getAttribute("userId");

            if (userId == null) {
                // 这种情况理论上不应发生，因为 AuthFilter 会拦截
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权：过滤器未能提供 userId。");
                return;
            }

            // 2. 使用 userId 从数据库获取完整的用户信息
            // 我们在 UserDAO 中添加了 findById 方法
            User user = userDAO.findById(userId);

            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户在数据库中不存在，但 JWT 有效。");
                return;
            }

            // 3. 将用户信息作为 JSON 返回
            // [安全] 永远不要将 user.getPassword() [cite: hanson5732/lectureproject/LectureProject-0efab5c5dd076978425e1382cdb23f0b336a53bf/src/main/java/com/estate/lectureproject/entity/User.java] (即 password_hash) 发送给前端
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            String jsonResponse = String.format(
                    "{\"id\": %d, \"username\": \"%s\", \"fullName\": \"%s\", \"phoneNumber\": \"%s\", \"idCardNumber\": \"%s\", \"role\": \"%s\"}",
                    user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getPhoneNumber(),
                    user.getIdCardNumber(),
                    user.getRole()
            );

            response.getWriter().write(jsonResponse);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "获取用户信息时发生数据库错误。");
        }
    }
}
