package com.example.connectDB;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class ConnectDB {
    private static ConnectDB instance = new ConnectDB();
    private static SessionFactory sessionFactory;

    public static ConnectDB getInstance() {
        return instance;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            instance.connectHibernate();
        }
        return sessionFactory;
    }

    public void connectHibernate() {
        if (sessionFactory == null) {
            try {
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .configure() // configures settings from hibernate.cfg.xml
                        .build();
                sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
                System.out.println("✅ Kết nối Hibernate thành công!");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("❌ Lỗi kết nối Hibernate!");
            }
        }
    }

    public void disconnect() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            System.out.println("🔌 Đã đóng SessionFactory.");
        }
    }
}