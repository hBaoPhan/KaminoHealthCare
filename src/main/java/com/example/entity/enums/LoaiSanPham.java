package com.example.entity.enums;

public enum LoaiSanPham {
    ETC("Thuốc kê đơn"),
    OTC("Thuốc không kê đơn"),
    TPCN("Thực phẩm chức năng");

    private final String moTa;

    LoaiSanPham(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
