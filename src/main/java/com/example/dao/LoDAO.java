package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.Lo;
import com.example.entity.SanPham;
import com.example.entity.SuPhanBoLo;
import com.example.entity.enums.LoaiSanPham;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoDAO {

    public LoDAO() {
        ensureColumnSoLuongNhapExists();
    }

    private void ensureColumnSoLuongNhapExists() {
        try {
            Connection con = ConnectDB.getConnection();
            DatabaseMetaData meta = con.getMetaData();
            boolean exists = false;
            try (ResultSet rs = meta.getColumns(null, null, "Lo", "soLuongNhap")) {
                if (rs.next()) {
                    exists = true;
                }
            }
            if (!exists) {
                try (Statement stmt = con.createStatement()) {
                    stmt.execute("ALTER TABLE Lo ADD soLuongNhap INT");
                    stmt.execute("UPDATE Lo SET soLuongNhap = soLuongSanPham");
                }
            }
            // Ensure any NULLs in newly imported tables are populated on startup
            try (Statement stmt = con.createStatement()) {
                stmt.execute("UPDATE Lo SET soLuongNhap = soLuongSanPham WHERE soLuongNhap IS NULL");
            }
        } catch (Exception e) {
            // Safe fall-through or logging
            e.printStackTrace();
        }
    }

    /**
     * Lấy tất cả lô hàng
     */
    public List<Lo> layTatCa() {
        List<Lo> danhSach = new ArrayList<>();
        String sql = "SELECT l.maLo, l.soLo, l.ngayHetHan, l.soLuongSanPham, l.giaNhap, l.soLuongNhap, " +
                     "s.maSanPham, s.tenSanPham, s.loaiSanPham, s.soLuongTon, s.moTa, s.hoatChat, s.donGiaCoBan, s.trangThaiKinhDoanh, s.thue " +
                     "FROM Lo l " +
                     "INNER JOIN SanPham s ON l.maSanPham = s.maSanPham " +
                     "ORDER BY l.ngayHetHan ASC, l.maLo ASC";

        try (Statement lenh = ConnectDB.getConnection().createStatement();
                ResultSet ketQua = lenh.executeQuery(sql)) {

            while (ketQua.next()) {
                Lo lo = new Lo();
                lo.setMaLo(ketQua.getString("maLo"));
                lo.setSoLo(ketQua.getString("soLo"));
                lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                lo.setSoLuongNhap(ketQua.getInt("soLuongNhap"));
                
                SanPham sp = new SanPham();
                sp.setMaSanPham(ketQua.getString("maSanPham"));
                sp.setTenSanPham(ketQua.getString("tenSanPham"));
                sp.setLoaiSanPham(LoaiSanPham.valueOf(ketQua.getString("loaiSanPham")));
                sp.setSoLuongTon(ketQua.getInt("soLuongTon"));
                sp.setMoTa(ketQua.getString("moTa"));
                sp.setHoatChat(ketQua.getString("hoatChat"));
                sp.setDonGiaCoBan(ketQua.getDouble("donGiaCoBan"));
                sp.setTrangThaiKinhDoanh(ketQua.getBoolean("trangThaiKinhDoanh"));
                sp.setThue(ketQua.getDouble("thue"));
                
                lo.setSanPham(sp);
                lo.setGiaNhap(ketQua.getDouble("giaNhap"));
                danhSach.add(lo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    /**
     * Lấy mã lô mới nhất theo prefix
     */
    public String layMaLoMoiNhat(String prefix) {
        String sql = "SELECT TOP 1 maLo FROM Lo WHERE maLo LIKE ? ORDER BY maLo DESC";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("maLo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thêm lô mới
     */
    public boolean themLo(Lo lo) {
        String sql = "INSERT INTO Lo(maLo, soLo, ngayHetHan, soLuongSanPham, maSanPham, giaNhap, soLuongNhap) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {

            stmt.setString(1, lo.getMaLo());
            stmt.setString(2, lo.getSoLo());
            stmt.setDate(3, Date.valueOf(lo.getNgayHetHan()));
            stmt.setInt(4, lo.getSoLuongSanPham());
            stmt.setString(5, lo.getSanPham().getMaSanPham());
            stmt.setDouble(6, lo.getGiaNhap());
            stmt.setInt(7, lo.getSoLuongNhap() > 0 ? lo.getSoLuongNhap() : lo.getSoLuongSanPham());

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
                "maSanPham = ?, giaNhap = ?, soLuongNhap = ? WHERE maLo = ?";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {

            stmt.setString(1, lo.getSoLo());
            stmt.setDate(2, Date.valueOf(lo.getNgayHetHan()));
            stmt.setInt(3, lo.getSoLuongSanPham());
            stmt.setString(4, lo.getSanPham().getMaSanPham());
            stmt.setDouble(5, lo.getGiaNhap());
            stmt.setInt(6, lo.getSoLuongNhap() > 0 ? lo.getSoLuongNhap() : lo.getSoLuongSanPham());
            stmt.setString(7, lo.getMaLo());

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
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {

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
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(sql)) {

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
                lo.setSoLuongNhap(ketQua.getInt("soLuongNhap"));
                return lo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean capNhatSoLuongTon(String maLo, int soLuongThayDoi) throws SQLException {
        String sql = "UPDATE Lo SET soLuongSanPham = soLuongSanPham + ? WHERE maLo = ?";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, soLuongThayDoi);
            stmt.setString(2, maLo);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean capNhatTonKhoNhieu(List<SuPhanBoLo> ds, boolean isCong) throws SQLException {
        if (ds == null || ds.isEmpty())
            return true;
        String operator = isCong ? "+" : "-";
        String sql = "UPDATE Lo SET soLuongSanPham = soLuongSanPham " + operator + " ? WHERE maLo = ?";
        try (PreparedStatement pst = ConnectDB.getConnection().prepareStatement(sql)) {
            boolean hasBatch = false;
            for (SuPhanBoLo sp : ds) {
                if (isCong && sp.isLoi()) {
                    continue; // Skip defective items when returning/adding to stock
                }
                pst.setInt(1, sp.getSoLuongPhanBo());
                pst.setString(2, sp.getLo().getMaLo());
                pst.addBatch();
                hasBatch = true;
            }
            if (hasBatch) {
                pst.executeBatch();
            }
            return true;
        }
    }

    public List<Lo> layDanhSachLoKhaDung(String maDonViQuyDoi) throws SQLException {
        List<Lo> danhSach = new ArrayList<>();
        String truyVan = "SELECT l.* FROM Lo l " +
                "INNER JOIN DonViQuyDoi dv ON l.maSanPham = dv.maSanPham " +
                "WHERE dv.maDonVi = ? AND l.soLuongSanPham > 0 " +
                "AND l.ngayHetHan > DATEADD(day, 30, GETDATE()) ORDER BY l.ngayHetHan ASC";

        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {
            lenh.setString(1, maDonViQuyDoi);
            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    Lo lo = new Lo();
                    lo.setMaLo(ketQua.getString("maLo"));
                    lo.setSoLo(ketQua.getString("soLo"));
                    lo.setNgayHetHan(ketQua.getDate("ngayHetHan").toLocalDate());
                    lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                    lo.setSanPham(new SanPham(ketQua.getString("maSanPham")));
                    lo.setGiaNhap(ketQua.getDouble("giaNhap"));
                    lo.setSoLuongNhap(ketQua.getInt("soLuongNhap"));
                    danhSach.add(lo);
                }
            }
        }
        return danhSach;
    }

    public int tinhTongTonKhoSanPham(String maSanPham) {
        String sql = "SELECT SUM(soLuongSanPham) FROM Lo WHERE maSanPham = ?";
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(sql)) {
            lenh.setString(1, maSanPham);
            try (ResultSet rs = lenh.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int tinhSoLuongNhapBanDau(String maLo) {
        String sql = "SELECT soLuongNhap FROM Lo WHERE maLo = ?";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {
            stmt.setString(1, maLo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("soLuongNhap");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}