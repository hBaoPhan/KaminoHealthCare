package com.example.gui.screens;

import com.example.dao.KhuyenMaiDAO;
import com.example.entity.KhuyenMai;
import com.example.entity.enums.LoaiKhuyenMai;
import com.example.entity.QuaTang;
import com.example.entity.SanPham;
import com.example.entity.DonViQuyDoi;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KhuyenMaiPanel extends JPanel {

    private final KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();

    private DefaultTableModel tableModel;
    private JTable table;
    private JScrollPane scrollPane;

    private List<KhuyenMai> danhSachKhuyenMai;
    private int hoveredRow = -1;
    private int currentSelectedIndex = -1;

    private JButton btnThem;
    private JButton btnXoa;
    private JButton btnSua;
    private JButton btnLamMoi;
    private JTextField txtSearch;
    private JComboBox<String> cboFilterLoai;

    private JTextField txtMaKhuyenMai;
    private JTextField txtTenKhuyenMai;
    private DatePicker datePickerBatDau;
    private DatePicker datePickerKetThuc;
    private JComboBox<LoaiKhuyenMai> cboLoaiKhuyenMai;
    private JTextField txtKhuyenMaiPhanTram;
    private JTextField txtSanPhamQuaTang;
    private JTextField txtSoLuongTang;
    private JTextField txtGiaTriDonHangToiThieu;

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
    }

    private void loadDataFromDatabase() {
        danhSachKhuyenMai = khuyenMaiDAO.layTatCa();
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(241, 246, 255));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(new Color(241, 246, 255));

        JLabel lblDanhSach = new JLabel("Danh sách khuyến mãi");
        lblDanhSach.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblDanhSach.setForeground(new Color(35, 55, 75));
        lblDanhSach.setPreferredSize(new Dimension(220, 35));

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

        String[] columns = {"STT", "Mã KM", "Tên KM", "Loại KM", "Giảm (%)", "Quà tặng", "Giá trị đơn hàng tối thiểu", "Thời gian bắt đầu", "Thời gian kết thúc"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (row == hoveredRow) {
                        c.setBackground(new Color(226, 232, 240));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    }
                    c.setForeground(new Color(51, 51, 51));
                } else {
                    c.setBackground(new Color(203, 213, 225));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };

        table.setFocusable(false);
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
                if (row >= 0) {
                    loadKhuyenMaiToForm(row);
                }
            }
        });

        table.setRowHeight(34);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(238, 238, 238));
        table.getTableHeader().setPreferredSize(new Dimension(100, 40));
        table.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        for (int i = 0; i < 9; i++) {
            if (i == 6) {
                table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            } else {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);
        table.getColumnModel().getColumn(3).setPreferredWidth(110);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(160);
        table.getColumnModel().getColumn(7).setPreferredWidth(130);
        table.getColumnModel().getColumn(8).setPreferredWidth(130);

        scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

        return leftPanel;
    }

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
        styleDatePicker(datePickerBatDau);

        DatePickerSettings ketThucSettings = new DatePickerSettings();
        ketThucSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        datePickerKetThuc = new DatePicker(ketThucSettings);
        styleDatePicker(datePickerKetThuc);

        cboLoaiKhuyenMai = new JComboBox<>(LoaiKhuyenMai.values());
        cboLoaiKhuyenMai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof LoaiKhuyenMai) {
                    setText(((LoaiKhuyenMai) value).getMoTa());
                }
                return this;
            }
        });

        txtKhuyenMaiPhanTram = new JTextField();
        txtSanPhamQuaTang = new JTextField();
        txtSoLuongTang = new JTextField();
        txtGiaTriDonHangToiThieu = new JTextField();

        int row = 0;
        addResponsiveFormField(formPanel, gbc, row++, "Mã khuyến mãi:", txtMaKhuyenMai);
        addResponsiveFormField(formPanel, gbc, row++, "Tên khuyến mãi:", txtTenKhuyenMai);
        addResponsiveFormField(formPanel, gbc, row++, "Ngày bắt đầu:", datePickerBatDau);
        addResponsiveFormField(formPanel, gbc, row++, "Ngày kết thúc:", datePickerKetThuc);
        addResponsiveFormField(formPanel, gbc, row++, "Loại khuyến mãi:", cboLoaiKhuyenMai);
        addResponsiveFormField(formPanel, gbc, row++, "% giảm:", txtKhuyenMaiPhanTram);
        addResponsiveFormField(formPanel, gbc, row++, "Sản phẩm tặng:", txtSanPhamQuaTang);
        addResponsiveFormField(formPanel, gbc, row++, "Số lượng tặng:", txtSoLuongTang);
        addResponsiveFormField(formPanel, gbc, row++, "Giá trị đơn hàng tối thiểu:", txtGiaTriDonHangToiThieu);

        cboLoaiKhuyenMai.addActionListener(e -> updateFormFieldsByLoai());
        updateFormFieldsByLoai();

        formPanel.add(Box.createVerticalStrut(10), gbc);
        rightPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnThem = createStyledButton("Thêm", new Color(40, 167, 69), Color.WHITE);
        btnThem.setPreferredSize(new Dimension(120, 40));
        btnThem.addActionListener(e -> themKhuyenMai());

        btnXoa = createStyledButton("Xóa", new Color(220, 53, 69), Color.WHITE);
        btnXoa.setPreferredSize(new Dimension(120, 40));
        btnXoa.addActionListener(e -> xoaKhuyenMai());

        btnLamMoi = createStyledButton("Làm mới", new Color(108, 117, 125), Color.WHITE);
        btnLamMoi.setPreferredSize(new Dimension(120, 40));
        btnLamMoi.addActionListener(e -> clearForm());

        btnSua = createStyledButton("Sửa", new Color(255, 193, 7), Color.BLACK);
        btnSua.setPreferredSize(new Dimension(120, 40));
        btnSua.addActionListener(e -> suaKhuyenMai());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        buttonPanel.add(btnSua);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private void addResponsiveFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent inputComp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(inputComp, gbc);

        if (inputComp instanceof JTextField) {
            ((JTextField) inputComp).setPreferredSize(new Dimension(220, 32));
        } else if (inputComp instanceof JComboBox || inputComp instanceof DatePicker) {
            inputComp.setPreferredSize(new Dimension(220, 32));
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
        Color disabledBg = bgColor.brighter();
        Color disabledFg = new Color(150, 150, 150);

        btn.addChangeListener(e -> {
            if (!btn.isEnabled()) {
                btn.setBackground(disabledBg);
                btn.setForeground(disabledFg);
            } else {
                btn.setBackground(bgColor);
                btn.setForeground(fgColor);
            }
        });

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
        boolean isTangKem = loai == LoaiKhuyenMai.TANG_KEM;

        txtKhuyenMaiPhanTram.setEnabled(isPhanTram);
        txtSanPhamQuaTang.setEnabled(isTangKem);
        txtSoLuongTang.setEnabled(isTangKem);
        txtGiaTriDonHangToiThieu.setEnabled(true);

        if (!isPhanTram) txtKhuyenMaiPhanTram.setText("");
        if (!isTangKem) {
            txtSanPhamQuaTang.setText("");
            txtSoLuongTang.setText("");
        }
    }

    private void styleDatePicker(DatePicker datePicker) {
        datePicker.setBackground(Color.WHITE);
        datePicker.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        datePicker.getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
    }

    // ===================== CRUD =====================

    private void themKhuyenMai() {
        if (!validateInput()) return;

        KhuyenMai km = buildKhuyenMaiFromForm();
        km.setMaKhuyenMai(generateNextMaKhuyenMai());

        if (khuyenMaiDAO.them(km)) {
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataFromDatabase();
            refreshTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaKhuyenMai() {
        if (currentSelectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần sửa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateInput()) return;

        KhuyenMai km = buildKhuyenMaiFromForm();
        km.setMaKhuyenMai(txtMaKhuyenMai.getText().trim());

        if (khuyenMaiDAO.capNhat(km)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadDataFromDatabase();
            refreshTableData();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhuyenMai() {
        if (currentSelectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa từ bảng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ma = txtMaKhuyenMai.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khuyến mãi " + ma + "?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (khuyenMaiDAO.xoa(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDataFromDatabase();
                refreshTableData();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        String ten = txtTenKhuyenMai.getText().trim();
        if (ten.isEmpty() || datePickerBatDau.getDate() == null || datePickerKetThuc.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private KhuyenMai buildKhuyenMaiFromForm() {
        KhuyenMai km = new KhuyenMai();
        km.setTenKhuyenMai(txtTenKhuyenMai.getText().trim());
        km.setThoiGianBatDau(datePickerBatDau.getDate().atStartOfDay());
        km.setThoiGianKetThuc(datePickerKetThuc.getDate().atStartOfDay());
        km.setLoaiKhuyenMai((LoaiKhuyenMai) cboLoaiKhuyenMai.getSelectedItem());
        km.setGiaTriDonHangToiThieu(txtGiaTriDonHangToiThieu.getText().trim().isEmpty() ? 0 :
                Double.parseDouble(txtGiaTriDonHangToiThieu.getText().trim()));

        if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            try {
                km.setKhuyenMaiPhanTram(Double.parseDouble(txtKhuyenMaiPhanTram.getText().trim()));
            } catch (Exception e) {
                km.setKhuyenMaiPhanTram(0);
            }
        }

        if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM) {
            String sanPhamTen = txtSanPhamQuaTang.getText().trim();
            String soLuongStr = txtSoLuongTang.getText().trim();
            if (!sanPhamTen.isEmpty() && !soLuongStr.isEmpty()) {
                try {
                    int soLuong = Integer.parseInt(soLuongStr);
                    QuaTang qt = new QuaTang();
                    SanPham sp = new SanPham();
                    sp.setTenSanPham(sanPhamTen);
                    DonViQuyDoi dv = new DonViQuyDoi();
                    dv.setSanPham(sp);
                    qt.setDonViQuyDoi(dv);
                    qt.setSoLuongTang(soLuong);
                    km.setQuaTangKem(qt);
                } catch (Exception ignored) {}
            }
        }
        return km;
    }

    private String generateNextMaKhuyenMai() {
        int maxNumber = 0;
        for (KhuyenMai km : danhSachKhuyenMai) {
            String ma = km.getMaKhuyenMai();
            if (ma != null && ma.startsWith("KM")) {
                try {
                    int num = Integer.parseInt(ma.substring(2));
                    if (num > maxNumber) maxNumber = num;
                } catch (Exception ignored) {}
            }
        }
        return String.format("KM%03d", maxNumber + 1);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedFilter = (String) cboFilterLoai.getSelectedItem();

        int index = 1;
        for (KhuyenMai km : danhSachKhuyenMai) {
            if (!keyword.isEmpty() && !keyword.startsWith("tìm kiếm") &&
                !km.getMaKhuyenMai().toLowerCase().contains(keyword) &&
                !km.getTenKhuyenMai().toLowerCase().contains(keyword)) {
                continue;
            }

            if (selectedFilter != null && !selectedFilter.equals("Tất cả")) {
                if (selectedFilter.equals("Phần trăm") && km.getLoaiKhuyenMai() != LoaiKhuyenMai.PHAN_TRAM) continue;
                if (selectedFilter.equals("Tặng kèm") && km.getLoaiKhuyenMai() != LoaiKhuyenMai.TANG_KEM) continue;
            }

            String loaiText = km.getLoaiKhuyenMai() != null ? km.getLoaiKhuyenMai().getMoTa() : "";
            String phanTram = km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM ? String.format("%.0f%%", km.getKhuyenMaiPhanTram()) : "";
            String quaTangText = "";
            if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                quaTangText = km.getQuaTangKem().getDonViQuyDoi().getSanPham().getTenSanPham() +
                        " x" + km.getQuaTangKem().getSoLuongTang();
            }
            String giaTriToiThieu = km.getGiaTriDonHangToiThieu() > 0 ? String.format("%,.0f", km.getGiaTriDonHangToiThieu()) : "";

            String batDau = km.getThoiGianBatDau() != null ?
                    km.getThoiGianBatDau().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            String ketThuc = km.getThoiGianKetThuc() != null ?
                    km.getThoiGianKetThuc().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";

            tableModel.addRow(new Object[]{
                    index++,
                    km.getMaKhuyenMai(),
                    km.getTenKhuyenMai(),
                    loaiText,
                    phanTram,
                    quaTangText,
                    giaTriToiThieu,
                    batDau,
                    ketThuc
            });
        }
        table.repaint();
    }

    private void loadKhuyenMaiToForm(int tableRow) {
        if (tableRow < 0 || tableRow >= tableModel.getRowCount()) return;

        String maKhuyenMai = (String) tableModel.getValueAt(tableRow, 1);

        for (int i = 0; i < danhSachKhuyenMai.size(); i++) {
            KhuyenMai km = danhSachKhuyenMai.get(i);
            if (km.getMaKhuyenMai().equals(maKhuyenMai)) {
                currentSelectedIndex = i;

                txtMaKhuyenMai.setText(km.getMaKhuyenMai());
                txtTenKhuyenMai.setText(km.getTenKhuyenMai());
                if (km.getThoiGianBatDau() != null) datePickerBatDau.setDate(km.getThoiGianBatDau().toLocalDate());
                if (km.getThoiGianKetThuc() != null) datePickerKetThuc.setDate(km.getThoiGianKetThuc().toLocalDate());
                cboLoaiKhuyenMai.setSelectedItem(km.getLoaiKhuyenMai());

                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                    txtKhuyenMaiPhanTram.setText(String.valueOf((int) km.getKhuyenMaiPhanTram()));
                }
                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                    txtSanPhamQuaTang.setText(km.getQuaTangKem().getDonViQuyDoi().getSanPham().getTenSanPham());
                    txtSoLuongTang.setText(String.valueOf(km.getQuaTangKem().getSoLuongTang()));
                }
                if (km.getGiaTriDonHangToiThieu() > 0) {
                    txtGiaTriDonHangToiThieu.setText(String.valueOf((int) km.getGiaTriDonHangToiThieu()));
                }
                updateFormFieldsByLoai();
                break;
            }
        }
        updateButtonStates();
    }

    private void clearForm() {
        txtMaKhuyenMai.setText(generateNextMaKhuyenMai());
        txtTenKhuyenMai.setText("");
        datePickerBatDau.clear();
        datePickerKetThuc.clear();
        cboLoaiKhuyenMai.setSelectedIndex(0);
        txtKhuyenMaiPhanTram.setText("");
        txtSanPhamQuaTang.setText("");
        txtSoLuongTang.setText("");
        txtGiaTriDonHangToiThieu.setText("");
        updateFormFieldsByLoai();
        currentSelectedIndex = -1;
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean hasSelection = currentSelectedIndex >= 0;
        btnSua.setEnabled(hasSelection);
        btnXoa.setEnabled(hasSelection);
    }
}