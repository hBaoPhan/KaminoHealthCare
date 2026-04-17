package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    public List<TaiKhoan> layTatCa() {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.createQuery("from TaiKhoan", TaiKhoan.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public TaiKhoan timTheoMa(String tenDN) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.get(TaiKhoan.class, tenDN);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(TaiKhoan tk) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(tk);
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

    public boolean capNhat(TaiKhoan tk) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(tk);
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

    public boolean xoa(String tenDN) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            TaiKhoan tk = session.get(TaiKhoan.class, tenDN);
            if (tk != null) {
                session.remove(tk);
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
