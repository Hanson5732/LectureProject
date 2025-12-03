package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.Order;
import com.estate.lectureproject.entity.OrderStatus;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.sql.Date;
import java.util.Arrays;
import java.util.List;

@RequestScoped
public class OrderDao {

    @PersistenceContext(unitName = "lecture-project")
    private EntityManager em;

    @Transactional
    public void save(Order order) {
        em.persist(order);
    }

    public Order findById(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findByUser(Long userId) {
        return em.createQuery(
                        "SELECT o FROM Order o LEFT JOIN FETCH o.room WHERE o.user.id = :userId ORDER BY o.createdAt DESC",
                        Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Order> findAll() {
        return em.createQuery(
                        "SELECT o FROM Order o LEFT JOIN FETCH o.user LEFT JOIN FETCH o.room ORDER BY o.createdAt DESC",
                        Order.class)
                .getResultList();
    }

    @Transactional
    public void update(Order order) {
        em.merge(order);
    }

    @Transactional
    public void updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = em.find(Order.class, orderId);
        if (order != null) {
            order.setStatus(newStatus);
        }
    }

    @Transactional
    public void delete(Long id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            em.remove(order);
        }
    }

    /**
     * [修改] 优化防重逻辑
     * 判定逻辑：(NewStart < OldEnd) AND (NewEnd > OldStart)
     * 只要满足上述条件，说明时间段有交集。
     */
    public boolean isRoomAvailable(Long roomId, Date newStart, Date newEnd) {
        List<OrderStatus> occupiedStatuses = Arrays.asList(
                OrderStatus.UNPAID,
                OrderStatus.PAID,
                OrderStatus.CONFIRMED
        );

        Long count = em.createQuery(
                        "SELECT COUNT(o) FROM Order o " +
                                "WHERE o.room.id = :roomId " +
                                "AND o.status IN :statuses " +
                                "AND (o.endDate > :newStart AND o.startDate < :newEnd)", Long.class)
                .setParameter("roomId", roomId)
                .setParameter("statuses", occupiedStatuses)
                .setParameter("newStart", newStart)
                .setParameter("newEnd", newEnd)
                .getSingleResult();

        return count == 0;
    }

    /**
     * 查询某房源所有未来的有效订单（用于前端日历禁用日期）
     */
    public List<Order> findActiveOrdersByRoom(Long roomId) {
        List<OrderStatus> activeStatuses = Arrays.asList(
                OrderStatus.UNPAID,
                OrderStatus.PAID,
                OrderStatus.CONFIRMED
        );

        return em.createQuery(
                        "SELECT o FROM Order o " +
                                "WHERE o.room.id = :roomId " +
                                "AND o.status IN :statuses " +
                                "AND o.endDate >= CURRENT_DATE", Order.class) // 只查未来的
                .setParameter("roomId", roomId)
                .setParameter("statuses", activeStatuses)
                .getResultList();
    }

    public Long countAll() {
        return em.createQuery("SELECT COUNT(o) FROM Order o", Long.class).getSingleResult();
    }
}