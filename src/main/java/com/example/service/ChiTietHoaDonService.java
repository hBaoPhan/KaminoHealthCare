package com.example.service;

import com.example.dao.ChiTietHoaDonDAO;
import com.example.entity.ChiTietHoaDon;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Chi tiết hóa đơn.
 * Lưu ý: ChiTietHoaDon thường được xử lý trong transaction của HoaDonService,
 * Service này chủ yếu phục vụ truy vấn độc lập.
 */
public class ChiTietHoaDonService {

    private final ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();

    // ==================== TRUY VẤN ====================

    /**
     * Lấy danh sách chi tiết của một hóa đơn, kèm thông tin phân bổ lô.
     */
    public List<ChiTietHoaDon> layTheoMaHoaDon(String maHoaDon) {
        return chiTietHoaDonDAO.layTheoMaHoaDon(maHoaDon);
    }

    // ==================== CRUD ====================

    /**
     * Thêm chi tiết hóa đơn trong phạm vi transaction đã cho.
     * Được gọi từ HoaDonService khi cần kiểm soát transaction thủ công.
     */
    public boolean them(ChiTietHoaDon ct, Connection con) throws SQLException {
        return chiTietHoaDonDAO.them(ct, con);
    }

    public boolean capNhat(ChiTietHoaDon ct) {
        return chiTietHoaDonDAO.capNhat(ct);
    }

    public boolean xoa(String maHD, String maDV, boolean laQuaTangKem) {
        return chiTietHoaDonDAO.xoa(maHD, maDV, laQuaTangKem);
    }
}
