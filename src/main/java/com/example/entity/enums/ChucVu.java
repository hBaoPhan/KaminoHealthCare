package com.example.entity.enums;

public enum ChucVu {
    DUOC_SI("Dược sĩ"),
    NHAN_VIEN_QUAN_LY("Nhân viên quản lý");

    private final String moTa;

    ChucVu(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
