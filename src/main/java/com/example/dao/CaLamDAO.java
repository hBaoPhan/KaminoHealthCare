package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.CaLam;
import com.example.entity.NhanVien;
import com.example.entity.enums.TrangThaiCaLam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaLamDAO {

    public List<CaLam> layTatCa() {
        List<CaLam> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM CaLam";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                CaLam cl = new CaLam();
                cl.setMaCa(ketQua.getString("maCa"));
                cl.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                cl.setGioBatDau(ketQua.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc = ketQua.getTimestamp("gioKetThuc");
                if (ketThuc != null) {
                    cl.setGioKetThuc(ketThuc.toLocalDateTime());
                }
                cl.setTrangThai(TrangThaiCaLam.valueOf(ketQua.getString("trangThaiCaLam")));
                cl.setTienMoCa(ketQua.getDouble("tienMoCa"));
                cl.setTienKetCa(ketQua.getDouble("tienKetCa"));
                cl.setTienHeThong(ketQua.getDouble("tienHeThong"));
                cl.setGhiChu(ketQua.getString("ghiChu"));
                danhSach.add(cl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public CaLam timTheoMa(String maCa) {
        CaLam cl = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM CaLam WHERE maCa = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maCa);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                cl = new CaLam();
                cl.setMaCa(ketQua.getString("maCa"));
                cl.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                cl.setGioBatDau(ketQua.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc2 = ketQua.getTimestamp("gioKetThuc");
                if (ketThuc2 != null) {
                    cl.setGioKetThuc(ketThuc2.toLocalDateTime());
                }
                cl.setTrangThai(TrangThaiCaLam.valueOf(ketQua.getString("trangThaiCaLam")));
                cl.setTienMoCa(ketQua.getDouble("tienMoCa"));
                cl.setTienKetCa(ketQua.getDouble("tienKetCa"));
                cl.setTienHeThong(ketQua.getDouble("tienHeThong"));
                cl.setGhiChu(ketQua.getString("ghiChu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cl;
    }

    public boolean them(CaLam cl) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO CaLam VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, cl.getMaCa());
            lenh.setString(2, cl.getNhanVien().getMaNhanVien());
            lenh.setTimestamp(3, Timestamp.valueOf(cl.getGioBatDau()));
            if (cl.getGioKetThuc() != null) {
                lenh.setTimestamp(4, Timestamp.valueOf(cl.getGioKetThuc()));
            } else {
                lenh.setNull(4, Types.TIMESTAMP);
            }
            lenh.setString(5, cl.getTrangThai().name());
            lenh.setDouble(6, cl.getTienMoCa());
            lenh.setDouble(7, cl.getTienKetCa());
            lenh.setDouble(8, cl.getTienHeThong());
            lenh.setString(9, cl.getGhiChu());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(CaLam cl) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE CaLam SET maNhanVien = ?, gioBatDau = ?, gioKetThuc = ?, trangThaiCaLam = ?, tienMoCa = ?, tienKetCa = ?, tienHeThong = ?, ghiChu = ? WHERE maCa = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, cl.getNhanVien().getMaNhanVien());
            lenh.setTimestamp(2, Timestamp.valueOf(cl.getGioBatDau()));
            if (cl.getGioKetThuc() != null) {
                lenh.setTimestamp(3, Timestamp.valueOf(cl.getGioKetThuc()));
            } else {
                lenh.setNull(3, Types.TIMESTAMP);
            }
            lenh.setString(4, cl.getTrangThai().name());
            lenh.setDouble(5, cl.getTienMoCa());
            lenh.setDouble(6, cl.getTienKetCa());
            lenh.setDouble(7, cl.getTienHeThong());
            lenh.setString(8, cl.getGhiChu());
            lenh.setString(9, cl.getMaCa());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maCa) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM CaLam WHERE maCa = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maCa);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public CaLam layCaHienTai(String maNhanVien) {
        CaLam cl = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT TOP 1 * FROM CaLam WHERE maNhanVien = ? AND trangThaiCaLam = 'DANG_MO' ORDER BY gioBatDau DESC";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maNhanVien);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                cl = new CaLam();
                cl.setMaCa(ketQua.getString("maCa"));
                cl.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                cl.setGioBatDau(ketQua.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc = ketQua.getTimestamp("gioKetThuc");
                if (ketThuc != null) {
                    cl.setGioKetThuc(ketThuc.toLocalDateTime());
                }
                cl.setTrangThai(TrangThaiCaLam.valueOf(ketQua.getString("trangThaiCaLam")));
                cl.setTienMoCa(ketQua.getDouble("tienMoCa"));
                cl.setTienKetCa(ketQua.getDouble("tienKetCa"));
                cl.setTienHeThong(ketQua.getDouble("tienHeThong"));
                cl.setGhiChu(ketQua.getString("ghiChu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cl;
    }
}
