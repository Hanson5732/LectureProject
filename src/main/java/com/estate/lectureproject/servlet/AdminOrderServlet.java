package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.OrderDao;
import com.estate.lectureproject.entity.Order;
import com.estate.lectureproject.entity.OrderStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/admin/orders")
public class AdminOrderServlet extends HttpServlet {

    @Inject
    private OrderDao orderDao;

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    // 1. 获取所有订单 (保持不变)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Order> orders = orderDao.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Order o : orders) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("totalAmount", o.getTotalAmount());
            map.put("status", o.getStatus());
            map.put("startDate", o.getStartDate() != null ? o.getStartDate().toString() : "");
            map.put("endDate", o.getEndDate() != null ? o.getEndDate().toString() : "");
            map.put("createdAt", o.getCreatedAt());

            if (o.getUser() != null) {
                map.put("username", o.getUser().getUsername());
                map.put("fullName", o.getUser().getFullName());
            } else {
                map.put("username", "Unknown");
            }

            if (o.getRoom() != null) {
                map.put("roomTitle", o.getRoom().getTitle());
            } else {
                map.put("roomTitle", "Room Deleted");
            }

            result.add(map);
        }

        resp.getWriter().write(gson.toJson(result));
    }

    // 2. 更新订单 (增加严格的状态校验)
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();

        if (!json.has("id")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing ID");
            return;
        }

        Long id = json.get("id").getAsLong();
        Order order = orderDao.findById(id);

        if (order == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Order not found");
            return;
        }

        // [关键修改] 状态变更校验
        if (json.has("status")) {
            OrderStatus newStatus;
            try {
                newStatus = OrderStatus.valueOf(json.get("status").getAsString());
            } catch (IllegalArgumentException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid status");
                return;
            }

            // 如果状态确实发生了改变
            if (order.getStatus() != newStatus) {
                // 校验规则：只允许从 REFUND_REQUESTED -> REFUND_COMPLETED
                boolean isRefundProcess = (order.getStatus() == OrderStatus.REFUND_REQUESTED
                        && newStatus == OrderStatus.REFUND_COMPLETED);

                if (isRefundProcess) {
                    order.setStatus(newStatus);
                } else {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
                    resp.getWriter().write("{\"error\": \"Admin can only change status from REFUND_REQUESTED to REFUND_COMPLETED.\"}");
                    return;
                }
            }
        }

        // 允许修改其他非状态字段 (如金额、日期修正)
        if (json.has("totalAmount")) {
            order.setTotalAmount(json.get("totalAmount").getAsBigDecimal());
        }
        if (json.has("startDate")) {
            order.setStartDate(Date.valueOf(json.get("startDate").getAsString()));
        }
        if (json.has("endDate")) {
            order.setEndDate(Date.valueOf(json.get("endDate").getAsString()));
        }

        orderDao.update(order);
        resp.getWriter().write("{\"message\": \"Updated successfully\"}");
    }

    // 3. 删除订单 (保持不变)
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            orderDao.delete(Long.parseLong(idStr));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing ID");
        }
    }
}