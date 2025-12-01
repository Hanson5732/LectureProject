package com.estate.lectureproject.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "areas")
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    // 关联关系：一个地区有多个楼盘
    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Building> buildings;

    // 关联关系：一个地区有多个房源 (可选，通过 Building 关联也可以，但直接关联方便查询)
    @OneToMany(mappedBy = "area", fetch = FetchType.LAZY)
    private List<Room> rooms;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Building> getBuildings() { return buildings; }
    public void setBuildings(List<Building> buildings) { this.buildings = buildings; }
    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
}