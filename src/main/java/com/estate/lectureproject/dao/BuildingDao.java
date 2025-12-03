package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.Building;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@RequestScoped
public class BuildingDao {
    @PersistenceContext(unitName = "lecture-project")
    private EntityManager em;

    public List<Building> findAll() {
        return em.createQuery("SELECT b FROM Building b", Building.class).getResultList();
    }

    public Building findById(Long id) {
        return em.find(Building.class, id);
    }
}