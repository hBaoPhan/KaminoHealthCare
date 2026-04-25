package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "maSanPham")
@ToString(of = {"maSanPham", "tenSanPham", "phanLoai", "soLuongTon", "donGiaCoBan", "trangThaiKinhDoanh", "thue"})
@Entity
@Table(name = "SanPham")
public class SanPham {

    @Id
    @Column(name = "maSanPham", columnDefinition = "nvarchar(50)")
    private String maSanPham;

    @Column(name = "tenSanPham", columnDefinition = "nvarchar(255)")
    private String tenSanPham;

    @Enumerated(EnumType.STRING)
    @Column(name = "phanLoai")
    private PhanLoai phanLoai;

    @Column(name = "soLuongTon")
    private int soLuongTon;

    @Column(name = "moTa", columnDefinition = "nvarchar(MAX)")
    private String moTa;

    @Column(name = "hoatChat", columnDefinition = "nvarchar(255)")
    private String hoatChat;

    @Column(name = "donGiaCoBan")
    private double donGiaCoBan;

    @Column(name = "trangThaiKinhDoanh")
    private boolean trangThaiKinhDoanh;

    @Column(name = "thue")
    private double thue;

    public SanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public SanPham(String maSanPham, String tenSanPham, PhanLoai phanLoai, int soLuongTon,
                   String moTa, String hoatChat, double donGiaCoBan,
                   boolean trangThaiKinhDoanh, double thue) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.phanLoai = phanLoai;
        this.soLuongTon = soLuongTon;
        this.moTa = moTa;
        this.hoatChat = hoatChat;
        this.donGiaCoBan = donGiaCoBan;
        this.trangThaiKinhDoanh = trangThaiKinhDoanh;
        this.thue = thue;
    }
}
