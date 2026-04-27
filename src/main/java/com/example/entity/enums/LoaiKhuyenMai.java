package com.example.entity.enums;

public enum LoaiKhuyenMai {
    PHAN_TRAM("Phần trăm"),
    TANG_KEM("Tặng kèm");

    private final String moTa;

    LoaiKhuyenMai(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
