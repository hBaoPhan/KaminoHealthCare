package com.example.gui.screens;
import com.example.gui.components.*;

import com.example.entity.TaiKhoan;
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

public class ManHinhChinhPanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255); // #F1F6FF
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_STATS = new Font("Segoe UI", Font.BOLD, 24);

    public ManHinhChinhPanel(TaiKhoan taiKhoan) {
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = createCenterPanel();
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);

        panel.add(createStatCard("Hóa đơn hôm nay", "12", ""));
        panel.add(createStatCard("Doanh thu hôm nay", "666, 777 VND", ""));
        panel.add(createStatCard("Lợi Nhuận", "666, 777 VND", ""));
        panel.add(createStatCard("Cảnh báo", "6 lô thuốc gần hết hạn", ""));

        return panel;
    }

    private JPanel createStatCard(String title, String value, String subtext) {
        RoundedPanel card = new RoundedPanel(16, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
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

        JPanel leftTablePanel = createTableContainer("Hóa đơn hôm nay", new String[] { "Mã Hóa đơn", "Tên KH",
                "Ngày tạo", "Khuyến mãi", "Người tạo", "Loại hóa đơn", "Tổng tiền", "Trạng thái" });
        tgbc.weightx = 0.7;
        tgbc.gridx = 0;
        tgbc.insets = new Insets(0, 0, 0, 10);
        tablePanel.add(leftTablePanel, tgbc);

        JPanel rightTablePanel = createTableContainer("Lô thuốc hết hạn hôm nay",
                new String[] { "Mã Lô", "Tên thuốc", "HSD" });
        tgbc.weightx = 0.3;
        tgbc.gridx = 1;
        tgbc.insets = new Insets(0, 10, 0, 0);
        tablePanel.add(rightTablePanel, tgbc);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        panel.add(tablePanel, gbc);

        RoundedPanel chartContainer = new RoundedPanel(16, true);
        chartContainer.setLayout(new GridLayout(1, 2, 20, 0));
        chartContainer.setBackground(Color.WHITE);

        chartContainer.add(createBarChartPanel());
        chartContainer.add(createDonutChartPanel());

        gbc.gridy = 1;
        gbc.weighty = 0.5;
        panel.add(chartContainer, gbc);

        return panel;
    }

    private JPanel createTableContainer(String title, String[] columns) {
        RoundedPanel panel = new RoundedPanel(16, true);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TITLE);
        panel.add(lblTitle, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (int i = 0; i < 5; i++)
            model.addRow(new Object[columns.length]);

        JTable table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBarChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(120, "Doanh thu", "08:00");
        dataset.addValue(200, "Doanh thu", "09:00");
        dataset.addValue(150, "Doanh thu", "10:00");
        dataset.addValue(80, "Doanh thu", "11:00");
        dataset.addValue(70, "Doanh thu", "12:00");
        dataset.addValue(110, "Doanh thu", "13:00");
        dataset.addValue(130, "Doanh thu", "14:00");

        JFreeChart chart = ChartFactory.createBarChart(
                "Doanh thu theo giờ",
                null, null,
                dataset, PlotOrientation.VERTICAL,
                false, true, false);
        chart.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }

    private JPanel createDonutChartPanel() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        dataset.setValue("Thuốc kháng sinh", 40);
        dataset.setValue("Thực phẩm chức năng", 30);
        dataset.setValue("Dụng cụ y tế", 20);
        dataset.setValue("Khác", 10);

        JFreeChart chart = ChartFactory.createRingChart(
                "Cơ cấu doanh thu theo Nhóm hàng",
                dataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 80));

        panel.add(createActionButton("Thêm hóa đơn mới", "F1", new Color(0x20C997)));
        panel.add(createActionButton("Tìm kiếm thuốc", "F3", new Color(0x38D9A9)));
        panel.add(createActionButton("Tìm kiếm khách hàng", "F4", new Color(0x3DB5E0)));
        panel.add(createActionButton("Thanh toán", "F9", new Color(0x00C4EC)));

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

    public void loadThongKeData() {

    }

    public void layDuLieuChoHoatDongGanDay() {

    }
}

