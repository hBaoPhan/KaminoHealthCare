package com.example.gui.screens;

import com.example.entity.KhuyenMai;
import com.example.entity.LoaiKhuyenMai;
import com.example.entity.QuaTang;
import com.example.entity.SanPham;
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
import java.util.ArrayList;
import java.util.List;

public class KhuyenMaiPanel extends JPanel {
    private DefaultTableModel tableModel;
    private final List<KhuyenMai> danhSachKhuyenMai;
    private int hoveredRow = -1;
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

        danhSachKhuyenMai = new ArrayList<>();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        splitPane.setDividerSize(8);

        add(splitPane, BorderLayout.CENTER);

        createSampleData();
        refreshTableData();
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
                if ("Tìm kiếm mã/tên...".equals(txtSearch.getText().trim())) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Tìm kiếm mã/tên...");
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

        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (row == hoveredRow) {
                        c.setBackground(new Color(230, 230, 230));
                    } else {
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(247, 249, 252));
                    }
                } else {
                    c.setBackground(new Color(209, 232, 255));
                }
                return c;
            }
        };

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

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(140);
        table.getColumnModel().getColumn(7).setPreferredWidth(110);
        table.getColumnModel().getColumn(8).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(table);
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

        JLabel lblTitle = new JLabel("THÔNG TIN KHỦYẾN MÃI", SwingConstants.CENTER);
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
        addResponsiveFormField(formPanel, gbc, row++, "Thời gian bắt đầu:", datePickerBatDau);
        addResponsiveFormField(formPanel, gbc, row++, "Thời gian kết thúc:", datePickerKetThuc);
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

        JButton btnThem = createStyledButton("Thêm khuyến mãi", new Color(0, 153, 102), Color.WHITE);
        btnThem.setPreferredSize(new Dimension(160, 40));
        btnThem.addActionListener(e -> themKhuyenMai());

        JButton btnLamMoi = createStyledButton("Làm mới", new Color(240, 240, 240), Color.BLACK);
        btnLamMoi.setPreferredSize(new Dimension(120, 40));
        btnLamMoi.addActionListener(e -> clearForm());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnLamMoi);

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
            ((JTextField) inputComp).setPreferredSize(new Dimension(150, 32));
        } else if (inputComp instanceof JComboBox) {
            inputComp.setPreferredSize(new Dimension(150, 32));
        } else if (inputComp instanceof DatePicker) {
            ((DatePicker) inputComp).setPreferredSize(new Dimension(150, 32));
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
        Color hoverColor = bgColor.equals(new Color(240, 240, 240)) ? new Color(220, 220, 220) : bgColor.darker();
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private void updateFormFieldsByLoai() {
        LoaiKhuyenMai loai = (LoaiKhuyenMai) cboLoaiKhuyenMai.getSelectedItem();
        boolean isPhanTram = loai == LoaiKhuyenMai.PHANTRAM;
        boolean isTangKem = loai == LoaiKhuyenMai.TANGKEM;

        txtKhuyenMaiPhanTram.setEnabled(isPhanTram);
        txtSanPhamQuaTang.setEnabled(isTangKem);
        txtSoLuongTang.setEnabled(isTangKem);
        txtGiaTriDonHangToiThieu.setEnabled(true);

        if (!isPhanTram) {
            txtKhuyenMaiPhanTram.setText("");
        }
        if (!isTangKem) {
            txtSanPhamQuaTang.setText("");
            txtSoLuongTang.setText("");
        }
    }

    private void themKhuyenMai() {
        String ma = txtMaKhuyenMai.getText().trim();
        String ten = txtTenKhuyenMai.getText().trim();
        LocalDateTime batDau = datePickerBatDau.getDate() == null ? null : datePickerBatDau.getDate().atStartOfDay();
        LocalDateTime ketThuc = datePickerKetThuc.getDate() == null ? null : datePickerKetThuc.getDate().atStartOfDay();
        LoaiKhuyenMai loai = (LoaiKhuyenMai) cboLoaiKhuyenMai.getSelectedItem();

        if (ma.isEmpty() || ten.isEmpty() || batDau == null || ketThuc == null || loai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double phanTram = 0;
        if (loai == LoaiKhuyenMai.PHANTRAM) {
            try {
                phanTram = Double.parseDouble(txtKhuyenMaiPhanTram.getText().trim());
                if (phanTram < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nhập giá trị phần trăm hợp lệ.", "Lỗi số", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        double giaTriToiThieu = 0;
        if (!txtGiaTriDonHangToiThieu.getText().trim().isEmpty()) {
            try {
                giaTriToiThieu = Double.parseDouble(txtGiaTriDonHangToiThieu.getText().trim());
                if (giaTriToiThieu < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Nhập giá trị đơn hàng tối thiểu hợp lệ.", "Lỗi số", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        QuaTang quaTang = null;
        if (loai == LoaiKhuyenMai.TANGKEM) {
            String sanPham = txtSanPhamQuaTang.getText().trim();
            String soLuong = txtSoLuongTang.getText().trim();
            if (sanPham.isEmpty() || soLuong.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập tên sản phẩm tặng và số lượng tặng.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int soLuongTang;
            try {
                soLuongTang = Integer.parseInt(soLuong);
                if (soLuongTang <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Số lượng tặng phải là số nguyên dương.", "Lỗi số", JOptionPane.ERROR_MESSAGE);
                return;
            }
            SanPham sp = new SanPham();
            sp.setTenSanPham(sanPham);
            quaTang = new QuaTang();
            quaTang.setSanPham(sp);
            quaTang.setSoLuongTang(soLuongTang);
        }

        KhuyenMai km = new KhuyenMai();
        km.setMaKhuyenMai(ma);
        km.setTenKhuyenMai(ten);
        km.setThoiGianBatDau(batDau);
        km.setThoiGianKetThuc(ketThuc);
        km.setLoaiKhuyenMai(loai);
        km.setKhuyenMaiPhanTram(phanTram);
        km.setGiaTriDonHangToiThieu(giaTriToiThieu);
        km.setQuaTangKem(quaTang);

        danhSachKhuyenMai.add(km);
        refreshTableData();
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearForm() {
        txtMaKhuyenMai.setText("");
        txtTenKhuyenMai.setText("");
        datePickerBatDau.clear();
        datePickerKetThuc.clear();
        cboLoaiKhuyenMai.setSelectedIndex(0);
        txtKhuyenMaiPhanTram.setText("");
        txtSanPhamQuaTang.setText("");
        txtSoLuongTang.setText("");
        txtGiaTriDonHangToiThieu.setText("");
        updateFormFieldsByLoai();
    }

    private void styleDatePicker(DatePicker datePicker) {
        datePicker.setBackground(Color.WHITE);
        datePicker.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        datePicker.getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
    }

    private void createSampleData() {
        KhuyenMai sample1 = new KhuyenMai();
        sample1.setMaKhuyenMai("KM001");
        sample1.setTenKhuyenMai("Giảm mùa hè");
        sample1.setThoiGianBatDau(LocalDateTime.now().minusDays(1));
        sample1.setThoiGianKetThuc(LocalDateTime.now().plusDays(10));
        sample1.setLoaiKhuyenMai(LoaiKhuyenMai.PHANTRAM);
        sample1.setKhuyenMaiPhanTram(10);
        sample1.setGiaTriDonHangToiThieu(150000);

        KhuyenMai sample2 = new KhuyenMai();
        sample2.setMaKhuyenMai("KM002");
        sample2.setTenKhuyenMai("Tặng ly sứ");
        sample2.setThoiGianBatDau(LocalDateTime.now());
        sample2.setThoiGianKetThuc(LocalDateTime.now().plusDays(15));
        sample2.setLoaiKhuyenMai(LoaiKhuyenMai.TANGKEM);
        sample2.setGiaTriDonHangToiThieu(200000);
        QuaTang qt = new QuaTang();
        SanPham sp = new SanPham();
        sp.setTenSanPham("Ly sứ");
        qt.setSanPham(sp);
        qt.setSoLuongTang(1);
        sample2.setQuaTangKem(qt);

        KhuyenMai sample3 = new KhuyenMai();
        sample3.setMaKhuyenMai("KM003");
        sample3.setTenKhuyenMai("Giảm 15% đơn cao");
        sample3.setThoiGianBatDau(LocalDateTime.now().minusDays(3));
        sample3.setThoiGianKetThuc(LocalDateTime.now().plusDays(20));
        sample3.setLoaiKhuyenMai(LoaiKhuyenMai.PHANTRAM);
        sample3.setKhuyenMaiPhanTram(15);
        sample3.setGiaTriDonHangToiThieu(300000);

        danhSachKhuyenMai.add(sample1);
        danhSachKhuyenMai.add(sample2);
        danhSachKhuyenMai.add(sample3);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedFilter = (String) cboFilterLoai.getSelectedItem();

        int index = 1;
        for (KhuyenMai km : danhSachKhuyenMai) {
            if (!keyword.isEmpty() && !keyword.equals("tìm kiếm mã/tên...")) {
                boolean matchMa = km.getMaKhuyenMai() != null && km.getMaKhuyenMai().toLowerCase().contains(keyword);
                boolean matchTen = km.getTenKhuyenMai() != null && km.getTenKhuyenMai().toLowerCase().contains(keyword);
                if (!matchMa && !matchTen) {
                    continue;
                }
            }

            if (selectedFilter != null && !selectedFilter.equals("Tất cả")) {
                if (selectedFilter.equals("Phần trăm") && km.getLoaiKhuyenMai() != LoaiKhuyenMai.PHANTRAM) {
                    continue;
                }
                if (selectedFilter.equals("Tặng kèm") && km.getLoaiKhuyenMai() != LoaiKhuyenMai.TANGKEM) {
                    continue;
                }
            }

            String loaiText = km.getLoaiKhuyenMai() != null ? km.getLoaiKhuyenMai().getMoTa() : "";
            String phanTram = km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHANTRAM ? String.format("%.0f%%", km.getKhuyenMaiPhanTram()) : "";
            String quaTangText = km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANGKEM && km.getQuaTangKem() != null ?
                    km.getQuaTangKem().getSanPham().getTenSanPham() + " x" + km.getQuaTangKem().getSoLuongTang() : "";
            String giaTriToiThieu = km.getGiaTriDonHangToiThieu() > 0 ? String.format("%.0f", km.getGiaTriDonHangToiThieu()) : "";
            String batDau = km.getThoiGianBatDau() != null ? km.getThoiGianBatDau().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
            String ketThuc = km.getThoiGianKetThuc() != null ? km.getThoiGianKetThuc().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";

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
    }
}
