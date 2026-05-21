package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.*;
import com.example.entity.enums.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ThongKeDAO {

    private HoaDon mapHoaDon(ResultSet rs, NhanVienDAO nvDAO, KhachHangDAO khDAO, KhuyenMaiDAO kmDAO)
            throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(rs.getString("maHoaDon"));
        hd.setThoiGianTao(rs.getTimestamp("thoiGianTao").toLocalDateTime());
        hd.setNhanVien(nvDAO.timTheoMa(rs.getString("maNhanVien")));
        hd.setTrangThaiThanhToan(rs.getBoolean("trangThaiThanhToan"));

        String maKH = rs.getString("maKhachHang");
        if (maKH != null && !maKH.trim().isEmpty())
            hd.setKhachHang(khDAO.timTheoMa(maKH));

        String maKM = rs.getString("maKhuyenMai");
        if (maKM != null)
            hd.setKhuyenMai(kmDAO.timTheoMa(maKM));

        hd.setLoaiHoaDon(LoaiHoaDon.valueOf(rs.getString("loaiHoaDon")));
        hd.setCa(new CaLam(rs.getString("maCa")));
        hd.setGhiChu(rs.getString("ghiChu"));

        String maHDDT = rs.getString("maHoaDonDoiTra");
        if (maHDDT != null)
            hd.setHoaDonDoiTra(new HoaDon(maHDDT));

        String maDT = rs.getString("maDonThuoc");
        if (maDT != null)
            hd.setDonThuoc(new DonThuoc(maDT));

        String pttt = rs.getString("phuongThucThanhToan");
        if (pttt != null)
            hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));

        return hd;
    }

    public List<HoaDon> layHoaDonTheoKhoangNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<HoaDon> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String sql = "SELECT * FROM HoaDon WHERE thoiGianTao >= ? AND thoiGianTao < ? AND trangThaiThanhToan = 1 ORDER BY thoiGianTao ASC";
            PreparedStatement lenh = ketNoi.prepareStatement(sql);

            lenh.setTimestamp(1, Timestamp.valueOf(tuNgay.atStartOfDay()));
            lenh.setTimestamp(2, Timestamp.valueOf(denNgay.plusDays(1).atStartOfDay()));

            ResultSet ketQua = lenh.executeQuery();
            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();

            while (ketQua.next()) {
                HoaDon hd = mapHoaDon(ketQua, nvDAO, khDAO, kmDAO);
                hd.setDsChiTiet(ctDAO.layTheoMaHoaDon(hd.getMaHoaDon()));
                danhSach.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public List<Object[]> layLoSapHetHan() {
        List<Object[]> ds = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT l.maLo, l.soLo, sp.maSanPham, sp.tenSanPham, l.soLuongSanPham, l.ngayHetHan " +
                    "FROM Lo l " +
                    "JOIN SanPham sp ON l.maSanPham = sp.maSanPham " +
                    "WHERE l.soLuongSanPham > 0 " +
                    "AND l.ngayHetHan >= DATEADD(day, 7, CAST(GETDATE() AS DATE)) " +
                    "AND l.ngayHetHan <= DATEADD(month, 1, CAST(GETDATE() AS DATE)) " +
                    "ORDER BY l.ngayHetHan ASC";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ds.add(new Object[] {
                        rs.getString("maLo"),
                        rs.getString("soLo"),
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getInt("soLuongSanPham"),
                        rs.getDate("ngayHetHan").toLocalDate()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<Object[]> laySanPhamTonKhoLau() {
        List<Object[]> ds = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT sp.maSanPham, sp.tenSanPham, sp.loaiSanPham, sp.soLuongTon, " +
                    "       MAX(hd.thoiGianTao) as lanBanCuoi, " +
                    "       DATEDIFF(day, COALESCE(MAX(hd.thoiGianTao), '2000-01-01'), GETDATE()) as soNgay " +
                    "FROM SanPham sp " +
                    "LEFT JOIN DonViQuyDoi dv ON sp.maSanPham = dv.maSanPham " +
                    "LEFT JOIN ChiTietHoaDon ct ON dv.maDonVi = ct.maDonVi " +
                    "LEFT JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon AND hd.trangThaiThanhToan = 1 AND hd.loaiHoaDon IN ('BAN_HANG', 'DOI_HANG') "
                    +
                    "WHERE sp.soLuongTon > 0 AND sp.trangThaiKinhDoanh = 1 " +
                    "GROUP BY sp.maSanPham, sp.tenSanPham, sp.loaiSanPham, sp.soLuongTon " +
                    "ORDER BY soNgay DESC, sp.soLuongTon DESC";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDate lbc = null;
                Timestamp ts = rs.getTimestamp("lanBanCuoi");
                if (ts != null) {
                    lbc = ts.toLocalDateTime().toLocalDate();
                }
                ds.add(new Object[] {
                        rs.getString("maSanPham"),
                        rs.getString("tenSanPham"),
                        rs.getString("loaiSanPham"),
                        rs.getInt("soLuongTon"),
                        lbc,
                        rs.getObject("lanBanCuoi") != null ? rs.getInt("soNgay") : -1
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<Object[]> layNgayDangKyKhachHang() {
        List<Object[]> ds = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT maKhachHang, MIN(thoiGianTao) as ngayDk " +
                    "FROM HoaDon " +
                    "WHERE maKhachHang IS NOT NULL AND maKhachHang != '' AND trangThaiThanhToan = 1 " +
                    "GROUP BY maKhachHang";
            PreparedStatement stmt = con.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ds.add(new Object[] {
                        rs.getString("maKhachHang"),
                        rs.getTimestamp("ngayDk").toLocalDateTime()
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
