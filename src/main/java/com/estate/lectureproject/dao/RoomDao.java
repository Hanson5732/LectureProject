package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.Room;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@RequestScoped
public class RoomDao {

    @PersistenceContext(unitName = "lecture-project")
    private EntityManager em;

    /**
     * 简单的全量查询（带搜索条件）
     */
    public List<Room> searchRooms(String keyword, Long areaId) {
        StringBuilder jpql = new StringBuilder("SELECT r FROM Room r WHERE 1=1");

        // 动态拼接 SQL
        if (areaId != null) {
            jpql.append(" AND r.area.id = :areaId");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            jpql.append(" AND (r.title LIKE :keyword OR r.building.name LIKE :keyword)");
        }

        // 按时间倒序排列
        jpql.append(" ORDER BY r.createdAt DESC");

        TypedQuery<Room> query = em.createQuery(jpql.toString(), Room.class);

        // 设置参数
        if (areaId != null) {
            query.setParameter("areaId", areaId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.setParameter("keyword", "%" + keyword + "%");
        }

        // 限制返回数量
        query.setMaxResults(50);

        return query.getResultList();
    }

    public Optional<Room> findById(Long id) {
        Room room = em.find(Room.class, id);
        return Optional.ofNullable(room);
    }
}