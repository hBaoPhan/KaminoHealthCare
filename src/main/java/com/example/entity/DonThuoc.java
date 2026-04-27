package com.example.entity;

import com.example.entity.enums.*;
import java.time.LocalDate;
import java.util.Objects;

public class DonThuoc {
    private String maDonThuoc;
    private String tenBacSi;
    private String coSoKhamBenh;
    private LocalDate ngayKeDon;

    public DonThuoc() {
    }

    public DonThuoc(String maDonThuoc) {
        this.maDonThuoc = maDonThuoc;
    }

    public DonThuoc(String maDonThuoc, String tenBacSi, String coSoKhamBenh, LocalDate ngayKeDon) {
        this.maDonThuoc = maDonThuoc;
        this.tenBacSi = tenBacSi;
        this.coSoKhamBenh = coSoKhamBenh;
        this.ngayKeDon = ngayKeDon;
    }

    public String getMaDonThuoc() {
        return maDonThuoc;
    }

    public void setMaDonThuoc(String maDonThuoc) {
        this.maDonThuoc = maDonThuoc;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public void setTenBacSi(String tenBacSi) {
        this.tenBacSi = tenBacSi;
    }

    public String getCoSoKhamBenh() {
        return coSoKhamBenh;
    }

    public void setCoSoKhamBenh(String coSoKhamBenh) {
        this.coSoKhamBenh = coSoKhamBenh;
    }

    public LocalDate getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(LocalDate ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DonThuoc donThuoc = (DonThuoc) o;
        return Objects.equals(maDonThuoc, donThuoc.maDonThuoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDonThuoc);
    }

    @Override
    public String toString() {
        return "DonThuoc{" +
                "maDonThuoc='" + maDonThuoc + '\'' +
                ", tenBacSi='" + tenBacSi + '\'' +
                ", coSoKhamBenh='" + coSoKhamBenh + '\'' +
                ", ngayKeDon=" + ngayKeDon +
                '}';
    }
}
