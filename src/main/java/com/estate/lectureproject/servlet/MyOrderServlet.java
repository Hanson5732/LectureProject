package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.OrderDao;
import com.estate.lectureproject.dao.UserDao;
import com.estate.lectureproject.entity.Order;
import com.estate.lectureproject.entity.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/orders/my")
public class MyOrderServlet extends HttpServlet {

    @Inject
    private OrderDao orderDao;
    @Inject
    private UserDao userDao;

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // 1. 检查登录
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;
        if (username == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login");
            return;
        }

        // 2. 获取用户 ID
        Optional<User> userOpt = userDao.findByUsername(username);
        if (userOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "User not found");
            return;
        }

        // 3. 查询订单
        List<Order> orders = orderDao.findByUser(userOpt.get().getId());

        // 4. 构建 JSON (简化结构)
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Order o : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("status", o.getStatus()); // UNPAID, PAID...
            map.put("totalAmount", o.getTotalAmount());
            map.put("startDate", o.getStartDate().toString());
            map.put("endDate", o.getEndDate().toString());
            map.put("createdAt", o.getCreatedAt());

            // 房源信息
            if (o.getRoom() != null) {
                map.put("roomTitle", o.getRoom().getTitle());
                map.put("roomImage", o.getRoom().getCoverImage());
            }

            resultList.add(map);
        }

        resp.getWriter().write(gson.toJson(resultList));
    }
}