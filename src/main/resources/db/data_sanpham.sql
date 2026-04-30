-- =====================================================
-- File: data_sanpham.sql
-- Mô tả: Dữ liệu mẫu sản phẩm - Mã sản phẩm theo quy tắc mới
-- Ảnh được lưu theo mã sản phẩm: /images/anhSanPham/{maSanPham}.png
-- =====================================================

USE QUANLYKAMINOHEALTHCARE;
GO

-- Xóa dữ liệu cũ nếu cần reset
-- DELETE FROM SanPham;

-- =====================================================
-- DỮ LIỆU SẢN PHẨM (Mã đã được tạo theo quy tắc)
-- =====================================================

INSERT INTO SanPham 
    (maSanPham, tenSanPham, phanLoai, soLuongTon, moTa, hoatChat, donGiaCoBan, trangThaiKinhDoanh, thue) 
VALUES
-- ETC
('ETC-HHT-001', 'Hoạt Huyết Trường Phúc', 'ETC', 150, 'Thuốc cải thiện tuần hoàn não, giúp tăng cường trí nhớ', 'Cao Gingko Biloba, Nattokinase', 99000, 1, 0.1),
('ETC-FGS-001', 'Freezefast Ghiaccio Spray', 'ETC', 80, 'Thuốc xịt lạnh giảm đau cơ bắp', 'Menthol, Camphor', 210000, 1, 0.1),
('ETC-NAT-001', 'Natto Gold 3000FU', 'ETC', 200, 'Hỗ trợ tan máu đông, ngăn ngừa đột quỵ', 'Nattokinase 3000FU', 295000, 1, 0.1),
('ETC-SVR-001', 'SVR Sebiaclear Moussant', 'ETC', 120, 'Sữa rửa mặt trị mụn cho da dầu', 'Zinc, Acid Salicylic', 229400, 1, 0.1),
('ETC-GIL-001', 'Giloba 40mg MEGA', 'ETC', 300, 'Thuốc bổ não, cải thiện trí nhớ', 'Ginkgo Biloba 40mg', 38333, 1, 0.1),
('ETC-TAN-001', 'Tanakan', 'ETC', 180, 'Thuốc điều rối loạn tuần hoàn não', 'Ginkgo Biloba 40mg', 120000, 1, 0.1),

-- OTC
('OTC-PAN-001', 'Panadol', 'OTC', 500, 'Thuốc hạ sốt, giảm đau Paracetamol', 'Paracetamol 500mg', 35000, 1, 0.05),
('OTC-ORE-001', 'Oresol', 'OTC', 1000, 'Bù điện giải khi tiêu chảy', 'ORS', 22000, 1, 0.05),
('OTC-EFF-001', 'Efferalgan', 'OTC', 280, 'Thuốc hạ sốt dạng sủi bọt', 'Paracetamol 500mg', 60000, 1, 0.1),
('OTC-CET-001', 'Cetirizine', 'OTC', 420, 'Thuốc chống dị ứng', 'Cetirizine 10mg', 55000, 1, 0.1),
('OTC-DAG-001', 'Dạ dày', 'OTC', 250, 'Thuốc điều trị viêm loét dạ dày', 'Omeprazole 20mg', 120000, 1, 0.1),
('OTC-SIR-001', 'Siro ho', 'OTC', 350, 'Siro ho cho trẻ em', 'Herb Extract', 47000, 1, 0.05),

-- TPCN
('TPCN-VIC-001', 'Vitamin C', 'TPCN', 400, 'Vitamin C bổ sung tăng đề kháng', 'Ascorbic Acid 500mg', 89000, 1, 0.1),
('TPCN-ZIN-001', 'Zincovit', 'TPCN', 350, 'Vitamin tổng hợp cho trẻ em', 'Vitamin và Khoáng chất', 130000, 1, 0.1),
('TPCN-PRO-001', 'Probiotics', 'TPCN', 220, 'Men vi sinh bổ sung hệ tiêu hóa', 'Lactobacillus', 175000, 0, 0.1),
('TPCN-BLA-001', 'Blackmores', 'TPCN', 160, 'Viên bổ sung Vitamin B', 'Vitamin B Complex', 250000, 1, 0.1),
('TPCN-GLU-001', 'Glucosamine', 'TPCN', 280, 'Bổ sung khớp xương', 'Glucosamine 1500mg', 230000, 1, 0.1),
('TPCN-COL-001', 'Collagen', 'TPCN', 180, 'Collagen làm đẹp da', 'Hydrolyzed Collagen', 310000, 1, 0.1),
('TPCN-OME-001', 'Omega 3', 'TPCN', 320, 'Dầu cá bổ tim mạch', 'Omega-3 Fish Oil', 220000, 1, 0.1);

-- =====================================================
-- LƯU Ý QUAN TRỌNG:
-- 1. Mỗi sản phẩm sẽ có ảnh tương ứng với tên file = maSanPham + ".png"
--    Ví dụ: ETC-HHT-001.png, OTC-PAN-001.png, TPCN-VIC-001.png
-- 2. Đường dẫn ảnh trong code: /images/anhSanPham/{maSanPham}.png
-- 3. Bạn cần đặt các file ảnh vào thư mục: 
--    src/main/resources/images/anhSanPham/
-- =====================================================