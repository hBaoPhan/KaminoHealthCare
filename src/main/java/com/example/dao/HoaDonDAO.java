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
                hd.setKhachHang(khDAO.timTheoMa(ketQua.getString("maKhachHang")));
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
                hd.setKhachHang(khDAO.timTheoMa(ketQua.getString("maKhachHang")));
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
            lenh.setString(5, hd.getKhachHang().getMaKhachHang());
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
            lenh.setString(4, hd.getKhachHang().getMaKhachHang());
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
                hd.setKhachHang(khDAO.timTheoMa(ketQua.getString("maKhachHang")));
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
                String themPhanBo = "INSERT INTO SuPhanBoLo (maChiTietHoaDon_HD, maChiTietHoaDon_DV, maLo, soLuong) VALUES (?, ?, ?, ?)";
                
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
}