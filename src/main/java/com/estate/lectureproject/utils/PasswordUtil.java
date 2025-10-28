package com.estate.lectureproject.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * 将明文密码哈希化 (自动生成盐)
     * @param plainTextPassword 明文密码
     * @return BCrypt 哈希值
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * 校验密码
     * @param plainTextPassword 用户输入的明文密码
     * @param storedHash 数据库中存储的哈希值
     * @return true 如果匹配
     */
    public static boolean checkPassword(String plainTextPassword, String storedHash) {
        return BCrypt.checkpw(plainTextPassword, storedHash);
    }
}
