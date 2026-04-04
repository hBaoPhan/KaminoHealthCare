package com.example.entity;

public enum LoaiHoaDon {
    BANHANG("Bán hàng"),
    DOIHANG("Đổi hàng"),
    TRAHANG("Trả hàng");

    private final String moTa;

    LoaiHoaDon(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
