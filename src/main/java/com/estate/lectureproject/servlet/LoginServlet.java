package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.UserDao;
import com.estate.lectureproject.entity.User;
import com.estate.lectureproject.utils.PasswordUtil;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Inject
    private UserDao userDao;

    private final Gson gson = new Gson();

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
        session.setAttribute("userId", user.getId());
        session.setAttribute("role", user.getRole());

        session.setMaxInactiveInterval(30 * 60); // 会话 30 分钟后超时

        // 3. 发送成功响应
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        Map<String, String> result = new HashMap<>();
        result.put("message", "Login successful");
        result.put("role", user.getRole());
        resp.getWriter().write(gson.toJson(result));
    }
}