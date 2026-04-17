package com.example.dao;

import com.example.entity.SuPhanBoLo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.example.connectDB.ConnectDB;

public class SuPhanBoLoDAO {

    public boolean themSuPhanBoLo(SuPhanBoLo spbl) {
        org.hibernate.Transaction transaction = null;
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(spbl);
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

    public List<SuPhanBoLo> layPhanBoLoCuaChiTiet(String maHoaDon, String maDonVi) {
        try (org.hibernate.Session session = ConnectDB.getSessionFactory().openSession()) {
            String hql = "from SuPhanBoLo s where s.chiTietHoaDon.hoaDon.maHoaDon = :maHD and s.chiTietHoaDon.donViQuyDoi.maDonVi = :maDV";
            return session.createQuery(hql, SuPhanBoLo.class)
                          .setParameter("maHD", maHoaDon)
                          .setParameter("maDV", maDonVi)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
