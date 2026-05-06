package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.Lo;
import com.example.entity.SanPham;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoDAO {

    /**
     * Lấy tất cả lô hàng
     */
    public List<Lo> layTatCa() {
        List<Lo> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM Lo ORDER BY ngayHetHan ASC, maLo ASC";

        try (Connection ketNoi = ConnectDB.getConnection();
             Statement lenh = ketNoi.createStatement();
             ResultSet ketQua = lenh.executeQuery(sql)) {

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
     * Tự động sinh mã lô theo định dạng: LO + DD + MM + YY + XXX
     * Ví dụ: LO060526001
     */
    public String sinhMaLo() {
        LocalDate today = LocalDate.now();
        String prefix = "LO" 
                      + String.format("%02d", today.getDayOfMonth())
                      + String.format("%02d", today.getMonthValue())
                      + String.format("%02d", today.getYear() % 100);

        String sql = "SELECT maLo FROM Lo WHERE maLo LIKE ? ORDER BY maLo DESC LIMIT 1";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String lastMaLo = rs.getString("maLo");
                int lastNumber = Integer.parseInt(lastMaLo.substring(lastMaLo.length() - 3));
                return prefix + String.format("%03d", lastNumber + 1);
            } else {
                return prefix + "001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return prefix + "001"; // fallback
        }
    }

    /**
     * Thêm lô mới
     */
    public boolean themLo(Lo lo) {
        String sql = "INSERT INTO Lo(maLo, soLo, ngayHetHan, soLuongSanPham, maSanPham, giaNhap) " +
                     "VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, lo.getMaLo());
            stmt.setString(2, lo.getSoLo());
            stmt.setDate(3, Date.valueOf(lo.getNgayHetHan()));
            stmt.setInt(4, lo.getSoLuongSanPham());
            stmt.setString(5, lo.getSanPham().getMaSanPham());
            stmt.setDouble(6, lo.getGiaNhap());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật lô
     */
    public boolean capNhatLo(Lo lo) {
        String sql = "UPDATE Lo SET soLo = ?, ngayHetHan = ?, soLuongSanPham = ?, " +
                     "maSanPham = ?, giaNhap = ? WHERE maLo = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, lo.getSoLo());
            stmt.setDate(2, Date.valueOf(lo.getNgayHetHan()));
            stmt.setInt(3, lo.getSoLuongSanPham());
            stmt.setString(4, lo.getSanPham().getMaSanPham());
            stmt.setDouble(5, lo.getGiaNhap());
            stmt.setString(6, lo.getMaLo());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xóa lô
     */
    public boolean xoaLo(String maLo) {
        String sql = "DELETE FROM Lo WHERE maLo = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maLo);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm theo mã lô
     */
    public Lo timTheoMa(String maLo) {
        String sql = "SELECT * FROM Lo WHERE maLo = ?";
        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(sql)) {

            lenh.setString(1, maLo);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                Lo lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
                return lo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ====================== CÁC HÀM CŨ (giữ lại) ======================
    public boolean capNhatSoLuongTon(String maLo, int soLuongThayDoi, Connection con) throws SQLException {
        String sql = "UPDATE Lo SET soLuongSanPham = soLuongSanPham + ? WHERE maLo = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, soLuongThayDoi);
        stmt.setString(2, maLo);
        return stmt.executeUpdate() > 0;
    }

    public boolean capNhatSoLuongTon(String maLo, int soLuongThayDoi) {
        try (Connection con = ConnectDB.getConnection()) {
            return capNhatSoLuongTon(maLo, soLuongThayDoi, con);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Lo> layDanhSachLoKhaDung(String maDonViQuyDoi) {
        List<Lo> danhSach = new ArrayList<>();
        String truyVan = "SELECT l.* FROM Lo l " +
                         "INNER JOIN DonViQuyDoi dv ON l.maSanPham = dv.maSanPham " +
                         "WHERE dv.maDonVi = ? AND l.soLuongSanPham > 0 " +
                         "AND l.ngayHetHan > GETDATE() ORDER BY l.ngayHetHan ASC";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, maDonViQuyDoi);
            ResultSet ketQua = lenh.executeQuery();

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
}