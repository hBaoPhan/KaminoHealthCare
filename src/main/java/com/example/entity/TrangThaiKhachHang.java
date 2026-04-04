package com.example.entity;

public enum TrangThaiKhachHang {
    KHACHHANGTHANHVIEN("Khách hàng thành viên"),
    KHACHLE("Khách lẻ");

    private final String moTa;

    TrangThaiKhachHang(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
