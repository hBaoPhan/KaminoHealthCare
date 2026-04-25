package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "maKhachHang")
@ToString(of = {"maKhachHang", "tenKhachHang", "sdt", "trangThaiKhachHang"})
@Entity
@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @Column(name = "maKhachHang", columnDefinition = "nvarchar(50)")
    private String maKhachHang;

    @Column(name = "tenKhachHang", columnDefinition = "nvarchar(255)")
    private String tenKhachHang;

    @Column(name = "sdt", length = 15)
    private String sdt;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThaiKhachHang")
    private TrangThaiKhachHang trangThaiKhachHang;

    public KhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public KhachHang(String maKhachHang, String tenKhachHang, String sdt, TrangThaiKhachHang trangThaiKhachHang) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.sdt = sdt;
        this.trangThaiKhachHang = trangThaiKhachHang;
    }
}
