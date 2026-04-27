package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.KhuyenMai;
import com.example.entity.enums.LoaiKhuyenMai;
import com.example.entity.QuaTang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    private final QuaTangDAO quaTangDAO = new QuaTangDAO();

    public List<KhuyenMai> layTatCa() {
        List<KhuyenMai> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM KhuyenMai";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                KhuyenMai km = new KhuyenMai();
                km.setMaKhuyenMai(ketQua.getString("maKhuyenMai"));
                km.setTenKhuyenMai(ketQua.getString("tenKhuyenMai"));
                km.setThoiGianBatDau(ketQua.getTimestamp("thoiGianBatDau").toLocalDateTime());
                km.setThoiGianKetThuc(ketQua.getTimestamp("thoiGianKetThuc").toLocalDateTime());
                km.setLoaiKhuyenMai(LoaiKhuyenMai.valueOf(ketQua.getString("loaiKhuyenMai")));
                km.setKhuyenMaiPhanTram(ketQua.getDouble("khuyenMaiPhanTram"));
                km.setGiaTriDonHangToiThieu(ketQua.getDouble("giaTriDonHangToiThieu"));
                // Load quaTangKem nếu có
                List<QuaTang> dsQt = quaTangDAO.timTheoKhuyenMai(km.getMaKhuyenMai());
                if (!dsQt.isEmpty()) km.setQuaTangKem(dsQt.get(0));
                danhSach.add(km);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public KhuyenMai timTheoMa(String maKM) {
        KhuyenMai km = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maKM);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                km = new KhuyenMai();
                km.setMaKhuyenMai(ketQua.getString("maKhuyenMai"));
                km.setTenKhuyenMai(ketQua.getString("tenKhuyenMai"));
                km.setThoiGianBatDau(ketQua.getTimestamp("thoiGianBatDau").toLocalDateTime());
                km.setThoiGianKetThuc(ketQua.getTimestamp("thoiGianKetThuc").toLocalDateTime());
                km.setLoaiKhuyenMai(LoaiKhuyenMai.valueOf(ketQua.getString("loaiKhuyenMai")));
                km.setKhuyenMaiPhanTram(ketQua.getDouble("khuyenMaiPhanTram"));
                km.setGiaTriDonHangToiThieu(ketQua.getDouble("giaTriDonHangToiThieu"));
                // Load quaTangKem nếu có
                List<QuaTang> dsQt = quaTangDAO.timTheoKhuyenMai(km.getMaKhuyenMai());
                if (!dsQt.isEmpty()) km.setQuaTangKem(dsQt.get(0));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return km;
    }

    public boolean them(KhuyenMai km) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO KhuyenMai VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, km.getMaKhuyenMai());
            lenh.setString(2, km.getTenKhuyenMai());
            lenh.setTimestamp(3, Timestamp.valueOf(km.getThoiGianBatDau()));
            lenh.setTimestamp(4, Timestamp.valueOf(km.getThoiGianKetThuc()));
            lenh.setString(5, km.getLoaiKhuyenMai().name());
            lenh.setDouble(6, km.getKhuyenMaiPhanTram());
            lenh.setDouble(7, km.getGiaTriDonHangToiThieu());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(KhuyenMai km) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE KhuyenMai SET tenKhuyenMai = ?, thoiGianBatDau = ?, thoiGianKetThuc = ?, loaiKhuyenMai = ?, khuyenMaiPhanTram = ?, giaTriDonHangToiThieu = ? WHERE maKhuyenMai = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, km.getTenKhuyenMai());
            lenh.setTimestamp(2, Timestamp.valueOf(km.getThoiGianBatDau()));
            lenh.setTimestamp(3, Timestamp.valueOf(km.getThoiGianKetThuc()));
            lenh.setString(4, km.getLoaiKhuyenMai().name());
            lenh.setDouble(5, km.getKhuyenMaiPhanTram());
            lenh.setDouble(6, km.getGiaTriDonHangToiThieu());
            lenh.setString(7, km.getMaKhuyenMai());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maKM) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maKM);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}
