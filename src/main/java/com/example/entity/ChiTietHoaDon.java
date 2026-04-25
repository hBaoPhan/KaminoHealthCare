package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"hoaDon", "donViQuyDoi"})
@Entity
@Table(name = "ChiTietHoaDon")
@IdClass(ChiTietHoaDonId.class)
public class ChiTietHoaDon {

    @Id
    @ManyToOne
    @JoinColumn(name = "maHoaDon", columnDefinition = "nvarchar(50)")
    private HoaDon hoaDon;

    @Id
    @ManyToOne
    @JoinColumn(name = "maDonVi", columnDefinition = "nvarchar(50)")
    private DonViQuyDoi donViQuyDoi;

    @Column(name = "soLuong")
    private int soLuong;

    @Column(name = "donGia")
    private double donGia;

    @Column(name = "laQuaTangKem")
    private boolean laQuaTangKem;

    @Transient
    private List<SuPhanBoLo> dsPhanBoLo;

    public ChiTietHoaDon(HoaDon hoaDon, DonViQuyDoi donViQuyDoi, int soLuong,
                         double donGia, boolean laQuaTangKem, List<SuPhanBoLo> dsPhanBoLo) {
        this.hoaDon = hoaDon;
        this.donViQuyDoi = donViQuyDoi;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.laQuaTangKem = laQuaTangKem;
        this.dsPhanBoLo = dsPhanBoLo;
    }

    /** Tính thành tiền của dòng chi tiết (bao gồm thuế, quà tặng = 0đ) */
    public double tinhThanhTien() {
        if (laQuaTangKem) return 0;
        return this.soLuong * this.donGia + tinhTienThue();
    }

    /** Tính tiền thuế dựa trên thuế suất của sản phẩm */
    public double tinhTienThue() {
        if (laQuaTangKem || donViQuyDoi == null || donViQuyDoi.getSanPham() == null) {
            return 0.0;
        }
        double thueSuat = donViQuyDoi.getSanPham().getThue();
        return (this.soLuong * this.donGia) * (thueSuat / 100.0);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "donViQuyDoi=" + (donViQuyDoi != null ? donViQuyDoi.getMaDonVi() : "null") +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", laQuaTangKem=" + laQuaTangKem +
                '}';
    }
}
