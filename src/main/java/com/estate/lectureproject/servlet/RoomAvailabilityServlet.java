package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.OrderDao;
import com.estate.lectureproject.entity.Order;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/room-availability")
public class RoomAvailabilityServlet extends HttpServlet {

    @Inject
    private OrderDao orderDao;

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String idStr = req.getParameter("roomId");
        if (idStr == null) {
            resp.getWriter().write("[]");
            return;
        }

        try {
            Long roomId = Long.parseLong(idStr);
            List<Order> orders = orderDao.findActiveOrdersByRoom(roomId);

            // 转换为 Flatpickr 能识别的格式: { from: '2023-01-01', to: '2023-01-05' }
            List<Map<String, String>> disableDates = new ArrayList<>();
            for (Order o : orders) {
                Map<String, String> range = new HashMap<>();
                range.put("from", o.getStartDate().toString());
                range.put("to", o.getEndDate().toString());
                disableDates.add(range);
            }

            resp.getWriter().write(gson.toJson(disableDates));

        } catch (Exception e) {
            e.printStackTrace();
            resp.getWriter().write("[]");
        }
    }
}