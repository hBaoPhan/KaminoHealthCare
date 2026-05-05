use master
CREATE DATABASE [QUANLYKAMINOHEALTHCARE];
GO
USE [QUANLYKAMINOHEALTHCARE];
GO

--- 2. KHỞI TẠO CÁC BẢNG THỰC THỂ ---

CREATE TABLE NhanVien (
    maNhanVien VARCHAR(10) PRIMARY KEY,
    tenNhanVien NVARCHAR(100) NOT NULL,
    cccd VARCHAR(12) UNIQUE,
    sdt VARCHAR(10),
    chucVu NVARCHAR(50) NOT NULL CHECK (chucVu IN (N'DUOC_SI', N'NHAN_VIEN_QUAN_LY')),
    trangThaiHoatDong BIT -- 1: Đang làm, 0: Nghỉ
);

CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(255) NOT NULL,
    maNhanVien VARCHAR(10) FOREIGN KEY REFERENCES NhanVien(maNhanVien)
);

CREATE TABLE CaLam (
    maCa VARCHAR(10) PRIMARY KEY,
    maNhanVien VARCHAR(10) FOREIGN KEY REFERENCES NhanVien(maNhanVien),
    gioBatDau DATETIME,
    gioKetThuc DATETIME,
    -- Thay cho bảng TrangThaiCaLam
    trangThaiCaLam NVARCHAR(20) CHECK (trangThaiCaLam IN (N'DANG_MO', N'DONG')),
    tienMoCa FLOAT,
    tienKetCa FLOAT,
    tienHeThong FLOAT,
    ghiChu NVARCHAR(MAX)
);

CREATE TABLE KhachHang (
    maKhachHang VARCHAR(10) PRIMARY KEY,
    tenKhachHang NVARCHAR(100),
    sdt VARCHAR(10),
    trangThaiKhachHang NVARCHAR(50) CHECK (trangThaiKhachHang IN (N'KHACH_HANG_THANH_VIEN', N'KHACH_LE'))
);

CREATE TABLE KhuyenMai (
    maKhuyenMai VARCHAR(10) PRIMARY KEY,
    tenKhuyenMai NVARCHAR(200),
    thoiGianBatDau DATETIME,
    thoiGianKetThuc DATETIME,
    loaiKhuyenMai NVARCHAR(20) CHECK (loaiKhuyenMai IN (N'PHAN_TRAM', N'TANG_KEM')),
    khuyenMaiPhanTram FLOAT,
    giaTriDonHangToiThieu FLOAT
);

CREATE TABLE SanPham (
    maSanPham VARCHAR(20) PRIMARY KEY,
    tenSanPham NVARCHAR(200) NOT NULL,
    loaiSanPham NVARCHAR(20) CHECK (loaiSanPham IN (N'ETC', N'OTC', N'TPCN')),
    soLuongTon INT DEFAULT 0,
    moTa NVARCHAR(MAX),
    hoatChat NVARCHAR(200),
    donGiaCoBan FLOAT,
    trangThaiKinhDoanh BIT,
    thue FLOAT
);

CREATE TABLE DonViQuyDoi (
    maDonVi VARCHAR(20) PRIMARY KEY,
    tenDonVi NVARCHAR(20) CHECK (tenDonVi IN (N'VIEN', N'VI', N'HOP', N'TUYP', N'CHAI', N'CAI')),
    heSoQuyDoi INT,
    maSanPham VARCHAR(20) FOREIGN KEY REFERENCES SanPham(maSanPham)
);

CREATE TABLE QuaTang (
    maKhuyenMai VARCHAR(10) FOREIGN KEY REFERENCES KhuyenMai(maKhuyenMai),
    maDonVi VARCHAR(20) FOREIGN KEY REFERENCES DonViQuyDoi(maDonVi),
    soLuongTang INT,
    PRIMARY KEY (maKhuyenMai, maDonVi)
);

CREATE TABLE Lo (
    maLo VARCHAR(20) PRIMARY KEY,
    soLo VARCHAR(50),
    ngayHetHan DATE,
    soLuongSanPham INT,
    maSanPham VARCHAR(20) FOREIGN KEY REFERENCES SanPham(maSanPham),
    giaNhap FLOAT
);

--- 3. NGHIỆP VỤ HÓA ĐƠN ---

CREATE TABLE DonThuoc (
    maDonThuoc VARCHAR(20) PRIMARY KEY,
    tenBacSi NVARCHAR(100),
    coSoKhamBenh NVARCHAR(200),
    ngayKeDon DATE
);

CREATE TABLE HoaDon (
    maHoaDon VARCHAR(20) PRIMARY KEY,
    thoiGianTao DATETIME DEFAULT GETDATE(),
    maNhanVien VARCHAR(10) FOREIGN KEY REFERENCES NhanVien(maNhanVien),
    trangThaiThanhToan BIT DEFAULT 0,
    maKhachHang VARCHAR(10) FOREIGN KEY REFERENCES KhachHang(maKhachHang),
    maKhuyenMai VARCHAR(10) FOREIGN KEY REFERENCES KhuyenMai(maKhuyenMai),
    loaiHoaDon NVARCHAR(20) CHECK (loaiHoaDon IN (N'BAN_HANG', N'DOI_HANG', N'TRA_HANG')),
    maCa VARCHAR(10) FOREIGN KEY REFERENCES CaLam(maCa),
    ghiChu NVARCHAR(MAX),
    maHoaDonDoiTra VARCHAR(20) FOREIGN KEY REFERENCES HoaDon(maHoaDon),
    maDonThuoc VARCHAR(20) FOREIGN KEY REFERENCES DonThuoc(maDonThuoc),
    phuongThucThanhToan NVARCHAR(20) CHECK (phuongThucThanhToan IN (N'TIEN_MAT', N'CHUYEN_KHOAN'))
);

CREATE TABLE ChiTietHoaDon (
    maHoaDon VARCHAR(20) FOREIGN KEY REFERENCES HoaDon(maHoaDon),
    maDonVi VARCHAR(20) FOREIGN KEY REFERENCES DonViQuyDoi(maDonVi),
    soLuong INT,
    donGia FLOAT,
    laQuaTangKem BIT DEFAULT 0,
    PRIMARY KEY (maHoaDon, maDonVi)
);

CREATE TABLE SuPhanBoLo (
    maHoaDon VARCHAR(20),
    maDonVi VARCHAR(20),
    maLo VARCHAR(20) FOREIGN KEY REFERENCES Lo(maLo),
    soLuong INT,
    CONSTRAINT FK_SuPhanBoLo_ChiTiet 
        FOREIGN KEY (maHoaDon, maDonVi) 
        REFERENCES ChiTietHoaDon(maHoaDon, maDonVi),
    PRIMARY KEY (maHoaDon, maDonVi, maLo)
);