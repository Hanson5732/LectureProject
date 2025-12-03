package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.Room;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import jakarta.transaction.Transactional;

@RequestScoped
public class RoomDao {

    @PersistenceContext(unitName = "lecture-project")
    private EntityManager em;

    /**
     * 简单的全量查询（带搜索条件）
     */
    public List<Room> searchRooms(String keyword, Long areaId) {
        // 使用 LEFT JOIN FETCH 一次性查出关联对象
        StringBuilder jpql = new StringBuilder(
                "SELECT r FROM Room r " +
                        "LEFT JOIN FETCH r.area " +
                        "LEFT JOIN FETCH r.building " +
                        "LEFT JOIN FETCH r.roomType " +
                        "WHERE 1=1");

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
        try {
            TypedQuery<Room> query = em.createQuery(
                    "SELECT r FROM Room r " +
                            "LEFT JOIN FETCH r.area " +
                            "LEFT JOIN FETCH r.building " +
                            "LEFT JOIN FETCH r.roomType " +
                            "WHERE r.id = :id", Room.class);

            query.setParameter("id", id);

            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            // 如果查不到数据，getSingleResult 会抛异常，这里捕获并返回空
            return Optional.empty();
        }
    }

    @Transactional
    public void create(Room room) {
        em.persist(room);
    }

    @Transactional
    public void update(Room room) {
        em.merge(room);
    }

    @Transactional
    public void delete(Long id) {
        Room room = em.find(Room.class, id);
        if (room != null) {
            em.remove(room);
        }
    }

    /**
     * 高级搜索：支持城市模糊匹配 + 关键词 + 房源空闲检查
     */
    public List<Room> searchRoomsAdvanced(String globalKeyword, String areaKeyword, java.sql.Date startDate, java.sql.Date endDate) {
        StringBuilder jpql = new StringBuilder(
                "SELECT r FROM Room r " +
                        "LEFT JOIN FETCH r.area " +
                        "LEFT JOIN FETCH r.building " +
                        "LEFT JOIN FETCH r.roomType " +
                        "WHERE 1=1 ");

        // 1. 全局关键字 (Title 或 BuildingName)
        if (globalKeyword != null && !globalKeyword.trim().isEmpty()) {
            jpql.append(" AND (r.title LIKE :globalKeyword OR r.building.name LIKE :globalKeyword)");
        }

        // 2. 城市/地区关键字 (模糊匹配 Area Name)
        if (areaKeyword != null && !areaKeyword.trim().isEmpty()) {
            jpql.append(" AND (r.area.name LIKE :areaKeyword)");
        }

        // 3. 空闲检查 (如果有日期)
        // 排除掉：在 [startDate, endDate] 区间内 存在 有效订单(UNPAID, PAID, CONFIRMED) 的房间
        if (startDate != null && endDate != null) {
            jpql.append(" AND NOT EXISTS ( " +
                    "   SELECT o FROM Order o " +
                    "   WHERE o.room = r " +
                    "   AND o.status IN ('UNPAID', 'PAID', 'CONFIRMED') " +
                    "   AND o.startDate < :endDate " +
                    "   AND o.endDate > :startDate " +
                    ")");
        }

        jpql.append(" ORDER BY r.createdAt DESC");

        TypedQuery<Room> query = em.createQuery(jpql.toString(), Room.class);

        // 设置参数
        if (globalKeyword != null && !globalKeyword.trim().isEmpty()) {
            query.setParameter("globalKeyword", "%" + globalKeyword + "%");
        }
        if (areaKeyword != null && !areaKeyword.trim().isEmpty()) {
            query.setParameter("areaKeyword", "%" + areaKeyword + "%");
        }
        if (startDate != null && endDate != null) {
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
        }

        query.setMaxResults(50);
        return query.getResultList();
    }

    public Long countAll() {
        return em.createQuery("SELECT COUNT(r) FROM Room r", Long.class).getSingleResult();
    }
}