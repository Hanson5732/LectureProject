package com.estate.lectureproject.servlet;

import com.estate.lectureproject.dao.AreaDao;
import com.estate.lectureproject.entity.Area;
import com.google.gson.Gson;
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

@WebServlet("/api/areas")
public class AreaServlet extends HttpServlet {

    private final AreaDao areaDao = new AreaDao();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 设置响应类型为 JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        List<Area> areas = areaDao.findAll();

        // 转换为简单的 Map 列表，避免 JPA 懒加载问题
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Area area : areas) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", area.getId());
            map.put("name", area.getName());
            map.put("description", area.getDescription());
            // 暂时不计算 roomCount，设为 0 或稍后实现
            map.put("roomCount", 0);
            resultList.add(map);
        }

        // 输出 JSON
        String json = gson.toJson(resultList);
        resp.getWriter().write(json);
    }
}