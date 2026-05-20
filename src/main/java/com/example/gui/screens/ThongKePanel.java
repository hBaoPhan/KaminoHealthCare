package com.example.gui.screens;

import com.example.dao.ThongKeDAO;
import com.example.service.ThongKeService;
import com.example.entity.HoaDon;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.SuPhanBoLo;
import com.example.entity.enums.LoaiHoaDon;
import com.example.gui.components.CustomLineChart;
import com.example.gui.components.CustomBarChart;
import com.example.gui.components.CustomPieChart;
import com.example.gui.components.RoundedButton;
import com.example.gui.components.RoundedPanel;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.print.PageFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThongKePanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255); // #F1F6FF
    private final Color COLOR_BORDER = new Color(218, 225, 233);
    private final Color COLOR_CYAN = new Color(0, 200, 255);
    private final Color COLOR_DARK_CYAN = new Color(0, 210, 210);

    private JComboBox<String> cboLoaiThongKe;
    private JComboBox<String> cboKyBaoCao;
    private DatePicker datePickerTu;
    private DatePicker datePickerDen;
    private RoundedButton btnXem;
    private RoundedButton btnXuatPdf;
    private JLabel lblTu;
    private JLabel lblDen;

    // Card Layout
    private JPanel contentCardPanel;
    private CardLayout contentCardLayout;

    // Revenue View Components
    private CustomLineChart chart;
    private JTable table;
    private DefaultTableModel modelTable;

    // Product View Components
    private JPanel panelSanPham;
    private CardLayout spCardLayout;
    private JPanel spCardPanel;
    private JButton btnTabBanChay;
    private JButton btnTabSapHetHan;
    private JButton btnTabTonKhoLau;
    private JTable tableBanChay;
    private DefaultTableModel modelBanChay;
    private JTable tableSapHetHan;
    private DefaultTableModel modelSapHetHan;
    private JTable tableTonKhoLau;
    private DefaultTableModel modelTonKhoLau;
    private JLabel lblTotalSpKinhDoanh;
    private CustomBarChart barChartBestSellers;
    private CustomPieChart pieChartCategories;
    private String activeTabSp = "Bán chạy";

    // Customer View Components
    private JPanel panelKhachHang;
    private JTable tableKhachHang;
    private DefaultTableModel modelKhachHang;
    private JLabel lblTotalKhachHang;
    private CustomPieChart pieChartCustTypes;
    private CustomLineChart chartNewCustRegistrations;

    private ThongKeService thongKeService;
    private final DecimalFormat currencyDf;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ThongKePanel() {
        thongKeService = new ThongKeService();

        // Configure Currency Formatter with Dot separator
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        currencyDf = new DecimalFormat("#,###", symbols);

        setLayout(new GridBagLayout());
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 12, 0);

        // 1. Top Bar Panel (Filters)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        add(createTopFilterPanel(), gbc);

        // 2. Content Card Panel
        contentCardLayout = new CardLayout();
        contentCardPanel = new JPanel(contentCardLayout);
        contentCardPanel.setOpaque(false);

        // A. Card Doanh Thu
        JPanel panelDoanhThu = new JPanel(new GridBagLayout());
        panelDoanhThu.setOpaque(false);
        GridBagConstraints dtGbc = new GridBagConstraints();
        dtGbc.fill = GridBagConstraints.BOTH;
        dtGbc.insets = new Insets(0, 0, 12, 0);
        dtGbc.gridx = 0;
        dtGbc.gridy = 0;
        dtGbc.weightx = 1.0;
        dtGbc.weighty = 0.55;
        panelDoanhThu.add(createChartCard(), dtGbc);
        dtGbc.gridy = 1;
        dtGbc.weighty = 0.45;
        dtGbc.insets = new Insets(0, 0, 0, 0);
        panelDoanhThu.add(createTableCard(), dtGbc);

        contentCardPanel.add(panelDoanhThu, "DoanhThu");

        // B. Card San Pham
        contentCardPanel.add(createSanPhamPanel(), "SanPham");

        // C. Card Khach Hang
        contentCardPanel.add(createKhachHangPanel(), "KhachHang");

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(contentCardPanel, gbc);

        // Add combobox listener to switch view
        cboLoaiThongKe.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switchThongKeView();
            }
        });

        // Initial Data Fetch
        updatePeriodSelection();
        switchThongKeView();
    }

    private JPanel createTopFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);

        // Combo Loại thống kê
        cboLoaiThongKe = new JComboBox<>(
                new String[] { "Thống kê doanh thu", "Thống kê sản phẩm", "Thống kê khách hàng" });
        cboLoaiThongKe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cboLoaiThongKe.setPreferredSize(new Dimension(180, 36));

        // Combo Kỳ báo cáo
        cboKyBaoCao = new JComboBox<>(new String[] { "Hôm nay", "Hôm qua", "7 ngày qua", "Tháng này", "Tùy chỉnh" });
        cboKyBaoCao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboKyBaoCao.setPreferredSize(new Dimension(130, 36));
        cboKyBaoCao.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updatePeriodSelection();
                loadData();
            }
        });

        // Date Pickers
        DatePickerSettings tuSettings = new DatePickerSettings();
        tuSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerTu = new DatePicker(tuSettings);
        datePickerTu.setPreferredSize(new Dimension(160, 36));
        datePickerTu.getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
        datePickerTu.getComponentDateTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        datePickerTu.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        datePickerTu.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        DatePickerSettings denSettings = new DatePickerSettings();
        denSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerDen = new DatePicker(denSettings);
        datePickerDen.setPreferredSize(new Dimension(160, 36));
        datePickerDen.getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
        datePickerDen.getComponentDateTextField().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        datePickerDen.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        datePickerDen.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        lblTu = new JLabel("Từ:");
        lblTu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDen = new JLabel("Đến:");
        lblDen.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Buttons
        btnXem = new RoundedButton("Xem");
        btnXem.setBackground(COLOR_CYAN);
        btnXem.setPreferredSize(new Dimension(100, 36));
        btnXem.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        btnXuatPdf = new RoundedButton("Xuất pdf");
        btnXuatPdf.setBackground(COLOR_DARK_CYAN);
        btnXuatPdf.setPreferredSize(new Dimension(110, 36));
        btnXuatPdf.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnXuatPdf.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xuatPDF();
            }
        });

        panel.add(cboLoaiThongKe);
        panel.add(cboKyBaoCao);
        panel.add(lblTu);
        panel.add(datePickerTu);
        panel.add(lblDen);
        panel.add(datePickerDen);
        panel.add(btnXem);
        panel.add(btnXuatPdf);

        return panel;
    }

    private RoundedPanel createRoundedCard() {
        RoundedPanel card = new RoundedPanel(16, false) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setBackground(Color.WHITE);
        return card;
    }

    private void setupTableStyle(JTable tbl) {
        tbl.setRowHeight(32);
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tbl.setShowGrid(true);
        tbl.setGridColor(new Color(240, 242, 245));

        JTableHeader header = tbl.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(245, 247, 250));
        header.setForeground(new Color(50, 60, 70));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));
    }

    private JPanel createChartCard() {
        RoundedPanel card = createRoundedCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Header Title
        JLabel lblTitle = new JLabel("Biểu đồ thể hiện doanh thu và lợi nhuận", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(40, 50, 60));
        card.add(lblTitle, BorderLayout.NORTH);

        // Chart Component wrapper
        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);

        chart = new CustomLineChart();
        centerWrapper.add(chart, BorderLayout.CENTER);

        // Legend box on the right
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setOpaque(false);
        legendPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        legendPanel.setMaximumSize(new Dimension(130, 75));
        legendPanel.setPreferredSize(new Dimension(130, 75));

        JLabel lblLegendTitle = new JLabel("Chú thích");
        lblLegendTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblLegendTitle.setForeground(new Color(60, 70, 80));
        lblLegendTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel item1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item1.setOpaque(false);
        item1.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel color1 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(135, 206, 235));
                g.fillRect(0, 4, 15, 6);
            }
        };
        color1.setPreferredSize(new Dimension(15, 14));
        JLabel lblItem1 = new JLabel("Doanh thu");
        lblItem1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item1.add(color1);
        item1.add(lblItem1);

        JPanel item2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        item2.setOpaque(false);
        item2.setAlignmentX(Component.LEFT_ALIGNMENT);
        JPanel color2 = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 150, 180));
                g.fillRect(0, 4, 15, 6);
            }
        };
        color2.setPreferredSize(new Dimension(15, 14));
        JLabel lblItem2 = new JLabel("Lợi nhuận");
        lblItem2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        item2.add(color2);
        item2.add(lblItem2);

        legendPanel.add(lblLegendTitle);
        legendPanel.add(Box.createVerticalStrut(6));
        legendPanel.add(item1);
        legendPanel.add(Box.createVerticalStrut(4));
        legendPanel.add(item2);

        JPanel rightWrapper = new JPanel(new GridBagLayout());
        rightWrapper.setOpaque(false);
        GridBagConstraints rGbc = new GridBagConstraints();
        rGbc.anchor = GridBagConstraints.EAST;
        rGbc.insets = new Insets(0, 10, 0, 0);
        rightWrapper.add(legendPanel, rGbc);

        centerWrapper.add(rightWrapper, BorderLayout.EAST);
        card.add(centerWrapper, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTableCard() {
        RoundedPanel card = createRoundedCard();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(new EmptyBorder(15, 20, 15, 20));

        // Title
        JLabel lblTitle = new JLabel("Chi tiết theo ngày");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(40, 50, 60));
        card.add(lblTitle, BorderLayout.NORTH);

        // Table
        String[] columns = { "Ngày", "Doanh thu", "Tiền trả hàng", "Giá vốn", "Lợi nhuận", "Số hóa đơn", "Số đơn trả" };
        modelTable = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(modelTable);
        setupTableStyle(table);

        // Center Renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        // Right Renderers for Currencies
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        card.add(scrollPane, BorderLayout.CENTER);

        return card;
    }

    private JPanel createSanPhamPanel() {
        panelSanPham = new JPanel(new GridBagLayout());
        panelSanPham.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Tab Bar
        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        tabBar.setOpaque(false);

        btnTabBanChay = new RoundedButton("Bán chạy");
        btnTabSapHetHan = new RoundedButton("Sắp hết hạn");
        btnTabTonKhoLau = new RoundedButton("Tồn kho lâu");

        Dimension btnSize = new Dimension(130, 36);
        btnTabBanChay.setPreferredSize(btnSize);
        btnTabSapHetHan.setPreferredSize(btnSize);
        btnTabTonKhoLau.setPreferredSize(btnSize);

        btnTabBanChay.addActionListener(e -> {
            activeTabSp = "Bán chạy";
            updateTabStyles();
            updatePeriodVisibilityForProductTab();
            spCardLayout.show(spCardPanel, "BanChay");
            loadData();
        });
        btnTabSapHetHan.addActionListener(e -> {
            activeTabSp = "Sắp hết hạn";
            updateTabStyles();
            updatePeriodVisibilityForProductTab();
            spCardLayout.show(spCardPanel, "SapHetHan");
            loadData();
        });
        btnTabTonKhoLau.addActionListener(e -> {
            activeTabSp = "Tồn kho lâu";
            updateTabStyles();
            updatePeriodVisibilityForProductTab();
            spCardLayout.show(spCardPanel, "TonKhoLau");
            loadData();
        });

        tabBar.add(btnTabBanChay);
        tabBar.add(btnTabSapHetHan);
        tabBar.add(btnTabTonKhoLau);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 10, 0);
        panelSanPham.add(tabBar, gbc);

        // 2. Card Panel
        spCardLayout = new CardLayout();
        spCardPanel = new JPanel(spCardLayout);
        spCardPanel.setOpaque(false);

        // A. Tab BanChay
        JPanel tabBanChayPanel = new JPanel(new GridBagLayout());
        tabBanChayPanel.setOpaque(false);
        GridBagConstraints bcGbc = new GridBagConstraints();
        bcGbc.fill = GridBagConstraints.BOTH;

        // Table Card
        RoundedPanel tableCard = createRoundedCard();
        tableCard.setLayout(new BorderLayout(0, 8));
        tableCard.setBorder(new EmptyBorder(12, 16, 12, 16));
        lblTotalSpKinhDoanh = new JLabel("Tổng số sản phẩm đang kinh doanh: 0");
        lblTotalSpKinhDoanh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalSpKinhDoanh.setForeground(new Color(40, 50, 60));
        tableCard.add(lblTotalSpKinhDoanh, BorderLayout.NORTH);

        String[] colsBC = { "Mã sản phẩm", "Tên sản phẩm", "Nhóm", "Số lượng bán", "Doanh thu", "Giá vốn" };
        modelBanChay = new DefaultTableModel(colsBC, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableBanChay = new JTable(modelBanChay);
        setupTableStyle(tableBanChay);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableBanChay.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableBanChay.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableBanChay.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // counts center

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tableBanChay.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tableBanChay.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);

        JScrollPane scrollBC = new JScrollPane(tableBanChay);
        scrollBC.getViewport().setBackground(Color.WHITE);
        scrollBC.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        tableCard.add(scrollBC, BorderLayout.CENTER);

        bcGbc.gridx = 0;
        bcGbc.gridy = 0;
        bcGbc.weightx = 1.0;
        bcGbc.weighty = 0.55;
        bcGbc.insets = new Insets(0, 0, 10, 0);
        tabBanChayPanel.add(tableCard, bcGbc);

        // Charts Side-by-side
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        chartsPanel.setOpaque(false);

        // Chart Left: Bar Chart
        RoundedPanel chartLeft = createRoundedCard();
        chartLeft.setLayout(new BorderLayout(0, 8));
        chartLeft.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblBarTitle = new JLabel("Top 5 sản phẩm bán chạy nhất", SwingConstants.CENTER);
        lblBarTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBarTitle.setForeground(new Color(40, 50, 60));
        chartLeft.add(lblBarTitle, BorderLayout.NORTH);
        barChartBestSellers = new CustomBarChart();
        chartLeft.add(barChartBestSellers, BorderLayout.CENTER);

        // Chart Right: Pie Chart
        RoundedPanel chartRight = createRoundedCard();
        chartRight.setLayout(new BorderLayout(0, 8));
        chartRight.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblPieTitle = new JLabel("Biểu đồ so sánh tỷ trọng các nhóm hàng", SwingConstants.CENTER);
        lblPieTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPieTitle.setForeground(new Color(40, 50, 60));
        chartRight.add(lblPieTitle, BorderLayout.NORTH);
        pieChartCategories = new CustomPieChart();
        chartRight.add(pieChartCategories, BorderLayout.CENTER);

        chartsPanel.add(chartLeft);
        chartsPanel.add(chartRight);

        bcGbc.gridy = 1;
        bcGbc.weighty = 0.45;
        bcGbc.insets = new Insets(0, 0, 0, 0);
        tabBanChayPanel.add(chartsPanel, bcGbc);

        spCardPanel.add(tabBanChayPanel, "BanChay");

        // B. Tab SapHetHan
        RoundedPanel cardSapHetHan = createRoundedCard();
        cardSapHetHan.setLayout(new BorderLayout(0, 8));
        cardSapHetHan.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblSapHetHanTitle = new JLabel("Danh sách lô sắp hết hạn");
        lblSapHetHanTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblSapHetHanTitle.setForeground(new Color(40, 50, 60));
        cardSapHetHan.add(lblSapHetHanTitle, BorderLayout.NORTH);

        String[] colsSHH = { "Mã lô", "Số lô", "Mã sản phẩm", "Tên sản phẩm", "Số lượng", "Ngày hết hạn" };
        modelSapHetHan = new DefaultTableModel(colsSHH, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableSapHetHan = new JTable(modelSapHetHan);
        setupTableStyle(tableSapHetHan);
        tableSapHetHan.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableSapHetHan.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tableSapHetHan.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableSapHetHan.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tableSapHetHan.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollSHH = new JScrollPane(tableSapHetHan);
        scrollSHH.getViewport().setBackground(Color.WHITE);
        scrollSHH.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        cardSapHetHan.add(scrollSHH, BorderLayout.CENTER);

        spCardPanel.add(cardSapHetHan, "SapHetHan");

        // C. Tab TonKhoLau
        RoundedPanel cardTonKhoLau = createRoundedCard();
        cardTonKhoLau.setLayout(new BorderLayout(0, 8));
        cardTonKhoLau.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblTonKhoLauTitle = new JLabel("Danh sách sản phẩm tồn kho lâu");
        lblTonKhoLauTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTonKhoLauTitle.setForeground(new Color(40, 50, 60));
        cardTonKhoLau.add(lblTonKhoLauTitle, BorderLayout.NORTH);

        String[] colsTKL = { "Mã sản phẩm", "Tên sản phẩm", "Nhóm", "Tồn kho", "Lần bán cuối", "Số ngày" };
        modelTonKhoLau = new DefaultTableModel(colsTKL, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableTonKhoLau = new JTable(modelTonKhoLau);
        setupTableStyle(tableTonKhoLau);
        tableTonKhoLau.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableTonKhoLau.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableTonKhoLau.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tableTonKhoLau.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tableTonKhoLau.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollTKL = new JScrollPane(tableTonKhoLau);
        scrollTKL.getViewport().setBackground(Color.WHITE);
        scrollTKL.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        cardTonKhoLau.add(scrollTKL, BorderLayout.CENTER);

        spCardPanel.add(cardTonKhoLau, "TonKhoLau");

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelSanPham.add(spCardPanel, gbc);

        return panelSanPham;
    }

    private JPanel createKhachHangPanel() {
        panelKhachHang = new JPanel(new GridBagLayout());
        panelKhachHang.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // 1. Table Card
        RoundedPanel tableCard = createRoundedCard();
        tableCard.setLayout(new BorderLayout(0, 8));
        tableCard.setBorder(new EmptyBorder(12, 16, 12, 16));
        lblTotalKhachHang = new JLabel("Tổng số khách hàng: 0");
        lblTotalKhachHang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalKhachHang.setForeground(new Color(40, 50, 60));
        tableCard.add(lblTotalKhachHang, BorderLayout.NORTH);

        String[] colsKH = { "Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Tổng chi tiêu", "Số đơn hàng",
                "Ngày mua gần nhất", "Phân loại" };
        modelKhachHang = new DefaultTableModel(colsKH, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tableKhachHang = new JTable(modelKhachHang);
        setupTableStyle(tableKhachHang);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableKhachHang.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableKhachHang.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableKhachHang.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        tableKhachHang.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        tableKhachHang.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tableKhachHang.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // spending right

        JScrollPane scrollKH = new JScrollPane(tableKhachHang);
        scrollKH.getViewport().setBackground(Color.WHITE);
        scrollKH.setBorder(BorderFactory.createLineBorder(new Color(230, 235, 240)));
        tableCard.add(scrollKH, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.55;
        gbc.insets = new Insets(0, 0, 10, 0);
        panelKhachHang.add(tableCard, gbc);

        // 2. Charts Panel
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        chartsPanel.setOpaque(false);

        // Chart Left: Pie Chart for customer types
        RoundedPanel chartLeft = createRoundedCard();
        chartLeft.setLayout(new BorderLayout(0, 8));
        chartLeft.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblLeftTitle = new JLabel("Biểu đồ so sánh tỷ trọng khách hàng", SwingConstants.CENTER);
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblLeftTitle.setForeground(new Color(40, 50, 60));
        chartLeft.add(lblLeftTitle, BorderLayout.NORTH);
        pieChartCustTypes = new CustomPieChart();
        chartLeft.add(pieChartCustTypes, BorderLayout.CENTER);

        // Chart Right: Line Chart for new registrations
        RoundedPanel chartRight = createRoundedCard();
        chartRight.setLayout(new BorderLayout(0, 8));
        chartRight.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel lblRightTitle = new JLabel("Biểu đồ thể hiện số lượng khách hàng mới đăng ký", SwingConstants.CENTER);
        lblRightTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRightTitle.setForeground(new Color(40, 50, 60));
        chartRight.add(lblRightTitle, BorderLayout.NORTH);
        chartNewCustRegistrations = new CustomLineChart();
        chartRight.add(chartNewCustRegistrations, BorderLayout.CENTER);

        chartsPanel.add(chartLeft);
        chartsPanel.add(chartRight);

        gbc.gridy = 1;
        gbc.weighty = 0.45;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelKhachHang.add(chartsPanel, gbc);

        return panelKhachHang;
    }

    private void updateTabStyles() {
        Color activeBg = new Color(0, 150, 214);
        Color activeFg = Color.WHITE;
        Color inactiveBg = new Color(230, 235, 245);
        Color inactiveFg = new Color(80, 90, 100);

        btnTabBanChay.setBackground(activeTabSp.equals("Bán chạy") ? activeBg : inactiveBg);
        btnTabBanChay.setForeground(activeTabSp.equals("Bán chạy") ? activeFg : inactiveFg);
        btnTabBanChay.setFont(new Font("Segoe UI", activeTabSp.equals("Bán chạy") ? Font.BOLD : Font.PLAIN, 13));

        btnTabSapHetHan.setBackground(activeTabSp.equals("Sắp hết hạn") ? activeBg : inactiveBg);
        btnTabSapHetHan.setForeground(activeTabSp.equals("Sắp hết hạn") ? activeFg : inactiveFg);
        btnTabSapHetHan.setFont(new Font("Segoe UI", activeTabSp.equals("Sắp hết hạn") ? Font.BOLD : Font.PLAIN, 13));

        btnTabTonKhoLau.setBackground(activeTabSp.equals("Tồn kho lâu") ? activeBg : inactiveBg);
        btnTabTonKhoLau.setForeground(activeTabSp.equals("Tồn kho lâu") ? activeFg : inactiveFg);
        btnTabTonKhoLau.setFont(new Font("Segoe UI", activeTabSp.equals("Tồn kho lâu") ? Font.BOLD : Font.PLAIN, 13));
    }

    private void updatePeriodVisibilityForProductTab() {
        boolean showDate = activeTabSp.equals("Bán chạy");
        lblTu.setVisible(showDate);
        datePickerTu.setVisible(showDate);
        lblDen.setVisible(showDate);
        datePickerDen.setVisible(showDate);
        cboKyBaoCao.setVisible(showDate);
        btnXem.setVisible(showDate);
    }

    private void switchThongKeView() {
        String selected = (String) cboLoaiThongKe.getSelectedItem();
        if (selected == null)
            return;

        CardLayout cl = (CardLayout) contentCardPanel.getLayout();
        if (selected.equals("Thống kê doanh thu")) {
            lblTu.setVisible(true);
            datePickerTu.setVisible(true);
            lblDen.setVisible(true);
            datePickerDen.setVisible(true);
            cboKyBaoCao.setVisible(true);
            btnXem.setVisible(true);
            cl.show(contentCardPanel, "DoanhThu");
        } else if (selected.equals("Thống kê sản phẩm")) {
            cl.show(contentCardPanel, "SanPham");
            updatePeriodVisibilityForProductTab();
        } else if (selected.equals("Thống kê khách hàng")) {
            lblTu.setVisible(true);
            datePickerTu.setVisible(true);
            lblDen.setVisible(true);
            datePickerDen.setVisible(true);
            cboKyBaoCao.setVisible(true);
            btnXem.setVisible(true);
            cl.show(contentCardPanel, "KhachHang");
        }
        loadData();
    }

    private void updatePeriodSelection() {
        String period = (String) cboKyBaoCao.getSelectedItem();
        if (period == null)
            return;

        LocalDate today = LocalDate.now();
        datePickerTu.setEnabled(false);
        datePickerDen.setEnabled(false);

        switch (period) {
            case "Hôm nay":
                datePickerTu.setDate(today);
                datePickerDen.setDate(today);
                break;
            case "Hôm qua":
                datePickerTu.setDate(today.minusDays(1));
                datePickerDen.setDate(today.minusDays(1));
                break;
            case "7 ngày qua":
                datePickerTu.setDate(today.minusDays(7));
                datePickerDen.setDate(today);
                break;
            case "Tháng này":
                datePickerTu.setDate(today.withDayOfMonth(1));
                datePickerDen.setDate(today);
                break;
            case "Tùy chỉnh":
                datePickerTu.setEnabled(true);
                datePickerDen.setEnabled(true);
                break;
        }
    }

    private void loadData() {
        LocalDate tuNgay = datePickerTu.getDate();
        LocalDate denNgay = datePickerDen.getDate();

        String type = (String) cboLoaiThongKe.getSelectedItem();
        boolean needsDate = true;
        if ("Thống kê sản phẩm".equals(type) && !activeTabSp.equals("Bán chạy")) {
            needsDate = false;
        }

        if (needsDate) {
            if (tuNgay == null || denNgay == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thời gian Từ và Đến!", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (tuNgay.isAfter(denNgay)) {
                JOptionPane.showMessageDialog(this, "Thời gian 'Từ' phải trước hoặc bằng 'Đến'!", "Lỗi thời gian",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if ("Thống kê doanh thu".equals(type)) {
            List<HoaDon> dsHoaDon = thongKeService.layHoaDonTheoKhoangNgay(tuNgay, denNgay);
            if (tuNgay.equals(denNgay)) {
                loadHourlyStats(tuNgay, dsHoaDon);
            } else {
                loadDailyStats(tuNgay, denNgay, dsHoaDon);
            }
        } else if ("Thống kê sản phẩm".equals(type)) {
            loadProductStats(tuNgay, denNgay);
        } else if ("Thống kê khách hàng".equals(type)) {
            loadCustomerStats(tuNgay, denNgay);
        }
    }

    private void loadHourlyStats(LocalDate targetDate, List<HoaDon> dsHoaDon) {
        modelTable.setRowCount(0);

        double totalRevenue = 0;
        double totalRefund = 0;
        double totalCost = 0;
        int totalInvoices = 0;
        int totalReturns = 0;

        List<String> labels = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        List<Double> profits = new ArrayList<>();

        Map<Integer, Double> hrRevenue = new HashMap<>();
        Map<Integer, Double> hrCost = new HashMap<>();
        Map<Integer, Double> hrRefund = new HashMap<>();

        for (int h = 7; h <= 21; h++) {
            hrRevenue.put(h, 0.0);
            hrCost.put(h, 0.0);
            hrRefund.put(h, 0.0);
        }

        for (HoaDon hd : dsHoaDon) {
            int hour = hd.getThoiGianTao().getHour();
            double finalTotal = hd.tinhTongTienThanhToan();

            double cost = 0.0;
            if (hd.getDsChiTiet() != null) {
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    if (ct.getDsPhanBoLo() != null) {
                        for (SuPhanBoLo spbl : ct.getDsPhanBoLo()) {
                            if (spbl.getLo() != null) {
                                cost += spbl.getSoLuong() * spbl.getLo().getGiaNhap();
                            }
                        }
                    }
                }
            }

            if (hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG || hd.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG) {
                totalRevenue += finalTotal;
                totalCost += cost;
                totalInvoices++;

                if (hour >= 7 && hour <= 21) {
                    hrRevenue.put(hour, hrRevenue.get(hour) + finalTotal);
                    hrCost.put(hour, hrCost.get(hour) + cost);
                }
            } else if (hd.getLoaiHoaDon() == LoaiHoaDon.TRA_HANG) {
                totalRefund += finalTotal;
                totalCost -= cost;
                totalReturns++;

                if (hour >= 7 && hour <= 21) {
                    hrRefund.put(hour, hrRefund.get(hour) + finalTotal);
                    hrCost.put(hour, hrCost.get(hour) - cost);
                }
            }
        }

        double totalProfit = totalRevenue - totalRefund - totalCost;

        modelTable.addRow(new Object[] {
                targetDate.format(dateFormatter),
                currencyDf.format(totalRevenue),
                currencyDf.format(totalRefund),
                currencyDf.format(totalCost),
                currencyDf.format(totalProfit),
                totalInvoices,
                totalReturns
        });

        for (int h = 7; h <= 21; h++) {
            labels.add(h + ":00");
            double rev = hrRevenue.get(h);
            double ref = hrRefund.get(h);
            double cst = hrCost.get(h);
            double prf = rev - ref - cst;

            revenues.add(rev);
            profits.add(prf);
        }

        chart.setValues(labels, revenues, profits);
    }

    private void loadDailyStats(LocalDate tuNgay, LocalDate denNgay, List<HoaDon> dsHoaDon) {
        modelTable.setRowCount(0);

        List<String> labels = new ArrayList<>();
        List<Double> revenues = new ArrayList<>();
        List<Double> profits = new ArrayList<>();

        Map<LocalDate, List<HoaDon>> dailyGroup = new HashMap<>();
        LocalDate curr = tuNgay;
        while (!curr.isAfter(denNgay)) {
            dailyGroup.put(curr, new ArrayList<>());
            curr = curr.plusDays(1);
        }

        for (HoaDon hd : dsHoaDon) {
            LocalDate date = hd.getThoiGianTao().toLocalDate();
            if (dailyGroup.containsKey(date)) {
                dailyGroup.get(date).add(hd);
            }
        }

        curr = tuNgay;
        while (!curr.isAfter(denNgay)) {
            List<HoaDon> list = dailyGroup.get(curr);
            double rev = 0;
            double refund = 0;
            double cost = 0;
            int invoices = 0;
            int returns = 0;

            for (HoaDon hd : list) {
                double finalTotal = hd.tinhTongTienThanhToan();

                double cstVal = 0.0;
                if (hd.getDsChiTiet() != null) {
                    for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                        if (ct.getDsPhanBoLo() != null) {
                            for (SuPhanBoLo spbl : ct.getDsPhanBoLo()) {
                                if (spbl.getLo() != null) {
                                    cstVal += spbl.getSoLuong() * spbl.getLo().getGiaNhap();
                                }
                            }
                        }
                    }
                }

                if (hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG || hd.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG) {
                    rev += finalTotal;
                    cost += cstVal;
                    invoices++;
                } else if (hd.getLoaiHoaDon() == LoaiHoaDon.TRA_HANG) {
                    refund += finalTotal;
                    cost -= cstVal;
                    returns++;
                }
            }

            double profit = rev - refund - cost;

            modelTable.addRow(new Object[] {
                    curr.format(dateFormatter),
                    currencyDf.format(rev),
                    currencyDf.format(refund),
                    currencyDf.format(cost),
                    currencyDf.format(profit),
                    invoices,
                    returns
            });

            labels.add(curr.format(DateTimeFormatter.ofPattern("dd/MM")));
            revenues.add(rev);
            profits.add(profit);

            curr = curr.plusDays(1);
        }

        chart.setValues(labels, revenues, profits);
    }

    private void loadProductStats(LocalDate tuNgay, LocalDate denNgay) {
        if (activeTabSp.equals("Bán chạy")) {
            List<HoaDon> dsHoaDon = thongKeService.layHoaDonTheoKhoangNgay(tuNgay, denNgay);
            Map<String, ProductStatItem> stats = new HashMap<>();

            for (HoaDon hd : dsHoaDon) {
                if (hd.getDsChiTiet() == null)
                    continue;
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    if (ct.getDonViQuyDoi() == null || ct.getDonViQuyDoi().getSanPham() == null)
                        continue;

                    String maSp = ct.getDonViQuyDoi().getSanPham().getMaSanPham();
                    String tenSp = ct.getDonViQuyDoi().getSanPham().getTenSanPham();
                    String nhom = ct.getDonViQuyDoi().getSanPham().getLoaiSanPham().getMoTa();

                    ProductStatItem item = stats.computeIfAbsent(maSp, k -> {
                        ProductStatItem i = new ProductStatItem();
                        i.maSp = maSp;
                        i.tenSp = tenSp;
                        i.nhom = nhom;
                        return i;
                    });

                    int baseQty = ct.getSoLuong() * ct.getDonViQuyDoi().getHeSoQuyDoi();
                    double revenue = ct.getSoLuong() * ct.getDonGia();
                    double cost = 0.0;
                    if (ct.getDsPhanBoLo() != null) {
                        for (SuPhanBoLo spbl : ct.getDsPhanBoLo()) {
                            if (spbl.getLo() != null) {
                                cost += spbl.getSoLuong() * spbl.getLo().getGiaNhap();
                            }
                        }
                    }

                    if (hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG || hd.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG) {
                        item.slBan += baseQty;
                        item.doanhThu += revenue;
                        item.giaVon += cost;
                    } else if (hd.getLoaiHoaDon() == LoaiHoaDon.TRA_HANG) {
                        item.slBan -= baseQty;
                        item.doanhThu -= revenue;
                        item.giaVon -= cost;
                    }
                }
            }

            List<ProductStatItem> sortedList = new ArrayList<>(stats.values());
            sortedList.sort((a, b) -> Integer.compare(b.slBan, a.slBan));

            modelBanChay.setRowCount(0);
            List<String> topLabels = new ArrayList<>();
            List<Integer> topValues = new ArrayList<>();
            Map<String, Double> categoryRevenue = new HashMap<>();

            for (int i = 0; i < sortedList.size(); i++) {
                ProductStatItem item = sortedList.get(i);
                modelBanChay.addRow(new Object[] {
                        item.maSp,
                        item.tenSp,
                        item.nhom,
                        item.slBan,
                        currencyDf.format(item.doanhThu),
                        currencyDf.format(item.giaVon)
                });

                if (i < 5) {
                    topLabels.add(item.tenSp);
                    topValues.add(item.slBan);
                }

                categoryRevenue.put(item.nhom, categoryRevenue.getOrDefault(item.nhom, 0.0) + item.doanhThu);
            }

            barChartBestSellers.setValues(topLabels, topValues);

            List<String> pieLabels = new ArrayList<>();
            List<Double> pieValues = new ArrayList<>();
            for (Map.Entry<String, Double> entry : categoryRevenue.entrySet()) {
                if (entry.getValue() > 0) {
                    pieLabels.add(entry.getKey());
                    pieValues.add(entry.getValue());
                }
            }
            pieChartCategories.setValues(pieLabels, pieValues);

            lblTotalSpKinhDoanh.setText("Tổng số sản phẩm đang kinh doanh: "
                    + new com.example.dao.SanPhamDAO().laySanPhamDangKinhDoanh().size());
        } else if (activeTabSp.equals("Sắp hết hạn")) {
            List<Object[]> ds = thongKeService.layLoSapHetHan();
            modelSapHetHan.setRowCount(0);
            for (Object[] row : ds) {
                modelSapHetHan.addRow(new Object[] {
                        row[0], // maLo
                        row[1], // soLo
                        row[2], // maSanPham
                        row[3], // tenSanPham
                        row[4], // soLuongTon
                        ((LocalDate) row[5]).format(dateFormatter) // ngayHetHan
                });
            }
        } else if (activeTabSp.equals("Tồn kho lâu")) {
            List<Object[]> ds = thongKeService.laySanPhamTonKhoLau();
            modelTonKhoLau.setRowCount(0);
            for (Object[] row : ds) {
                String lanBanStr = row[4] != null ? ((LocalDate) row[4]).format(dateFormatter) : "Chưa bán";
                String soNgayStr = (int) row[5] >= 0 ? String.valueOf(row[5]) : "N/A";

                String loaiMoTa = "";
                try {
                    loaiMoTa = com.example.entity.enums.LoaiSanPham.valueOf(row[2].toString()).getMoTa();
                } catch (Exception e) {
                    loaiMoTa = row[2].toString();
                }

                modelTonKhoLau.addRow(new Object[] {
                        row[0], // maSp
                        row[1], // tenSp
                        loaiMoTa, // nhom
                        row[3], // tonKho
                        lanBanStr, // lanBanCuoi
                        soNgayStr // soNgay
                });
            }
        }
    }

    private void loadCustomerStats(LocalDate tuNgay, LocalDate denNgay) {
        List<HoaDon> dsHoaDon = thongKeService.layHoaDonTheoKhoangNgay(tuNgay, denNgay);
        Map<String, CustomerStatItem> stats = new HashMap<>();
        double retailSpending = 0.0;

        for (HoaDon hd : dsHoaDon) {
            double finalTotal = hd.tinhTongTienThanhToan();
            com.example.entity.KhachHang kh = hd.getKhachHang();

            if (kh == null || kh.getMaKhachHang() == null || kh.getMaKhachHang().trim().isEmpty()) {
                retailSpending += finalTotal;
                continue;
            }

            String maKh = kh.getMaKhachHang();
            String tenKh = kh.getTenKhachHang();
            String sdt = kh.getSdt();
            String phanLoai = kh.getTrangThai() != null ? kh.getTrangThai().getMoTa() : "Thành viên";

            CustomerStatItem item = stats.computeIfAbsent(maKh, k -> {
                CustomerStatItem i = new CustomerStatItem();
                i.maKh = maKh;
                i.tenKh = tenKh;
                i.sdt = sdt;
                i.phanLoai = phanLoai;
                return i;
            });

            if (hd.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG || hd.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG) {
                item.tongChiTieu += finalTotal;
                item.soDonHang++;
                if (item.ngayMuaGanNhat == null || hd.getThoiGianTao().isAfter(item.ngayMuaGanNhat)) {
                    item.ngayMuaGanNhat = hd.getThoiGianTao();
                }
            } else if (hd.getLoaiHoaDon() == LoaiHoaDon.TRA_HANG) {
                item.tongChiTieu -= finalTotal;
            }
        }

        List<CustomerStatItem> sortedList = new ArrayList<>(stats.values());
        sortedList.sort((a, b) -> Double.compare(b.tongChiTieu, a.tongChiTieu));

        modelKhachHang.setRowCount(0);
        double totalMemberSpending = 0.0;
        for (CustomerStatItem item : sortedList) {
            String dateStr = item.ngayMuaGanNhat != null ? item.ngayMuaGanNhat.toLocalDate().format(dateFormatter)
                    : "N/A";
            modelKhachHang.addRow(new Object[] {
                    item.maKh,
                    item.tenKh,
                    item.sdt != null ? item.sdt : "",
                    currencyDf.format(item.tongChiTieu),
                    item.soDonHang,
                    dateStr,
                    item.phanLoai
            });
            totalMemberSpending += Math.max(0, item.tongChiTieu);
        }

        lblTotalKhachHang.setText("Tổng số khách hàng: " + stats.size());

        List<String> pieLabels = new ArrayList<>();
        List<Double> pieValues = new ArrayList<>();
        if (totalMemberSpending > 0) {
            pieLabels.add("Khách hàng thành viên");
            pieValues.add(totalMemberSpending);
        }
        if (retailSpending > 0) {
            pieLabels.add("Khách lẻ");
            pieValues.add(retailSpending);
        }
        pieChartCustTypes.setValues(pieLabels, pieValues);

        // Line Chart: New customer registrations
        List<Object[]> allRegs = thongKeService.layNgayDangKyKhachHang();

        List<String> lineLabels = new ArrayList<>();
        List<Double> lineValues = new ArrayList<>();
        List<Double> emptyProfits = new ArrayList<>();

        if (tuNgay.equals(denNgay)) {
            Map<Integer, Integer> hourlyCount = new HashMap<>();
            for (int h = 7; h <= 21; h++)
                hourlyCount.put(h, 0);

            for (Object[] row : allRegs) {
                java.time.LocalDateTime dt = (java.time.LocalDateTime) row[1];
                if (dt.toLocalDate().equals(tuNgay)) {
                    int hr = dt.getHour();
                    if (hr >= 7 && hr <= 21) {
                        hourlyCount.put(hr, hourlyCount.get(hr) + 1);
                    }
                }
            }

            for (int h = 7; h <= 21; h++) {
                lineLabels.add(h + ":00");
                lineValues.add((double) hourlyCount.get(h));
                emptyProfits.add(0.0);
            }
        } else {
            Map<LocalDate, Integer> dailyCount = new HashMap<>();
            LocalDate curr = tuNgay;
            while (!curr.isAfter(denNgay)) {
                dailyCount.put(curr, 0);
                curr = curr.plusDays(1);
            }

            for (Object[] row : allRegs) {
                java.time.LocalDateTime dt = (java.time.LocalDateTime) row[1];
                LocalDate d = dt.toLocalDate();
                if (dailyCount.containsKey(d)) {
                    dailyCount.put(d, dailyCount.get(d) + 1);
                }
            }

            curr = tuNgay;
            while (!curr.isAfter(denNgay)) {
                lineLabels.add(curr.format(DateTimeFormatter.ofPattern("dd/MM")));
                lineValues.add((double) dailyCount.get(curr));
                emptyProfits.add(0.0);
                curr = curr.plusDays(1);
            }
        }

        chartNewCustRegistrations.setValues(lineLabels, lineValues, emptyProfits);
    }

    public void capNhatDuLieuThongKe() {
        loadData();
    }

    private void xuatPDF() {
        String type = (String) cboLoaiThongKe.getSelectedItem();
        if (type == null)
            return;

        boolean needsDate = "Thống kê doanh thu".equals(type) 
                || ("Thống kê sản phẩm".equals(type) && "Bán chạy".equals(activeTabSp))
                || "Thống kê khách hàng".equals(type);

        if (needsDate) {
            if (datePickerTu.getDate() == null || datePickerDen.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thời gian Từ và Đến trước khi xuất PDF!", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (datePickerTu.getDate().isAfter(datePickerDen.getDate())) {
                JOptionPane.showMessageDialog(this, "Thời gian 'Từ' phải trước hoặc bằng 'Đến'!", "Lỗi thời gian", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Bao Cao Kamino Health Care");

        job.setPrintable(new Printable() {
            @Override
            public int print(Graphics pg, PageFormat pf, int pageNum) {
                if (pageNum > 0) {
                    return Printable.NO_SUCH_PAGE;
                }

                Graphics2D g2 = (Graphics2D) pg;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                double pWidth = pf.getImageableWidth();
                double pHeight = pf.getImageableHeight();

                g2.translate(pf.getImageableX(), pf.getImageableY());

                // Draw Header
                g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
                g2.setColor(new Color(0, 102, 204));

                String title = "";
                String periodStr = "";
                String[] headers = null;
                int[] colWidths = null;
                DefaultTableModel activeModel = null;

                if ("Thống kê doanh thu".equals(type)) {
                    title = "BÁO CÁO DOANH THU & LỢI NHUẬN";
                    periodStr = "Kỳ báo cáo: Từ " + datePickerTu.getDate().format(dateFormatter) +
                            " đến " + datePickerDen.getDate().format(dateFormatter);
                    headers = new String[] { "Ngày", "Doanh thu", "Tiền trả hàng", "Giá vốn", "Lợi nhuận", "Số HĐ",
                            "Số ĐT" };
                    colWidths = new int[] { 80, 85, 75, 80, 85, 45, 45 };
                    activeModel = modelTable;
                } else if ("Thống kê sản phẩm".equals(type)) {
                    title = "BÁO CÁO THỐNG KÊ SẢN PHẨM - " + activeTabSp.toUpperCase();
                    if ("Bán chạy".equals(activeTabSp)) {
                        periodStr = "Kỳ báo cáo: Từ " + datePickerTu.getDate().format(dateFormatter) +
                                " đến " + datePickerDen.getDate().format(dateFormatter);
                        headers = new String[] { "Mã sản phẩm", "Tên sản phẩm", "Nhóm", "Số lượng bán", "Doanh thu",
                                "Giá vốn" };
                        colWidths = new int[] { 80, 160, 100, 60, 85, 85 };
                        activeModel = modelBanChay;
                    } else if ("Sắp hết hạn".equals(activeTabSp)) {
                        periodStr = "Thời điểm kiểm tra: " + java.time.LocalDate.now().format(dateFormatter)
                                + " (Thời hạn < 30 ngày)";
                        headers = new String[] { "Mã lô", "Số lô", "Mã SP", "Tên sản phẩm", "Số lượng", "Hạn dùng" };
                        colWidths = new int[] { 80, 80, 80, 160, 60, 80 };
                        activeModel = modelSapHetHan;
                    } else {
                        periodStr = "Thời điểm kiểm tra: " + java.time.LocalDate.now().format(dateFormatter);
                        headers = new String[] { "Mã sản phẩm", "Tên sản phẩm", "Nhóm", "Tồn kho", "Lần bán cuối",
                                "Số ngày" };
                        colWidths = new int[] { 80, 160, 100, 60, 85, 60 };
                        activeModel = modelTonKhoLau;
                    }
                } else {
                    title = "BÁO CÁO THỐNG KÊ CHI TIÊU KHÁCH HÀNG";
                    periodStr = "Kỳ báo cáo: Từ " + datePickerTu.getDate().format(dateFormatter) +
                            " đến " + datePickerDen.getDate().format(dateFormatter);
                    headers = new String[] { "Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Tổng chi tiêu",
                            "Số đơn", "Mua cuối", "Phân loại" };
                    colWidths = new int[] { 80, 130, 80, 95, 45, 80, 65 };
                    activeModel = modelKhachHang;
                }

                g2.drawString(title, 30, 45);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                g2.setColor(Color.DARK_GRAY);
                g2.drawString("Hệ thống nhà thuốc Kamino Health Care", 30, 62);
                g2.drawString("Thời gian xuất báo cáo: " + java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), 30, 77);
                g2.drawString(periodStr, 30, 92);

                // Line Separator
                g2.setColor(new Color(200, 200, 200));
                g2.drawLine(30, 105, (int) pWidth - 30, 105);

                // Table Layout Details
                int startY = 135;
                int rowH = 22;

                g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                g2.setColor(Color.BLACK);

                // Draw Header Columns
                int currX = 30;
                for (int i = 0; i < headers.length; i++) {
                    g2.drawString(headers[i], currX, startY);
                    currX += colWidths[i];
                }

                g2.drawLine(30, startY + 5, (int) pWidth - 30, startY + 5);
                startY += 20;

                // Draw Row Content
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
                int rowCount = activeModel.getRowCount();
                for (int row = 0; row < rowCount; row++) {
                    currX = 30;
                    for (int col = 0; col < headers.length; col++) {
                        Object cellVal = activeModel.getValueAt(row, col);
                        String val = cellVal != null ? cellVal.toString() : "";
                        g2.drawString(val, currX, startY);
                        currX += colWidths[col];
                    }

                    g2.setColor(new Color(240, 240, 240));
                    g2.drawLine(30, startY + 5, (int) pWidth - 30, startY + 5);
                    g2.setColor(Color.BLACK);

                    startY += rowH;
                    if (startY > pHeight - 60) {
                        break;
                    }
                }

                // If "Thống kê doanh thu", draw the summary total row
                if ("Thống kê doanh thu".equals(type)) {
                    double totalRev = 0, totalRefund = 0, totalCost = 0, totalProfit = 0;
                    int totalInvoices = 0, totalReturns = 0;

                    for (int row = 0; row < rowCount; row++) {
                        try {
                            totalRev += Double.parseDouble(modelTable.getValueAt(row, 1).toString().replace(".", ""));
                            totalRefund += Double
                                    .parseDouble(modelTable.getValueAt(row, 2).toString().replace(".", ""));
                            totalCost += Double.parseDouble(modelTable.getValueAt(row, 3).toString().replace(".", ""));
                            totalProfit += Double
                                    .parseDouble(modelTable.getValueAt(row, 4).toString().replace(".", ""));
                            totalInvoices += Integer.parseInt(modelTable.getValueAt(row, 5).toString());
                            totalReturns += Integer.parseInt(modelTable.getValueAt(row, 6).toString());
                        } catch (Exception e) {
                            // ignore parsing error
                        }
                    }

                    g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                    g2.drawLine(30, startY, (int) pWidth - 30, startY);
                    startY += 15;

                    currX = 30;
                    g2.drawString("TỔNG CỘNG", currX, startY);
                    currX += colWidths[0];
                    g2.drawString(currencyDf.format(totalRev), currX, startY);
                    currX += colWidths[1];
                    g2.drawString(currencyDf.format(totalRefund), currX, startY);
                    currX += colWidths[2];
                    g2.drawString(currencyDf.format(totalCost), currX, startY);
                    currX += colWidths[3];
                    g2.drawString(currencyDf.format(totalProfit), currX, startY);
                    currX += colWidths[4];
                    g2.drawString(String.valueOf(totalInvoices), currX, startY);
                    currX += colWidths[5];
                    g2.drawString(String.valueOf(totalReturns), currX, startY);

                    g2.drawLine(30, startY + 5, (int) pWidth - 30, startY + 5);
                }

                return Printable.PAGE_EXISTS;
            }
        });

        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch (PrinterException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi in ấn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class ProductStatItem {
        String maSp;
        String tenSp;
        String nhom;
        int slBan = 0;
        double doanhThu = 0;
        double giaVon = 0;
    }

    private static class CustomerStatItem {
        String maKh;
        String tenKh;
        String sdt;
        double tongChiTieu = 0;
        int soDonHang = 0;
        java.time.LocalDateTime ngayMuaGanNhat = null;
        String phanLoai = "";
    }
}
