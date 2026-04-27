package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.Lo;
import com.example.entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoDAO {

    public List<Lo> layTatCa() {
        List<Lo> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM Lo";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                Lo lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
                danhSach.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public Lo timTheoMa(String maLo) {
        Lo lo = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM Lo WHERE maLo = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maLo);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lo;
    }

    public boolean them(Lo lo) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO Lo VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, lo.getMaLo());
            lenh.setString(2, lo.getSoLo());
            lenh.setDate(3, Date.valueOf(lo.getNgayHetHan()));
            lenh.setInt(4, lo.getSoLuongSanPham());
            lenh.setString(5, lo.getSanPham().getMaSanPham());
            lenh.setDouble(6, lo.getGiaNhap());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(Lo lo) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE Lo SET soLo = ?, ngayHetHan = ?, soLuongSanPham = ?, maSanPham = ?, giaNhap = ? WHERE maLo = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, lo.getSoLo());
            lenh.setDate(2, Date.valueOf(lo.getNgayHetHan()));
            lenh.setInt(3, lo.getSoLuongSanPham());
            lenh.setString(4, lo.getSanPham().getMaSanPham());
            lenh.setDouble(5, lo.getGiaNhap());
            lenh.setString(6, lo.getMaLo());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maLo) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM Lo WHERE maLo = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maLo);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    /**
     * Lấy danh sách các Lô của một Đơn vị quy đổi (hoặc Sản phẩm)
     * còn hạn sử dụng và số lượng > 0, sắp xếp theo Hạn sử dụng tăng dần (FEFO)
     */
    public List<Lo> layDanhSachLoKhaDung(String maDonViQuyDoi) {
        List<Lo> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT l.* FROM Lo l INNER JOIN DonViQuyDoi dv ON l.maSanPham = dv.maSanPham " +
                             "WHERE dv.maDonVi = ? AND l.soLuongSanPham > 0 AND l.ngayHetHan > GETDATE() " +
                             "ORDER BY l.ngayHetHan ASC";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maDonViQuyDoi);
            ResultSet ketQua = lenh.executeQuery();

            while (ketQua.next()) {
                Lo lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
                danhSach.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}