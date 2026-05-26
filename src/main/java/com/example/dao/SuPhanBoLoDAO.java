package com.example.dao;

import com.example.entity.Lo;
import com.example.entity.SuPhanBoLo;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.connectDB.ConnectDB;

public class SuPhanBoLoDAO {

    public boolean themSuPhanBoLo(SuPhanBoLo spbl) throws SQLException {
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong, laQuaTangKem, biLoi) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = ConnectDB.getConnection().prepareStatement(sql);

        pst.setString(1, spbl.getChiTietHoaDon().getHoaDon().getMaHoaDon());
        pst.setString(2, spbl.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
        pst.setString(3, spbl.getLo().getMaLo());
        pst.setInt(4, spbl.getSoLuong());
        pst.setBoolean(5, spbl.getChiTietHoaDon().isLaQuaTangKem());
        pst.setBoolean(6, spbl.isLoi());

        return pst.executeUpdate() > 0;
    }

    /**
     * Overload an toàn: nhận maHoaDon trực tiếp, tránh NPE khi ChiTietHoaDon.getHoaDon() == null.
     */
    public boolean themSuPhanBoLo(SuPhanBoLo spbl, String maHoaDon) throws SQLException {
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong, laQuaTangKem, biLoi) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = ConnectDB.getConnection().prepareStatement(sql)) {
            pst.setString(1, maHoaDon);
            pst.setString(2, spbl.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
            pst.setString(3, spbl.getLo().getMaLo());
            pst.setInt(4, spbl.getSoLuong());
            pst.setBoolean(5, spbl.getChiTietHoaDon().isLaQuaTangKem());
            pst.setBoolean(6, spbl.isLoi());
            return pst.executeUpdate() > 0;
        }
    }

    public boolean themNhieu(List<SuPhanBoLo> ds, String maHoaDon) throws SQLException {
        if (ds == null || ds.isEmpty())
            return true;
        String sql = "INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong, laQuaTangKem, biLoi) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = ConnectDB.getConnection().prepareStatement(sql)) {
            for (SuPhanBoLo spbl : ds) {
                pst.setString(1, maHoaDon);
                pst.setString(2, spbl.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi());
                pst.setString(3, spbl.getLo().getMaLo());
                pst.setInt(4, spbl.getSoLuong());
                pst.setBoolean(5, spbl.getChiTietHoaDon().isLaQuaTangKem());
                pst.setBoolean(6, spbl.isLoi());
                pst.addBatch();
            }
            pst.executeBatch();
            return true;
        }
    }

    public List<SuPhanBoLo> layPhanBoLoCuaChiTiet(String maHD, String maDV, boolean laQuaTangKem) {
        List<SuPhanBoLo> ds = new ArrayList<>();
        String sql = "SELECT s.* FROM SuPhanBoLo s " +
                "JOIN ChiTietHoaDon c ON s.maHoaDon = c.maHoaDon AND s.maDonVi = c.maDonVi " +
                "WHERE s.maHoaDon = ? AND s.maDonVi = ? AND c.laQuaTangKem = ?";

        try {
            // 1. Lấy connection bình thường (Không đặt trong try-with-resources)
            Connection con = ConnectDB.getConnection();

            // 2. Chỉ đưa PreparedStatement vào try-with-resources để tự động giải phóng
            // vùng nhớ
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maHD);
                stmt.setString(2, maDV);
                stmt.setBoolean(3, laQuaTangKem);

                try (ResultSet rs = stmt.executeQuery()) {
                    LoDAO loDAO = new LoDAO();
                    while (rs.next()) {
                        SuPhanBoLo spb = new SuPhanBoLo();
                        Lo lo = loDAO.timTheoMa(rs.getString("maLo"));
                        spb.setLo(lo);
                        spb.setSoLuong(rs.getInt("soLuong"));
                        spb.setLoi(rs.getBoolean("biLoi"));
                        ds.add(spb);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean xoaToanBoPhanBoLo(String maHD) throws SQLException {
        String truyVan = "DELETE FROM SuPhanBoLo WHERE maHoaDon = ?";
        try (PreparedStatement lenh = ConnectDB.getConnection().prepareStatement(truyVan)) {
            lenh.setString(1, maHD);
            return lenh.executeUpdate() >= 0;
        }
    }

    /**
     * Lấy danh sách phân bổ lô tương ứng để hoàn trả kho.
     */
    public List<SuPhanBoLo> layDanhSachPhanBoLoCanTra(String maHoaDonGoc, List<ChiTietHoaDon> dsChiTietTra) {
        List<SuPhanBoLo> dsPhanBoTra = new ArrayList<>();
        String sql = "SELECT maLo, soLuong FROM SuPhanBoLo WHERE maHoaDon = ? AND maDonVi = ?";
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                for (ChiTietHoaDon ctMoi : dsChiTietTra) {
                    int heSo = ctMoi.getDonViQuyDoi().getHeSoQuyDoi();
                    int soLuongCanTra = ctMoi.getSoLuong() * heSo;
                    int soLuongLoiRemaining = ctMoi.getSoLuongLoi() * heSo;
                    ps.setString(1, maHoaDonGoc);
                    ps.setString(2, ctMoi.getDonViQuyDoi().getMaDonVi());

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next() && soLuongCanTra > 0) {
                            String maLoGoc = rs.getString("maLo");
                            int slGocTrongLo = rs.getInt("soLuong");
                            int slTraVaoLo = Math.min(soLuongCanTra, slGocTrongLo);

                            // Phân bổ phần lỗi
                            int slLoiVaoLo = Math.min(soLuongLoiRemaining, slTraVaoLo);
                            if (slLoiVaoLo > 0) {
                                Lo lo = new Lo();
                                lo.setMaLo(maLoGoc);

                                SuPhanBoLo spbLoi = new SuPhanBoLo();
                                spbLoi.setLo(lo);
                                spbLoi.setChiTietHoaDon(ctMoi);
                                spbLoi.setSoLuong(slLoiVaoLo);
                                spbLoi.setLoi(true);
                                dsPhanBoTra.add(spbLoi);

                                soLuongLoiRemaining -= slLoiVaoLo;
                            }

                            // Phân bổ phần bình thường
                            int slNormalVaoLo = slTraVaoLo - slLoiVaoLo;
                            if (slNormalVaoLo > 0) {
                                Lo lo = new Lo();
                                lo.setMaLo(maLoGoc);

                                SuPhanBoLo spbNormal = new SuPhanBoLo();
                                spbNormal.setLo(lo);
                                spbNormal.setChiTietHoaDon(ctMoi);
                                spbNormal.setSoLuong(slNormalVaoLo);
                                spbNormal.setLoi(false);
                                dsPhanBoTra.add(spbNormal);
                            }

                            soLuongCanTra -= slTraVaoLo;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhanBoTra;
    }

    /**
     * Lấy danh sách tất cả sản phẩm lỗi (biLoi = 1) kèm đầy đủ thông tin liên kết.
     */
    public List<SuPhanBoLo> layDanhSachLoi() {
        List<SuPhanBoLo> ds = new ArrayList<>();
        String sql = "SELECT s.maHoaDon, s.maDonVi, s.maLo, s.soLuong, s.laQuaTangKem, s.biLoi, h.thoiGianTao " +
                     "FROM SuPhanBoLo s " +
                     "JOIN HoaDon h ON s.maHoaDon = h.maHoaDon " +
                     "WHERE s.biLoi = 1";
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                LoDAO loDAO = new LoDAO();
                DonViQuyDoiDAO dvDAO = new DonViQuyDoiDAO();
                while (rs.next()) {
                    SuPhanBoLo spb = new SuPhanBoLo();
                    spb.setSoLuong(rs.getInt("soLuong"));
                    spb.setLoi(rs.getBoolean("biLoi"));

                    Lo lo = loDAO.timTheoMa(rs.getString("maLo"));
                    spb.setLo(lo);

                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setLaQuaTangKem(rs.getBoolean("laQuaTangKem"));
                    ct.setDonViQuyDoi(dvDAO.timTheoMa(rs.getString("maDonVi")));

                    // Lấy đơn giá gốc từ bảng ChiTietHoaDon
                    String sqlCT = "SELECT donGia FROM ChiTietHoaDon WHERE maHoaDon = ? AND maDonVi = ? AND laQuaTangKem = ?";
                    try (PreparedStatement stmtCT = con.prepareStatement(sqlCT)) {
                        stmtCT.setString(1, rs.getString("maHoaDon"));
                        stmtCT.setString(2, rs.getString("maDonVi"));
                        stmtCT.setBoolean(3, rs.getBoolean("laQuaTangKem"));
                        try (ResultSet rsCT = stmtCT.executeQuery()) {
                            if (rsCT.next()) {
                                ct.setDonGia(rsCT.getDouble("donGia"));
                            }
                        }
                    }

                    HoaDon hd = new HoaDon();
                    hd.setMaHoaDon(rs.getString("maHoaDon"));
                    hd.setThoiGianTao(rs.getTimestamp("thoiGianTao").toLocalDateTime());
                    ct.setHoaDon(hd);

                    spb.setChiTietHoaDon(ct);
                    ds.add(spb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Giải quyết sản phẩm lỗi bằng cách xóa bản ghi lỗi tương ứng khỏi SuPhanBoLo khi trả về NSX.
     */
    public boolean giaiQuyetHangLoi(String maHoaDon, String maDonVi, String maLo, boolean laQuaTangKem) {
        String sql = "DELETE FROM SuPhanBoLo WHERE maHoaDon = ? AND maDonVi = ? AND maLo = ? AND laQuaTangKem = ? AND biLoi = 1";
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maHoaDon);
                stmt.setString(2, maDonVi);
                stmt.setString(3, maLo);
                stmt.setBoolean(4, laQuaTangKem);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}