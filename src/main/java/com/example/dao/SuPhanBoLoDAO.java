package com.example.dao;

import com.example.entity.SuPhanBoLo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.connectDB.ConnectDB;

public class SuPhanBoLoDAO {

    public boolean themSuPhanBoLo(SuPhanBoLo spbl) {
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong) VALUES (?, ?, ?, ?)";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            // Lấy maHoaDon và maDonVi thông qua đối tượng ChiTietHoaDon
            pst.setString(1, spbl.getChiTietHoaDon().getHoaDon().getMaHoaDon());
            pst.setString(2, spbl.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
            pst.setString(3, spbl.getLo().getMaLo());
            pst.setInt(4, spbl.getSoLuong());
            
            return pst.executeUpdate() > 0;
        }
    }

    // (Tùy chọn) Lấy danh sách các lô đã dùng cho một dòng chi tiết hóa đơn
    public List<SuPhanBoLo> layPhanBoLoCuaChiTiet(String maHoaDon, String maDonVi) {
        List<SuPhanBoLo> list = new ArrayList<>();
        String sql = "SELECT * FROM SuPhanBoLo WHERE maHoaDon = ? AND maDonVi = ?";

        Connection con = ConnectDB.getConnection();
        try (PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, maHoaDon);
            pst.setString(2, maDonVi);
            ResultSet rs = pst.executeQuery();

            LoDAO loDAO = new LoDAO();

            while (rs.next()) {
                SuPhanBoLo spbl = new SuPhanBoLo();
                spbl.setLo(loDAO.timTheoMa(rs.getString("maLo")));
                spbl.setSoLuong(rs.getInt("soLuong"));
                // (Set thêm ChiTietHoaDon nếu cần thiết)
                list.add(spbl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
