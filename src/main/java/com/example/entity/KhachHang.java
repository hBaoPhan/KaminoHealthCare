package com.example.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "KhachHang")
public class KhachHang {
    
    @Id
    @Column(name = "maKhachHang", length = 50)
    private String maKhachHang;
    
    @Column(name = "tenKhachHang", columnDefinition = "nvarchar(255)")
    private String tenKhachHang;
    
    @Column(name = "sdt", length = 15)
    private String sdt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "trangThai")
    private TrangThaiKhachHang trangThai;

    public KhachHang() {
    }

    public KhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public KhachHang(String maKhachHang, String tenKhachHang, String sdt, TrangThaiKhachHang trangThai) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.sdt = sdt;
        this.trangThai = trangThai;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public TrangThaiKhachHang getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiKhachHang trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhachHang khachHang = (KhachHang) o;
        return Objects.equals(maKhachHang, khachHang.maKhachHang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhachHang);
    }

    @Override
    public String toString() {
        return "KhachHang{" +
                "maKhachHang='" + maKhachHang + '\'' +
                ", tenKhachHang='" + tenKhachHang + '\'' +
                ", sdt='" + sdt + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }
}
