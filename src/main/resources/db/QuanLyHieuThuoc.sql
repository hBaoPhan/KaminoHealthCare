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
    trangThaiCaLam NVARCHAR(20) CHECK (trangThaiCaLam IN (N'DANG_MO', N'DONG', N'CHUA_MO')),
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
    PRIMARY KEY (maHoaDon, maDonVi, laQuaTangKem)
);

CREATE TABLE SuPhanBoLo (
    maHoaDon VARCHAR(20),
    maDonVi VARCHAR(20),
    maLo VARCHAR(20) FOREIGN KEY REFERENCES Lo(maLo),
    soLuong INT,
    laQuaTangKem BIT DEFAULT 0,
    CONSTRAINT FK_SuPhanBoLo_ChiTiet 
        FOREIGN KEY (maHoaDon, maDonVi, laQuaTangKem) 
        REFERENCES ChiTietHoaDon(maHoaDon, maDonVi, laQuaTangKem),
    PRIMARY KEY (maHoaDon, maDonVi, maLo, laQuaTangKem)
);
GO

--- =================================================== ---
--- 4. CHÈN DỮ LIỆU MẪU (ÁP DỤNG QUY TẮC MÃ ĐỊNH DANH MỚI)
--- =================================================== ---

-- 4.1. NHÂN VIÊN & TÀI KHOẢN
INSERT INTO NhanVien (maNhanVien, tenNhanVien, cccd, sdt, chucVu, trangThaiHoatDong) VALUES
('QL001', N'Phan Hoài Bảo', '079200000001', '0901234567', N'NHAN_VIEN_QUAN_LY', 1),
('QL002', N'Trần Tấn Tài', '079200000002', '0901234568', N'NHAN_VIEN_QUAN_LY', 1),
('QL003', N'Nguyễn Xuân Trường', '079200000003', '0901234569', N'NHAN_VIEN_QUAN_LY', 1),
('QL004', N'Nguyễn Minh Nhật', '079200000004', '0901234570', N'NHAN_VIEN_QUAN_LY', 1),
('DS001', N'Phạm Thị Dung', '079200000005', '0901234571', N'DUOC_SI', 0);

INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNhanVien) VALUES
('baoph', '$2a$12$bfiVg8.pufHx/TEcJYISSeteaaAWStGxzGbIzNkwdgY.2HFhp79Ym', 'QL001'),
('taitr', '$2a$12$bfiVg8.pufHx/TEcJYISSeteaaAWStGxzGbIzNkwdgY.2HFhp79Y', 'QL002'),
('truongng', '$2a$12$bfiVg8.pufHx/TEcJYISSeteaaAWStGxzGbIzNkwdgY.2HFhp79Y', 'QL003'),
('nhatng', '$2a$12$bfiVg8.pufHx/TEcJYISSeteaaAWStGxzGbIzNkwdgY.2HFhp79Y', 'QL004'),
('nv', '$2a$12$bfiVg8.pufHx/TEcJYISSeteaaAWStGxzGbIzNkwdgY.2HFhp79Y', 'DS001');

-- 4.2. KHÁCH HÀNG
INSERT INTO KhachHang (maKhachHang, tenKhachHang, sdt, trangThaiKhachHang) VALUES
('KH_LE', N'Khách vãng lai', '0911111111', N'KHACH_LE'),
('TV000001', N'Ngô Thị Em', '0922222222', N'KHACH_HANG_THANH_VIEN'),
('TV000002', N'Vũ Văn Phong', '0933333333', N'KHACH_HANG_THANH_VIEN'),
('TV000003', N'Đặng Thị Giang', '0944444444', N'KHACH_HANG_THANH_VIEN'),
('TV000004', N'Bùi Văn Hùng', '0955555555', N'KHACH_HANG_THANH_VIEN');

-- 4.3. KHUYẾN MÃI
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, thoiGianBatDau, thoiGianKetThuc, loaiKhuyenMai, khuyenMaiPhanTram, giaTriDonHangToiThieu) VALUES
('KM010401', N'Giảm 10% đơn từ 500k', '2026-04-01', '2026-05-01', N'PHAN_TRAM', 10, 500000),
('KM150401', N'Tặng Vitamin C từ đơn 50k', '2026-04-15', '2026-04-30', N'TANG_KEM', 0, 50000),
('KM250401', N'Giảm giá 5% Lễ đơn 200k', '2026-04-25', '2026-05-05', N'PHAN_TRAM', 5, 200000),
('KM010402', N'Tặng Allerphast cho đơn từ 100k', '2026-04-01', '2026-12-31', N'TANG_KEM', 0, 100000),
('KM011001', N'Tặng Contractubex đơn từ 100k', '2026-10-01', '2026-10-31', N'TANG_KEM', 0, 100000);

-- 4.4. ĐƠN THUỐC
INSERT INTO DonThuoc (maDonThuoc, tenBacSi, coSoKhamBenh, ngayKeDon) VALUES
('DT200426001', N'BS. Nguyễn Văn A', N'Bệnh viện Chợ Rẫy', '2026-04-20'),
('DT210426001', N'BS. Trần Thị B', N'Bệnh viện 115', '2026-04-21'),
('DT220426001', N'BS. Lê Văn C', N'Phòng khám đa khoa', '2026-04-22'),
('DT230426001', N'BS. Phạm Thị D', N'Bệnh viện Đại học Y Dược', '2026-04-23'),
('DT240426001', N'BS. Ngô Văn E', N'Bệnh viện Nhi Đồng', '2026-04-24'),
('DT300426001', N'BS. Hoàng Trọng E', N'Bệnh viện Nhân dân Gia Định', '2026-04-30');

-- 4.5. CA LÀM
INSERT INTO CaLam (maCa, maNhanVien, gioBatDau, gioKetThuc, trangThaiCaLam, tienMoCa, tienKetCa, tienHeThong, ghiChu) VALUES
('CA20042601', 'QL001', '2026-04-20 08:00:00', '2026-04-20 16:00:00', N'DONG', 1000000, 5000000, 4000000, N'Ca bình thường'),
('CA20042602', 'DS001', '2026-04-20 16:00:00', '2026-04-20 22:00:00', N'DONG', 5000000, 8000000, 3000000, N'Ca bình thường'),
('CA21042601', 'DS001', '2026-04-21 08:00:00', '2026-04-21 16:00:00', N'DONG', 1000000, 6000000, 5000000, N'Ca bình thường'),
('CA21042602', 'DS001', '2026-04-21 16:00:00', '2026-04-21 22:00:00', N'DONG', 6000000, 9000000, 3000000, N'Ca bình thường'),
('CA22042601', 'DS001', '2026-04-22 08:00:00', '2026-04-22 08:00:00', N'DONG', 1000000, 1000000, 0, N'Ca bình thường'),
('CA26042601', 'DS001', '2026-04-26 08:00:00', '2026-04-26 16:00:00', N'DONG', 1000000, 5000000, 4000000, N'Ca bình thường'),
('CA30042601', 'DS001', '2026-04-30 08:00:00', '2026-04-30 16:00:00', N'DONG', 1000000, 6000000, 5000000, N'Ca bình thường'),
('CA02052601', 'QL001', '2026-05-02 08:00:00', '2026-05-02 16:00:00', N'DONG', 1000000, 5000000, 4000000, N'Đang trực');

-- 4.6. SẢN PHẨM & ĐƠN VỊ QUY ĐỔI (SỬA ĐƠN GIÁ CƠ BẢN LÀ GIÁ 1 VIÊN/1 TUÝP)
INSERT INTO SanPham (maSanPham, tenSanPham, loaiSanPham, soLuongTon, moTa, hoatChat, donGiaCoBan, trangThaiKinhDoanh, thue) VALUES
('OTC-BIA-001', N'Biafine Janssen', N'OTC', 998, N'Kem trị bỏng, vết thương và bỏng nắng', N'Biafine', 95000, 1, 5),
('OTC-CON-002', N'Contractubex', N'OTC', 500, N'Kem trị sẹo lồi, sẹo lõm', N'Contractubex', 145000, 1, 5),
('OTC-MIN-003', N'Minox 20mg Edol', N'OTC', 100, N'Dung dịch trị rụng tóc Minoxidil', N'Minoxidil 20mg', 185000, 1, 5),
('OTC-NIZ-004', N'Nizoral Shampoo Janssen', N'OTC', 100, N'Dầu gội trị nấm da đầu', N'Ketoconazole', 125000, 1, 5),
('OTC-REM-005', N'Remowart Farmalabor', N'OTC', 0, N'Thuốc bôi trị mụn cóc, u mềm', N'Acid Salicylic', 98000, 1, 5),
('OTC-ALL-006', N'Allerphast 180mg', N'OTC', 795, N'Thuốc chống dị ứng thế hệ mới', N'Fexofenadine', 850, 1, 5),
('OTC-CET-007', N'Cetirizin 10mg', N'OTC', 0, N'Thuốc chống dị ứng', N'Cetirizine 10mg', 550, 1, 5),
('OTC-CLO-008', N'Clorpheniramin 4mg', N'OTC', 0, N'Thuốc kháng histamine trị dị ứng', N'Chlorpheniramine', 450, 1, 5),
('OTC-EXO-009', N'Exopadin 60mg', N'OTC', 0, N'Thuốc chống dị ứng', N'Fexofenadine 60mg', 920, 1, 5),
('OTC-HIS-010', N'Histalong L 5mg', N'OTC', 0, N'Thuốc chống dị ứng', N'Loratadine 5mg', 680, 1, 5),
('OTC-ATS-011', N'Atsirox 1 An Thiên', N'OTC', 0, N'Thuốc kháng sinh trị nhiễm khuẩn', N'Amoxicillin', 750, 1, 5),
('OTC-FUG-012', N'Fugacar Janssen', N'OTC', 0, N'Thuốc tẩy giun Fugacar', N'Mebendazole', 62000, 1, 5),
('OTC-CIC-013', N'Shampoo Ciclopirox VCP', N'OTC', 0, N'Dầu gội trị nấm da đầu', N'Ciclopirox', 115000, 1, 5),
('OTC-TRI-014', N'Thuốc trị vảy nến da đầu', N'OTC', 0, N'Thuốc trị vảy nến da đầu', N'Coal Tar + Salicylic Acid', 98000, 1, 5),
('OTC-TIM-015', N'Timbov Farmaprim', N'OTC', 0, N'Thuốc kháng sinh', N'Amoxicillin/Clavulanic acid', 135000, 1, 5),
('OTC-CAL-016', N'Calcid Soft', N'OTC', 1000, N'Canxi mềm bổ sung xương', N'Calcium', 1480, 1, 5),
('OTC-COR-017', N'Calcium Corbiere Kids', N'OTC', 1000, N'Canxi cho trẻ em Extra Sanofi', N'Calcium', 165000, 1, 5),
('OTC-CST-018', N'Calcium Stada 500mg', N'OTC', 1000, N'Canxi Stada bổ sung xương', N'Calcium Carbonate 500mg', 920, 1, 5),
('OTC-KID-019', N'KiddieCal Catalent', N'OTC', 1000, N'Canxi cho trẻ em', N'Calcium', 135000, 1, 5),
('OTC-XKH-020', N'Xương Khớp Trương Phúc', N'OTC', 1000, N'Hỗ trợ xương khớp', N'Glucosamine & Chondroitin', 2100, 1, 5),
('ETC-BET-019', N'Betamethason 0.064% Medipharco', N'ETC', 500, N'Corticoid bôi da điều trị viêm da', N'Betamethason 0.064%', 85000, 1, 10),
('ETC-DEX-020', N'Dexamethasone', N'ETC', 3000, N'Thuốc chống viêm, dị ứng mạnh', N'Dexamethasone', 650, 1, 10),
('ETC-MAX-021', N'Maxx Acne AC Ampharco', N'ETC', 500, N'Thuốc trị mụn nặng isotretinoin', N'Isotretinoin', 320000, 0, 10),
('ETC-SRI-022', N'Srinron 10g Mipharco', N'ETC', 500, N'Kem trị viêm da, eczema', N'Betamethason + Gentamicin', 95000, 1, 10),
('ETC-TOM-023', N'Tomax Genta Detapharm', N'ETC', 500, N'Kem kháng sinh da', N'Gentamicin', 78000, 1, 10),
('ETC-BIL-024', N'Bilaxiten 20mg Menarini', N'ETC', 500, N'Thuốc chống dị ứng', N'Bilastine 20mg', 1150, 1, 10),
('ETC-LOD-025', N'Lodax 20mg', N'ETC', 500, N'Thuốc chống dị ứng', N'Loratadine 20mg', 920, 1, 10),
('ETC-RUP-026', N'Rupafin 10mg', N'ETC', 500, N'Thuốc chống dị ứng', N'Rupatadine 10mg', 1050, 1, 10),
('ETC-RUT-027', N'Rutadin 10mg', N'ETC', 500, N'Thuốc chống dị ứng', N'Rupatadine', 980, 1, 10),
('ETC-ZYR-028', N'Zyrtec GSK', N'ETC', 500, N'Thuốc chống dị ứng', N'Cetirizine', 880, 1, 10),
('ETC-ZYR-029', N'Zyrtec GSK', N'ETC', 500, N'Thuốc chống dị ứng', N'Cetirizine', 880, 1, 10),
('ETC-CLE-029', N'Cledomox 625 Tenamyd', N'ETC', 500, N'Kháng sinh Amoxicillin/Clavulanic acid', N'Amoxicillin 625mg', 1350, 1, 10),
('ETC-FLU-030', N'Flucona-Denk 150mg', N'ETC', 500, N'Thuốc trị nấm', N'Fluconazole 150mg', 125000, 1, 10),
('ETC-IME-031', N'Imexime 100 Imexpharm', N'ETC', 500, N'Kháng sinh Cefixime', N'Cefixime 100mg', 950, 1, 10),
('ETC-IME-032', N'Imexime 200 Imexpharm', N'ETC', 500, N'Kháng sinh Cefixime', N'Cefixime 200mg', 1450, 1, 10),
('ETC-LEC-033', N'Lecifex 500mg Abbott', N'ETC', 500, N'Kháng sinh Levofloxacin', N'Levofloxacin 500mg', 1680, 1, 10),
('ETC-FEB-034', N'Febuxotid VK 40mg', N'ETC', 500, N'Điều trị gout', N'Febuxostat 40mg', 1950, 1, 10),
('ETC-FEB-035', N'Febuxotid VK 80mg', N'ETC', 500, N'Điều trị gout', N'Febuxostat 80mg', 2450, 1, 10),
('ETC-MET-036', N'Methocarbamol 500mg Khapharco', N'ETC', 500, N'Thuốc giãn cơ', N'Methocarbamol 500mg', 1150, 1, 10),
('ETC-OPE-037', N'Opedulox 40 OPV', N'ETC', 500, N'Thuốc giảm đau thần kinh', N'Duloxetine 40mg', 1780, 1, 10),
('ETC-OPE-038', N'Opedulox 80 OPV', N'ETC', 500, N'Thuốc giảm đau thần kinh', N'Duloxetine 80mg', 2250, 1, 10),
('TPCN-BPK-039', N'Bio Plus Kenko', N'TPCN', 500, N'Bổ sung vitamin và khoáng chất tổng hợp', N'Multi Vitamin & Minerals', 1950, 1, 10),
('TPCN-BNI-040', N'Bổ Não Ích Trí Gold', N'TPCN', 500, N'Bổ não, tăng cường trí nhớ', N'Ginkgo Biloba & Thảo dược', 2450, 1, 10),
('TPCN-BPL-041', N'Bổ Phế Labebe', N'TPCN', 500, N'Bổ phế, hỗ trợ hô hấp', N'Thảo dược', 168000, 1, 10),
('TPCN-CDK-042', N'Canxi D3 K2 Kingphar', N'TPCN', 220, N'Canxi kết hợp Vitamin D3 và K2', N'Calcium, Vitamin D3, K2', 1750, 1, 10),
('TPCN-D3D-043', N'D3 Drops DAO Nordic Health', N'TPCN', 500, N'Vitamin D3 dạng giọt', N'Vitamin D3', 135000, 1, 10),
('TPCN-HPM-044', N'High Potency MK-7 Drops', N'TPCN', 500, N'Vitamin K2 MK-7 cao cấp', N'Vitamin K2 MK-7', 225000, 1, 10),
('TPCN-HHT-045', N'Hoạt Huyết Thống Mạch Gold TW3', N'TPCN', 500, N'Hoạt huyết thông mạch', N'Thảo dược', 265000, 1, 10),
('TPCN-IMU-046', N'Immuvita Easylife', N'TPCN', 500, N'Tăng cường sức đề kháng', N'Vitamin & Immuno Support', 1890, 1, 10),
('TPCN-KAN-047', N'Kanzo Gold', N'TPCN', 500, N'Bổ gan Kanzo', N'Thảo dược', 198000, 1, 10),
('TPCN-KID-048', N'KID GROW Kenko', N'TPCN', 500, N'Vitamin tăng trưởng cho trẻ em', N'Multi Vitamin', 172000, 1, 10),
('TPCN-LBG-049', N'Lacto Biomin Gold', N'TPCN', 500, N'Men vi sinh Biomin', N'Probiotics', 168000, 1, 10),
('TPCN-LAC-050', N'Lactobact Intima 30V', N'TPCN', 500, N'Men vi sinh phụ khoa', N'Lactobacillus', 2450, 1, 10),
('TPCN-NUT-051', N'NutriGrow Nutrimed', N'TPCN', 500, N'Bổ sung dinh dưỡng trẻ em', N'Multi Vitamin', 165000, 1, 10),
('TPCN-OMP-052', N'Omega 3 Plus Kenko', N'TPCN', 500, N'Omega 3 cao cấp', N'Omega-3 Fish Oil', 2350, 1, 10),
('TPCN-OPD-053', N'Omega 3 Power DAO Nordic Health', N'TPCN', 500, N'Omega 3 Nordic Health', N'Omega-3', 258000, 1, 10),
('TPCN-OMX-054', N'Omexxel 3-6-9 Premium', N'TPCN', 500, N'Omega 3-6-9 Premium', N'Omega 3,6,9', 275000, 1, 10),
('TPCN-OMG-055', N'Omexxel Ginkgo 120 Premium', N'TPCN', 300, N'Ginkgo Biloba 120mg', N'Ginkgo Biloba', 2450, 1, 10),
('TPCN-PIK-056', N'Pikolin Ocavill', N'TPCN', 500, N'Sắt Pikolin dễ hấp thu', N'Iron Pikolin', 1980, 1, 10),
('TPCN-POB-057', N'Premium Omexxel Bone Health', N'TPCN', 500, N'Hỗ trợ xương khớp cao cấp', N'Calcium + Vitamin D & K2', 2890, 1, 10),
('TPCN-SKI-058', N'SkillMax Ocavill', N'TPCN', 500, N'Hỗ trợ trí não', N'Ginkgo & Phosphatidylserine', 2150, 1, 10),
('TPCN-VTC-059', N'Vitamin C 500mg Enervon', N'TPCN', 1000, N'Bổ sung Vitamin C và Vitamin B', N'Vitamin C 500mg', 35000, 1, 10);

INSERT INTO DonViQuyDoi (maDonVi, tenDonVi, heSoQuyDoi, maSanPham) VALUES
('DV001', N'TUYP', 1, 'OTC-BIA-001'),
('DV002', N'TUYP', 1, 'OTC-CON-002'),
('DV003', N'CHAI', 1, 'OTC-MIN-003'),
('DV004', N'CHAI', 1, 'OTC-NIZ-004'),
('DV005', N'TUYP', 1, 'OTC-REM-005'),
('DV006', N'CHAI', 1, 'OTC-CIC-013'),
('DV007', N'TUYP', 1, 'ETC-BET-019'),
('DV008', N'TUYP', 1, 'ETC-SRI-022'),
('DV009', N'TUYP', 1, 'ETC-TOM-023'),
('DV010', N'CHAI', 1, 'TPCN-D3D-043'),
('DV011', N'CHAI', 1, 'TPCN-HPM-044'),
('DV012', N'CHAI', 1, 'TPCN-LBG-049'),
('DV013', N'CHAI', 1, 'TPCN-KAN-047'),
('DV014', N'CHAI', 1, 'OTC-COR-017'),
('DV015', N'VIEN', 1, 'OTC-ALL-006'), ('DV016', N'VI', 10, 'OTC-ALL-006'), ('DV017', N'HOP', 100, 'OTC-ALL-006'),
('DV018', N'VIEN', 1, 'OTC-CET-007'), ('DV019', N'VI', 10, 'OTC-CET-007'), ('DV020', N'HOP', 100, 'OTC-CET-007'),
('DV021', N'VIEN', 1, 'OTC-CLO-008'), ('DV022', N'VI', 10, 'OTC-CLO-008'), ('DV023', N'HOP', 100, 'OTC-CLO-008'),
('DV024', N'VIEN', 1, 'OTC-EXO-009'), ('DV025', N'VI', 10, 'OTC-EXO-009'), ('DV026', N'HOP', 100, 'OTC-EXO-009'),
('DV027', N'VIEN', 1, 'OTC-HIS-010'), ('DV028', N'VI', 10, 'OTC-HIS-010'), ('DV029', N'HOP', 100, 'OTC-HIS-010'),
('DV030', N'VIEN', 1, 'OTC-ATS-011'), ('DV031', N'VI', 10, 'OTC-ATS-011'), ('DV032', N'HOP', 100, 'OTC-ATS-011'),
('DV033', N'VIEN', 1, 'OTC-FUG-012'), ('DV034', N'VI', 1, 'OTC-FUG-012'), ('DV035', N'HOP', 1, 'OTC-FUG-012'),
('DV036', N'VIEN', 1, 'ETC-DEX-020'), ('DV037', N'VI', 10, 'ETC-DEX-020'), ('DV038', N'HOP', 100, 'ETC-DEX-020'),
('DV039', N'VIEN', 1, 'ETC-BIL-024'), ('DV040', N'VI', 10, 'ETC-BIL-024'), ('DV041', N'HOP', 30, 'ETC-BIL-024'),
('DV042', N'VIEN', 1, 'ETC-LOD-025'), ('DV043', N'VI', 10, 'ETC-LOD-025'), ('DV044', N'HOP', 100, 'ETC-LOD-025'),
('DV045', N'VIEN', 1, 'ETC-RUP-026'), ('DV046', N'VI', 10, 'ETC-RUP-026'), ('DV047', N'HOP', 30, 'ETC-RUP-026'),
('DV048', N'VIEN', 1, 'ETC-CLE-029'), ('DV049', N'VI', 7, 'ETC-CLE-029'), ('DV050', N'HOP', 14, 'ETC-CLE-029'),
('DV051', N'VIEN', 1, 'ETC-FLU-030'), ('DV052', N'VI', 1, 'ETC-FLU-030'), ('DV053', N'HOP', 1, 'ETC-FLU-030'),
('DV054', N'VIEN', 1, 'ETC-IME-031'), ('DV055', N'VI', 10, 'ETC-IME-031'), ('DV056', N'HOP', 20, 'ETC-IME-031'),
('DV057', N'VIEN', 1, 'ETC-FEB-034'), ('DV058', N'VI', 10, 'ETC-FEB-034'), ('DV059', N'HOP', 30, 'ETC-FEB-034'),
('DV060', N'VIEN', 1, 'ETC-MET-036'), ('DV061', N'VI', 10, 'ETC-MET-036'), ('DV062', N'HOP', 50, 'ETC-MET-036'),
('DV063', N'VIEN', 1, 'TPCN-BPK-039'), ('DV064', N'VI', 10, 'TPCN-BPK-039'), ('DV065', N'HOP', 60, 'TPCN-BPK-039'),
('DV066', N'VIEN', 1, 'TPCN-BNI-040'), ('DV067', N'VI', 10, 'TPCN-BNI-040'), ('DV068', N'HOP', 30, 'TPCN-BNI-040'),
('DV069', N'VIEN', 1, 'TPCN-CDK-042'), ('DV070', N'VI', 10, 'TPCN-CDK-042'), ('DV071', N'HOP', 30, 'TPCN-CDK-042'),
('DV072', N'VIEN', 1, 'TPCN-OMP-052'), ('DV073', N'VI', 15, 'TPCN-OMP-052'), ('DV074', N'HOP', 60, 'TPCN-OMP-052'),
('DV075', N'VIEN', 1, 'TPCN-OMG-055'), ('DV076', N'VI', 10, 'TPCN-OMG-055'), ('DV077', N'HOP', 30, 'TPCN-OMG-055'),
('DV078', N'VIEN', 1, 'TPCN-POB-057'), ('DV079', N'VI', 10, 'TPCN-POB-057'), ('DV080', N'HOP', 30, 'TPCN-POB-057'),
('DV081', N'VIEN', 1, 'OTC-CAL-016'), ('DV082', N'VI', 10, 'OTC-CAL-016'), ('DV083', N'HOP', 100, 'OTC-CAL-016'),
('DV084', N'VIEN', 1, 'OTC-CST-018'), ('DV085', N'VI', 10, 'OTC-CST-018'), ('DV086', N'HOP', 100, 'OTC-CST-018'),
('DV087', N'VIEN', 1, 'OTC-XKH-020'), ('DV088', N'VI', 10, 'OTC-XKH-020'), ('DV089', N'HOP', 60, 'OTC-XKH-020'),
('DV090', N'VIEN', 1, 'TPCN-IMU-046'), ('DV091', N'VI', 10, 'TPCN-IMU-046'), ('DV092', N'HOP', 30, 'TPCN-IMU-046'),
('DV093', N'VIEN', 1, 'TPCN-LAC-050'), ('DV094', N'VI', 10, 'TPCN-LAC-050'), ('DV095', N'HOP', 30, 'TPCN-LAC-050'),
('DV096', N'VIEN', 1, 'TPCN-PIK-056'), ('DV097', N'VI', 10, 'TPCN-PIK-056'), ('DV098', N'HOP', 30, 'TPCN-PIK-056'),
('DV099', N'VIEN', 1, 'TPCN-SKI-058'), ('DV100', N'VI', 10, 'TPCN-SKI-058'), ('DV101', N'HOP', 30, 'TPCN-SKI-058'),
('DV102', N'VIEN', 1, 'TPCN-VTC-059'), ('DV103', N'VI', 10, 'TPCN-VTC-059'), ('DV104', N'HOP', 100, 'TPCN-VTC-059');

-- 4.7. LÔ HÀNG (SỬA LẠI GIÁ NHẬP CHO CÓ LÃI VÀ KHỚP ĐƠN VỊ CƠ BẢN)
INSERT INTO Lo (maLo, soLo, ngayHetHan, soLuongSanPham, maSanPham, giaNhap) VALUES
('LO010126001', 'L001', '2028-01-01', 500, 'OTC-BIA-001', 10000),
('LO010226001', 'L002', '2027-06-01', 500, 'OTC-BIA-001', 11000),
('LO150326001', 'L003', '2027-12-31', 500, 'OTC-CON-002', 35000),
('LO200326001', 'L004', '2026-10-01', 2000, 'OTC-ALL-006', 15000),
('LO250326001', 'L005', '2029-01-01', 3000, 'ETC-DEX-020', 3000),
('LO070526001', 'L006', '2028-05-07', 1000, 'TPCN-VTC-059', 5000),
('LO070526002', 'L007', '2028-12-01', 100, 'OTC-MIN-003', 150000),
('LO070526003', 'L008', '2027-05-01', 100, 'OTC-NIZ-004', 100000),
('LO070526004', 'L009', '2029-01-01', 500, 'TPCN-BPK-039', 150000),
('LO070526005', 'L010', '2028-01-01', 300, 'TPCN-OMG-055', 200000),
('LO070526006', 'LN001', '2028-12-31', 1000, 'OTC-CAL-016', 1000),
('LO070526007', 'LN002', '2028-12-31', 1000, 'OTC-COR-017', 120000),
('LO070526008', 'LN003', '2028-12-31', 1000, 'OTC-CST-018', 600),
('LO070526009', 'LN004', '2028-12-31', 1000, 'OTC-KID-019', 100000),
('LO070526010', 'LN005', '2028-12-31', 1000, 'OTC-XKH-020', 1500),
('LO070526011', 'LN006', '2028-12-31', 500, 'ETC-BET-019', 65000),
('LO070526012', 'LN007', '2028-12-31', 500, 'ETC-MAX-021', 250000),
('LO070526013', 'LN008', '2028-12-31', 500, 'ETC-SRI-022', 75000),
('LO070526014', 'LN009', '2028-12-31', 500, 'ETC-TOM-023', 60000),
('LO070526015', 'LN010', '2028-12-31', 500, 'ETC-BIL-024', 800),
('LO070526016', 'LN011', '2028-12-31', 500, 'ETC-LOD-025', 650),
('LO070526017', 'LN012', '2028-12-31', 500, 'ETC-RUP-026', 750),
('LO070526018', 'LN013', '2028-12-31', 500, 'ETC-RUT-027', 700),
('LO070526019', 'LN014', '2028-12-31', 500, 'ETC-ZYR-028', 600),
('LO070526020', 'LN015', '2028-12-31', 500, 'ETC-ZYR-029', 600),
('LO070526021', 'LN016', '2028-12-31', 500, 'ETC-CLE-029', 1000),
('LO070526022', 'LN017', '2028-12-31', 500, 'ETC-FLU-030', 95000),
('LO070526023', 'LN018', '2028-12-31', 500, 'ETC-IME-031', 700),
('LO070526024', 'LN019', '2028-12-31', 500, 'ETC-IME-032', 1000),
('LO070526025', 'LN020', '2028-12-31', 500, 'ETC-LEC-033', 1200),
('LO070526026', 'LN021', '2028-12-31', 500, 'ETC-FEB-034', 1400),
('LO070526027', 'LN022', '2028-12-31', 500, 'ETC-FEB-035', 1800),
('LO070526028', 'LN023', '2028-12-31', 500, 'ETC-MET-036', 800),
('LO070526029', 'LN024', '2028-12-31', 500, 'ETC-OPE-037', 1200),
('LO070526030', 'LN025', '2028-12-31', 500, 'ETC-OPE-038', 1600),
('LO070526031', 'LN026', '2028-12-31', 500, 'TPCN-BNI-040', 1800),
('LO070526032', 'LN027', '2028-12-31', 500, 'TPCN-BPL-041', 120000),
('LO070526033', 'LN028', '2028-12-31', 220, 'TPCN-CDK-042', 1200),
('LO070526034', 'LN029', '2028-12-31', 500, 'TPCN-D3D-043', 95000),
('LO070526035', 'LN030', '2028-12-31', 500, 'TPCN-HPM-044', 160000),
('LO070526036', 'LN031', '2028-12-31', 500, 'TPCN-HHT-045', 185000),
('LO070526037', 'LN032', '2028-12-31', 500, 'TPCN-IMU-046', 1300),
('LO070526038', 'LN033', '2028-12-31', 500, 'TPCN-KAN-047', 145000),
('LO070526039', 'LN034', '2028-12-31', 500, 'TPCN-KID-048', 125000),
('LO070526040', 'LN035', '2028-12-31', 500, 'TPCN-LBG-049', 115000),
('LO070526041', 'LN036', '2028-12-31', 500, 'TPCN-LAC-050', 1800),
('LO070526042', 'LN037', '2028-12-31', 500, 'TPCN-NUT-051', 120000),
('LO070526043', 'LN038', '2028-12-31', 500, 'TPCN-OMP-052', 1600),
('LO070526044', 'LN039', '2028-12-31', 500, 'TPCN-OPD-053', 195000),
('LO070526045', 'LN040', '2028-12-31', 500, 'TPCN-OMX-054', 210000),
('LO070526046', 'LN041', '2028-12-31', 500, 'TPCN-PIK-056', 1400),
('LO070526047', 'LN042', '2028-12-31', 500, 'TPCN-POB-057', 2100),
('LO070526048', 'LN043', '2028-12-31', 500, 'TPCN-SKI-058', 1500);

-- 4.8. QUÀ TẶNG 
INSERT INTO QuaTang (maKhuyenMai, maDonVi, soLuongTang) VALUES
('KM150401', 'DV103', 1), 
('KM010402', 'DV016', 1), 
('KM150401', 'DV102', 10), 
('KM010402', 'DV015', 5), 
('KM011001', 'DV002', 1); 

-- 4.9. HÓA ĐƠN & CHI TIẾT 
INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc, phuongThucThanhToan) VALUES
('HDB200426001', '2026-04-20 10:00:00', 'QL001', 1, 'TV000001', 'KM010401', N'BAN_HANG', 'CA20042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB200426002', '2026-04-20 14:00:00', 'QL001', 1, 'KH_LE', NULL, N'BAN_HANG', 'CA20042601', N'', NULL, 'DT200426001', N'CHUYEN_KHOAN'),
('HDB210426001', '2026-04-21 09:00:00', 'DS001', 1, 'TV000002', 'KM150401', N'BAN_HANG', 'CA21042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB210426002', '2026-04-21 11:00:00', 'DS001', 1, 'TV000003', NULL, N'BAN_HANG', 'CA21042601', N'', NULL, 'DT210426001', N'CHUYEN_KHOAN'),
('HDB220426001', '2026-04-22 08:30:00', 'DS001', 1, 'TV000004', 'KM010402', N'BAN_HANG', 'CA22042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB260426001', '2026-04-26 09:00:00', 'DS001', 1, 'TV000002', 'KM150401', N'BAN_HANG', 'CA26042601', N'', NULL, NULL, N'TIEN_MAT'),
('HDB300426001', '2026-04-30 11:00:00', 'DS001', 1, 'TV000003', NULL, N'BAN_HANG', 'CA30042601', N'', NULL, 'DT300426001', N'CHUYEN_KHOAN'),
('HDB020526001', '2026-05-02 08:30:00', 'DS001', 1, 'TV000004', 'KM010402', N'BAN_HANG', 'CA02052601', N'', NULL, NULL, N'TIEN_MAT');

-- SỬA LẠI TOÀN BỘ ĐƠN GIÁ (BẰNG DONGIACOBAN * HESOQUYDOI)
INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuong, donGia, laQuaTangKem) VALUES
('HDB200426001', 'DV001', 2, 95000, 0), 
('HDB200426001', 'DV015', 1, 850, 0),  
('HDB200426002', 'DV016', 10, 8500, 0), 
('HDB210426001', 'DV015', 2, 850, 0),
('HDB210426002', 'DV017', 5, 85000, 0), 
('HDB260426001', 'DV016', 10, 8500, 0),
('HDB300426001', 'DV015', 2, 850, 0),
('HDB020526001', 'DV017', 5, 85000, 0);

INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong, laQuaTangKem) VALUES
('HDB200426001', 'DV001', 'LO010126001', 2, 0),
('HDB200426001', 'DV015', 'LO200326001', 1, 0),
('HDB200426002', 'DV016', 'LO200326001', 10, 0),
('HDB210426001', 'DV015', 'LO200326001', 2, 0),
('HDB210426002', 'DV017', 'LO200326001', 5, 0),
('HDB260426001', 'DV016', 'LO200326001', 10, 0),
('HDB300426001', 'DV015', 'LO200326001', 2, 0),
('HDB020526001', 'DV017', 'LO200326001', 5, 0);
GO

--- =================================================== ---
--- 5. Trigger
--- =================================================== ---
CREATE TRIGGER trg_Lo_UpdateTonKho
ON Lo
AFTER INSERT, UPDATE, DELETE
AS
BEGIN
    SET NOCOUNT ON;

    -- Xử lý phần dữ liệu bị mất đi (Dành cho sự kiện DELETE và UPDATE)
    -- Lấy số lượng cũ từ bảng ảo 'deleted' trừ khỏi kho
    IF EXISTS (SELECT 1 FROM deleted)
    BEGIN
        UPDATE sp
        SET sp.soLuongTon = ISNULL(sp.soLuongTon, 0) - d.TongSoLuongCu
        FROM SanPham sp
        INNER JOIN (
            -- Tính tổng số lượng lô bị xóa/cũ theo từng mã sản phẩm
            SELECT maSanPham, SUM(soLuongSanPham) AS TongSoLuongCu
            FROM deleted
            GROUP BY maSanPham
        ) d ON sp.maSanPham = d.maSanPham;
    END

    -- Xử lý phần dữ liệu mới được thêm (Dành cho sự kiện INSERT và UPDATE)
    -- Lấy số lượng mới từ bảng ảo 'inserted' cộng vào kho
    IF EXISTS (SELECT 1 FROM inserted)
    BEGIN
        UPDATE sp
        SET sp.soLuongTon = ISNULL(sp.soLuongTon, 0) + i.TongSoLuongMoi
        FROM SanPham sp
        INNER JOIN (
            -- Tính tổng số lượng lô mới theo từng mã sản phẩm
            SELECT maSanPham, SUM(soLuongSanPham) AS TongSoLuongMoi
            FROM inserted
            GROUP BY maSanPham
        ) i ON sp.maSanPham = i.maSanPham;
    END
END;
GO