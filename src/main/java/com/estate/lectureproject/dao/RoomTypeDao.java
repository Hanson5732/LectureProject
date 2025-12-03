package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.RoomType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@RequestScoped
public class RoomTypeDao {
    @PersistenceContext(unitName = "lecture-project")
    private EntityManager em;

    public List<RoomType> findAll() {
        return em.createQuery("SELECT t FROM RoomType t", RoomType.class).getResultList();
    }

    public RoomType findById(Long id) {
        return em.find(RoomType.class, id);
    }
}