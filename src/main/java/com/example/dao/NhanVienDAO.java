package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.ChucVu;
import com.example.entity.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    
    public List<NhanVien> layTatCa() {
        List<NhanVien> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM NhanVien";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);
            
            while (ketQua.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(ketQua.getString("maNhanVien"));
                nv.setTenNhanVien(ketQua.getString("tenNhanVien"));
                nv.setCccd(ketQua.getString("cccd"));
                nv.setSdt(ketQua.getString("sdt"));
                nv.setChucVu(ChucVu.valueOf(ketQua.getString("chucVu")));
                nv.setTrangThai(ketQua.getBoolean("trangThai"));
                danhSach.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public NhanVien timTheoMa(String maNV) {
        NhanVien nv = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM NhanVien WHERE maNhanVien = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maNV);
            ResultSet ketQua = lenh.executeQuery();
            
            if (ketQua.next()) {
                nv = new NhanVien();
                nv.setMaNhanVien(ketQua.getString("maNhanVien"));
                nv.setTenNhanVien(ketQua.getString("tenNhanVien"));
                nv.setCccd(ketQua.getString("cccd"));
                nv.setSdt(ketQua.getString("sdt"));
                nv.setChucVu(ChucVu.valueOf(ketQua.getString("chucVu")));
                nv.setTrangThai(ketQua.getBoolean("trangThai"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nv;
    }

    public boolean them(NhanVien nv) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO NhanVien VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, nv.getMaNhanVien());
            lenh.setString(2, nv.getTenNhanVien());
            lenh.setString(3, nv.getCccd());
            lenh.setString(4, nv.getSdt());
            lenh.setString(5, nv.getChucVu().name());
            lenh.setBoolean(6, nv.isTrangThai());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(NhanVien nv) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE NhanVien SET tenNhanVien = ?, cccd = ?, sdt = ?, chucVu = ?, trangThai = ? WHERE maNhanVien = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, nv.getTenNhanVien());
            lenh.setString(2, nv.getCccd());
            lenh.setString(3, nv.getSdt());
            lenh.setString(4, nv.getChucVu().name());
            lenh.setBoolean(5, nv.isTrangThai());
            lenh.setString(6, nv.getMaNhanVien());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maNV) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM NhanVien WHERE maNhanVien = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maNV);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}
