package com.example.entity;

public enum PhuongThucThanhToan {
    TIENMAT("Tiền mặt"),
    CHUYENKHOAN("Chuyển khoản");

    private final String moTa;

    PhuongThucThanhToan(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
