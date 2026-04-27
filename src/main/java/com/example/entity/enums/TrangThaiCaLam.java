package com.example.entity.enums;

public enum TrangThaiCaLam {
    DANG_MO("Đang mở"),
    DONG("Đóng");

    private final String moTa;

    TrangThaiCaLam(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
