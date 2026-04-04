package com.example.entity;

import java.util.Objects;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private DonViQuyDoi donViQuyDoi;
    private int soLuong;
    private double donGia;
    private boolean laQuaTangKem;
    private Lo lo;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, DonViQuyDoi donViQuyDoi, int soLuong, double donGia, boolean laQuaTangKem, Lo lo) {
        this.hoaDon = hoaDon;
        this.donViQuyDoi = donViQuyDoi;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.laQuaTangKem = laQuaTangKem;
        this.lo = lo;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public DonViQuyDoi getDonViQuyDoi() {
        return donViQuyDoi;
    }

    public void setDonViQuyDoi(DonViQuyDoi donViQuyDoi) {
        this.donViQuyDoi = donViQuyDoi;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public boolean isLaQuaTangKem() {
        return laQuaTangKem;
    }

    public void setLaQuaTangKem(boolean laQuaTangKem) {
        this.laQuaTangKem = laQuaTangKem;
    }

    public Lo getLo() {
        return lo;
    }

    public void setLo(Lo lo) {
        this.lo = lo;
    }

    public double tinhThanhTien() {
        if (laQuaTangKem) return 0;
        return soLuong * donGia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(donViQuyDoi, that.donViQuyDoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, donViQuyDoi);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "donViQuyDoi=" + (donViQuyDoi != null ? donViQuyDoi.getMaDonVi() : "null") +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", laQuaTangKem=" + laQuaTangKem +
                '}';
    }
}
