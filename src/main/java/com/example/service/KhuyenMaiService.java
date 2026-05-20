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
     * <p>
     * Di chuyển từ BanHangPanel: loại bỏ KM chưa bắt đầu hoặc đã hết hạn.
     */
    public List<KhuyenMai> layKhuyenMaiConHan() {
        LocalDateTime now = LocalDateTime.now();
        return khuyenMaiDAO.layTatCa().stream()
                .filter(km -> (km.getThoiGianBatDau() == null || !km.getThoiGianBatDau().isAfter(now))
                           && (km.getThoiGianKetThuc() == null || !km.getThoiGianKetThuc().isBefore(now)))
                .collect(Collectors.toList());
    }

    /**
     * Chọn khuyến mãi tốt nhất áp dụng cho đơn hàng với tổng tiền cho trước.
     * <p>
     * Ưu tiên: KM giảm % có số tiền giảm lớn nhất > KM tặng kèm > không áp dụng.
     * Di chuyển từ BanHangPanel.autoSelectBestKhuyenMai().
     *
     * @param dsKhuyenMai Danh sách khuyến mãi còn hạn
     * @param tongTienHang Tổng tiền hàng (chưa thuế, chưa KM)
     * @return index trong dsKhuyenMai của KM tốt nhất (0-based), hoặc -1 nếu không có KM phù hợp
     */
    public int chonKhuyenMaiTotNhat(List<KhuyenMai> dsKhuyenMai, double tongTienHang) {
        int bestIndex = -1;
        double maxGiam = -1;

        for (int i = 0; i < dsKhuyenMai.size(); i++) {
            KhuyenMai km = dsKhuyenMai.get(i);
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
