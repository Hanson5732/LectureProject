package com.estate.lectureproject.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "id_card_number", unique = true, nullable = false, length = 50)
    private String idCardNumber;

    @Column(name = "phone_number", unique = true, nullable = false, length = 30)
    private String phoneNumber;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<Order> orders;

    // 枚举定义
    public enum Role {
        USER, ADMIN
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public User() {}

    public User(String username, String password, String fullName, String idCardNumber, String phoneNumber, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.idCardNumber = idCardNumber;
        this.phoneNumber = phoneNumber;
        this.setRole(role); // 使用增强后的 setRole
    }

    // Getters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPasswordHash(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getIdCardNumber() { return idCardNumber; }
    public void setIdCardNumber(String idCardNumber) { this.idCardNumber = idCardNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getRole() {
        return (this.role != null) ? this.role.name() : null;
    }

    /**
     * [修改] 增强的 setRole 方法
     * 1. 支持不区分大小写
     * 2. 将 "agent" 自动映射为 "ADMIN"
     */
    public void setRole(String roleString) {
        if (roleString == null) {
            this.role = Role.USER; // 默认值
            return;
        }

        // 兼容处理：如果是 agent，则视为 admin
        if ("agent".equalsIgnoreCase(roleString) || "admin".equalsIgnoreCase(roleString)) {
            this.role = Role.ADMIN;
        } else {
            try {
                // 尝试转换为大写后匹配 (user -> USER)
                this.role = Role.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 如果都不匹配，默认为 USER，防止报错崩溃
                this.role = Role.USER;
            }
        }
    }
}