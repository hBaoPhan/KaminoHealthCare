package com.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class LoPanel extends JPanel {

    public LoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);

        add(splitPane, BorderLayout.CENTER);
    }

    // ==========================================
    // PANEL BÊN TRÁI (Danh sách lô hàng)
    // ==========================================
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Thanh công cụ
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(new Color(245, 245, 245));

        JLabel lblDanhSachLo = new JLabel("Danh sách lô");
        lblDanhSachLo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDanhSachLo.setForeground(new Color(50, 50, 50));
        lblDanhSachLo.setPreferredSize(new Dimension(150, 35));

        JTextField txtSearch = new JTextField("Tìm kiếm mã lô...");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(220, 35));
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Tìm kiếm mã lô...".equals(txtSearch.getText().trim())) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Tìm kiếm mã lô...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        RoundedButton btnSearch = new RoundedButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(80, 35));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setContentAreaFilled(false);
        btnSearch.setBorder(BorderFactory.createEmptyBorder(5, 18, 5, 18));
        btnSearch.addActionListener(e -> JOptionPane.showMessageDialog(
                LoPanel.this,
                "Tìm kiếm theo mã: " + txtSearch.getText().trim(),
                "Tìm kiếm",
                JOptionPane.INFORMATION_MESSAGE
        ));

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchWrapper.setBackground(new Color(245, 245, 245));
        searchWrapper.add(txtSearch);
        searchWrapper.add(btnSearch);

        topBar.add(lblDanhSachLo, BorderLayout.WEST);
        topBar.add(searchWrapper, BorderLayout.EAST);

        // 2. Bảng dữ liệu (JTable) được căn chỉnh cho đẹp
        String[] columns = {"STT", "Mã lô", "Số lô", "Ngày hết hạn", "SL sản phẩm", "Giá nhập"};
        Object[][] data = {
            {"1", "LO001", "SL-A100", "20/12/2026", "500", "45.000"},
            {"2", "LO002", "SL-B205", "15/01/2027", "1.200", "12.500"},
            {"3", "LO003", "SL-C330", "01/05/2026", "300", "150.000"},
            {"4", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"5", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"6", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"7", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"8", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"9", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"10", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"11", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"12", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"13", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"14", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
            {"15", "LO004", "SL-D440", "10/10/2026", "850", "25.000"},
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        // Custom JTable để đổi màu nền xen kẽ các dòng
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Dòng chẵn màu trắng, dòng lẻ màu xám xanh cực nhạt
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                } else {
                    // Màu khi click chọn dòng
                    c.setBackground(new Color(204, 232, 255));
                }
                return c;
            }
        };

        // --- CĂN CHỈNH BẢNG & KẺ KHUNG NHẠT ---
        table.setRowHeight(35); // Tăng chiều cao dòng cho thoáng
        table.setShowGrid(true); // Bật lưới
        table.setGridColor(new Color(230, 230, 230)); // Kẻ khung màu xám nhạt
        table.setIntercellSpacing(new Dimension(0, 0)); // Xóa khoảng trống thừa giữa các ô
        
        // Header bảng
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.getTableHeader().setPreferredSize(new Dimension(100, 40));
        table.getTableHeader().setReorderingAllowed(false);
        
        // Renderers cho việc căn chỉnh nội dung
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Renderer căn phải, có thêm padding phải 5px cho số không bị sát lề
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
                return this;
            }
        };
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        // Áp dụng căn giữa
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // STT
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Mã lô
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Số lô
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Ngày hết hạn

        // Áp dụng căn phải cho số liệu
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);  // SL sản phẩm
        table.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);  // Giá nhập
        
        // Chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(table);
        // Viền nhạt bao quanh bảng
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE); // Đáy bảng màu trắng

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        return leftPanel;
    }

    // ==========================================
    // PANEL BÊN PHẢI (Thông tin nhập lô)
    // ==========================================
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)
        ));

        JLabel lblTitle = new JLabel("THÔNG TIN NHẬP LÔ", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 0, 10, 0));
        rightPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(0, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5); 
        int row = 0;

        addResponsiveFormField(formPanel, gbc, row++, "Mã lô:", new JTextField("Tự động tạo..."), false);
        addResponsiveFormField(formPanel, gbc, row++, "Số lô:", new JTextField(), true);
        addResponsiveFormField(formPanel, gbc, row++, "Ngày hết hạn:", new JTextField("dd/mm/yyyy"), true);
        addResponsiveFormField(formPanel, gbc, row++, "Số lượng SP:", new JTextField(), true);
        
        addResponsiveFormField(formPanel, gbc, row++, "Giá nhập:", new JTextField(), true);

        JPanel spacer = new JPanel();
        spacer.setBackground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = row++; 
        gbc.gridwidth = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(spacer, gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnThem = createStyledButton("Thêm lô hàng", new Color(95, 219, 221), Color.BLACK);
        btnThem.setPreferredSize(new Dimension(150, 40));
        
        JButton btnLamMoi = createStyledButton("Làm mới", new Color(240, 240, 240), Color.BLACK);
        btnLamMoi.setPreferredSize(new Dimension(120, 40));

        buttonPanel.add(btnThem);
        buttonPanel.add(btnLamMoi);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void addResponsiveFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent inputComp, boolean isEditable) {
        gbc.gridwidth = 1; 
        
        gbc.gridx = 0; gbc.gridy = row; 
        gbc.weightx = 0; gbc.weighty = 0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);

        if (inputComp instanceof JTextField) {
            JTextField txt = (JTextField) inputComp;
            txt.setEditable(isEditable);
            if (!isEditable) {
                txt.setBackground(new Color(235, 235, 235));
                txt.setForeground(Color.GRAY);
            }
            txt.setPreferredSize(new Dimension(150, 32));
        } else if (inputComp instanceof JComboBox) {
            inputComp.setPreferredSize(new Dimension(150, 32));
        }

        gbc.gridx = 1; gbc.gridy = row; 
        gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(inputComp, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 18, 18));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getForeground());
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 18, 18));
            g2.dispose();
        }
    }
}