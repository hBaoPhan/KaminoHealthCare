package com.example.entity;

import java.io.Serializable;
import java.util.Objects;

public class QuaTangId implements Serializable {
    private String khuyenMai;
    private String sanPham;

    public QuaTangId() {}

    public QuaTangId(String khuyenMai, String sanPham) {
        this.khuyenMai = khuyenMai;
        this.sanPham = sanPham;
    }

    public String getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(String khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public String getSanPham() {
        return sanPham;
    }

    public void setSanPham(String sanPham) {
        this.sanPham = sanPham;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuaTangId quaTangId = (QuaTangId) o;
        return Objects.equals(khuyenMai, quaTangId.khuyenMai) && Objects.equals(sanPham, quaTangId.sanPham);
    }

    @Override
    public int hashCode() {
        return Objects.hash(khuyenMai, sanPham);
    }
}
