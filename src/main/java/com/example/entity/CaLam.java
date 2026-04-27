package com.example.entity;

import com.example.entity.enums.*;
import java.time.LocalDateTime;
import java.util.Objects;

public class CaLam {
    private String maCa;
    private NhanVien nhanVien;
    private LocalDateTime gioBatDau;
    private LocalDateTime gioKetThuc;
    private TrangThaiCaLam trangThai;
    private double tienMoCa;
    private double tienKetCa;
    private double tienHeThong;
    private String ghiChu;

    public CaLam() {
    }

    public CaLam(String maCa) {
        this.maCa = maCa;
    }

    public CaLam(String maCa, NhanVien nhanVien, LocalDateTime gioBatDau, LocalDateTime gioKetThuc, TrangThaiCaLam trangThai, double tienMoCa, double tienKetCa, double tienHeThong, String ghiChu) {
        this.maCa = maCa;
        this.nhanVien = nhanVien;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.trangThai = trangThai;
        this.tienMoCa = tienMoCa;
        this.tienKetCa = tienKetCa;
        this.tienHeThong = tienHeThong;
        this.ghiChu = ghiChu;
    }

    public String getMaCa() {
        return maCa;
    }

    public void setMaCa(String maCa) {
        this.maCa = maCa;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDateTime getGioBatDau() {
        return gioBatDau;
    }

    public void setGioBatDau(LocalDateTime gioBatDau) {
        this.gioBatDau = gioBatDau;
    }

    public LocalDateTime getGioKetThuc() {
        return gioKetThuc;
    }

    public void setGioKetThuc(LocalDateTime gioKetThuc) {
        this.gioKetThuc = gioKetThuc;
    }

    public TrangThaiCaLam getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiCaLam trangThai) {
        this.trangThai = trangThai;
    }

    public double getTienMoCa() {
        return tienMoCa;
    }

    public void setTienMoCa(double tienMoCa) {
        this.tienMoCa = tienMoCa;
    }

    public double getTienKetCa() {
        return tienKetCa;
    }

    public void setTienKetCa(double tienKetCa) {
        this.tienKetCa = tienKetCa;
    }

    public double getTienHeThong() {
        return tienHeThong;
    }

    public void setTienHeThong(double tienHeThong) {
        this.tienHeThong = tienHeThong;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CaLam caLam = (CaLam) o;
        return Objects.equals(maCa, caLam.maCa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCa);
    }

    @Override
    public String toString() {
        return "CaLam{" +
                "maCa='" + maCa + '\'' +
                ", nhanVien=" + nhanVien +
                ", gioBatDau=" + gioBatDau +
                ", gioKetThuc=" + gioKetThuc +
                ", trangThai=" + trangThai +
                ", tienMoCa=" + tienMoCa +
                ", tienKetCa=" + tienKetCa +
                ", tienHeThong=" + tienHeThong +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
    }
}
