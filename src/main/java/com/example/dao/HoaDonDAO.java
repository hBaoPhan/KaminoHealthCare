package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.HoaDon;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.entity.HoaDon;

public class HoaDonDAO extends GenericDAO<HoaDon, String> {

    @Override
    protected Class<HoaDon> getEntityClass() {
        return HoaDon.class;
    }

    public List<HoaDon> timKiem(String maHoaDon, LocalDate ngayTao) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("from HoaDon h where 1=1");

            if (maHoaDon != null && !maHoaDon.trim().isEmpty()) {
                hql.append(" and h.maHoaDon like :maHoaDon");
            }
            if (ngayTao != null) {
                hql.append(" and h.thoiGianTao >= :startOfDay and h.thoiGianTao < :startOfNextDay");
            }

            Query<HoaDon> query = session.createQuery(hql.toString(), HoaDon.class);

            if (maHoaDon != null && !maHoaDon.trim().isEmpty()) {
                query.setParameter("maHoaDon", "%" + maHoaDon.trim() + "%");
            }
            if (ngayTao != null) {
                query.setParameter("startOfDay", ngayTao.atStartOfDay());
                query.setParameter("startOfNextDay", ngayTao.plusDays(1).atStartOfDay());
            }

            List<HoaDon> list = query.list();
            ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
            for (HoaDon h : list) {
                h.setDsChiTiet(ctDAO.layTheoMaHoaDon(h.getMaHoaDon()));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<HoaDon> layTatCa() {
        List<HoaDon> list = super.layTatCa();
        ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
        for (HoaDon h : list) {
            h.setDsChiTiet(ctDAO.layTheoMaHoaDon(h.getMaHoaDon()));
        }
        return list;
    }

    @Override
    public HoaDon timTheoMa(String id) {
        HoaDon h = super.timTheoMa(id);
        if (h != null) {
            h.setDsChiTiet(new ChiTietHoaDonDAO().layTheoMaHoaDon(h.getMaHoaDon()));
        }
        return h;
    }
}
