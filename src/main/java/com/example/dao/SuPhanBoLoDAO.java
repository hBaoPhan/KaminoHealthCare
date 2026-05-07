package com.example.dao;

import com.example.entity.Lo;
import com.example.entity.SuPhanBoLo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.connectDB.ConnectDB;

public class SuPhanBoLoDAO {

    public boolean themSuPhanBoLo(SuPhanBoLo spbl, Connection con) throws SQLException {
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = con.prepareStatement(sql);
        
        pst.setString(1, spbl.getChiTietHoaDon().getHoaDon().getMaHoaDon());
        pst.setString(2, spbl.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
        pst.setString(3, spbl.getLo().getMaLo());
        pst.setInt(4, spbl.getSoLuong());
        pst.setBoolean(5, spbl.getChiTietHoaDon().isLaQuaTangKem());
        
        return pst.executeUpdate() > 0;
    }

    public List<SuPhanBoLo> layPhanBoLoCuaChiTiet(String maHD, String maDV, boolean laQuaTangKem) {
        List<SuPhanBoLo> ds = new ArrayList<>();
        String sql = "SELECT s.* FROM SuPhanBoLo s " +
                     "JOIN ChiTietHoaDon c ON s.maHoaDon = c.maHoaDon AND s.maDonVi = c.maDonVi " +
                     "WHERE s.maHoaDon = ? AND s.maDonVi = ? AND c.laQuaTangKem = ?";
        
        try {
            // 1. Lấy connection bình thường (Không đặt trong try-with-resources)
            Connection con = ConnectDB.getConnection();
            
            // 2. Chỉ đưa PreparedStatement vào try-with-resources để tự động giải phóng vùng nhớ
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maHD);
                stmt.setString(2, maDV);
                stmt.setBoolean(3, laQuaTangKem);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    LoDAO loDAO = new LoDAO();
                    while (rs.next()) {
                        SuPhanBoLo spb = new SuPhanBoLo();
                        Lo lo = loDAO.timTheoMa(rs.getString("maLo"));
                        spb.setLo(lo); 
                        spb.setSoLuong(rs.getInt("soLuong"));
                        ds.add(spb);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}