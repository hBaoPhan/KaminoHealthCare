package com.example.dao;

import com.example.connectDB.ConnectDB;
<<<<<<< Updated upstream
import com.example.entity.PhanLoai;
=======
>>>>>>> Stashed changes
import com.example.entity.SanPham;
import com.example.entity.enums.PhanLoai;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SanPhamDAO - Data Access Object cho bảng SanPham
 * Đã hoàn thiện đầy đủ các chức năng cần thiết cho giao diện
 */
public class SanPhamDAO {

    // ==================== LẤY DANH SÁCH ====================

    public List<SanPham> layTatCa() {
        List<SanPham> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM SanPham ORDER BY maSanPham";

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSach.add(mapResultSetToSanPham(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ==================== TÌM KIẾM ====================

    public SanPham timTheoMa(String maSP) {
        String sql = "SELECT * FROM SanPham WHERE maSanPham = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSP);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSanPham(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<SanPham> timTheoMaHoacTen(String tuKhoa) {
    List<SanPham> danhSach = new ArrayList<>();
    
    if (tuKhoa == null || tuKhoa.trim().isEmpty()) {
        return layTatCa(); // nếu không nhập gì thì trả về tất cả
    }

    String sql = "SELECT * FROM SanPham WHERE maSanPham LIKE ? OR tenSanPham LIKE ?";
    
    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        String keyword = "%" + tuKhoa.trim() + "%";
        pstmt.setString(1, keyword);
        pstmt.setString(2, keyword);

        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                danhSach.add(mapResultSetToSanPham(rs));
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return danhSach;
}
    public List<SanPham> timKiemNangCao(String tuKhoa, PhanLoai phanLoai) {
        List<SanPham> danhSach = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM SanPham WHERE 1=1");

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append(" AND (maSanPham LIKE ? OR tenSanPham LIKE ?)");
        }
        if (phanLoai != null) {
            sql.append(" AND phanLoai = ?");
        }
        sql.append(" ORDER BY maSanPham");

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String keyword = "%" + tuKhoa.trim() + "%";
                pstmt.setString(index++, keyword);
                pstmt.setString(index++, keyword);
            }
            if (phanLoai != null) {
                pstmt.setString(index, phanLoai.name());
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToSanPham(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ==================== CRUD ====================

    public boolean them(SanPham sp) {
        String sql = "INSERT INTO SanPham (maSanPham, tenSanPham, phanLoai, soLuongTon, moTa, hoatChat, " +
                     "donGiaCoBan, trangThaiKinhDoanh, thue) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sp.getMaSanPham());
            pstmt.setString(2, sp.getTenSanPham());
            pstmt.setString(3, sp.getPhanLoai().name());
            pstmt.setInt(4, sp.getSoLuongTon());
            pstmt.setString(5, sp.getMoTa());
            pstmt.setString(6, sp.getHoatChat());
            pstmt.setDouble(7, sp.getDonGiaCoBan());
            pstmt.setBoolean(8, sp.isTrangThaiKinhDoanh());
            pstmt.setDouble(9, sp.getThue());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(SanPham sp) {
        String sql = "UPDATE SanPham SET tenSanPham = ?, phanLoai = ?, soLuongTon = ?, moTa = ?, " +
                     "hoatChat = ?, donGiaCoBan = ?, trangThaiKinhDoanh = ?, thue = ? WHERE maSanPham = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sp.getTenSanPham());
            pstmt.setString(2, sp.getPhanLoai().name());
            pstmt.setInt(3, sp.getSoLuongTon());
            pstmt.setString(4, sp.getMoTa());
            pstmt.setString(5, sp.getHoatChat());
            pstmt.setDouble(6, sp.getDonGiaCoBan());
            pstmt.setBoolean(7, sp.isTrangThaiKinhDoanh());
            pstmt.setDouble(8, sp.getThue());
            pstmt.setString(9, sp.getMaSanPham());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String maSP) {
        String sql = "DELETE FROM SanPham WHERE maSanPham = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSP);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
<<<<<<< Updated upstream
}
=======

    // ==================== KIỂM TRA & HỖ TRỢ ====================

    public boolean tonTaiMaSanPham(String maSP) {
        String sql = "SELECT 1 FROM SanPham WHERE maSanPham = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maSP);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật số lượng tồn (dùng khi bán hàng)
     */
    public boolean capNhatSoLuongTon(String maSP, int soLuongMoi) {
        String sql = "UPDATE SanPham SET soLuongTon = ? WHERE maSanPham = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, soLuongMoi);
            pstmt.setString(2, maSP);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==================== MAPPING RESULTSET ====================
    private SanPham mapResultSetToSanPham(ResultSet rs) throws SQLException {
        SanPham sp = new SanPham();
        sp.setMaSanPham(rs.getString("maSanPham"));
        sp.setTenSanPham(rs.getString("tenSanPham"));
        sp.setPhanLoai(PhanLoai.valueOf(rs.getString("phanLoai")));
        sp.setSoLuongTon(rs.getInt("soLuongTon"));
        sp.setMoTa(rs.getString("moTa"));
        sp.setHoatChat(rs.getString("hoatChat"));
        sp.setDonGiaCoBan(rs.getDouble("donGiaCoBan"));
        sp.setTrangThaiKinhDoanh(rs.getBoolean("trangThaiKinhDoanh"));
        sp.setThue(rs.getDouble("thue"));
        return sp;
    }

    // ==================== PHƯƠNG THỨC BỔ SUNG (nếu cần sau này) ====================

    public List<SanPham> laySanPhamDangKinhDoanh() {
        List<SanPham> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE trangThaiKinhDoanh = 1 ORDER BY maSanPham";

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                danhSach.add(mapResultSetToSanPham(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public List<SanPham> timTheoPhanLoai(PhanLoai phanLoai) {
        List<SanPham> danhSach = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE phanLoai = ? ORDER BY maSanPham";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phanLoai.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToSanPham(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }
}
>>>>>>> Stashed changes
