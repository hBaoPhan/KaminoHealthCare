package com.example.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HoaDon {
    private String maHoaDon;
    private LocalDateTime thoiGianTao;
    private NhanVien nhanVien;
    private boolean trangThaiThanhToan;
    private KhachHang khachHang;
    private KhuyenMai khuyenMai;
    private LoaiHoaDon loaiHoaDon;
    private CaLam ca;
    private String ghiChu;
    private HoaDon hoaDonDoiTra;
    private DonThuoc donThuoc;
    private List<ChiTietHoaDon> dsChiTiet;

    public HoaDon() {
        this.dsChiTiet = new ArrayList<>();
    }

    public HoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
        this.dsChiTiet = new ArrayList<>();
    }

    public HoaDon(String maHoaDon, LocalDateTime thoiGianTao, NhanVien nhanVien, boolean trangThaiThanhToan, KhachHang khachHang, KhuyenMai khuyenMai, LoaiHoaDon loaiHoaDon, CaLam ca, String ghiChu, HoaDon hoaDonDoiTra, DonThuoc donThuoc) {
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

    public List<ChiTietHoaDon> getDsChiTiet() {
        return dsChiTiet;
    }

    public void setDsChiTiet(List<ChiTietHoaDon> dsChiTiet) {
        this.dsChiTiet = dsChiTiet;
    }

    public double tinhTongTien() {
        double tong = 0;
        for (ChiTietHoaDon ct : dsChiTiet) {
            tong += ct.tinhThanhTien();
        }
        return tong;
    }

    public double tinhTongThue() {
        double tongThue = 0;
        for (ChiTietHoaDon ct : dsChiTiet) {
            if (ct.getDonViQuyDoi() != null && ct.getDonViQuyDoi().getSanPham() != null) {
                tongThue += ct.tinhThanhTien() * ct.getDonViQuyDoi().getSanPham().getThue();
            }
        }
        return tongThue;
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
