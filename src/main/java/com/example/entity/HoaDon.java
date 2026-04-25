package com.example.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public HoaDon(String maHoaDon, LocalDateTime thoiGianTao, NhanVien nhanVien, boolean trangThaiThanhToan, KhachHang khachHang, KhuyenMai khuyenMai, LoaiHoaDon loaiHoaDon, CaLam ca, String ghiChu, HoaDon hoaDonDoiTra, DonThuoc donThuoc, PhuongThucThanhToan phuongThucThanhToan) {
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

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDateTime getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(LocalDateTime thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public boolean isTrangThaiThanhToan() {
        return trangThaiThanhToan;
    }

    public void setTrangThaiThanhToan(boolean trangThaiThanhToan) {
        this.trangThaiThanhToan = trangThaiThanhToan;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public LoaiHoaDon getLoaiHoaDon() {
        return loaiHoaDon;
    }

    public void setLoaiHoaDon(LoaiHoaDon loaiHoaDon) {
        this.loaiHoaDon = loaiHoaDon;
    }

    public CaLam getCa() {
        return ca;
    }

    public void setCa(CaLam ca) {
        this.ca = ca;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public HoaDon getHoaDonDoiTra() {
        return hoaDonDoiTra;
    }

    public void setHoaDonDoiTra(HoaDon hoaDonDoiTra) {
        this.hoaDonDoiTra = hoaDonDoiTra;
    }

    public DonThuoc getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(DonThuoc donThuoc) {
        this.donThuoc = donThuoc;
    }

    public PhuongThucThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public List<ChiTietHoaDon> getDsChiTiet() {
        return dsChiTiet;
    }

    public void setDsChiTiet(List<ChiTietHoaDon> dsChiTiet) {
        this.dsChiTiet = dsChiTiet;
    }
// Tính tổng tiền hàng tạm thời (chưa trừ khuyến mãi)
    public double tinhTongTienTamThoi() {
    	if (dsChiTiet == null || dsChiTiet.isEmpty()) return 0.0;
        double tongTienHang = 0;
        for (ChiTietHoaDon chiTiet : dsChiTiet) {
            tongTienHang += chiTiet.tinhThanhTien();
        }
        return tongTienHang;
    }

    public double tinhTongThue() {
    	if (dsChiTiet == null || dsChiTiet.isEmpty()) return 0.0;
        double tongThue = 0;
        for (ChiTietHoaDon chiTiet : dsChiTiet) {
            tongThue += chiTiet.tinhTienThue();
        }
        return tongThue;
    }
// Tính tổng tiền hàng cuối cùng
    public double tinhTongTienThanhToan() {
        double tongTienHang = tinhTongTienTamThoi();
        double soTienGiam = 0.0;
        
        if (this.khuyenMai != null && this.khuyenMai.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            soTienGiam = tongTienHang * (this.khuyenMai.getKhuyenMaiPhanTram() / 100.0);
        }
        
        return tongTienHang - soTienGiam;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHoaDon, hoaDon.maHoaDon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", thoiGianTao=" + thoiGianTao +
                ", trangThaiThanhToan=" + trangThaiThanhToan +
                ", loaiHoaDon=" + loaiHoaDon +
                '}';
    }
}
