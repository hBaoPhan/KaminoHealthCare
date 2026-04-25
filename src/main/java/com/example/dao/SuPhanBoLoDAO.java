package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.SuPhanBoLo;
import com.example.entity.SuPhanBoLoId;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho SuPhanBoLo (composite key: chiTietHoaDon + lo).
 * Kế thừa them() từ GenericDAO.
 */
public class SuPhanBoLoDAO extends GenericDAO<SuPhanBoLo, SuPhanBoLoId> {

    @Override
    protected Class<SuPhanBoLo> getEntityClass() {
        return SuPhanBoLo.class;
    }

    /** Lấy tất cả phân bổ lô của một dòng chi tiết hóa đơn. */
    public List<SuPhanBoLo> layPhanBoLoCuaChiTiet(String maHoaDon, String maDonVi) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            String hql = "from SuPhanBoLo s " +
                         "where s.chiTietHoaDon.hoaDon.maHoaDon = :maHD " +
                         "and s.chiTietHoaDon.donViQuyDoi.maDonVi = :maDV";
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
