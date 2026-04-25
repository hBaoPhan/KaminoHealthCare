package com.example.entity;

public enum TrangThaiKhachHang {
    KHACH_HANG_THANH_VIEN("Khách hàng thành viên"),
    KHACH_LE("Khách lẻ");

    private final String moTa;

    TrangThaiKhachHang(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
