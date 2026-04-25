package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "maCa")
@ToString(of = {"maCa", "gioBatDau", "gioKetThuc", "trangThaiCaLam", "tienMoCa", "tienKetCa", "tienHeThong"})
@Entity
@Table(name = "CaLam")
public class CaLam {

    @Id
    @Column(name = "maCa", columnDefinition = "nvarchar(50)")
    private String maCa;

    @ManyToOne
    @JoinColumn(name = "maNhanVien", columnDefinition = "nvarchar(50)")
    private NhanVien nhanVien;

    @Column(name = "gioBatDau")
    private LocalDateTime gioBatDau;

    @Column(name = "gioKetThuc")
    private LocalDateTime gioKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "trangThaiCaLam")
    private TrangThaiCaLam trangThaiCaLam;

    @Column(name = "tienMoCa")
    private double tienMoCa;

    @Column(name = "tienKetCa")
    private double tienKetCa;

    @Column(name = "tienHeThong")
    private double tienHeThong;

    @Column(name = "ghiChu", columnDefinition = "nvarchar(MAX)")
    private String ghiChu;

    public CaLam(String maCa) {
        this.maCa = maCa;
    }

    public CaLam(String maCa, NhanVien nhanVien, LocalDateTime gioBatDau, LocalDateTime gioKetThuc,
                 TrangThaiCaLam trangThaiCaLam, double tienMoCa, double tienKetCa,
                 double tienHeThong, String ghiChu) {
        this.maCa = maCa;
        this.nhanVien = nhanVien;
        this.gioBatDau = gioBatDau;
        this.gioKetThuc = gioKetThuc;
        this.trangThaiCaLam = trangThaiCaLam;
        this.tienMoCa = tienMoCa;
        this.tienKetCa = tienKetCa;
        this.tienHeThong = tienHeThong;
        this.ghiChu = ghiChu;
    }
}
