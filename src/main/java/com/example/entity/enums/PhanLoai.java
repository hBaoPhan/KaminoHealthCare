package com.example.entity.enums;

public enum PhanLoai {
    ETC("Thuốc kê đơn"),
    OTC("Thuốc không kê đơn"),
    TPCN("Thực phẩm chức năng");

    private final String moTa;

    PhanLoai(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
