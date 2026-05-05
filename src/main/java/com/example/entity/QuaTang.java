package com.example.entity;

import com.example.entity.enums.*;
import java.util.Objects;

public class QuaTang {
    private KhuyenMai khuyenMai;
    private DonViQuyDoi donViQuyDoi;
    private int soLuongTang;

    public QuaTang() {
    }

    public QuaTang(KhuyenMai khuyenMai, DonViQuyDoi donViQuyDoi, int soLuongTang) {
        this.khuyenMai = khuyenMai;
        this.donViQuyDoi = donViQuyDoi;
        this.soLuongTang = soLuongTang;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public DonViQuyDoi getDonViQuyDoi() {
        return donViQuyDoi;
    }

    public void setDonViQuyDoi(DonViQuyDoi donViQuyDoi) {
        this.donViQuyDoi = donViQuyDoi;
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
        return Objects.equals(khuyenMai, quaTang.khuyenMai) && Objects.equals(donViQuyDoi, quaTang.donViQuyDoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(khuyenMai, donViQuyDoi);
    }

    @Override
    public String toString() {
        return "QuaTang{" +
                "khuyenMai=" + (khuyenMai != null ? khuyenMai.getMaKhuyenMai() : "null") +
                ", donViQuyDoi=" + (donViQuyDoi != null ? donViQuyDoi.getMaDonVi() : "null") +
                ", soLuongTang=" + soLuongTang +
                '}';
    }
}
