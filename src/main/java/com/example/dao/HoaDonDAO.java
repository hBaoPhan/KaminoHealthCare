package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.CaLam;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.DonThuoc;
import com.example.entity.HoaDon;
import com.example.entity.Lo;
import com.example.entity.SuPhanBoLo;
import com.example.entity.enums.LoaiHoaDon;
import com.example.entity.enums.PhuongThucThanhToan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> layTatCa() {
        List<HoaDon> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM HoaDon";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            while (ketQua.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(nvDAO.timTheoMa(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                
                // CẬP NHẬT: Kiểm tra null trước khi tìm khách hàng
                String maKH = ketQua.getString("maKhachHang");
                if (maKH != null && !maKH.trim().isEmpty()) {
                    hd.setKhachHang(khDAO.timTheoMa(maKH));
                }

                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null)
                    hd.setKhuyenMai(kmDAO.timTheoMa(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null)
                    hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null)
                    hd.setDonThuoc(new DonThuoc(maDT));
                String pttt = ketQua.getString("phuongThucThanhToan");
                if (pttt != null)
                    hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));

                danhSach.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public HoaDon timTheoMa(String maHD) {
        HoaDon hd = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM HoaDon WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maHD);
            ResultSet ketQua = lenh.executeQuery();

            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            if (ketQua.next()) {
                hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(nvDAO.timTheoMa(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                
                // CẬP NHẬT: Kiểm tra null trước khi tìm khách hàng
                String maKH = ketQua.getString("maKhachHang");
                if (maKH != null && !maKH.trim().isEmpty()) {
                    hd.setKhachHang(khDAO.timTheoMa(maKH));
                }

                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null)
                    hd.setKhuyenMai(kmDAO.timTheoMa(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null)
                    hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null)
                    hd.setDonThuoc(new DonThuoc(maDT));
                String pttt = ketQua.getString("phuongThucThanhToan");
                if (pttt != null)
                    hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hd;
    }

    public boolean them(HoaDon hd) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc, phuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, hd.getMaHoaDon());
            lenh.setTimestamp(2, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(3, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(4, hd.isTrangThaiThanhToan());
            
            // Đảm bảo không bị lỗi NullPointer khi getKhachHang() bị null
            lenh.setString(5, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            
            lenh.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(7, hd.getLoaiHoaDon().name());
            lenh.setString(8, hd.getCa().getMaCa());
            lenh.setString(9, hd.getGhiChu());
            lenh.setString(10, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(11, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
            lenh.setString(12, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan().name() : null);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(HoaDon hd) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE HoaDon SET thoiGianTao = ?, maNhanVien = ?, trangThaiThanhToan = ?, maKhachHang = ?, maKhuyenMai = ?, loaiHoaDon = ?, maCa = ?, ghiChu = ?, maHoaDonDoiTra = ?, maDonThuoc = ?, phuongThucThanhToan = ? WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setTimestamp(1, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(2, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(3, hd.isTrangThaiThanhToan());
            
            // Đảm bảo không bị lỗi NullPointer khi getKhachHang() bị null
            lenh.setString(4, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            
            lenh.setString(5, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(6, hd.getLoaiHoaDon().name());
            lenh.setString(7, hd.getCa().getMaCa());
            lenh.setString(8, hd.getGhiChu());
            lenh.setString(9, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(10, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
            lenh.setString(11, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan().name() : null);
            lenh.setString(12, hd.getMaHoaDon());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maHD) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM HoaDon WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maHD);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public List<HoaDon> timKiem(String maHD, LocalDate ngayTao) {
        List<HoaDon> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            StringBuilder truyVan = new StringBuilder("SELECT * FROM HoaDon WHERE 1=1");
            if (maHD != null && !maHD.trim().isEmpty()) {
                truyVan.append(" AND maHoaDon LIKE ?");
            }
            if (ngayTao != null) {
                truyVan.append(" AND thoiGianTao >= ? AND thoiGianTao < ?");
            }

            PreparedStatement lenh = ketNoi.prepareStatement(truyVan.toString());
            int paramIndex = 1;
            if (maHD != null && !maHD.trim().isEmpty()) {
                lenh.setString(paramIndex++, "%" + maHD.trim() + "%");
            }
            if (ngayTao != null) {
                lenh.setTimestamp(paramIndex++, Timestamp.valueOf(ngayTao.atStartOfDay()));
                lenh.setTimestamp(paramIndex++, Timestamp.valueOf(ngayTao.plusDays(1).atStartOfDay()));
            }

            ResultSet ketQua = lenh.executeQuery();
            ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            while (ketQua.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(nvDAO.timTheoMa(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                
                // CẬP NHẬT: Kiểm tra null trước khi tìm khách hàng
                String maKH = ketQua.getString("maKhachHang");
                if (maKH != null && !maKH.trim().isEmpty()) {
                    hd.setKhachHang(khDAO.timTheoMa(maKH));
                }

                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null)
                    hd.setKhuyenMai(kmDAO.timTheoMa(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null)
                    hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null)
                    hd.setDonThuoc(new DonThuoc(maDT));
                String pttt = ketQua.getString("phuongThucThanhToan");
                if (pttt != null)
                    hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));

                hd.setDsChiTiet(ctDAO.layTheoMaHoaDon(hd.getMaHoaDon()));
                danhSach.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}