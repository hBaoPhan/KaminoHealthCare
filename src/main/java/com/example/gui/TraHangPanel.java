package com.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class TraHangPanel extends JPanel {

    private JTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienTra, txtChenhLech, txtThue, txtThanhTien, txtTienTraLai;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JCheckBox chkTienMat, chkChuyenKhoan;

    public TraHangPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // --- PHẦN BÊN TRÁI: DANH SÁCH SẢN PHẨM HÓA ĐƠN TRẢ ---
        add(createTablePanel("Danh sách sản phẩm hóa đơn trả", "Mã hóa đơn"), BorderLayout.CENTER);

        // --- PHẦN BÊN PHẢI: THÔNG TIN HÓA ĐƠN TRẢ HÀNG ---
        add(createInfoPanel(), BorderLayout.EAST);
    }

    /**
     * Tạo Panel chứa bảng dữ liệu và thanh tìm kiếm phía trên
     */
    private JPanel createTablePanel(String title, String placeholder) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        // Header Panel (Tiêu đề + Ô tìm kiếm)
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Panel chứa Ô nhập và Nút Tìm
        JPanel pnlSearchAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlSearchAction.setOpaque(false);

        JTextField txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(180, 30));
        txtSearch.setText(placeholder);
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

        pnlSearchAction.add(txtSearch);
        pnlSearchAction.add(btnSearch);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearchAction, BorderLayout.EAST);

        // Bảng dữ liệu
        String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền"};
        // Dữ liệu mẫu 
        Object[][] data = {
            {"PAR-100MG-V", "Paracetamol 100mg", "Hộp", 2, 100000, "5%", 105000},
            {"PAR-100MG-V", "Paracetamol 100mg", "Vỉ", 2, 10000, "5%", 10500}
        };
        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        
        for(int i=0; i<20; i++) model.addRow(new Object[]{});

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));

        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(scrollPane, BorderLayout.CENTER);

        return pnl;
    }

    /**
     * Panel thông tin hóa đơn trả hàng bên phải
     */
    private JPanel createInfoPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setPreferredSize(new Dimension(380, 0));
        pnlMain.setBackground(new Color(248, 248, 248));
        pnlMain.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel lblTitle = new JLabel("Hóa đơn trả hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        pnlMain.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 15, 3, 15);
        gbc.weightx = 1.0;

        int r = 0;
        // Phần thông tin chung
        addInputRow(pnlContent, "Mã hóa gốc", txtMaHoaGoc = new JTextField("HDB27032026001"), gbc, r++);
        addInputRow(pnlContent, "Mã hóa đơn", txtMaHoaDon = new JTextField("HDT27032026001"), gbc, r++);
        addInputRow(pnlContent, "Ngày tạo", txtNgayTao = new JTextField("27/03/2026"), gbc, r++);
        addInputRow(pnlContent, "Người tạo", txtNguoiTao = new JTextField("Phan Hoai Bao"), gbc, r++);
        addInputRow(pnlContent, "Tên khách hàng", txtTenKhachHang = new JTextField("Tran Tan Tai"), gbc, r++);

        gbc.gridy = r++;
        pnlContent.add(new JLabel("Chi chú"), gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(4, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlContent.add(new JScrollPane(txtGhiChu), gbc);

        gbc.gridy = r++;
        pnlContent.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        // Phần tính toán tiền
        addInputRow(pnlContent, "Tiền hóa đơn gốc :", txtTienGoc = new JTextField("220.500Đ"), gbc, r++);
        addInputRow(pnlContent, "Tiền hóa đơn trả :", txtTienTra = new JTextField("105.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Chênh lệch giá :", txtChenhLech = new JTextField("114.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Tổng tiền thuế:", txtThue = new JTextField("5.250Đ"), gbc, r++);
        addInputRow(pnlContent, "Thành tiền :", txtThanhTien = new JTextField("114.750Đ"), gbc, r++);

        // Phương thức thanh toán
        JPanel pnlPayMethod = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlPayMethod.setOpaque(false);
        pnlPayMethod.add(new JLabel("Phương thức thanh toán:"));
        pnlPayMethod.add(chkTienMat = new JCheckBox("Tiền mặt", true));
        pnlPayMethod.add(chkChuyenKhoan = new JCheckBox("Chuyển khoản"));
        gbc.gridy = r++;
        pnlContent.add(pnlPayMethod, gbc);

        addInputRow(pnlContent, "Tiền trả lại:", txtTienTraLai = new JTextField("114.750Đ"), gbc, r++);

        // Nút Thanh Toán
        btnThanhToan = new JButton("Thanh Toán");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThanhToan.setFocusPainted(false);
        btnThanhToan.setPreferredSize(new Dimension(0, 45));

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(10, 15, 20, 15));
        pnlBottom.add(btnThanhToan, BorderLayout.CENTER);

        pnlMain.add(pnlContent, BorderLayout.CENTER);
        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        setupStyles();
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

    private void setupStyles() {
        JTextField[] readonly = {txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang, 
                                 txtTienGoc, txtTienTra, txtChenhLech, txtThue, txtThanhTien, txtTienTraLai};
        for (JTextField f : readonly) {
            f.setEditable(false);
            f.setBackground(new Color(200, 200, 200));
            f.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            f.setHorizontalAlignment(JTextField.LEFT);
        }
    }
}