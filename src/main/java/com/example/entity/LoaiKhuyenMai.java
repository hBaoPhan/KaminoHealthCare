package com.example.entity;

public enum LoaiKhuyenMai {
    PHANTRAM("Phần trăm"),
    TANGKEM("Tặng kèm");

    private final String moTa;

    LoaiKhuyenMai(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
