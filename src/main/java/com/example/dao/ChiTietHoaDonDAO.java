package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.*;
import com.example.entity.enums.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDonDAO {

    private SuPhanBoLoDAO suPhanBoLoDAO = new SuPhanBoLoDAO();

    public List<ChiTietHoaDon> layTheoMaHoaDon(String maHD) {
        List<ChiTietHoaDon> ds = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM ChiTietHoaDon WHERE maHoaDon = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            ResultSet rs = stmt.executeQuery();
            
            DonViQuyDoiDAO dvDAO = new DonViQuyDoiDAO();
            
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                ct.setSoLuongBan(rs.getInt("soLuongBan"));
                ct.setDonGia(rs.getDouble("donGia"));
                ct.setLaQuaTangKem(rs.getBoolean("laQuaTangKem"));
                
                String maDV = rs.getString("maDonVi");
                ct.setDonViQuyDoi(dvDAO.timTheoMa(maDV));
                
                SuPhanBoLoDAO spbDAO = new SuPhanBoLoDAO();
                ct.setDsPhanBoLo(spbDAO.layPhanBoLoCuaChiTiet(maHD, maDV, ct.isLaQuaTangKem()));
                
                ds.add(ct);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return ds;
    }

    public boolean them(ChiTietHoaDon ct) throws SQLException {
        String truyVan = "INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuongBan, donGia, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
       
        PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan);
        lenh.setString(1, ct.getHoaDon().getMaHoaDon());
        lenh.setString(2, ct.getDonViQuyDoi().getMaDonVi());
        lenh.setInt(3, ct.getSoLuongBan());
        lenh.setDouble(4, ct.getDonGia());
        lenh.setBoolean(5, ct.isLaQuaTangKem());
        
        return lenh.executeUpdate() > 0;
    }

    public boolean themNhieu(List<ChiTietHoaDon> ds, String maHoaDon) throws SQLException {
        if (ds == null || ds.isEmpty()) return true;
        String truyVan = "INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuongBan, donGia, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {
            for (ChiTietHoaDon ct : ds) {
                lenh.setString(1, maHoaDon);
                lenh.setString(2, ct.getDonViQuyDoi().getMaDonVi());
                lenh.setInt(3, ct.getSoLuongBan());
                lenh.setDouble(4, ct.getDonGia());
                lenh.setBoolean(5, ct.isLaQuaTangKem());
                lenh.addBatch();
            }
            lenh.executeBatch();
            return true;
        }
    }

    public boolean capNhat(ChiTietHoaDon ct) {
        int soDongThayDoi = 0;
        String truyVan = "UPDATE ChiTietHoaDon SET soLuongBan = ?, donGia = ? WHERE maHoaDon = ? AND maDonVi = ? AND laQuaTangKem = ?";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setInt(1, ct.getSoLuongBan());
            lenh.setDouble(2, ct.getDonGia());
            lenh.setString(3, ct.getHoaDon().getMaHoaDon());
            lenh.setString(4, ct.getDonViQuyDoi().getMaDonVi());
            lenh.setBoolean(5, ct.isLaQuaTangKem());
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maHD, String maDV, boolean laQuaTangKem) {
        int soDongThayDoi = 0;
        String truyVan = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ? AND maDonVi = ? AND laQuaTangKem = ?";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setString(1, maHD);
            lenh.setString(2, maDV);
            lenh.setBoolean(3, laQuaTangKem);
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoaToanBoChiTiet(String maHD) throws SQLException {
        String truyVan = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ?";
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {
            lenh.setString(1, maHD);
            return lenh.executeUpdate() >= 0;
        }
    }
}