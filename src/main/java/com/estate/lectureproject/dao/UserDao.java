package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

@RequestScoped // 1. 标记为 CDI Bean
public class UserDao {

    @PersistenceContext(unitName = "lecture-project")
    private EntityManager em;

    public Optional<User> findByUsername(String username) {
        // 3. 直接使用 em，不需要 try-catch-finally 关闭它
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            return Optional.of(query.getSingleResult());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // 4. 写入操作建议加上 @Transactional，或者确保调用它的 Servlet 有事务
    @Transactional(Transactional.TxType.REQUIRED)
    public void save(User user) {
        em.persist(user);
        // 不需要 commit，方法结束自动提交
        // 不需要 close
    }

    public boolean existsByUsernameOrIdCardOrPhone(String username, String idCardNumber, String phoneNumber) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username " +
                        "OR u.idCardNumber = :idCard " +
                        "OR u.phoneNumber = :phone", Long.class);
        query.setParameter("username", username);
        query.setParameter("idCard", idCardNumber);
        query.setParameter("phone", phoneNumber);
        return query.getSingleResult() > 0;
    }
}