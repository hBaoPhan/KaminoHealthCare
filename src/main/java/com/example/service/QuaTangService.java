package com.example.service;

import com.example.dao.QuaTangDAO;
import com.example.entity.QuaTang;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Quà tặng kèm khuyến mãi.
 * Lưu ý: QuaTang thường được quản lý qua KhuyenMaiService (cascade),
 * Service này phục vụ truy vấn và thao tác độc lập nếu cần.
 */
public class QuaTangService {

    private final QuaTangDAO quaTangDAO = new QuaTangDAO();

    // ==================== TRUY VẤN ====================

    public List<QuaTang> layTatCa() {
        return quaTangDAO.layTatCa();
    }

    public List<QuaTang> timTheoKhuyenMai(String maKhuyenMai) {
        return quaTangDAO.timTheoKhuyenMai(maKhuyenMai);
    }

    // ==================== CRUD ====================

    public boolean them(QuaTang qt) {
        return quaTangDAO.them(qt);
    }

    public boolean capNhat(QuaTang qt) {
        return quaTangDAO.capNhat(qt);
    }

    public boolean xoa(String maKM, String maDonVi) {
        return quaTangDAO.xoa(maKM, maDonVi);
    }

    public boolean xoaTheoKhuyenMai(String maKM) {
        return quaTangDAO.xoaTheoKhuyenMai(maKM);
    }
}
