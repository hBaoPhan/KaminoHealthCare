package com.example.entity.enums;

public enum DonVi {
    VIEN("Viên"),
    VI("Vỉ"),
    HOP("Hộp"),
    TUYP("Tuýp"),
    CHAI("Chai");

    private final String moTa;

    DonVi(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }
}
