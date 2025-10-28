package com.estate.lectureproject.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private static final Properties properties = new Properties();

    // 静态代码块，在类加载时执行一次
    static {
        // 从 classpath 读取 config.properties
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {

            if (input == null) {
                System.err.println("错误: 无法找到 config.properties 文件");
                // 在实际项目中，这里应该抛出异常或导致程序启动失败
            }

            // 加载配置
            properties.load(input);

        } catch (IOException e) {
            e.printStackTrace();
            // 处理加载异常
        }
    }

    /**
     * 根据键获取配置值
     * @param key 配置的键名
     * @return 配置的值 (String)
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * 获取整型配置值
     */
    public static int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}