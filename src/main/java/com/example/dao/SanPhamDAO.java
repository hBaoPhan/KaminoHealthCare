package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.enums.PhanLoai;
import com.example.entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPham> layTatCa() {
        List<SanPham> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM SanPham";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                SanPham sp = new SanPham();
                sp.setMaSanPham(ketQua.getString("maSanPham"));
                sp.setTenSanPham(ketQua.getString("tenSanPham"));
                sp.setPhanLoai(PhanLoai.valueOf(ketQua.getString("phanLoai")));
                sp.setSoLuongTon(ketQua.getInt("soLuongTon"));
                sp.setMoTa(ketQua.getString("moTa"));
                sp.setHoatChat(ketQua.getString("hoatChat"));
                sp.setDonGiaCoBan(ketQua.getDouble("donGiaCoBan"));
                sp.setTrangThaiKinhDoanh(ketQua.getBoolean("trangThaiKinhDoanh"));
                sp.setThue(ketQua.getDouble("thue"));
                danhSach.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public SanPham timTheoMa(String maSP) {
        SanPham sp = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM SanPham WHERE maSanPham = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maSP);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                sp = new SanPham();
                sp.setMaSanPham(ketQua.getString("maSanPham"));
                sp.setTenSanPham(ketQua.getString("tenSanPham"));
                sp.setPhanLoai(PhanLoai.valueOf(ketQua.getString("phanLoai")));
                sp.setSoLuongTon(ketQua.getInt("soLuongTon"));
                sp.setMoTa(ketQua.getString("moTa"));
                sp.setHoatChat(ketQua.getString("hoatChat"));
                sp.setDonGiaCoBan(ketQua.getDouble("donGiaCoBan"));
                sp.setTrangThaiKinhDoanh(ketQua.getBoolean("trangThaiKinhDoanh"));
                sp.setThue(ketQua.getDouble("thue"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sp;
    }

    public boolean them(SanPham sp) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO SanPham VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, sp.getMaSanPham());
            lenh.setString(2, sp.getTenSanPham());
            lenh.setString(3, sp.getPhanLoai().name());
            lenh.setInt(4, sp.getSoLuongTon());
            lenh.setString(5, sp.getMoTa());
            lenh.setString(6, sp.getHoatChat());
            lenh.setDouble(7, sp.getDonGiaCoBan());
            lenh.setBoolean(8, sp.isTrangThaiKinhDoanh());
            lenh.setDouble(9, sp.getThue());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(SanPham sp) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE SanPham SET tenSanPham = ?, phanLoai = ?, soLuongTon = ?, moTa = ?, hoatChat = ?, donGiaCoBan = ?, trangThaiKinhDoanh = ?, thue = ? WHERE maSanPham = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, sp.getTenSanPham());
            lenh.setString(2, sp.getPhanLoai().name());
            lenh.setInt(3, sp.getSoLuongTon());
            lenh.setString(4, sp.getMoTa());
            lenh.setString(5, sp.getHoatChat());
            lenh.setDouble(6, sp.getDonGiaCoBan());
            lenh.setBoolean(7, sp.isTrangThaiKinhDoanh());
            lenh.setDouble(8, sp.getThue());
            lenh.setString(9, sp.getMaSanPham());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maSP) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM SanPham WHERE maSanPham = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maSP);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public List<SanPham> timTheoMaHoacTen(String tuKhoa) {
        List<SanPham> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM SanPham WHERE maSanPham LIKE ? OR tenSanPham LIKE ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, "%" + tuKhoa + "%");
            lenh.setString(2, "%" + tuKhoa + "%");
            ResultSet ketQua = lenh.executeQuery();

            while (ketQua.next()) {
                SanPham sp = new SanPham();
                sp.setMaSanPham(ketQua.getString("maSanPham"));
                sp.setTenSanPham(ketQua.getString("tenSanPham"));
                sp.setPhanLoai(PhanLoai.valueOf(ketQua.getString("phanLoai")));
                sp.setSoLuongTon(ketQua.getInt("soLuongTon"));
                sp.setMoTa(ketQua.getString("moTa"));
                sp.setHoatChat(ketQua.getString("hoatChat"));
                sp.setDonGiaCoBan(ketQua.getDouble("donGiaCoBan"));
                sp.setTrangThaiKinhDoanh(ketQua.getBoolean("trangThaiKinhDoanh"));
                sp.setThue(ketQua.getDouble("thue"));
                danhSach.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}
