package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "maKhuyenMai")
@ToString(of = {"maKhuyenMai", "tenKhuyenMai", "loaiKhuyenMai", "khuyenMaiPhanTram", "giaTriDonHangToiThieu"})
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

    @Column(name = "khuyenMaiPhanTram",nullable = true)
    private double khuyenMaiPhanTram;

    @Transient
    private QuaTang quaTangKem;

    @Column(name = "giaTriDonHangToiThieu")
    private double giaTriDonHangToiThieu;

    public KhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, LocalDateTime thoiGianBatDau,
                     LocalDateTime thoiGianKetThuc, LoaiKhuyenMai loaiKhuyenMai,
                     double khuyenMaiPhanTram, QuaTang quaTangKem, double giaTriDonHangToiThieu) {
        this.maKhuyenMai = maKhuyenMai;
        this.tenKhuyenMai = tenKhuyenMai;
        this.thoiGianBatDau = thoiGianBatDau;
        this.thoiGianKetThuc = thoiGianKetThuc;
        this.loaiKhuyenMai = loaiKhuyenMai;
        this.khuyenMaiPhanTram = khuyenMaiPhanTram;
        this.quaTangKem = quaTangKem;
        this.giaTriDonHangToiThieu = giaTriDonHangToiThieu;
    }

    /** Kiểm tra khuyến mãi đã hết hạn */
    public boolean daHetHan() {
        return thoiGianKetThuc != null && thoiGianKetThuc.isBefore(LocalDateTime.now());
    }
}
