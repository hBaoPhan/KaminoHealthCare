package com.example.gui.screens;

import com.example.gui.components.*;
import com.example.entity.TaiKhoan;
import com.example.entity.HoaDon;
import com.example.entity.Lo;
import com.example.entity.KhuyenMai;
import com.example.entity.SanPham;
import com.example.entity.CaLam;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.enums.LoaiHoaDon;
import com.example.entity.enums.LoaiSanPham;
import com.example.service.HoaDonService;
import com.example.service.LoService;
import com.example.service.KhuyenMaiService;
import com.example.service.SanPhamService;
import com.example.service.CaLamService;
import com.example.service.ChiTietHoaDonService;
import com.example.service.KhachHangService;
import com.example.entity.SuPhanBoLo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ManHinhChinhNhanVienPanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_STATS = new Font("Segoe UI", Font.BOLD, 22);

    private JLabel lblHoaDonDaLap, lblDoanhThuCa, lblKhachHang, lblCanhBao;
    private DefaultTableModel modelHoaDon, modelKhuyenMai, modelSanPhamMoi, modelLoHetHan;
    private DefaultPieDataset<String> donutDataset;

    private RoundedButton btnTroGiup, btnBanHang, btnTimThuoc, btnTimKhachHang, btnThanhToan;

    private HoaDonService hoaDonService = new HoaDonService();
    private LoService loService = new LoService();
    private KhuyenMaiService khuyenMaiService = new KhuyenMaiService();
    private SanPhamService sanPhamService = new SanPhamService();
    private CaLamService caLamService = new CaLamService();
    private ChiTietHoaDonService chiTietHoaDonService = new ChiTietHoaDonService();
    private KhachHangService khachHangService = new KhachHangService();

    private TaiKhoan taiKhoan;

    public ManHinhChinhNhanVienPanel(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
        setLayout(new BorderLayout(15, 15));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Top Panel (Stats)
        add(createTopPanel(), BorderLayout.NORTH);

        // 2. Center Panel (Tables and Charts)
        add(createCenterPanel(), BorderLayout.CENTER);

        // 3. Bottom Panel (Actions)
        add(createBottomPanel(), BorderLayout.SOUTH);

        refreshData();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);

        lblHoaDonDaLap = new JLabel("0");
        lblDoanhThuCa = new JLabel("0 VND");
        lblKhachHang = new JLabel("0");
        lblCanhBao = new JLabel("0 lô thuốc gần hết hạn");

        panel.add(createStatCard("Hóa đơn đã lập", lblHoaDonDaLap));
        panel.add(createStatCard("Doanh thu của ca", lblDoanhThuCa));
        panel.add(createStatCard("Khách hàng", lblKhachHang));
        panel.add(createStatCard("Cảnh báo", lblCanhBao));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel lblValue) {
        RoundedPanel card = new RoundedPanel(12, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValue.setFont(FONT_STATS);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblValue);

        return card;
    }

    private JPanel createCenterPanel() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Row 1: Left (Hóa đơn hôm nay) - Right (Lô thuốc hết hạn hôm nay)
        modelHoaDon = new DefaultTableModel(new String[] { "Mã Hóa đơn", "Tên KH", "Ngày tạo", "Khuyến mãi",
                "Người tạo", "Loại hóa đơn", "Tổng tiền", "Trạng thái thanh toán" }, 0);
        JPanel pnlHoaDon = createTableContainer("Hóa đơn hôm nay", modelHoaDon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 0.6;
        main.add(pnlHoaDon, gbc);

        modelLoHetHan = new DefaultTableModel(new String[] { "Mã Lô", "Tên thuốc", "HSD", "Trạng thái" }, 0);
        JPanel pnlLoHetHan = createTableContainer("Cảnh báo lô thuốc sắp hết hạn", modelLoHetHan);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 0.6;
        main.add(pnlLoHetHan, gbc);

        // Row 2: Bottom 3 sections
        JPanel bottomRow = new JPanel(new GridLayout(1, 3, 10, 0));
        bottomRow.setOpaque(false);

        modelKhuyenMai = new DefaultTableModel(new String[] { "Tên chương trình", "Chiết khấu", "Ngày kết thúc" }, 0);
        bottomRow.add(createTableContainer("Chương trình khuyến mãi hiện hành", modelKhuyenMai));

        modelSanPhamMoi = new DefaultTableModel(new String[] { "Mã SP", "Tên sản phẩm", "Phân loại", "Giá" }, 0);
        bottomRow.add(createTableContainer("Sản phẩm mới", modelSanPhamMoi));

        // Donut Chart Container
        RoundedPanel chartWrapper = new RoundedPanel(12, true);
        chartWrapper.setLayout(new BorderLayout());
        chartWrapper.setBackground(Color.WHITE);
        chartWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblChartTitle = new JLabel("Cơ cấu doanh thu theo Nhóm hàng");
        lblChartTitle.setFont(FONT_TITLE);
        chartWrapper.add(lblChartTitle, BorderLayout.NORTH);
        chartWrapper.add(createDonutChartPanel(), BorderLayout.CENTER);

        bottomRow.add(chartWrapper);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        main.add(bottomRow, gbc);

        return main;
    }

    private JPanel createTableContainer(String title, DefaultTableModel model) {
        RoundedPanel panel = new RoundedPanel(12, true);
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        panel.add(lblTitle, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDonutChartPanel() {
        donutDataset = new DefaultPieDataset<>();
        JFreeChart chart = ChartFactory.createRingChart(null, donutDataset, false, true, false);
        chart.setBackgroundPaint(Color.WHITE);

        org.jfree.chart.plot.RingPlot plot = (org.jfree.chart.plot.RingPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setSectionDepth(0.35);
        plot.setLabelGenerator(null); // Hide labels for clean look

        // Colors from image: Blue, Purple, Salmon, Cyan, Orange
        plot.setSectionPaint("Thuốc kê đơn", new Color(108, 117, 255));
        plot.setSectionPaint("Thuốc không kê đơn", new Color(147, 108, 255));
        plot.setSectionPaint("Thực phẩm chức năng", new Color(255, 148, 148));
        plot.setSectionPaint("Dụng cụ y tế", new Color(108, 218, 255));
        plot.setSectionPaint("Khác", new Color(255, 178, 108));

        return new ChartPanel(chart);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 20, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 70));

        btnTroGiup = createActionButton("Trợ giúp", "F1", new Color(0x6C757D));
        btnBanHang = createActionButton("Bán hàng", "F2", new Color(0x20C997));
        btnTimThuoc = createActionButton("Tìm kiếm thuốc", "F3", new Color(0x38D9A9));
        btnTimKhachHang = createActionButton("Tìm kiếm khách hàng", "F4", new Color(0x3DB5E0));
        btnThanhToan = createActionButton("Thanh toán", "F9", new Color(0x00C4EC));

        btnTroGiup.addActionListener(e -> navigateTo("Trợ Giúp"));
        btnBanHang.addActionListener(e -> navigateTo("Bán Hàng"));
        btnTimThuoc.addActionListener(e -> navigateTo("Quản Lý Sản Phẩm"));
        btnTimKhachHang.addActionListener(e -> navigateTo("Khách Hàng"));
        btnThanhToan.addActionListener(e -> navigateTo("Quản Lý Hóa Đơn"));

        panel.add(btnTroGiup);
        panel.add(btnBanHang);
        panel.add(btnTimThuoc);
        panel.add(btnTimKhachHang);
        panel.add(btnThanhToan);

        setupShortcuts();
        return panel;
    }

    private RoundedButton createActionButton(String text, String shortcut, Color color) {
        RoundedButton btn = new RoundedButton("");
        btn.setLayout(new BorderLayout());
        btn.setBackground(color);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblText = new JLabel(text);
        lblText.setForeground(Color.WHITE);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblShortcut = new JLabel(shortcut);
        lblShortcut.setForeground(new Color(255, 255, 255, 160));
        lblShortcut.setFont(new Font("Segoe UI", Font.BOLD, 18));

        btn.add(lblText, BorderLayout.WEST);
        btn.add(lblShortcut, BorderLayout.EAST);

        return btn;
    }

    private void setupShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("F1"), "F1_Action");
        actionMap.put("F1_Action", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnTroGiup.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F2"), "F2_Action");
        actionMap.put("F2_Action", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnBanHang.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F3"), "F3_Action");
        actionMap.put("F3_Action", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnTimThuoc.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F4"), "F4_Action");
        actionMap.put("F4_Action", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnTimKhachHang.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F9"), "F9_Action");
        actionMap.put("F9_Action", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btnThanhToan.doClick();
            }
        });
    }

    private void navigateTo(String tabName) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JFrame) {
            Component[] comps = ((JFrame) window).getContentPane().getComponents();
            for (Component c : comps) {
                if (c instanceof JPanel && ((JPanel) c).getLayout() instanceof CardLayout) {
                    ((CardLayout) ((JPanel) c).getLayout()).show((JPanel) c, tabName);
                    break;
                }
            }
        }
    }

    public void refreshData() {
        LocalDate today = LocalDate.now();
        DecimalFormat df = new DecimalFormat("###,###,### VND");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 1. Stats and Hoa Don Hom Nay
        List<HoaDon> dsHoaDon = hoaDonService.timKiem(null, today);
        modelHoaDon.setRowCount(0);
        int soHoaDon = 0;

        // Xác định ca hiện tại để lọc doanh thu trong ca
        CaLam caHienTai = caLamService.layCaHienTai(taiKhoan.getNhanVien().getMaNhanVien());
        LocalDateTime shiftStart = (caHienTai != null) ? caHienTai.getGioBatDau() : today.atStartOfDay();

        // Tính doanh thu ca theo công thức đúng: DT(Bán/Đổi) - Tiền trả(Trả) - Giá vốn
        double tongBanDoiCa = 0;
        double tongTraHangCa = 0;
        double tongGiaVonCa = 0;

        for (HoaDon hd : dsHoaDon) {
            // Đếm số hóa đơn của nhân viên hiện tại
            boolean isMyHD = hd.getNhanVien() != null
                    && hd.getNhanVien().getMaNhanVien().equals(taiKhoan.getNhanVien().getMaNhanVien());
            if (isMyHD)
                soHoaDon++;

            // Populate bảng hóa đơn hôm nay
            modelHoaDon.addRow(new Object[] {
                    hd.getMaHoaDon(),
                    hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "Khách lẻ",
                    hd.getThoiGianTao().format(dtf),
                    hd.getKhuyenMai() != null ? hd.getKhuyenMai().getTenKhuyenMai() : "Không",
                    hd.getNhanVien() != null ? hd.getNhanVien().getTenNhanVien() : "",
                    hd.getLoaiHoaDon().getMoTa(),
                    df.format(hd.tinhTongTienThanhToan()),
                    hd.isTrangThaiThanhToan() ? "Đã thanh toán" : "Chưa thanh toán"
            });

            // Chỉ tính doanh thu ca cho hóa đơn ĐÃ THANH TOÁN và TRONG CA HIỆN TẠI
            if (!hd.isTrangThaiThanhToan())
                continue;
            if (hd.getThoiGianTao() == null || !hd.getThoiGianTao().isAfter(shiftStart))
                continue;

            double finalTotal = hd.tinhTongTienThanhToan();
            double giaVonHD = 0;

            // Tải chi tiết nếu chưa có để tính giá vốn
            if (hd.getDsChiTiet() == null) {
                hd.setDsChiTiet(chiTietHoaDonService.layTheoMaHoaDon(hd.getMaHoaDon()));
            }
            if (hd.getDsChiTiet() != null) {
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    if (ct.getDsPhanBoLo() != null) {
                        for (SuPhanBoLo spbl : ct.getDsPhanBoLo()) {
                            if (spbl.getLo() != null) {
                                double giaNhapLoo = spbl.getLo().getGiaNhap();
                                int slTonLo = spbl.getLo().getSoLuongSanPham();
                                double giaNhapDonVi = slTonLo > 0 ? (giaNhapLoo / slTonLo) : 0;
                                giaVonHD += spbl.getSoLuong() * giaNhapDonVi;
                            }
                        }
                    }
                }
            }

            LoaiHoaDon loai = hd.getLoaiHoaDon();
            if (loai == LoaiHoaDon.BAN_HANG || loai == LoaiHoaDon.DOI_HANG) {
                tongBanDoiCa += finalTotal;
                tongGiaVonCa += giaVonHD;
            } else if (loai == LoaiHoaDon.TRA_HANG) {
                tongTraHangCa += finalTotal;
                tongGiaVonCa -= giaVonHD;
            }
        }

        // Lợi nhuận của ca = Doanh thu bán/đổi - Tiền trả hàng - Giá vốn
        double loiNhuanCa = tongBanDoiCa - tongTraHangCa - tongGiaVonCa;

        lblHoaDonDaLap.setText(String.valueOf(soHoaDon));
        lblDoanhThuCa.setText(df.format(loiNhuanCa));
        lblKhachHang.setText(String.valueOf(khachHangService.layTatCa().size()));

        // 2. Lô thuốc hết hạn hôm nay & Cảnh báo
        List<Lo> dsLo = loService.layTatCa();
        modelLoHetHan.setRowCount(0);
        int loGanHetHan = 0;
        int loNgungBan = 0;
        for (Lo lo : dsLo) {
            if (lo.getNgayHetHan() == null || lo.getSanPham() == null)
                continue;

            LocalDate ngayHetHan = lo.getNgayHetHan();
            String tenThuoc = lo.getSanPham().getTenSanPham();
            String ngayFormatted = ngayHetHan.format(dfDate);

            long soNgay = java.time.Duration.between(today.atStartOfDay(), ngayHetHan.atStartOfDay()).toDays();

            // Quá hạn
            if (soNgay <= 0) {
                modelLoHetHan.addRow(new Object[] {
                        lo.getMaLo(), tenThuoc, ngayFormatted, "HẾT HẠN"
                });
                // Sắp hết hạn: từ 1 ngày tới 1 tháng (<= 30 ngày)
            } else if (soNgay <= 30) {
                loNgungBan++;
                modelLoHetHan.addRow(new Object[] {
                        lo.getMaLo(), tenThuoc, ngayFormatted, "NGỪNG BÁN"
                });
            } else if (soNgay <= 37) {
                loGanHetHan++;
                modelLoHetHan.addRow(new Object[] {
                        lo.getMaLo(), tenThuoc, ngayFormatted, "SẮP HẾT"
                });
            }
        }

        // Cập nhật nhãn cảnh báo
        StringBuilder canhBao = new StringBuilder();
        if (loGanHetHan > 0)
            canhBao.append(loGanHetHan).append(" lô sắp hết");
        if (loNgungBan > 0) {
            if (canhBao.length() > 0)
                canhBao.append(", ");
            canhBao.append(loNgungBan).append(" lô ngưng bán");
        }
        lblCanhBao.setText(canhBao.length() > 0 ? canhBao.toString() : "Không có cảnh báo");

        // 3. Khuyến mãi hiện hành
        List<KhuyenMai> dsKM = khuyenMaiService.layTatCa();
        modelKhuyenMai.setRowCount(0);
        for (KhuyenMai km : dsKM) {
            if (km.getThoiGianBatDau().toLocalDate().isBefore(today.plusDays(1))
                    && km.getThoiGianKetThuc().toLocalDate().isAfter(today.minusDays(1))) {
                modelKhuyenMai.addRow(new Object[] { km.getTenKhuyenMai(), km.getKhuyenMaiPhanTram() + "%",
                        km.getThoiGianKetThuc().format(dtf) });
            }
        }

        // 4. Sản phẩm mới
        List<SanPham> dsSP = sanPhamService.layTatCa();
        modelSanPhamMoi.setRowCount(0);
        dsSP.stream().sorted((s1, s2) -> s2.getMaSanPham().compareTo(s1.getMaSanPham())).limit(10).forEach(sp -> {
            modelSanPhamMoi.addRow(new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), sp.getLoaiSanPham().name(),
                    df.format(sp.getDonGiaCoBan()) });
        });

        // 5. Donut Chart
        updateDonutChart(dsHoaDon);
    }

    private void updateDonutChart(List<HoaDon> dsHoaDon) {
        donutDataset.clear();
        double etc = 0, otc = 0, tpcn = 0, dcyt = 0, khac = 0;

        for (HoaDon hd : dsHoaDon) {
            if (!hd.isTrangThaiThanhToan())
                continue;
            hd.setDsChiTiet(chiTietHoaDonService.layTheoMaHoaDon(hd.getMaHoaDon()));
            for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                if (ct.getDonViQuyDoi() == null || ct.getDonViQuyDoi().getSanPham() == null)
                    continue;
                LoaiSanPham loai = ct.getDonViQuyDoi().getSanPham().getLoaiSanPham();
                double thanhTien = ct.tinhThanhTien();
                if (loai == LoaiSanPham.ETC)
                    etc += thanhTien;
                else if (loai == LoaiSanPham.OTC)
                    otc += thanhTien;
                else if (loai == LoaiSanPham.TPCN)
                    tpcn += thanhTien;
                else
                    khac += thanhTien;
            }
        }

        if (etc > 0)
            donutDataset.setValue("Thuốc kê đơn", etc);
        if (otc > 0)
            donutDataset.setValue("Thuốc không kê đơn", otc);
        if (tpcn > 0)
            donutDataset.setValue("Thực phẩm chức năng", tpcn);
        if (khac > 0)
            donutDataset.setValue("Khác", khac);

        if (donutDataset.getItemCount() == 0)
            donutDataset.setValue("Chưa có dữ liệu", 1);
    }
}
