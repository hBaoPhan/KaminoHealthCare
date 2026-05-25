package com.example.service;

import com.example.dao.DonViQuyDoiDAO;
import com.example.entity.DonViQuyDoi;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Đơn vị quy đổi.
 */
public class DonViQuyDoiService {

    private final DonViQuyDoiDAO donViQuyDoiDAO = new DonViQuyDoiDAO();

    // ==================== TRUY VẤN ====================

    public List<DonViQuyDoi> layTatCa() {
        return donViQuyDoiDAO.layTatCa();
    }

    public DonViQuyDoi timTheoMa(String maDV) {
        return donViQuyDoiDAO.timTheoMa(maDV);
    }

    public List<DonViQuyDoi> timTheoMaSanPham(String maSanPham) {
        return donViQuyDoiDAO.timTheoMaSanPham(maSanPham);
    }

    public DonViQuyDoi timTheoTenVaMaSP(String tenDonViStr, String maSanPham) {
        return donViQuyDoiDAO.timTheoTenVaMaSP(tenDonViStr, maSanPham);
    }

    public DonViQuyDoi timTheoBarcode(String barcode) {
        return donViQuyDoiDAO.timTheoBarcode(barcode);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Sinh mã đơn vị quy đổi tự động (DV001, DV002...).
     * Di chuyển từ DAO — đây là nghiệp vụ sinh mã, không phải CRUD thuần.
     */
    public String taoMaDonViTuDong() {
        return donViQuyDoiDAO.taoMaDonViTuDong();
    }

    // ==================== CRUD ====================

    public boolean them(DonViQuyDoi dv) {
        return donViQuyDoiDAO.them(dv);
    }

    public boolean capNhat(DonViQuyDoi dv) {
        return donViQuyDoiDAO.capNhat(dv);
    }

    public boolean xoa(String maDV) {
        return donViQuyDoiDAO.xoa(maDV);
    }

    public boolean xoaTheoMaSanPham(String maSanPham) {
        return donViQuyDoiDAO.xoaTheoMaSanPham(maSanPham);
    }
}
