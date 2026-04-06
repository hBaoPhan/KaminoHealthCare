-- Khởi tạo Database
CREATE DATABASE IF NOT EXISTS `QUANLYKAMINOHEATHCARE`
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `QUANLYKAMINOHEATHCARE`;

-- TẠO CÁC BẢNG 


-- 1. Bảng NhanVien
CREATE TABLE `NhanVien` (
    `maNhanVien` VARCHAR(50) NOT NULL,
    `tenNhanVien` VARCHAR(255) NOT NULL,
    `cccd` VARCHAR(20) NOT NULL,
    `sdt` VARCHAR(15) NOT NULL,
    `chucVu` ENUM('DUOCSI', 'NHANVIENQUANLY') NOT NULL,
    `trangThai` BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`maNhanVien`),
    UNIQUE (`cccd`)
);

-- 2. Bảng KhachHang
CREATE TABLE `KhachHang` (
    `maKhachHang` VARCHAR(50) NOT NULL,
    `tenKhachHang` VARCHAR(255) NOT NULL,
    `sdt` VARCHAR(15) NOT NULL,
    `trangThai` ENUM('KHACHHANGTHANHVIEN', 'KHACHLE') NOT NULL,
    PRIMARY KEY (`maKhachHang`)
);

-- 3. Bảng DonThuoc
CREATE TABLE `DonThuoc` (
    `maDonThuoc` VARCHAR(50) NOT NULL,
    `tenBacSi` VARCHAR(255),
    `coSoKhamBenh` VARCHAR(255),
    `ngayKeDon` DATE,
    PRIMARY KEY (`maDonThuoc`)
);

-- 4. Bảng SanPham
CREATE TABLE `SanPham` (
    `maSanPham` VARCHAR(50) NOT NULL,
    `tenSanPham` VARCHAR(255) NOT NULL,
    `phanLoai` ENUM('ETC', 'OTC', 'TPCN') NOT NULL,
    `soLuongTon` INT NOT NULL DEFAULT 0,
    `moTa` TEXT,
    `hoatChat` VARCHAR(255),
    `donGiaCoBan` DOUBLE NOT NULL,
    `trangThaiKinhDoanh` BOOLEAN NOT NULL DEFAULT TRUE,
    `thue` DOUBLE NOT NULL DEFAULT 0,
    PRIMARY KEY (`maSanPham`)
);

-- 5. Bảng KhuyenMai
CREATE TABLE `KhuyenMai` (
    `maKhuyenMai` VARCHAR(50) NOT NULL,
    `tenKhuyenMai` VARCHAR(255) NOT NULL,
    `thoiGianBatDau` DATETIME NOT NULL,
    `thoiGianKetThuc` DATETIME NOT NULL,
    `loaiKhuyenMai` ENUM('PHANTRAM', 'TANGKEM') NOT NULL,
    `khuyenMaiPhanTram` DOUBLE DEFAULT 0,
    `giaTriDonHangToiThieu` DOUBLE DEFAULT 0,
    PRIMARY KEY (`maKhuyenMai`)
);

-- ==========================================
-- TẠO CÁC BẢNG CÓ KHÓA NGOẠI (PHỤ THUỘC)
-- ==========================================

-- 6. Bảng TaiKhoan
CREATE TABLE `TaiKhoan` (
    `tenDangNhap` VARCHAR(50) NOT NULL,
    `matKhau` VARCHAR(255) NOT NULL,
    `maNhanVien` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`tenDangNhap`),
    UNIQUE (`maNhanVien`),
    CONSTRAINT `fk_TaiKhoan_NhanVien` FOREIGN KEY (`maNhanVien`) REFERENCES `NhanVien`(`maNhanVien`) ON DELETE CASCADE
);

-- 7. Bảng CaLam
CREATE TABLE `CaLam` (
    `maCa` VARCHAR(50) NOT NULL,
    `maNhanVien` VARCHAR(50) NOT NULL,
    `gioBatDau` DATETIME NOT NULL,
    `gioKetThuc` DATETIME,
    `trangThai` ENUM('DANGMO', 'DONG') NOT NULL,
    `tienMoCa` DOUBLE NOT NULL DEFAULT 0,
    `tienKetCa` DOUBLE DEFAULT 0,
    `tienHeThong` DOUBLE DEFAULT 0,
    `ghiChu` TEXT,
    PRIMARY KEY (`maCa`),
    CONSTRAINT `fk_CaLam_NhanVien` FOREIGN KEY (`maNhanVien`) REFERENCES `NhanVien`(`maNhanVien`)
);

-- 8. Bảng DonViQuyDoi
CREATE TABLE `DonViQuyDoi` (
    `maDonVi` VARCHAR(50) NOT NULL,
    `tenDonVi` ENUM('VIEN', 'VI', 'HOP', 'TUYP', 'CHAI') NOT NULL,
    `heSoQuyDoi` INT NOT NULL,
    `maSanPham` VARCHAR(50) NOT NULL,
    PRIMARY KEY (`maDonVi`),
    CONSTRAINT `fk_DonViQuyDoi_SanPham` FOREIGN KEY (`maSanPham`) REFERENCES `SanPham`(`maSanPham`) ON DELETE CASCADE
);

-- 9. Bảng Lo
CREATE TABLE `Lo` (
    `maLo` VARCHAR(50) NOT NULL,
    `soLo` VARCHAR(50) NOT NULL,
    `ngayHetHan` DATE NOT NULL,
    `soLuongSanPham` INT NOT NULL DEFAULT 0,
    `maSanPham` VARCHAR(50) NOT NULL,
    `giaNhap` DOUBLE NOT NULL,
    PRIMARY KEY (`maLo`),
    CONSTRAINT `fk_Lo_SanPham` FOREIGN KEY (`maSanPham`) REFERENCES `SanPham`(`maSanPham`) ON DELETE CASCADE
);

-- 10. Bảng QuaTang
CREATE TABLE `QuaTang` (
    `maKhuyenMai` VARCHAR(50) NOT NULL,
    `maSanPham` VARCHAR(50) NOT NULL,
    `soLuongTang` INT NOT NULL DEFAULT 1,
    PRIMARY KEY (`maKhuyenMai`, `maSanPham`),
    CONSTRAINT `fk_QuaTang_KhuyenMai` FOREIGN KEY (`maKhuyenMai`) REFERENCES `KhuyenMai`(`maKhuyenMai`) ON DELETE CASCADE,
    CONSTRAINT `fk_QuaTang_SanPham` FOREIGN KEY (`maSanPham`) REFERENCES `SanPham`(`maSanPham`) ON DELETE CASCADE
);

-- 11. Bảng HoaDon
CREATE TABLE `HoaDon` (
    `maHoaDon` VARCHAR(50) NOT NULL,
    `thoiGianTao` DATETIME NOT NULL,
    `maNhanVien` VARCHAR(50) NOT NULL,
    `trangThaiThanhToan` BOOLEAN NOT NULL DEFAULT FALSE,
    `maKhachHang` VARCHAR(50),
    `maKhuyenMai` VARCHAR(50),
    `loaiHoaDon` ENUM('BANHANG', 'DOIHANG', 'TRAHANG') NOT NULL,
    `maCa` VARCHAR(50) NOT NULL,
    `ghiChu` TEXT,
    `maHoaDonDoiTra` VARCHAR(50),
    `maDonThuoc` VARCHAR(50),
    PRIMARY KEY (`maHoaDon`),
    CONSTRAINT `fk_HoaDon_NhanVien` FOREIGN KEY (`maNhanVien`) REFERENCES `NhanVien`(`maNhanVien`),
    CONSTRAINT `fk_HoaDon_KhachHang` FOREIGN KEY (`maKhachHang`) REFERENCES `KhachHang`(`maKhachHang`),
    CONSTRAINT `fk_HoaDon_KhuyenMai` FOREIGN KEY (`maKhuyenMai`) REFERENCES `KhuyenMai`(`maKhuyenMai`),
    CONSTRAINT `fk_HoaDon_CaLam` FOREIGN KEY (`maCa`) REFERENCES `CaLam`(`maCa`),
    CONSTRAINT `fk_HoaDon_HoaDonDoiTra` FOREIGN KEY (`maHoaDonDoiTra`) REFERENCES `HoaDon`(`maHoaDon`),
    CONSTRAINT `fk_HoaDon_DonThuoc` FOREIGN KEY (`maDonThuoc`) REFERENCES `DonThuoc`(`maDonThuoc`)
);

-- 12. Bảng ChiTietHoaDon 
CREATE TABLE `ChiTietHoaDon` (
    `maHoaDon` VARCHAR(50) NOT NULL,
    `maDonVi` VARCHAR(50) NOT NULL,
    `soLuong` INT NOT NULL,
    `donGia` DOUBLE NOT NULL,
    `laQuaTangKem` BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`maHoaDon`, `maDonVi`),
    CONSTRAINT `fk_CTHD_HoaDon` FOREIGN KEY (`maHoaDon`) REFERENCES `HoaDon`(`maHoaDon`) ON DELETE CASCADE,
    CONSTRAINT `fk_CTHD_DonVi` FOREIGN KEY (`maDonVi`) REFERENCES `DonViQuyDoi`(`maDonVi`)
);

-- 13. Bảng SuPhanBoLo 
CREATE TABLE `SuPhanBoLo` (
    `maHoaDon` VARCHAR(50) NOT NULL,
    `maDonVi` VARCHAR(50) NOT NULL,
    `maLo` VARCHAR(50) NOT NULL,
    `soLuong` INT NOT NULL,
    PRIMARY KEY (`maHoaDon`, `maDonVi`, `maLo`),
    CONSTRAINT `fk_SPBL_ChiTietHoaDon` FOREIGN KEY (`maHoaDon`, `maDonVi`) REFERENCES `ChiTietHoaDon`(`maHoaDon`, `maDonVi`) ON DELETE CASCADE,
    CONSTRAINT `fk_SPBL_Lo` FOREIGN KEY (`maLo`) REFERENCES `Lo`(`maLo`) ON DELETE CASCADE
);