package com.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class ChiTietHoaDonId implements Serializable {
    private String hoaDon;
    private String donViQuyDoi;

    public ChiTietHoaDonId() {}

    public ChiTietHoaDonId(String hoaDon, String donViQuyDoi) {
        this.hoaDon = hoaDon;
        this.donViQuyDoi = donViQuyDoi;
    }

    public String getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(String hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getDonViQuyDoi() {
        return donViQuyDoi;
    }

    public void setDonViQuyDoi(String donViQuyDoi) {
        this.donViQuyDoi = donViQuyDoi;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDonId that = (ChiTietHoaDonId) o;
        return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(donViQuyDoi, that.donViQuyDoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, donViQuyDoi);
    }
}
