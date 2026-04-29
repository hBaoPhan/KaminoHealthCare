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
                
                // 3. Load danh sách chi tiết sản phẩm để hiển thị lên bảng "Hàng đã mua"
                ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
                hd.setDsChiTiet(ctDAO.layTheoMaHoaDon(maHD));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hd;
    }
//    Bán hàng: gọi luuHoaDon(hdMoi, null). Hệ thống sẽ lưu hóa đơn và trừ kho sản phẩm bán ra.  
//    Đổi hàng: gọi luuHoaDon(hdDoi, dsHangTra). Hệ thống vừa trừ kho hàng mới, vừa cộng lại kho cho những lô hàng khách trả về.
    public boolean luuHoaDon(HoaDon hd, List<ChiTietHoaDon> dsHangTraVe) {
        Connection ketNoi = null;
        try {
            ketNoi = ConnectDB.getConnection();
            ketNoi.setAutoCommit(false); // Bắt đầu giao dịch 

            // 1. Lưu hóa đơn (Dùng được cho cả BAN_HANG và DOI_HANG)
            if (!them(hd)) throw new SQLException("Lỗi lưu hóa đơn");

            ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
            SuPhanBoLoDAO spbDAO = new SuPhanBoLoDAO();
            LoDAO loDAO = new LoDAO();

            // 2. Xử lý hàng xuất đi (Hàng mới trong hóa đơn)
            for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                if (!ctDAO.them(ct, ketNoi)) throw new SQLException("Lỗi lưu chi tiết");
                
                for (SuPhanBoLo spb : ct.getDsPhanBoLo()) {
                    // Trừ kho: truyền số âm 
                    if (!loDAO.capNhatSoLuongTon(spb.getLo().getMaLo(), -spb.getSoLuong())) 
                        throw new SQLException("Lỗi trừ kho lô: " + spb.getLo().getMaLo());
                    
                    if (!spbDAO.themSuPhanBoLo(spb, ketNoi)) throw new SQLException("Lỗi lưu phân bổ lô");
                }
            }

            // 3. Xử lý hàng trả về (Chỉ dành cho DOI_HANG / TRA_HANG)
            if (dsHangTraVe != null) {
                for (ChiTietHoaDon ctTra : dsHangTraVe) {
                    for (SuPhanBoLo spbTra : ctTra.getDsPhanBoLo()) {
                        // Cộng lại kho: truyền số dương 
                        if (!loDAO.capNhatSoLuongTon(spbTra.getLo().getMaLo(), spbTra.getSoLuong()))
                            throw new SQLException("Lỗi cộng kho lô: " + spbTra.getLo().getMaLo());
                    }
                }
            }

            ketNoi.commit(); // Thành công toàn bộ 
            return true;
        } catch (SQLException e) {
            if (ketNoi != null) try { ketNoi.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
            return false;
        } finally {
            if (ketNoi != null) try { ketNoi.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
        }
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
}