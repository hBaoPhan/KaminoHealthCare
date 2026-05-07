package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.CaLam;
import com.example.entity.NhanVien;
import com.example.entity.enums.TrangThaiCaLam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CaLamDAO {

    public List<CaLam> layTatCa() {
        List<CaLam> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM CaLam";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            while (ketQua.next()) {
                CaLam cl = new CaLam();
                cl.setMaCa(ketQua.getString("maCa"));
                cl.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                cl.setGioBatDau(ketQua.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc = ketQua.getTimestamp("gioKetThuc");
                if (ketThuc != null) {
                    cl.setGioKetThuc(ketThuc.toLocalDateTime());
                }
                cl.setTrangThai(TrangThaiCaLam.valueOf(ketQua.getString("trangThaiCaLam")));
                cl.setTienMoCa(ketQua.getDouble("tienMoCa"));
                cl.setTienKetCa(ketQua.getDouble("tienKetCa"));
                cl.setTienHeThong(ketQua.getDouble("tienHeThong"));
                cl.setGhiChu(ketQua.getString("ghiChu"));
                danhSach.add(cl);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public CaLam timTheoMa(String maCa) {
        CaLam cl = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM CaLam WHERE maCa = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maCa);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                cl = new CaLam();
                cl.setMaCa(ketQua.getString("maCa"));
                cl.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                cl.setGioBatDau(ketQua.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc2 = ketQua.getTimestamp("gioKetThuc");
                if (ketThuc2 != null) {
                    cl.setGioKetThuc(ketThuc2.toLocalDateTime());
                }
                cl.setTrangThai(TrangThaiCaLam.valueOf(ketQua.getString("trangThaiCaLam")));
                cl.setTienMoCa(ketQua.getDouble("tienMoCa"));
                cl.setTienKetCa(ketQua.getDouble("tienKetCa"));
                cl.setTienHeThong(ketQua.getDouble("tienHeThong"));
                cl.setGhiChu(ketQua.getString("ghiChu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cl;
    }

    public boolean them(CaLam cl) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO CaLam VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, cl.getMaCa());
            lenh.setString(2, cl.getNhanVien().getMaNhanVien());
            lenh.setTimestamp(3, Timestamp.valueOf(cl.getGioBatDau()));
            if (cl.getGioKetThuc() != null) {
                lenh.setTimestamp(4, Timestamp.valueOf(cl.getGioKetThuc()));
            } else {
                lenh.setNull(4, Types.TIMESTAMP);
            }
            lenh.setString(5, cl.getTrangThai().name());
            lenh.setDouble(6, cl.getTienMoCa());
            lenh.setDouble(7, cl.getTienKetCa());
            lenh.setDouble(8, cl.getTienHeThong());
            lenh.setString(9, cl.getGhiChu());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(CaLam cl) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE CaLam SET maNhanVien = ?, gioBatDau = ?, gioKetThuc = ?, trangThaiCaLam = ?, tienMoCa = ?, tienKetCa = ?, tienHeThong = ?, ghiChu = ? WHERE maCa = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, cl.getNhanVien().getMaNhanVien());
            lenh.setTimestamp(2, Timestamp.valueOf(cl.getGioBatDau()));
            if (cl.getGioKetThuc() != null) {
                lenh.setTimestamp(3, Timestamp.valueOf(cl.getGioKetThuc()));
            } else {
                lenh.setNull(3, Types.TIMESTAMP);
            }
            lenh.setString(4, cl.getTrangThai().name());
            lenh.setDouble(5, cl.getTienMoCa());
            lenh.setDouble(6, cl.getTienKetCa());
            lenh.setDouble(7, cl.getTienHeThong());
            lenh.setString(8, cl.getGhiChu());
            lenh.setString(9, cl.getMaCa());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maCa) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM CaLam WHERE maCa = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maCa);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public CaLam layCaHienTai(String maNhanVien) {
        CaLam cl = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT TOP 1 * FROM CaLam WHERE maNhanVien = ? AND trangThaiCaLam = 'DANG_MO' ORDER BY gioBatDau DESC";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maNhanVien);
            ResultSet ketQua = lenh.executeQuery();

            if (ketQua.next()) {
                cl = new CaLam();
                cl.setMaCa(ketQua.getString("maCa"));
                cl.setNhanVien(new NhanVien(ketQua.getString("maNhanVien")));
                cl.setGioBatDau(ketQua.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc = ketQua.getTimestamp("gioKetThuc");
                if (ketThuc != null) {
                    cl.setGioKetThuc(ketThuc.toLocalDateTime());
                }
                cl.setTrangThai(TrangThaiCaLam.valueOf(ketQua.getString("trangThaiCaLam")));
                cl.setTienMoCa(ketQua.getDouble("tienMoCa"));
                cl.setTienKetCa(ketQua.getDouble("tienKetCa"));
                cl.setTienHeThong(ketQua.getDouble("tienHeThong"));
                cl.setGhiChu(ketQua.getString("ghiChu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cl;
    }

    public int laySoLuongCaTrongNgay(String prefix) {
        int maxStt = 0;
        // Sử dụng MAX(maCa) thay vì COUNT(*) để không bị trùng lặp khi có dòng bị xóa
        String sql = "SELECT MAX(maCa) FROM CaLam WHERE maCa LIKE ?";
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, prefix + "%");
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        String maxMaCa = rs.getString(1); // Lấy ra mã lớn nhất (VD: CA09052602)
                        // Nếu có mã, tiến hành cắt bỏ phần chữ (prefix) để lấy phần số đuôi
                        if (maxMaCa != null && maxMaCa.length() > prefix.length()) {
                            String sttStr = maxMaCa.substring(prefix.length());
                            maxStt = Integer.parseInt(sttStr); // maxStt = 2
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Trả về số thứ tự lớn nhất hiện tại (Nếu chưa có thì trả về 0)
        return maxStt; 
    }
    // 1. Lấy danh sách ca làm theo ngày và tìm kiếm tên nhân viên (nếu có)
    public List<CaLam> layCaTheoNgayVaTen(java.time.LocalDate ngay, String tenNV) {
        List<CaLam> danhSach = new ArrayList<>();
        String sql = "SELECT cl.*, nv.tenNhanVien FROM CaLam cl " +
                     "JOIN NhanVien nv ON cl.maNhanVien = nv.maNhanVien " +
                     "WHERE CAST(cl.gioBatDau AS DATE) = ?";
        if (tenNV != null && !tenNV.trim().isEmpty()) {
            sql += " AND nv.tenNhanVien LIKE ?";
        }
        
        try (Connection con = ConnectDB.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDate(1, java.sql.Date.valueOf(ngay));
            if (tenNV != null && !tenNV.trim().isEmpty()) {
                pst.setString(2, "%" + tenNV.trim() + "%");
            }
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                CaLam cl = new CaLam();
                cl.setMaCa(rs.getString("maCa"));
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("maNhanVien"));
                nv.setTenNhanVien(rs.getString("tenNhanVien"));
                cl.setNhanVien(nv);
                cl.setGioBatDau(rs.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc = rs.getTimestamp("gioKetThuc");
                if (ketThuc != null) cl.setGioKetThuc(ketThuc.toLocalDateTime());
                cl.setTrangThai(TrangThaiCaLam.valueOf(rs.getString("trangThaiCaLam")));
                danhSach.add(cl);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // 2. Lấy toàn bộ ca làm trong 1 khoảng thời gian (Dùng cho Lịch Tuần)
    public List<CaLam> layCaTrongTuan(java.time.LocalDate tuNgay, java.time.LocalDate denNgay) {
        List<CaLam> danhSach = new ArrayList<>();
        String sql = "SELECT cl.*, nv.tenNhanVien FROM CaLam cl " +
                     "JOIN NhanVien nv ON cl.maNhanVien = nv.maNhanVien " +
                     "WHERE CAST(cl.gioBatDau AS DATE) >= ? AND CAST(cl.gioBatDau AS DATE) <= ? " +
                     "ORDER BY cl.gioBatDau ASC";
        try (Connection con = ConnectDB.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setDate(1, java.sql.Date.valueOf(tuNgay));
            pst.setDate(2, java.sql.Date.valueOf(denNgay));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                CaLam cl = new CaLam();
                cl.setMaCa(rs.getString("maCa"));
               NhanVien nv = new NhanVien();
                nv.setMaNhanVien(rs.getString("maNhanVien"));
                nv.setTenNhanVien(rs.getString("tenNhanVien"));
                cl.setNhanVien(nv);
                cl.setGioBatDau(rs.getTimestamp("gioBatDau").toLocalDateTime());
                Timestamp ketThuc = rs.getTimestamp("gioKetThuc");
                if (ketThuc != null) cl.setGioKetThuc(ketThuc.toLocalDateTime());
                cl.setTrangThai(TrangThaiCaLam.valueOf(rs.getString("trangThaiCaLam")));
                danhSach.add(cl);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return danhSach;
    }

    // 3. Kiểm tra xem nhân viên có bị trùng giờ làm không
    public boolean kiemTraTrungCa(String maNV, java.time.LocalDateTime start, java.time.LocalDateTime end, String maCaHienTai) {
        String sql = "SELECT COUNT(*) FROM CaLam WHERE maNhanVien = ? AND maCa != ? AND " +
                     "NOT (gioKetThuc <= ? OR gioBatDau >= ?)";
        try (Connection con = ConnectDB.getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, maNV);
            pst.setString(2, maCaHienTai == null ? "" : maCaHienTai);
            pst.setTimestamp(3, java.sql.Timestamp.valueOf(start));
            pst.setTimestamp(4, java.sql.Timestamp.valueOf(end));
            ResultSet rs = pst.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true; // Có trùng
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }
}
