package com.example.gui.screens;

import com.example.gui.components.*;

import com.example.entity.TaiKhoan;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.Lo;
import com.example.entity.enums.LoaiHoaDon;
import com.example.entity.enums.LoaiSanPham;
import com.example.service.HoaDonService;
import com.example.service.LoService;
import com.example.service.ChiTietHoaDonService;
import com.example.entity.SuPhanBoLo;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.DecimalFormat;
import java.util.List;

public class ManHinhChinhPanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255); // #F1F6FF
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_STATS = new Font("Segoe UI", Font.BOLD, 24);

    private JLabel lblHoaDonHomNay;
    private JLabel lblDoanhThuHomNay;
    private JLabel lblLoiNhuan;
    private JLabel lblCanhBao;

    private DefaultTableModel modelHoaDon;
    private DefaultTableModel modelLoThuoc;

    private DefaultCategoryDataset barDataset;
    private CustomPieChart donutChart;

    private RoundedButton btnTroGiup;
    private RoundedButton btnBanHang;
    private RoundedButton btnTimThuoc;
    private RoundedButton btnTimKhachHang;
    private RoundedButton btnThanhToan;

    private HoaDonService hoaDonService;
    private LoService loService;
    private ChiTietHoaDonService chiTietHoaDonService;

    public ManHinhChinhPanel(TaiKhoan taiKhoan) {
        hoaDonService = new HoaDonService();
        loService = new LoService();
        chiTietHoaDonService = new ChiTietHoaDonService();

        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        loadThongKeData();
        layDuLieuChoHoatDongGanDay();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);

        lblHoaDonHomNay = new JLabel("0");
        lblDoanhThuHomNay = new JLabel("0 VND");
        lblLoiNhuan = new JLabel("0 VND");
        lblCanhBao = new JLabel("Không có cảnh báo");
        lblCanhBao.setFont(new Font("Segoe UI", Font.BOLD, 14));

        panel.add(createStatCard("Hóa đơn hôm nay", lblHoaDonHomNay));
        panel.add(createStatCard("Doanh thu hôm nay", lblDoanhThuHomNay));
        panel.add(createStatCard("Lợi Nhuận hôm nay", lblLoiNhuan));
        panel.add(createStatCard("Cảnh báo", lblCanhBao));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel lblValue) {
        RoundedPanel card = new RoundedPanel(16, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValue.setFont(FONT_STATS);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);

        return card;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 0);

        JPanel tablePanel = new JPanel(new GridBagLayout());
        tablePanel.setOpaque(false);
        GridBagConstraints tgbc = new GridBagConstraints();
        tgbc.fill = GridBagConstraints.BOTH;
        tgbc.weighty = 1.0;

        modelHoaDon = new DefaultTableModel(new String[] { "Mã Hóa đơn", "Tên KH",
                "Ngày tạo", "Khuyến mãi", "Người tạo", "Loại hóa đơn", "Tổng tiền", "Trạng thái" }, 0);
        JPanel leftTablePanel = createTableContainer("Hóa đơn hôm nay", modelHoaDon);
        tgbc.weightx = 0.7;
        tgbc.gridx = 0;
        tgbc.insets = new Insets(0, 0, 0, 10);
        tablePanel.add(leftTablePanel, tgbc);

        modelLoThuoc = new DefaultTableModel(new String[] { "Mã Lô", "Tên thuốc", "HSD", "Trạng thái" }, 0);
        JPanel rightTablePanel = createTableContainer("Cảnh báo lô thuốc sắp hết hạn", modelLoThuoc);
        tgbc.weightx = 0.3;
        tgbc.gridx = 1;
        tgbc.insets = new Insets(0, 10, 0, 0);
        tablePanel.add(rightTablePanel, tgbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        panel.add(tablePanel, gbc);

        JPanel chartContainer = new JPanel();
        chartContainer.setOpaque(false);
        chartContainer.setLayout(new GridLayout(1, 2, 20, 0));

        RoundedPanel barChartWrapper = new RoundedPanel(16, true);
        barChartWrapper.setLayout(new BorderLayout());
        barChartWrapper.setBackground(Color.WHITE);
        barChartWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        barChartWrapper.add(createBarChartPanel(), BorderLayout.CENTER);

        RoundedPanel donutChartWrapper = new RoundedPanel(16, true);
        donutChartWrapper.setLayout(new BorderLayout(0, 10));
        donutChartWrapper.setBackground(Color.WHITE);
        donutChartWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel lblDonutTitle = new JLabel("Cơ cấu doanh thu theo Nhóm hàng", SwingConstants.CENTER);
        lblDonutTitle.setFont(FONT_TITLE);
        donutChartWrapper.add(lblDonutTitle, BorderLayout.NORTH);
        donutChartWrapper.add(createDonutChartPanel(), BorderLayout.CENTER);

        chartContainer.add(barChartWrapper);
        chartContainer.add(donutChartWrapper);

        gbc.gridy = 1;
        gbc.weighty = 0.5;
        panel.add(chartContainer, gbc);

        return panel;
    }

    private JPanel createTableContainer(String title, DefaultTableModel model) {
        RoundedPanel panel = new RoundedPanel(16, true);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        panel.add(lblTitle, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBarChartPanel() {
        barDataset = new DefaultCategoryDataset();

        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu theo giờ trong ngày",
                null, null,
                barDataset, PlotOrientation.VERTICAL,
                false, true, false);
        chart.setBackgroundPaint(Color.WHITE);

        org.jfree.chart.plot.CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        org.jfree.chart.axis.CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(org.jfree.chart.axis.CategoryLabelPositions.UP_45);

        org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) plot
                .getRenderer();
        renderer.setSeriesPaint(0, new Color(56, 182, 255));
        renderer.setBarPainter(new org.jfree.chart.renderer.category.StandardBarPainter());

        return new ChartPanel(chart);
    }

    private JPanel createDonutChartPanel() {
        donutChart = new CustomPieChart();
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(donutChart, BorderLayout.CENTER);
        return p;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 80));

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
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblText = new JLabel(text);
        lblText.setForeground(Color.WHITE);
        lblText.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JLabel lblShortcut = new JLabel(shortcut);
        lblShortcut.setForeground(new Color(255, 255, 255, 180));
        lblShortcut.setFont(new Font("Segoe UI", Font.BOLD, 20));

        btn.add(lblText, BorderLayout.WEST);
        btn.add(lblShortcut, BorderLayout.EAST);

        return btn;
    }

    private void setupShortcuts() {
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("F1"), "F1_Action");
        actionMap.put("F1_Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnTroGiup.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F2"), "F2_Action");
        actionMap.put("F2_Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnBanHang.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F3"), "F3_Action");
        actionMap.put("F3_Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnTimThuoc.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F4"), "F4_Action");
        actionMap.put("F4_Action", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnTimKhachHang.doClick();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke("F9"), "F9_Action");
        actionMap.put("F9_Action", new AbstractAction() {
            @Override
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

    public void loadThongKeData() {
        LocalDate today = LocalDate.now();
        List<HoaDon> dsHoaDon = hoaDonService.timKiem(null, today);

        int soHoaDon = dsHoaDon.size();
        double doanhThu = 0;
        double tongGiaVon = 0;

        double[] doanhThuTheoGio = new double[24];
        double dtETC = 0;
        double dtOTC = 0;
        double dtTPCN = 0;
        double dtMyPham = 0;

        for (HoaDon hd : dsHoaDon) {
            // Chỉ tính doanh thu/lợi nhuận cho các hóa đơn ĐÃ THANH TOÁN
            if (!hd.isTrangThaiThanhToan())
                continue;

            hd.setDsChiTiet(chiTietHoaDonService.layTheoMaHoaDon(hd.getMaHoaDon()));
            double finalTotal = tinhTongTienThucTeHoaDon(hd);
            LoaiHoaDon loai = hd.getLoaiHoaDon();

            if (loai == LoaiHoaDon.DOI_HANG && hd.getHoaDonDoiTra() != null) {
                String maGoc = hd.getHoaDonDoiTra().getMaHoaDon();
                HoaDon hdGoc = hoaDonService.timTheoMa(maGoc);
                if (hdGoc != null) {
                    hdGoc.setDsChiTiet(chiTietHoaDonService.layTheoMaHoaDon(maGoc));
                    finalTotal -= tinhTongTienThucTeHoaDon(hdGoc);
                }
            }

            double cost = tinhGiaVonHoaDon(hd);

            double tienHD = 0;
            if (loai == LoaiHoaDon.BAN_HANG || loai == LoaiHoaDon.DOI_HANG || loai == null) {
                tienHD = finalTotal;
                doanhThu += finalTotal;
                tongGiaVon += cost;
            } else if (loai == LoaiHoaDon.TRA_HANG) {
                tienHD = -finalTotal;
                doanhThu -= finalTotal;
                tongGiaVon -= cost;
            }

            if (hd.getThoiGianTao() != null) {
                int gio = hd.getThoiGianTao().getHour();
                doanhThuTheoGio[gio] += tienHD;
            }

            double tongHienTai = tinhTongTienThucTeHoaDon(hd);
            if (hd.getDsChiTiet() != null) {
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    if (ct.getDonViQuyDoi() != null && ct.getDonViQuyDoi().getSanPham() != null) {
                        // Phân bổ doanh thu của chi tiết theo tỷ lệ đóng góp vào tổng hóa đơn
                        // Nếu là Đổi/Trả thì dùng doanh thu hiệu chỉnh
                        double tienCT = ct.tinhThanhTien();
                        if (tongHienTai > 0) {
                            tienCT = (tienCT / tongHienTai) * tienHD;
                        } else if (tienHD < 0) {
                            // Trường hợp hiếm khi tổng hiện tại = 0 nhưng có doanh thu âm
                            tienCT = tienHD / hd.getDsChiTiet().size();
                        }

                        LoaiSanPham pl = ct.getDonViQuyDoi().getSanPham().getLoaiSanPham();
                        if (pl == LoaiSanPham.ETC)
                            dtETC += tienCT;
                        else if (pl == LoaiSanPham.OTC)
                            dtOTC += tienCT;
                        else if (pl == LoaiSanPham.TPCN)
                            dtTPCN += tienCT;
                        else if (pl == LoaiSanPham.MY_PHAM)
                            dtMyPham += tienCT;
                    }
                }
            }
        }

        double loiNhuan = doanhThu - tongGiaVon;

        lblHoaDonHomNay.setText(String.valueOf(soHoaDon));

        DecimalFormat df = new DecimalFormat("###,###,### VND");
        lblDoanhThuHomNay.setText(doanhThu == 0 ? "0 VND" : df.format(doanhThu));
        lblLoiNhuan.setText(loiNhuan == 0 ? "0 VND" : df.format(loiNhuan));

        List<Lo> dsLo = loService.layTatCa();
        int loGanHetHan = 0;
        int loNgungBan = 0;
        for (Lo lo : dsLo) {
            if (lo.getNgayHetHan() != null) {
                long soNgay = java.time.Duration.between(today.atStartOfDay(), lo.getNgayHetHan().atStartOfDay())
                        .toDays();
                if (soNgay > 0) {
                    if (soNgay <= 30) {
                        loNgungBan++;
                    } else if (soNgay <= 37) {
                        loGanHetHan++;
                    }
                }
            }
        }
        StringBuilder canhBao = new StringBuilder();
        if (loGanHetHan > 0)
            canhBao.append(loGanHetHan).append(" lô sắp hết");
        if (loNgungBan > 0) {
            if (canhBao.length() > 0)
                canhBao.append(", ");
            canhBao.append(loNgungBan).append(" lô ngưng bán");
        }
        lblCanhBao.setText(canhBao.length() > 0 ? canhBao.toString() : "Không có cảnh báo");

        // Update charts
        if (barDataset != null) {
            barDataset.clear();
            for (int i = 6; i <= 23; i++) {
                barDataset.addValue(doanhThuTheoGio[i], "Doanh thu", String.format("%02d:00", i));
            }
        }

        if (donutChart != null) {
            List<String> labels = new java.util.ArrayList<>();
            List<Double> values = new java.util.ArrayList<>();
            
            if (dtETC > 0) { labels.add("Thuốc kê đơn"); values.add(dtETC); }
            if (dtOTC > 0) { labels.add("Thuốc không kê đơn"); values.add(dtOTC); }
            if (dtTPCN > 0) { labels.add("Thực phẩm chức năng"); values.add(dtTPCN); }
            if (dtMyPham > 0) { labels.add("Mỹ phẩm"); values.add(dtMyPham); }

            donutChart.setValues(labels, values);
        }
    }

    public void layDuLieuChoHoatDongGanDay() {
        LocalDate today = LocalDate.now();
        List<HoaDon> dsHoaDon = hoaDonService.timKiem(null, today);

        modelHoaDon.setRowCount(0);
        DecimalFormat df = new DecimalFormat("###,###,### VND");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (HoaDon hd : dsHoaDon) {
            hd.setDsChiTiet(chiTietHoaDonService.layTheoMaHoaDon(hd.getMaHoaDon()));
            modelHoaDon.addRow(new Object[] {
                    hd.getMaHoaDon(),
                    hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "Khách lẻ",
                    hd.getThoiGianTao() != null ? hd.getThoiGianTao().format(dtf) : "",
                    hd.getKhuyenMai() != null ? hd.getKhuyenMai().getTenKhuyenMai() : "Không có",
                    hd.getNhanVien() != null ? hd.getNhanVien().getTenNhanVien() : "",
                    hd.getLoaiHoaDon() != null ? hd.getLoaiHoaDon().getMoTa() : "",
                    df.format(tinhTongTienThucTeHoaDon(hd)),
                    hd.isTrangThaiThanhToan() ? "Đã thanh toán" : "Chưa thanh toán"
            });
        }

        List<Lo> dsLoTable = loService.layTatCa();
        modelLoThuoc.setRowCount(0);
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (Lo lo : dsLoTable) {
            if (lo.getNgayHetHan() == null || lo.getSanPham() == null)
                continue;

            LocalDate ngayHetHan = lo.getNgayHetHan();
            String tenThuoc = lo.getSanPham().getTenSanPham();
            String ngayFormatted = ngayHetHan.format(dtfDate);

            long soNgay = java.time.Duration.between(today.atStartOfDay(), ngayHetHan.atStartOfDay()).toDays();

            // Quá hạn
            if (soNgay <= 0) {
                modelLoThuoc.addRow(new Object[] {
                        lo.getMaLo(), tenThuoc, ngayFormatted, "HẾT HẠN"
                });
                // Sắp hết hạn: từ 1 ngày tới 1 tháng (<= 30 ngày)
            } else if (soNgay <= 30) {
                modelLoThuoc.addRow(new Object[] {
                        lo.getMaLo(), tenThuoc, ngayFormatted, "NGỪNG BÁN"
                });
            } else if (soNgay <= 37) {
                modelLoThuoc.addRow(new Object[] {
                        lo.getMaLo(), tenThuoc, ngayFormatted, "SẮP HẾT HẠN"
                });
            }
        }
    }

    private double tinhGiaVonHoaDon(HoaDon hd) {
        double cost = 0.0;

        // Nếu là hóa đơn bán hàng hoặc trả hàng: cost bằng SuPhanBo (thì +)
        if (hd.getLoaiHoaDon() != LoaiHoaDon.DOI_HANG) {
            if (hd.getDsChiTiet() != null) {
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    if (ct.getDsPhanBoLo() != null) {
                        for (SuPhanBoLo spbl : ct.getDsPhanBoLo()) {
                            if (spbl.getLo() != null) {
                                double giaNhapLoo = spbl.getLo().getGiaNhap();
                                int slBanDau = loService.tinhSoLuongNhapBanDau(spbl.getLo().getMaLo());
                                double giaNhapDonVi = slBanDau > 0 ? (giaNhapLoo / slBanDau) : 0;
                                cost += spbl.getSoLuongPhanBo() * giaNhapDonVi;
                            }
                        }
                    }
                }
            }
            return cost;
        }

        // Nếu là hóa đơn đổi hàng (hd_đổi)
        if (hd.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG && hd.getHoaDonDoiTra() != null) {
            String maGoc = hd.getHoaDonDoiTra().getMaHoaDon();
            HoaDon hdGoc = hoaDonService.timTheoMa(maGoc);
            if (hdGoc != null) {
                hdGoc.setDsChiTiet(chiTietHoaDonService.layTheoMaHoaDon(maGoc));

                double refundedCost = 0.0;
                double addedCost = 0.0;

                // 1. Tính vốn hoàn lại (từ các sản phẩm trả) dựa vào SoLuongBan của
                // ChiTietHoaDon
                if (hdGoc.getDsChiTiet() != null) {
                    for (ChiTietHoaDon ctGoc : hdGoc.getDsChiTiet()) {
                        String maDv = ctGoc.getDonViQuyDoi().getMaDonVi();
                        int qtyGoc = ctGoc.getSoLuongBan();
                        int qtyMoi = 0;

                        if (hd.getDsChiTiet() != null) {
                            for (ChiTietHoaDon ctMoi : hd.getDsChiTiet()) {
                                if (ctMoi.getDonViQuyDoi().getMaDonVi().equals(maDv)) {
                                    qtyMoi = ctMoi.getSoLuongBan();
                                    break;
                                }
                            }
                        }

                        if (qtyGoc > qtyMoi) {
                            int qtyTra = qtyGoc - qtyMoi; // Lượng trả lại
                            double totalCostGoc = 0.0;
                            int totalQtyGoc = 0;
                            if (ctGoc.getDsPhanBoLo() != null) {
                                for (SuPhanBoLo spbl : ctGoc.getDsPhanBoLo()) {
                                    if (spbl.getLo() != null) {
                                        double giaNhapLoo = spbl.getLo().getGiaNhap();
                                        int slBanDau = loService.tinhSoLuongNhapBanDau(spbl.getLo().getMaLo());
                                        double giaNhapDonVi = slBanDau > 0 ? (giaNhapLoo / slBanDau) : 0;
                                        totalCostGoc += spbl.getSoLuongPhanBo() * giaNhapDonVi;
                                        totalQtyGoc += spbl.getSoLuongPhanBo();
                                    }
                                }
                            }
                            double avgUnitCost = (totalQtyGoc > 0) ? (totalCostGoc / totalQtyGoc) : 0.0;
                            int qtyTraBase = qtyTra * ctGoc.getDonViQuyDoi().getHeSoQuyDoi();
                            refundedCost += qtyTraBase * avgUnitCost;
                        }
                    }
                }

                // 2. Tính vốn mua thêm (từ các sản phẩm mới)
                if (hd.getDsChiTiet() != null) {
                    for (ChiTietHoaDon ctMoi : hd.getDsChiTiet()) {
                        String maDv = ctMoi.getDonViQuyDoi().getMaDonVi();
                        int qtyMoi = ctMoi.getSoLuongBan();
                        int qtyGoc = 0;

                        if (hdGoc.getDsChiTiet() != null) {
                            for (ChiTietHoaDon ctGoc : hdGoc.getDsChiTiet()) {
                                if (ctGoc.getDonViQuyDoi().getMaDonVi().equals(maDv)) {
                                    qtyGoc = ctGoc.getSoLuongBan();
                                    break;
                                }
                            }
                        }

                        if (qtyMoi > qtyGoc) {
                            int qtyThem = qtyMoi - qtyGoc;
                            double totalCostMoi = 0.0;
                            int totalQtyMoi = 0;
                            if (ctMoi.getDsPhanBoLo() != null) {
                                for (SuPhanBoLo spbl : ctMoi.getDsPhanBoLo()) {
                                    if (spbl.getLo() != null) {
                                        double giaNhapLoo = spbl.getLo().getGiaNhap();
                                        int slBanDau = loService.tinhSoLuongNhapBanDau(spbl.getLo().getMaLo());
                                        double giaNhapDonVi = slBanDau > 0 ? (giaNhapLoo / slBanDau) : 0;
                                        totalCostMoi += spbl.getSoLuongPhanBo() * giaNhapDonVi;
                                        totalQtyMoi += spbl.getSoLuongPhanBo();
                                    }
                                }
                            }
                            double avgUnitCost = (totalQtyMoi > 0) ? (totalCostMoi / totalQtyMoi) : 0.0;
                            int qtyThemBase = qtyThem * ctMoi.getDonViQuyDoi().getHeSoQuyDoi();
                            addedCost += qtyThemBase * avgUnitCost;
                        }
                    }
                }

                cost = addedCost - refundedCost;
            }
        }
        return cost;
    }

    private double tinhTongTienThucTeHoaDon(HoaDon h) {
        double dTongTien = h.tinhTongTienThanhToan();
        if (h.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG && h.getHoaDonDoiTra() != null
                && h.getHoaDonDoiTra().getMaHoaDon() != null) {
            try {
                String maHDGocRef = h.getHoaDonDoiTra().getMaHoaDon();
                List<ChiTietHoaDon> dsChiTiet = chiTietHoaDonService.layTheoMaHoaDon(h.getMaHoaDon());
                List<ChiTietHoaDon> dsGoc = chiTietHoaDonService.layTheoMaHoaDon(maHDGocRef);

                HoaDon hdGocRef = hoaDonService.timTheoMa(maHDGocRef);
                double kmMoiPt = (h.getKhuyenMai() != null
                        && h.getKhuyenMai().getLoaiKhuyenMai() == com.example.entity.enums.LoaiKhuyenMai.PHAN_TRAM)
                                ? h.getKhuyenMai().getKhuyenMaiPhanTram()
                                : 0;
                double tiLeGiamGoc = (hdGocRef != null && hdGocRef.getKhuyenMai() != null
                        && hdGocRef.getKhuyenMai()
                                .getLoaiKhuyenMai() == com.example.entity.enums.LoaiKhuyenMai.PHAN_TRAM)
                                        ? hdGocRef.getKhuyenMai().getKhuyenMaiPhanTram()
                                        : 0;

                double tongTienHang = 0;
                double soTienGiam = 0;
                double soTienKMGoc = 0;
                double tongThue = 0;

                for (ChiTietHoaDon ctDoi : dsChiTiet) {
                    if (ctDoi.isLaQuaTangKem())
                        continue;
                    tongTienHang += ctDoi.getSoLuongBan() * ctDoi.getDonGia();
                }

                // Tính KM mới (soTienGiam)
                if (h.getKhuyenMai() != null
                        && h.getKhuyenMai().getLoaiKhuyenMai() == com.example.entity.enums.LoaiKhuyenMai.PHAN_TRAM) {
                    double tongTienMuaThem = 0;
                    for (ChiTietHoaDon ctDoi : dsChiTiet) {
                        if (ctDoi.isLaQuaTangKem())
                            continue;
                        String maSP = ctDoi.getDonViQuyDoi().getSanPham() != null
                                ? ctDoi.getDonViQuyDoi().getSanPham().getMaSanPham()
                                : "";
                        String tenDV = ctDoi.getDonViQuyDoi().getTenDonVi() != null
                                ? ctDoi.getDonViQuyDoi().getTenDonVi().name()
                                : "";
                        int slGoc = 0;
                        for (ChiTietHoaDon ctGoc : dsGoc) {
                            if (ctGoc.isLaQuaTangKem())
                                continue;
                            String maSPGoc = ctGoc.getDonViQuyDoi().getSanPham() != null
                                    ? ctGoc.getDonViQuyDoi().getSanPham().getMaSanPham()
                                    : "";
                            String tenDVGoc = ctGoc.getDonViQuyDoi().getTenDonVi() != null
                                    ? ctGoc.getDonViQuyDoi().getTenDonVi().name()
                                    : "";
                            if (maSPGoc.equals(maSP) && tenDVGoc.equals(tenDV)) {
                                slGoc += ctGoc.getSoLuongBan();
                            }
                        }
                        int slMuaThem = Math.max(0, ctDoi.getSoLuongBan() - slGoc);
                        tongTienMuaThem += slMuaThem * ctDoi.getDonGia();
                    }
                    if (tongTienMuaThem >= h.getKhuyenMai().getGiaTriDonHangToiThieu()) {
                        soTienGiam = tongTienMuaThem * (kmMoiPt / 100.0);
                    }
                }

                // Tính KM gốc (soTienKMGoc)
                double tongTienSPCu = 0;
                for (ChiTietHoaDon ctDoi : dsChiTiet) {
                    if (ctDoi.isLaQuaTangKem())
                        continue;
                    String maSPDoi = ctDoi.getDonViQuyDoi().getSanPham() != null
                            ? ctDoi.getDonViQuyDoi().getSanPham().getMaSanPham()
                            : "";
                    String tenDVDoi = ctDoi.getDonViQuyDoi().getTenDonVi() != null
                            ? ctDoi.getDonViQuyDoi().getTenDonVi().name()
                            : "";
                    int slTrongGoc = 0;
                    for (ChiTietHoaDon ctGoc : dsGoc) {
                        if (ctGoc.isLaQuaTangKem())
                            continue;
                        String maSPGocX = ctGoc.getDonViQuyDoi().getSanPham() != null
                                ? ctGoc.getDonViQuyDoi().getSanPham().getMaSanPham()
                                : "";
                        String tenDVGocX = ctGoc.getDonViQuyDoi().getTenDonVi() != null
                                ? ctGoc.getDonViQuyDoi().getTenDonVi().name()
                                : "";
                        if (maSPGocX.equals(maSPDoi) && tenDVGocX.equals(tenDVDoi)) {
                            slTrongGoc += ctGoc.getSoLuongBan();
                        }
                    }
                    int slCuDangDoi = Math.min(ctDoi.getSoLuongBan(), slTrongGoc);
                    tongTienSPCu += slCuDangDoi * ctDoi.getDonGia();
                }
                soTienKMGoc = tongTienSPCu * (tiLeGiamGoc / 100.0);

                // Tính Thuế
                for (ChiTietHoaDon ctDoi : dsChiTiet) {
                    if (ctDoi.isLaQuaTangKem())
                        continue;
                    String maSPDoi = ctDoi.getDonViQuyDoi().getSanPham() != null
                            ? ctDoi.getDonViQuyDoi().getSanPham().getMaSanPham()
                            : "";
                    String tenDVDoi = ctDoi.getDonViQuyDoi().getTenDonVi() != null
                            ? ctDoi.getDonViQuyDoi().getTenDonVi().name()
                            : "";
                    int slTrongGoc = 0;
                    for (ChiTietHoaDon ctGoc : dsGoc) {
                        if (ctGoc.isLaQuaTangKem())
                            continue;
                        String maSPGocX = ctGoc.getDonViQuyDoi().getSanPham() != null
                                ? ctGoc.getDonViQuyDoi().getSanPham().getMaSanPham()
                                : "";
                        String tenDVGocX = ctGoc.getDonViQuyDoi().getTenDonVi() != null
                                ? ctGoc.getDonViQuyDoi().getTenDonVi().name()
                                : "";
                        if (maSPGocX.equals(maSPDoi) && tenDVGocX.equals(tenDVDoi)) {
                            slTrongGoc += ctGoc.getSoLuongBan();
                        }
                    }
                    int slDoiNgang = Math.min(ctDoi.getSoLuongBan(), slTrongGoc);
                    int slMuaThem = Math.max(0, ctDoi.getSoLuongBan() - slTrongGoc);
                    double thuePt = ctDoi.getDonViQuyDoi().getSanPham() != null
                            ? ctDoi.getDonViQuyDoi().getSanPham().getThue()
                            : 0;
                    double price = ctDoi.getDonGia();

                    tongThue += slDoiNgang * price * (thuePt / 100.0) * (1 - tiLeGiamGoc / 100.0);
                    tongThue += slMuaThem * price * (thuePt / 100.0) * (1 - kmMoiPt / 100.0);
                }

                dTongTien = tongTienHang - soTienGiam - soTienKMGoc + tongThue;
            } catch (Exception ignored) {
            }
        }
        return dTongTien;
    }
}
