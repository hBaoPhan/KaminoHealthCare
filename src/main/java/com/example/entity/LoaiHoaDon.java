package com.example.entity;

public enum LoaiHoaDon {
    BAN_HANG("Bán hàng"),
    DOI_HANG("Đổi hàng"),
    TRA_HANG("Trả hàng");

    private final String moTa;

    LoaiHoaDon(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
