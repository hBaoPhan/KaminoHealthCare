package com.example.entity;

import com.example.entity.enums.*;
public class SuPhanBoLo {
    private ChiTietHoaDon chiTietHoaDon;
    private Lo lo;
    private int soLuong;

    public SuPhanBoLo() {}

    public SuPhanBoLo(ChiTietHoaDon chiTietHoaDon, Lo lo, int soLuong) {
        this.chiTietHoaDon = chiTietHoaDon;
        this.lo = lo;
        this.soLuong = soLuong;
    }

    public ChiTietHoaDon getChiTietHoaDon() { return chiTietHoaDon; }
    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) { this.chiTietHoaDon = chiTietHoaDon; }
    public Lo getLo() { return lo; }
    public void setLo(Lo lo) { this.lo = lo; }
    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    @Override
    public String toString() {
        return "SuPhanBoLo{" + "lo=" + (lo != null ? lo.getMaLo() : "null") + ", soLuong=" + soLuong + '}';
    }
}