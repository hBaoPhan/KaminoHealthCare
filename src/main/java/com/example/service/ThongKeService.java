package com.example.service;

import com.example.dao.ThongKeDAO;
import com.example.entity.HoaDon;

import java.time.LocalDate;
import java.util.List;

/**
 * Service xử lý nghiệp vụ liên quan đến Thống kê.
 */
public class ThongKeService {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();

    /**
     * Lấy danh sách hóa đơn đã thanh toán hoàn tất trong khoảng ngày.
     *
     * @param tuNgay  Từ ngày
     * @param denNgay Đến ngày
     * @return Danh sách hóa đơn
     */
    public List<HoaDon> layHoaDonTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        return thongKeDAO.layHoaDonTheoKhoangNgay(tuNgay, denNgay);
    }

    /**
     * Lấy danh sách các lô sản phẩm sắp hết hạn sử dụng (trong vòng 30 ngày).
     *
     * @return Danh sách mảng Object chứa thông tin lô cận hạn
     */
    public List<Object[]> layLoSapHetHan() {
        return thongKeDAO.layLoSapHetHan();
    }

    /**
     * Lấy danh sách sản phẩm tồn kho lâu chưa có giao dịch bán hàng phát sinh.
     *
     * @return Danh sách mảng Object chứa thông tin tồn lâu
     */
    public List<Object[]> laySanPhamTonKhoLau() {
        return thongKeDAO.laySanPhamTonKhoLau();
    }

    /**
     * Lấy danh sách các ngày đăng ký thành viên của khách hàng.
     *
     * @return Danh sách mảng Object chứa thông tin đăng ký thành viên
     */
    public List<Object[]> layNgayDangKyKhachHang() {
        return thongKeDAO.layNgayDangKyKhachHang();
    }
}
