package com.example.service;

import com.example.dao.NhanVienDAO;
import com.example.dao.TaiKhoanDAO;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Tài khoản và xác thực.
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    // ==================== XÁC THỰC ====================

    /**
     * Xác thực đăng nhập. Hỗ trợ cả mật khẩu BCrypt lẫn plain-text (legacy).
     * <p>
     * Di chuyển từ DangNhapPanel.actionPerformed() — logic BCrypt không thuộc về tầng UI.
     *
     * @param tenDangNhap Tên đăng nhập
     * @param matKhau     Mật khẩu plain-text do người dùng nhập
     * @return TaiKhoan nếu xác thực thành công, null nếu sai tài khoản/mật khẩu
     * @throws IllegalStateException nếu tài khoản bị khóa
     */
    public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
        TaiKhoan tk = taiKhoanDAO.timTheoMa(tenDangNhap);
        if (tk == null) {
            return null; // Tài khoản không tồn tại
        }

        if (!tk.getNhanVien().isTrangThai()) {
            throw new IllegalStateException("Tài khoản đã bị khóa, vui lòng liên hệ quản lý.");
        }

        String dbPassword = tk.getMatKhau();
        boolean isMatch;

        // Hỗ trợ cả BCrypt (hash bắt đầu bằng $2) lẫn plain-text legacy
        if (dbPassword != null && dbPassword.startsWith("$2")) {
            try {
                isMatch = BCrypt.checkpw(matKhau, dbPassword);
            } catch (Exception ex) {
                isMatch = false;
            }
        } else {
            isMatch = matKhau.equals(dbPassword);
        }

        return isMatch ? tk : null;
    }

    // ==================== TRUY VẤN ====================

    public List<TaiKhoan> layTatCa() {
        return taiKhoanDAO.layTatCa();
    }

    public TaiKhoan timTheoMa(String tenDangNhap) {
        return taiKhoanDAO.timTheoMa(tenDangNhap);
    }

    // ==================== CRUD ====================

    /**
     * Thêm tài khoản mới. Kiểm tra tên đăng nhập chưa tồn tại
     * và nhân viên chưa có tài khoản.
     *
     * @return true nếu thành công
     */
    public boolean them(TaiKhoan tk, List<TaiKhoan> danhSachHienTai) {
        // Kiểm tra tên đăng nhập đã tồn tại
        if (taiKhoanDAO.timTheoMa(tk.getTenDangNhap()) != null) {
            return false;
        }
        // Kiểm tra nhân viên đã có tài khoản
        String maNV = tk.getNhanVien().getMaNhanVien();
        boolean nvDaCoTaiKhoan = danhSachHienTai.stream()
                .anyMatch(t -> t.getNhanVien().getMaNhanVien().equals(maNV));
        if (nvDaCoTaiKhoan) {
            return false;
        }
        return taiKhoanDAO.them(tk);
    }

    /**
     * Cập nhật mật khẩu và/hoặc trạng thái nhân viên.
     * Di chuyển từ TaiKhoanPanel.suaTaiKhoan().
     */
    public boolean capNhatTaiKhoan(TaiKhoan tk, boolean trangThaiMoi, String matKhauMoi) {
        NhanVien nv = nhanVienDAO.timTheoMa(tk.getNhanVien().getMaNhanVien());
        if (nv == null) return false;

        nv.setTrangThai(trangThaiMoi);
        nhanVienDAO.capNhat(nv);

        if (matKhauMoi != null && !matKhauMoi.trim().isEmpty()) {
            TaiKhoan tkCapNhat = new TaiKhoan(tk.getTenDangNhap(), matKhauMoi, nv);
            return taiKhoanDAO.capNhat(tkCapNhat);
        }
        return true;
    }

    public boolean xoa(String tenDangNhap) {
        return taiKhoanDAO.xoa(tenDangNhap);
    }
}
