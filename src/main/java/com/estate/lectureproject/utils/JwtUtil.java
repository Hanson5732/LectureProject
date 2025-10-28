package com.estate.lectureproject.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final Key accessSecretKey;
    private static final long accessTokenValidityInMilliseconds;

    private static final Key refreshSecretKey;
    private static final long refreshTokenValidityInMilliseconds;

    /**
     * 用于返回Token和JTI
     */
    public static class TokenWithJti {
        public final String tokenString;
        public final String jti; // JWT ID

        public TokenWithJti(String tokenString, String jti) {
            this.tokenString = tokenString;
            this.jti = jti;
        }
    }

    static {
        // 1. 加载 Access Token 配置
        String accessSecretString = ConfigUtil.getProperty("jwt.secret.key");
        accessSecretKey = Keys.hmacShaKeyFor(accessSecretString.getBytes(StandardCharsets.UTF_8));
        long accessMinutes = ConfigUtil.getIntProperty("jwt.access.expiration.minutes", 15);
        accessTokenValidityInMilliseconds = accessMinutes * 60 * 1000;

        // 2. 加载 Refresh Token 配置
        String refreshSecretString = ConfigUtil.getProperty("jwt.refresh.secret.key");
        refreshSecretKey = Keys.hmacShaKeyFor(refreshSecretString.getBytes(StandardCharsets.UTF_8));
        long refreshDays = ConfigUtil.getIntProperty("jwt.refresh.expiration.days", 7);
        refreshTokenValidityInMilliseconds = refreshDays * 24 * 60 * 60 * 1000;
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
                .signWith(accessSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 验证 Access Token 并获取 Claims
     *
     * @param token
     * @return
     * @throws JwtException
     */
    public Jws<Claims> validateTokenAndGetClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * 生成 Refresh Token
     *
     * @param userId
     * @return
     */
    public TokenWithJti generateRefreshToken(long userId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date validity = new Date(nowMillis + refreshTokenValidityInMilliseconds);

        // JTI (JWT ID) 是我们将存储在数据库中的唯一标识符
        String jti = UUID.randomUUID().toString();

        String tokenString = Jwts.builder()
                .claim("userId", userId)
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(refreshSecretKey, SignatureAlgorithm.HS256)
                .compact();

        return new TokenWithJti(tokenString, jti);
    }

    /**
     * 验证 Access Token
     *
     * @param token
     * @return
     * @throws JwtException
     */
    public Jws<Claims> validateAccessTokenAndGetClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(accessSecretKey)
                .build()
                .parseClaimsJws(token);
    }

    /**
     * 验证 Refresh Token
     *
     * @param token
     * @return
     * @throws JwtException
     */
    public Jws<Claims> validateRefreshTokenAndGetClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(refreshSecretKey)
                .build()
                .parseClaimsJws(token);
    }
}