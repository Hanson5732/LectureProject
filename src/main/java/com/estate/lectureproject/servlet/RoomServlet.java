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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/rooms")
public class RoomServlet extends HttpServlet {

    @Inject
    private RoomDao roomDao;

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // 1. 获取查询参数
        String keyword = req.getParameter("keyword");
        String areaIdStr = req.getParameter("areaId");
        Long areaId = null;
        if (areaIdStr != null && !areaIdStr.isEmpty()) {
            try {
                areaId = Long.parseLong(areaIdStr);
            } catch (NumberFormatException e) {
                // 忽略错误的 ID 格式
            }
        }

        // 2. 查询数据库
        List<Room> rooms = roomDao.searchRooms(keyword, areaId);

        // 3. 转换为 JSON 友好的 Map 结构
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Room r : rooms) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("title", r.getTitle());
            map.put("price", r.getPrice());
            map.put("size", r.getSize());
            map.put("coverImage", r.getCoverImage());

            // 处理关联对象，防止空指针
            if (r.getArea() != null) {
                map.put("areaName", r.getArea().getName());
            }
            if (r.getBuilding() != null) {
                map.put("buildingName", r.getBuilding().getName());
            }
            if (r.getRoomType() != null) {
                map.put("roomTypeName", r.getRoomType().getName());
            }

            resultList.add(map);
        }

        // 4. 返回 JSON
        resp.getWriter().write(gson.toJson(resultList));
    }
}