package com.estate.lectureproject.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final EntityManagerFactory emf;

    static {
        try {
            // "lecture-project-pu" 必须匹配 persistence.xml 中的 name
            emf = Persistence.createEntityManagerFactory("lecture-project-pu");
        } catch (Throwable ex) {
            System.err.println("JPA EntityManagerFactory 创建失败。" + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * 为每个数据库操作创建一个新的 EntityManager
     */
    public static EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    /**
     * (可选) 在应用关闭时调用
     */
    public static void close() {
        emf.close();
    }
}