package com.example.gui.screens;
import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class BanHangPanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255); // #F1F6FF
    private final Color COLOR_CARD_BG = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(0, 200, 83); // Green
    private final Color COLOR_SECONDARY = new Color(0, 123, 255); // Blue
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_TEXT_DIM = new Color(110, 110, 110);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 14);

    public BanHangPanel() {
        setLayout(new BorderLayout(20, 0));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createLeftPanel(), BorderLayout.CENTER);
        add(createRightSidebar(), BorderLayout.EAST);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Danh sách sản phẩm");
        lblTitle.setFont(FONT_TITLE);

        RoundedTextField txtSearch = new RoundedTextField("Mã sản phẩm", 15);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setFont(FONT_TEXT);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(txtSearch, BorderLayout.EAST);

        RoundedPanel tableContainer = new RoundedPanel(12, true);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBackground(COLOR_CARD_BG);

        String[] columns = { "Mã sản phẩm", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(columns, 15);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_TEXT);
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setFont(FONT_LABEL);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableContainer, BorderLayout.CENTER);
        panel.add(createSummaryBar(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSummaryBar() {
        RoundedPanel panel = new RoundedPanel(12, true);
        panel.setLayout(new GridLayout(4, 1, 5, 2));
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        panel.add(createSummaryLabel("Tổng tiền hóa đơn :", ""));
        panel.add(createSummaryLabel("Khuyến mãi :", ""));
        panel.add(createSummaryLabel("Thuế :", ""));

        JLabel lblTotal = new JLabel("Thành tiền :");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTotal);

        return panel;
    }

    private JLabel createSummaryLabel(String text, String value) {
        JLabel label = new JLabel(text + " " + value);
        label.setFont(FONT_LABEL);
        return label;
    }

    private JPanel createRightSidebar() {
        RoundedPanel sidebar = new RoundedPanel(12, true);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(320, 0));
        sidebar.setBackground(COLOR_CARD_BG);
        sidebar.setBorder(new EmptyBorder(20, 15, 20, 15));

        sidebar.add(createSectionTitle("Thông tin khách hàng"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createFieldGroup("Số điện thoại", new RoundedTextField(10)));
        sidebar.add(Box.createVerticalStrut(25));

        // Invoice Info
        sidebar.add(createSectionTitle("Thông tin hóa đơn"));
        sidebar.add(Box.createVerticalStrut(15));
        sidebar.add(createFieldGroup("Mã hóa đơn", createReadOnlyField("HD27032026001")));

        // Date Picker for Ngay tao
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setFontValidDate(FONT_TEXT);
        DatePicker datePicker = new DatePicker(dateSettings);
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        dateFinderStyle(datePicker);

        sidebar.add(createFieldGroup("Ngày tạo", datePicker));
        sidebar.add(createFieldGroup("Người tạo", createReadOnlyField("Phan Hoai Bao")));

        sidebar.add(createFieldGroup("Khuyến mãi", new JComboBox<>(new String[] { "30/4 - 1/5" })));
        sidebar.add(createFieldGroup("Tên khách hàng", createReadOnlyField("")));

        JTextArea areaNotes = new JTextArea(3, 20);
        areaNotes.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        sidebar.add(createFieldGroup("Ghi chú", areaNotes));

        sidebar.add(Box.createVerticalGlue());

        sidebar.add(createActionButton("Tạo / Lưu", COLOR_SECONDARY));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createActionButton("Thanh Toán", COLOR_PRIMARY));

        return sidebar;
    }

    private JPanel createSectionTitle(String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_TITLE);
        wrapper.add(lbl, BorderLayout.WEST);

        return wrapper;
    }

    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel group = new JPanel(new BorderLayout(0, 5));
        group.setOpaque(false);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL);

        if (component instanceof JTextField || component instanceof JComboBox) {
            component.setPreferredSize(new Dimension(component.getPreferredSize().width, 30));
            if (component instanceof JTextField) {
                ((JTextField) component).setHorizontalAlignment(JTextField.CENTER);
            }
        }

        group.add(lbl, BorderLayout.NORTH);
        group.add(component, BorderLayout.CENTER);
        group.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

        return group;
    }

    private RoundedTextField createReadOnlyField(String text) {
        RoundedTextField field = new RoundedTextField(text, 10);
        field.setEditable(false);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(new Color(235, 235, 235));
        return field;
    }

    private void dateFinderStyle(DatePicker datePicker) {
        datePicker.setBackground(Color.WHITE);
        datePicker.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));
        datePicker.getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
    }

    private RoundedButton createActionButton(String text, Color bg) {
        RoundedButton btn = new RoundedButton(text);
        btn.setFont(FONT_LABEL);
        btn.setBackground(bg);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }
}

