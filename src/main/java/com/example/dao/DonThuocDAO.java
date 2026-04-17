package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.DonThuoc;

import java.sql.*;
import java.util.List;

public class DonThuocDAO {

    public List<DonThuoc> layTatCa() {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.createQuery("from DonThuoc", DonThuoc.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public DonThuoc timTheoMa(String maDT) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.get(DonThuoc.class, maDT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(DonThuoc dt) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(dt);
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

    public boolean capNhat(DonThuoc dt) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(dt);
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

    public boolean xoa(String maDT) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            DonThuoc dt = session.get(DonThuoc.class, maDT);
            if (dt != null) {
                session.remove(dt);
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
