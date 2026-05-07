package com.example.gui.screens;

import com.example.dao.LoDAO;
import com.example.entity.Lo;
import com.example.entity.SanPham;
import com.toedter.calendar.JDateChooser; // ← Cần import này
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class LoPanel extends JPanel {

    private final LoDAO loDAO = new LoDAO();
    private final com.example.dao.SanPhamDAO sanPhamDAO = new com.example.dao.SanPhamDAO();
    private JTable table;
    private DefaultTableModel model;

    private JTextField txtMaLo, txtSoLo, txtMaSanPham, txtSoLuong, txtGiaNhap;
    private JDateChooser dateChooserNgayHetHan; // ← Thay thế cho JTextField ngày

    private List<Lo> danhSachLo;
    private List<SanPham> danhSachSanPham;
    private JPopupMenu searchPopup;
    private boolean isUpdatingSearch = false;

    public LoPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);

        add(splitPane, BorderLayout.CENTER);

        loadDataToTable();
        lamMoi();
    }

    // ==========================================
    // PANEL BÊN TRÁI
    // ==========================================
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(new Color(245, 245, 245));

        JLabel lblDanhSachLo = new JLabel("Danh sách lô hàng");
        lblDanhSachLo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDanhSachLo.setForeground(new Color(50, 50, 50));

        JTextField txtSearch = new JTextField("Tìm kiếm theo mã sản phẩm...");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(280, 35));

        searchPopup = new JPopupMenu();
        searchPopup.setFocusable(false);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if ("Tìm kiếm theo mã sản phẩm...".equals(txtSearch.getText().trim())) {
                    isUpdatingSearch = true;
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                    isUpdatingSearch = false;
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    isUpdatingSearch = true;
                    txtSearch.setText("Tìm kiếm theo mã sản phẩm...");
                    txtSearch.setForeground(Color.GRAY);
                    isUpdatingSearch = false;
                }
            }
        });

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch(txtSearch);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch(txtSearch);
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch(txtSearch);
            }
        });

        JButton btnSearch = new RoundedButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(80, 35));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> {
            searchPopup.setVisible(false);
            locVaHienThiLo(txtSearch.getText().trim());
        });
        txtSearch.addActionListener(e -> {
            searchPopup.setVisible(false);
            locVaHienThiLo(txtSearch.getText().trim());
        });

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchWrapper.setBackground(new Color(245, 245, 245));
        searchWrapper.add(txtSearch);
        searchWrapper.add(btnSearch);

        topBar.add(lblDanhSachLo, BorderLayout.WEST);
        topBar.add(searchWrapper, BorderLayout.EAST);

        // Bảng
        String[] columns = { "STT", "Mã lô", "Số lô", "Mã SP", "Ngày hết hạn", "SL tồn", "Giá nhập" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                String maLo = (String) getValueAt(row, 1);
                boolean isExpiredOrNear = false;
                if (danhSachLo != null) {
                    for (Lo lo : danhSachLo) {
                        if (lo.getMaLo().equals(maLo)) {
                            // Hết hạn hoặc gần hết hạn (<= 30 ngày)
                            if (!lo.getNgayHetHan().isAfter(java.time.LocalDate.now().plusDays(30))) {
                                isExpiredOrNear = true;
                            }
                            break;
                        }
                    }
                }

                if (!isRowSelected(row)) {
                    if (isExpiredOrNear) {
                        c.setBackground(new Color(230, 230, 230)); // Xám
                        c.setForeground(new Color(150, 150, 150)); // Chữ xám
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                        c.setForeground(Color.BLACK);
                    }
                } else {
                    c.setBackground(new Color(203, 213, 225));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(240, 240, 240));

        // Double-click hoặc click để load form sửa
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadSelectedRowToForm(); // Load dữ liệu khi click
            }
        });

        // Renderer
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(110);

        for (int i = 0; i < 7; i++) {
            if (i == 5 || i == 6)
                table.getColumnModel().getColumn(i).setCellRenderer(right);
            else
                table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        return leftPanel;
    }

    // ==========================================
    // PANEL BÊN PHẢI - FORM
    // ==========================================
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)));

        JLabel lblTitle = new JLabel("THÔNG TIN LÔ HÀNG", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        rightPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        int row = 0;

        txtMaLo = new JTextField();
        txtMaLo.setEditable(false);
        txtMaLo.setBackground(new Color(235, 235, 235));

        txtSoLo = new JTextField();
        txtMaSanPham = new JTextField();
        txtSoLuong = new JTextField();
        txtGiaNhap = new JTextField();

        // === DATE CHOOSER ===
        dateChooserNgayHetHan = new JDateChooser();
        dateChooserNgayHetHan.setDateFormatString("dd/MM/yyyy");
        dateChooserNgayHetHan.setPreferredSize(new Dimension(200, 32));

        addResponsiveFormField(formPanel, gbc, row++, "Mã lô:", txtMaLo, false);
        addResponsiveFormField(formPanel, gbc, row++, "Số lô:", txtSoLo, true);
        addResponsiveFormField(formPanel, gbc, row++, "Mã sản phẩm:", txtMaSanPham, true);
        addDateField(formPanel, gbc, row++, "Ngày hết hạn:", dateChooserNgayHetHan);
        addResponsiveFormField(formPanel, gbc, row++, "Số lượng SP:", txtSoLuong, true);
        addResponsiveFormField(formPanel, gbc, row++, "Giá nhập (VNĐ):", txtGiaNhap, true);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnThem = createStyledButton("Thêm", new Color(40, 167, 69), Color.WHITE);
        JButton btnSua = createStyledButton("Sửa", new Color(0, 123, 255), Color.WHITE);
        JButton btnXoa = createStyledButton("Xóa", new Color(220, 53, 69), Color.WHITE);
        JButton btnLamMoi = createStyledButton("Làm mới", new Color(108, 117, 125), Color.WHITE);

        btnThem.addActionListener(e -> themLo());
        btnSua.addActionListener(e -> suaLo());
        btnXoa.addActionListener(e -> xoaLo());
        btnLamMoi.addActionListener(e -> lamMoi());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void addResponsiveFormField(JPanel panel, GridBagConstraints gbc, int row,
            String labelText, JComponent inputComp, boolean isEditable) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);

        if (inputComp instanceof JTextField txt) {
            txt.setEditable(isEditable);
            if (!isEditable)
                txt.setBackground(new Color(235, 235, 235));
            txt.setPreferredSize(new Dimension(200, 32));
        }

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(inputComp, gbc);
    }

    private void addDateField(JPanel panel, GridBagConstraints gbc, int row,
            String labelText, JDateChooser dateChooser) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(dateChooser, gbc);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(110, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ====================== CÁC HÀM XỬ LÝ ======================

    private void loadDataToTable() {
        if (danhSachSanPham == null)
            danhSachSanPham = sanPhamDAO.layTatCa();
        danhSachLo = loDAO.layTatCa();

        boolean hasChanged = false;
        java.time.LocalDate now = java.time.LocalDate.now();
        for (Lo lo : danhSachLo) {
            if (lo.getSoLuongSanPham() > 0 && !lo.getNgayHetHan().isAfter(now.plusDays(30))) {
                lo.setSoLuongSanPham(0);
                loDAO.capNhatLo(lo);
                hasChanged = true;
            }
        }

        if (hasChanged) {
            danhSachLo = loDAO.layTatCa();
        }

        hienThiLoLenBang(danhSachLo);
    }

    private void hienThiLoLenBang(List<Lo> list) {
        model.setRowCount(0);
        if (list == null)
            return;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        int stt = 1;
        for (Lo lo : list) {
            model.addRow(new Object[] {
                    stt++,
                    lo.getMaLo(),
                    lo.getSoLo(),
                    lo.getSanPham().getMaSanPham(),
                    sdf.format(java.sql.Date.valueOf(lo.getNgayHetHan())),
                    lo.getSoLuongSanPham(),
                    String.format("%,.0f", lo.getGiaNhap())
            });
        }
    }

    private void updateSearch(JTextField txtSearch) {
        if (isUpdatingSearch)
            return;

        String text = txtSearch.getText().trim();
        if (text.isEmpty() || text.equals("Tìm kiếm lô theo mã sản phẩm...")) {
            searchPopup.setVisible(false);
            if (text.isEmpty()) {
                hienThiLoLenBang(danhSachLo);
            }
            return;
        }

        SwingUtilities.invokeLater(() -> {
            List<SanPham> results = new java.util.ArrayList<>();
            if (danhSachSanPham == null)
                danhSachSanPham = sanPhamDAO.layTatCa();
            for (SanPham sp : danhSachSanPham) {
                if (sp.getMaSanPham().toLowerCase().contains(text.toLowerCase())) {
                    results.add(sp);
                }
            }

            searchPopup.removeAll();
            if (results.isEmpty()) {
                searchPopup.setVisible(false);
                return;
            }

            int count = 0;
            for (SanPham sp : results) {
                if (count >= 10)
                    break;
                JMenuItem item = new JMenuItem(sp.getMaSanPham() + " - " + sp.getTenSanPham());
                item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                item.addActionListener(e -> {
                    isUpdatingSearch = true;
                    txtSearch.setText(sp.getMaSanPham());
                    isUpdatingSearch = false;
                    searchPopup.setVisible(false);

                    List<Lo> locKetQua = new java.util.ArrayList<>();
                    if (danhSachLo != null) {
                        for (Lo lo : danhSachLo) {
                            if (lo.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                                locKetQua.add(lo);
                            }
                        }
                    }
                    hienThiLoLenBang(locKetQua);
                });
                searchPopup.add(item);
                count++;
            }

            if (txtSearch.isShowing()) {
                searchPopup.show(txtSearch, 0, txtSearch.getHeight());
                txtSearch.requestFocus();
            }
        });
    }

    private void locVaHienThiLo(String text) {
        if (text.isEmpty() || text.equals("Tìm kiếm lô theo mã sản phẩm...")) {
            hienThiLoLenBang(danhSachLo);
            return;
        }

        List<Lo> locKetQua = new java.util.ArrayList<>();
        if (danhSachLo != null) {
            for (Lo lo : danhSachLo) {
                String maSP = lo.getSanPham().getMaSanPham().toLowerCase();
                if (maSP.contains(text.toLowerCase())) {
                    locKetQua.add(lo);
                }
            }
        }
        hienThiLoLenBang(locKetQua);
    }

    private void loadNewMaLo() {
        txtMaLo.setText(loDAO.sinhMaLo());
    }

    private void lamMoi() {
        loadNewMaLo();
        txtSoLo.setText("");
        txtMaSanPham.setText("");
        dateChooserNgayHetHan.setDate(new Date()); // Ngày hiện tại
        txtSoLuong.setText("");
        txtGiaNhap.setText("");
        table.clearSelection();
    }

    /** Load dữ liệu từ bảng lên form để sửa */
    private void loadSelectedRowToForm() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        try {
            txtMaLo.setText((String) model.getValueAt(row, 1));
            txtSoLo.setText((String) model.getValueAt(row, 2));
            txtMaSanPham.setText((String) model.getValueAt(row, 3));

            // Chuyển ngày từ String sang Date cho JDateChooser
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse((String) model.getValueAt(row, 4));
            dateChooserNgayHetHan.setDate(date);

            txtSoLuong.setText(model.getValueAt(row, 5).toString());
            txtGiaNhap.setText(model.getValueAt(row, 6).toString().replace(",", ""));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void themLo() { /* giữ nguyên như cũ */
        // ... (code thêm lô giống trước)
        try {
            if (txtSoLo.getText().trim().isEmpty() || txtMaSanPham.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Lo lo = new Lo();
            lo.setMaLo(txtMaLo.getText());
            lo.setSoLo(txtSoLo.getText().trim());
            lo.setNgayHetHan(dateChooserNgayHetHan.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            lo.setSoLuongSanPham(Integer.parseInt(txtSoLuong.getText().trim()));
            lo.setSanPham(new SanPham(txtMaSanPham.getText().trim()));
            lo.setGiaNhap(Double.parseDouble(txtGiaNhap.getText().trim().replace(",", "")));

            if (loDAO.themLo(lo)) {
                JOptionPane.showMessageDialog(this, "Thêm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
                lamMoi();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaLo() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lô cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Lo lo = new Lo();
            lo.setMaLo(txtMaLo.getText());
            lo.setSoLo(txtSoLo.getText().trim());
            lo.setNgayHetHan(dateChooserNgayHetHan.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            lo.setSoLuongSanPham(Integer.parseInt(txtSoLuong.getText().trim()));
            lo.setSanPham(new SanPham(txtMaSanPham.getText().trim()));
            lo.setGiaNhap(Double.parseDouble(txtGiaNhap.getText().trim().replace(",", "")));

            if (loDAO.capNhatLo(lo)) {
                JOptionPane.showMessageDialog(this, "Cập nhật lô hàng thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDataToTable();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaLo() {
        int row = table.getSelectedRow();
        if (row == -1)
            return;

        String maLo = (String) model.getValueAt(row, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa lô " + maLo + "?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION && loDAO.xoaLo(maLo)) {
            loadDataToTable();
            lamMoi();
        }
    }

    // RoundedButton class (giữ nguyên)
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
    }
}