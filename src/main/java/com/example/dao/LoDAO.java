package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.Lo;
import com.example.entity.SanPham;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoDAO {

    /**
     * Lấy tất cả danh sách lô hàng từ cơ sở dữ liệu
     */
    public List<Lo> layTatCa() {
        List<Lo> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            // Sửa tên cột soLuongTon cho khớp với file QuanLyHieuThuoc.sql 
            String truyVan = "SELECT * FROM Lo";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                Lo lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
                danhSach.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    /**
     * Cập nhật số lượng tồn kho (Dùng chung cho Bán hàng & Đổi hàng)
     * soLuongThayDoi > 0: Cộng thêm (khi khách TRẢ hàng)
     * soLuongThayDoi < 0: Trừ đi (khi khách LẤY hàng mới hoặc BÁN hàng)
     */
    public boolean capNhatSoLuongTon(String maLo, int soLuongThayDoi, Connection con) throws SQLException {
        String sql = "UPDATE Lo SET soLuongSanPham = soLuongSanPham + ? WHERE maLo = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, soLuongThayDoi);
        stmt.setString(2, maLo);
        return stmt.executeUpdate() > 0;
    }

    /**
     * Tìm kiếm lô hàng theo mã
     */
    public Lo timTheoMa(String maLo) {
        Lo lo = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM Lo WHERE maLo = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maLo);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lo;
    }

    /**
     * Lấy danh sách lô khả dụng theo nguyên tắc FEFO (Hết hạn trước xuất trước)
     */
    public List<Lo> layDanhSachLoKhaDung(String maDonViQuyDoi) {
        List<Lo> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT l.* FROM Lo l " +
                             "INNER JOIN DonViQuyDoi dv ON l.maSanPham = dv.maSanPham " +
                             "WHERE dv.maDonVi = ? AND l.soLuongSanPham > 0 AND l.ngayHetHan > GETDATE() " +
                             "ORDER BY l.ngayHetHan ASC";
            
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maDonViQuyDoi);
            ResultSet ketQua = lenh.executeQuery();

            while (ketQua.next()) {
                Lo lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                // Đảm bảo tên cột ở đây cũng là soLuongSanPham
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham")); 
                lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
                danhSach.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Overload để dùng trong trường hợp đơn lẻ không transaction
    public boolean capNhatSoLuongTon(String maLo, int soLuongThayDoi) {
        try {
            Connection con = ConnectDB.getConnection();
            return capNhatSoLuongTon(maLo, soLuongThayDoi, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}