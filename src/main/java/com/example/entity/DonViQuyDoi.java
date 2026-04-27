package com.example.entity;

import java.util.Objects;

public class DonViQuyDoi {
    private String maDonVi;
    private DonVi tenDonVi;
    private int heSoQuyDoi;
    private SanPham sanPham;

    public DonViQuyDoi() {
    }

    public DonViQuyDoi(String maDonVi) {
        this.maDonVi = maDonVi;
    }

    public DonViQuyDoi(String maDonVi, DonVi tenDonVi, int heSoQuyDoi, SanPham sanPham) {
        this.maDonVi = maDonVi;
        this.tenDonVi = tenDonVi;
        this.heSoQuyDoi = heSoQuyDoi;
        this.sanPham = sanPham;
    }

    public String getMaDonVi() {
        return maDonVi;
    }

    public void setMaDonVi(String maDonVi) {
        this.maDonVi = maDonVi;
    }

    public DonVi getTenDonVi() {
        return tenDonVi;
    }

    public void setTenDonVi(DonVi tenDonVi) {
        this.tenDonVi = tenDonVi;
    }

    public int getHeSoQuyDoi() {
        return heSoQuyDoi;
    }

    public void setHeSoQuyDoi(int heSoQuyDoi) {
        this.heSoQuyDoi = heSoQuyDoi;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonViQuyDoi that = (DonViQuyDoi) o;
        return Objects.equals(maDonVi, that.maDonVi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDonVi);
    }

    @Override
    public String toString() {
        return "DonViQuyDoi{" +
                "maDonVi='" + maDonVi + '\'' +
                ", tenDonVi=" + tenDonVi +
                ", heSoQuyDoi=" + heSoQuyDoi +
                ", sanPham=" + sanPham +
                '}';
    }
}
