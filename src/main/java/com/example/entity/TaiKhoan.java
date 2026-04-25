package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "tenDangNhap")
@ToString(of = {"tenDangNhap", "nhanVien"})
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan {

    @Id
    @Column(name = "tenDangNhap", columnDefinition = "nvarchar(50)")
    private String tenDangNhap;

    @Column(name = "matKhau", columnDefinition = "nvarchar(255)")
    private String matKhau;

    @ManyToOne
    @JoinColumn(name = "maNhanVien", columnDefinition = "nvarchar(50)")
    private NhanVien nhanVien;

    public TaiKhoan(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }
}
