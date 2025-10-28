package com.estate.lectureproject.dao;

import com.estate.lectureproject.entity.RefreshToken;
import com.estate.lectureproject.utils.ConfigUtil;
import com.estate.lectureproject.utils.DatabaseUtil;

import java.sql.*;

public class RefreshTokenDAO {

    /**
     * 为用户创建并存储一个新的 Refresh Token
     * (会先删除该用户的所有旧令牌)
     */
    public void createRefreshToken(long userId, String jti) throws SQLException {
        // 1. 删除旧令牌
        deleteTokensByUserId(userId);

        // 2. 计算过期时间 (与JWT的过期时间一致)
        long days = ConfigUtil.getIntProperty("jwt.refresh.expiration.days", 7);
        long expirationMillis = System.currentTimeMillis() + (days * 24 * 60 * 60 * 1000);
        Timestamp expireTime = new Timestamp(expirationMillis);

        // 3. 存入数据库 (token 列存储 jti)
        String sql = "INSERT INTO refresh_tokens (user_id, token, expire_time) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, jti);
            stmt.setTimestamp(3, expireTime);
            stmt.executeUpdate();
        }
    }

    /**
     * 根据令牌字符串查找令牌 (且未过期)
     */
    public RefreshToken findToken(String jti) throws SQLException {
        String sql = "SELECT * FROM refresh_tokens WHERE token = ? AND expire_time > CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, jti);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    RefreshToken rt = new RefreshToken();
                    rt.setId(rs.getLong("id"));
                    rt.setUserId(rs.getLong("user_id"));
                    rt.setToken(rs.getString("token")); // JTI
                    rt.setExpireTime(rs.getTimestamp("expire_time"));
                    return rt;
                }
            }
        }
        return null; // 未找到或已过期
    }

    /**
     * 删除一个具体的令牌 (用于令牌轮换)
     */
    public void deleteToken(String jti) throws SQLException {
        String sql = "DELETE FROM refresh_tokens WHERE token = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jti);
            stmt.executeUpdate();
        }
    }

    /**
     * 删除指定用户的所有刷新令牌 (用于登出或重新登录)
     */
    public void deleteTokensByUserId(long userId) throws SQLException {
        String sql = "DELETE FROM refresh_tokens WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        }
    }
}