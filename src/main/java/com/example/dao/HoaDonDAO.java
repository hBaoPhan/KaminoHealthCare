package com.example.dao;

import com.example.connectDB.ConnectDB;
import com.example.entity.CaLam;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.DonThuoc;
import com.example.entity.HoaDon;
import com.example.entity.Lo;
import com.example.entity.SuPhanBoLo;
import com.example.entity.enums.LoaiHoaDon;
import com.example.entity.enums.PhuongThucThanhToan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    // ====================== HELPER MAPPING ======================

    /**
     * Map một dòng ResultSet thành đối tượng HoaDon.
     * Tránh lặp lại cùng đoạn code trong layTatCa(), timTheoMa(), timKiem().
     */
    private HoaDon mapHoaDon(ResultSet rs,
            NhanVienDAO nvDAO,
            KhachHangDAO khDAO,
            KhuyenMaiDAO kmDAO) throws SQLException {
        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(rs.getString("maHoaDon"));
        hd.setThoiGianTao(rs.getTimestamp("thoiGianTao").toLocalDateTime());
        hd.setNhanVien(nvDAO.timTheoMa(rs.getString("maNhanVien")));
        hd.setTrangThaiThanhToan(rs.getBoolean("trangThaiThanhToan"));

        String maKH = rs.getString("maKhachHang");
        if (maKH != null && !maKH.trim().isEmpty())
            hd.setKhachHang(khDAO.timTheoMa(maKH));

        String maKM = rs.getString("maKhuyenMai");
        if (maKM != null)
            hd.setKhuyenMai(kmDAO.timTheoMa(maKM));

        hd.setLoaiHoaDon(LoaiHoaDon.valueOf(rs.getString("loaiHoaDon")));
        hd.setCa(new CaLam(rs.getString("maCa")));
        hd.setGhiChu(rs.getString("ghiChu"));

        String maHDDT = rs.getString("maHoaDonDoiTra");
        if (maHDDT != null)
            hd.setHoaDonDoiTra(new HoaDon(maHDDT));

        String maDT = rs.getString("maDonThuoc");
        if (maDT != null)
            hd.setDonThuoc(new DonThuoc(maDT));

        String pttt = rs.getString("phuongThucThanhToan");
        if (pttt != null)
            hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));

        return hd;
    }

    public List<HoaDon> layTatCa() {
        List<HoaDon> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM HoaDon ORDER BY thoiGianTao DESC";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            while (ketQua.next()) {
                danhSach.add(mapHoaDon(ketQua, nvDAO, khDAO, kmDAO));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    public HoaDon timTheoMa(String maHD) {
        HoaDon hd = null;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM HoaDon WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maHD);
            ResultSet ketQua = lenh.executeQuery();

            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            if (ketQua.next()) {
                hd = mapHoaDon(ketQua, nvDAO, khDAO, kmDAO);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hd;
    }

    /**
     * Lấy hóa đơn bán hàng chưa thanh toán mới nhất của nhân viên.
     * Kèm theo danh sách ChiTietHoaDon đầy đủ (gồm cả DonViQuyDoi với SanPham).
     */
    public HoaDon layHoaDonChuaThanhToan(String maNhanVien) {
        HoaDon hd = null;
        String sql = "SELECT TOP 1 * FROM HoaDon " +
                "WHERE maNhanVien = ? AND loaiHoaDon = 'BAN_HANG' AND trangThaiThanhToan = 0 " +
                "ORDER BY thoiGianTao DESC";
        try {
            Connection con = ConnectDB.getConnection();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            DonViQuyDoiDAO dvDAO = new DonViQuyDoiDAO();

            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, maNhanVien);
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        hd = mapHoaDon(rs, new NhanVienDAO(), khDAO, kmDAO);
                    }
                }
            }

            // Load ChiTietHoaDon
            if (hd != null) {
                List<ChiTietHoaDon> dsChiTiet = new ArrayList<>();
                String sqlCT = "SELECT ct.*, sp.maSanPham, sp.tenSanPham, sp.donGiaCoBan, sp.thue, " +
                        "dv.tenDonVi, dv.heSoQuyDoi " +
                        "FROM ChiTietHoaDon ct " +
                        "JOIN DonViQuyDoi dv ON ct.maDonVi = dv.maDonVi " +
                        "JOIN SanPham sp ON dv.maSanPham = sp.maSanPham " +
                        "WHERE ct.maHoaDon = ?";
                try (PreparedStatement pstCT = con.prepareStatement(sqlCT)) {
                    pstCT.setString(1, hd.getMaHoaDon());
                    try (ResultSet rsCT = pstCT.executeQuery()) {
                        while (rsCT.next()) {
                            ChiTietHoaDon ct = new ChiTietHoaDon();
                            ct.setHoaDon(hd);
                            ct.setSoLuong(rsCT.getInt("soLuong"));
                            ct.setDonGia(rsCT.getDouble("donGia"));
                            ct.setLaQuaTangKem(rsCT.getBoolean("laQuaTangKem"));

                            // Build DonViQuyDoi with SanPham
                            com.example.entity.SanPham sp = new com.example.entity.SanPham();
                            sp.setMaSanPham(rsCT.getString("maSanPham"));
                            sp.setTenSanPham(rsCT.getString("tenSanPham"));
                            sp.setDonGiaCoBan(rsCT.getDouble("donGiaCoBan"));
                            sp.setThue(rsCT.getDouble("thue"));

                            com.example.entity.DonViQuyDoi dv = new com.example.entity.DonViQuyDoi();
                            dv.setMaDonVi(rsCT.getString("maDonVi"));
                            dv.setHeSoQuyDoi(rsCT.getInt("heSoQuyDoi"));
                            dv.setSanPham(sp);

                            String tenDonViStr = rsCT.getString("tenDonVi");
                            if (tenDonViStr != null) {
                                try {
                                    dv.setTenDonVi(com.example.entity.enums.DonVi.valueOf(tenDonViStr));
                                } catch (IllegalArgumentException ignored) {
                                }
                            }

                            ct.setDonViQuyDoi(dv);
                            dsChiTiet.add(ct);
                        }
                    }
                }
                hd.setDsChiTiet(dsChiTiet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hd;
    }

    public boolean them(HoaDon hd) throws SQLException {
        String truyVan = "INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc, phuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {
            lenh.setString(1, hd.getMaHoaDon());
            lenh.setTimestamp(2, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(3, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(4, hd.isTrangThaiThanhToan());
            lenh.setString(5, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            lenh.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(7, hd.getLoaiHoaDon().name());
            lenh.setString(8, hd.getCa().getMaCa());
            lenh.setString(9, hd.getGhiChu());
            lenh.setString(10, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(11, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
            lenh.setString(12, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan().name() : null);
            return lenh.executeUpdate() > 0;
        }
    }

    public boolean capNhat(HoaDon hd) throws SQLException {
        String truyVan = "UPDATE HoaDon SET thoiGianTao = ?, maNhanVien = ?, trangThaiThanhToan = ?, maKhachHang = ?, maKhuyenMai = ?, loaiHoaDon = ?, maCa = ?, ghiChu = ?, maHoaDonDoiTra = ?, maDonThuoc = ?, phuongThucThanhToan = ? WHERE maHoaDon = ?";
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {
            lenh.setTimestamp(1, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(2, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(3, hd.isTrangThaiThanhToan());
            lenh.setString(4, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            lenh.setString(5, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(6, hd.getLoaiHoaDon().name());
            lenh.setString(7, hd.getCa().getMaCa());
            lenh.setString(8, hd.getGhiChu());
            lenh.setString(9, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(10, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
            lenh.setString(11, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan().name() : null);
            lenh.setString(12, hd.getMaHoaDon());
            return lenh.executeUpdate() > 0;
        }
    }

    public boolean capNhatTrangThaiThanhToan(String maHD, boolean trangThai) throws SQLException {
        try (PreparedStatement pst = ConnectDB.getConnection()
                .prepareStatement("UPDATE HoaDon SET trangThaiThanhToan = ? WHERE maHoaDon = ?")) {
            pst.setBoolean(1, trangThai);
            pst.setString(2, maHD);
            return pst.executeUpdate() > 0;
        }
    }

    public boolean xoa(String maHD) throws SQLException {
        try (PreparedStatement pst = ConnectDB.getConnection()
                .prepareStatement("DELETE FROM HoaDon WHERE maHoaDon = ?")) {
            pst.setString(1, maHD);
            return pst.executeUpdate() > 0;
        }
    }

    public List<HoaDon> timKiem(String maHD, LocalDate ngayTao) {
        List<HoaDon> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            StringBuilder truyVan = new StringBuilder("SELECT * FROM HoaDon WHERE 1=1");
            if (maHD != null && !maHD.trim().isEmpty()) {
                truyVan.append(" AND maHoaDon LIKE ?");
            }
            if (ngayTao != null) {
                truyVan.append(" AND thoiGianTao >= ? AND thoiGianTao < ?");
            }
            truyVan.append(" ORDER BY thoiGianTao DESC");
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan.toString());
            int paramIndex = 1;
            if (maHD != null && !maHD.trim().isEmpty()) {
                lenh.setString(paramIndex++, "%" + maHD.trim() + "%");
            }
            if (ngayTao != null) {
                lenh.setTimestamp(paramIndex++, Timestamp.valueOf(ngayTao.atStartOfDay()));
                lenh.setTimestamp(paramIndex++, Timestamp.valueOf(ngayTao.plusDays(1).atStartOfDay()));
            }

            ResultSet ketQua = lenh.executeQuery();
            ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            while (ketQua.next()) {
                HoaDon hd = mapHoaDon(ketQua, nvDAO, khDAO, kmDAO);
                hd.setDsChiTiet(ctDAO.layTheoMaHoaDon(hd.getMaHoaDon()));
                danhSach.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    // Kiểm tra hóa đơn đã từng được đổi/trả chưa
    public boolean daTungDoiTra(String maHDGoc) {
        boolean check = false;
        try {
            Connection con = ConnectDB.getConnection();
            // Tìm các hóa đơn có maHoaDonDoiTra trỏ về HD gốc này
            String sql = "SELECT COUNT(*) FROM HoaDon WHERE maHoaDonDoiTra = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maHDGoc);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                check = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return check;
    }

    // Tìm kiếm hóa đơn gốc để đổi hàng (kèm kiểm tra điều kiện)
    public HoaDon layHoaDonDeDoi(String maHD) {
        HoaDon hd = null;
        try {
            Connection con = ConnectDB.getConnection();

            // 1. Truy vấn hóa đơn kèm tính số ngày chênh lệch để kiểm tra quy định 7 ngày
            // Chỉ lấy hóa đơn đã thanh toán thành công (trangThaiThanhToan = 1)
            String sql = "SELECT *, DATEDIFF(DAY, thoiGianTao, GETDATE()) as SoNgay " +
                    "FROM HoaDon WHERE maHoaDon = ? AND trangThaiThanhToan = 1";

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Kiểm tra quy định thời hạn 7 ngày
                int soNgay = rs.getInt("SoNgay");
                if (soNgay > 7) {
                    return null; // Hóa đơn quá hạn đổi trả
                }

                // Kiểm tra quy định đổi 1 lần duy nhất (maHoaDonDoiTra trỏ về HD này)
                if (daTungDoiTra(maHD)) {
                    return null; // Hóa đơn này đã được dùng để đổi/trả trước đó
                }

                // 2. Mapping dữ liệu vào thực thể HoaDon
                hd = new HoaDon();
                hd.setMaHoaDon(rs.getString("maHoaDon"));

                Timestamp timestamp = rs.getTimestamp("thoiGianTao");
                if (timestamp != null) {
                    hd.setThoiGianTao(timestamp.toLocalDateTime());
                }

                hd.setGhiChu(rs.getString("ghiChu"));

                // --- QUAN TRỌNG: Nạp đầy đủ thông tin Nhân viên và Khách hàng ---
                // Lấy thông tin Nhân viên đã tạo hóa đơn gốc
                String maNV = rs.getString("maNhanVien");
                if (maNV != null) {
                    NhanVienDAO nvDAO = new NhanVienDAO();
                    hd.setNhanVien(nvDAO.timTheoMa(maNV));
                }

                // Lấy thông tin Khách hàng đã mua đơn hàng này
                String maKH = rs.getString("maKhachHang");
                if (maKH != null) {
                    KhachHangDAO khDAO = new KhachHangDAO();
                    hd.setKhachHang(khDAO.timTheoMa(maKH));
                }

                // Lấy thông tin Khuyến mãi (để áp dụng chiết khấu phần trăm nếu có)
                String maKM = rs.getString("maKhuyenMai");
                if (maKM != null) {
                    KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
                    hd.setKhuyenMai(kmDAO.timTheoMa(maKM));
                }

                // 3. Load danh sách chi tiết sản phẩm để hiển thị lên bảng "Hàng đã mua"
                ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
                hd.setDsChiTiet(ctDAO.layTheoMaHoaDon(maHD));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hd;
    }

    public int laySoLuongHoaDonTrongNgay(String loaiPrefix, String ngayThangNam) {
        int count = 0;
        try {
            Connection con = ConnectDB.getConnection();
            // Tìm các mã hóa đơn bắt đầu bằng Prefix (vd: HDD290426)
            String sql = "SELECT COUNT(*) FROM HoaDon WHERE maHoaDon LIKE ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, loaiPrefix + ngayThangNam + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public double tinhTongDoanhThuCa(String maCa) {
        double tong = 0;
        String sql = "SELECT SUM(ct.soLuong * ct.donGia * (1 + sp.thue/100)) as tong " +
                "FROM ChiTietHoaDon ct " +
                "JOIN HoaDon hd ON ct.maHoaDon = hd.maHoaDon " +
                "JOIN DonViQuyDoi dv ON ct.maDonVi = dv.maDonVi " +
                "JOIN SanPham sp ON dv.maSanPham = sp.maSanPham " +
                "WHERE hd.maCa = ? AND hd.trangThaiThanhToan = 1";

        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, maCa);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                tong = rs.getDouble("tong");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tong;
    }



    /**
     * Đếm số lượng hóa đơn trong ngày theo loại để sinh mã tuần tự
     */
    public int demHoaDonTrongNgay(LoaiHoaDon loaiHoaDon) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM HoaDon WHERE CAST(thoiGianTao as DATE) = CAST(GETDATE() as DATE) AND loaiHoaDon = ?";
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setString(1, loaiHoaDon.name());
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }


}
