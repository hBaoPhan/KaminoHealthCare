package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.enums.LoaiSanPham;
import com.example.entity.SanPham;

import java.sql.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SanPhamDAO {

    // ================== CÁC PHƯƠNG THỨC ĐÃ CÓ ==================
    public List<SanPham> layTatCa() {
        List<SanPham> danhSach = new ArrayList<>();
        Connection ketNoi = ConnectDB.getConnection();
        try (Statement lenh = ketNoi.createStatement();
             ResultSet ketQua = lenh.executeQuery("SELECT * FROM SanPham")) {

            while (ketQua.next()) {
                danhSach.add(mapResultSetToSanPham(ketQua));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public SanPham timTheoMa(String maSP) {
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement("SELECT * FROM SanPham WHERE maSanPham = ?")) {

            lenh.setString(1, maSP);
            try (ResultSet ketQua = lenh.executeQuery()) {
                if (ketQua.next()) {
                    return mapResultSetToSanPham(ketQua);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(SanPham sp) {
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "INSERT INTO SanPham (maSanPham, tenSanPham, loaiSanPham, soLuongTon, moTa, hoatChat, donGiaCoBan, trangThaiKinhDoanh, thue) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            setParameters(lenh, sp);
            return lenh.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(SanPham sp) {
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "UPDATE SanPham SET tenSanPham = ?, loaiSanPham = ?, soLuongTon = ?, moTa = ?, hoatChat = ?, " +
                        "donGiaCoBan = ?, trangThaiKinhDoanh = ?, thue = ? WHERE maSanPham = ?")) {

            lenh.setString(1, sp.getTenSanPham());
            lenh.setString(2, sp.getLoaiSanPham().name());
            lenh.setInt(3, sp.getSoLuongTon());
            lenh.setString(4, sp.getMoTa());
            lenh.setString(5, sp.getHoatChat());
            lenh.setDouble(6, sp.getDonGiaCoBan());
            lenh.setBoolean(7, sp.isTrangThaiKinhDoanh());
            lenh.setDouble(8, sp.getThue());
            lenh.setString(9, sp.getMaSanPham());

            return lenh.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String maSP) {
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement("DELETE FROM SanPham WHERE maSanPham = ?")) {

            lenh.setString(1, maSP);
            return lenh.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<SanPham> timTheoMaHoacTen(String tuKhoa) {
        List<SanPham> danhSach = new ArrayList<>();
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "SELECT * FROM SanPham WHERE maSanPham LIKE ? OR tenSanPham LIKE ?")) {

            String keyword = "%" + tuKhoa + "%";
            lenh.setString(1, keyword);
            lenh.setString(2, keyword);

            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    danhSach.add(mapResultSetToSanPham(ketQua));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public List<SanPham> timKiemGoiY(String tuKhoa) {
        List<SanPham> ds = new ArrayList<>();
        try {
            Connection con = ConnectDB.getConnection();
            String sql = "SELECT * FROM SanPham WHERE maSanPham LIKE ? OR tenSanPham LIKE ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + tuKhoa + "%");
            stmt.setString(2, "%" + tuKhoa + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setMaSanPham(rs.getString("maSanPham"));
                sp.setTenSanPham(rs.getString("tenSanPham"));
                sp.setDonGiaCoBan(rs.getDouble("donGiaCoBan"));
                sp.setThue(rs.getDouble("thue"));
                ds.add(sp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }


    // ================== CÁC PHƯƠNG THỨC MỚI ĐƯỢC THÊM ==================

    /**
     * Lọc theo phân loại (ETC, OTC, TPCN)
     */
    public List<SanPham> timTheoPhanLoai(LoaiSanPham loaiSanPham) {
        List<SanPham> danhSach = new ArrayList<>();
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement("SELECT * FROM SanPham WHERE loaiSanPham = ?")) {

            lenh.setString(1, loaiSanPham.name());
            try (ResultSet rs = lenh.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToSanPham(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    /**
     * Lấy tất cả sản phẩm đang kinh doanh
     */
    public List<SanPham> laySanPhamDangKinhDoanh() {
        List<SanPham> danhSach = new ArrayList<>();
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "SELECT * FROM SanPham WHERE trangThaiKinhDoanh = 1")) {

            try (ResultSet rs = lenh.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToSanPham(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    /**
     * Kiểm tra mã sản phẩm đã tồn tại chưa
     */
    public boolean tonTaiMaSanPham(String maSP) {
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "SELECT COUNT(*) FROM SanPham WHERE maSanPham = ?")) {

            lenh.setString(1, maSP);
            try (ResultSet rs = lenh.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật số lượng tồn (dùng khi bán hàng)
     */
    public boolean capNhatSoLuongTon(String maSP, int soLuongMoi) {
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(
                "UPDATE SanPham SET soLuongTon = ? WHERE maSanPham = ?")) {

            lenh.setInt(1, soLuongMoi);
            lenh.setString(2, maSP);
            return lenh.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tạo mã sản phẩm theo quy tắc:
     * [PhanLoai]-[Viết tắt 3 chữ]-[Số thứ tự 3 chữ số]
     * <p>
     * - Tên 1 từ: lấy 3 chữ cái đầu
     * - Tên >= 2 từ: lấy chữ cái đầu của tối đa 3 từ
     * <p>
     * Ví dụ: Panadol (OTC) -> OTC-PAN-001
     */
    public String taoMaSanPhamTuDong(LoaiSanPham loaiSanPham, String tenSanPham) {
        String vietTat = taoVietTatTenSanPham(tenSanPham);
        String prefix = loaiSanPham.name() + "-" + vietTat + "-";

        String sql = "SELECT MAX(maSanPham) FROM SanPham WHERE maSanPham LIKE ?";
        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement ps = ketNoi.prepareStatement(sql)) {

            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                int next = 1;
                if (rs.next()) {
                    String maxMa = rs.getString(1);
                    if (maxMa != null && maxMa.startsWith(prefix) && maxMa.length() >= prefix.length() + 3) {
                        String so = maxMa.substring(prefix.length());
                        try {
                            next = Integer.parseInt(so) + 1;
                        } catch (NumberFormatException ignored) {
                            next = 1;
                        }
                    }
                }
                return prefix + String.format("%03d", next);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // fallback nếu lỗi DB
        return prefix + "001";
    }

    /**
     * Tìm kiếm nâng cao (kết hợp từ khóa + phân loại)
     */
    public List<SanPham> timKiemNangCao(String tuKhoa, LoaiSanPham loaiSanPham) {
        List<SanPham> danhSach = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM SanPham WHERE 1=1");

        if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
            sql.append(" AND (maSanPham LIKE ? OR tenSanPham LIKE ?)");
        }
        if (loaiSanPham != null) {
            sql.append(" AND loaiSanPham = ?");
        }

        Connection ketNoi = ConnectDB.getConnection();
        try (PreparedStatement lenh = ketNoi.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                String keyword = "%" + tuKhoa.trim() + "%";
                lenh.setString(paramIndex++, keyword);
                lenh.setString(paramIndex++, keyword);
            }
            if (loaiSanPham != null) {
                lenh.setString(paramIndex, loaiSanPham.name());
            }

            try (ResultSet rs = lenh.executeQuery()) {
                while (rs.next()) {
                    danhSach.add(mapResultSetToSanPham(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // ================== HÀM HỖ TRỢ (giúp code sạch hơn) ==================
    private SanPham mapResultSetToSanPham(ResultSet rs) throws SQLException {
        SanPham sp = new SanPham();
        sp.setMaSanPham(rs.getString("maSanPham"));
        sp.setTenSanPham(rs.getString("tenSanPham"));
        sp.setLoaiSanPham(LoaiSanPham.valueOf(rs.getString("loaiSanPham")));
        sp.setSoLuongTon(rs.getInt("soLuongTon"));
        sp.setMoTa(rs.getString("moTa"));
        sp.setHoatChat(rs.getString("hoatChat"));
        sp.setDonGiaCoBan(rs.getDouble("donGiaCoBan"));
        sp.setTrangThaiKinhDoanh(rs.getBoolean("trangThaiKinhDoanh"));
        sp.setThue(rs.getDouble("thue"));
        return sp;
    }

    private void setParameters(PreparedStatement lenh, SanPham sp) throws SQLException {
        lenh.setString(1, sp.getMaSanPham());
        lenh.setString(2, sp.getTenSanPham());
        lenh.setString(3, sp.getLoaiSanPham().name());
        lenh.setInt(4, sp.getSoLuongTon());
        lenh.setString(5, sp.getMoTa());
        lenh.setString(6, sp.getHoatChat());
        lenh.setDouble(7, sp.getDonGiaCoBan());
        lenh.setBoolean(8, sp.isTrangThaiKinhDoanh());
        lenh.setDouble(9, sp.getThue());
    }

    private String taoVietTatTenSanPham(String tenSanPham) {
        if (tenSanPham == null) {
            return "XXX";
        }

        String s = Normalizer.normalize(tenSanPham, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");
        s = s.toUpperCase(Locale.ROOT);
        s = s.replaceAll("[^A-Z0-9\\s]+", " ");
        s = s.replaceAll("\\s+", " ").trim();

        if (s.isEmpty()) {
            return "XXX";
        }

        String[] parts = s.split(" ");
        if (parts.length == 1) {
            String p = parts[0];
            return (p.length() >= 3 ? p.substring(0, 3) : (p + "XXX").substring(0, 3));
        }

        StringBuilder sb = new StringBuilder(3);
        for (int i = 0; i < parts.length && sb.length() < 3; i++) {
            if (!parts[i].isEmpty()) {
                sb.append(parts[i].charAt(0));
            }
        }
        while (sb.length() < 3) {
            sb.append('X');
        }
        return sb.toString();
    }

}