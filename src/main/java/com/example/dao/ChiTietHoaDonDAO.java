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
            while (rs.next()) {
                ChiTietHoaDon ct = new ChiTietHoaDon();
                String maDV = rs.getString("maDonVi");
                ct.setSoLuong(rs.getInt("soLuong"));
                ct.setDonGia(rs.getDouble("donGia"));
                
                // Gọi DAO phân bổ lô với 2 khóa
                SuPhanBoLoDAO spbDAO = new SuPhanBoLoDAO();
                ct.setDsPhanBoLo(spbDAO.layPhanBoLoCuaChiTiet(maHD, maDV));
                
                ds.add(ct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean them(ChiTietHoaDon ct) {
        int soDongThayDoi = 0;
        String truyVan = "INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuong, donGia, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setString(1, ct.getHoaDon().getMaHoaDon());
            lenh.setString(2, ct.getDonViQuyDoi().getMaDonVi());
            lenh.setInt(3, ct.getSoLuong());
            lenh.setDouble(4, ct.getDonGia());
            lenh.setBoolean(5, ct.isLaQuaTangKem());
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(ChiTietHoaDon ct) {
        int soDongThayDoi = 0;
        String truyVan = "UPDATE ChiTietHoaDon SET soLuong = ?, donGia = ?, laQuaTangKem = ? WHERE maHoaDon = ? AND maDonVi = ?";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setInt(1, ct.getSoLuong());
            lenh.setDouble(2, ct.getDonGia());
            lenh.setBoolean(3, ct.isLaQuaTangKem());
            lenh.setString(4, ct.getHoaDon().getMaHoaDon());
            lenh.setString(5, ct.getDonViQuyDoi().getMaDonVi());
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maHD, String maDV) {
        int soDongThayDoi = 0;
        String truyVan = "DELETE FROM ChiTietHoaDon WHERE maHoaDon = ? AND maDonVi = ?";
        
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            
            lenh.setString(1, maHD);
            lenh.setString(2, maDV);
            
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }
}