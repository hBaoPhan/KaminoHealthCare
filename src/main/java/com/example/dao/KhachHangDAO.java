package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.KhachHang;
import com.example.entity.enums.TrangThaiKhachHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> layTatCa() {
        List<KhachHang> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM KhachHang";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(ketQua.getString("maKhachHang"));
                kh.setTenKhachHang(ketQua.getString("tenKhachHang"));
                kh.setSdt(ketQua.getString("sdt"));
                kh.setTrangThai(TrangThaiKhachHang.valueOf(ketQua.getString("trangThaiKhachHang")));
                danhSach.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public KhachHang timTheoMa(String maKH) {
        KhachHang kh = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM KhachHang WHERE maKhachHang = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maKH);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                kh = new KhachHang();
                kh.setMaKhachHang(ketQua.getString("maKhachHang"));
                kh.setTenKhachHang(ketQua.getString("tenKhachHang"));
                kh.setSdt(ketQua.getString("sdt"));
                String trangThaiStr = ketQua.getString("trangThaiKhachHang");
                if (trangThaiStr != null) {
                    kh.setTrangThai(TrangThaiKhachHang.valueOf(trangThaiStr));
                } else {
                    // Gán giá trị mặc định nếu cần
                    kh.setTrangThai(TrangThaiKhachHang.KHACH_LE); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kh;
    }

    public boolean them(KhachHang kh) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO KhachHang VALUES (?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, kh.getMaKhachHang());
            lenh.setString(2, kh.getTenKhachHang());
            lenh.setString(3, kh.getSdt());
            lenh.setString(4, kh.getTrangThai().name());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(KhachHang kh) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE KhachHang SET tenKhachHang = ?, sdt = ?, trangThaiKhachHang = ? WHERE maKhachHang = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, kh.getTenKhachHang());
            lenh.setString(2, kh.getSdt());
            lenh.setString(3, kh.getTrangThai().name());
            lenh.setString(4, kh.getMaKhachHang());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public KhachHang timTheoSdt(String sdt) {
        KhachHang kh = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM KhachHang WHERE sdt = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, sdt);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                kh = new KhachHang();
                kh.setMaKhachHang(ketQua.getString("maKhachHang"));
                kh.setTenKhachHang(ketQua.getString("tenKhachHang"));
                kh.setSdt(ketQua.getString("sdt"));
                kh.setTrangThai(TrangThaiKhachHang.valueOf(ketQua.getString("trangThaiKhachHang")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kh;
    }

    public boolean xoa(String maKH) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM KhachHang WHERE maKhachHang = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maKH);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}
