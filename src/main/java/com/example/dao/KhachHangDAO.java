package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.KhachHang;
import com.example.entity.TrangThaiKhachHang;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> layTatCa() {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.createQuery("from KhachHang", KhachHang.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public KhachHang timTheoMa(String maKH) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            return session.get(KhachHang.class, maKH);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean them(KhachHang kh) {
        Transaction transaction = null;
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(kh);
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

    public boolean capNhat(KhachHang kh) {
        Transaction transaction = null;
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(kh);
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

    public boolean xoa(String maKH) {
        Transaction transaction = null;
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            KhachHang kh = session.get(KhachHang.class, maKH);
            if (kh != null) {
                session.remove(kh);
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
