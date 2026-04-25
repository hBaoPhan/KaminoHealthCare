package com.example.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "maDonVi")
@ToString(of = {"maDonVi", "tenDonVi", "heSoQuyDoi"})
@Entity
@Table(name = "DonViQuyDoi")
public class DonViQuyDoi {

    @Id
    @Column(name = "maDonVi", columnDefinition = "nvarchar(50)")
    private String maDonVi;

    @Enumerated(EnumType.STRING)
    @Column(name = "tenDonVi")
    private DonVi tenDonVi;

    @Column(name = "heSoQuyDoi")
    private int heSoQuyDoi;

    @ManyToOne
    @JoinColumn(name = "maSanPham", columnDefinition = "nvarchar(50)")
    private SanPham sanPham;

    public DonViQuyDoi(String maDonVi) {
        this.maDonVi = maDonVi;
    }
}
