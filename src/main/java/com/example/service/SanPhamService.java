package com.example.service;

import com.example.dao.SanPhamDAO;
import com.example.entity.SanPham;
import com.example.entity.enums.LoaiSanPham;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Sản phẩm.
 */
public class SanPhamService {

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    // ==================== TRUY VẤN ====================

    public List<SanPham> layTatCa() {
        return sanPhamDAO.layTatCa();
    }

    public SanPham timTheoMa(String maSP) {
        return sanPhamDAO.timTheoMa(maSP);
    }

    public List<SanPham> timTheoMaHoacTen(String tuKhoa) {
        return sanPhamDAO.timTheoMaHoacTen(tuKhoa);
    }

    public List<SanPham> timKiemGoiY(String tuKhoa) {
        return sanPhamDAO.timKiemGoiY(tuKhoa);
    }

    public List<SanPham> timTheoPhanLoai(LoaiSanPham loaiSanPham) {
        return sanPhamDAO.timTheoPhanLoai(loaiSanPham);
    }

    public List<SanPham> laySanPhamDangKinhDoanh() {
        return sanPhamDAO.laySanPhamDangKinhDoanh();
    }

    public List<SanPham> timKiemNangCao(String tuKhoa, LoaiSanPham loaiSanPham) {
        return sanPhamDAO.timKiemNangCao(tuKhoa, loaiSanPham);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Tự động sinh mã sản phẩm theo quy tắc: [PhanLoai]-[VietTat3Chu]-[STT3ChuSo].
     * <p>
     * Ví dụ: Panadol (OTC) → "OTC-PAN-001"
     * <p>
     * Di chuyển từ SanPhamDAO — đây là logic nghiệp vụ, không phải CRUD thuần.
     */
    public String taoMaSanPhamTuDong(LoaiSanPham loaiSanPham, String tenSanPham) {
        return sanPhamDAO.taoMaSanPhamTuDong(loaiSanPham, tenSanPham);
    }

    /**
     * Thêm sản phẩm mới. Kiểm tra mã trùng trước khi lưu.
     *
     * @return true nếu thành công, false nếu mã đã tồn tại hoặc lỗi DB
     */
    public boolean them(SanPham sp) {
        if (sanPhamDAO.tonTaiMaSanPham(sp.getMaSanPham())) {
            return false; // Mã đã tồn tại
        }
        return sanPhamDAO.them(sp);
    }

    public boolean capNhat(SanPham sp) {
        return sanPhamDAO.capNhat(sp);
    }

    public boolean xoa(String maSP) {
        return sanPhamDAO.xoa(maSP);
    }

    public boolean capNhatSoLuongTon(String maSP, int soLuongMoi) {
        return sanPhamDAO.capNhatSoLuongTon(maSP, soLuongMoi);
    }

    public boolean tonTaiMaSanPham(String maSP) {
        return sanPhamDAO.tonTaiMaSanPham(maSP);
    }
}
