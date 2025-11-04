package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.User;
import com.estate.lectureproject.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class UserDAO {

    /**
     * 根据用户名查找用户 (使用 JPA)
     */
    public Optional<User> findByUsername(String username) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            // 使用 JPQL，查询的是实体 "User"，而不是表 "users"
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty(); // 未找到用户
        } finally {
            em.close();
        }
    }

    /**
     * 保存新用户到数据库 (使用 JPA)
     * 注意：此方法现在是 void，它将在失败时抛出异常
     */
    public void save(User user) {
        EntityManager em = JPAUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(user); // "persist" 类似于 "insert"
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback(); // 回滚事务
            }
            // 抛出异常，以便 Servlet 可以捕获它
            throw new RuntimeException("Database error during user save", e);
        } finally {
            em.close();
        }
    }

    /**
     * 检查用户是否已存在 (使用 JPA)
     */
    public boolean existsByUsernameOrIdCardOrPhone(String username, String idCardNumber, String phoneNumber) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            // 使用 JPQL COUNT 查询
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.username = :username " +
                            "OR u.idCardNumber = :idCard " +
                            "OR u.phoneNumber = :phone", Long.class);
            query.setParameter("username", username);
            query.setParameter("idCard", idCardNumber);
            query.setParameter("phone", phoneNumber);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }
}