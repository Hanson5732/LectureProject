package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.Room;
import com.estate.lectureproject.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class RoomDao {

    /**
     * 简单的全量查询（带搜索条件）
     * 实际项目中通常需要分页，这里先做基础版
     */
    public List<Room> searchRooms(String keyword, Long areaId) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
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

            // 限制返回数量，防止数据过多
            query.setMaxResults(50);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<Room> findById(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            Room room = em.find(Room.class, id);
            return Optional.ofNullable(room);
        } finally {
            em.close();
        }
    }
}