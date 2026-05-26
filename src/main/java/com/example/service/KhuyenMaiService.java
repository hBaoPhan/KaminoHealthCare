package com.example.service;

import com.example.dao.KhuyenMaiDAO;
import com.example.entity.KhuyenMai;
import com.example.entity.enums.LoaiKhuyenMai;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ liên quan đến Khuyến mãi.
 */
public class KhuyenMaiService {

    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    // ==================== TRUY VẤN ====================

    public List<KhuyenMai> layTatCa() {
        return khuyenMaiDAO.layTatCa();
    }

    public KhuyenMai timTheoMa(String maKM) {
        return khuyenMaiDAO.timTheoMa(maKM);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Lấy danh sách khuyến mãi còn hiệu lực tại thời điểm hiện tại.
     * Áp dụng lọc theo loại khách hàng: khách lẻ không thấy KM có uuDaiThanhVien = true.
     *
     * @param isThanhVien true nếu là khách hàng thành viên
     */
    public List<KhuyenMai> layKhuyenMaiConHan(boolean isThanhVien) {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiDAO.layTatCa().stream()
                .filter(km -> (km.getThoiGianBatDau() == null || !km.getThoiGianBatDau().isAfter(now))
                           && (km.getThoiGianKetThuc() == null || !km.getThoiGianKetThuc().isBefore(now)))
                .filter(km -> !km.isUuDaiThanhVien() || isThanhVien) // KM thành viên chỉ hiện khi isThanhVien=true
                .collect(Collectors.toList());
    }

    /**
     * Overload không tham số: trả về tất cả KM còn hạn (cả dành riêng thành viên).
     * Dùng cho màn hình quản lý KM.
     */
    public List<KhuyenMai> layKhuyenMaiConHan() {
        return layKhuyenMaiConHan(true); // hiện tất cả cho màn hình quản lý
    }

    /**
     * Chọn khuyến mãi tốt nhất áp dụng cho đơn hàng với tổng tiền cho trước.
     * <p>
     * Ưu tiên: KM giảm % có số tiền giảm lớn nhất > KM tặng kèm > không áp dụng.
     * KM có uuDaiThanhVien = true sẽ tự động bỏ qua nếu isThanhVien = false.
     *
     * @param dsKhuyenMai Danh sách khuyến mãi còn hạn
     * @param tongTienHang Tổng tiền hàng (chưa thuế, chưa KM)
     * @param isThanhVien  true nếu khách là khách hàng thành viên
     * @return index trong dsKhuyenMai của KM tốt nhất (0-based), hoặc -1 nếu không có KM phù hợp
     */
    public int chonKhuyenMaiTotNhat(List<KhuyenMai> dsKhuyenMai, double tongTienHang, boolean isThanhVien) {
        int bestIndex = -1;
        double maxGiam = -1;

        for (int i = 0; i < dsKhuyenMai.size(); i++) {
            KhuyenMai km = dsKhuyenMai.get(i);
            // Bỏ qua KM ưu đãi thành viên nếu khách là khách lẻ
            if (km.isUuDaiThanhVien() && !isThanhVien) continue;
            if (tongTienHang >= km.getGiaTriDonHangToiThieu()) {
                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                    double giam = tongTienHang * km.getKhuyenMaiPhanTram() / 100.0;
                    if (giam > maxGiam) {
                        maxGiam = giam;
                        bestIndex = i;
                    }
                } else if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM) {
                    if (maxGiam <= 0) {
                        maxGiam = 0;
                        bestIndex = i;
                    }
                }
            }
        }
        return bestIndex;
    }

    /**
     * Overload backward-compatible (không phân biệt thành viên).
     * Dùng cho màn hình quản lý hoặc khi chưa xác định trạng thái khách hàng.
     */
    public int chonKhuyenMaiTotNhat(List<KhuyenMai> dsKhuyenMai, double tongTienHang) {
        return chonKhuyenMaiTotNhat(dsKhuyenMai, tongTienHang, true);
    }

    /**
     * Tính số tiền được giảm từ một khuyến mãi cụ thể.
     *
     * @return số tiền giảm, hoặc 0 nếu không đủ điều kiện hoặc không phải loại %
     */
    public double tinhSoTienGiam(KhuyenMai km, double tongTienHang) {
        if (km == null || tongTienHang < km.getGiaTriDonHangToiThieu()) {
            return 0;
        }
        if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            return tongTienHang * km.getKhuyenMaiPhanTram() / 100.0;
        }
        return 0;
    }

    // ==================== CRUD ====================

    public boolean them(KhuyenMai km) {
        return khuyenMaiDAO.them(km);
    }

    public boolean capNhat(KhuyenMai km) {
        return khuyenMaiDAO.capNhat(km);
    }

    public boolean xoa(String maKM) {
        return khuyenMaiDAO.xoa(maKM);
    }

    public String generateNextMaKhuyenMai() {
        return khuyenMaiDAO.generateNextMaKhuyenMai();
    }
}
