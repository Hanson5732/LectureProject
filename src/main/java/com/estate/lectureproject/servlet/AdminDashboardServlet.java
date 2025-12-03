package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.OrderDao;
import com.estate.lectureproject.dao.RoomDao;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/stats")
public class AdminDashboardServlet extends HttpServlet {

    @Inject
    private OrderDao orderDao;

    @Inject
    private RoomDao roomDao;

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            Long orderCount = orderDao.countAll();
            Long roomCount = roomDao.countAll();

            Map<String, Long> stats = new HashMap<>();
            stats.put("totalOrders", orderCount);
            stats.put("activeRooms", roomCount);

            resp.getWriter().write(gson.toJson(stats));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching stats");
        }
    }
}