package com.example.entity;

import com.example.entity.enums.*;
import java.util.List;
import java.util.Objects;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private DonViQuyDoi donViQuyDoi;
    private int soLuong;
    private double donGia;
    private boolean laQuaTangKem;
    private List<SuPhanBoLo> dsPhanBoLo;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, DonViQuyDoi donViQuyDoi, int soLuong, double donGia, boolean laQuaTangKem,
			List<SuPhanBoLo> dsPhanBoLo) {
		super();
		this.hoaDon = hoaDon;
		this.donViQuyDoi = donViQuyDoi;
		this.soLuong = soLuong;
		this.donGia = donGia;
		this.laQuaTangKem = laQuaTangKem;
		this.dsPhanBoLo = dsPhanBoLo;
	}

	public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public DonViQuyDoi getDonViQuyDoi() {
        return donViQuyDoi;
    }

    public void setDonViQuyDoi(DonViQuyDoi donViQuyDoi) {
        this.donViQuyDoi = donViQuyDoi;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public boolean isLaQuaTangKem() {
        return laQuaTangKem;
    }

    public void setLaQuaTangKem(boolean laQuaTangKem) {
        this.laQuaTangKem = laQuaTangKem;
    }


    public List<SuPhanBoLo> getDsPhanBoLo() {
		return dsPhanBoLo;
	}

	public void setDsPhanBoLo(List<SuPhanBoLo> dsPhanBoLo) {
		this.dsPhanBoLo = dsPhanBoLo;
	}

	public double tinhThanhTien() {
        if (laQuaTangKem) return 0;
        return this.soLuong * this.donGia + tinhTienThue();
    }
	
	public double tinhTienThue() {
        if (laQuaTangKem || donViQuyDoi == null || donViQuyDoi.getSanPham() == null) {
            return 0.0;
        }
        double thueSuat = donViQuyDoi.getSanPham().getThue();
        return (this.soLuong * this.donGia) * (thueSuat / 100.0);
    }
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChiTietHoaDon that = (ChiTietHoaDon) o;
        return Objects.equals(hoaDon, that.hoaDon) && Objects.equals(donViQuyDoi, that.donViQuyDoi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hoaDon, donViQuyDoi);
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon{" +
                "donViQuyDoi=" + (donViQuyDoi != null ? donViQuyDoi.getMaDonVi() : "null") +
                ", soLuong=" + soLuong +
                ", donGia=" + donGia +
                ", laQuaTangKem=" + laQuaTangKem +
                '}';
    }
}
