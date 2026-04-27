package com.example.entity;

import java.util.Objects;

public class QuaTang {
    private KhuyenMai khuyenMai;
    private SanPham sanPham;
    private int soLuongTang;

    public QuaTang() {
    }

    public QuaTang(KhuyenMai khuyenMai, SanPham sanPham, int soLuongTang) {
        this.khuyenMai = khuyenMai;
        this.sanPham = sanPham;
        this.soLuongTang = soLuongTang;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuongTang() {
        return soLuongTang;
    }

    public void setSoLuongTang(int soLuongTang) {
        this.soLuongTang = soLuongTang;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuaTang quaTang = (QuaTang) o;
        return Objects.equals(khuyenMai, quaTang.khuyenMai) && Objects.equals(sanPham, quaTang.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(khuyenMai, sanPham);
    }

    @Override
    public String toString() {
        return "QuaTang{" +
                "khuyenMai=" + (khuyenMai != null ? khuyenMai.getMaKhuyenMai() : "null") +
                ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : "null") +
                ", soLuongTang=" + soLuongTang +
                '}';
    }
}
