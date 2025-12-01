package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.Area;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@RequestScoped
public class AreaDao {

    @PersistenceContext(unitName = "lecture-project")
    private EntityManager em;

    public List<Area> findAll() {
        // 容器会自动管理 EntityManager 的开启和关闭
        return em.createQuery("SELECT a FROM Area a", Area.class).getResultList();
    }

    // 根据ID查找
    public Area findById(Long id) {
        return em.find(Area.class, id);
    }
}