package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.enums.DonVi;
import com.example.entity.DonViQuyDoi;
import com.example.entity.SanPham;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonViQuyDoiDAO {

    public List<DonViQuyDoi> layTatCa() {
        List<DonViQuyDoi> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM DonViQuyDoi";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            SanPhamDAO spDAO = new SanPhamDAO();
            while (ketQua.next()) {
                DonViQuyDoi dv = new DonViQuyDoi();
                dv.setMaDonVi(ketQua.getString("maDonVi"));
                dv.setTenDonVi(DonVi.valueOf(ketQua.getString("tenDonVi")));
                dv.setHeSoQuyDoi(ketQua.getInt("heSoQuyDoi"));
                dv.setSanPham(spDAO.timTheoMa(ketQua.getString("maSanPham")));
                danhSach.add(dv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public DonViQuyDoi timTheoMa(String maDV) {
        DonViQuyDoi dv = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM DonViQuyDoi WHERE maDonVi = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maDV);
            ResultSet ketQua = lenh.executeQuery();

            SanPhamDAO spDAO = new SanPhamDAO();
            if (ketQua.next()) {
                dv = new DonViQuyDoi();
                dv.setMaDonVi(ketQua.getString("maDonVi"));
                dv.setTenDonVi(DonVi.valueOf(ketQua.getString("tenDonVi")));
                dv.setHeSoQuyDoi(ketQua.getInt("heSoQuyDoi"));
                dv.setSanPham(spDAO.timTheoMa(ketQua.getString("maSanPham")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dv;
    }

    public boolean them(DonViQuyDoi dv) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO DonViQuyDoi VALUES (?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, dv.getMaDonVi());
            lenh.setString(2, dv.getTenDonVi().name());
            lenh.setInt(3, dv.getHeSoQuyDoi());
            lenh.setString(4, dv.getSanPham().getMaSanPham());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(DonViQuyDoi dv) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE DonViQuyDoi SET tenDonVi = ?, heSoQuyDoi = ?, maSanPham = ? WHERE maDonVi = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, dv.getTenDonVi().name());
            lenh.setInt(2, dv.getHeSoQuyDoi());
            lenh.setString(3, dv.getSanPham().getMaSanPham());
            lenh.setString(4, dv.getMaDonVi());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maDV) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM DonViQuyDoi WHERE maDonVi = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maDV);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public List<DonViQuyDoi> timTheoMaSanPham(String maSanPham) {
        List<DonViQuyDoi> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM DonViQuyDoi WHERE maSanPham = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maSanPham);
            ResultSet ketQua = lenh.executeQuery();

            SanPhamDAO spDAO = new SanPhamDAO();
            while (ketQua.next()) {
                DonViQuyDoi dv = new DonViQuyDoi();
                dv.setMaDonVi(ketQua.getString("maDonVi"));
                dv.setTenDonVi(DonVi.valueOf(ketQua.getString("tenDonVi")));
                dv.setHeSoQuyDoi(ketQua.getInt("heSoQuyDoi"));
                dv.setSanPham(spDAO.timTheoMa(ketQua.getString("maSanPham")));
                danhSach.add(dv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}
