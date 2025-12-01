package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.UserDao;
import com.estate.lectureproject.entity.User;
import com.estate.lectureproject.utils.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        Optional<User> userOptional = userDao.findByUsername(username);

        if (userOptional.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login failed (Invalid username or password)");
            return;
        }

        User user = userOptional.get();

        if (!PasswordUtil.checkPassword(password, user.getPassword())) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Login failed (Invalid username or password)");
            return;
        }

        // 1. 创建或获取 HttpSession
        HttpSession session = req.getSession(true); // true = 如果不存在，则创建一个新会话

        // 2. 将用户信息存储在会话中
        session.setAttribute("username", user.getUsername());
        session.setAttribute("fullName", user.getFullName());
        session.setMaxInactiveInterval(30 * 60); // 会话 30 分钟后超时

        // 3. 发送成功响应
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        // 服务器现在会自动将会话 ID (JSESSIONID) 作为 cookie 发送给浏览器
        resp.getWriter().write("{\"message\": \"Login successful\"}");
    }
}