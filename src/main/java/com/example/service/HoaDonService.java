package com.example.service;

import com.example.dao.CaLamDAO;
import com.example.dao.HoaDonDAO;
import com.example.entity.CaLam;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.SuPhanBoLo;
import com.example.entity.enums.LoaiHoaDon;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service xử lý toàn bộ nghiệp vụ liên quan đến Hóa đơn.
 * Là tầng trung gian giữa GUI và DAO — chứa logic nghiệp vụ,
 * không chứa SQL và không xử lý UI trực tiếp.
 */
public class HoaDonService {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final CaLamDAO caLamDAO = new CaLamDAO();

    // ==================== TRUY VẤN ====================

    public List<HoaDon> layTatCa() {
        return hoaDonDAO.layTatCa();
    }

    public HoaDon timTheoMa(String maHD) {
        return hoaDonDAO.timTheoMa(maHD);
    }

    /**
     * Lấy hóa đơn bán hàng chưa thanh toán mới nhất của nhân viên.
     */
    public HoaDon layHoaDonChuaThanhToan(String maNhanVien) {
        return hoaDonDAO.layHoaDonChuaThanhToan(maNhanVien);
    }

    public List<HoaDon> timKiem(String maHD, LocalDate ngayTao) {
        return hoaDonDAO.timKiem(maHD, ngayTao);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Sinh mã hóa đơn theo định dạng: [PREFIX][ddMMyy][STT 3 chữ số].
     * <p>
     * Ví dụ: BAN_HANG -> "HDB160526001"
     * <p>
     * Di chuyển từ BanHangPanel.sinhMaHoaDon() và DoiHangPanel.
     */
    public String sinhMaHoaDon(LoaiHoaDon loaiHoaDon) {
        String prefix = switch (loaiHoaDon) {
            case BAN_HANG -> "HDB";
            case DOI_HANG -> "HDD";
            case TRA_HANG -> "HDT";
        };
        int stt = hoaDonDAO.demHoaDonTrongNgay(loaiHoaDon) + 1;
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        return String.format("%s%s%03d", prefix, date, stt);
    }

    /**
     * Kiểm tra và lấy hóa đơn gốc phục vụ nghiệp vụ đổi hàng.
     * <p>
     * Nghiệp vụ được giữ nguyên từ HoaDonDAO:
     * - Chỉ lấy hóa đơn đã thanh toán thành công
     * - Hóa đơn phải còn trong hạn 7 ngày
     * - Hóa đơn chỉ được đổi 1 lần duy nhất
     */
    public HoaDon layHoaDonDeDoi(String maHD) {
        return hoaDonDAO.layHoaDonDeDoi(maHD);
    }

    /**
     * Lấy ca làm việc hiện tại đang mở của nhân viên.
     * Trả về null nếu nhân viên chưa mở ca.
     */
    public CaLam layCaHienTai(String maNhanVien) {
        return caLamDAO.layCaHienTai(maNhanVien);
    }

    // ==================== LƯU / CẬP NHẬT ====================

    /**
     * Lưu hóa đơn bán hàng với trạng thái chưa thanh toán (không trừ kho).
     * Nếu hóa đơn đã tồn tại, chỉ cập nhật thông tin và chi tiết (idempotent).
     */
    public boolean luuHoaDonBanHang(HoaDon hd, List<ChiTietHoaDon> dsChiTiet) {
        return hoaDonDAO.luuHoaDonBanHang(hd, dsChiTiet);
    }

    /**
     * Xác nhận thanh toán: cập nhật trạng thái và trừ kho theo FEFO.
     *
     * @throws SQLException nếu không đủ tồn kho
     */
    public boolean xacNhanThanhToan(String maHoaDon, List<ChiTietHoaDon> dsChiTiet) throws SQLException {
        return hoaDonDAO.xacNhanThanhToan(maHoaDon, dsChiTiet);
    }

    /**
     * Thực thi toàn bộ luồng đổi hàng trong 1 transaction duy nhất.
     */
    public boolean luuHoaDonDoiHang(HoaDon hoaDonMoi,
                                     List<SuPhanBoLo> dsTraLai,
                                     List<ChiTietHoaDon> dsChiTietMoi,
                                     List<SuPhanBoLo> dsPhanBoMoi) {
        return hoaDonDAO.luuHoaDonDoiHang(hoaDonMoi, dsTraLai, dsChiTietMoi, dsPhanBoMoi);
    }

    /**
     * Thực thi toàn bộ luồng trả hàng trong 1 transaction duy nhất.
     */
    public boolean luuHoaDonTraHang(HoaDon hoaDonTra, List<SuPhanBoLo> dsPhanBoTra) {
        return hoaDonDAO.luuHoaDonTraHang(hoaDonTra, dsPhanBoTra);
    }

    /**
     * Hủy hóa đơn (xóa chuỗi SuPhanBoLo → ChiTietHoaDon → HoaDon trong 1 transaction).
     */
    public boolean huyHoaDon(String maHD) {
        return hoaDonDAO.huyHoaDon(maHD);
    }

    public boolean them(HoaDon hd) {
        return hoaDonDAO.them(hd);
    }

    public boolean capNhat(HoaDon hd) {
        return hoaDonDAO.capNhat(hd);
    }

    public boolean xoa(String maHD) {
        return hoaDonDAO.xoa(maHD);
    }

    /**
     * Lấy danh sách phân bổ lô tương ứng để hoàn trả kho khi trả hàng.
     * Di chuyển raw SQL từ TraHangPanel — tầng UI không được viết SQL.
     */
    public List<SuPhanBoLo> layDanhSachPhanBoLoCanTra(String maHoaDonGoc,
                                                       List<ChiTietHoaDon> dsChiTietTra) {
        return hoaDonDAO.layDanhSachPhanBoLoCanTra(maHoaDonGoc, dsChiTietTra);
    }

    // ==================== THỐNG KÊ ====================

    /**
     * Tính tổng doanh thu của một ca làm việc.
     * Di chuyển từ HoaDonDAO (đây là nghiệp vụ thống kê, không phải CRUD thuần).
     */
    public double tinhTongDoanhThuCa(String maCa) {
        return hoaDonDAO.tinhTongDoanhThuCa(maCa);
    }
}
