package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.estate.lectureproject.utils.DatabaseUtil;

public class UserDAO {

    /**
     * 检查用户名、身份证或电话是否已经存在
     *
     * @param username
     * @param idCard
     * @param phone
     * @return
     * @throws SQLException
     */
    public boolean isUserExists(String username, String idCard, String phone) throws SQLException {
        String sql = "SELECT COUNT(id) FROM users WHERE username = ? OR id_card_number = ? OR phone_number = ?";

        // try-with-resources 自动关闭连接和语句
        try (Connection conn = DatabaseUtil.getConnection(); // 你需要自己实现这个方法
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, idCard);
            stmt.setString(3, phone);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // 如果计数 > 0，则表示已存在
                }
            }
        }
        return false;
    }

    /**
     * 注册新用户
     *
     * @param user
     * @return
     * @throws SQLException
     */
    public boolean registerUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, id_card_number, phone_number, role) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getIdCardNumber());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getRole());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * 根据用户名查找用户
     *
     * @param username
     * @return
     * @throws SQLException
     */
    public User findByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * 根据ID查找用户
     *
     * @param id
     * @return
     * @throws SQLException
     */
    public User findById(long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * 将 ResultSet 映射到 User 对象
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("id_card_number"),
                rs.getString("phone_number"),
                rs.getString("role")
        );
        user.setId(rs.getLong("id"));
        return user;
    }
}
