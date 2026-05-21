package com.example.service;

import com.example.dao.SuPhanBoLoDAO;
import com.example.entity.SuPhanBoLo;
import com.example.entity.ChiTietHoaDon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến phân bổ lô hàng (SuPhanBoLo).
 * Lưu ý: Hầu hết các thao tác ghi SuPhanBoLo diễn ra trong transaction
 * của HoaDonService — Service này chủ yếu phục vụ truy vấn.
 */
public class SuPhanBoLoService {

    private final SuPhanBoLoDAO suPhanBoLoDAO = new SuPhanBoLoDAO();

    // ==================== TRUY VẤN ====================

    /**
     * Lấy danh sách phân bổ lô của một dòng chi tiết hóa đơn.
     */
    public List<SuPhanBoLo> layPhanBoLoCuaChiTiet(String maHoaDon, String maDonVi, boolean laQuaTangKem) {
        return suPhanBoLoDAO.layPhanBoLoCuaChiTiet(maHoaDon, maDonVi, laQuaTangKem);
    }

    // ==================== CRUD ====================

    /**
     * Ghi bản ghi phân bổ lô trong transaction đã cho.
     * Được gọi từ HoaDonService khi thực thi nghiệp vụ bán/đổi/trả hàng.
     */
    public boolean them(SuPhanBoLo spbl) throws SQLException {
        return suPhanBoLoDAO.themSuPhanBoLo(spbl);
    }

    public boolean themSuPhanBoLo(SuPhanBoLo spb) throws SQLException {
        return suPhanBoLoDAO.themSuPhanBoLo(spb);
    }

    public boolean themNhieu(List<SuPhanBoLo> ds, String maHD) throws SQLException {
        return suPhanBoLoDAO.themNhieu(ds, maHD);
    }

    public boolean xoaToanBoPhanBoLo(String maHD) throws SQLException {
        return suPhanBoLoDAO.xoaToanBoPhanBoLo(maHD);
    }

    public List<SuPhanBoLo> layDanhSachPhanBoLoCanTra(String maHoaDonGoc, List<ChiTietHoaDon> dsChiTietTra) {
        return suPhanBoLoDAO.layDanhSachPhanBoLoCanTra(maHoaDonGoc, dsChiTietTra);
    }

}
