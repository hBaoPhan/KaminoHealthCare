package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.DonVi;
import com.example.entity.DonViQuyDoi;
import com.example.entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonViQuyDoiDAO {

    public List<DonViQuyDoi> layTatCa() {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.createQuery("from DonViQuyDoi", DonViQuyDoi.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new java.util.ArrayList<>();
        }
    }

    public DonViQuyDoi timTheoMa(String maDV) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.get(DonViQuyDoi.class, maDV);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(DonViQuyDoi dv) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(dv);
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

    public boolean capNhat(DonViQuyDoi dv) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(dv);
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

    public boolean xoa(String maDV) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            DonViQuyDoi dv = session.get(DonViQuyDoi.class, maDV);
            if (dv != null) {
                session.remove(dv);
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
