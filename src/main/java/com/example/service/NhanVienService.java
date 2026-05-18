package com.example.service;

import com.example.dao.NhanVienDAO;
import com.example.entity.NhanVien;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Nhân viên.
 */
public class NhanVienService {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    // ==================== TRUY VẤN ====================

    public List<NhanVien> layTatCa() {
        return nhanVienDAO.layTatCa();
    }

    public NhanVien timTheoMa(String maNV) {
        return nhanVienDAO.timTheoMa(maNV);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Thêm nhân viên mới. Kiểm tra mã chưa tồn tại trước khi lưu.
     *
     * @return true nếu thành công, false nếu mã đã tồn tại
     */
    public boolean them(NhanVien nv) {
        if (nhanVienDAO.timTheoMa(nv.getMaNhanVien()) != null) {
            return false; // Mã đã tồn tại
        }
        return nhanVienDAO.them(nv);
    }

    public boolean capNhat(NhanVien nv) {
        return nhanVienDAO.capNhat(nv);
    }

    /**
     * Khoá/mở tài khoản nhân viên bằng cách cập nhật trạng thái.
     */
    public boolean capNhatTrangThai(String maNV, boolean trangThaiMoi) {
        NhanVien nv = nhanVienDAO.timTheoMa(maNV);
        if (nv == null) return false;
        nv.setTrangThai(trangThaiMoi);
        return nhanVienDAO.capNhat(nv);
    }

    public boolean xoa(String maNV) {
        return nhanVienDAO.xoa(maNV);
    }
}
