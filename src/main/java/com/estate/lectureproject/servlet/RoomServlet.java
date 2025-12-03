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
import java.sql.Date;
import java.time.LocalDate;
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
        String keyword = req.getParameter("keyword"); // 顶部的搜索框
        String areaKeyword = req.getParameter("areaKeyword"); // 首页表单的城市
        String checkInDateStr = req.getParameter("checkInDate");
        String searchType = req.getParameter("searchType");

        Date sqlCheckIn = null;
        Date sqlCheckOut = null;

        // 2. 计算日期范围 (如果有 Check-in)
        if (checkInDateStr != null && !checkInDateStr.isEmpty()) {
            try {
                LocalDate start = LocalDate.parse(checkInDateStr);
                LocalDate end;

                // 根据类型决定租期
                if ("short".equalsIgnoreCase(searchType)) {
                    end = start.plusMonths(1);
                } else {
                    end = start.plusMonths(2); // 默认2个月
                }

                sqlCheckIn = Date.valueOf(start);
                sqlCheckOut = Date.valueOf(end);
            } catch (Exception e) {
                // 日期格式错误，忽略日期过滤
            }
        }

        // 3. 调用 DAO (支持 关键字 + 城市模糊 + 日期空闲检查)
        List<Room> rooms = roomDao.searchRoomsAdvanced(keyword, areaKeyword, sqlCheckIn, sqlCheckOut);

        // 4. 转换为 JSON Map
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Room r : rooms) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("title", r.getTitle());
            map.put("price", r.getPrice());
            map.put("size", r.getSize());
            map.put("coverImage", r.getCoverImage());

            if (r.getArea() != null) map.put("areaName", r.getArea().getName());
            if (r.getBuilding() != null) map.put("buildingName", r.getBuilding().getName());
            if (r.getRoomType() != null) map.put("roomTypeName", r.getRoomType().getName());

            resultList.add(map);
        }

        resp.getWriter().write(gson.toJson(resultList));
    }
}