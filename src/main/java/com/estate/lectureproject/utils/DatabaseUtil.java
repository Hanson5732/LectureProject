package com.estate.lectureproject.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Connection getConnection() throws SQLException {
        // 每次调用时，都从 ConfigUtil 读取最新的配置值
        return DriverManager.getConnection(
                ConfigUtil.getProperty("db.url"),
                ConfigUtil.getProperty("db.username"),
                ConfigUtil.getProperty("db.password")
        );
    }
}
