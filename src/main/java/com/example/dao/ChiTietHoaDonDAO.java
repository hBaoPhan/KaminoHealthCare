package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    public List<ChiTietHoaDon> layTheoMaHoaDon(String maHD) {
        List<ChiTietHoaDon> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maHD);
            ResultSet ketQua = lenh.executeQuery();

            while (ketQua.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setHoaDon(new HoaDon(ketQua.getString("maHoaDon")));
                ct.setDonViQuyDoi(new DonViQuyDoi(ketQua.getString("maDonViQuyDoi")));
                ct.setSoLuong(ketQua.getInt("soLuong"));
                ct.setDonGia(ketQua.getDouble("donGia"));
                ct.setLaQuaTangKem(ketQua.getBoolean("laQuaTangKem"));
                String maLo = ketQua.getString("maLo");
                if (maLo != null) ct.setLo(new Lo(maLo));
                danhSach.add(ct);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public boolean them(ChiTietHoaDon ct) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO ChiTietHoaDon VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, ct.getHoaDon().getMaHoaDon());
            lenh.setString(2, ct.getDonViQuyDoi().getMaDonVi());
            lenh.setInt(3, ct.getSoLuong());
            lenh.setDouble(4, ct.getDonGia());
            lenh.setBoolean(5, ct.isLaQuaTangKem());
            lenh.setString(6, ct.getLo() != null ? ct.getLo().getMaLo() : null);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(ChiTietHoaDon ct) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE ChiTietHoaDon SET soLuong = ?, donGia = ?, laQuaTangKem = ?, maLo = ? WHERE maHoaDon = ? AND maDonViQuyDoi = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setInt(1, ct.getSoLuong());
            lenh.setDouble(2, ct.getDonGia());
            lenh.setBoolean(3, ct.isLaQuaTangKem());
            lenh.setString(4, ct.getLo() != null ? ct.getLo().getMaLo() : null);
            lenh.setString(5, ct.getHoaDon().getMaHoaDon());
            lenh.setString(6, ct.getDonViQuyDoi().getMaDonVi());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maHD, String maDV) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ? AND maDonViQuyDoi = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maHD);
            lenh.setString(2, maDV);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}
