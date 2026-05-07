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

        try (Statement lenh = ConnectDB.getConnection().createStatement();
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

        String sql = "SELECT TOP 1 maLo FROM Lo WHERE maLo LIKE ? ORDER BY maLo DESC";

        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {

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
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {

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
     * Thêm lô + cập nhật tồn kho sản phẩm (tồn kho chỉ thay đổi theo Lô).
     */
    public boolean themLoVaCapNhatTonKho(Lo lo) {
        String sqlInsert = "INSERT INTO Lo(maLo, soLo, ngayHetHan, soLuongSanPham, maSanPham, giaNhap) " +
                "VALUES(?, ?, ?, ?, ?, ?)";
        String sqlUpdateTon = "UPDATE SanPham SET soLuongTon = CASE " +
                "WHEN soLuongTon + ? < 0 THEN 0 ELSE soLuongTon + ? END " +
                "WHERE maSanPham = ?";

        Connection con = ConnectDB.getConnection();
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            try (PreparedStatement stmt = con.prepareStatement(sqlInsert);
                 PreparedStatement up = con.prepareStatement(sqlUpdateTon)) {

                stmt.setString(1, lo.getMaLo());
                stmt.setString(2, lo.getSoLo());
                stmt.setDate(3, Date.valueOf(lo.getNgayHetHan()));
                stmt.setInt(4, lo.getSoLuongSanPham());
                stmt.setString(5, lo.getSanPham().getMaSanPham());
                stmt.setDouble(6, lo.getGiaNhap());

                int inserted = stmt.executeUpdate();
                if (inserted <= 0) {
                    con.rollback();
                    return false;
                }

                int delta = lo.getSoLuongSanPham();
                up.setInt(1, delta);
                up.setInt(2, delta);
                up.setString(3, lo.getSanPham().getMaSanPham());

                if (up.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }

                con.commit();
                return true;
            }
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
        }
    }

    /**
     * Cập nhật lô
     */
    public boolean capNhatLo(Lo lo) {
        String sql = "UPDATE Lo SET soLo = ?, ngayHetHan = ?, soLuongSanPham = ?, " +
                     "maSanPham = ?, giaNhap = ? WHERE maLo = ?";
        try (PreparedStatement stmt = ConnectDB.getConnection().prepareStatement(sql)) {

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
     * Cập nhật lô + cập nhật tồn kho sản phẩm theo chênh lệch số lượng (và/hoặc đổi mã SP).
     */
    public boolean capNhatLoVaCapNhatTonKho(Lo loMoi) {
        String sqlGetOld = "SELECT maSanPham, soLuongSanPham FROM Lo WHERE maLo = ?";
        String sqlUpdateLo = "UPDATE Lo SET soLo = ?, ngayHetHan = ?, soLuongSanPham = ?, maSanPham = ?, giaNhap = ? WHERE maLo = ?";
        String sqlUpdateTon = "UPDATE SanPham SET soLuongTon = CASE " +
                "WHEN soLuongTon + ? < 0 THEN 0 ELSE soLuongTon + ? END " +
                "WHERE maSanPham = ?";

        Connection con = ConnectDB.getConnection();
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            String maSanPhamCu;
            int soLuongCu;
            try (PreparedStatement get = con.prepareStatement(sqlGetOld)) {
                get.setString(1, loMoi.getMaLo());
                try (ResultSet rs = get.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return false;
                    }
                    maSanPhamCu = rs.getString(1);
                    soLuongCu = rs.getInt(2);
                }
            }

            try (PreparedStatement upLo = con.prepareStatement(sqlUpdateLo);
                 PreparedStatement upTon = con.prepareStatement(sqlUpdateTon)) {

                upLo.setString(1, loMoi.getSoLo());
                upLo.setDate(2, Date.valueOf(loMoi.getNgayHetHan()));
                upLo.setInt(3, loMoi.getSoLuongSanPham());
                upLo.setString(4, loMoi.getSanPham().getMaSanPham());
                upLo.setDouble(5, loMoi.getGiaNhap());
                upLo.setString(6, loMoi.getMaLo());

                if (upLo.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }

                String maSanPhamMoi = loMoi.getSanPham().getMaSanPham();
                int soLuongMoi = loMoi.getSoLuongSanPham();

                if (maSanPhamMoi != null && maSanPhamMoi.equals(maSanPhamCu)) {
                    int delta = soLuongMoi - soLuongCu;
                    if (delta != 0) {
                        upTon.setInt(1, delta);
                        upTon.setInt(2, delta);
                        upTon.setString(3, maSanPhamMoi);
                        if (upTon.executeUpdate() <= 0) {
                            con.rollback();
                            return false;
                        }
                    }
                } else {
                    // Trừ tồn kho SP cũ
                    upTon.setInt(1, -soLuongCu);
                    upTon.setInt(2, -soLuongCu);
                    upTon.setString(3, maSanPhamCu);
                    if (upTon.executeUpdate() <= 0) {
                        con.rollback();
                        return false;
                    }

                    // Cộng tồn kho SP mới
                    upTon.setInt(1, soLuongMoi);
                    upTon.setInt(2, soLuongMoi);
                    upTon.setString(3, maSanPhamMoi);
                    if (upTon.executeUpdate() <= 0) {
                        con.rollback();
                        return false;
                    }
                }

                con.commit();
                return true;
            }
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
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
     * Xóa lô + trừ tồn kho sản phẩm theo số lượng của lô.
     */
    public boolean xoaLoVaCapNhatTonKho(String maLo) {
        String sqlGetOld = "SELECT maSanPham, soLuongSanPham FROM Lo WHERE maLo = ?";
        String sqlDelete = "DELETE FROM Lo WHERE maLo = ?";
        String sqlUpdateTon = "UPDATE SanPham SET soLuongTon = CASE " +
                "WHEN soLuongTon + ? < 0 THEN 0 ELSE soLuongTon + ? END " +
                "WHERE maSanPham = ?";

        Connection con = ConnectDB.getConnection();
        boolean oldAutoCommit = true;
        try {
            oldAutoCommit = con.getAutoCommit();
            con.setAutoCommit(false);

            String maSanPham;
            int soLuong;
            try (PreparedStatement get = con.prepareStatement(sqlGetOld)) {
                get.setString(1, maLo);
                try (ResultSet rs = get.executeQuery()) {
                    if (!rs.next()) {
                        con.rollback();
                        return false;
                    }
                    maSanPham = rs.getString(1);
                    soLuong = rs.getInt(2);
                }
            }

            try (PreparedStatement del = con.prepareStatement(sqlDelete);
                 PreparedStatement up = con.prepareStatement(sqlUpdateTon)) {
                del.setString(1, maLo);
                if (del.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }

                int delta = -soLuong;
                up.setInt(1, delta);
                up.setInt(2, delta);
                up.setString(3, maSanPham);
                if (up.executeUpdate() <= 0) {
                    con.rollback();
                    return false;
                }

                con.commit();
                return true;
            }
        } catch (SQLException e) {
            try { con.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try { con.setAutoCommit(oldAutoCommit); } catch (SQLException ignored) {}
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
        try {
            Connection con = ConnectDB.getConnection();
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

        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {

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