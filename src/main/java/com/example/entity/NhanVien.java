package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "maNhanVien")
@ToString(of = {"maNhanVien", "tenNhanVien", "cccd", "sdt", "chucVu", "trangThaiHoatDong"})
@Entity
@Table(name = "NhanVien")
public class NhanVien {

    @Id
    @Column(name = "maNhanVien", columnDefinition = "nvarchar(50)")
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

    @Column(name = "trangThaiHoatDong")
    private boolean trangThaiHoatDong;

    public NhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public NhanVien(String maNhanVien, String tenNhanVien, String cccd, String sdt,
                    ChucVu chucVu, boolean trangThaiHoatDong) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.cccd = cccd;
        this.sdt = sdt;
        this.chucVu = chucVu;
        this.trangThaiHoatDong = trangThaiHoatDong;
    }
}
