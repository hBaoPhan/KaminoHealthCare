package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.ChiTietHoaDonId;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO extends GenericDAO<ChiTietHoaDon, ChiTietHoaDonId> {

    private final SuPhanBoLoDAO suPhanBoLoDAO = new SuPhanBoLoDAO();

    @Override
    protected Class<ChiTietHoaDon> getEntityClass() {
        return ChiTietHoaDon.class;
    }

    public List<ChiTietHoaDon> layTheoMaHoaDon(String maHD) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            String hql = "from ChiTietHoaDon c where c.hoaDon.maHoaDon = :maHoaDon";
            return session.createQuery(hql, ChiTietHoaDon.class)
                    .setParameter("maHoaDon", maHD)
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean xoa(String maHD, String maDV) {
        return super.xoa(new ChiTietHoaDonId(maHD, maDV));
    }
}