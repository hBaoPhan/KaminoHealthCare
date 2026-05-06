USE master;
GO

-- 1. XÓA DATABASE CŨ (NẾU ĐANG TỒN TẠI)
IF EXISTS (SELECT name FROM sys.databases WHERE name = N'QUANLYKAMINOHEALTHCARE')
BEGIN
    ALTER DATABASE [QUANLYKAMINOHEALTHCARE] SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE [QUANLYKAMINOHEALTHCARE];
END
GO

-- 2. TẠO MỚI DATABASE
CREATE DATABASE [QUANLYKAMINOHEALTHCARE];
GO

USE [QUANLYKAMINOHEALTHCARE];
GO

--- =================================================== ---
--- 3. KHỞI TẠO CÁC BẢNG THỰC THỂ (DDL)
--- =================================================== ---

CREATE TABLE NhanVien (
    maNhanVien VARCHAR(10) PRIMARY KEY,
    tenNhanVien NVARCHAR(100) NOT NULL,
    cccd VARCHAR(12) UNIQUE,
    sdt VARCHAR(10),
    chucVu NVARCHAR(50) NOT NULL CHECK (chucVu IN (N'DUOC_SI', N'NHAN_VIEN_QUAN_LY')),
    trangThaiHoatDong BIT 
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
GO

--- =================================================== ---
--- 4. CHÈN DỮ LIỆU MẪU (ÁP DỤNG QUY TẮC MÃ ĐỊNH DANH MỚI)
--- =================================================== ---

-- 4.1. NHÂN VIÊN & TÀI KHOẢN (Mã: Chức vụ + 3 số)
INSERT INTO NhanVien (maNhanVien, tenNhanVien, cccd, sdt, chucVu, trangThaiHoatDong) VALUES
('QL001', N'Phan Hoài Bảo', '079200000001', '0901234567', N'NHAN_VIEN_QUAN_LY', 1),
('DS001', N'Nguyễn Văn An', '079200000002', '0901234568', N'DUOC_SI', 1),
('DS002', N'Trần Thị Bích', '079200000003', '0901234569', N'DUOC_SI', 1),
('DS003', N'Lê Văn Cường', '079200000004', '0901234570', N'DUOC_SI', 1),
('DS004', N'Phạm Thị Dung', '079200000005', '0901234571', N'DUOC_SI', 0);

INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNhanVien) VALUES
('admin_QL001', '123456', 'QL001'),
('duocsi_DS001', '123456', 'DS001'),
('duocsi_DS002', '123456', 'DS002'),
('duocsi_DS003', '123456', 'DS003'),
('duocsi_DS004', '123456', 'DS004');

-- 4.2. KHÁCH HÀNG (Mã: TV/KL + 6 số)
INSERT INTO KhachHang (maKhachHang, tenKhachHang, sdt, trangThaiKhachHang) VALUES
('KL000001', N'Khách vãng lai 1', '0911111111', N'KHACH_LE'),
('TV000001', N'Ngô Thị Em', '0922222222', N'KHACH_HANG_THANH_VIEN'),
('TV000002', N'Vũ Văn Phong', '0933333333', N'KHACH_HANG_THANH_VIEN'),
('TV000003', N'Đặng Thị Giang', '0944444444', N'KHACH_HANG_THANH_VIEN'),
('TV000004', N'Bùi Văn Hùng', '0955555555', N'KHACH_HANG_THANH_VIEN');

-- 4.3. KHUYẾN MÃI (Mã: KM + Ngày Tháng + Số)
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, thoiGianBatDau, thoiGianKetThuc, loaiKhuyenMai, khuyenMaiPhanTram, giaTriDonHangToiThieu) VALUES
('KM010401', N'Giảm 10% đơn từ 500k', '2026-04-01', '2026-05-01', N'PHAN_TRAM', 10, 500000),
('KM150401', N'Mua 1 tặng 1 Vitamin C', '2026-04-15', '2026-04-30', N'TANG_KEM', 0, 0),
('KM250401', N'Giảm giá 5% Lễ 30/4', '2026-04-25', '2026-05-05', N'PHAN_TRAM', 5, 0),
('KM010402', N'Tặng khẩu trang đơn 100k', '2026-04-01', '2026-12-31', N'TANG_KEM', 0, 100000),
('KM011001', N'Sale sinh nhật 20%', '2026-10-01', '2026-10-31', N'PHAN_TRAM', 20, 1000000);

-- 4.4. ĐƠN THUỐC (Mã: DT + DDMMYY + 3 số)
INSERT INTO DonThuoc (maDonThuoc, tenBacSi, coSoKhamBenh, ngayKeDon) VALUES
('DT200426001', N'BS. Nguyễn Văn A', N'Bệnh viện Chợ Rẫy', '2026-04-20'),
('DT210426001', N'BS. Trần Thị B', N'Bệnh viện 115', '2026-04-21'),
('DT220426001', N'BS. Lê Văn C', N'Phòng khám đa khoa', '2026-04-22'),
('DT230426001', N'BS. Phạm Thị D', N'Bệnh viện Đại học Y Dược', '2026-04-23'),
('DT240426001', N'BS. Ngô Văn E', N'Bệnh viện Nhi Đồng', '2026-04-24'),
('DT300426001', N'BS. Hoàng Trọng E', N'Bệnh viện Nhân dân Gia Định', '2026-04-30');

-- 4.5. CA LÀM (Mã: CA + DDMMYY + 2 số)
INSERT INTO CaLam (maCa, maNhanVien, gioBatDau, gioKetThuc, trangThaiCaLam, tienMoCa, tienKetCa, tienHeThong, ghiChu) VALUES
('CA20042601', 'QL001', '2026-04-20 08:00:00', '2026-04-20 16:00:00', N'DONG', 1000000, 5000000, 4000000, N'Ca bình thường'),
('CA20042602', 'DS001', '2026-04-20 16:00:00', '2026-04-20 22:00:00', N'DONG', 5000000, 8000000, 3000000, N'Ca bình thường'),
('CA21042601', 'DS002', '2026-04-21 08:00:00', '2026-04-21 16:00:00', N'DONG', 1000000, 6000000, 5000000, N'Ca bình thường'),
('CA21042602', 'DS003', '2026-04-21 16:00:00', '2026-04-21 22:00:00', N'DONG', 6000000, 9000000, 3000000, N'Ca bình thường'),
('CA22042601', 'DS001', '2026-04-22 08:00:00', '2026-04-22 08:00:00', N'DONG', 1000000, 1000000, 0, N'Ca bình thường'),
('CA26042601', 'DS002', '2026-04-26 08:00:00', '2026-04-26 16:00:00', N'DONG', 1000000, 5000000, 4000000, N'Ca bình thường'),
('CA30042601', 'DS002', '2026-04-30 08:00:00', '2026-04-30 16:00:00', N'DONG', 1000000, 6000000, 5000000, N'Ca bình thường'),
('CA02052601', 'DS001', '2026-05-02 08:00:00', '2026-05-02 16:00:00', N'DANG_MO', 1000000, 5000000, 4000000, N'Đang trực');



-- 4.6. SẢN PHẨM & ĐƠN VỊ QUY ĐỔI
INSERT INTO SanPham (maSanPham, tenSanPham, loaiSanPham, soLuongTon, moTa, hoatChat, donGiaCoBan, trangThaiKinhDoanh, thue) VALUES
('OTC-BIA-001', 'Biafine Janssen', 'OTC', 120, 'Kem trị bỏng, vết thương và bỏng nắng', 'Biafine', 95000, 1, 0.05),
('OTC-CON-002', 'Contractubex', 'OTC', 90, 'Kem trị sẹo lồi, sẹo lõm', 'Contractubex', 145000, 1, 0.05),
('OTC-MIN-003', 'Minox 20mg Edol', 'OTC', 80, 'Dung dịch trị rụng tóc Minoxidil', 'Minoxidil 20mg', 185000, 1, 0.05),
('OTC-NIZ-004', 'Nizoral Shampoo Janssen', 'OTC', 95, 'Dầu gội trị nấm da đầu', 'Ketoconazole', 125000, 1, 0.05),
('OTC-REM-005', 'Remowart Farmalabor', 'OTC', 75, 'Thuốc bôi trị mụn cóc, u mềm', 'Acid Salicylic', 98000, 1, 0.05),
('OTC-ALL-006', 'Allerphast 180mg', 'OTC', 180, 'Thuốc chống dị ứng thế hệ mới', 'Fexofenadine', 85000, 1, 0.05),
('OTC-CET-007', 'Cetirizin 10mg', 'OTC', 420, 'Thuốc chống dị ứng', 'Cetirizine 10mg', 55000, 1, 0.05),
('OTC-CLO-008', 'Clorpheniramin 4mg', 'OTC', 300, 'Thuốc kháng histamine trị dị ứng', 'Chlorpheniramine', 45000, 1, 0.05),
('OTC-EXO-009', 'Exopadin 60mg', 'OTC', 150, 'Thuốc chống dị ứng', 'Fexofenadine 60mg', 92000, 1, 0.05),
('OTC-HIS-010', 'Histalong L 5mg', 'OTC', 200, 'Thuốc chống dị ứng', 'Loratadine 5mg', 68000, 1, 0.05),
('OTC-ATS-011', 'Atsirox 1 An Thiên', 'OTC', 110, 'Thuốc kháng sinh trị nhiễm khuẩn', 'Amoxicillin', 75000, 1, 0.05),
('OTC-FUG-012', 'Fugacar Janssen', 'OTC', 130, 'Thuốc tẩy giun Fugacar', 'Mebendazole', 62000, 1, 0.05),
('OTC-CIC-013', 'Shampoo Ciclopirox VCP', 'OTC', 85, 'Dầu gội trị nấm da đầu', 'Ciclopirox', 115000, 1, 0.05),
('OTC-TRI-014', 'Thuốc trị vảy nến da đầu', 'OTC', 70, 'Thuốc trị vảy nến da đầu', 'Coal Tar + Salicylic Acid', 98000, 1, 0.05),
('OTC-TIM-015', 'Timbov Farmaprim', 'OTC', 95, 'Thuốc kháng sinh', 'Amoxicillin/Clavulanic acid', 135000, 1, 0.05),
('OTC-CAL-016', 'Calcid Soft', 'OTC', 240, 'Canxi mềm bổ sung xương', 'Calcium', 148000, 1, 0.05),
('OTC-COR-017', 'Calcium Corbiere Kids', 'OTC', 180, 'Canxi cho trẻ em Extra Sanofi', 'Calcium', 165000, 1, 0.05),
('OTC-CST-018', 'Calcium Stada 500mg', 'OTC', 220, 'Canxi Stada bổ sung xương', 'Calcium Carbonate 500mg', 92000, 1, 0.05),
('OTC-KID-019', 'KiddieCal Catalent', 'OTC', 190, 'Canxi cho trẻ em', 'Calcium', 135000, 1, 0.05),
('OTC-XKH-020', 'Xương Khớp Trương Phúc', 'OTC', 160, 'Hỗ trợ xương khớp', 'Glucosamine & Chondroitin', 210000, 1, 0.05),
('ETC-BET-019', 'Betamethason 0.064% Medipharco', 'ETC', 65, 'Corticoid bôi da điều trị viêm da', 'Betamethason 0.064%', 85000, 1, 0.1),
('ETC-DEX-020', 'Dexamethasone', 'ETC', 70, 'Thuốc chống viêm, dị ứng mạnh', 'Dexamethasone', 65000, 1, 0.1),
('ETC-MAX-021', 'Maxx Acne AC Ampharco', 'ETC', 55, 'Thuốc trị mụn nặng isotretinoin', 'Isotretinoin', 320000, 0, 0.1),
('ETC-SRI-022', 'Srinron 10g Mipharco', 'ETC', 80, 'Kem trị viêm da, eczema', 'Betamethason + Gentamicin', 95000, 1, 0.1),
('ETC-TOM-023', 'Tomax Genta Detapharm', 'ETC', 45, 'Kem kháng sinh da', 'Gentamicin', 78000, 1, 0.1),
('ETC-BIL-024', 'Bilaxiten 20mg Menarini', 'ETC', 90, 'Thuốc chống dị ứng', 'Bilastine 20mg', 115000, 1, 0.1),
('ETC-LOD-025', 'Lodax 20mg', 'ETC', 85, 'Thuốc chống dị ứng', 'Loratadine 20mg', 92000, 1, 0.1),
('ETC-RUP-026', 'Rupafin 10mg', 'ETC', 110, 'Thuốc chống dị ứng', 'Rupatadine 10mg', 105000, 1, 0.1),
('ETC-RUT-027', 'Rutadin 10mg', 'ETC', 100, 'Thuốc chống dị ứng', 'Rupatadine', 98000, 1, 0.1),
('ETC-ZYR-028', 'Zyrtec GSK', 'ETC', 120, 'Thuốc chống dị ứng', 'Cetirizine', 88000, 1, 0.1),
('ETC-CLE-029', 'Cledomox 625 Tenamyd', 'ETC', 75, 'Kháng sinh Amoxicillin/Clavulanic acid', 'Amoxicillin 625mg', 135000, 1, 0.1),
('ETC-FLU-030', 'Flucona-Denk 150mg', 'ETC', 60, 'Thuốc trị nấm', 'Fluconazole 150mg', 125000, 1, 0.1),
('ETC-IME-031', 'Imexime 100 Imexpharm', 'ETC', 80, 'Kháng sinh Cefixime', 'Cefixime 100mg', 95000, 1, 0.1),
('ETC-IME-032', 'Imexime 200 Imexpharm', 'ETC', 70, 'Kháng sinh Cefixime', 'Cefixime 200mg', 145000, 1, 0.1),
('ETC-LEC-033', 'Lecifex 500mg Abbott', 'ETC', 65, 'Kháng sinh Levofloxacin', 'Levofloxacin 500mg', 168000, 1, 0.1),
('ETC-FEB-034', 'Febuxotid VK 40mg', 'ETC', 85, 'Điều trị gout', 'Febuxostat 40mg', 195000, 1, 0.1),
('ETC-FEB-035', 'Febuxotid VK 80mg', 'ETC', 75, 'Điều trị gout', 'Febuxostat 80mg', 245000, 1, 0.1),
('ETC-MET-036', 'Methocarbamol 500mg Khapharco', 'ETC', 90, 'Thuốc giãn cơ', 'Methocarbamol 500mg', 115000, 1, 0.1),
('ETC-OPE-037', 'Opedulox 40 OPV', 'ETC', 80, 'Thuốc giảm đau thần kinh', 'Duloxetine 40mg', 178000, 1, 0.1),
('ETC-OPE-038', 'Opedulox 80 OPV', 'ETC', 70, 'Thuốc giảm đau thần kinh', 'Duloxetine 80mg', 225000, 1, 0.1),
('TPCN-BPK-039', 'Bio Plus Kenko', 'TPCN', 180, 'Bổ sung vitamin và khoáng chất tổng hợp', 'Multi Vitamin & Minerals', 195000, 1, 0.1),
('TPCN-BNI-040', 'Bổ Não Ích Trí Gold', 'TPCN', 140, 'Bổ não, tăng cường trí nhớ', 'Ginkgo Biloba & Thảo dược', 245000, 1, 0.1),
('TPCN-BPL-041', 'Bổ Phế Labebe', 'TPCN', 160, 'Bổ phế, hỗ trợ hô hấp', 'Thảo dược', 168000, 1, 0.1),
('TPCN-CDK-042', 'Canxi D3 K2 Kingphar', 'TPCN', 220, 'Canxi kết hợp Vitamin D3 và K2', 'Calcium, Vitamin D3, K2', 175000, 1, 0.1),
('TPCN-D3D-043', 'D3 Drops DAO Nordic Health', 'TPCN', 190, 'Vitamin D3 dạng giọt', 'Vitamin D3', 135000, 1, 0.1),
('TPCN-HPM-044', 'High Potency MK-7 Drops', 'TPCN', 130, 'Vitamin K2 MK-7 cao cấp', 'Vitamin K2 MK-7', 225000, 1, 0.1),
('TPCN-HHT-045', 'Hoạt Huyết Thống Mạch Gold TW3', 'TPCN', 150, 'Hoạt huyết thông mạch', 'Thảo dược', 265000, 1, 0.1),
('TPCN-IMU-046', 'Immuvita Easylife', 'TPCN', 170, 'Tăng cường sức đề kháng', 'Vitamin & Immuno Support', 189000, 1, 0.1),
('TPCN-KAN-047', 'Kanzo Gold', 'TPCN', 145, 'Bổ gan Kanzo', 'Thảo dược', 198000, 1, 0.1),
('TPCN-KID-048', 'KID GROW Kenko', 'TPCN', 200, 'Vitamin tăng trưởng cho trẻ em', 'Multi Vitamin', 172000, 1, 0.1),
('TPCN-LBG-049', 'Lacto Biomin Gold', 'TPCN', 160, 'Men vi sinh Biomin', 'Probiotics', 168000, 1, 0.1),
('TPCN-LAC-050', 'Lactobact Intima 30V', 'TPCN', 95, 'Men vi sinh phụ khoa', 'Lactobacillus', 245000, 1, 0.1),
('TPCN-NUT-051', 'NutriGrow Nutrimed', 'TPCN', 175, 'Bổ sung dinh dưỡng trẻ em', 'Multi Vitamin', 165000, 1, 0.1),
('TPCN-OMP-052', 'Omega 3 Plus Kenko', 'TPCN', 210, 'Omega 3 cao cấp', 'Omega-3 Fish Oil', 235000, 1, 0.1),
('TPCN-OPD-053', 'Omega 3 Power DAO Nordic Health', 'TPCN', 155, 'Omega 3 Nordic Health', 'Omega-3', 258000, 1, 0.1),
('TPCN-OMX-054', 'Omexxel 3-6-9 Premium', 'TPCN', 180, 'Omega 3-6-9 Premium', 'Omega 3,6,9', 275000, 1, 0.1),
('TPCN-OMG-055', 'Omexxel Ginkgo 120 Premium', 'TPCN', 140, 'Ginkgo Biloba 120mg', 'Ginkgo Biloba', 245000, 1, 0.1),
('TPCN-PIK-056', 'Pikolin Ocavill', 'TPCN', 165, 'Sắt Pikolin dễ hấp thu', 'Iron Pikolin', 198000, 1, 0.1),
('TPCN-POB-057', 'Premium Omexxel Bone Health', 'TPCN', 135, 'Hỗ trợ xương khớp cao cấp', 'Calcium + Vitamin D & K2', 289000, 1, 0.1),
('TPCN-SKI-058', 'SkillMax Ocavill', 'TPCN', 130, 'Hỗ trợ trí não', 'Ginkgo & Phosphatidylserine', 215000, 1, 0.1);

INSERT INTO DonViQuyDoi (maDonVi, tenDonVi, heSoQuyDoi, maSanPham) VALUES
('DV001', 'TUYP', 1, 'OTC-BIA-001'),
('DV002', 'TUYP', 1, 'OTC-CON-002'),
('DV003', 'CHAI', 1, 'OTC-MIN-003'),
('DV004', 'CHAI', 1, 'OTC-NIZ-004'),
('DV005', 'TUYP', 1, 'OTC-REM-005'),
('DV006', 'CHAI', 1, 'OTC-CIC-013'),
('DV007', 'TUYP', 1, 'ETC-BET-019'),
('DV008', 'TUYP', 1, 'ETC-SRI-022'),
('DV009', 'TUYP', 1, 'ETC-TOM-023'),
('DV010', 'CHAI', 1, 'TPCN-D3D-043'),
('DV011', 'CHAI', 1, 'TPCN-HPM-044'),
('DV012', 'CHAI', 1, 'TPCN-LBG-049'),
('DV013', 'CHAI', 1, 'TPCN-KAN-047'),
('DV014', 'CHAI', 1, 'OTC-COR-017'),
('DV015', 'VIEN', 1, 'OTC-ALL-006'), ('DV016', 'VI', 10, 'OTC-ALL-006'), ('DV017', 'HOP', 100, 'OTC-ALL-006'),
('DV018', 'VIEN', 1, 'OTC-CET-007'), ('DV019', 'VI', 10, 'OTC-CET-007'), ('DV020', 'HOP', 100, 'OTC-CET-007'),
('DV021', 'VIEN', 1, 'OTC-CLO-008'), ('DV022', 'VI', 10, 'OTC-CLO-008'), ('DV023', 'HOP', 100, 'OTC-CLO-008'),
('DV024', 'VIEN', 1, 'OTC-EXO-009'), ('DV025', 'VI', 10, 'OTC-EXO-009'), ('DV026', 'HOP', 100, 'OTC-EXO-009'),
('DV027', 'VIEN', 1, 'OTC-HIS-010'), ('DV028', 'VI', 10, 'OTC-HIS-010'), ('DV029', 'HOP', 100, 'OTC-HIS-010'),
('DV030', 'VIEN', 1, 'OTC-ATS-011'), ('DV031', 'VI', 10, 'OTC-ATS-011'), ('DV032', 'HOP', 100, 'OTC-ATS-011'),
('DV033', 'VIEN', 1, 'OTC-FUG-012'), ('DV034', 'VI', 1, 'OTC-FUG-012'), ('DV035', 'HOP', 1, 'OTC-FUG-012'),
('DV036', 'VIEN', 1, 'ETC-DEX-020'), ('DV037', 'VI', 10, 'ETC-DEX-020'), ('DV038', 'HOP', 100, 'ETC-DEX-020'),
('DV039', 'VIEN', 1, 'ETC-BIL-024'), ('DV040', 'VI', 10, 'ETC-BIL-024'), ('DV041', 'HOP', 30, 'ETC-BIL-024'),
('DV042', 'VIEN', 1, 'ETC-LOD-025'), ('DV043', 'VI', 10, 'ETC-LOD-025'), ('DV044', 'HOP', 100, 'ETC-LOD-025'),
('DV045', 'VIEN', 1, 'ETC-RUP-026'), ('DV046', 'VI', 10, 'ETC-RUP-026'), ('DV047', 'HOP', 30, 'ETC-RUP-026'),
('DV048', 'VIEN', 1, 'ETC-CLE-029'), ('DV049', 'VI', 7, 'ETC-CLE-029'), ('DV050', 'HOP', 14, 'ETC-CLE-029'),
('DV051', 'VIEN', 1, 'ETC-FLU-030'), ('DV052', 'VI', 1, 'ETC-FLU-030'), ('DV053', 'HOP', 1, 'ETC-FLU-030'),
('DV054', 'VIEN', 1, 'ETC-IME-031'), ('DV055', 'VI', 10, 'ETC-IME-031'), ('DV056', 'HOP', 20, 'ETC-IME-031'),
('DV057', 'VIEN', 1, 'ETC-FEB-034'), ('DV058', 'VI', 10, 'ETC-FEB-034'), ('DV059', 'HOP', 30, 'ETC-FEB-034'),
('DV060', 'VIEN', 1, 'ETC-MET-036'), ('DV061', 'VI', 10, 'ETC-MET-036'), ('DV062', 'HOP', 50, 'ETC-MET-036'),
('DV063', 'VIEN', 1, 'TPCN-BPK-039'), ('DV064', 'VI', 10, 'TPCN-BPK-039'), ('DV065', 'HOP', 60, 'TPCN-BPK-039'),
('DV066', 'VIEN', 1, 'TPCN-BNI-040'), ('DV067', 'VI', 10, 'TPCN-BNI-040'), ('DV068', 'HOP', 30, 'TPCN-BNI-040'),
('DV069', 'VIEN', 1, 'TPCN-CDK-042'), ('DV070', 'VI', 10, 'TPCN-CDK-042'), ('DV071', 'HOP', 30, 'TPCN-CDK-042'),
('DV072', 'VIEN', 1, 'TPCN-OMP-052'), ('DV073', 'VI', 15, 'TPCN-OMP-052'), ('DV074', 'HOP', 60, 'TPCN-OMP-052'),
('DV075', 'VIEN', 1, 'TPCN-OMG-055'), ('DV076', 'VI', 10, 'TPCN-OMG-055'), ('DV077', 'HOP', 30, 'TPCN-OMG-055'),
('DV078', 'VIEN', 1, 'TPCN-POB-057'), ('DV079', 'VI', 10, 'TPCN-POB-057'), ('DV080', 'HOP', 30, 'TPCN-POB-057'),
('DV081', 'VIEN', 1, 'OTC-CAL-016'), ('DV082', 'VI', 10, 'OTC-CAL-016'), ('DV083', 'HOP', 100, 'OTC-CAL-016'),
('DV084', 'VIEN', 1, 'OTC-CST-018'), ('DV085', 'VI', 10, 'OTC-CST-018'), ('DV086', 'HOP', 100, 'OTC-CST-018'),
('DV087', 'VIEN', 1, 'OTC-XKH-020'), ('DV088', 'VI', 10, 'OTC-XKH-020'), ('DV089', 'HOP', 60, 'OTC-XKH-020'),
('DV090', 'VIEN', 1, 'TPCN-IMU-046'), ('DV091', 'VI', 10, 'TPCN-IMU-046'), ('DV092', 'HOP', 30, 'TPCN-IMU-046'),
('DV093', 'VIEN', 1, 'TPCN-LAC-050'), ('DV094', 'VI', 10, 'TPCN-LAC-050'), ('DV095', 'HOP', 30, 'TPCN-LAC-050'),
('DV096', 'VIEN', 1, 'TPCN-PIK-056'), ('DV097', 'VI', 10, 'TPCN-PIK-056'), ('DV098', 'HOP', 30, 'TPCN-PIK-056'),
('DV099', 'VIEN', 1, 'TPCN-SKI-058'), ('DV100', 'VI', 10, 'TPCN-SKI-058'), ('DV101', 'HOP', 30, 'TPCN-SKI-058');

-- 4.7. LÔ HÀNG (Mã: LO + DDMMYY + 3 số)
INSERT INTO Lo (maLo, soLo, ngayHetHan, soLuongSanPham, maSanPham, giaNhap) VALUES
('LO010126001', 'L001', '2028-01-01', 500, 'OTC-BIA-001', 10000),
('LO010226001', 'L002', '2027-06-01', 500, 'OTC-BIA-001', 11000),
('LO150326001', 'L003', '2027-12-31', 500, 'OTC-CON-002', 35000),
('LO200326001', 'L004', '2026-10-01', 2000, 'OTC-ALL-006', 15000),
('LO250326001', 'L005', '2029-01-01', 3000, 'ETC-DEX-020', 3000);

-- 4.8. QUÀ TẶNG (Khớp mã Khuyến Mãi và Đơn Vị mới)
INSERT INTO QuaTang (maKhuyenMai, maDonVi, soLuongTang) VALUES
('KM150401', 'DV015', 1),
('KM010402', 'DV017', 1),
('KM150401', 'DV001', 2),
('KM010402', 'DV016', 1),
('KM011001', 'DV002', 5);

-- 4.9. HÓA ĐƠN & CHI TIẾT (Mã: HD + B/D/T + DDMMYY + 3 số)
INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc, phuongThucThanhToan) VALUES
('HDB200426001', '2026-04-20 10:00:00', 'QL001', 1, 'TV000001', 'KM010401', N'BAN_HANG', 'CA20042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB200426002', '2026-04-20 14:00:00', 'QL001', 1, 'KL000001', NULL, N'BAN_HANG', 'CA20042601', N'', NULL, 'DT200426001', N'CHUYEN_KHOAN'),
('HDB210426001', '2026-04-21 09:00:00', 'DS002', 1, 'TV000002', 'KM150401', N'BAN_HANG', 'CA21042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB210426002', '2026-04-21 11:00:00', 'DS002', 1, 'TV000003', NULL, N'BAN_HANG', 'CA21042601', N'', NULL, 'DT210426001', N'CHUYEN_KHOAN'),
('HDB220426001', '2026-04-22 08:30:00', 'DS001', 1, 'TV000004', 'KM010402', N'BAN_HANG', 'CA22042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB260426001', '2026-04-26 09:00:00', 'DS002', 1, 'TV000002', 'KM150401', N'BAN_HANG', 'CA26042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB300426001', '2026-04-30 11:00:00', 'DS002', 1, 'TV000003', NULL, N'BAN_HANG', 'CA30042601', N'', NULL, 'DT300426001', N'CHUYEN_KHOAN'),
('HDB020526001', '2026-05-02 08:30:00', 'DS001', 1, 'TV000004', 'KM010402', N'BAN_HANG', 'CA02052601', N'', NULL, NULL, N'TIEN_MAT');

INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuong, donGia, laQuaTangKem) VALUES
('HDB200426001', 'DV001', 2, 15000, 0),
('HDB200426001', 'DV015', 1, 50000, 0),
('HDB200426002', 'DV016', 10, 20000, 0),
('HDB210426001', 'DV015', 2, 50000, 0),
('HDB210426002', 'DV017', 5, 5000, 0),
('HDB260426001', 'DV016', 10, 20000, 0),
('HDB300426001', 'DV015', 2, 50000, 0),
('HDB020526001', 'DV017', 5, 5000, 0);

INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong) VALUES
('HDB200426001', 'DV001', 'LO010126001', 2),
('HDB200426001', 'DV015', 'LO200326001', 1),
('HDB200426002', 'DV016', 'LO200326001', 10),
('HDB210426001', 'DV015', 'LO200326001', 2),
('HDB210426002', 'DV017', 'LO200326001', 5),
('HDB260426001', 'DV016', 'LO200326001', 10),
('HDB300426001', 'DV015', 'LO200326001', 2),
('HDB020526001', 'DV017', 'LO200326001', 5);
GO