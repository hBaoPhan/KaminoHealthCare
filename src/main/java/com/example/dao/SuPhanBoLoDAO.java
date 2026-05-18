package com.example.dao;

import com.example.entity.Lo;
import com.example.entity.SuPhanBoLo;
import com.example.entity.ChiTietHoaDon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.connectDB.ConnectDB;

public class SuPhanBoLoDAO {

    public boolean themSuPhanBoLo(SuPhanBoLo spbl) throws SQLException {
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pst = ConnectDB.getConnection().prepareStatement(sql);
        
        pst.setString(1, spbl.getChiTietHoaDon().getHoaDon().getMaHoaDon());
        pst.setString(2, spbl.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
        pst.setString(3, spbl.getLo().getMaLo());
        pst.setInt(4, spbl.getSoLuong());
        pst.setBoolean(5, spbl.getChiTietHoaDon().isLaQuaTangKem());
        
        return pst.executeUpdate() > 0;
    }

    public boolean themNhieu(List<SuPhanBoLo> ds, String maHoaDon) throws SQLException {
        if (ds == null || ds.isEmpty()) return true;
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = ConnectDB.getConnection().prepareStatement(sql)) {
            for (SuPhanBoLo spbl : ds) {
                pst.setString(1, maHoaDon);
                pst.setString(2, spbl.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
                pst.setString(3, spbl.getLo().getMaLo());
                pst.setInt(4, spbl.getSoLuong());
                pst.setBoolean(5, spbl.getChiTietHoaDon().isLaQuaTangKem());
                pst.addBatch();
            }
            pst.executeBatch();
            return true;
        }
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

    public boolean xoaToanBoPhanBoLo(String maHD) throws SQLException {
        String truyVan = "DELETE FROM SuPhanBoLo WHERE maHoaDon = ?";
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {
            lenh.setString(1, maHD);
            return lenh.executeUpdate() >= 0;
        }
    }

    /**
     * Lấy danh sách phân bổ lô tương ứng để hoàn trả kho.
     */
    public List<SuPhanBoLo> layDanhSachPhanBoLoCanTra(String maHoaDonGoc, List<ChiTietHoaDon> dsChiTietTra) {
        List<SuPhanBoLo> dsPhanBoTra = new ArrayList<>();
        String sql = "SELECT maLo, soLuong FROM SuPhanBoLo WHERE maHoaDon = ? AND maDonVi = ?";
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (ChiTietHoaDon ctMoi : dsChiTietTra) {
                    int soLuongCanTra = ctMoi.getSoLuong();
                    ps.setString(1, maHoaDonGoc);
                    ps.setString(2, ctMoi.getDonViQuyDoi().getMaDonVi());

                    try (ResultSet rs = ps.executeQuery()) {
                        // Nếu 1 SP được lấy từ nhiều Lô, chia đúng số lượng trả về từng Lô
                        while (rs.next() && soLuongCanTra > 0) {
                            String maLoGoc = rs.getString("maLo");
                            int slGocTrongLo = rs.getInt("soLuong");
                            int slTraVaoLo = Math.min(soLuongCanTra, slGocTrongLo);

                            Lo lo = new Lo();
                            lo.setMaLo(maLoGoc);

                            SuPhanBoLo spb = new SuPhanBoLo();
                            spb.setLo(lo);
                            spb.setChiTietHoaDon(ctMoi);
                            spb.setSoLuong(slTraVaoLo);
                            dsPhanBoTra.add(spb);

                            soLuongCanTra -= slTraVaoLo;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanBoTra;
    }
}