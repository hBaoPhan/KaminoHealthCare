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
    private JTable table;
    private JScrollPane scrollPane;
    private final List<KhuyenMai> danhSachKhuyenMai;
    private int hoveredRow = -1;
    private int currentSelectedIndex = -1;
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
                if ("Tìm kiếm mã/tên...".equals(txtSearch.getText().trim()) || "Tìm kiếm mã/tên khuyến mãi...".equals(txtSearch.getText().trim())) {
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
                        // Màu xám nhạt khi hover chuột
                        c.setBackground(new Color(226, 232, 240)); 
                    } else {
                        // Hiệu ứng sọc ngựa vằn
                        c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                    }
                    c.setForeground(new Color(51, 51, 51)); // Màu chữ mặc định
                } else {
                    // Màu xám đậm hơn khi click chọn dòng
                    c.setBackground(new Color(203, 213, 225)); 
                    c.setForeground(Color.BLACK); // Đảm bảo chữ đen rõ nét
                }
                return c;
            }
        };

        // Bỏ viền nét đứt khi click vào ô của Swing mặc định
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

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);

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
        
        txtMaKhuyenMai.setText(generateNextMaKhuyenMai());

        formPanel.add(Box.createVerticalStrut(10), gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnThem = createStyledButton("Thêm", new Color(0, 153, 102), Color.WHITE);
        btnThem.setPreferredSize(new Dimension(120, 40));
        btnThem.addActionListener(e -> themKhuyenMai());

        JButton btnXoa = createStyledButton("Xóa", new Color(220, 53, 69), Color.WHITE);
        btnXoa.setPreferredSize(new Dimension(120, 40));
        btnXoa.addActionListener(e -> xoaKhuyenMai());

        JButton btnSua = createStyledButton("Sửa", new Color(255, 193, 7), Color.BLACK);
        btnSua.setPreferredSize(new Dimension(120, 40));
        btnSua.addActionListener(e -> suaKhuyenMai());

        JButton btnLamMoi = createStyledButton("Làm mới", new Color(108, 117, 125), Color.WHITE);
        btnLamMoi.setPreferredSize(new Dimension(120, 40));
        btnLamMoi.addActionListener(e -> clearForm());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnSua);
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
        boolean isPhanTram = loai == LoaiKhuyenMai.PHAN_TRAM;
        boolean isTangKem = loai == LoaiKhuyenMai.TANG_KEM;

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
        String ten = txtTenKhuyenMai.getText().trim();
        LocalDateTime batDau = datePickerBatDau.getDate() == null ? null : datePickerBatDau.getDate().atStartOfDay();
        LocalDateTime ketThuc = datePickerKetThuc.getDate() == null ? null : datePickerKetThuc.getDate().atStartOfDay();
        LoaiKhuyenMai loai = (LoaiKhuyenMai) cboLoaiKhuyenMai.getSelectedItem();

        if (ten.isEmpty() || batDau == null || ketThuc == null || loai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ma = generateNextMaKhuyenMai();

        double phanTram = 0;
        if (loai == LoaiKhuyenMai.PHAN_TRAM) {
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
        if (loai == LoaiKhuyenMai.TANG_KEM) {
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
    }

    private void styleDatePicker(DatePicker datePicker) {
        datePicker.setBackground(Color.WHITE);
        datePicker.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));
        datePicker.getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
    }

    private String generateNextMaKhuyenMai() {
        int maxNumber = 0;
        for (KhuyenMai km : danhSachKhuyenMai) {
            String ma = km.getMaKhuyenMai();
            if (ma != null && ma.startsWith("KM")) {
                try {
                    int num = Integer.parseInt(ma.substring(2));
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid formats
                }
            }
        }
        return String.format("KM%03d", maxNumber + 1);
    }

    private void createSampleData() {
        // Khuyến mãi 1
        KhuyenMai km1 = new KhuyenMai();
        km1.setMaKhuyenMai("KM001");
        km1.setTenKhuyenMai("Giảm mùa hè");
        km1.setThoiGianBatDau(LocalDateTime.now().minusDays(1));
        km1.setThoiGianKetThuc(LocalDateTime.now().plusDays(10));
        km1.setLoaiKhuyenMai(LoaiKhuyenMai.PHAN_TRAM);
        km1.setKhuyenMaiPhanTram(10);
        km1.setGiaTriDonHangToiThieu(150000);
        danhSachKhuyenMai.add(km1);

        // Khuyến mãi 2
        KhuyenMai km2 = new KhuyenMai();
        km2.setMaKhuyenMai("KM002");
        km2.setTenKhuyenMai("Tặng ly sứ cao cấp");
        km2.setThoiGianBatDau(LocalDateTime.now());
        km2.setThoiGianKetThuc(LocalDateTime.now().plusDays(15));
        km2.setLoaiKhuyenMai(LoaiKhuyenMai.TANG_KEM);
        km2.setGiaTriDonHangToiThieu(200000);
        QuaTang qt2 = new QuaTang();
        SanPham sp2 = new SanPham();
        sp2.setTenSanPham("Ly sứ cao cấp");
        qt2.setSanPham(sp2);
        qt2.setSoLuongTang(1);
        km2.setQuaTangKem(qt2);
        danhSachKhuyenMai.add(km2);

        // Khuyến mãi 3
        KhuyenMai km3 = new KhuyenMai();
        km3.setMaKhuyenMai("KM003");
        km3.setTenKhuyenMai("Giảm 15% đơn cao");
        km3.setThoiGianBatDau(LocalDateTime.now().minusDays(3));
        km3.setThoiGianKetThuc(LocalDateTime.now().plusDays(20));
        km3.setLoaiKhuyenMai(LoaiKhuyenMai.PHAN_TRAM);
        km3.setKhuyenMaiPhanTram(15);
        km3.setGiaTriDonHangToiThieu(300000);
        danhSachKhuyenMai.add(km3);

        // Khuyến mãi 4
        KhuyenMai km4 = new KhuyenMai();
        km4.setMaKhuyenMai("KM004");
        km4.setTenKhuyenMai("Giảm 20% viên uống");
        km4.setThoiGianBatDau(LocalDateTime.now().minusDays(5));
        km4.setThoiGianKetThuc(LocalDateTime.now().plusDays(25));
        km4.setLoaiKhuyenMai(LoaiKhuyenMai.PHAN_TRAM);
        km4.setKhuyenMaiPhanTram(20);
        km4.setGiaTriDonHangToiThieu(250000);
        danhSachKhuyenMai.add(km4);

        // Khuyến mãi 5
        KhuyenMai km5 = new KhuyenMai();
        km5.setMaKhuyenMai("KM005");
        km5.setTenKhuyenMai("Tặng khăn microfiber");
        km5.setThoiGianBatDau(LocalDateTime.now().minusDays(2));
        km5.setThoiGianKetThuc(LocalDateTime.now().plusDays(30));
        km5.setLoaiKhuyenMai(LoaiKhuyenMai.TANG_KEM);
        km5.setGiaTriDonHangToiThieu(180000);
        QuaTang qt5 = new QuaTang();
        SanPham sp5 = new SanPham();
        sp5.setTenSanPham("Khăn microfiber");
        qt5.setSanPham(sp5);
        qt5.setSoLuongTang(2);
        km5.setQuaTangKem(qt5);
        danhSachKhuyenMai.add(km5);

        // Khuyến mãi 6
        KhuyenMai km6 = new KhuyenMai();
        km6.setMaKhuyenMai("KM006");
        km6.setTenKhuyenMai("Giảm 25% sản phẩm chọn lọc");
        km6.setThoiGianBatDau(LocalDateTime.now().plusDays(1));
        km6.setThoiGianKetThuc(LocalDateTime.now().plusDays(35));
        km6.setLoaiKhuyenMai(LoaiKhuyenMai.PHAN_TRAM);
        km6.setKhuyenMaiPhanTram(25);
        km6.setGiaTriDonHangToiThieu(400000);
        danhSachKhuyenMai.add(km6);

        // Khuyến mãi 7
        KhuyenMai km7 = new KhuyenMai();
        km7.setMaKhuyenMai("KM007");
        km7.setTenKhuyenMai("Tặng mặt nạ dưỡng da");
        km7.setThoiGianBatDau(LocalDateTime.now());
        km7.setThoiGianKetThuc(LocalDateTime.now().plusDays(12));
        km7.setLoaiKhuyenMai(LoaiKhuyenMai.TANG_KEM);
        km7.setGiaTriDonHangToiThieu(220000);
        QuaTang qt7 = new QuaTang();
        SanPham sp7 = new SanPham();
        sp7.setTenSanPham("Mặt nạ dưỡng da");
        qt7.setSanPham(sp7);
        qt7.setSoLuongTang(3);
        km7.setQuaTangKem(qt7);
        danhSachKhuyenMai.add(km7);

        // Khuyến mãi 8
        KhuyenMai km8 = new KhuyenMai();
        km8.setMaKhuyenMai("KM008");
        km8.setTenKhuyenMai("Giảm 30% thuốc đặc biệt");
        km8.setThoiGianBatDau(LocalDateTime.now().minusDays(7));
        km8.setThoiGianKetThuc(LocalDateTime.now().plusDays(40));
        km8.setLoaiKhuyenMai(LoaiKhuyenMai.PHAN_TRAM);
        km8.setKhuyenMaiPhanTram(30);
        km8.setGiaTriDonHangToiThieu(500000);
        danhSachKhuyenMai.add(km8);

        // Khuyến mãi 9
        KhuyenMai km9 = new KhuyenMai();
        km9.setMaKhuyenMai("KM009");
        km9.setTenKhuyenMai("Tặng hộp vitamin bổ sung");
        km9.setThoiGianBatDau(LocalDateTime.now().minusDays(4));
        km9.setThoiGianKetThuc(LocalDateTime.now().plusDays(45));
        km9.setLoaiKhuyenMai(LoaiKhuyenMai.TANG_KEM);
        km9.setGiaTriDonHangToiThieu(350000);
        QuaTang qt9 = new QuaTang();
        SanPham sp9 = new SanPham();
        sp9.setTenSanPham("Hộp vitamin bổ sung");
        qt9.setSanPham(sp9);
        qt9.setSoLuongTang(1);
        km9.setQuaTangKem(qt9);
        danhSachKhuyenMai.add(km9);

        // Khuyến mãi 10
        KhuyenMai km10 = new KhuyenMai();
        km10.setMaKhuyenMai("KM010");
        km10.setTenKhuyenMai("Giảm 12% tất cả sản phẩm");
        km10.setThoiGianBatDau(LocalDateTime.now().minusDays(6));
        km10.setThoiGianKetThuc(LocalDateTime.now().plusDays(50));
        km10.setLoaiKhuyenMai(LoaiKhuyenMai.PHAN_TRAM);
        km10.setKhuyenMaiPhanTram(12);
        km10.setGiaTriDonHangToiThieu(100000);
        danhSachKhuyenMai.add(km10);
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        String keyword = txtSearch.getText().trim().toLowerCase();
        String selectedFilter = (String) cboFilterLoai.getSelectedItem();

        int index = 1;
        for (KhuyenMai km : danhSachKhuyenMai) {
            if (!keyword.isEmpty() && !keyword.equals("tìm kiếm mã/tên khuyến mãi...")) {
                boolean matchMa = km.getMaKhuyenMai() != null && km.getMaKhuyenMai().toLowerCase().contains(keyword);
                boolean matchTen = km.getTenKhuyenMai() != null && km.getTenKhuyenMai().toLowerCase().contains(keyword);
                if (!matchMa && !matchTen) {
                    continue;
                }
            }

            if (selectedFilter != null && !selectedFilter.equals("Tất cả")) {
                if (selectedFilter.equals("Phần trăm") && km.getLoaiKhuyenMai() != LoaiKhuyenMai.PHAN_TRAM) {
                    continue;
                }
                if (selectedFilter.equals("Tặng kèm") && km.getLoaiKhuyenMai() != LoaiKhuyenMai.TANG_KEM) {
                    continue;
                }
            }

            String loaiText = km.getLoaiKhuyenMai() != null ? km.getLoaiKhuyenMai().getMoTa() : "";
            String phanTram = km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM ? String.format("%.0f%%", km.getKhuyenMaiPhanTram()) : "";
            String quaTangText = km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null ?
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
        
        // Refresh table display
        if (table != null) {
            table.repaint();
            table.revalidate();
        }
        if (scrollPane != null) {
            scrollPane.repaint();
            scrollPane.revalidate();
        }
    }

    private void loadKhuyenMaiToForm(int tableRow) {
        if (tableRow < 0 || tableRow >= tableModel.getRowCount()) {
            return;
        }

        String maKhuyenMai = (String) tableModel.getValueAt(tableRow, 1);

        for (int i = 0; i < danhSachKhuyenMai.size(); i++) {
            KhuyenMai km = danhSachKhuyenMai.get(i);
            if (km.getMaKhuyenMai().equals(maKhuyenMai)) {
                currentSelectedIndex = i;
                txtMaKhuyenMai.setText(km.getMaKhuyenMai());
                txtTenKhuyenMai.setText(km.getTenKhuyenMai());
                if (km.getThoiGianBatDau() != null) {
                    datePickerBatDau.setDate(km.getThoiGianBatDau().toLocalDate());
                }
                if (km.getThoiGianKetThuc() != null) {
                    datePickerKetThuc.setDate(km.getThoiGianKetThuc().toLocalDate());
                }
                if (km.getLoaiKhuyenMai() != null) {
                    cboLoaiKhuyenMai.setSelectedItem(km.getLoaiKhuyenMai());
                }
                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                    txtKhuyenMaiPhanTram.setText(String.valueOf((int) km.getKhuyenMaiPhanTram()));
                }
                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                    txtSanPhamQuaTang.setText(km.getQuaTangKem().getSanPham().getTenSanPham());
                    txtSoLuongTang.setText(String.valueOf(km.getQuaTangKem().getSoLuongTang()));
                }
                if (km.getGiaTriDonHangToiThieu() > 0) {
                    txtGiaTriDonHangToiThieu.setText(String.valueOf((int) km.getGiaTriDonHangToiThieu()));
                }
                break;
            }
        }
    }

    private void xoaKhuyenMai() {
        if (currentSelectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần xóa từ bảng.", "Chọn khuyến mãi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khuyến mãi này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            danhSachKhuyenMai.remove(currentSelectedIndex);
            currentSelectedIndex = -1;
            clearForm();
            refreshTableData();
            JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void suaKhuyenMai() {
        if (currentSelectedIndex < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần sửa từ bảng.", "Chọn khuyến mãi", JOptionPane.WARNING_MESSAGE);
            return;
        }

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
        if (loai == LoaiKhuyenMai.PHAN_TRAM) {
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
        if (loai == LoaiKhuyenMai.TANG_KEM) {
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

        KhuyenMai kmCapNhat = danhSachKhuyenMai.get(currentSelectedIndex);
        kmCapNhat.setMaKhuyenMai(ma);
        kmCapNhat.setTenKhuyenMai(ten);
        kmCapNhat.setThoiGianBatDau(batDau);
        kmCapNhat.setThoiGianKetThuc(ketThuc);
        kmCapNhat.setLoaiKhuyenMai(loai);
        kmCapNhat.setKhuyenMaiPhanTram(phanTram);
        kmCapNhat.setGiaTriDonHangToiThieu(giaTriToiThieu);
        kmCapNhat.setQuaTangKem(quaTang);

        refreshTableData();
        clearForm();
        currentSelectedIndex = -1;
        JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }
}