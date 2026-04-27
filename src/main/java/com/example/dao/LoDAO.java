package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.Lo;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

public class LoDAO extends GenericDAO<Lo, String> {

    @Override
    protected Class<Lo> getEntityClass() {
        return Lo.class;
    }

    /**
     * Lấy danh sách các Lô của một Đơn vị quy đổi (hoặc Sản phẩm)
     * còn hạn sử dụng và số lượng > 0, sắp xếp theo Hạn sử dụng tăng dần (FEFO)
     */
    public List<Lo> layDanhSachLoKhaDung(String maDonViQuyDoi) {
        try (Session session = ConnectDB.getSessionFactory().openSession()) {
            // Giả định entity Lo có map khóa ngoại donViQuyDoi và thuộc tính hanSuDung
            String hql = "from Lo l " +
                         "where l.donViQuyDoi.maDonVi = :maDV " +
                         "and l.soLuong > 0 " +
                         "and l.hanSuDung > current_date() " +
                         "order by l.hanSuDung ASC";
            return session.createQuery(hql, Lo.class)
                          .setParameter("maDV", maDonViQuyDoi)
                          .list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}