package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "maLo")
@ToString(of = {"maLo", "soLo", "ngayHetHan", "soLuongSanPham", "giaNhap"})
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

    @Column(name = "giaNhap")
    private double giaNhap;

    public Lo(String maLo) {
        this.maLo = maLo;
    }

    public Lo(String maLo, String soLo, LocalDate ngayHetHan, int soLuongSanPham,
              SanPham sanPham, double giaNhap) {
        this.maLo = maLo;
        this.soLo = soLo;
        this.ngayHetHan = ngayHetHan;
        this.soLuongSanPham = soLuongSanPham;
        this.sanPham = sanPham;
        this.giaNhap = giaNhap;
    }

    /** Kiểm tra lô hàng đã hết hạn sử dụng */
    public boolean daHetHan() {
        return ngayHetHan != null && ngayHetHan.isBefore(LocalDate.now());
    }
}
