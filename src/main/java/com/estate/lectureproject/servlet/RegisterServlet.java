package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.UserDAO;
import com.estate.lectureproject.entity.User;
import com.estate.lectureproject.utils.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private final UserDAO userDao = new UserDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // 1. 从请求中获取表单数据
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String fullName = req.getParameter("fullName");
        String idCardNumber = req.getParameter("idCardNumber");
        String phoneNumber = req.getParameter("phoneNumber");
        String role = req.getParameter("role");

        // [!! 关键修复 !!]
        // 2. 添加服务器端验证，检查 NOT NULL 字段是否为空字符串
        if (username == null || username.trim().isEmpty() ||
                password == null || password.trim().isEmpty() ||
                fullName == null || fullName.trim().isEmpty() ||
                idCardNumber == null || idCardNumber.trim().isEmpty() ||
                phoneNumber == null || phoneNumber.trim().isEmpty()) {

            // 400 Bad Request: 客户端请求无效
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Registration failed (All fields are required)");
            return;
        }

        // 3. 检查用户是否已存在
        if (userDao.existsByUsernameOrIdCardOrPhone(username, idCardNumber, phoneNumber)) {
            // 409 Conflict: 资源冲突
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Registration failed (Username, ID Card, or Phone Number already exists)");
            return;
        }

        // 4. 哈希密码
        String hashedPassword = PasswordUtil.hashPassword(password);

        // 5. 创建 User 对象
        User newUser = new User(username, hashedPassword, fullName, idCardNumber, phoneNumber, role);

        // 6. 保存用户
        userDao.save(newUser);

        // 7. 注册成功
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("{\"message\": \"Registration successful! Please login.\"}");
    }
}