package com.example.utils;

import com.example.entity.*;
import com.example.entity.enums.*;

import javax.swing.*;
import java.awt.*;
import java.awt.print.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InHoaDonPOS {

    public static void inHoaDon(HoaDon hd, List<ChiTietHoaDon> dsChiTiet, double tienKhachDua, double tienThoi) {
        PrinterJob job = PrinterJob.getPrinterJob();

        // Cấu hình trang cho máy in POS 80mm
        PageFormat pf = job.defaultPage();
        pf.setOrientation(PageFormat.PORTRAIT);
        Paper paper = new Paper();

        // Chiều rộng cuộn giấy 80mm = 80 / 25.4 * 72 = ~226.7 pt.
        // Ta lấy 220 pt làm chiều rộng in ấn tiêu chuẩn của máy in POS.
        double width = 220;

        // Tính toán chiều cao cuộn giấy động để vừa khít nội dung, tiết kiệm giấy in
        // nhiệt
        double height = 280 + (dsChiTiet.size() * 22);
        if (hd.getDonThuoc() != null) {
            height += 20;
        }
        if (hd.getKhuyenMai() != null) {
            height += 10;
        }
        if (hd.getPhuongThucThanhToan() == PhuongThucThanhToan.TIEN_MAT) {
            height += 20;
        }

        paper.setSize(width, height);
        paper.setImageableArea(0, 0, width, height);
        pf.setPaper(paper);

        // Định nghĩa đối tượng Printable vẽ hóa đơn
        Printable receiptPrintable = new Printable() {
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                if (pageIndex > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) graphics;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                // Kích hoạt Antialiasing để nét chữ in ra mịn màng và rõ đẹp hơn
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2d.setColor(Color.BLACK);

                int y = 15;
                int startX = 8;
                int endX = (int) (width - 8);
                DecimalFormat df = new DecimalFormat("#,### đ");
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                // Định nghĩa hệ thống Font chữ POS chuyên nghiệp
                Font fontTitle = new Font("Segoe UI", Font.BOLD, 12);
                Font fontHeader = new Font("Segoe UI", Font.BOLD, 8);
                Font fontNormal = new Font("Segoe UI", Font.PLAIN, 8);
                Font fontItalic = new Font("Segoe UI", Font.ITALIC, 7);

                // --- THÔNG TIN TIÊU ĐỀ HỆ THỐNG ---
                g2d.setFont(fontTitle);
                drawCenteredString("KAMINO HEALTH CARE", endX, g2d, y);
                y += 12;

                g2d.setFont(fontNormal);
                drawCenteredString("12 Nguyễn Văn Bảo, Hạnh Thông, Hồ Chí Minh", endX, g2d, y);
                y += 10;
                drawCenteredString("SĐT: 033 580 6335", endX, g2d, y);
                y += 12;

                drawSeparator("=====================================", g2d, startX, y);
                y += 12;

                // --- TIÊU ĐỀ HOÁ ĐƠN ---
                g2d.setFont(fontTitle);
                String titleHD = "HÓA ĐƠN BÁN LẺ";
                if (hd.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG) {
                    titleHD = "HÓA ĐƠN ĐỔI HÀNG";
                } else if (hd.getLoaiHoaDon() == LoaiHoaDon.TRA_HANG) {
                    titleHD = "HÓA ĐƠN TRẢ HÀNG";
                }
                drawCenteredString(titleHD, endX, g2d, y);
                y += 14;

                // --- THÔNG TIN HÀNH CHÍNH ---
                g2d.setFont(fontNormal);
                g2d.drawString("Mã HĐ   : " + hd.getMaHoaDon(), startX, y);
                y += 10;
                g2d.drawString("Ngày tạo: " + (hd.getThoiGianTao() != null ? hd.getThoiGianTao().format(dtf) : ""),
                        startX, y);
                y += 10;
                g2d.drawString("Thu ngân: " + (hd.getNhanVien() != null ? hd.getNhanVien().getTenNhanVien() : "N/A"),
                        startX, y);
                y += 10;

                // --- THÔNG TIN KHÁCH HÀNG ---
                String tenKH = "Khách vãng lai";
                String sdtKH = "";
                if (hd.getKhachHang() != null && !"KH_LE".equals(hd.getKhachHang().getMaKhachHang())) {
                    tenKH = hd.getKhachHang().getTenKhachHang();
                    sdtKH = hd.getKhachHang().getSdt();
                }
                g2d.drawString("Khách   : " + tenKH, startX, y);
                y += 10;
                if (!sdtKH.isEmpty()) {
                    g2d.drawString("SĐT     : " + sdtKH, startX, y);
                    y += 10;
                }

                // --- THÔNG TIN ĐƠN THUỐC (NẾU CÓ) ---
                if (hd.getDonThuoc() != null) {
                    g2d.drawString("Đơn thuốc: " + hd.getDonThuoc().getMaDonThuoc(), startX, y);
                    y += 10;
                    g2d.drawString("Bác sĩ   : " + hd.getDonThuoc().getTenBacSi(), startX, y);
                    y += 10;
                }

                drawSeparator("------------------------------------------------------------------", g2d, startX, y);
                y += 10;

                // --- TIÊU ĐỀ BẢNG CHI TIẾT ---
                g2d.setFont(fontHeader);
                g2d.drawString("TÊN SẢN PHẨM / DV", startX, y);
                g2d.drawString("SL", startX + 92, y);
                g2d.drawString("Đ.GIÁ", startX + 115, y);
                g2d.drawString("T.TIỀN", startX + 165, y);
                y += 10;
                drawSeparator("------------------------------------------------------------------", g2d, startX, y);
                y += 10;

                // --- DANH SÁCH CHI TIẾT SẢN PHẨM ---
                g2d.setFont(fontNormal);
                double tongTienHang = 0;
                double tongThue = 0;

                for (ChiTietHoaDon ct : dsChiTiet) {
                    DonViQuyDoi dv = ct.getDonViQuyDoi();
                    String tenSP = dv.getSanPham() != null ? dv.getSanPham().getTenSanPham() : "";
                    String tenDonVi = dv.getTenDonVi() != null ? dv.getTenDonVi().getMoTa() : dv.getMaDonVi();
                    int qty = ct.getSoLuong();
                    double price = ct.getDonGia();
                    double thuePt = dv.getSanPham() != null ? dv.getSanPham().getThue() : 0;
                    double priceWithTax = price * (1 + thuePt / 100);
                    double total = qty * priceWithTax;

                    if (ct.isLaQuaTangKem()) {
                        tenSP += " (Quà tặng)";
                        price = 0;
                        total = 0;
                    }

                    // Thiết kế dòng kép để tên sản phẩm hiển thị trọn vẹn, không bị cắt cụt
                    g2d.setFont(fontHeader);
                    g2d.drawString(tenSP, startX, y);
                    y += 10;

                    g2d.setFont(fontNormal);
                    g2d.drawString("  " + tenDonVi, startX, y);
                    g2d.drawString(String.valueOf(qty), startX + 92, y);
                    g2d.drawString(df.format(price), startX + 115, y);
                    g2d.drawString(df.format(total), startX + 165, y);
                    y += 12;

                    if (!ct.isLaQuaTangKem()) {
                        tongTienHang += qty * price;
                        tongThue += qty * price * (thuePt / 100);
                    }
                }

                drawSeparator("------------------------------------------------------------------", g2d, startX, y);
                y += 10;

                // --- PHẦN TỔNG HỢP TÀI CHÍNH ---
                double soTienGiam = 0;
                if (hd.getKhuyenMai() != null) {
                    KhuyenMai km = hd.getKhuyenMai();
                    if (tongTienHang >= km.getGiaTriDonHangToiThieu()) {
                        if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                            soTienGiam = tongTienHang * km.getKhuyenMaiPhanTram() / 100.0;
                        }
                    }
                }
                double finalTotal = tongTienHang + tongThue - soTienGiam;

                g2d.drawString("Cộng tiền hàng:", startX, y);
                g2d.drawString(df.format(tongTienHang), startX + 130, y);
                y += 10;

                g2d.drawString("Thuế GTGT:", startX, y);
                g2d.drawString(df.format(tongThue), startX + 130, y);
                y += 10;

                if (soTienGiam > 0) {
                    g2d.drawString("Khuyến mãi:", startX, y);
                    g2d.drawString("-" + df.format(soTienGiam), startX + 130, y);
                    y += 10;
                }

                drawSeparator("------------------------------------------------------------------", g2d, startX, y);
                y += 12;

                g2d.setFont(fontHeader);
                g2d.drawString("TỔNG CỘNG:", startX, y);
                g2d.drawString(df.format(finalTotal), startX + 130, y);
                y += 12;

                drawSeparator("------------------------------------------------------------------", g2d, startX, y);
                y += 10;

                // --- PHƯƠNG THỨC & TIỀN THỐI ---
                g2d.setFont(fontNormal);
                g2d.drawString("Thanh toán    : "
                        + (hd.getPhuongThucThanhToan() == PhuongThucThanhToan.TIEN_MAT ? "Tiền mặt" : "Chuyển khoản"),
                        startX, y);
                y += 10;

                if (hd.getPhuongThucThanhToan() == PhuongThucThanhToan.TIEN_MAT) {
                    g2d.drawString("Khách đưa     : " + df.format(tienKhachDua), startX, y);
                    y += 10;
                    g2d.drawString("Tiền thối lại : " + df.format(tienThoi), startX, y);
                    y += 12;
                }

                drawSeparator("=====================================", g2d, startX, y);
                y += 12;

                g2d.setFont(fontItalic);
                drawCenteredString("CẢM ƠN QUÝ KHÁCH & HẸN GẶP LẠI!", endX, g2d, y);

                return Printable.PAGE_EXISTS;
            }
        };

        // Kích hoạt hiển thị giao diện Xem trước hóa đơn & Hỏi có muốn in hay không
        SwingUtilities.invokeLater(() -> {
            hienThiPreviewVaHoiIn(receiptPrintable, pf, job);
        });
    }

    /** Phương thức dựng cửa sổ Xem trước hóa đơn (WYSWYG) và Hỏi in ấn */
    private static void hienThiPreviewVaHoiIn(Printable printable, PageFormat pageFormat, PrinterJob job) {
        JDialog dialog = new JDialog((Frame) null, "Xem Trước Hóa Đơn", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getContentPane().setBackground(new Color(240, 242, 245));

        POSReceiptPreviewPanel previewPanel = new POSReceiptPreviewPanel(printable, pageFormat);

        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(240, 242, 245));
        container.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tạo khung giả lập tờ giấy in hóa đơn có đổ bóng nhẹ
        JPanel paperWrapper = new JPanel(new BorderLayout());
        paperWrapper.setBackground(Color.WHITE);
        paperWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 220, 224), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        paperWrapper.add(previewPanel, BorderLayout.CENTER);

        container.add(paperWrapper);

        JScrollPane scrollPane = new JScrollPane(container);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(340, 500));

        dialog.add(scrollPane, BorderLayout.CENTER);

        // Thanh thao tác nút bấm ở chân cửa sổ
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(224, 224, 224)));

        JButton btnPrint = new JButton("In Hóa Đơn (Enter)");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPrint.setBackground(new Color(0, 200, 83)); // Màu xanh lá nổi bật
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setFocusPainted(false);
        btnPrint.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCancel = new JButton("Hủy bỏ (Esc)");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.setBackground(new Color(108, 117, 125)); // Màu xám tiêu chuẩn
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        bottomBar.add(btnPrint);
        bottomBar.add(btnCancel);
        dialog.add(bottomBar, BorderLayout.SOUTH);

        // Gắn sự kiện in ấn
        btnPrint.addActionListener(e -> {
            dialog.dispose();
            boolean ok = job.printDialog();
            if (ok) {
                try {
                    job.setPrintable(printable, pageFormat);
                    job.print();
                } catch (PrinterException ex) {
                    JOptionPane.showMessageDialog(null, "Lỗi khi in hóa đơn: " + ex.getMessage(), "Lỗi in ấn",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Gắn sự kiện hủy bỏ
        btnCancel.addActionListener(e -> dialog.dispose());

        // Thiết lập phím nóng Enter và Esc để thao tác cực nhanh không cần dùng chuột
        dialog.getRootPane().setDefaultButton(btnPrint);

        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        dialog.getRootPane().registerKeyboardAction(e -> dialog.dispose(), stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialog.pack();
        dialog.setSize(360, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private static void drawCenteredString(String s, int w, Graphics2D g2d, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2;
        g2d.drawString(s, x, y);
    }

    private static void drawSeparator(String s, Graphics2D g2d, int startX, int y) {
        g2d.drawString(s, startX, y);
    }

    /** Lớp Panel chuyên trách kết xuất (Render) hình ảnh hóa đơn in thử */
    private static class POSReceiptPreviewPanel extends JPanel {
        private final Printable printable;
        private final PageFormat pageFormat;

        public POSReceiptPreviewPanel(Printable printable, PageFormat pageFormat) {
            this.printable = printable;
            this.pageFormat = pageFormat;
            int w = (int) pageFormat.getWidth();
            int h = (int) pageFormat.getHeight();
            // Scale nhẹ 1.3x để xem trên màn hình lớn rõ nét hơn kích thước thực của giấy
            // POS 80mm
            double scale = 1.3;
            setPreferredSize(new Dimension((int) (w * scale), (int) (h * scale)));
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            double scale = 1.3;
            g2d.scale(scale, scale);

            try {
                // Kết xuất trực tiếp chính xác những gì sẽ in ra giấy
                printable.print(g2d, pageFormat, 0);
            } catch (PrinterException e) {
                e.printStackTrace();
            }
        }
    }
}
