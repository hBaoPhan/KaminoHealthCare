package com.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class SuPhanBoLoId implements Serializable {
    private ChiTietHoaDonId chiTietHoaDon;
    private String lo;

    public SuPhanBoLoId() {}

    public SuPhanBoLoId(ChiTietHoaDonId chiTietHoaDon, String lo) {
        this.chiTietHoaDon = chiTietHoaDon;
        this.lo = lo;
    }

    public ChiTietHoaDonId getChiTietHoaDon() {
        return chiTietHoaDon;
    }

    public void setChiTietHoaDon(ChiTietHoaDonId chiTietHoaDon) {
        this.chiTietHoaDon = chiTietHoaDon;
    }

    public String getLo() {
        return lo;
    }

    public void setLo(String lo) {
        this.lo = lo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuPhanBoLoId that = (SuPhanBoLoId) o;
        return Objects.equals(chiTietHoaDon, that.chiTietHoaDon) && Objects.equals(lo, that.lo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chiTietHoaDon, lo);
    }
}
