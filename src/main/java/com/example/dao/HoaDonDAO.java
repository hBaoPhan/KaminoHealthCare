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

    public List<HoaDon> layTatCa() {
        List<HoaDon> danhSach = new ArrayList<>();
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "SELECT * FROM HoaDon";
            Statement lenh = ketNoi.createStatement();
            ResultSet ketQua = lenh.executeQuery(truyVan);

            NhanVienDAO nvDAO = new NhanVienDAO();
            KhachHangDAO khDAO = new KhachHangDAO();
            KhuyenMaiDAO kmDAO = new KhuyenMaiDAO();
            while (ketQua.next()) {
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(nvDAO.timTheoMa(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                
                // CẬP NHẬT: Kiểm tra null trước khi tìm khách hàng
                String maKH = ketQua.getString("maKhachHang");
                if (maKH != null && !maKH.trim().isEmpty()) {
                    hd.setKhachHang(khDAO.timTheoMa(maKH));
                }

                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null)
                    hd.setKhuyenMai(kmDAO.timTheoMa(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null)
                    hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null)
                    hd.setDonThuoc(new DonThuoc(maDT));
                String pttt = ketQua.getString("phuongThucThanhToan");
                if (pttt != null)
                    hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));

                danhSach.add(hd);
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
                hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(nvDAO.timTheoMa(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                
                // CẬP NHẬT: Kiểm tra null trước khi tìm khách hàng
                String maKH = ketQua.getString("maKhachHang");
                if (maKH != null && !maKH.trim().isEmpty()) {
                    hd.setKhachHang(khDAO.timTheoMa(maKH));
                }

                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null)
                    hd.setKhuyenMai(kmDAO.timTheoMa(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null)
                    hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null)
                    hd.setDonThuoc(new DonThuoc(maDT));
                String pttt = ketQua.getString("phuongThucThanhToan");
                if (pttt != null)
                    hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));
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
                        hd = new HoaDon();
                        hd.setMaHoaDon(rs.getString("maHoaDon"));
                        hd.setThoiGianTao(rs.getTimestamp("thoiGianTao").toLocalDateTime());
                        hd.setTrangThaiThanhToan(rs.getBoolean("trangThaiThanhToan"));
                        hd.setGhiChu(rs.getString("ghiChu"));
                        hd.setCa(new CaLam(rs.getString("maCa")));

                        String maKH = rs.getString("maKhachHang");
                        if (maKH != null && !maKH.trim().isEmpty())
                            hd.setKhachHang(khDAO.timTheoMa(maKH));

                        String maKM = rs.getString("maKhuyenMai");
                        if (maKM != null)
                            hd.setKhuyenMai(kmDAO.timTheoMa(maKM));

                        String pttt = rs.getString("phuongThucThanhToan");
                        if (pttt != null)
                            hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));
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
                                } catch (IllegalArgumentException ignored) {}
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

    public boolean them(HoaDon hd) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc, phuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, hd.getMaHoaDon());
            lenh.setTimestamp(2, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(3, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(4, hd.isTrangThaiThanhToan());
            
            // Đảm bảo không bị lỗi NullPointer khi getKhachHang() bị null
            lenh.setString(5, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            
            lenh.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(7, hd.getLoaiHoaDon().name());
            lenh.setString(8, hd.getCa().getMaCa());
            lenh.setString(9, hd.getGhiChu());
            lenh.setString(10, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(11, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
            lenh.setString(12, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan().name() : null);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean capNhat(HoaDon hd) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "UPDATE HoaDon SET thoiGianTao = ?, maNhanVien = ?, trangThaiThanhToan = ?, maKhachHang = ?, maKhuyenMai = ?, loaiHoaDon = ?, maCa = ?, ghiChu = ?, maHoaDonDoiTra = ?, maDonThuoc = ?, phuongThucThanhToan = ? WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setTimestamp(1, Timestamp.valueOf(hd.getThoiGianTao()));
            lenh.setString(2, hd.getNhanVien().getMaNhanVien());
            lenh.setBoolean(3, hd.isTrangThaiThanhToan());
            
            // Đảm bảo không bị lỗi NullPointer khi getKhachHang() bị null
            lenh.setString(4, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
            
            lenh.setString(5, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
            lenh.setString(6, hd.getLoaiHoaDon().name());
            lenh.setString(7, hd.getCa().getMaCa());
            lenh.setString(8, hd.getGhiChu());
            lenh.setString(9, hd.getHoaDonDoiTra() != null ? hd.getHoaDonDoiTra().getMaHoaDon() : null);
            lenh.setString(10, hd.getDonThuoc() != null ? hd.getDonThuoc().getMaDonThuoc() : null);
            lenh.setString(11, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan().name() : null);
            lenh.setString(12, hd.getMaHoaDon());
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
    }

    public boolean xoa(String maHD) {
        int soDongThayDoi = 0;
        try {
            Connection ketNoi = ConnectDB.getConnection();
            String truyVan = "DELETE FROM HoaDon WHERE maHoaDon = ?";
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan);
            lenh.setString(1, maHD);
            soDongThayDoi = lenh.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soDongThayDoi > 0;
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
                HoaDon hd = new HoaDon();
                hd.setMaHoaDon(ketQua.getString("maHoaDon"));
                hd.setThoiGianTao(ketQua.getTimestamp("thoiGianTao").toLocalDateTime());
                hd.setNhanVien(nvDAO.timTheoMa(ketQua.getString("maNhanVien")));
                hd.setTrangThaiThanhToan(ketQua.getBoolean("trangThaiThanhToan"));
                
                // CẬP NHẬT: Kiểm tra null trước khi tìm khách hàng
                String maKH = ketQua.getString("maKhachHang");
                if (maKH != null && !maKH.trim().isEmpty()) {
                    hd.setKhachHang(khDAO.timTheoMa(maKH));
                }

                String maKM = ketQua.getString("maKhuyenMai");
                if (maKM != null)
                    hd.setKhuyenMai(kmDAO.timTheoMa(maKM));
                hd.setLoaiHoaDon(LoaiHoaDon.valueOf(ketQua.getString("loaiHoaDon")));
                hd.setCa(new CaLam(ketQua.getString("maCa")));
                hd.setGhiChu(ketQua.getString("ghiChu"));
                String maHDDT = ketQua.getString("maHoaDonDoiTra");
                if (maHDDT != null)
                    hd.setHoaDonDoiTra(new HoaDon(maHDDT));
                String maDT = ketQua.getString("maDonThuoc");
                if (maDT != null)
                    hd.setDonThuoc(new DonThuoc(maDT));
                String pttt = ketQua.getString("phuongThucThanhToan");
                if (pttt != null)
                    hd.setPhuongThucThanhToan(PhuongThucThanhToan.valueOf(pttt));

                hd.setDsChiTiet(ctDAO.layTheoMaHoaDon(hd.getMaHoaDon()));
                danhSach.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSach;
    }

    /**
     * Hàm thực thi toàn bộ luồng đổi hàng trong 1 Transaction duy nhất
     */
    public boolean luuGiaoDichDoiHang(HoaDon hoaDonMoi,
            List<SuPhanBoLo> dsTraLai,
            List<ChiTietHoaDon> dsChiTietMoi,
            List<SuPhanBoLo> dsPhanBoMoi) {
        Connection ketNoi = null;
        try {
            ketNoi = ConnectDB.getConnection();
            ketNoi.setAutoCommit(false);

            // 1. XỬ LÝ HÀNG TRẢ: Cộng lại số lượng tồn kho cho các Lô cũ
            if (dsTraLai != null && !dsTraLai.isEmpty()) {
                String capNhatLoTra = "UPDATE Lo SET soLuongSanPham = soLuongSanPham + ? WHERE maLo = ?";
                try (PreparedStatement psLoTra = ketNoi.prepareStatement(capNhatLoTra)) {
                    for (SuPhanBoLo spTra : dsTraLai) {
                        psLoTra.setInt(1, spTra.getSoLuong());
                        psLoTra.setString(2, spTra.getLo().getMaLo());
                        psLoTra.addBatch();
                    }
                    psLoTra.executeBatch();
                }
            }

            // 2. LƯU HÓA ĐƠN MỚI
            String themHoaDon = "INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc, phuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psHoaDon = ketNoi.prepareStatement(themHoaDon)) {
                psHoaDon.setString(1, hoaDonMoi.getMaHoaDon());
                psHoaDon.setTimestamp(2, Timestamp.valueOf(hoaDonMoi.getThoiGianTao()));
                psHoaDon.setString(3, hoaDonMoi.getNhanVien().getMaNhanVien());
                psHoaDon.setBoolean(4, hoaDonMoi.isTrangThaiThanhToan());
                psHoaDon.setString(5, hoaDonMoi.getKhachHang().getMaKhachHang());
                psHoaDon.setString(6, hoaDonMoi.getKhuyenMai() != null ? hoaDonMoi.getKhuyenMai().getMaKhuyenMai() : null);
                psHoaDon.setString(7, hoaDonMoi.getLoaiHoaDon().name());
                psHoaDon.setString(8, hoaDonMoi.getCa().getMaCa());
                psHoaDon.setString(9, hoaDonMoi.getGhiChu());
                psHoaDon.setString(10, hoaDonMoi.getHoaDonDoiTra() != null ? hoaDonMoi.getHoaDonDoiTra().getMaHoaDon() : null);
                psHoaDon.setString(11, hoaDonMoi.getDonThuoc() != null ? hoaDonMoi.getDonThuoc().getMaDonThuoc() : null);
                psHoaDon.setString(12, hoaDonMoi.getPhuongThucThanhToan() != null ? hoaDonMoi.getPhuongThucThanhToan().name() : null);
                psHoaDon.executeUpdate();
            }

            // 3. LƯU CHI TIẾT HÓA ĐƠN MỚI
            if (dsChiTietMoi != null && !dsChiTietMoi.isEmpty()) {
                String themChiTiet = "INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuong, donGia, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psChiTiet = ketNoi.prepareStatement(themChiTiet)) {
                    for (ChiTietHoaDon ct : dsChiTietMoi) {
                        psChiTiet.setString(1, hoaDonMoi.getMaHoaDon());
                        psChiTiet.setString(2, ct.getDonViQuyDoi().getMaDonVi());
                        psChiTiet.setInt(3, ct.getSoLuong());
                        psChiTiet.setDouble(4, ct.getDonGia());
                        psChiTiet.setBoolean(5, ct.isLaQuaTangKem());
                        psChiTiet.addBatch();
                    }
                    psChiTiet.executeBatch();
                }
            }

            // 4. XỬ LÝ HÀNG MỚI: Trừ tồn kho và lưu Sự Phân Bổ Lô
            if (dsPhanBoMoi != null && !dsPhanBoMoi.isEmpty()) {
                String ktLo = "SELECT soLuongSanPham FROM Lo WHERE maLo = ?";
                String capNhatLoMoi = "UPDATE Lo SET soLuongSanPham = soLuongSanPham - ? WHERE maLo = ?";
                String themPhanBo = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong) VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement psKtLo = ketNoi.prepareStatement(ktLo);
                     PreparedStatement psLoMoi = ketNoi.prepareStatement(capNhatLoMoi);
                     PreparedStatement psPhanBo = ketNoi.prepareStatement(themPhanBo)) {
                     
                    for (SuPhanBoLo spMoi : dsPhanBoMoi) {
                        psKtLo.setString(1, spMoi.getLo().getMaLo());
                        try (ResultSet rsKt = psKtLo.executeQuery()) {
                            if (rsKt.next()) {
                                int soLuongHienTai = rsKt.getInt("soLuongSanPham");
                                if (soLuongHienTai < spMoi.getSoLuong()) {
                                    throw new RuntimeException("Lô " + spMoi.getLo().getMaLo() + " không đủ số lượng để đổi!");
                                }
                            } else {
                                throw new RuntimeException("Không tìm thấy Lô " + spMoi.getLo().getMaLo());
                            }
                        }

                        psLoMoi.setInt(1, spMoi.getSoLuong());
                        psLoMoi.setString(2, spMoi.getLo().getMaLo());
                        psLoMoi.addBatch();

                        psPhanBo.setString(1, hoaDonMoi.getMaHoaDon());
                        psPhanBo.setString(2, spMoi.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
                        psPhanBo.setString(3, spMoi.getLo().getMaLo());
                        psPhanBo.setInt(4, spMoi.getSoLuong());
                        psPhanBo.addBatch();
                    }
                    psLoMoi.executeBatch();
                    psPhanBo.executeBatch();
                }
            }

            ketNoi.commit();
            return true;

        } catch (Exception e) {
            if (ketNoi != null) {
                try {
                    ketNoi.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (ketNoi != null) {
                try {
                    ketNoi.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
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
     * Lưu HoaDon + ChiTietHoaDon với trạng thái chưa thanh toán.
     * Không trừ kho. Nếu HoaDon đã tồn tại, chỉ lưu thêm chi tiết (idempotent với maHoaDon).
     */
    public boolean luuHoaDon(HoaDon hd, List<ChiTietHoaDon> dsChiTiet) {
        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            // Kiểm tra hoaDon đã tồn tại chưa
            boolean exists = false;
            try (PreparedStatement check = con.prepareStatement("SELECT 1 FROM HoaDon WHERE maHoaDon = ?")) {
                check.setString(1, hd.getMaHoaDon());
                try (ResultSet rs = check.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (!exists) {
                String sqlHD = "INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, phuongThucThanhToan) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstHD = con.prepareStatement(sqlHD)) {
                    pstHD.setString(1, hd.getMaHoaDon());
                    pstHD.setTimestamp(2, Timestamp.valueOf(hd.getThoiGianTao()));
                    pstHD.setString(3, hd.getNhanVien().getMaNhanVien());
                    pstHD.setBoolean(4, false); // Chưa thanh toán
                    pstHD.setString(5, hd.getKhachHang() != null ? hd.getKhachHang().getMaKhachHang() : null);
                    pstHD.setString(6, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKhuyenMai() : null);
                    pstHD.setString(7, hd.getLoaiHoaDon().name());
                    pstHD.setString(8, hd.getCa().getMaCa());
                    pstHD.setString(9, hd.getGhiChu());
                    pstHD.setString(10, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan().name() : null);
                    pstHD.executeUpdate();
                }

                // Lưu chi tiết hóa đơn
                String sqlCT = "INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuong, donGia, laQuaTangKem) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstCT = con.prepareStatement(sqlCT)) {
                    for (ChiTietHoaDon ct : dsChiTiet) {
                        pstCT.setString(1, hd.getMaHoaDon());
                        pstCT.setString(2, ct.getDonViQuyDoi().getMaDonVi());
                        pstCT.setInt(3, ct.getSoLuong());
                        pstCT.setDouble(4, ct.getDonGia());
                        pstCT.setBoolean(5, ct.isLaQuaTangKem());
                        pstCT.addBatch();
                    }
                    pstCT.executeBatch();
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /**
     * Xác nhận thanh toán: Cập nhật trangThaiThanhToan = true và trừ kho (FEFO).
     * Phải gọi luuHoaDon() trước khi gọi hàm này.
     */
    public boolean xacNhanThanhToan(String maHoaDon, List<ChiTietHoaDon> dsChiTiet) {
        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            // Cập nhật trạng thái thanh toán
            try (PreparedStatement pstUpd = con.prepareStatement(
                    "UPDATE HoaDon SET trangThaiThanhToan = 1 WHERE maHoaDon = ?")) {
                pstUpd.setString(1, maHoaDon);
                pstUpd.executeUpdate();
            }

            // Trừ kho và ghi nhận SuPhanBoLo
            String sqlLo = "UPDATE Lo SET soLuongSanPham = soLuongSanPham - ? WHERE maLo = ?";
            String sqlSPBL = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong) VALUES (?, ?, ?, ?)";

            try (PreparedStatement pstLo = con.prepareStatement(sqlLo);
                 PreparedStatement pstSPBL = con.prepareStatement(sqlSPBL)) {

                for (ChiTietHoaDon ct : dsChiTiet) {
                    int soLuongCanTru = ct.getSoLuong() * ct.getDonViQuyDoi().getHeSoQuyDoi();
                    List<Lo> dsLo = layDanhSachLoKhaDung(con, ct.getDonViQuyDoi().getMaDonVi());

                    for (Lo lo : dsLo) {
                        if (soLuongCanTru <= 0) break;
                        int tru = Math.min(soLuongCanTru, lo.getSoLuongSanPham());

                        pstLo.setInt(1, tru);
                        pstLo.setString(2, lo.getMaLo());
                        pstLo.executeUpdate();

                        pstSPBL.setString(1, maHoaDon);
                        pstSPBL.setString(2, ct.getDonViQuyDoi().getMaDonVi());
                        pstSPBL.setString(3, lo.getMaLo());
                        pstSPBL.setInt(4, tru);
                        pstSPBL.executeUpdate();

                        soLuongCanTru -= tru;
                    }

                    if (soLuongCanTru > 0) {
                        throw new SQLException("Không đủ tồn kho cho: " + ct.getDonViQuyDoi().getSanPham().getTenSanPham());
                    }
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null) try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) try { con.setAutoCommit(true); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    /** @deprecated Use luuHoaDon + xacNhanThanhToan instead */
    public boolean luuHoaDonBanHang(HoaDon hd, List<ChiTietHoaDon> dsChiTiet) {
        return luuHoaDon(hd, dsChiTiet) && xacNhanThanhToan(hd.getMaHoaDon(), dsChiTiet);
    }

    private List<Lo> layDanhSachLoKhaDung(Connection con, String maDonViQuyDoi) throws SQLException {
        List<Lo> danhSach = new ArrayList<>();
        String truyVan = "SELECT l.* FROM Lo l INNER JOIN DonViQuyDoi dv ON l.maSanPham = dv.maSanPham " +
                         "WHERE dv.maDonVi = ? AND l.soLuongSanPham > 0 AND l.ngayHetHan > GETDATE() " +
                         "ORDER BY l.ngayHetHan ASC";
        try (PreparedStatement lenh = con.prepareStatement(truyVan)) {
            lenh.setString(1, maDonViQuyDoi);
            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    Lo lo = new Lo();
                    lo.setMaLo(ketQua.getString("maLo"));
                    lo.setSoLuongSanPham(ketQua.getInt("soLuongSanPham"));
                    danhSach.add(lo);
                }
            }
        }
        return danhSach;
    }

    /** Đếm số lượng hóa đơn trong ngày theo loại để sinh mã tuần tự */
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