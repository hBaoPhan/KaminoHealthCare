package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "maHoaDon")
@ToString(of = {"maHoaDon", "thoiGianTao", "trangThaiThanhToan", "loaiHoaDon"})
@Entity
@Table(name = "HoaDon")
public class HoaDon {

    @Id
    @Column(name = "maHoaDon", columnDefinition = "nvarchar(50)")
    private String maHoaDon;

    @Column(name = "thoiGianTao")
    private LocalDateTime thoiGianTao;

    @ManyToOne
    @JoinColumn(name = "maNhanVien", columnDefinition = "nvarchar(50)")
    private NhanVien nhanVien;

    @Column(name = "trangThaiThanhToan")
    private boolean trangThaiThanhToan;

    @ManyToOne
    @JoinColumn(name = "maKhachHang", columnDefinition = "nvarchar(50)")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "maKhuyenMai", columnDefinition = "nvarchar(50)")
    private KhuyenMai khuyenMai;

    @Enumerated(EnumType.STRING)
    @Column(name = "loaiHoaDon")
    private LoaiHoaDon loaiHoaDon;

    @ManyToOne
    @JoinColumn(name = "maCa", columnDefinition = "nvarchar(50)")
    private CaLam ca;

    @Column(name = "ghiChu", columnDefinition = "nvarchar(MAX)")
    private String ghiChu;

    @ManyToOne
    @JoinColumn(name = "maHoaDonDoiTra", columnDefinition = "nvarchar(50)")
    private HoaDon hoaDonDoiTra;

    @ManyToOne
    @JoinColumn(name = "maDonThuoc", columnDefinition = "nvarchar(50)")
    private DonThuoc donThuoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "phuongThucThanhToan")
    private PhuongThucThanhToan phuongThucThanhToan;

    @Transient
    private List<ChiTietHoaDon> dsChiTiet;

    public HoaDon() {
        this.dsChiTiet = new ArrayList<>();
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
        this.dsChiTiet = new ArrayList<>();
    }

    public HoaDon(String maHoaDon, LocalDateTime thoiGianTao, NhanVien nhanVien,
                  boolean trangThaiThanhToan, KhachHang khachHang, KhuyenMai khuyenMai,
                  LoaiHoaDon loaiHoaDon, CaLam ca, String ghiChu, HoaDon hoaDonDoiTra,
                  DonThuoc donThuoc, PhuongThucThanhToan phuongThucThanhToan) {
        this.maHoaDon = maHoaDon;
        this.thoiGianTao = thoiGianTao;
        this.nhanVien = nhanVien;
        this.trangThaiThanhToan = trangThaiThanhToan;
        this.khachHang = khachHang;
        this.khuyenMai = khuyenMai;
        this.loaiHoaDon = loaiHoaDon;
        this.ca = ca;
        this.ghiChu = ghiChu;
        this.hoaDonDoiTra = hoaDonDoiTra;
        this.donThuoc = donThuoc;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.dsChiTiet = new ArrayList<>();
    }

    /** Tính tổng tiền hàng tạm thời (chưa trừ khuyến mãi) */
    public double tinhTongTienTamThoi() {
        if (dsChiTiet == null || dsChiTiet.isEmpty()) return 0.0;
        double tongTienHang = 0;
        for (ChiTietHoaDon chiTiet : dsChiTiet) {
            tongTienHang += chiTiet.tinhThanhTien();
        }
        return tongTienHang;
    }

    /** Tính tổng thuế của toàn bộ dòng chi tiết */
    public double tinhTongThue() {
        if (dsChiTiet == null || dsChiTiet.isEmpty()) return 0.0;
        double tongThue = 0;
        for (ChiTietHoaDon chiTiet : dsChiTiet) {
            tongThue += chiTiet.tinhTienThue();
        }
        return tongThue;
    }

    /** Tính tổng tiền thanh toán cuối cùng (đã trừ khuyến mãi phần trăm) */
    public double tinhTongTienThanhToan() {
        double tongTienHang = tinhTongTienTamThoi();
        double soTienGiam = 0.0;

        if (this.khuyenMai != null && this.khuyenMai.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            soTienGiam = tongTienHang * (this.khuyenMai.getKhuyenMaiPhanTram() / 100.0);
        }

        return tongTienHang - soTienGiam;
    }
}
