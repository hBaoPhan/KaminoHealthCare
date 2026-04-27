package com.example.entity;

import java.util.Objects;

public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private PhanLoai phanLoai;
    private int soLuongTon;
    private String moTa;
    private String hoatChat;
    private double donGiaCoBan;
    private boolean trangThaiKinhDoanh;
    private double thue;

    public SanPham() {
    }

    public SanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public SanPham(String maSanPham, String tenSanPham, PhanLoai phanLoai, int soLuongTon, String moTa, String hoatChat, double donGiaCoBan, boolean trangThaiKinhDoanh, double thue) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.phanLoai = phanLoai;
        this.soLuongTon = soLuongTon;
        this.moTa = moTa;
        this.hoatChat = hoatChat;
        this.donGiaCoBan = donGiaCoBan;
        this.trangThaiKinhDoanh = trangThaiKinhDoanh;
        this.thue = thue;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public PhanLoai getPhanLoai() {
        return phanLoai;
    }

    public void setPhanLoai(PhanLoai phanLoai) {
        this.phanLoai = phanLoai;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        this.hoatChat = hoatChat;
    }

    public double getDonGiaCoBan() {
        return donGiaCoBan;
    }

    public void setDonGiaCoBan(double donGiaCoBan) {
        this.donGiaCoBan = donGiaCoBan;
    }

    public boolean isTrangThaiKinhDoanh() {
        return trangThaiKinhDoanh;
    }

    public void setTrangThaiKinhDoanh(boolean trangThaiKinhDoanh) {
        this.trangThaiKinhDoanh = trangThaiKinhDoanh;
    }

    public double getThue() {
        return thue;
    }

    public void setThue(double thue) {
        this.thue = thue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SanPham sanPham = (SanPham) o;
        return Objects.equals(maSanPham, sanPham.maSanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSanPham);
    }

    @Override
    public String toString() {
        return "SanPham{" +
                "maSanPham='" + maSanPham + '\'' +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", phanLoai=" + phanLoai +
                ", soLuongTon=" + soLuongTon +
                ", moTa='" + moTa + '\'' +
                ", hoatChat='" + hoatChat + '\'' +
                ", donGiaCoBan=" + donGiaCoBan +
                ", trangThaiKinhDoanh=" + trangThaiKinhDoanh +
                ", thue=" + thue +
                '}';
    }
}
