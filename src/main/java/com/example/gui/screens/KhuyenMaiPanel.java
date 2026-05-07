package com.example.gui.screens;

import com.example.dao.DonViQuyDoiDAO;
import com.example.dao.KhuyenMaiDAO;
import com.example.dao.SanPhamDAO;
import com.example.entity.KhuyenMai;
import com.example.entity.QuaTang;
import com.example.entity.SanPham;
import com.example.entity.DonViQuyDoi;
import com.example.entity.enums.LoaiKhuyenMai;
import com.example.entity.enums.DonVi;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

// FIX 1: Sửa import MouseEvent từ DOM sang AWT
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KhuyenMaiPanel extends JPanel {

    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();

    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;

    private List<KhuyenMai> danhSachKhuyenMai;
    private int hoveredRow = -1;
    private int currentSelectedIndex = -1;

    private JButton btnThem, btnXoa, btnSua, btnLamMoi;
    private JTextField txtSearch;
    private JComboBox<String> cboFilterLoai;

    private JTextField txtMaKhuyenMai;
    private JTextField txtTenKhuyenMai;
    private DatePicker datePickerBatDau;
    private DatePicker datePickerKetThuc;
    private JComboBox<LoaiKhuyenMai> cboLoaiKhuyenMai;
    private JComboBox<DonVi> cboDonViQuaTang;
    private JTextField txtKhuyenMaiPhanTram;
    private JTextField txtSanPhamQuaTang;
    private JTextField txtSoLuongTang;
    private JTextField txtGiaTriDonHangToiThieu;
    
    private JPopupMenu popupMenuSuggestions;
    private JList<String> listSuggestions;
    private DefaultListModel<String> listModelSuggestions;
    private boolean isAdjusting = false;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);

        add(splitPane, BorderLayout.CENTER);

        loadDataFromDatabase();
        refreshTableData();
        // FIX 4: Gọi clearForm() khi khởi tạo để đảm bảo trạng thái nút đúng
        clearForm();
    }

    private void loadDataFromDatabase() {
        danhSachKhuyenMai = khuyenMaiDAO.layTatCa();
    }

    // ===================== LEFT PANEL - DANH SÁCH =====================
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(241, 246, 255));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(new Color(241, 246, 255));

        JLabel lblDanhSach = new JLabel("Danh sách khuyến mãi");
        lblDanhSach.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDanhSach.setForeground(new Color(35, 55, 75));

        txtSearch = new JTextField("Tìm kiếm mã/tên khuyến mãi...");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(240, 35));
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtSearch.getText().trim().startsWith("Tìm kiếm")) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Tìm kiếm mã/tên khuyến mãi...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnSearch = createStyledButton("Tìm", new Color(30, 144, 255), Color.WHITE);
        btnSearch.setPreferredSize(new Dimension(80, 35));
        btnSearch.addActionListener(e -> refreshTableData());

        String[] filterValues = {"Tất cả", "Phần trăm", "Tặng kèm"};
        cboFilterLoai = new JComboBox<>(filterValues);
        cboFilterLoai.setPreferredSize(new Dimension(130, 35));
        cboFilterLoai.addActionListener(e -> refreshTableData());

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchWrapper.setBackground(new Color(241, 246, 255));
        searchWrapper.add(txtSearch);
        searchWrapper.add(cboFilterLoai);
        searchWrapper.add(btnSearch);

        topBar.add(lblDanhSach, BorderLayout.WEST);
        topBar.add(searchWrapper, BorderLayout.EAST);

        // Table
        String[] columns = {"STT", "Mã KM", "Tên KM", "Loại KM", "Giảm (%)", "Quà tặng", "Giá trị ĐH tối thiểu", "Bắt đầu", "Kết thúc"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                String maKM = (String) getValueAt(row, 1);
                boolean isExpired = false;
                if (danhSachKhuyenMai != null) {
                    for (KhuyenMai km : danhSachKhuyenMai) {
                        if (km.getMaKhuyenMai().equals(maKM)) {
                            // Hết hạn nếu ngày kết thúc trước ngày hiện tại (hoặc cùng ngày nhưng đã qua thời gian, ở đây so sánh ngày)
                            if (km.getThoiGianKetThuc().toLocalDate().isBefore(java.time.LocalDate.now())) {
                                isExpired = true;
                            }
                            break;
                        }
                    }
                }

                if (!isRowSelected(row)) {
                    if (isExpired) {
                        c.setBackground(new Color(230, 230, 230)); // Nền xám nhạt
                        c.setForeground(new Color(150, 150, 150)); // Chữ xám nhạt
                    } else {
                        c.setBackground(row == hoveredRow ? new Color(226, 232, 240) :
                                       (row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250)));
                        c.setForeground(new Color(51, 51, 51));
                    }
                } else {
                    c.setBackground(new Color(203, 213, 225));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        table.setFocusable(false);
        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(238, 238, 238));
        table.getTableHeader().setPreferredSize(new Dimension(100, 40));

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    table.repaint();
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                table.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) loadKhuyenMaiToForm(row);
            }
        });

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(i == 6 ? rightRenderer : centerRenderer);
        }

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        return leftPanel;
    }

    // ===================== RIGHT PANEL - FORM =====================
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(10, 10, 10, 10),
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true)));

        JLabel lblTitle = new JLabel("THÔNG TIN KHUYẾN MÃI", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 0, 10, 0));
        rightPanel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(0, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaKhuyenMai = new JTextField();
        txtMaKhuyenMai.setEditable(false);
        txtMaKhuyenMai.setBackground(new Color(240, 240, 240));

        txtTenKhuyenMai = new JTextField();

        DatePickerSettings batDauSettings = new DatePickerSettings();
        batDauSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerBatDau = new DatePicker(batDauSettings);

        DatePickerSettings ketThucSettings = new DatePickerSettings();
        ketThucSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerKetThuc = new DatePicker(ketThucSettings);

        cboLoaiKhuyenMai = new JComboBox<>(LoaiKhuyenMai.values());
        cboLoaiKhuyenMai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LoaiKhuyenMai) {
                    setText(((LoaiKhuyenMai) value).getMoTa());
                }
                return this;
            }
        });

        txtKhuyenMaiPhanTram = new JTextField();
        txtSanPhamQuaTang    = new JTextField();
        txtSoLuongTang       = new JTextField();
        txtGiaTriDonHangToiThieu = new JTextField();

        setupAutoCompleteForSanPhamTang();

        cboDonViQuaTang = new JComboBox<>(DonVi.values());
        cboDonViQuaTang.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DonVi) {
                    setText(((DonVi) value).getMoTa());
                }
                return this;
            }
        });

        // FIX 2: Khai báo row TRƯỚC khi dùng, thêm field đúng thứ tự, không trùng lặp
        int row = 0;
        addFormField(formPanel, gbc, row++, "Mã khuyến mãi:", txtMaKhuyenMai);
        addFormField(formPanel, gbc, row++, "Tên khuyến mãi:", txtTenKhuyenMai);
        addFormField(formPanel, gbc, row++, "Ngày bắt đầu:", datePickerBatDau);
        addFormField(formPanel, gbc, row++, "Ngày kết thúc:", datePickerKetThuc);
        addFormField(formPanel, gbc, row++, "Loại khuyến mãi:", cboLoaiKhuyenMai);
        addFormField(formPanel, gbc, row++, "% giảm:", txtKhuyenMaiPhanTram);
        addFormField(formPanel, gbc, row++, "Sản phẩm tặng:", txtSanPhamQuaTang);

        // Row "Số lượng + Đơn vị" — layout đặc biệt: 2 control trên cùng 1 hàng
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lblSoLuong = new JLabel("Số lượng tặng:");
        lblSoLuong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        formPanel.add(lblSoLuong, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JPanel panelSoLuongDonVi = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelSoLuongDonVi.setBackground(Color.WHITE);
        txtSoLuongTang.setPreferredSize(new Dimension(100, 32));
        cboDonViQuaTang.setPreferredSize(new Dimension(120, 32));
        panelSoLuongDonVi.add(txtSoLuongTang);
        panelSoLuongDonVi.add(cboDonViQuaTang);
        formPanel.add(panelSoLuongDonVi, gbc);
        row++;

        addFormField(formPanel, gbc, row++, "Giá trị đơn hàng tối thiểu:", txtGiaTriDonHangToiThieu);

        cboLoaiKhuyenMai.addActionListener(e -> updateFormFieldsByLoai());
        updateFormFieldsByLoai();

        rightPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnThem   = createStyledButton("Thêm",    new Color(40, 167, 69),  Color.WHITE);
        btnSua    = createStyledButton("Sửa",     new Color(255, 193, 7),  Color.BLACK);
        btnXoa    = createStyledButton("Xóa",     new Color(220, 53, 69),  Color.WHITE);
        btnLamMoi = createStyledButton("Làm mới", new Color(108, 117, 125), Color.WHITE);

        btnThem.addActionListener(e -> themKhuyenMai());
        btnSua.addActionListener(e -> suaKhuyenMai());
        btnXoa.addActionListener(e -> xoaKhuyenMai());
        btnLamMoi.addActionListener(e -> clearForm());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        return rightPanel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent input) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(input, gbc);

        if (input instanceof JTextField) {
            ((JTextField) input).setPreferredSize(new Dimension(220, 32));
        } else {
            input.setPreferredSize(new Dimension(220, 32));
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setOpaque(true);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color hoverColor = bgColor.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hoverColor);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private void updateFormFieldsByLoai() {
        LoaiKhuyenMai loai = (LoaiKhuyenMai) cboLoaiKhuyenMai.getSelectedItem();
        boolean isPhanTram = loai == LoaiKhuyenMai.PHAN_TRAM;
        boolean isTangKem  = loai == LoaiKhuyenMai.TANG_KEM;

        txtKhuyenMaiPhanTram.setEnabled(isPhanTram);
        txtSanPhamQuaTang.setEnabled(isTangKem);
        txtSoLuongTang.setEnabled(isTangKem);
        cboDonViQuaTang.setEnabled(isTangKem);

        if (!isPhanTram) txtKhuyenMaiPhanTram.setText("");
        if (!isTangKem) {
            txtSanPhamQuaTang.setText("");
            txtSoLuongTang.setText("");
            if (cboDonViQuaTang.getItemCount() > 0) {
                cboDonViQuaTang.setSelectedIndex(0);
            }
        }
    }

    private void setupAutoCompleteForSanPhamTang() {
        popupMenuSuggestions = new JPopupMenu();
        listModelSuggestions = new DefaultListModel<>();
        listSuggestions = new JList<>(listModelSuggestions);
        listSuggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSuggestions.setVisibleRowCount(5);
        
        JScrollPane scrollPane = new JScrollPane(listSuggestions);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        popupMenuSuggestions.add(scrollPane);
        
        txtSanPhamQuaTang.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSuggestions(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSuggestions(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSuggestions(); }
        });
        
        txtSanPhamQuaTang.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (!isAdjusting) {
                    updateDonViCombo(txtSanPhamQuaTang.getText().trim());
                }
            }
        });

        listSuggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 || e.getClickCount() == 2) {
                    selectSuggestion();
                }
            }
        });
        
        txtSanPhamQuaTang.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (popupMenuSuggestions.isVisible() && listModelSuggestions.getSize() > 0) {
                        listSuggestions.requestFocusInWindow();
                        listSuggestions.setSelectedIndex(0);
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (popupMenuSuggestions.isVisible()) {
                        selectSuggestion();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popupMenuSuggestions.setVisible(false);
                }
            }
        });
        
        listSuggestions.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectSuggestion();
                } else if (e.getKeyCode() == KeyEvent.VK_UP && listSuggestions.getSelectedIndex() == 0) {
                    txtSanPhamQuaTang.requestFocusInWindow();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    popupMenuSuggestions.setVisible(false);
                    txtSanPhamQuaTang.requestFocusInWindow();
                }
            }
        });
    }

    private void updateSuggestions() {
        if (isAdjusting) return;
        
        String keyword = txtSanPhamQuaTang.getText().trim();
        if (keyword.isEmpty()) {
            popupMenuSuggestions.setVisible(false);
            return;
        }
        
        SwingUtilities.invokeLater(() -> {
            List<SanPham> suggestions = sanPhamDAO.timKiemGoiY(keyword);
            listModelSuggestions.clear();
            if (!suggestions.isEmpty()) {
                for (SanPham sp : suggestions) {
                    listModelSuggestions.addElement(sp.getTenSanPham());
                }
                
                if (!popupMenuSuggestions.isVisible()) {
                    popupMenuSuggestions.show(txtSanPhamQuaTang, 0, txtSanPhamQuaTang.getHeight());
                } else {
                    popupMenuSuggestions.pack();
                }
                txtSanPhamQuaTang.requestFocusInWindow();
            } else {
                popupMenuSuggestions.setVisible(false);
            }
        });
    }

    private void selectSuggestion() {
        String selected = listSuggestions.getSelectedValue();
        if (selected != null) {
            isAdjusting = true;
            txtSanPhamQuaTang.setText(selected);
            popupMenuSuggestions.setVisible(false);
            isAdjusting = false;
            updateDonViCombo(selected);
        }
    }

    private void updateDonViCombo(String tenSP) {
        cboDonViQuaTang.removeAllItems();
        SanPham sp = findSanPhamByName(tenSP);
        if (sp != null) {
            DonViQuyDoiDAO dvqdDAO = new DonViQuyDoiDAO();
            List<DonViQuyDoi> listDV = dvqdDAO.timTheoMaSanPham(sp.getMaSanPham());
            for (DonViQuyDoi dv : listDV) {
                if (dv.getTenDonVi() != null) {
                    cboDonViQuaTang.addItem(dv.getTenDonVi());
                }
            }
        }
        
        if (cboDonViQuaTang.getItemCount() == 0) {
            for (DonVi dv : DonVi.values()) {
                cboDonViQuaTang.addItem(dv);
            }
        }
    }

    // ===================== HELPER METHODS =====================
    private SanPham findSanPhamByName(String tenSanPham) {
        try {
            List<SanPham> danhSach = sanPhamDAO.layTatCa();
            for (SanPham sp : danhSach) {
                if (sp.getTenSanPham().equalsIgnoreCase(tenSanPham)) {
                    return sp;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    // ===================== CRUD OPERATIONS =====================
    private void themKhuyenMai() {
        if (!validateInput()) return;
        KhuyenMai km = buildKhuyenMaiFromForm();
        if (km == null) return;

        if (khuyenMaiDAO.them(km)) {
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!\nMã: " + km.getMaKhuyenMai(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataFromDatabase();
            refreshTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhuyenMai() {
        if (currentSelectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần sửa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;

        KhuyenMai km = buildKhuyenMaiFromForm();
        if (km == null) return;

        km.setMaKhuyenMai(txtMaKhuyenMai.getText().trim());

        if (khuyenMaiDAO.capNhat(km)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataFromDatabase();
            refreshTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhuyenMai() {
        if (currentSelectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ma = txtMaKhuyenMai.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this, "Xóa khuyến mãi " + ma + "?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (khuyenMaiDAO.xoa(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataFromDatabase();
                refreshTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // FIX 5: Thêm validation ngày kết thúc > ngày bắt đầu
    // FIX 6: Thêm validation cho giaTriDonHangToiThieu
    private boolean validateInput() {
        if (txtTenKhuyenMai.getText().trim().isEmpty()
                || datePickerBatDau.getDate() == null
                || datePickerKetThuc.getDate() == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin bắt buộc!",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!datePickerKetThuc.getDate().isAfter(datePickerBatDau.getDate())) {
            JOptionPane.showMessageDialog(this,
                    "Ngày kết thúc phải sau ngày bắt đầu!",
                    "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!datePickerKetThuc.getDate().isAfter(java.time.LocalDate.now().minusDays(1))) {
            JOptionPane.showMessageDialog(this,
                    "Ngày kết thúc không được ở trong quá khứ!",
                    "Ngày không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String giaTriStr = txtGiaTriDonHangToiThieu.getText().trim();
        if (!giaTriStr.isEmpty()) {
            try {
                double giaTri = Double.parseDouble(giaTriStr);
                if (giaTri < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Giá trị đơn hàng tối thiểu không được âm!",
                            "Giá trị không hợp lệ", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Giá trị đơn hàng tối thiểu phải là số!",
                        "Giá trị không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private KhuyenMai buildKhuyenMaiFromForm() {
        KhuyenMai km = new KhuyenMai();
        km.setTenKhuyenMai(txtTenKhuyenMai.getText().trim());
        km.setThoiGianBatDau(datePickerBatDau.getDate().atStartOfDay());
        km.setThoiGianKetThuc(datePickerKetThuc.getDate().atStartOfDay());
        km.setLoaiKhuyenMai((LoaiKhuyenMai) cboLoaiKhuyenMai.getSelectedItem());

        // An toàn vì đã validate trước
        String giaTriStr = txtGiaTriDonHangToiThieu.getText().trim();
        km.setGiaTriDonHangToiThieu(giaTriStr.isEmpty() ? 0 : Double.parseDouble(giaTriStr));

        if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            try {
                km.setKhuyenMaiPhanTram(Double.parseDouble(txtKhuyenMaiPhanTram.getText().trim()));
            } catch (Exception e) {
                km.setKhuyenMaiPhanTram(0);
            }
        }

        // ==================== XỬ LÝ QUÀ TẶNG ====================
        if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM) {
            String tenSP   = txtSanPhamQuaTang.getText().trim();
            String slStr   = txtSoLuongTang.getText().trim();
            DonVi donVi = (DonVi) cboDonViQuaTang.getSelectedItem();

            if (tenSP.isEmpty() || slStr.isEmpty() || donVi == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ: Sản phẩm tặng, Số lượng và Đơn vị!",
                        "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            try {
                int soLuong = Integer.parseInt(slStr);
                if (soLuong <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Số lượng tặng phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                SanPham sp = findSanPhamByName(tenSP);
                if (sp == null) {
                    JOptionPane.showMessageDialog(this,
                            "Sản phẩm '" + tenSP + "' không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                DonViQuyDoiDAO dvqdDAO = new DonViQuyDoiDAO();
                DonViQuyDoi dvqd = dvqdDAO.timTheoTenVaMaSP(donVi.name(), sp.getMaSanPham());

                if (dvqd == null) {
                    JOptionPane.showMessageDialog(this,
                            "Không tìm thấy đơn vị '" + donVi.getMoTa() + "' cho sản phẩm này!\n"
                            + "Vui lòng kiểm tra lại đơn vị quy đổi của sản phẩm.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return null;
                }

                QuaTang qt = new QuaTang();
                qt.setDonViQuyDoi(dvqd);
                qt.setSoLuongTang(soLuong);
                km.setQuaTangKem(qt);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng tặng phải là số nguyên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return km;
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        String keyword = txtSearch.getText().trim().toLowerCase();
        String filter  = (String) cboFilterLoai.getSelectedItem();

        int stt = 1;
        for (KhuyenMai km : danhSachKhuyenMai) {
            if (!keyword.isEmpty() && !keyword.startsWith("tìm kiếm")
                    && !km.getMaKhuyenMai().toLowerCase().contains(keyword)
                    && !km.getTenKhuyenMai().toLowerCase().contains(keyword)) {
                continue;
            }

            if (!"Tất cả".equals(filter)) {
                if ("Phần trăm".equals(filter) && km.getLoaiKhuyenMai() != LoaiKhuyenMai.PHAN_TRAM) continue;
                if ("Tặng kèm".equals(filter)  && km.getLoaiKhuyenMai() != LoaiKhuyenMai.TANG_KEM)  continue;
            }

            String quaTangText = "";
            if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                QuaTang qt = km.getQuaTangKem();
                if (qt.getDonViQuyDoi() != null && qt.getDonViQuyDoi().getSanPham() != null) {
                    quaTangText = qt.getDonViQuyDoi().getSanPham().getTenSanPham()
                            + " x" + qt.getSoLuongTang();
                }
            }

            tableModel.addRow(new Object[]{
                    stt++,
                    km.getMaKhuyenMai(),
                    km.getTenKhuyenMai(),
                    km.getLoaiKhuyenMai().getMoTa(),
                    km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM
                            ? String.format("%.0f%%", km.getKhuyenMaiPhanTram()) : "",
                    quaTangText,
                    km.getGiaTriDonHangToiThieu() > 0
                            ? String.format("%,.0f", km.getGiaTriDonHangToiThieu()) : "",
                    km.getThoiGianBatDau().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    km.getThoiGianKetThuc().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            });
        }
    }

    private void loadKhuyenMaiToForm(int tableRow) {
        if (tableRow < 0 || tableRow >= tableModel.getRowCount()) return;

        String maKM = (String) tableModel.getValueAt(tableRow, 1);
        for (int i = 0; i < danhSachKhuyenMai.size(); i++) {
            if (danhSachKhuyenMai.get(i).getMaKhuyenMai().equals(maKM)) {
                currentSelectedIndex = i;
                KhuyenMai km = danhSachKhuyenMai.get(i);

                txtMaKhuyenMai.setText(km.getMaKhuyenMai());
                txtTenKhuyenMai.setText(km.getTenKhuyenMai());
                datePickerBatDau.setDate(km.getThoiGianBatDau().toLocalDate());
                datePickerKetThuc.setDate(km.getThoiGianKetThuc().toLocalDate());
                cboLoaiKhuyenMai.setSelectedItem(km.getLoaiKhuyenMai());

                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                    txtKhuyenMaiPhanTram.setText(String.valueOf((int) km.getKhuyenMaiPhanTram()));
                }

                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                    QuaTang qt = km.getQuaTangKem();
                    if (qt.getDonViQuyDoi() != null && qt.getDonViQuyDoi().getSanPham() != null) {
                        DonViQuyDoi dvqd = qt.getDonViQuyDoi();
                        String tenSP = dvqd.getSanPham().getTenSanPham();
                        txtSanPhamQuaTang.setText(tenSP);
                        txtSoLuongTang.setText(String.valueOf(qt.getSoLuongTang()));
                        
                        updateDonViCombo(tenSP);
                        cboDonViQuaTang.setSelectedItem(dvqd.getTenDonVi());
                    }
                }

                txtGiaTriDonHangToiThieu.setText(
                        km.getGiaTriDonHangToiThieu() > 0
                                ? String.valueOf((int) km.getGiaTriDonHangToiThieu()) : "");

                updateFormFieldsByLoai();
                updateButtonStates();
                break;
            }
        }
    }

    private void clearForm() {
        txtMaKhuyenMai.setText(khuyenMaiDAO.generateNextMaKhuyenMai());
        txtTenKhuyenMai.setText("");
        datePickerBatDau.clear();
        datePickerKetThuc.clear();
        cboLoaiKhuyenMai.setSelectedIndex(0);
        txtKhuyenMaiPhanTram.setText("");
        txtSanPhamQuaTang.setText("");
        txtSoLuongTang.setText("");
        txtGiaTriDonHangToiThieu.setText("");
        
        cboDonViQuaTang.removeAllItems();
        for (DonVi dv : DonVi.values()) {
            cboDonViQuaTang.addItem(dv);
        }
        if (cboDonViQuaTang.getItemCount() > 0) {
            cboDonViQuaTang.setSelectedIndex(0);
        }
        currentSelectedIndex = -1;
        updateFormFieldsByLoai();
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean selected = currentSelectedIndex >= 0;
        btnSua.setEnabled(selected);
        btnXoa.setEnabled(selected);
    }
}