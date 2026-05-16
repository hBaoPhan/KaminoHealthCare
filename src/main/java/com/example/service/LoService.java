package com.example.service;

import com.example.dao.LoDAO;
import com.example.entity.Lo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Lô hàng.
 */
public class LoService {

    private final LoDAO loDAO = new LoDAO();

    // ==================== TRUY VẤN ====================

    public List<Lo> layTatCa() {
        return loDAO.layTatCa();
    }

    public Lo timTheoMa(String maLo) {
        return loDAO.timTheoMa(maLo);
    }

    public List<Lo> layDanhSachLoKhaDung(String maDonViQuyDoi) {
        return loDAO.layDanhSachLoKhaDung(maDonViQuyDoi);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Sinh mã lô tự động — di chuyển từ LoDAO (đây là nghiệp vụ, không phải CRUD).
     */
    public String sinhMaLo() {
        return loDAO.sinhMaLo();
    }

    /**
     * Lấy danh sách lô sắp hết hạn trong N ngày tới.
     * Di chuyển từ LoPanel — đây là nghiệp vụ cảnh báo.
     *
     * @param soNgay Số ngày cảnh báo trước (ví dụ: 30)
     * @return Danh sách lô hết hạn trong vòng soNgay ngày
     */
    public List<Lo> layLoSapHetHan(int soNgay) {
        LocalDate ngayGioi = LocalDate.now().plusDays(soNgay);
        return loDAO.layTatCa().stream()
                .filter(lo -> lo.getNgayHetHan() != null
                        && !lo.getNgayHetHan().isAfter(ngayGioi)
                        && lo.getSoLuongSanPham() > 0)
                .collect(Collectors.toList());
    }

    /**
     * Lấy các lô đã hết hàng (soLuong = 0).
     */
    public List<Lo> layLoHetHang() {
        return loDAO.layTatCa().stream()
                .filter(lo -> lo.getSoLuongSanPham() == 0)
                .collect(Collectors.toList());
    }

    // ==================== CRUD ====================

    public boolean them(Lo lo) {
        return loDAO.themLo(lo);
    }

    public boolean capNhat(Lo lo) {
        return loDAO.capNhatLo(lo);
    }

    public boolean xoa(String maLo) {
        return loDAO.xoaLo(maLo);
    }

    public boolean capNhatSoLuongTon(String maLo, int soLuongThayDoi) {
        return loDAO.capNhatSoLuongTon(maLo, soLuongThayDoi);
    }
}
