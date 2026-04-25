package com.example.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "KhuyenMai")
public class KhuyenMai {
    @Id
    @Column(name = "maKhuyenMai", columnDefinition = "nvarchar(50)")
    private String maKhuyenMai;

    @Column(name = "tenKhuyenMai", columnDefinition = "nvarchar(255)")
    private String tenKhuyenMai;

    @Column(name = "thoiGianBatDau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoiGianKetThuc")
    private LocalDateTime thoiGianKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "loaiKhuyenMai")
    private LoaiKhuyenMai loaiKhuyenMai;

    @Column(name = "khuyenMaiPhanTram")
    private double khuyenMaiPhanTram;

    @Transient
    private QuaTang quaTangKem;

    @Column(name = "giaTriDonHangToiThieu")
    private double giaTriDonHangToiThieu;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, LocalDateTime thoiGianBatDau, LocalDateTime thoiGianKetThuc, LoaiKhuyenMai loaiKhuyenMai, double khuyenMaiPhanTram, QuaTang quaTangKem, double giaTriDonHangToiThieu) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.loaiKhuyenMai = loaiKhuyenMai;
        this.khuyenMaiPhanTram = khuyenMaiPhanTram;
        this.quaTangKem = quaTangKem;
        this.giaTriDonHangToiThieu = giaTriDonHangToiThieu;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getTenKhuyenMai() {
        return tenKhuyenMai;
    }

    public void setTenKhuyenMai(String tenKhuyenMai) {
        this.tenKhuyenMai = tenKhuyenMai;
    }

    public LocalDateTime getThoiGianBatDau() {
        return thoiGianBatDau;
    }

    public void setThoiGianBatDau(LocalDateTime thoiGianBatDau) {
        this.thoiGianBatDau = thoiGianBatDau;
    }

    public LocalDateTime getThoiGianKetThuc() {
        return thoiGianKetThuc;
    }

    public void setThoiGianKetThuc(LocalDateTime thoiGianKetThuc) {
        this.thoiGianKetThuc = thoiGianKetThuc;
    }

    public LoaiKhuyenMai getLoaiKhuyenMai() {
        return loaiKhuyenMai;
    }

    public void setLoaiKhuyenMai(LoaiKhuyenMai loaiKhuyenMai) {
        this.loaiKhuyenMai = loaiKhuyenMai;
    }

    public double getKhuyenMaiPhanTram() {
        return khuyenMaiPhanTram;
    }

    public void setKhuyenMaiPhanTram(double khuyenMaiPhanTram) {
        this.khuyenMaiPhanTram = khuyenMaiPhanTram;
    }

    public QuaTang getQuaTangKem() {
        return quaTangKem;
    }

    public void setQuaTangKem(QuaTang quaTangKem) {
        this.quaTangKem = quaTangKem;
    }

    public double getGiaTriDonHangToiThieu() {
        return giaTriDonHangToiThieu;
    }

    public void setGiaTriDonHangToiThieu(double giaTriDonHangToiThieu) {
        this.giaTriDonHangToiThieu = giaTriDonHangToiThieu;
    }

    public boolean daHetHan() {
        return thoiGianKetThuc != null && thoiGianKetThuc.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhuyenMai khuyenMai = (KhuyenMai) o;
        return Objects.equals(maKhuyenMai, khuyenMai.maKhuyenMai);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhuyenMai);
    }

    @Override
    public String toString() {
        return "KhuyenMai{" +
                "maKhuyenMai='" + maKhuyenMai + '\'' +
                ", tenKhuyenMai='" + tenKhuyenMai + '\'' +
                ", thoiGianBatDau=" + thoiGianBatDau +
                ", thoiGianKetThuc=" + thoiGianKetThuc +
                ", loaiKhuyenMai=" + loaiKhuyenMai +
                ", khuyenMaiPhanTram=" + khuyenMaiPhanTram +
                ", giaTriDonHangToiThieu=" + giaTriDonHangToiThieu +
                '}';
    }
}
