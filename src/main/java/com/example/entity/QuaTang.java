package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"khuyenMai", "sanPham"})
@Entity
@Table(name = "QuaTang")
@IdClass(QuaTangId.class)
public class QuaTang {

    @Id
    @ManyToOne
    @JoinColumn(name = "maKhuyenMai", columnDefinition = "nvarchar(50)")
    private KhuyenMai khuyenMai;

    @Id
    @ManyToOne
    @JoinColumn(name = "maSanPham", columnDefinition = "nvarchar(50)")
    private SanPham sanPham;

    @Column(name = "soLuongTang")
    private int soLuongTang;

    public QuaTang(KhuyenMai khuyenMai, SanPham sanPham, int soLuongTang) {
        this.khuyenMai = khuyenMai;
        this.sanPham = sanPham;
        this.soLuongTang = soLuongTang;
    }

    @Override
    public String toString() {
        return "QuaTang{" +
                "khuyenMai=" + (khuyenMai != null ? khuyenMai.getMaKhuyenMai() : "null") +
                ", sanPham=" + (sanPham != null ? sanPham.getMaSanPham() : "null") +
                ", soLuongTang=" + soLuongTang +
                '}';
    }
}
