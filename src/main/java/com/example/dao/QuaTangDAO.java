package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.KhuyenMai;
import com.example.entity.QuaTang;
import com.example.entity.DonViQuyDoi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuaTangDAO {

    public List<QuaTang> layTatCa() {
        List<QuaTang> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM QuaTang";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            DonViQuyDoiDAO dvDAO = new DonViQuyDoiDAO();
            while (ketQua.next()) {
                QuaTang qt = new QuaTang();
                qt.setKhuyenMai(new KhuyenMai(ketQua.getString("maKhuyenMai")));
                qt.setDonViQuyDoi(dvDAO.timTheoMa(ketQua.getString("maDonVi")));
                qt.setSoLuongTang(ketQua.getInt("soLuongTang"));
                danhSach.add(qt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public List<QuaTang> timTheoKhuyenMai(String maKM) {
        List<QuaTang> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM QuaTang WHERE maKhuyenMai = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maKM);
            ResultSet ketQua = lenh.executeQuery();

            DonViQuyDoiDAO dvDAO = new DonViQuyDoiDAO();
            while (ketQua.next()) {
                QuaTang qt = new QuaTang();
                qt.setKhuyenMai(new KhuyenMai(ketQua.getString("maKhuyenMai")));
                qt.setDonViQuyDoi(dvDAO.timTheoMa(ketQua.getString("maDonVi")));
                qt.setSoLuongTang(ketQua.getInt("soLuongTang"));
                danhSach.add(qt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public boolean them(QuaTang qt) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO QuaTang VALUES (?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, qt.getKhuyenMai().getMaKhuyenMai());
            lenh.setString(2, qt.getDonViQuyDoi().getMaDonVi());
            lenh.setInt(3, qt.getSoLuongTang());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maKM, String maDonVi) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM QuaTang WHERE maKhuyenMai = ? AND maDonVi = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maKM);
            lenh.setString(2, maDonVi);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoaTheoKhuyenMai(String maKM) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM QuaTang WHERE maKhuyenMai = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maKM);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(QuaTang qt) {
        // Vì QuaTang không có ID riêng, ta xóa cũ thêm mới hoặc update dựa trên maKM
        // Tuy nhiên, logic thường là 1 KM TẶNG KÈM chỉ có 1 sản phẩm tặng kèm (theo thiết kế hiện tại)
        xoaTheoKhuyenMai(qt.getKhuyenMai().getMaKhuyenMai());
        return them(qt);
    }
}
