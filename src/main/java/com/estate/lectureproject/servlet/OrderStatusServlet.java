package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.OrderDao;
import com.estate.lectureproject.entity.OrderStatus;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@WebServlet("/api/orders/action")
public class OrderStatusServlet extends HttpServlet {

    @Inject
    private OrderDao orderDao;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // 读取 JSON: { "orderId": 1, "action": "pay" }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();

        Long orderId = json.get("orderId").getAsLong();
        String action = json.get("action").getAsString(); // "pay", "confirm", "refund"

        // 简单的状态机逻辑
        OrderStatus newStatus = null;
        switch (action.toLowerCase()) {
            case "pay":
                newStatus = OrderStatus.PAID;
                break;
            case "confirm":
                newStatus = OrderStatus.CONFIRMED;
                break;
            case "refund":
                newStatus = OrderStatus.REFUND_REQUESTED;
                break;
            case "cancel":
                newStatus = OrderStatus.CANCELLED;
                break;
            default:
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
                return;
        }

        // 更新数据库
        orderDao.updateStatus(orderId, newStatus);

        resp.getWriter().write(new Gson().toJson(Map.of("message", "Success", "status", newStatus)));
    }
}