package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.entity.enums.ChucVu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    public List<TaiKhoan> layTatCa() {
        List<TaiKhoan> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            // Tối ưu: Dùng JOIN để lấy luôn thông tin nhân viên
            String truyVan = "SELECT tk.*, nv.tenNhanVien, nv.chucVu FROM TaiKhoan tk " +
                             "JOIN NhanVien nv ON tk.maNhanVien = nv.maNhanVien";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                TaiKhoan tk = parseTaiKhoan(ketQua);
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
            // Tối ưu: Lấy thông tin nhân viên đi kèm để tránh NullPointerException ở GUI
            String truyVan = "SELECT tk.*, nv.tenNhanVien, nv.chucVu FROM TaiKhoan tk " +
                             "JOIN NhanVien nv ON tk.maNhanVien = nv.maNhanVien " +
                             "WHERE tk.tenDangNhap = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tenDN);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                tk = parseTaiKhoan(ketQua);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tk;
    }

    /**
     * Hàm hỗ trợ chuyển dữ liệu từ ResultSet sang đối tượng TaiKhoan
     * Đảm bảo NhanVien không bị null
     */
    private TaiKhoan parseTaiKhoan(ResultSet rs) throws SQLException {
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(rs.getString("tenDangNhap"));
        tk.setMatKhau(rs.getString("matKhau"));
        
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(rs.getString("maNhanVien"));
        nv.setTenNhanVien(rs.getString("tenNhanVien"));
        // Chuyển đổi String từ DB sang Enum ChucVu
        nv.setChucVu(ChucVu.valueOf(rs.getString("chucVu"))); 
        
        tk.setNhanVien(nv);
        return tk;
    }

    public boolean them(TaiKhoan tk) {
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNhanVien) VALUES (?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tk.getTenDangNhap());
            lenh.setString(2, tk.getMatKhau());
            lenh.setString(3, tk.getNhanVien().getMaNhanVien());
            return lenh.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(TaiKhoan tk) {
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE TaiKhoan SET matKhau = ? WHERE tenDangNhap = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tk.getMatKhau());
            lenh.setString(2, tk.getTenDangNhap());
            return lenh.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String tenDN) {
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM TaiKhoan WHERE tenDangNhap = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, tenDN);
            return lenh.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}