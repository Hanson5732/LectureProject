package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.RoomDao;
import com.estate.lectureproject.entity.Room;
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
import java.util.Optional;

@WebServlet("/api/room-detail")
public class RoomDetailServlet extends HttpServlet {

    @Inject
    private RoomDao roomDao;

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing room ID");
            return;
        }

        try {
            Long id = Long.parseLong(idStr);
            Optional<Room> roomOpt = roomDao.findById(id);

            if (roomOpt.isPresent()) {
                Room r = roomOpt.get();
                // 转换为 Map 避免 Lazy Loading 问题
                Map<String, Object> map = new HashMap<>();
                map.put("id", r.getId());
                map.put("title", r.getTitle());
                map.put("price", r.getPrice());
                map.put("size", r.getSize());
                map.put("description", r.getDescription());
                map.put("coverImage", r.getCoverImage());

                // 关联数据
                if (r.getArea() != null) map.put("areaName", r.getArea().getName());
                if (r.getBuilding() != null) map.put("buildingName", r.getBuilding().getName());
                if (r.getRoomType() != null) map.put("roomTypeName", r.getRoomType().getName());

                resp.getWriter().write(gson.toJson(map));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Room not found");
            }

        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid room ID format");
        }
    }
}