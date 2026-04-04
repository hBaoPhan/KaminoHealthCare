package com.example.entity;

public enum ChucVu {
    DUOCSI("Dược sĩ"),
    NHANVIENQUANLY("Nhân viên quản lý");

    private final String moTa;

    ChucVu(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
