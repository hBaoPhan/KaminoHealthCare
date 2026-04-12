package com.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DoiHangPanel extends JPanel {

    private JTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien, txtKhachDua, txtTienThoi;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JCheckBox chkTiềnMặt, chkChuyenKhoan;

    public DoiHangPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // --- PHẦN BÊN TRÁI: HAI BẢNG DỮ LIỆU ---
        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);

        // Bảng 1: Tìm kiếm mã hóa đơn
        pnlLeft.add(createTablePanel("Danh sách sản phẩm hóa đơn gốc", "Mã hóa đơn"));
        
        // Bảng 2: Tìm kiếm mã sản phẩm
        pnlLeft.add(createTablePanel("Danh sách sản phẩm", "Mã sản phẩm"));

        add(pnlLeft, BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);
    }

    /**
     * Tạo Panel chứa bảng và Header có tiêu đề, ô nhập (placeholder) và nút Tìm
     */
    private JPanel createTablePanel(String title, String placeholder) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        // Header Panel (Tiêu đề bên trái - Cụm tìm kiếm bên phải)
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Panel chứa Ô nhập + Nút Tìm
        JPanel pnlSearchAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlSearchAction.setOpaque(false);

        JTextField txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(150, 30));
        txtSearch.setText(placeholder); // Thiết lập placeholder
        txtSearch.setForeground(Color.GRAY);
        
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals(placeholder)) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(placeholder);
                }
            }
        });

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(65, 30));
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btnSearch.addActionListener(e -> {
            String input = txtSearch.getText();
            if (!input.equals(placeholder)) {
                JOptionPane.showMessageDialog(this, "Đang tìm kiếm: " + input);
            }
        });

        pnlSearchAction.add(txtSearch);
        pnlSearchAction.add(btnSearch);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearchAction, BorderLayout.EAST);

        // Bảng dữ liệu
        String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 15);
        JTable table = new JTable(model);
        table.setRowHeight(28);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));

        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(scrollPane, BorderLayout.CENTER);

        return pnl;
    }

    /**
     * Panel thông tin hóa đơn bên phải
     */
    private JPanel createInfoPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setPreferredSize(new Dimension(380, 0));
        pnlMain.setBackground(Color.WHITE);
        pnlMain.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel lblTitle = new JLabel("Hóa đơn đổi hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        pnlMain.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 15, 3, 15);
        gbc.weightx = 1.0;

        int r = 0;
        addInputRow(pnlContent, "Mã hóa gốc", txtMaHoaGoc = new JTextField("HDB27032026001"), gbc, r++);
        addInputRow(pnlContent, "Mã hóa đơn", txtMaHoaDon = new JTextField("HDĐ27032026001"), gbc, r++);
        addInputRow(pnlContent, "Ngày tạo", txtNgayTao = new JTextField("27/03/2026"), gbc, r++);
        addInputRow(pnlContent, "Người tạo", txtNguoiTao = new JTextField("Phan Hoài Bảo"), gbc, r++);
        addInputRow(pnlContent, "Tên khách hàng", txtTenKhachHang = new JTextField("Tran Tan Tai"), gbc, r++);

        gbc.gridy = r++;
        pnlContent.add(new JLabel("Ghi chú"), gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlContent.add(new JScrollPane(txtGhiChu), gbc);

        gbc.gridy = r++;
        pnlContent.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        addInputRow(pnlContent, "Tiền hóa đơn gốc :", txtTienGoc = new JTextField("220.500Đ"), gbc, r++);
        addInputRow(pnlContent, "Tiền hóa đơn đổi :", txtTienDoi = new JTextField("105.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Chênh lệch giá :", txtChenhLech = new JTextField("114.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Tổng tiền thuế:", txtThue = new JTextField("5.250Đ"), gbc, r++);
        addInputRow(pnlContent, "Thành tiền :", txtThanhTien = new JTextField("114.750Đ"), gbc, r++);

        JPanel pnlPayMethod = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlPayMethod.setOpaque(false);
        pnlPayMethod.add(new JLabel("Phương thức thanh toán:"));
        pnlPayMethod.add(chkTiềnMặt = new JCheckBox("Tiền mặt", true));
        pnlPayMethod.add(chkChuyenKhoan = new JCheckBox("Chuyển khoản"));
        gbc.gridy = r++;
        pnlContent.add(pnlPayMethod, gbc);

        addInputRow(pnlContent, "Tiền khách đưa:", txtKhachDua = new JTextField("150.000Đ"), gbc, r++);
        addInputRow(pnlContent, "Tiền thối lại:", txtTienThoi = new JTextField("35.000Đ"), gbc, r++);

        btnThanhToan = new JButton("Thanh Toán");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThanhToan.setPreferredSize(new Dimension(0, 45));

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(Color.WHITE);
        pnlBottom.setBorder(new EmptyBorder(10, 15, 20, 15));
        pnlBottom.add(btnThanhToan, BorderLayout.CENTER);

        pnlMain.add(pnlContent, BorderLayout.CENTER);
        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        setupReadOnlyFields();
        return pnlMain;
    }

    private void addInputRow(JPanel pnl, String labelText, JTextField txt, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(130, 25));
        rowPanel.add(lbl, BorderLayout.WEST);
        rowPanel.add(txt, BorderLayout.CENTER);
        pnl.add(rowPanel, gbc);
    }

    private void setupReadOnlyFields() {
        JTextField[] readonly = {txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang, 
                                 txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien, txtTienThoi};
        for (JTextField f : readonly) {
            f.setEditable(false);
            f.setBackground(new Color(225, 225, 225));
            f.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
    }
}