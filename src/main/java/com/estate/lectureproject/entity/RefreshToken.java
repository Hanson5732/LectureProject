package com.estate.lectureproject.entity;

import java.sql.Timestamp;

public class RefreshToken {
    private long id;
    private long userId;
    private String token;
    private Timestamp expireTime;


    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Timestamp getExpireTime() { return expireTime; }
    public void setExpireTime(Timestamp expireTime) { this.expireTime = expireTime; }
}