package com.example.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "SuPhanBoLo")
@IdClass(SuPhanBoLoId.class)
public class SuPhanBoLo {

    @Id
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "maHoaDon", referencedColumnName = "maHoaDon", columnDefinition = "nvarchar(50)"),
            @JoinColumn(name = "maDonVi", referencedColumnName = "maDonVi", columnDefinition = "nvarchar(50)")
    })
    private ChiTietHoaDon chiTietHoaDon;

    @Id
    @ManyToOne
    @JoinColumn(name = "maLo", columnDefinition = "nvarchar(50)")
    private Lo lo;

    @Column(name = "soLuong")
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