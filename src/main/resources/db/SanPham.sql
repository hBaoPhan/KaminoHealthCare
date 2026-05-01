-- =====================================================
-- File: data_sanpham.sql
-- Mô tả: Dữ liệu mẫu 60 sản phẩm cho ứng dụng quản lý nhà thuốc
-- Mã sản phẩm: [PhanLoai]-[Viết tắt tên]-[Số thứ tự]
-- Ảnh: /images/anhSanPham/{maSanPham}.png
-- =====================================================

USE QUANLYKAMINOHEALTHCARE;
GO

-- =====================================================
-- DDL: BẢNG + INDEX (phù hợp SanPhamDAO)
-- =====================================================

IF OBJECT_ID('dbo.SanPham', 'U') IS NULL
BEGIN
	CREATE TABLE dbo.SanPham (
		maSanPham           VARCHAR(20)    NOT NULL,
		tenSanPham          NVARCHAR(255)  NOT NULL,
		phanLoai            VARCHAR(10)    NOT NULL,  -- ETC/OTC/TPCN
		soLuongTon          INT            NOT NULL   CONSTRAINT DF_SanPham_soLuongTon DEFAULT (0),
		moTa                NVARCHAR(1000) NULL,
		hoatChat            NVARCHAR(255)  NULL,
		donGiaCoBan         DECIMAL(18,2)  NOT NULL   CONSTRAINT DF_SanPham_donGiaCoBan DEFAULT (0),
		trangThaiKinhDoanh  BIT            NOT NULL   CONSTRAINT DF_SanPham_trangThaiKinhDoanh DEFAULT (1),
		thue                DECIMAL(5,2)   NOT NULL   CONSTRAINT DF_SanPham_thue DEFAULT (0.10),
		CONSTRAINT PK_SanPham PRIMARY KEY (maSanPham),
		CONSTRAINT CK_SanPham_phanLoai CHECK (phanLoai IN ('ETC', 'OTC', 'TPCN')),
		CONSTRAINT CK_SanPham_soLuongTon CHECK (soLuongTon >= 0),
		CONSTRAINT CK_SanPham_donGiaCoBan CHECK (donGiaCoBan >= 0),
		CONSTRAINT CK_SanPham_thue CHECK (thue >= 0 AND thue <= 1)
	);

	CREATE INDEX IX_SanPham_TenSanPham ON dbo.SanPham(tenSanPham);
	CREATE INDEX IX_SanPham_PhanLoai ON dbo.SanPham(phanLoai);
END
GO

-- =====================================================
-- STORED PROCEDURES (tương ứng các hàm trong SanPhamDAO)
-- Lưu ý: hiện SanPhamDAO đang dùng query trực tiếp, không bắt buộc dùng SP.
-- =====================================================

IF OBJECT_ID('dbo.sp_SanPham_LayTatCa', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_LayTatCa;
GO
CREATE PROCEDURE dbo.sp_SanPham_LayTatCa
AS
BEGIN
	SET NOCOUNT ON;
	SELECT * FROM dbo.SanPham;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_TimTheoMa', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_TimTheoMa;
GO
CREATE PROCEDURE dbo.sp_SanPham_TimTheoMa
	@maSanPham VARCHAR(20)
AS
BEGIN
	SET NOCOUNT ON;
	SELECT * FROM dbo.SanPham WHERE maSanPham = @maSanPham;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_Them', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_Them;
GO
CREATE PROCEDURE dbo.sp_SanPham_Them
	@maSanPham          VARCHAR(20),
	@tenSanPham         NVARCHAR(255),
	@phanLoai           VARCHAR(10),
	@soLuongTon         INT,
	@moTa               NVARCHAR(1000) = NULL,
	@hoatChat           NVARCHAR(255) = NULL,
	@donGiaCoBan        DECIMAL(18,2),
	@trangThaiKinhDoanh BIT,
	@thue               DECIMAL(5,2)
AS
BEGIN
	SET NOCOUNT ON;
	INSERT INTO dbo.SanPham (maSanPham, tenSanPham, phanLoai, soLuongTon, moTa, hoatChat, donGiaCoBan, trangThaiKinhDoanh, thue)
	VALUES (@maSanPham, @tenSanPham, @phanLoai, @soLuongTon, @moTa, @hoatChat, @donGiaCoBan, @trangThaiKinhDoanh, @thue);
END
GO

IF OBJECT_ID('dbo.sp_SanPham_CapNhat', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_CapNhat;
GO
CREATE PROCEDURE dbo.sp_SanPham_CapNhat
	@maSanPham          VARCHAR(20),
	@tenSanPham         NVARCHAR(255),
	@phanLoai           VARCHAR(10),
	@soLuongTon         INT,
	@moTa               NVARCHAR(1000) = NULL,
	@hoatChat           NVARCHAR(255) = NULL,
	@donGiaCoBan        DECIMAL(18,2),
	@trangThaiKinhDoanh BIT,
	@thue               DECIMAL(5,2)
AS
BEGIN
	SET NOCOUNT ON;
	UPDATE dbo.SanPham
	SET tenSanPham = @tenSanPham,
		phanLoai = @phanLoai,
		soLuongTon = @soLuongTon,
		moTa = @moTa,
		hoatChat = @hoatChat,
		donGiaCoBan = @donGiaCoBan,
		trangThaiKinhDoanh = @trangThaiKinhDoanh,
		thue = @thue
	WHERE maSanPham = @maSanPham;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_Xoa', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_Xoa;
GO
CREATE PROCEDURE dbo.sp_SanPham_Xoa
	@maSanPham VARCHAR(20)
AS
BEGIN
	SET NOCOUNT ON;
	DELETE FROM dbo.SanPham WHERE maSanPham = @maSanPham;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_TimTheoMaHoacTen', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_TimTheoMaHoacTen;
GO
CREATE PROCEDURE dbo.sp_SanPham_TimTheoMaHoacTen
	@tuKhoa NVARCHAR(255)
AS
BEGIN
	SET NOCOUNT ON;
	DECLARE @kw NVARCHAR(260) = N'%' + @tuKhoa + N'%';
	SELECT *
	FROM dbo.SanPham
	WHERE maSanPham LIKE @kw OR tenSanPham LIKE @kw;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_TimTheoPhanLoai', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_TimTheoPhanLoai;
GO
CREATE PROCEDURE dbo.sp_SanPham_TimTheoPhanLoai
	@phanLoai VARCHAR(10)
AS
BEGIN
	SET NOCOUNT ON;
	SELECT * FROM dbo.SanPham WHERE phanLoai = @phanLoai;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_LayDangKinhDoanh', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_LayDangKinhDoanh;
GO
CREATE PROCEDURE dbo.sp_SanPham_LayDangKinhDoanh
AS
BEGIN
	SET NOCOUNT ON;
	SELECT * FROM dbo.SanPham WHERE trangThaiKinhDoanh = 1;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_TonTaiMa', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_TonTaiMa;
GO
CREATE PROCEDURE dbo.sp_SanPham_TonTaiMa
	@maSanPham VARCHAR(20)
AS
BEGIN
	SET NOCOUNT ON;
	SELECT CASE WHEN EXISTS (SELECT 1 FROM dbo.SanPham WHERE maSanPham = @maSanPham) THEN 1 ELSE 0 END AS tonTai;
END
GO

IF OBJECT_ID('dbo.sp_SanPham_CapNhatSoLuongTon', 'P') IS NOT NULL DROP PROCEDURE dbo.sp_SanPham_CapNhatSoLuongTon;
GO
CREATE PROCEDURE dbo.sp_SanPham_CapNhatSoLuongTon
	@maSanPham VARCHAR(20),
	@soLuongTon INT
AS
BEGIN
	SET NOCOUNT ON;
	UPDATE dbo.SanPham SET soLuongTon = @soLuongTon WHERE maSanPham = @maSanPham;
END
GO

-- Xóa dữ liệu cũ nếu cần reset
-- DELETE FROM SanPham;

-- =====================================================
-- 60 SẢN PHẨM - KHÔNG TRÙNG LẶP
-- =====================================================

INSERT INTO SanPham 
    (maSanPham, tenSanPham, phanLoai, soLuongTon, moTa, hoatChat, donGiaCoBan, trangThaiKinhDoanh, thue) 
VALUES

-- ==================== OTC - THUỐC KHÔNG KÊ ĐƠN (Theo thứ tự ảnh) ====================

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
('OTC-XKH-020', 'Xương Khớp Trương Phúc', 'OTC', 160, 'Hỗ trợ xương khớp', 'Glucosamine & Chondroitin', 210000, 1, 0.05);

-- ==================== ETC - THUỐC KÊ ĐƠN (Theo thứ tự ảnh mới) ====================

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
('ETC-OPE-038', 'Opedulox 80 OPV', 'ETC', 70, 'Thuốc giảm đau thần kinh', 'Duloxetine 80mg', 225000, 1, 0.1);

-- ==================== TPCN - THỰC PHẨM CHỨC NĂNG ====================
-- ==================== TPCN - THỰC PHẨM CHỨC NĂNG (Theo thứ tự ảnh) ====================

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

-- =====================================================
-- LƯU Ý QUAN TRỌNG:
-- 1. Tổng cộng: 60 sản phẩm, KHÔNG TRÙNG lặp
-- 2. Mỗi sản phẩm có mã duy nhất theo quy tắc
-- 3. Ảnh phải được đặt tên đúng theo mã sản phẩm + ".png"
--    Ví dụ: ETC-HHT-001.png, OTC-PAN-001.png, TPCN-VIC-001.png
-- 4. Thư mục ảnh: src/main/resources/images/anhSanPham/
-- =====================================================