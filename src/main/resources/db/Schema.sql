-- Khởi tạo Database
CREATE DATABASE IF NOT EXISTS QUANLYKAMINOHEATHCARE
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE QUANLYKAMINOHEATHCARE;

-- ==========================================
-- TẠO CÁC BẢNG KHÔNG CÓ KHÓA NGOẠI (ĐỘC LẬP)
-- ==========================================

-- 1. Bảng NhanVien
CREATE TABLE NhanVien (
    maNhanVien VARCHAR(50) PRIMARY KEY,
    tenNhanVien VARCHAR(255) NOT NULL,
    cccd VARCHAR(20) NOT NULL UNIQUE,
    sdt VARCHAR(15) NOT NULL,
    chucVu ENUM('DUOCSI', 'NHANVIENQUANLY') NOT NULL,
    trangThai BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Bảng KhachHang
CREATE TABLE KhachHang (
    maKhachHang VARCHAR(50) PRIMARY KEY,
    tenKhachHang VARCHAR(255) NOT NULL,
    sdt VARCHAR(15) NOT NULL,
    trangThai ENUM('KHACHHANGTHANHVIEN', 'KHACHLE') NOT NULL
);

-- 3. Bảng DonThuoc
CREATE TABLE DonThuoc (
    maDonThuoc VARCHAR(50) PRIMARY KEY,
    tenBacSi VARCHAR(255),
    coSoKhamBenh VARCHAR(255),
    ngayKeDon DATETIME
);

-- 4. Bảng SanPham
CREATE TABLE SanPham (
    maSanPham VARCHAR(50) PRIMARY KEY,
    tenSanPham VARCHAR(255) NOT NULL,
    phanLoai ENUM('ETC', 'OTC', 'TPCN') NOT NULL,
    soLuongTon INT NOT NULL DEFAULT 0,
    moTa TEXT,
    hoatChat VARCHAR(255),
    donGiaCoBan DOUBLE NOT NULL,
    trangThaiKinhDoanh BOOLEAN NOT NULL DEFAULT TRUE,
    thue DOUBLE NOT NULL DEFAULT 0
);

-- 5. Bảng KhuyenMai
CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(50) PRIMARY KEY,
    tenKhuyenMai VARCHAR(255) NOT NULL,
    thoiGianBatDau DATETIME NOT NULL,
    thoiGianKetThuc DATETIME NOT NULL,
    loaiKhuyenMai ENUM('PHANTRAM', 'TANGKEM') NOT NULL,
    khuyenMaiPhanTram DOUBLE DEFAULT 0
);

-- ==========================================
-- TẠO CÁC BẢNG CÓ KHÓA NGOẠI (PHỤ THUỘC)
-- ==========================================

-- 6. Bảng TaiKhoan (Phụ thuộc NhanVien)
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(255) NOT NULL,
    maNhanVien VARCHAR(50) NOT NULL UNIQUE,
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien) ON DELETE CASCADE
);

-- 7. Bảng CaLam (Phụ thuộc NhanVien)
CREATE TABLE CaLam (
    maCa VARCHAR(50) PRIMARY KEY,
    maNhanVien VARCHAR(50) NOT NULL,
    gioBatDau DATETIME NOT NULL,
    gioKetThuc DATETIME,
    trangThai ENUM('DANGMO', 'DONG') NOT NULL,
    tienMoCa DOUBLE NOT NULL DEFAULT 0,
    tienKetCa DOUBLE DEFAULT 0,
    tienHeThong DOUBLE DEFAULT 0,
    ghiChu TEXT,
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien)
);

-- 8. Bảng DonViQuyDoi (Phụ thuộc SanPham)
CREATE TABLE DonViQuyDoi (
    maDonVi VARCHAR(50) PRIMARY KEY,
    tenDonVi ENUM('VIEN', 'VI', 'HOP', 'TUYP', 'CHAI') NOT NULL,
    heSoQuyDoi INT NOT NULL,
    maSanPham VARCHAR(50) NOT NULL,
    FOREIGN KEY (maSanPham) REFERENCES SanPham(maSanPham) ON DELETE CASCADE
);

-- 9. Bảng Lo (Phụ thuộc SanPham)
CREATE TABLE Lo (
    maLo VARCHAR(50) PRIMARY KEY,
    soLo VARCHAR(50) NOT NULL,
    ngayHetHan DATE NOT NULL,
    soLuongSanPham INT NOT NULL DEFAULT 0,
    maSanPham VARCHAR(50) NOT NULL,
    giaNhap DOUBLE NOT NULL,
    FOREIGN KEY (maSanPham) REFERENCES SanPham(maSanPham) ON DELETE CASCADE
);

-- 10. Bảng QuaTang (Bảng trung gian giữa KhuyenMai và SanPham)
CREATE TABLE QuaTang (
    maKhuyenMai VARCHAR(50) NOT NULL,
    maSanPham VARCHAR(50) NOT NULL,
    soLuongTang INT NOT NULL DEFAULT 1,
    PRIMARY KEY (maKhuyenMai, maSanPham),
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai) ON DELETE CASCADE,
    FOREIGN KEY (maSanPham) REFERENCES SanPham(maSanPham) ON DELETE CASCADE
);

-- 11. Bảng HoaDon (Trung tâm, phụ thuộc nhiều bảng)
CREATE TABLE HoaDon (
    maHoaDon VARCHAR(50) PRIMARY KEY,
    thoiGianTao DATETIME NOT NULL,
    maNhanVien VARCHAR(50) NOT NULL,
    trangThaiThanhToan BOOLEAN NOT NULL DEFAULT FALSE,
    maKhachHang VARCHAR(50),
    maKhuyenMai VARCHAR(50),
    loaiHoaDon ENUM('BANHANG', 'DOIHANG', 'TRAHANG') NOT NULL,
    maCa VARCHAR(50) NOT NULL,
    ghiChu TEXT,
    maHoaDonDoiTra VARCHAR(50),
    maDonThuoc VARCHAR(50),
    
    FOREIGN KEY (maNhanVien) REFERENCES NhanVien(maNhanVien),
    FOREIGN KEY (maKhachHang) REFERENCES KhachHang(maKhachHang),
    FOREIGN KEY (maKhuyenMai) REFERENCES KhuyenMai(maKhuyenMai),
    FOREIGN KEY (maCa) REFERENCES CaLam(maCa),
    FOREIGN KEY (maHoaDonDoiTra) REFERENCES HoaDon(maHoaDon),
    FOREIGN KEY (maDonThuoc) REFERENCES DonThuoc(maDonThuoc)
);

-- 12. Bảng ChiTietHoaDon (Bảng trung gian yếu, phụ thuộc HoaDon, DonViQuyDoi, Lo)
CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(50) NOT NULL,
    maDonVi VARCHAR(50) NOT NULL,
    maLo VARCHAR(50) NOT NULL,
    soLuong INT NOT NULL,
    donGia DOUBLE NOT NULL,
    laQuaTangKem BOOLEAN NOT NULL DEFAULT FALSE,
    
    PRIMARY KEY (maHoaDon, maDonVi, maLo),
    FOREIGN KEY (maHoaDon) REFERENCES HoaDon(maHoaDon) ON DELETE CASCADE,
    FOREIGN KEY (maDonVi) REFERENCES DonViQuyDoi(maDonVi),
    FOREIGN KEY (maLo) REFERENCES Lo(maLo)
);