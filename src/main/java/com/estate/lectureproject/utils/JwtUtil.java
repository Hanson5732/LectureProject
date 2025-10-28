package com.estate.lectureproject.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final Key secretKey;
    private static final long accessTokenValidityInMilliseconds;

    static {
        // 从配置文件加载密钥
        String secretString = ConfigUtil.getProperty("jwt.secret.key");
        secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));

        // 从配置文件加载过期时间 (毫秒)
        long minutes = ConfigUtil.getIntProperty("jwt.access.expiration.minutes", 15);
        accessTokenValidityInMilliseconds = minutes * 60 * 1000;
    }

    /**
     * 生成 Access Token (JWT)
     * @param userId 用户ID
     * @param username 用户名
     * @param role 角色
     * @return JWT 字符串
     */
    public String generateAccessToken(long userId, String username, String role) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date validity = new Date(nowMillis + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                // 1. 设置 "subject" (主题)，通常是用户名
                .setSubject(username)
                // 2. 添加 "claims" (自定义信息)，存储ID和角色
                .claim("userId", userId)
                .claim("role", role)
                // 3. 设置签发时间
                .setIssuedAt(now)
                // 4. 设置过期时间
                .setExpiration(validity)
                // 5. 使用HS256算法和我们的密钥签名
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


}