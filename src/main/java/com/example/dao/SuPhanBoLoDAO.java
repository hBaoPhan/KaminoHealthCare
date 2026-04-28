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

	public boolean themSuPhanBoLo(SuPhanBoLo spbl, Connection con) throws SQLException {
        // Sử dụng maHoaDon và maDonVi làm khóa ngoại ánh xạ với ChiTietHoaDon
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong) VALUES (?, ?, ?, ?)";
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
	public List<SuPhanBoLo> layPhanBoLoCuaChiTiet(String maHD, String maDV) {
	    List<SuPhanBoLo> ds = new ArrayList<>();
	    try {
	        ConnectDB.getInstance().connect();
	        Connection con = ConnectDB.getConnection();
	        String sql = "SELECT * FROM SuPhanBoLo WHERE maHoaDon = ? AND maDonVi = ?";
	        PreparedStatement stmt = con.prepareStatement(sql);
	        stmt.setString(1, maHD);
	        stmt.setString(2, maDV);
	        ResultSet rs = stmt.executeQuery();
	        while (rs.next()) {
	            SuPhanBoLo spb = new SuPhanBoLo();
	            spb.getLo().setMaLo(rs.getString("maLo"));
	            spb.setSoLuong(rs.getInt("soLuong"));
	            ds.add(spb);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return ds;
	}
}
