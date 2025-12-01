package com.estate.lectureproject.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "room_types")
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 例如：一居室、Studio

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "roomType", fetch = FetchType.LAZY)
    private List<Room> rooms;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Room> getRooms() { return rooms; }
    public void setRooms(List<Room> rooms) { this.rooms = rooms; }
}