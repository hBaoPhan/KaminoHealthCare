package com.example.entity;

import com.example.entity.enums.*;
public class SuPhanBoLo {
    private ChiTietHoaDon chiTietHoaDon;
    private Lo lo;
    private int soLuongPhanBo;
    private boolean isLoi; // default is false

    public SuPhanBoLo() {}

    public SuPhanBoLo(ChiTietHoaDon chiTietHoaDon, Lo lo, int soLuongPhanBo) {
        this.chiTietHoaDon = chiTietHoaDon;
        this.lo = lo;
        this.soLuongPhanBo = soLuongPhanBo;
        this.isLoi = false;
    }

    public SuPhanBoLo(ChiTietHoaDon chiTietHoaDon, Lo lo, int soLuongPhanBo, boolean isLoi) {
        this.chiTietHoaDon = chiTietHoaDon;
        this.lo = lo;
        this.soLuongPhanBo = soLuongPhanBo;
        this.isLoi = isLoi;
    }

    public ChiTietHoaDon getChiTietHoaDon() { return chiTietHoaDon; }
    public void setChiTietHoaDon(ChiTietHoaDon chiTietHoaDon) { this.chiTietHoaDon = chiTietHoaDon; }
    public Lo getLo() { return lo; }
    public void setLo(Lo lo) { this.lo = lo; }
    public int getSoLuongPhanBo() { return soLuongPhanBo; }
    public void setSoLuongPhanBo(int soLuongPhanBo) { this.soLuongPhanBo = soLuongPhanBo; }
    public boolean isLoi() { return isLoi; }
    public void setLoi(boolean isLoi) { this.isLoi = isLoi; }

    @Override
    public String toString() {
        return "SuPhanBoLo{" + "lo=" + (lo != null ? lo.getMaLo() : "null") + ", soLuongPhanBo=" + soLuongPhanBo + ", isLoi=" + isLoi + '}';
    }
}