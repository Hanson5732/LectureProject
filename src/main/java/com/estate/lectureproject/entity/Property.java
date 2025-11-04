package com.estate.lectureproject.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
public class Property {

    /**
     * 房产图片列表。
     * 这将被 JPA 映射到一个名为 "property_images" 的独立表中。
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "property_images",
            joinColumns = @JoinColumn(name = "property_id")
    )
    @Column(name = "image_url", length = 512, nullable = false)
    private List<String> imageUrls = new ArrayList<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal area;

    @Lob // 映射到 TEXT 类型
    @Column
    private String description;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PropertyType type;

    // 拥有者关系 (多对一)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // 使用者关系 (多对一)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 默认为 nullable = true
    private User user;

    // 自动管理的创建时间
    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp createdAt;

    // 自动管理的更新时间
    @Column(name = "updated_at", nullable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Timestamp updatedAt;

    @PrePersist // 在 INSERT 之前自动设置创建时间
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate // 在 UPDATE 之前自动设置更新时间
    protected void onUpdate() {
        updatedAt = new Timestamp(System.currentTimeMillis());
    }


    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public PropertyType getType() { return type; }
    public void setType(PropertyType type) { this.type = type; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}