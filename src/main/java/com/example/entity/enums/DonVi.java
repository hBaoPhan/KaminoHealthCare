package com.example.entity.enums;

public enum DonVi {
    VIEN("Viên"),
    VI("Vỉ"),
    HOP("Hộp"),
    TUYP("Tuýp"),
    CHAI("Chai"),
    CAI("Cái");

    private final String moTa;

    DonVi(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    public static DonVi tuMoTa(String moTa) {
        for (DonVi dv : DonVi.values()) {
            if (dv.moTa.equalsIgnoreCase(moTa)) {
                return dv;
            }
        }
        return null;
    }
}
