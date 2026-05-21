package com.example.entity;

import com.example.entity.enums.*;
public class SuPhanBoLo {
    private ChiTietHoaDon chiTietHoaDon;
    private Lo lo;
    private int soLuong;
    private boolean isLoi; // default is false

    public SuPhanBoLo() {}

    public SuPhanBoLo(ChiTietHoaDon chiTietHoaDon, Lo lo, int soLuong) {
        this.chiTietHoaDon = chiTietHoaDon;
        this.lo = lo;
        this.soLuong = soLuong;
        this.isLoi = false;
    }

    public SuPhanBoLo(ChiTietHoaDon chiTietHoaDon, Lo lo, int soLuong, boolean isLoi) {
        this.chiTietHoaDon = chiTietHoaDon;
        this.lo = lo;
        this.soLuong = soLuong;
        this.isLoi = isLoi;
    }

    public ChiTietHoaDon getChiTietHoaDon() { return chiTietHoaDon; }
    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) { this.chiTietHoaDon = chiTietHoaDon; }
    public Lo getLo() { return lo; }
    public void setLo(Lo lo) { this.lo = lo; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }
    public boolean isLoi() { return isLoi; }
    public void setLoi(boolean isLoi) { this.isLoi = isLoi; }

    @Override
    public String toString() {
        return "SuPhanBoLo{" + "lo=" + (lo != null ? lo.getMaLo() : "null") + ", soLuong=" + soLuong + ", isLoi=" + isLoi + '}';
    }
}