-- =====================================================================
-- 1. THÊM DỮ LIỆU CÁC BẢNG DANH MỤC ĐỘC LẬP (BẢNG CHA)
-- =====================================================================

-- Bảng NhanVien
INSERT INTO NhanVien (maNhanVien, tenNhanVien, cccd, sdt, chucVu, trangThai) VALUES
                                                                                 ('NV01', N'Phan Hoài Bảo', '079200000001', '0901234567', N'NHAN_VIEN_QUAN_LY', 1),
                                                                                 ('NV02', N'Nguyễn Văn An', '079200000002', '0901234568', N'DUOC_SI', 1),
                                                                                 ('NV03', N'Trần Thị Bích', '079200000003', '0901234569', N'DUOC_SI', 1),
                                                                                 ('NV04', N'Lê Văn Cường', '079200000004', '0901234570', N'DUOC_SI', 1),
                                                                                 ('NV05', N'Phạm Thị Dung', '079200000005', '0901234571', N'DUOC_SI', 0);

-- Bảng KhachHang
INSERT INTO KhachHang (maKhachHang, tenKhachHang, sdt, trangThai) VALUES
                                                                      ('KH01', N'Khách vãng lai 1', '0911111111', N'KHACH_LE'),
                                                                      ('KH02', N'Ngô Thị Em', '0922222222', N'KHACH_HANG_THANH_VIEN'),
                                                                      ('KH03', N'Vũ Văn Phong', '0933333333', N'KHACH_HANG_THANH_VIEN'),
                                                                      ('KH04', N'Đặng Thị Giang', '0944444444', N'KHACH_HANG_THANH_VIEN'),
                                                                      ('KH05', N'Bùi Văn Hùng', '0955555555', N'KHACH_HANG_THANH_VIEN');

-- Bảng KhuyenMai
INSERT INTO KhuyenMai (maKhuyenMai, tenKhuyenMai, thoiGianBatDau, thoiGianKetThuc, loaiKhuyenMai, khuyenMaiPhanTram, giaTriDonHangToiThieu) VALUES
                                                                                                                                                ('KM01', N'Giảm 10% đơn từ 500k', '2026-04-01', '2026-05-01', N'PHAN_TRAM', 10, 500000),
                                                                                                                                                ('KM02', N'Mua 1 tặng 1 Vitamin C', '2026-04-15', '2026-04-30', N'TANG_KEM', NULL, 0),
                                                                                                                                                ('KM03', N'Giảm giá 5% Lễ 30/4', '2026-04-25', '2026-05-05', N'PHAN_TRAM', 5, 0),
                                                                                                                                                ('KM04', N'Tặng khẩu trang đơn 100k', '2026-04-01', '2026-12-31', N'TANG_KEM', NULL, 100000),
                                                                                                                                                ('KM05', N'Sale sinh nhật 20%', '2026-10-01', '2026-10-31', N'PHAN_TRAM', 20, 1000000);

-- Bảng SanPham
INSERT INTO SanPham (maSanPham, tenSanPham, phanLoai, soLuongTon, moTa, hoatChat, donGiaCoBan, trangThaiKinhDoanh, thue) VALUES
                                                                                                                             ('SP01', N'Panadol Extra', N'OTC', 1000, N'Giảm đau, hạ sốt', N'Paracetamol 500mg, Caffeine 65mg', 15000, 1, 5),
                                                                                                                             ('SP02', N'Vitamin C 500mg', N'TPCN', 500, N'Bổ sung Vitamin C', N'Vitamin C', 50000, 1, 10),
                                                                                                                             ('SP03', N'Amoxicillin 500mg', N'ETC', 2000, N'Kháng sinh', N'Amoxicillin', 20000, 1, 5),
                                                                                                                             ('SP04', N'Khẩu trang y tế 4 lớp', N'OTC', 5000, N'Bảo vệ đường hô hấp', N'Không', 35000, 1, 8),
                                                                                                                             ('SP05', N'Nước muối sinh lý 0.9%', N'OTC', 3000, N'Rửa mắt, mũi', N'NaCl 0.9%', 5000, 1, 5);

-- Bảng DonThuoc
INSERT INTO DonThuoc (maDonThuoc, tenBacSi, coSoKhamBenh, ngayKeDon) VALUES
                                                                         ('DT01', N'BS. Nguyễn Văn A', N'Bệnh viện Chợ Rẫy', '2026-04-20'),
                                                                         ('DT02', N'BS. Trần Thị B', N'Bệnh viện 115', '2026-04-21'),
                                                                         ('DT03', N'BS. Lê Văn C', N'Phòng khám đa khoa', '2026-04-22'),
                                                                         ('DT04', N'BS. Phạm Thị D', N'Bệnh viện Đại học Y Dược', '2026-04-23'),
                                                                         ('DT05', N'BS. Ngô Văn E', N'Bệnh viện Nhi Đồng', '2026-04-24');


-- =====================================================================
-- 2. THÊM DỮ LIỆU CÁC BẢNG PHỤ THUỘC BẬC 1 (CÓ 1 KHÓA NGOẠI)
-- =====================================================================

-- Bảng TaiKhoan (Phụ thuộc NhanVien)
INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNhanVien) VALUES
                                                            ('admin', '123456', 'NV01'),
                                                            ('duocsi1', '123456', 'NV02'),
                                                            ('duocsi2', '123456', 'NV03'),
                                                            ('duocsi3', '123456', 'NV04'),
                                                            ('duocsi4', '123456', 'NV05');

-- Bảng CaLam (Phụ thuộc NhanVien)
INSERT INTO CaLam (maCa, maNhanVien, gioBatDau, gioKetThuc, trangThai, tienMoCa, tienKetCa, tienHeThong, ghiChu) VALUES
                                                                                                                     ('CA01', 'NV01', '2026-04-20 08:00:00', '2026-04-20 16:00:00', N'DONG', 1000000, 5000000, 4000000, N'Ca bình thường'),
                                                                                                                     ('CA02', 'NV02', '2026-04-20 16:00:00', '2026-04-20 22:00:00', N'DONG', 5000000, 8000000, 3000000, N'Ca bình thường'),
                                                                                                                     ('CA03', 'NV03', '2026-04-21 08:00:00', '2026-04-21 16:00:00', N'DONG', 1000000, 6000000, 5000000, N'Ca bình thường'),
                                                                                                                     ('CA04', 'NV04', '2026-04-21 16:00:00', '2026-04-21 22:00:00', N'DONG', 6000000, 9000000, 3000000, N'Ca bình thường'),
                                                                                                                     ('CA05', 'NV02', '2026-04-22 08:00:00', NULL, N'DANG_MO', 1000000, NULL, 0, N'Đang trực');

-- Bảng DonViQuyDoi (Phụ thuộc SanPham)
INSERT INTO DonViQuyDoi (maDonVi, tenDonVi, heSoQuyDoi, maSanPham) VALUES
                                                                       ('DV01', N'VI', 10, 'SP01'),
                                                                       ('DV02', N'HOP', 100, 'SP01'),
                                                                       ('DV03', N'HOP', 30, 'SP02'),
                                                                       ('DV04', N'VIEN', 1, 'SP03'),
                                                                       ('DV05', N'CHAI', 1, 'SP05');

-- Bảng Lo (Phụ thuộc SanPham)
INSERT INTO Lo (maLo, soLo, ngayHetHan, soLuongSanPham, maSanPham, giaNhap) VALUES
                                                                                ('LO01', 'L001', '2028-01-01', 500, 'SP01', 10000),
                                                                                ('LO02', 'L002', '2027-06-01', 500, 'SP01', 11000),
                                                                                ('LO03', 'L003', '2027-12-31', 500, 'SP02', 35000),
                                                                                ('LO04', 'L004', '2026-10-01', 2000, 'SP03', 15000),
                                                                                ('LO05', 'L005', '2029-01-01', 3000, 'SP05', 3000);


-- =====================================================================
-- 3. THÊM DỮ LIỆU BẢNG GIAO NHAU (NHIỀU KHÓA NGOẠI)
-- =====================================================================

-- Bảng QuaTang (Phụ thuộc KhuyenMai, SanPham)
INSERT INTO QuaTang (maKhuyenMai, maSanPham, soLuongTang) VALUES
                                                              ('KM02', 'SP02', 1),
                                                              ('KM04', 'SP04', 1),
                                                              ('KM02', 'SP04', 2),
                                                              ('KM04', 'SP05', 1),
                                                              ('KM05', 'SP04', 5);

-- Bảng HoaDon (Phụ thuộc NhanVien, KhachHang, KhuyenMai, CaLam, DonThuoc)
INSERT INTO HoaDon (maHoaDon, thoiGianTao, maNhanVien, trangThaiThanhToan, maKhachHang, maKhuyenMai, loaiHoaDon, maCa, ghiChu, maHoaDonDoiTra, maDonThuoc, phuongThucThanhToan) VALUES
                                                                                                                                                                                    ('HD01', '2026-04-20 10:00:00', 'NV01', 1, 'KH02', 'KM01', N'BAN_HANG', 'CA01', N'', NULL, NULL, N'TIEN_MAT'),
                                                                                                                                                                                    ('HD02', '2026-04-20 14:00:00', 'NV01', 1, 'KH01', NULL, N'BAN_HANG', 'CA01', N'', NULL, 'DT01', N'CHUYEN_KHOAN'),
                                                                                                                                                                                    ('HD03', '2026-04-21 09:00:00', 'NV03', 1, 'KH03', 'KM02', N'BAN_HANG', 'CA03', N'', NULL, NULL, N'TIEN_MAT'),
                                                                                                                                                                                    ('HD04', '2026-04-21 11:00:00', 'NV03', 1, 'KH04', NULL, N'BAN_HANG', 'CA03', N'', NULL, 'DT02', N'CHUYEN_KHOAN'),
                                                                                                                                                                                    ('HD05', '2026-04-22 08:30:00', 'NV02', 1, 'KH05', 'KM04', N'BAN_HANG', 'CA05', N'', NULL, NULL, N'TIEN_MAT');

-- Bảng ChiTietHoaDon (Phụ thuộc HoaDon, DonViQuyDoi)
INSERT INTO ChiTietHoaDon (maHoaDon, maDonVi, soLuong, donGia, laQuaTangKem) VALUES
                                                                                 ('HD01', 'DV01', 2, 15000, 0),
                                                                                 ('HD01', 'DV03', 1, 50000, 0),
                                                                                 ('HD02', 'DV04', 10, 20000, 0),
                                                                                 ('HD03', 'DV03', 2, 50000, 0),
                                                                                 ('HD04', 'DV05', 5, 5000, 0);

-- Bảng SuPhanBoLo (Phụ thuộc ChiTietHoaDon, Lo)
INSERT INTO SuPhanBoLo (maHoaDon, maDonVi, maLo, soLuong) VALUES
                                                              ('HD01', 'DV01', 'LO01', 2),
                                                              ('HD01', 'DV03', 'LO03', 1),
                                                              ('HD02', 'DV04', 'LO04', 10),
                                                              ('HD03', 'DV03', 'LO03', 2),
                                                              ('HD04', 'DV05', 'LO05', 5);