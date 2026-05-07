package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.KhuyenMai;
import com.example.entity.QuaTang;
import com.example.entity.enums.LoaiKhuyenMai;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiDAO {

    private final QuaTangDAO quaTangDAO = new QuaTangDAO();

    // ==================== SINH MÃ KHUYẾN MÃI ====================
    public String generateNextMaKhuyenMai() {
        LocalDate today = LocalDate.now();
        String datePart = today.format(DateTimeFormatter.ofPattern("MMdd"));
        String prefix = "KM" + datePart;

        String maxMa = null;
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(
                "SELECT TOP 1 maKhuyenMai FROM KhuyenMai WHERE maKhuyenMai LIKE ? ORDER BY maKhuyenMai DESC")) {

            lenh.setString(1, prefix + "%");
            try (ResultSet rs = lenh.executeQuery()) {
                if (rs.next()) {
                    maxMa = rs.getString("maKhuyenMai");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        int nextSeq = 1;
        if (maxMa != null && maxMa.startsWith(prefix)) {
            try {
                String seqStr = maxMa.substring(prefix.length());
                nextSeq = Integer.parseInt(seqStr) + 1;
            } catch (Exception ignored) {}
        }

        return prefix + String.format("%02d", nextSeq);
    }

    public List<KhuyenMai> layTatCa() {
        List<KhuyenMai> danhSach = new ArrayList<>();
        try (Statement lenh = ConnectDB.getConnection().createStatement();
             ResultSet ketQua = lenh.executeQuery("SELECT * FROM KhuyenMai ORDER BY maKhuyenMai")) {

            while (ketQua.next()) {
                KhuyenMai km = mapResultSetToKhuyenMai(ketQua);
                List<QuaTang> dsQt = quaTangDAO.timTheoKhuyenMai(km.getMaKhuyenMai());
                if (!dsQt.isEmpty()) {
                    km.setQuaTangKem(dsQt.get(0));
                }
                danhSach.add(km);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public KhuyenMai timTheoMa(String maKM) {
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(
                "SELECT * FROM KhuyenMai WHERE maKhuyenMai = ?")) {
            lenh.setString(1, maKM);
            try (ResultSet ketQua = lenh.executeQuery()) {
                if (ketQua.next()) {
                    KhuyenMai km = mapResultSetToKhuyenMai(ketQua);
                    List<QuaTang> dsQt = quaTangDAO.timTheoKhuyenMai(km.getMaKhuyenMai());
                    if (!dsQt.isEmpty()) {
                        km.setQuaTangKem(dsQt.get(0));
                    }
                    return km;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(KhuyenMai km) {
        if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().trim().isEmpty()) {
            km.setMaKhuyenMai(generateNextMaKhuyenMai());
        }

        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(
                "INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, thoiGianBatDau, thoiGianKetThuc, " +
                        "loaiKhuyenMai, khuyenMaiPhanTram, giaTriDonHangToiThieu) VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            lenh.setString(1, km.getMaKhuyenMai());
            lenh.setString(2, km.getTenKhuyenMai());
            lenh.setTimestamp(3, Timestamp.valueOf(km.getThoiGianBatDau()));
            lenh.setTimestamp(4, Timestamp.valueOf(km.getThoiGianKetThuc()));
            lenh.setString(5, km.getLoaiKhuyenMai().name());
            lenh.setDouble(6, km.getKhuyenMaiPhanTram());
            lenh.setDouble(7, km.getGiaTriDonHangToiThieu());

            boolean success = lenh.executeUpdate() > 0;

            if (success && km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                QuaTang qt = km.getQuaTangKem();
                qt.setKhuyenMai(km);
                if (qt.getDonViQuyDoi() != null) {
                    quaTangDAO.them(qt);
                }
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(KhuyenMai km) {
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(
                "UPDATE KhuyenMai SET tenKhuyenMai = ?, thoiGianBatDau = ?, thoiGianKetThuc = ?, " +
                        "loaiKhuyenMai = ?, khuyenMaiPhanTram = ?, giaTriDonHangToiThieu = ? WHERE maKhuyenMai = ?")) {

            lenh.setString(1, km.getTenKhuyenMai());
            lenh.setTimestamp(2, Timestamp.valueOf(km.getThoiGianBatDau()));
            lenh.setTimestamp(3, Timestamp.valueOf(km.getThoiGianKetThuc()));
            lenh.setString(4, km.getLoaiKhuyenMai().name());
            lenh.setDouble(5, km.getKhuyenMaiPhanTram());
            lenh.setDouble(6, km.getGiaTriDonHangToiThieu());
            lenh.setString(7, km.getMaKhuyenMai());

            boolean success = lenh.executeUpdate() > 0;

            if (success) {
                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                    QuaTang qt = km.getQuaTangKem();
                    qt.setKhuyenMai(km);
                    if (qt.getDonViQuyDoi() != null) {
                        quaTangDAO.capNhat(qt);
                    }
                } else {
                    quaTangDAO.xoaTheoKhuyenMai(km.getMaKhuyenMai());
                }
            }
            return success;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String maKM) {
        try {
            // Xóa quà tặng trước
            quaTangDAO.xoaTheoKhuyenMai(maKM);

            try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(
                    "DELETE FROM KhuyenMai WHERE maKhuyenMai = ?")) {
                lenh.setString(1, maKM);
                return lenh.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private KhuyenMai mapResultSetToKhuyenMai(ResultSet rs) throws SQLException {
        KhuyenMai km = new KhuyenMai();
        km.setMaKhuyenMai(rs.getString("maKhuyenMai"));
        km.setTenKhuyenMai(rs.getString("tenKhuyenMai"));
        km.setThoiGianBatDau(rs.getTimestamp("thoiGianBatDau").toLocalDateTime());
        km.setThoiGianKetThuc(rs.getTimestamp("thoiGianKetThuc").toLocalDateTime());
        km.setLoaiKhuyenMai(LoaiKhuyenMai.valueOf(rs.getString("loaiKhuyenMai")));
        km.setKhuyenMaiPhanTram(rs.getDouble("khuyenMaiPhanTram"));
        km.setGiaTriDonHangToiThieu(rs.getDouble("giaTriDonHangToiThieu"));
        return km;
    }
}