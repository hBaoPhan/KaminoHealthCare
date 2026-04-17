package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.PhanLoai;
import com.example.entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPham> layTatCa() {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.createQuery("from SanPham", SanPham.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public SanPham timTheoMa(String maSP) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.get(SanPham.class, maSP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(SanPham sp) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(sp);
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

    public boolean capNhat(SanPham sp) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(sp);
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

    public boolean xoa(String maSP) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            SanPham sp = session.get(SanPham.class, maSP);
            if (sp != null) {
                session.remove(sp);
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
