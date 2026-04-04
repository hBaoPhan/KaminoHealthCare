package com.example.entity;

public enum TrangThaiCaLam {
    DANGMO("Đang mở"),
    DONG("Đóng");

    private final String moTa;

    TrangThaiCaLam(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
