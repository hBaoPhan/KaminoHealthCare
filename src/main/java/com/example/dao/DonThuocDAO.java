package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.DonThuoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonThuocDAO {

    public List<DonThuoc> layTatCa() {
        List<DonThuoc> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM DonThuoc";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                DonThuoc dt = new DonThuoc();
                dt.setMaDonThuoc(ketQua.getString("maDonThuoc"));
                dt.setTenBacSi(ketQua.getString("tenBacSi"));
                dt.setCoSoKhamBenh(ketQua.getString("coSoKhamBenh"));
                dt.setNgayKeDon(ketQua.getDate("ngayKeDon").toLocalDate());
                danhSach.add(dt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public DonThuoc timTheoMa(String maDT) {
        DonThuoc dt = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM DonThuoc WHERE maDonThuoc = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maDT);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                dt = new DonThuoc();
                dt.setMaDonThuoc(ketQua.getString("maDonThuoc"));
                dt.setTenBacSi(ketQua.getString("tenBacSi"));
                dt.setCoSoKhamBenh(ketQua.getString("coSoKhamBenh"));
                dt.setNgayKeDon(ketQua.getDate("ngayKeDon").toLocalDate());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public boolean them(DonThuoc dt) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO DonThuoc VALUES (?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, dt.getMaDonThuoc());
            lenh.setString(2, dt.getTenBacSi());
            lenh.setString(3, dt.getCoSoKhamBenh());
            lenh.setDate(4, Date.valueOf(dt.getNgayKeDon()));
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(DonThuoc dt) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE DonThuoc SET tenBacSi = ?, coSoKhamBenh = ?, ngayKeDon = ? WHERE maDonThuoc = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, dt.getTenBacSi());
            lenh.setString(2, dt.getCoSoKhamBenh());
            lenh.setDate(3, Date.valueOf(dt.getNgayKeDon()));
            lenh.setString(4, dt.getMaDonThuoc());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maDT) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM DonThuoc WHERE maDonThuoc = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maDT);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}
