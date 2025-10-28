package com.estate.lectureproject.controller;

import com.estate.lectureproject.dao.UserDAO;
import com.estate.lectureproject.entity.User;
import com.estate.lectureproject.utils.PasswordUtil;

// Servlet API 基于你的 pom.xml
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        this.userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 1. 获取所有请求参数
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String fullName = request.getParameter("fullName");
        String idCardNumber = request.getParameter("idCardNumber");
        String phoneNumber = request.getParameter("phoneNumber");
        String role = request.getParameter("role");

        // 2. 服务端验证
        // 2a. 检查密码和确认密码是否一致
        if (password == null || !password.equals(confirmPassword)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "The password and the confirmation password do not match.");
            return;
        }

        // 2b. 检查其他字段是否为空
        if (username == null || username.isEmpty() ||
                fullName == null || fullName.isEmpty() ||
                idCardNumber == null || idCardNumber.isEmpty() ||
                phoneNumber == null || phoneNumber.isEmpty()
        ) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "All fields are mandatory.");
            return;
        }

        // 2c. 检查角色是否有效
        if (!"user".equals(role) && !"agent".equals(role)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid user role。");
            return;
        }

        try {
            // 3. 检查用户是否已存在
            if (userDAO.isUserExists(username, idCardNumber, phoneNumber)) {
                response.sendError(HttpServletResponse.SC_CONFLICT, "The username, ID number or phone number has already been registered.");
                return;
            }

            // 4. 密码加密
            String hashedPassword = PasswordUtil.hashPassword(password);

            // 5. 创建用户实体
            User newUser = new User(username, hashedPassword, fullName, idCardNumber, phoneNumber, role);

            // 6. 存入数据库
            boolean success = userDAO.registerUser(newUser);

            if (success) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\": \"Registration success.\"}");
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Registration failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection error.");
        }
    }
}
