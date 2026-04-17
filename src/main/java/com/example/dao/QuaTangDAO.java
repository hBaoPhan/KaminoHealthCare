package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.KhuyenMai;
import com.example.entity.QuaTang;
import com.example.entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuaTangDAO {

    public List<QuaTang> layTatCa() {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.createQuery("from QuaTang", QuaTang.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<QuaTang> timTheoKhuyenMai(String maKM) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            String hql = "from QuaTang q where q.khuyenMai.maKhuyenMai = :maKM";
            return session.createQuery(hql, QuaTang.class)
                          .setParameter("maKM", maKM)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean them(QuaTang qt) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(qt);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String maKM, String maSP) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            com.example.entity.QuaTangId id = new com.example.entity.QuaTangId(maKM, maSP);
            QuaTang qt = session.get(QuaTang.class, id);
            if (qt != null) {
                session.remove(qt);
                transaction.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
