package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.AreaDao;
import com.estate.lectureproject.dao.BuildingDao;
import com.estate.lectureproject.dao.RoomDao;
import com.estate.lectureproject.dao.RoomTypeDao;
import com.estate.lectureproject.entity.Room;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/admin/rooms")
public class AdminRoomServlet extends HttpServlet {

    @Inject private RoomDao roomDao;
    @Inject private AreaDao areaDao;
    @Inject private BuildingDao buildingDao;
    @Inject private RoomTypeDao roomTypeDao;

    private final Gson gson = new Gson();

    // 1. 获取列表 (修复死循环问题)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // 确保 RoomDao.searchRooms 已经使用了 JOIN FETCH
        List<Room> rooms = roomDao.searchRooms(null, null);

        // [修复] 手动转换为 Map 列表，避免 Gson 序列化死循环
        List<Map<String, Object>> result = new ArrayList<>();
        for (Room r : rooms) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", r.getId());
            map.put("title", r.getTitle());
            map.put("price", r.getPrice());
            map.put("size", r.getSize());
            map.put("coverImage", r.getCoverImage());
            map.put("description", r.getDescription());

            // 安全地获取关联对象数据，并包含 ID 方便前端“编辑”时回填
            if (r.getArea() != null) {
                map.put("areaName", r.getArea().getName());
                map.put("areaId", r.getArea().getId());
                // 前端需要 area 对象来做级联判断，或者直接传 id 也可以，这里展平传
                Map<String, Object> areaObj = new HashMap<>();
                areaObj.put("id", r.getArea().getId());
                areaObj.put("name", r.getArea().getName());
                map.put("area", areaObj);
            }

            if (r.getBuilding() != null) {
                map.put("buildingName", r.getBuilding().getName());
                map.put("buildingId", r.getBuilding().getId());
                Map<String, Object> bObj = new HashMap<>();
                bObj.put("id", r.getBuilding().getId());
                map.put("building", bObj);
            }

            if (r.getRoomType() != null) {
                map.put("roomTypeName", r.getRoomType().getName());
                map.put("roomTypeId", r.getRoomType().getId());
                Map<String, Object> tObj = new HashMap<>();
                tObj.put("id", r.getRoomType().getId());
                map.put("roomType", tObj);
            }

            result.add(map);
        }

        resp.getWriter().write(gson.toJson(result));
    }

    // 2. 新增房源
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processSave(req, resp, false);
    }

    // 3. 更新房源
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processSave(req, resp, true);
    }

    // 4. 删除房源
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idStr = req.getParameter("id");
        if (idStr != null) {
            roomDao.delete(Long.parseLong(idStr));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing ID");
        }
    }

    // --- 统一处理保存逻辑 ---
    private void processSave(HttpServletRequest req, HttpServletResponse resp, boolean isUpdate) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
        }
        JsonObject json = JsonParser.parseString(sb.toString()).getAsJsonObject();

        Room room = new Room();
        if (isUpdate) {
            // 如果是更新，且没有传 ID，直接报错
            if (!json.has("id") || json.get("id").getAsString().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Update requires ID");
                return;
            }
            long id = json.get("id").getAsLong();
            room = roomDao.findById(id).orElse(null);
            if (room == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Room not found");
                return;
            }
        }

        // 绑定字段
        if (json.has("title")) room.setTitle(json.get("title").getAsString());
        if (json.has("price")) room.setPrice(json.get("price").getAsBigDecimal());
        if (json.has("size")) room.setSize(json.get("size").getAsInt());
        if (json.has("description")) room.setDescription(json.get("description").getAsString());
        if (json.has("coverImage")) room.setCoverImage(json.get("coverImage").getAsString());

        // 绑定关联
        if (json.has("areaId") && !json.get("areaId").getAsString().isEmpty())
            room.setArea(areaDao.findById(json.get("areaId").getAsLong()));

        if (json.has("buildingId") && !json.get("buildingId").getAsString().isEmpty())
            room.setBuilding(buildingDao.findById(json.get("buildingId").getAsLong()));

        if (json.has("roomTypeId") && !json.get("roomTypeId").getAsString().isEmpty())
            room.setRoomType(roomTypeDao.findById(json.get("roomTypeId").getAsLong()));

        if (isUpdate) {
            roomDao.update(room);
        } else {
            roomDao.create(room);
        }

        resp.getWriter().write("{\"message\": \"Saved successfully\"}");
    }
}