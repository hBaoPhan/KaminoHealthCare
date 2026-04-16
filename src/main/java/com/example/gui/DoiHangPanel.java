package com.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;

public class DoiHangPanel extends JPanel {

    // Khai báo toàn bộ các thành phần giao diện
    private JTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien, txtKhachDua, txtTienThoi;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JRadioButton radTienMat, radChuyenKhoan;
    private JPanel pnlPaymentDetail; 

    public DoiHangPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // --- PHẦN BÊN TRÁI: HAI BẢNG DỮ LIỆU ---
        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);
        pnlLeft.add(createTablePanel("Danh sách sản phẩm hóa đơn gốc", "Mã hóa đơn"));
        pnlLeft.add(createTablePanel("Danh sách sản phẩm", "Mã sản phẩm"));

        add(pnlLeft, BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);
    }

    private JPanel createTablePanel(String title, String placeholder) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 5, 10));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        // Ô tìm kiếm
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlSearch.setOpaque(false);
        JTextField txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(150, 30));
        JButton btnSearch = new JButton("Tìm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        pnlSearch.add(txtSearch);
        pnlSearch.add(btnSearch);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        // Bảng dữ liệu
        String[] columns = {"Mã sản phẩm", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(columns, 8);
        JTable table = new JTable(model);
        
        // 1. In đậm tiêu đề bảng
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setRowHeight(30);

        // 2. Tăng giảm số lượng bằng Spinner (Cột index 3)
        table.getColumnModel().getColumn(3).setCellRenderer(new SpinnerRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new SpinnerEditor());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 10, 10, 10));
        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(scrollPane, BorderLayout.CENTER);

        return pnl;
    }

    private JPanel createInfoPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setPreferredSize(new Dimension(420, 0));
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
        // Khởi tạo và thêm các ô nhập liệu
        addInputRow(pnlContent, "Mã hóa gốc", txtMaHoaGoc = new JTextField("HDB27032026001"), gbc, r++);
        addInputRow(pnlContent, "Mã hóa đơn", txtMaHoaDon = new JTextField("HDĐ27032026001"), gbc, r++);
        addInputRow(pnlContent, "Ngày tạo", txtNgayTao = new JTextField("27/03/2026"), gbc, r++);
        addInputRow(pnlContent, "Người tạo", txtNguoiTao = new JTextField("Phan Hoài Bảo"), gbc, r++);
        addInputRow(pnlContent, "Tên khách hàng", txtTenKhachHang = new JTextField("Tran Tan Tai"), gbc, r++);

        gbc.gridy = r++;
        pnlContent.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(2, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlContent.add(new JScrollPane(txtGhiChu), gbc);

        addInputRow(pnlContent, "Tiền hóa đơn gốc :", txtTienGoc = new JTextField("220.500Đ"), gbc, r++);
        addInputRow(pnlContent, "Tiền hóa đơn đổi :", txtTienDoi = new JTextField("105.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Chênh lệch giá :", txtChenhLech = new JTextField("114.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Tổng tiền thuế:", txtThue = new JTextField("5.250Đ"), gbc, r++); // Khởi tạo txtThue
        addInputRow(pnlContent, "Thành tiền :", txtThanhTien = new JTextField("114.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Tiền khách đưa:", txtKhachDua = new JTextField("150.000Đ"), gbc, r++);
        addInputRow(pnlContent, "Tiền thối lại:", txtTienThoi = new JTextField("35.000Đ"), gbc, r++);

        // --- PHƯƠNG THỨC THANH TOÁN ---
        gbc.gridy = r++;
        JPanel pnlPaymentHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlPaymentHeader.setOpaque(false);
        pnlPaymentHeader.add(new JLabel("PT Thanh toán: "));
        radTienMat = new JRadioButton("Tiền mặt", true);
        radChuyenKhoan = new JRadioButton("Chuyển khoản");
        ButtonGroup bg = new ButtonGroup();
        bg.add(radTienMat); bg.add(radChuyenKhoan);
        pnlPaymentHeader.add(radTienMat); pnlPaymentHeader.add(radChuyenKhoan);
        pnlContent.add(pnlPaymentHeader, gbc);

        gbc.gridy = r++;
        pnlPaymentDetail = new JPanel(new CardLayout());
        pnlPaymentDetail.setPreferredSize(new Dimension(0, 150));
        pnlPaymentDetail.add(createCashPanel(), "CASH");
        pnlPaymentDetail.add(createQRPanel(), "QR");
        pnlContent.add(pnlPaymentDetail, gbc);

        // Sự kiện chuyển đổi
        radTienMat.addActionListener(e -> ((CardLayout)pnlPaymentDetail.getLayout()).show(pnlPaymentDetail, "CASH"));
        radChuyenKhoan.addActionListener(e -> ((CardLayout)pnlPaymentDetail.getLayout()).show(pnlPaymentDetail, "QR"));

        // Nút Thanh Toán
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(new EmptyBorder(10, 15, 20, 15));
        pnlBottom.setOpaque(false);
        pnlBottom.add(btnThanhToan, BorderLayout.CENTER);

        pnlMain.add(pnlContent, BorderLayout.CENTER);
        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        setupReadOnlyFields(); // Gọi sau khi tất cả biến đã được new
        return pnlMain;
    }

    private JPanel createCashPanel() {
        JPanel pnl = new JPanel(new GridLayout(3, 3, 5, 5));
        String[] bills = {"1.000", "2.000", "5.000", "10.000", "20.000", "50.000", "100.000", "200.000", "500.000"};
        for (String b : bills) pnl.add(new JButton(b));
        return pnl;
    }

    private JPanel createQRPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        JLabel lblQR = new JLabel("QUÉT MÃ QR TẠI ĐÂY", SwingConstants.CENTER);
        lblQR.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnl.add(lblQR, BorderLayout.CENTER);
        return pnl;
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
        // Danh sách đảm bảo không thiếu biến nào
        JTextField[] readonly = {
            txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang, 
            txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien, txtTienThoi, txtKhachDua
        };
        for (JTextField f : readonly) {
            if (f != null) {
                f.setEditable(false);
                f.setBackground(new Color(235, 235, 235));
                f.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        }
    }

    // --- INNER CLASSES ---
    class SpinnerRenderer extends JSpinner implements TableCellRenderer {
        public SpinnerRenderer() { setBorder(null); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r, int c) {
            setValue(v != null ? v : 0); return this;
        }
    }
    class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner s = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            s.setValue(v != null ? v : 0); return s;
        }
        public Object getCellEditorValue() { return s.getValue(); }
    }
}