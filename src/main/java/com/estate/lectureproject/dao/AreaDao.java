package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.Area;
import com.estate.lectureproject.utils.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class AreaDao {

    public List<Area> findAll() {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            // 查询所有地区
            return em.createQuery("SELECT a FROM Area a", Area.class).getResultList();
        } finally {
            em.close();
        }
    }

    // 根据ID查找（备用）
    public Area findById(Long id) {
        EntityManager em = JPAUtil.createEntityManager();
        try {
            return em.find(Area.class, id);
        } finally {
            em.close();
        }
    }
}