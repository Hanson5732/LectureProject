package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.AreaDao;
import com.estate.lectureproject.dao.BuildingDao;
import com.estate.lectureproject.dao.RoomTypeDao;
import com.estate.lectureproject.entity.Area;
import com.estate.lectureproject.entity.Building;
import com.estate.lectureproject.entity.RoomType;
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

// 获取后台所需的公共选项数据
@WebServlet("/api/admin/options")
public class AdminDataServlet extends HttpServlet {

    @Inject private AreaDao areaDao;
    @Inject private BuildingDao buildingDao;
    @Inject private RoomTypeDao roomTypeDao;

    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        // 简单封装，避免懒加载问题
        Map<String, Object> data = new HashMap<>();

        data.put("areas", simpleAreaList(areaDao.findAll()));
        data.put("buildings", simpleBuildingList(buildingDao.findAll()));
        data.put("roomTypes", simpleRoomTypeList(roomTypeDao.findAll()));

        resp.getWriter().write(gson.toJson(data));
    }

    // --- 简单的 DTO 转换方法 (防止 JSON 无限递归) ---

    private List<Map<String, Object>> simpleAreaList(List<Area> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Area i : list) {
            result.add(Map.of("id", i.getId(), "name", i.getName()));
        }
        return result;
    }

    private List<Map<String, Object>> simpleBuildingList(List<Building> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Building i : list) {
            // 包含 areaId 方便前端级联选择
            result.add(Map.of("id", i.getId(), "name", i.getName(), "areaId", i.getArea().getId()));
        }
        return result;
    }

    private List<Map<String, Object>> simpleRoomTypeList(List<RoomType> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (RoomType i : list) {
            result.add(Map.of("id", i.getId(), "name", i.getName()));
        }
        return result;
    }
}