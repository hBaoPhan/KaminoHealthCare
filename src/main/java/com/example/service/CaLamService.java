package com.example.service;

import com.example.dao.CaLamDAO;
import com.example.entity.CaLam;
import com.example.entity.enums.TrangThaiCaLam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Ca làm việc.
 */
public class CaLamService {

    private final CaLamDAO caLamDAO = new CaLamDAO();

    // ==================== TRUY VẤN ====================

    public List<CaLam> layTatCa() {
        return caLamDAO.layTatCa();
    }

    public CaLam timTheoMa(String maCa) {
        return caLamDAO.timTheoMa(maCa);
    }

    /**
     * Lấy ca đang mở (DANG_MO) của nhân viên — trả null nếu chưa mở ca.
     */
    public CaLam layCaHienTai(String maNhanVien) {
        return caLamDAO.layCaHienTai(maNhanVien);
    }

    public List<CaLam> layCaTheoNgayVaTen(LocalDate ngay, String tenNV) {
        return caLamDAO.layCaTheoNgayVaTen(ngay, tenNV);
    }

    public List<CaLam> layCaTrongTuan(LocalDate tuNgay, LocalDate denNgay) {
        return caLamDAO.layCaTrongTuan(tuNgay, denNgay);
    }

    public boolean kiemTraTrungCa(String maNV, LocalDateTime start, LocalDateTime end, String maCaHienTai) {
        return caLamDAO.kiemTraTrungCa(maNV, start, end, maCaHienTai);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Sinh mã ca tự động theo định dạng: CA[ddMMyy][STT 2 chữ số].
     * <p>
     * Ví dụ: CA16052601
     * Di chuyển từ MoCaPanel — đây là nghiệp vụ, không phải CRUD.
     */
    public String sinhMaCaLam(String maNhanVien) {
        LocalDate today = LocalDate.now();
        String prefix = "CA" + today.format(DateTimeFormatter.ofPattern("ddMMyy"))
                + maNhanVien.replaceAll("[^0-9]", "").substring(0, Math.min(2,
                        maNhanVien.replaceAll("[^0-9]", "").length()));
        // Dùng laySoLuongCaTrongNgay để tránh trùng mã
        int stt = caLamDAO.laySoLuongCaTrongNgay(prefix) + 1;
        return prefix + String.format("%02d", stt);
    }

    /**
     * Mở ca làm việc mới cho nhân viên.
     * Kiểm tra không có ca đang mở trước khi tạo mới.
     *
     * @return true nếu mở ca thành công, false nếu đã có ca đang mở
     */
    public boolean moCa(CaLam caLam) {
        CaLam caHienTai = caLamDAO.layCaHienTai(caLam.getNhanVien().getMaNhanVien());
        if (caHienTai != null) {
            return false; // Đã có ca đang mở
        }
        return caLamDAO.them(caLam);
    }

    /**
     * Đóng ca làm việc: cập nhật giờ kết thúc, tiền két, trạng thái DA_DONG.
     */
    public boolean dongCa(CaLam caLam) {
        caLam.setGioKetThuc(LocalDateTime.now());
        caLam.setTrangThai(TrangThaiCaLam.DONG);
        return caLamDAO.capNhat(caLam);
    }

    // ==================== CRUD ====================

    public boolean them(CaLam caLam) {
        return caLamDAO.them(caLam);
    }

    public boolean capNhat(CaLam caLam) {
        return caLamDAO.capNhat(caLam);
    }

    public boolean xoa(String maCa) {
        return caLamDAO.xoa(maCa);
    }
}
