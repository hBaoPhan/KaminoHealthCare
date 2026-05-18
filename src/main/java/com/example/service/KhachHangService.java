package com.example.service;

import com.example.dao.KhachHangDAO;
import com.example.entity.KhachHang;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Khách hàng.
 */
public class KhachHangService {

    private final KhachHangDAO khachHangDAO = new KhachHangDAO();

    // ==================== TRUY VẤN ====================

    public List<KhachHang> layTatCa() {
        return khachHangDAO.layTatCa();
    }

    public KhachHang timTheoMa(String maKH) {
        return khachHangDAO.timTheoMa(maKH);
    }

    /**
     * Tìm khách hàng theo số điện thoại.
     * Sử dụng trong màn hình Bán hàng để tra cứu nhanh.
     *
     * @return KhachHang nếu tìm thấy, null nếu không có
     */
    public KhachHang timTheoSdt(String sdt) {
        return khachHangDAO.timTheoSdt(sdt);
    }

    // ==================== CRUD ====================

    /**
     * Thêm khách hàng mới.
     * Kiểm tra số điện thoại đã tồn tại trước khi lưu.
     *
     * @return true nếu thành công, false nếu SĐT đã tồn tại
     */
    public boolean them(KhachHang kh) {
        if (kh.getSdt() != null && timTheoSdt(kh.getSdt()) != null) {
            return false; // SĐT đã tồn tại
        }
        return khachHangDAO.them(kh);
    }

    public boolean capNhat(KhachHang kh) {
        return khachHangDAO.capNhat(kh);
    }

    public boolean xoa(String maKH) {
        return khachHangDAO.xoa(maKH);
    }
}
