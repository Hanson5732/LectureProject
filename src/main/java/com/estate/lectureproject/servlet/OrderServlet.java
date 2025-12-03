package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.OrderDao;
import com.estate.lectureproject.dao.RoomDao;
import com.estate.lectureproject.dao.UserDao;
import com.estate.lectureproject.entity.Order;
import com.estate.lectureproject.entity.OrderStatus;
import com.estate.lectureproject.entity.Room;
import com.estate.lectureproject.entity.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebServlet("/api/orders")
public class OrderServlet extends HttpServlet {

    @Inject
    private OrderDao orderDao;
    @Inject
    private RoomDao roomDao;
    @Inject
    private UserDao userDao;

    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // 1. 检查登录状态
        HttpSession session = req.getSession(false);
        String username = (session != null) ? (String) session.getAttribute("username") : null;

        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"message\": \"Please login first.\"}");
            return;
        }

        // 2. 解析请求体
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        JsonObject jsonBody = JsonParser.parseString(sb.toString()).getAsJsonObject();

        Long roomId = jsonBody.get("roomId").getAsLong();
        String startDateStr = jsonBody.get("startDate").getAsString();
        int durationMonths = jsonBody.get("duration").getAsInt(); // 获取月数
        int guests = jsonBody.has("guests") ? jsonBody.get("guests").getAsInt() : 1; // 获取人数

        // [验证] 人数限制
        if (guests > 4) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Max 4 guests allowed.");
            return;
        }
        if (durationMonths < 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Duration must be at least 1 month.");
            return;
        }

        // 3. 计算日期和金额
        LocalDate start = LocalDate.parse(startDateStr);
        LocalDate end = start.plusMonths(durationMonths); // 自动计算结束日期
        Date sqlStart = Date.valueOf(start);
        Date sqlEnd = Date.valueOf(end);

        // [验证] 检查时间冲突
        if (!orderDao.isRoomAvailable(roomId, sqlStart, sqlEnd)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
            resp.getWriter().write("{\"message\": \"Room is already booked for these dates.\"}");
            return;
        }

        // 4. 获取关联实体
        Optional<User> userOpt = userDao.findByUsername(username);
        Optional<Room> roomOpt = roomDao.findById(roomId);

        if (userOpt.isEmpty() || roomOpt.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid User or Room");
            return;
        }

        User user = userOpt.get();
        Room room = roomOpt.get();

        // 5. 计算总金额 (单价 * 月数)
        BigDecimal totalAmount = room.getPrice().multiply(new BigDecimal(durationMonths));

        // 6. 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setRoom(room);
        order.setStartDate(sqlStart);
        order.setEndDate(sqlEnd);
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.UNPAID);
        order.setGuestCount(guests); // 设置人数

        // 7. 保存到数据库
        orderDao.save(order);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "Order created successfully");
        result.put("orderId", order.getId());
        result.put("totalAmount", totalAmount);
        result.put("endDate", end.toString());

        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(gson.toJson(result));
    }
}