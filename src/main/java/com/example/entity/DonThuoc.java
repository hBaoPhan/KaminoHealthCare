package com.example.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "maDonThuoc")
@ToString(of = {"maDonThuoc", "tenBacSi", "coSoKhamBenh", "ngayKeDon"})
@Entity
@Table(name = "DonThuoc")
public class DonThuoc {

    @Id
    @Column(name = "maDonThuoc", columnDefinition = "nvarchar(50)")
    private String maDonThuoc;

    @Column(name = "tenBacSi", columnDefinition = "nvarchar(255)")
    private String tenBacSi;

    @Column(name = "coSoKhamBenh", columnDefinition = "nvarchar(255)")
    private String coSoKhamBenh;

    @Column(name = "ngayKeDon")
    private LocalDate ngayKeDon;

    public DonThuoc(String maDonThuoc) {
        this.maDonThuoc = maDonThuoc;
    }
}
