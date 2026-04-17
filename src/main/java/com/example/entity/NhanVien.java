package com.example.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "NhanVien")
public class NhanVien {
    @Id
    @Column(name = "maNhanVien", length = 50)
    private String maNhanVien;

    @Column(name = "tenNhanVien", columnDefinition = "nvarchar(255)")
    private String tenNhanVien;

    @Column(name = "cccd", length = 20)
    private String cccd;

    @Column(name = "sdt", length = 15)
    private String sdt;

    @Enumerated(EnumType.STRING)
    @Column(name = "chucVu")
    private ChucVu chucVu;

    @Column(name = "trangThai")
    private boolean trangThai;

    public NhanVien() {
    }

    public NhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String maNhanVien, String tenNhanVien, String cccd, String sdt, ChucVu chucVu, boolean trangThai) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.cccd = cccd;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.trangThai = trangThai;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        this.chucVu = chucVu;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(maNhanVien, nhanVien.maNhanVien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhanVien);
    }

    @Override
    public String toString() {
        return "NhanVien{" +
                "maNhanVien='" + maNhanVien + '\'' +
                ", tenNhanVien='" + tenNhanVien + '\'' +
                ", cccd='" + cccd + '\'' +
                ", sdt='" + sdt + '\'' +
                ", chucVu=" + chucVu +
                ", trangThai=" + trangThai +
                '}';
    }
}
