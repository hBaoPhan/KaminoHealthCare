package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.QuaTang;
import com.example.entity.QuaTangId;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho QuaTang (composite key: maKhuyenMai + maSanPham).
 * Kế thừa them() và layTatCa() từ GenericDAO.
 * Override xoa() nhận composite key rời thay vì QuaTangId object.
 */
public class QuaTangDAO extends GenericDAO<QuaTang, QuaTangId> {

    @Override
    protected Class<QuaTang> getEntityClass() {
        return QuaTang.class;
    }

    /** Tìm tất cả quà tặng thuộc một khuyến mãi. */
    public List<QuaTang> timTheoKhuyenMai(String maKM) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            String hql = "from QuaTang q where q.khuyenMai.maKhuyenMai = :maKM";
            return session.createQuery(hql, QuaTang.class)
                          .setParameter("maKM", maKM)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /** Xóa quà tặng theo composite key (maKhuyenMai, maSanPham). */
    public boolean xoa(String maKM, String maSP) {
        return super.xoa(new QuaTangId(maKM, maSP));
    }
}
