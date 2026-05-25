package com.example.service;

import com.example.dao.HoaDonDAO;
import com.example.entity.CaLam;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.SuPhanBoLo;
import com.example.entity.enums.LoaiHoaDon;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.example.connectDB.ConnectDB;
import com.example.entity.Lo;

/**
 * Service xử lý toàn bộ nghiệp vụ liên quan đến Hóa đơn.
 * Là tầng trung gian giữa GUI và DAO — chứa logic nghiệp vụ,
 * không chứa SQL và không xử lý UI trực tiếp.
 */
public class HoaDonService {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final CaLamService caLamService = new CaLamService();
    private final ChiTietHoaDonService ctService = new ChiTietHoaDonService();
    private final LoService loService = new LoService();
    private final SuPhanBoLoService spbService = new SuPhanBoLoService();

    // ==================== TRUY VẤN ====================

    public List<HoaDon> layTatCa() {
        return hoaDonDAO.layTatCa();
    }

    public HoaDon timTheoMa(String maHD) {
        return hoaDonDAO.timTheoMa(maHD);
    }

    /**
     * Lấy hóa đơn bán hàng chưa thanh toán mới nhất.
     */
    public HoaDon layHoaDonChuaThanhToan(String maNhanVien) {
        HoaDon hd = hoaDonDAO.layHoaDonChuaThanhToan();
        if (hd != null) {
            hd.setDsChiTiet(ctService.layTheoMaHoaDon(hd.getMaHoaDon()));
        }
        return hd;
    }

    public List<HoaDon> timKiem(String maHD, LocalDate ngayTao) {

        return hoaDonDAO.timKiem(maHD, ngayTao);
    }

    // ==================== NGHIỆP VỤ ====================

    /**
     * Sinh mã hóa đơn theo định dạng: [PREFIX][ddMMyy][STT 3 chữ số].
     */
    public String sinhMaHoaDon(LoaiHoaDon loaiHoaDon) {
        String prefix = switch (loaiHoaDon) {
            case BAN_HANG -> "HDB";
            case DOI_HANG -> "HDD";
            case TRA_HANG -> "HDT";
        };
        int stt = hoaDonDAO.demHoaDonTrongNgay(loaiHoaDon) + 1;
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        return String.format("%s%s%03d", prefix, date, stt);
    }

    /**
     * Kiểm tra và lấy hóa đơn gốc phục vụ nghiệp vụ đổi hàng.
     */
    public HoaDon layHoaDonDeDoi(String maHD) {
        return hoaDonDAO.layHoaDonDeDoi(maHD);
    }

    /**
     * Lấy ca làm việc hiện tại đang mở của nhân viên.
     * Trả về null nếu nhân viên chưa mở ca.
     */
    public CaLam layCaHienTai(String maNhanVien) {
        return caLamService.layCaHienTai(maNhanVien);
    }

    // ==================== LƯU / CẬP NHẬT ====================

    /**
     * Lưu hóa đơn bán hàng với trạng thái chưa thanh toán (không trừ kho).
     * Nếu hóa đơn đã tồn tại, chỉ cập nhật thông tin và chi tiết (idempotent).
     */
    public boolean luuHoaDonBanHang(HoaDon hd, List<ChiTietHoaDon> dsChiTiet) {
        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            HoaDon existing = hoaDonDAO.timTheoMa(hd.getMaHoaDon());
            if (existing != null) {
                hoaDonDAO.capNhat(hd);
                ctService.xoaToanBoChiTiet(hd.getMaHoaDon());
            } else {
                hoaDonDAO.them(hd);
            }
            ctService.themNhieu(dsChiTiet, hd.getMaHoaDon());

            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null)
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    public boolean xacNhanThanhToan(String maHoaDon, List<ChiTietHoaDon> dsChiTiet) throws SQLException {
        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            hoaDonDAO.capNhatTrangThaiThanhToan(maHoaDon, true);

            for (ChiTietHoaDon ct : dsChiTiet) {
                int soLuongCanTru = ct.getSoLuong() * ct.getDonViQuyDoi().getHeSoQuyDoi();
                List<Lo> dsLo = loService.layDanhSachLoKhaDung(ct.getDonViQuyDoi().getMaDonVi());

                for (Lo lo : dsLo) {
                    if (soLuongCanTru <= 0)
                        break;
                    int tru = Math.min(soLuongCanTru, lo.getSoLuongSanPham());

                    loService.capNhatSoLuongTon(lo.getMaLo(), -tru);

                    SuPhanBoLo spb = new SuPhanBoLo();
                    spb.setChiTietHoaDon(ct);
                    spb.setLo(lo);
                    spb.setSoLuong(tru);
                    spbService.themSuPhanBoLo(spb, maHoaDon);

                    soLuongCanTru -= tru;
                }

                if (soLuongCanTru > 0) {
                    throw new SQLException("Không đủ tồn kho cho: " + ct.getDonViQuyDoi().getSanPham().getTenSanPham());
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            if (e instanceof SQLException)
                throw (SQLException) e;
            throw new RuntimeException(e);
        } finally {
            if (con != null)
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Thực thi toàn bộ luồng đổi hàng trong 1 transaction duy nhất.
     */
    public boolean luuHoaDonDoiHang(HoaDon hoaDonMoi,
            List<SuPhanBoLo> dsTraLai,
            List<ChiTietHoaDon> dsChiTietMoi,
            List<SuPhanBoLo> dsPhanBoMoi) {
        Connection ketNoi = null;
        try {
            ketNoi = ConnectDB.getConnection();
            ketNoi.setAutoCommit(false);

            loService.capNhatTonKhoNhieu(dsTraLai, true);

            hoaDonDAO.them(hoaDonMoi);
            ctService.themNhieu(dsChiTietMoi, hoaDonMoi.getMaHoaDon());

            if (dsTraLai != null && !dsTraLai.isEmpty()) {
                spbService.themNhieu(dsTraLai, hoaDonMoi.getMaHoaDon());
            }

            if (dsPhanBoMoi != null && !dsPhanBoMoi.isEmpty()) {
                for (SuPhanBoLo spMoi : dsPhanBoMoi) {
                    Lo lo = loService.timTheoMa(spMoi.getLo().getMaLo());
                    if (lo == null)
                        throw new RuntimeException("Không tìm thấy Lô " + spMoi.getLo().getMaLo());
                    if (lo.getSoLuongSanPham() < spMoi.getSoLuong()) {
                        throw new RuntimeException("Lô " + spMoi.getLo().getMaLo() + " không đủ số lượng để đổi!");
                    }
                }
                loService.capNhatTonKhoNhieu(dsPhanBoMoi, false);
                spbService.themNhieu(dsPhanBoMoi, hoaDonMoi.getMaHoaDon());
            }

            ketNoi.commit();
            return true;
        } catch (Exception e) {
            if (ketNoi != null)
                try {
                    ketNoi.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
            return false;
        } finally {
            if (ketNoi != null)
                try {
                    ketNoi.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Thực thi toàn bộ luồng trả hàng trong 1 transaction duy nhất.
     */
    public boolean luuHoaDonTraHang(HoaDon hoaDonTra, List<SuPhanBoLo> dsPhanBoTra) {
        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

            hoaDonTra.setTrangThaiThanhToan(true);
            hoaDonDAO.them(hoaDonTra);
            ctService.themNhieu(hoaDonTra.getDsChiTiet(), hoaDonTra.getMaHoaDon());

            loService.capNhatTonKhoNhieu(dsPhanBoTra, true);
            spbService.themNhieu(dsPhanBoTra, hoaDonTra.getMaHoaDon());

            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null)
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    /**
     * Hủy hóa đơn (xóa chuỗi SuPhanBoLo → ChiTietHoaDon → HoaDon trong 1
     * transaction).
     */
    public boolean huyHoaDon(String maHD) {
        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);
            spbService.xoaToanBoPhanBoLo(maHD);
            ctService.xoaToanBoChiTiet(maHD);
            hoaDonDAO.xoa(maHD);

            con.commit();
            return true;
        } catch (Exception e) {
            if (con != null)
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null)
                try {
                    con.setAutoCommit(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
        }
    }

    public boolean them(HoaDon hd) {
        try {
            return hoaDonDAO.them(hd);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean capNhat(HoaDon hd) {
        try {
            return hoaDonDAO.capNhat(hd);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean xoa(String maHD) {
        try {
            return hoaDonDAO.xoa(maHD);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách phân bổ lô tương ứng để hoàn trả kho khi trả hàng.
     * Di chuyển raw SQL từ TraHangPanel — tầng UI không được viết SQL.
     */
    public List<SuPhanBoLo> layDanhSachPhanBoLoCanTra(String maHoaDonGoc,
            List<ChiTietHoaDon> dsChiTietTra) {
        return spbService.layDanhSachPhanBoLoCanTra(maHoaDonGoc, dsChiTietTra);
    }

    // ==================== THỐNG KÊ ====================

    /**
     * Tính tổng doanh thu của một ca làm việc.
     * Di chuyển từ HoaDonDAO (đây là nghiệp vụ thống kê, không phải CRUD thuần).
     */
    public double tinhTongDoanhThuCa(String maCa) {
        return hoaDonDAO.tinhTongDoanhThuCa(maCa);
    }
}
