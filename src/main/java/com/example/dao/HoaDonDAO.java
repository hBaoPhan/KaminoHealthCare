package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> layTatCa() {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.createQuery("from HoaDon", HoaDon.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public HoaDon timTheoMa(String maHD) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.get(HoaDon.class, maHD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(HoaDon hd) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(hd);
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

    public boolean capNhat(HoaDon hd) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(hd);
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

    public boolean xoa(String maHD) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            HoaDon hd = session.get(HoaDon.class, maHD);
            if (hd != null) {
                session.remove(hd);
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
