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
        RoundedPanel wrapper = new RoundedPanel(12, true);
        wrapper.setLayout(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(340, 0));
        wrapper.setBackground(COLOR_CARD_BG);
        wrapper.setBorder(new EmptyBorder(10, 5, 10, 5));

        JPanel sidebar = new ScrollableViewportPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setBorder(new EmptyBorder(0, 5, 0, 5));

        sidebar.add(createSectionTitle("Thông tin khách hàng"));
        sidebar.add(createFieldGroup("Số điện thoại", new RoundedTextField(10)));

        JCheckBox chkKhachLe = new JCheckBox("Khách lẻ");
        chkKhachLe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkKhachLe.setOpaque(false);

        JPanel chkWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chkWrapper.setOpaque(false);
        chkWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkWrapper.add(chkKhachLe);
        chkWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, chkKhachLe.getPreferredSize().height));

        sidebar.add(chkWrapper);

        sidebar.add(createSectionTitle("Thông tin hóa đơn"));
        sidebar.add(createFieldGroup("Mã hóa đơn", createReadOnlyField("HD27032026001")));

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

        JTextArea areaNotes = new JTextArea(5, 20);
        areaNotes.setLineWrap(true);
        areaNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(areaNotes);
        notesScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        sidebar.add(createFieldGroup("Ghi chú", notesScroll));

        sidebar.add(createPaymentSection());

        JScrollPane scrollPane = new JScrollPane(sidebar);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapper.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(8, 5, 0, 5));

        RoundedButton btnSave = createActionButton("Tạo / Lưu", COLOR_SECONDARY);
        RoundedButton btnPay = createActionButton("Thanh Toán", COLOR_PRIMARY);

        buttonPanel.add(btnSave);
        buttonPanel.add(Box.createVerticalStrut(8));
        buttonPanel.add(btnPay);

        wrapper.add(buttonPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createPaymentSection() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(5, 5, 5, 5)));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        headerPanel.setOpaque(false);
        JLabel lblThanhToan = new JLabel("Phương thức:");
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JRadioButton rdoTienMat = new JRadioButton("Tiền mặt", true);
        rdoTienMat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rdoTienMat.setOpaque(false);

        JRadioButton rdoChuyenKhoan = new JRadioButton("Chuyển khoản");
        rdoChuyenKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rdoChuyenKhoan.setOpaque(false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdoTienMat);
        bg.add(rdoChuyenKhoan);

        headerPanel.add(lblThanhToan);
        headerPanel.add(rdoTienMat);
        headerPanel.add(rdoChuyenKhoan);

        JPanel cards = new JPanel(new CardLayout());
        cards.setOpaque(false);

        JPanel pnlTienMat = new JPanel();
        pnlTienMat.setLayout(new BoxLayout(pnlTienMat, BoxLayout.Y_AXIS));
        pnlTienMat.setOpaque(false);
        pnlTienMat.add(createHorizontalGroup("Tiền khách đưa:", new RoundedTextField("500.000Đ", 10)));
        pnlTienMat.add(createHorizontalGroup("Tiền thối lại:", createReadOnlyField("289.500Đ")));

        JPanel pnlChuyenKhoan = new JPanel(new BorderLayout());
        pnlChuyenKhoan.setOpaque(false);
        JLabel lblQR = new JLabel("Mã QR", SwingConstants.CENTER);
        lblQR.setPreferredSize(new Dimension(80, 80));
        lblQR.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 2, true));
        lblQR.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQR.setForeground(COLOR_PRIMARY);
        JPanel qrContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        qrContainer.setOpaque(false);
        qrContainer.add(lblQR);
        pnlChuyenKhoan.add(qrContainer, BorderLayout.CENTER);

        cards.add(pnlTienMat, "TIEN_MAT");
        cards.add(pnlChuyenKhoan, "CHUYEN_KHOAN");

        container.add(headerPanel);
        container.add(cards);

        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, container.getPreferredSize().height));

        CardLayout cl = (CardLayout) cards.getLayout();
        rdoTienMat.addActionListener(e -> {
            cl.show(cards, "TIEN_MAT");
            cards.revalidate();
            cards.repaint();
        });
        rdoChuyenKhoan.addActionListener(e -> {
            cl.show(cards, "CHUYEN_KHOAN");
            cards.revalidate();
            cards.repaint();
        });

        return container;
    }

    private JPanel createHorizontalGroup(String labelText, JComponent component) {
        JPanel group = new JPanel(new BorderLayout(5, 0));
        group.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(110, 28));

        if (component instanceof JTextField) {
            component.setPreferredSize(new Dimension(140, 28));
            ((JTextField) component).setHorizontalAlignment(JTextField.LEFT);
            ((JTextField) component).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        JPanel pad = new JPanel(new BorderLayout());
        pad.setOpaque(false);
        pad.setBorder(new EmptyBorder(2, 5, 2, 5));
        pad.add(lbl, BorderLayout.WEST);
        pad.add(component, BorderLayout.CENTER);

        group.add(pad, BorderLayout.CENTER);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, group.getPreferredSize().height));
        return group;
    }

    private JPanel createSectionTitle(String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_TITLE);
        wrapper.add(lbl, BorderLayout.WEST);
        wrapper.setBorder(new EmptyBorder(5, 0, 2, 0));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, wrapper.getPreferredSize().height));

        return wrapper;
    }

    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel group = new JPanel(new BorderLayout(0, 2));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL);

        if (component instanceof JTextField || component instanceof JComboBox) {
            component.setPreferredSize(new Dimension(component.getPreferredSize().width, 28));
            if (component instanceof JTextField) {
                ((JTextField) component).setHorizontalAlignment(JTextField.CENTER);
            }
        }

        group.add(lbl, BorderLayout.NORTH);
        group.add(component, BorderLayout.CENTER);
        group.setBorder(new EmptyBorder(0, 0, 5, 0));
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, group.getPreferredSize().height));

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
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private class ScrollableViewportPanel extends JPanel implements Scrollable {
        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return super.getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
