package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    private SuPhanBoLoDAO suPhanBoLoDAO = new SuPhanBoLoDAO();

    public List<ChiTietHoaDon> layTheoMaHoaDon(String maHD) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            String hql = "from ChiTietHoaDon c where c.hoaDon.maHoaDon = :maHoaDon";
            return session.createQuery(hql, ChiTietHoaDon.class)
                          .setParameter("maHoaDon", maHD)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean them(ChiTietHoaDon ct) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(ct);
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

    public boolean capNhat(ChiTietHoaDon ct) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(ct);
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

    public boolean xoa(String maHD, String maDV) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ChiTietHoaDonId id = new ChiTietHoaDonId(maHD, maDV);
            ChiTietHoaDon ct = session.get(ChiTietHoaDon.class, id);
            if (ct != null) {
                session.remove(ct);
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