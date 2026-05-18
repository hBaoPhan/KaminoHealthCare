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

    public List<Lo> layDanhSachLoKhaDung(String maDonViQuyDoi) throws java.sql.SQLException {
        return loDAO.layDanhSachLoKhaDung(maDonViQuyDoi);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Sinh mã lô tự động — di chuyển từ LoDAO (đây là nghiệp vụ, không phải CRUD).
     * Định dạng: LO + DD + MM + YY + XXX
     * Ví dụ: LO060526001
     */
    public String sinhMaLo() {
        LocalDate today = LocalDate.now();
        String prefix = "LO"
                + String.format("%02d", today.getDayOfMonth())
                + String.format("%02d", today.getMonthValue())
                + String.format("%02d", today.getYear() % 100);

        String lastMaLo = loDAO.layMaLoMoiNhat(prefix);
        if (lastMaLo != null) {
            int lastNumber = Integer.parseInt(lastMaLo.substring(lastMaLo.length() - 3));
            return prefix + String.format("%03d", lastNumber + 1);
        } else {
            return prefix + "001";
        }
    }

    /**
     * Lấy danh sách lô sắp hết hạn trong N ngày tới.
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

    public boolean capNhatSoLuongTon(String maLo, int soLuongThayDoi) throws java.sql.SQLException {
        return loDAO.capNhatSoLuongTon(maLo, soLuongThayDoi);
    }

    public boolean capNhatTonKhoNhieu(List<com.example.entity.SuPhanBoLo> danhSachPhanBo, boolean isHoanTra)
            throws java.sql.SQLException {
        return loDAO.capNhatTonKhoNhieu(danhSachPhanBo, isHoanTra);
    }
}
