package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    public List<TaiKhoan> layTatCa() {
        List<TaiKhoan> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM TaiKhoan";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setTenDangNhap(ketQua.getString("tenDangNhap"));
                tk.setMatKhau(ketQua.getString("matKhau"));
                tk.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                danhSach.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public TaiKhoan timTheoMa(String tenDN) {
        TaiKhoan tk = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tenDN);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                tk = new TaiKhoan();
                tk.setTenDangNhap(ketQua.getString("tenDangNhap"));
                tk.setMatKhau(ketQua.getString("matKhau"));
                tk.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tk;
    }

    public boolean them(TaiKhoan tk) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO TaiKhoan VALUES (?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tk.getTenDangNhap());
            lenh.setString(2, tk.getMatKhau());
            lenh.setString(3, tk.getNhanVien().getMaNhanVien());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(TaiKhoan tk) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tk.getMatKhau());
            lenh.setString(2, tk.getTenDangNhap());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String tenDN) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM TaiKhoan WHERE tenDangNhap = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tenDN);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}
