package com.example.gui.screens;

import com.example.gui.components.*;

import com.example.entity.TaiKhoan;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.Lo;
import com.example.entity.enums.LoaiHoaDon;
import com.example.entity.enums.LoaiSanPham;
import com.example.dao.HoaDonDAO;
import com.example.dao.LoDAO;
import com.example.dao.ChiTietHoaDonDAO;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

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
    private DefaultPieDataset<String> donutDataset;

    private RoundedButton btnBanHang;
    private RoundedButton btnTimThuoc;
    private RoundedButton btnTimKhachHang;
    private RoundedButton btnThanhToan;

    private HoaDonDAO hoaDonDAO;
    private LoDAO loDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;

    public ManHinhChinhPanel(TaiKhoan taiKhoan) {
        hoaDonDAO = new HoaDonDAO();
        loDAO = new LoDAO();
        chiTietHoaDonDAO = new ChiTietHoaDonDAO();

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
        panel.add(createStatCard("Lợi Nhuận", lblLoiNhuan));
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

        modelLoThuoc = new DefaultTableModel(new String[] { "Mã Lô", "Tên thuốc", "HSD" }, 0);
        JPanel rightTablePanel = createTableContainer("Cảnh báo lô thuốc gần hết hạn", modelLoThuoc);
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
        donutChartWrapper.setLayout(new BorderLayout());
        donutChartWrapper.setBackground(Color.WHITE);
        donutChartWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
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
        donutDataset = new DefaultPieDataset<>();

        JFreeChart chart = ChartFactory.createRingChart(
                "Cơ cấu doanh thu theo Nhóm hàng",
                donutDataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);

        org.jfree.chart.plot.RingPlot plot = (org.jfree.chart.plot.RingPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);

        plot.setLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator(
                "{0}: {1} ({2})",
                new java.text.DecimalFormat("###,### VND"),
                new java.text.DecimalFormat("0.0%")));
        plot.setLabelBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 80));

        btnBanHang = createActionButton("Thêm hóa đơn mới", "F1", new Color(0x20C997));
        btnTimThuoc = createActionButton("Tìm kiếm thuốc", "F3", new Color(0x38D9A9));
        btnTimKhachHang = createActionButton("Tìm kiếm khách hàng", "F4", new Color(0x3DB5E0));
        btnThanhToan = createActionButton("Thanh toán", "F9", new Color(0x00C4EC));

        btnBanHang.addActionListener(e -> navigateTo("Bán Hàng"));
        btnTimThuoc.addActionListener(e -> navigateTo("Quản Lý Sản Phẩm"));
        btnTimKhachHang.addActionListener(e -> navigateTo("Khách Hàng"));
        btnThanhToan.addActionListener(e -> navigateTo("Quản Lý Hóa Đơn"));

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
        List<HoaDon> dsHoaDon = hoaDonDAO.timKiem(null, today);

        int soHoaDon = dsHoaDon.size();
        double doanhThu = 0;

        double[] doanhThuTheoGio = new double[24];
        double dtETC = 0;
        double dtOTC = 0;
        double dtTPCN = 0;

        for (HoaDon hd : dsHoaDon) {
            // Chỉ tính doanh thu cho các hóa đơn ĐÃ THANH TOÁN
            if (!hd.isTrangThaiThanhToan())
                continue;

            double tienHD = 0;
            double tongHienTai = hd.tinhTongTienThanhToan();
            LoaiHoaDon loai = hd.getLoaiHoaDon();

            if (loai == LoaiHoaDon.BAN_HANG || loai == null) {
                tienHD = tongHienTai;
            } else {
                double tongGoc = 0;
                if (hd.getHoaDonDoiTra() != null) {
                    String maGoc = hd.getHoaDonDoiTra().getMaHoaDon();
                    HoaDon hdGoc = hoaDonDAO.timTheoMa(maGoc);
                    if (hdGoc != null) {
                        hdGoc.setDsChiTiet(chiTietHoaDonDAO.layTheoMaHoaDon(maGoc));
                        tongGoc = hdGoc.tinhTongTienThanhToan();
                    }
                }

                if (loai == LoaiHoaDon.DOI_HANG) {
                    tienHD = tongHienTai - tongGoc;
                } else if (loai == LoaiHoaDon.TRA_HANG) {
                    tienHD = -tongHienTai;
                } else {
                    tienHD = tongHienTai;
                }
            }

            doanhThu += tienHD;

            if (hd.getThoiGianTao() != null) {
                int gio = hd.getThoiGianTao().getHour();
                doanhThuTheoGio[gio] += tienHD;
            }

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
                    }
                }
            }
        }

        // Tạm tính lợi nhuận bằng 20% doanh thu
        double loiNhuan = doanhThu * 0.2;

        lblHoaDonHomNay.setText(String.valueOf(soHoaDon));

        DecimalFormat df = new DecimalFormat("###,###,### VND");
        lblDoanhThuHomNay.setText(doanhThu == 0 ? "0 VND" : df.format(doanhThu));
        lblLoiNhuan.setText(loiNhuan == 0 ? "0 VND" : df.format(loiNhuan));

        List<Lo> dsLo = loDAO.layTatCa();
        int loHetHan = 0;
        int loGanHetHan = 0;
        LocalDate nextMonth = today.plusDays(37);
        for (Lo lo : dsLo) {
            if (lo.getNgayHetHan() != null) {
                if (!lo.getNgayHetHan().isAfter(today)) {
                    loHetHan++;
                } else if (lo.getNgayHetHan().isBefore(nextMonth)) {
                    loGanHetHan++;
                }
            }
        }
        String canhBaoText = "";
        if (loHetHan > 0) {
            canhBaoText += loHetHan + " lô đã hết hạn";
        }
        if (loGanHetHan > 0) {
            if (!canhBaoText.isEmpty()) canhBaoText += ", ";
            canhBaoText += loGanHetHan + " lô sắp hết hạn";
        }
        if (canhBaoText.isEmpty()) {
            canhBaoText = "Không có cảnh báo";
        }
        lblCanhBao.setText(canhBaoText);

        // Update charts
        if (barDataset != null) {
            barDataset.clear();
            for (int i = 6; i <= 23; i++) {
                barDataset.addValue(doanhThuTheoGio[i], "Doanh thu", String.format("%02d:00", i));
            }
        }

        if (donutDataset != null) {
            donutDataset.clear();
            if (dtETC > 0)
                donutDataset.setValue("Thuốc kê đơn", dtETC);
            if (dtOTC > 0)
                donutDataset.setValue("Thuốc không kê đơn", dtOTC);
            if (dtTPCN > 0)
                donutDataset.setValue("Thực phẩm chức năng", dtTPCN);

            if (dtETC == 0 && dtOTC == 0 && dtTPCN == 0) {
                donutDataset.setValue("Chưa có dữ liệu", 1);
            }
        }
    }

    public void layDuLieuChoHoatDongGanDay() {
        LocalDate today = LocalDate.now();
        List<HoaDon> dsHoaDon = hoaDonDAO.timKiem(null, today);

        modelHoaDon.setRowCount(0);
        DecimalFormat df = new DecimalFormat("###,###,### VND");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (HoaDon hd : dsHoaDon) {
            modelHoaDon.addRow(new Object[] {
                    hd.getMaHoaDon(),
                    hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "Khách lẻ",
                    hd.getThoiGianTao() != null ? hd.getThoiGianTao().format(dtf) : "",
                    hd.getKhuyenMai() != null ? hd.getKhuyenMai().getTenKhuyenMai() : "Không có",
                    hd.getNhanVien() != null ? hd.getNhanVien().getTenNhanVien() : "",
                    hd.getLoaiHoaDon() != null ? hd.getLoaiHoaDon().getMoTa() : "",
                    df.format(hd.tinhTongTienThanhToan()),
                    hd.isTrangThaiThanhToan() ? "Đã thanh toán" : "Chưa thanh toán"
            });
        }

        List<Lo> dsLoTable = loDAO.layTatCa();
        modelLoThuoc.setRowCount(0);
        DateTimeFormatter dtfDate = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate nextMonthTable = today.plusDays(37);

        for (Lo lo : dsLoTable) {
            if (lo.getNgayHetHan() != null
                    && (!lo.getNgayHetHan().isAfter(today) || lo.getNgayHetHan().isBefore(nextMonthTable))) {
                String trangThai = !lo.getNgayHetHan().isAfter(today) ? " [HẾT HẠN]" : "";
                modelLoThuoc.addRow(new Object[] {
                        lo.getSoLo(),
                        lo.getSanPham() != null ? lo.getSanPham().getTenSanPham() : "",
                        lo.getNgayHetHan().format(dtfDate) + trangThai
                });
            }
        }
    }

    private double tinhDoanhThuHieuChinh(HoaDon hd) {
        if (hd == null)
            return 0;
        double tongHienTai = hd.tinhTongTienThanhToan();
        LoaiHoaDon loai = hd.getLoaiHoaDon();

        if (loai == LoaiHoaDon.BAN_HANG || loai == null) {
            return tongHienTai;
        }

        double tongGoc = 0;
        if (hd.getHoaDonDoiTra() != null) {
            String maGoc = hd.getHoaDonDoiTra().getMaHoaDon();
            HoaDon hdGoc = hoaDonDAO.timTheoMa(maGoc);
            if (hdGoc != null) {
                hdGoc.setDsChiTiet(chiTietHoaDonDAO.layTheoMaHoaDon(maGoc));
                tongGoc = hdGoc.tinhTongTienThanhToan();
            }
        }

        if (loai == LoaiHoaDon.DOI_HANG) {
            return tongHienTai - tongGoc;
        } else if (loai == LoaiHoaDon.TRA_HANG) {
            return tongGoc - tongHienTai;
        }
        return tongHienTai;
    }
}
