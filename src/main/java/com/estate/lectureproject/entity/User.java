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

    // 更新 Enum
    public enum Role {
        USER, ADMIN // 对应数据库 ENUM('USER','ADMIN')
    }

    // 确保 role 字段使用新的 Enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // JPA 需要一个无参数的构造函数
    public User() {}

    // 您现有的构造函数
    public User(String username, String password, String fullName, String idCardNumber, String phoneNumber, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.idCardNumber = idCardNumber;
        this.phoneNumber = phoneNumber;
        this.setRole(role);
    }


    // --- Getters 和 Setters (重要修改) ---

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPasswordHash(String password) { this.password = password; } // 保持 servlet 兼容
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getIdCardNumber() { return idCardNumber; }
    public void setIdCardNumber(String idCardNumber) { this.idCardNumber = idCardNumber; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    // [!! 修改 !!] Getter 和 Setter 以处理 Enum 和 String 之间的转换
    public String getRole() {
        return (this.role != null) ? this.role.name() : null;
    }
    public void setRole(String roleString) {
        if (roleString != null) {
            this.role = Role.valueOf(roleString);
        }
    }
}