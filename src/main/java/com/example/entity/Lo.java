package com.example.entity;

import java.time.LocalDate;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "Lo")
public class Lo {
    @Id
    @Column(name = "maLo", columnDefinition = "nvarchar(50)")
    private String maLo;

    @Column(name = "soLo", columnDefinition = "nvarchar(50)")
    private String soLo;

    @Column(name = "ngayHetHan", columnDefinition = "date")
    private LocalDate ngayHetHan;

    @Column(name = "soLuongSanPham")
    private int soLuongSanPham;

    @ManyToOne
    @JoinColumn(name = "maSanPham", columnDefinition = "nvarchar(50)")
    private SanPham sanPham;

    @Column(name = "giaNhap", columnDefinition = "")
    private double giaNhap;

    public Lo() {
    }

    public Lo(String maLo) {
        this.maLo = maLo;
    }

    public Lo(String maLo, String soLo, LocalDate ngayHetHan, int soLuongSanPham, SanPham sanPham, double giaNhap) {
        this.maLo = maLo;
        this.soLo = soLo;
        this.ngayHetHan = ngayHetHan;
        this.soLuongSanPham = soLuongSanPham;
        this.sanPham = sanPham;
        this.giaNhap = giaNhap;
    }

    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo;
    }

    public String getSoLo() {
        return soLo;
    }

    public void setSoLo(String soLo) {
        this.soLo = soLo;
    }

    public LocalDate getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDate ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public int getSoLuongSanPham() {
        return soLuongSanPham;
    }

    public void setSoLuongSanPham(int soLuongSanPham) {
        this.soLuongSanPham = soLuongSanPham;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public boolean daHetHan() {
        return ngayHetHan != null && ngayHetHan.isBefore(LocalDate.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lo lo = (Lo) o;
        return Objects.equals(maLo, lo.maLo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLo);
    }

    @Override
    public String toString() {
        return "Lo{" +
                "maLo='" + maLo + '\'' +
                ", soLo='" + soLo + '\'' +
                ", ngayHetHan=" + ngayHetHan +
                ", soLuongSanPham=" + soLuongSanPham +
                ", sanPham=" + sanPham +
                ", giaNhap=" + giaNhap +
                '}';
    }
}
