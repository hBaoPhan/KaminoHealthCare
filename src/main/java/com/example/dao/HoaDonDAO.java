package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.*;

import java.sql.*;
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

            while (ketQua.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                hd.setKhachHang(new KhachHang(ketQua.getString("maKhachHang")));
                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null) hd.setKhuyenMai(new KhuyenMai(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null) hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null) hd.setDonThuoc(new DonThuoc(maDT));
                
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

            if (ketQua.next()) {
                hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                hd.setKhachHang(new KhachHang(ketQua.getString("maKhachHang")));
                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null) hd.setKhuyenMai(new KhuyenMai(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null) hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null) hd.setDonThuoc(new DonThuoc(maDT));
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
            String truyVan = "INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, hd.getMaHoaDon());
            lenh.setTimestamp(2, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(3, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(4, hd.isTrangThaiThanhToan());
            lenh.setString(5, hd.getKhachHang().getMaKhachHang());
            lenh.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(7, hd.getLoaiHoaDon().name());
            lenh.setString(8, hd.getCa().getMaCa());
            lenh.setString(9, hd.getGhiChu());
            lenh.setString(10, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(11, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
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
            String truyVan = "UPDATE HoaDon SET thoiGianTao = ?, maNhanVien = ?, trangThaiThanhToan = ?, maKhachHang = ?, maKhuyenMai = ?, loaiHoaDon = ?, maCa = ?, ghiChu = ?, maHoaDonDoiTra = ?, maDonThuoc = ? WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setTimestamp(1, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(2, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(3, hd.isTrangThaiThanhToan());
            lenh.setString(4, hd.getKhachHang().getMaKhachHang());
            lenh.setString(5, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(6, hd.getLoaiHoaDon().name());
            lenh.setString(7, hd.getCa().getMaCa());
            lenh.setString(8, hd.getGhiChu());
            lenh.setString(9, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(10, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
            lenh.setString(11, hd.getMaHoaDon());
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
}
