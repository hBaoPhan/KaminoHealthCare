package com.example.gui.screens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class DoiHangPanel extends JPanel {

    private JTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien, txtKhachDua, txtTienThoi;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JRadioButton radTienMat, radChuyenKhoan;
    private JPanel pnlDynamicContent;
    private JTable tblHoaDonGoc, tblSanPham;

    public DoiHangPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // --- PHẦN BÊN TRÁI: HAI BẢNG DỮ LIỆU ---
        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);

        // Bảng 1: Chi tiết hóa đơn gốc
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn gốc", "Tìm hóa đơn:", "Nhập mã hóa đơn gốc", tblHoaDonGoc = createTable()));
        
        // Bảng 2: Chi tiết hóa đơn đổi
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn đổi", "Tìm kiếm sản phẩm:", "Nhập mã/tên sản phẩm", tblSanPham = createTable()));

        add(pnlLeft, BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);
    }

    private JTable createTable() {
        String[] columns = { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(columns, 5);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.getColumnModel().getColumn(2).setCellEditor(new UnitOfMeasureCellEditor());
        table.getColumnModel().getColumn(3).setCellRenderer(new QuantitySpinnerRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());

        return table;
    }

    private JPanel createTablePanel(String title, String labelSearch, String placeholder, JTable table) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        // Header của bảng gồm Tiêu đề và Thanh tìm kiếm
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        // Thanh tìm kiếm bên phải tiêu đề
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel(labelSearch);
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JTextField txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(200, 30));
        // Giả lập placeholder bằng ToolTip hoặc bạn có thể dùng PromptSupport (nếu có thư viện)
        txtSearch.setToolTipText(placeholder); 
        
        pnlSearch.add(lblSearch);
        pnlSearch.add(txtSearch);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
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
        gbc.insets = new Insets(4, 15, 4, 15);
        gbc.weightx = 1.0;

        int r = 0;
        addInputRow(pnlContent, "Mã hóa gốc:", txtMaHoaGoc = new JTextField("HDB27032026001"), gbc, r++);
        addInputRow(pnlContent, "Mã hóa đơn:", txtMaHoaDon = new JTextField("HDĐ27032026001"), gbc, r++);
        addInputRow(pnlContent, "Ngày tạo:", txtNgayTao = new JTextField("27/04/2026"), gbc, r++);
        addInputRow(pnlContent, "Người tạo:", txtNguoiTao = new JTextField("Phan Hoài Bảo"), gbc, r++);
        addInputRow(pnlContent, "Khách hàng:", txtTenKhachHang = new JTextField("Tran Tan Tai"), gbc, r++);

        gbc.gridy = r++;
        pnlContent.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(2, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlContent.add(new JScrollPane(txtGhiChu), gbc);

        addInputRow(pnlContent, "Tiền HĐ gốc:", txtTienGoc = new JTextField("220.500"), gbc, r++);
        addInputRow(pnlContent, "Tiền HĐ đổi:", txtTienDoi = new JTextField("105.750"), gbc, r++);
        addInputRow(pnlContent, "Chênh lệch:", txtChenhLech = new JTextField("114.750"), gbc, r++);
        addInputRow(pnlContent, "Thuế:", txtThue = new JTextField("5.250"), gbc, r++);
        addInputRow(pnlContent, "Thành tiền:", txtThanhTien = new JTextField("114.750"), gbc, r++);

        // --- PT THANH TOÁN ---
        gbc.gridy = r++;
        JPanel pnlRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        pnlRadio.setOpaque(false);
        pnlRadio.add(new JLabel("PT Thanh toán: "));
        radTienMat = new JRadioButton("Tiền mặt", true);
        radChuyenKhoan = new JRadioButton("Chuyển khoản");
        ButtonGroup bg = new ButtonGroup();
        bg.add(radTienMat); bg.add(radChuyenKhoan);
        pnlRadio.add(radTienMat); pnlRadio.add(radChuyenKhoan);
        pnlContent.add(pnlRadio, gbc);

        // --- NỘI DUNG ĐỘNG (TIỀN MẶT / QR) ---
        gbc.gridy = r++;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        pnlDynamicContent = new JPanel(new CardLayout());
        pnlDynamicContent.setOpaque(false);
        
        // Giao diện Tiền mặt
        JPanel pnlCash = new JPanel(new GridBagLayout());
        pnlCash.setOpaque(false);
        GridBagConstraints gbcCash = new GridBagConstraints();
        gbcCash.fill = GridBagConstraints.HORIZONTAL;
        gbcCash.weightx = 1.0;
        gbcCash.insets = new Insets(0, 0, 8, 0);
        
        txtKhachDua = new JTextField();
        txtTienThoi = new JTextField();
        txtTienThoi.setEditable(false);
        txtTienThoi.setBackground(new Color(245, 245, 245));

        addInputRow(pnlCash, "Tiền khách đưa:", txtKhachDua, gbcCash, 0);
        addInputRow(pnlCash, "Tiền thối lại:", txtTienThoi, gbcCash, 1);
        pnlDynamicContent.add(pnlCash, "CASH");

        // Giao diện Chuyển khoản
        pnlDynamicContent.add(createQRPanel(), "QR");
        pnlContent.add(pnlDynamicContent, gbc);

        radTienMat.addActionListener(e -> ((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "CASH"));
        radChuyenKhoan.addActionListener(e -> ((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "QR"));

        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThanhToan.setPreferredSize(new Dimension(0, 50));

        pnlMain.add(pnlContent, BorderLayout.CENTER);
        pnlMain.add(btnThanhToan, BorderLayout.SOUTH);

        setupReadOnlyFields();
        return pnlMain;
    }

    private JPanel createQRPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createTitledBorder("Mã QR Thanh Toán"));
        JLabel lblQR = new JLabel("HÌNH ẢNH MÃ QR", SwingConstants.CENTER);
        lblQR.setPreferredSize(new Dimension(0, 150));
        lblQR.setOpaque(true);
        lblQR.setBackground(Color.WHITE);
        lblQR.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnl.add(lblQR, BorderLayout.CENTER);
        return pnl;
    }

    private void addInputRow(JPanel pnl, String labelText, JTextField txt, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(120, 25));
        rowPanel.add(lbl, BorderLayout.WEST);
        rowPanel.add(txt, BorderLayout.CENTER);
        pnl.add(rowPanel, gbc);
    }

    private void setupReadOnlyFields() {
        JTextField[] readonly = { txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang, 
                                  txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien };
        for (JTextField f : readonly) {
            f.setEditable(false);
            f.setBackground(new Color(245, 245, 245));
        }
    }

    // ================ CÁC LỚP HỖ TRỢ BẢNG ================

    private class UnitOfMeasureCellEditor extends DefaultCellEditor {
        public UnitOfMeasureCellEditor() {
            super(new JComboBox<>(new String[]{"Vỉ", "Viên", "Hộp", "Tuýp", "Chai"}));
            setClickCountToStart(1);
        }
    }

    private class QuantitySpinnerRenderer extends DefaultTableCellRenderer {
        public QuantitySpinnerRenderer() { setHorizontalAlignment(SwingConstants.RIGHT); }
    }

    private class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        public QuantitySpinnerEditor() {
            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.RIGHT);
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            spinner.setValue(v != null ? v : 1);
            return spinner;
        }
        @Override
        public Object getCellEditorValue() { return spinner.getValue(); }
    }
}