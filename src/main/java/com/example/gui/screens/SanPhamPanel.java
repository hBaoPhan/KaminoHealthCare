package com.example.gui.screens;

import com.example.dao.DonViQuyDoiDAO;
import com.example.dao.SanPhamDAO;
import com.example.entity.DonViQuyDoi;
import com.example.entity.SanPham;
import com.example.entity.enums.DonVi;
import com.example.entity.enums.LoaiSanPham;
import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class SanPhamPanel extends JPanel {

    private SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private DonViQuyDoiDAO donViQuyDoiDAO = new DonViQuyDoiDAO();
    private List<SanPham> danhSachSanPham = new ArrayList<>();

    // Components
    private JPanel gridPanel;
    private GridLayout gridLayout;
    private JTextField txtSearch;
    private JComboBox<String> cbDanhMuc;
    private JPanel rightPanel;
    private JLabel lblImageRight;
    private JTextField txtMaSP, txtTenSP, txtHoatChat, txtSoLuong, txtDonGia;
    private JComboBox<String> cbLoaiSP;
    private JTextArea txtMoTa;

    // Đơn vị quy đổi UI
    private JTextField txtHeSoQuyDoi;
    private JComboBox<DonVi> cbDonViQuyDoi;
    private JButton btnThemDonVi;
    private JButton btnXoaDonVi;

    private JTable tblDonViQuyDoi;
    private DefaultTableModel donViQuyDoiModel;
    private SanPham sanPhamDangChon = null;
    private File selectedImageFile = null;
    private JPopupMenu searchPopup;
    private boolean isUpdatingSearch = false;

    public SanPhamPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);

        // Load dữ liệu ban đầu
        loadDanhSachSanPham();
    }

    // ====================== LEFT PANEL ======================
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(241, 246, 255));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(new Color(245, 245, 245));

        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBar.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("Danh sách sản phẩm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(50, 50, 50));
        leftBar.add(lblTitle);

        cbDanhMuc = new JComboBox<>(new String[] { "Tất cả", "Thuốc ETC", "Thuốc OTC", "TPCN" });
        cbDanhMuc.setPreferredSize(new Dimension(150, 35));
        cbDanhMuc.addActionListener(e -> locVaHienThiSanPham());
        leftBar.add(cbDanhMuc);

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightBar.setBackground(new Color(245, 245, 245));

        txtSearch = new JTextField("Tìm kiếm theo mã hoặc tên...");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(220, 35));
        
        searchPopup = new JPopupMenu();
        searchPopup.setFocusable(false);
        
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Tìm kiếm theo mã hoặc tên...".equals(txtSearch.getText().trim())) {
                    isUpdatingSearch = true;
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                    isUpdatingSearch = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    isUpdatingSearch = true;
                    txtSearch.setText("Tìm kiếm theo mã hoặc tên...");
                    txtSearch.setForeground(Color.GRAY);
                    isUpdatingSearch = false;
                }
            }
        });
        txtSearch.addActionListener(e -> {
            searchPopup.setVisible(false);
            locVaHienThiSanPham();
        });
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateSearch(); }
        });

        JButton btnSearch = new RoundedButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(80, 35));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> locVaHienThiSanPham());

        rightBar.add(txtSearch);
        rightBar.add(btnSearch);

        topBar.add(leftBar, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // Grid Panel
        gridLayout = new GridLayout(0, 5, 15, 15);
        gridPanel = new JPanel(gridLayout);
        gridPanel.setBackground(new Color(245, 245, 245));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(245, 245, 245));
        wrapperPanel.add(gridPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(wrapperPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Responsive grid
        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int viewportWidth = scrollPane.getViewport().getWidth();
                if (viewportWidth > 0) {
                    int cols = Math.max(1, Math.min(5, viewportWidth / 195));
                    gridLayout.setColumns(cols);
                    gridPanel.revalidate();
                }
            }
        });

        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(new Color(241, 246, 255));
        paginationPanel.add(new JLabel("• • • •"));

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(paginationPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    // ====================== RIGHT PANEL ======================
    private JPanel createRightPanel() {
        RoundedPanel panel = new RoundedPanel(16, true);
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("THÔNG TIN SẢN PHẨM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        panel.add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        int row = 0;

        // Ảnh
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setBackground(Color.WHITE);
        lblImageRight = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImageRight.setPreferredSize(new Dimension(150, 100));
        lblImageRight.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblImageRight.setOpaque(true);
        lblImageRight.setBackground(new Color(240, 240, 240));

        JButton btnSelectImage = new RoundedButton("Chọn ảnh");
        btnSelectImage.setBackground(new Color(153, 225, 255));
        btnSelectImage.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fileChooser.getSelectedFile();
                try {
                    BufferedImage bImage = ImageIO.read(selectedImageFile);
                    Image scaled = bImage.getScaledInstance(150, 100, Image.SCALE_SMOOTH);
                    lblImageRight.setIcon(new ImageIcon(scaled));
                    lblImageRight.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tải ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel btnWrapper = new JPanel();
        btnWrapper.setBackground(Color.WHITE);
        btnWrapper.add(btnSelectImage);

        imagePanel.add(lblImageRight, BorderLayout.CENTER);
        imagePanel.add(btnWrapper, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(imagePanel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        txtMaSP = new RoundedTextField("", 15);
        txtTenSP = new RoundedTextField("", 15);
        txtHoatChat = new RoundedTextField("", 15);
        txtSoLuong = new RoundedTextField("", 15);
        txtDonGia = new RoundedTextField("", 15);
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        cbLoaiSP = new JComboBox<>(new String[] { "ETC", "OTC", "TPCN" });

        addFormField(formPanel, gbc, row++, "Mã sản phẩm:", txtMaSP, false);
        addFormField(formPanel, gbc, row++, "Tên sản phẩm:", txtTenSP, true);
        addFormField(formPanel, gbc, row++, "Hoạt chất:", txtHoatChat, true);
        // Tồn kho được cập nhật từ Lô, không nhập tay ở màn Sản phẩm
        addFormField(formPanel, gbc, row++, "Số lượng tồn:", txtSoLuong, false);
        addFormField(formPanel, gbc, row++, "Đơn giá:", txtDonGia, true);
        addFormField(formPanel, gbc, row++, "Loại:", cbLoaiSP, true);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(new JScrollPane(txtMoTa), gbc);
        row++;

        // Hệ số quy đổi (Thêm mới)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Hệ số quy đổi:"), gbc);

        JPanel pnlHeSo = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlHeSo.setBackground(Color.WHITE);
        txtHeSoQuyDoi = new RoundedTextField("", 5);
        txtHeSoQuyDoi.setPreferredSize(new Dimension(60, 32));

        DonVi[] dsDonVi = { DonVi.HOP, DonVi.VI, DonVi.VIEN, DonVi.CHAI, DonVi.TUYP, DonVi.CAI };
        cbDonViQuyDoi = new JComboBox<>(dsDonVi);
        cbDonViQuyDoi.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DonVi) {
                    setText(hienThiTenDonVi((DonVi) value));
                }
                return this;
            }
        });
        cbDonViQuyDoi.setPreferredSize(new Dimension(80, 32));

        btnThemDonVi = new RoundedButton("+ Thêm");
        btnThemDonVi.setBackground(new Color(0, 153, 51));
        btnThemDonVi.setForeground(Color.WHITE);
        btnThemDonVi.setPreferredSize(new Dimension(80, 32));
        btnThemDonVi.addActionListener(e -> themDonViVaoBang());

        btnXoaDonVi = new RoundedButton("- Xóa");
        btnXoaDonVi.setBackground(new Color(255, 102, 102));
        btnXoaDonVi.setForeground(Color.WHITE);
        btnXoaDonVi.setPreferredSize(new Dimension(70, 32));
        btnXoaDonVi.addActionListener(e -> xoaDonViKhoiBang());

        pnlHeSo.add(txtHeSoQuyDoi);
        pnlHeSo.add(cbDonViQuyDoi);
        pnlHeSo.add(btnThemDonVi);
        pnlHeSo.add(btnXoaDonVi);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(pnlHeSo, gbc);
        row++;

        // Bảng đơn vị quy đổi (dưới hệ số quy đổi)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Đơn vị hiện có:"), gbc);

        donViQuyDoiModel = new DefaultTableModel(
                new Object[] { "Đơn vị", "Số lượng quy đổi" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDonViQuyDoi = new JTable(donViQuyDoiModel);
        tblDonViQuyDoi.setRowHeight(26);
        tblDonViQuyDoi.getTableHeader().setReorderingAllowed(false);
        tblDonViQuyDoi.setFillsViewportHeight(true);

        JScrollPane donViScroll = new JScrollPane(tblDonViQuyDoi);
        donViScroll.setPreferredSize(new Dimension(200, 120));

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(donViScroll, gbc);
        row++;

        // reset fill mặc định cho các field phía sau (nếu có)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnThem = createStyledButton("Thêm", new Color(0, 204, 204), Color.WHITE);
        JButton btnSua = createStyledButton("Sửa", new Color(255, 255, 102), Color.BLACK);
        JButton btnXoa = createStyledButton("Xóa", new Color(255, 102, 102), Color.WHITE);
        JButton btnLamMoi = createStyledButton("Làm mới", new Color(0, 204, 255), Color.WHITE);

        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        rightPanel = panel;
        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row,
            String labelText, JComponent inputComp, boolean isEditable) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(labelText), gbc);

        if (inputComp instanceof JTextField) {
            ((JTextField) inputComp).setEditable(isEditable);
            if (!isEditable)
                inputComp.setBackground(new Color(235, 235, 235));
        }
        inputComp.setPreferredSize(new Dimension(200, 32));

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(inputComp, gbc);
    }

    // ====================== LOGIC ======================

    public void loadDanhSachSanPham() {
        danhSachSanPham = sanPhamDAO.laySanPhamDangKinhDoanh();
        hienThiSanPhamLenGrid(danhSachSanPham);
    }

    private void updateSearch() {
        if (isUpdatingSearch) return;

        String text = txtSearch.getText().trim();
        if (text.isEmpty() || text.equals("Tìm kiếm theo mã hoặc tên...")) {
            searchPopup.setVisible(false);
            if (text.isEmpty()) {
                locVaHienThiSanPham();
            }
            return;
        }

        SwingUtilities.invokeLater(() -> {
            List<SanPham> results = new ArrayList<>();
            for (SanPham sp : danhSachSanPham) {
                if (sp.getMaSanPham().toLowerCase().contains(text.toLowerCase()) ||
                    sp.getTenSanPham().toLowerCase().contains(text.toLowerCase())) {
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
                if (count >= 10) break;
                JMenuItem item = new JMenuItem(sp.getMaSanPham() + " - " + sp.getTenSanPham());
                item.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                item.addActionListener(e -> {
                    isUpdatingSearch = true;
                    txtSearch.setText(sp.getTenSanPham());
                    isUpdatingSearch = false;
                    searchPopup.setVisible(false);
                    hienThiSanPhamLenGrid(List.of(sp));
                    hienThiChiTietSanPham(sp);
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

    private void locVaHienThiSanPham() {
        String tuKhoa = txtSearch.getText().trim();
        if ("Tìm kiếm theo mã hoặc tên...".equals(tuKhoa))
            tuKhoa = "";

        String danhMuc = (String) cbDanhMuc.getSelectedItem();

        List<SanPham> ketQua = new ArrayList<>();

        for (SanPham sp : danhSachSanPham) {
            boolean khopTuKhoa = tuKhoa.isEmpty() ||
                    sp.getMaSanPham().toLowerCase().contains(tuKhoa.toLowerCase()) ||
                    sp.getTenSanPham().toLowerCase().contains(tuKhoa.toLowerCase());

            boolean khopDanhMuc = danhMuc.equals("Tất cả") ||
                    (danhMuc.equals("Thuốc ETC") && sp.getLoaiSanPham() == LoaiSanPham.ETC) ||
                    (danhMuc.equals("Thuốc OTC") && sp.getLoaiSanPham() == LoaiSanPham.OTC) ||
                    (danhMuc.equals("TPCN") && sp.getLoaiSanPham() == LoaiSanPham.TPCN);

            if (khopTuKhoa && khopDanhMuc) {
                ketQua.add(sp);
            }
        }

        hienThiSanPhamLenGrid(ketQua);
    }

    private void hienThiSanPhamLenGrid(List<SanPham> danhSach) {
        gridPanel.removeAll();

        for (SanPham sp : danhSach) {
            JPanel card = createProductCard(sp);
            gridPanel.add(card);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createProductCard(SanPham sp) {
        RoundedPanel card = new RoundedPanel(14, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        card.setPreferredSize(new Dimension(180, 220));
        card.setMaximumSize(new Dimension(180, 220));
        card.setMinimumSize(new Dimension(180, 220));

        // Ảnh
        JLabel lblImage = new JLabel("", SwingConstants.CENTER);
        lblImage.setOpaque(true);
        lblImage.setBackground(new Color(230, 230, 230));
        lblImage.setPreferredSize(new Dimension(160, 80));
        lblImage.setMaximumSize(new Dimension(160, 80));
        lblImage.setMinimumSize(new Dimension(160, 80));
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = loadProductImageIcon(sp.getMaSanPham(), 160, 80);
        if (icon != null) {
            lblImage.setIcon(icon);
            lblImage.setText("");
        } else {
            lblImage.setIcon(null);
            lblImage.setText("Ảnh SP");
        }

        // Tên
        JLabel lblName = new JLabel("<html><div style='text-align: center;'>" + sp.getTenSanPham() + "</div></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setPreferredSize(new Dimension(160, 35));

        // Giá
        JLabel lblPrice = new JLabel(String.format("Giá: %,dđ", (long) sp.getDonGiaCoBan()));
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JLabel lblStatus = new JLabel("Tồn: " + sp.getSoLuongTon());
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(sp.getSoLuongTon() > 0 ? new Color(0, 153, 0) : Color.RED);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(lblPrice);
        infoPanel.add(lblStatus);

        card.add(lblImage);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblName);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(infoPanel);

        // Click event
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiChiTietSanPham(sp);
            }
        });

        return card;
    }

    private void hienThiChiTietSanPham(SanPham sp) {
        sanPhamDangChon = sp;
        selectedImageFile = null;

        txtMaSP.setText(sp.getMaSanPham());
        txtTenSP.setText(sp.getTenSanPham());
        txtHoatChat.setText(sp.getHoatChat() != null ? sp.getHoatChat() : "");
        txtSoLuong.setText(String.valueOf(sp.getSoLuongTon()));
        txtDonGia.setText(String.format("%.0f", sp.getDonGiaCoBan()));
        txtMoTa.setText(sp.getMoTa() != null ? sp.getMoTa() : "");

        cbLoaiSP.setSelectedItem(sp.getLoaiSanPham().name());

        // Load ảnh (Sử dụng hàm loadProductImageIcon để có scaling)
        ImageIcon icon = loadProductImageIcon(sp.getMaSanPham(), 150, 100);
        if (icon != null) {
            lblImageRight.setIcon(icon);
            lblImageRight.setText("");
        } else {
            lblImageRight.setIcon(null);
            lblImageRight.setText("Không có ảnh");
        }

        loadBangDonViQuyDoi(sp);
    }

    private void themDonViVaoBang() {
        DonVi dv = (DonVi) cbDonViQuyDoi.getSelectedItem();
        int soLuong = parseIntOrZero(txtHeSoQuyDoi.getText());
        if (soLuong <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng quy đổi phải > 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra trùng lặp đơn vị trong bảng
        for (int i = 0; i < donViQuyDoiModel.getRowCount(); i++) {
            String tenDVHienTai = donViQuyDoiModel.getValueAt(i, 0).toString();
            if (tenDVHienTai.equals(hienThiTenDonVi(dv))) {
                JOptionPane.showMessageDialog(this, "Đơn vị này đã tồn tại trong bảng!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        donViQuyDoiModel.addRow(new Object[] { hienThiTenDonVi(dv), soLuong });
        txtHeSoQuyDoi.setText("");
    }

    private void xoaDonViKhoiBang() {
        int selectedRow = tblDonViQuyDoi.getSelectedRow();
        if (selectedRow >= 0) {
            donViQuyDoiModel.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần xóa trong bảng!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void loadBangDonViQuyDoi(SanPham sp) {
        donViQuyDoiModel.setRowCount(0);
        if (sp == null || sp.getMaSanPham() == null || sp.getMaSanPham().trim().isEmpty()) {
            return;
        }

        List<DonViQuyDoi> ds = donViQuyDoiDAO.timTheoMaSanPham(sp.getMaSanPham());
        if (ds == null || ds.isEmpty()) {
            return;
        }

        for (DonViQuyDoi dv : ds) {
            if (dv != null && dv.getTenDonVi() != null) {
                donViQuyDoiModel.addRow(new Object[] { hienThiTenDonVi(dv.getTenDonVi()), dv.getHeSoQuyDoi() });
            }
        }
    }

    private DonVi getDonViTuTenHienThi(String ten) {
        for (DonVi dv : DonVi.values()) {
            if (hienThiTenDonVi(dv).equals(ten)) {
                return dv;
            }
        }
        return DonVi.VIEN;
    }

    private String hienThiTenDonVi(DonVi dv) {
        if (dv == null)
            return "";
        switch (dv) {
            case HOP:
                return "Hộp";
            case VI:
                return "Vỉ";
            case VIEN:
                return "Viên";
            case TUYP:
                return "Tuýp";
            case CHAI:
                return "Chai";
            case CAI:
                return "Cái";
            default:
                return dv.name();
        }
    }

    // ====================== CRUD ======================
    private void themSanPham() {
        try {
            String ten = txtTenSP.getText().trim();
            if (ten.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên sản phẩm!", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LoaiSanPham phanLoai = LoaiSanPham.valueOf((String) cbLoaiSP.getSelectedItem());
            String maMoi = sanPhamDAO.taoMaSanPhamTuDong(phanLoai, ten);
            if (sanPhamDAO.tonTaiMaSanPham(maMoi)) {
                JOptionPane.showMessageDialog(this, "Mã sản phẩm bị trùng: " + maMoi, "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            double donGia = parseDoubleOrZero(txtDonGia.getText());

            SanPham sp = new SanPham();
            sp.setMaSanPham(maMoi);
            sp.setTenSanPham(ten);
            sp.setLoaiSanPham(phanLoai);
            sp.setHoatChat(txtHoatChat.getText().trim());
            // Sản phẩm mới: tồn kho mặc định = 0 (cập nhật từ Lô)
            sp.setSoLuongTon(0);
            sp.setDonGiaCoBan(donGia);
            sp.setMoTa(txtMoTa.getText().trim());

            // Mặc định hợp lý (panel chưa có input cho 2 trường này)
            sp.setTrangThaiKinhDoanh(true);
            sp.setThue(0.10);

            boolean success = sanPhamDAO.them(sp);
            if (success) {
                luuDonViQuyDoi(maMoi);
                luuAnhSanPham(maMoi);
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công: " + maMoi, "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachSanPham();

                // chọn lại item vừa thêm (nếu có)
                SanPham spMoi = sanPhamDAO.timTheoMa(maMoi);
                if (spMoi != null) {
                    hienThiChiTietSanPham(spMoi);
                } else {
                    txtMaSP.setText(maMoi);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thất bại!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng/Đơn giá không hợp lệ!", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Không thể thêm sản phẩm: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private ImageIcon loadProductImageIcon(String maSanPham, int w, int h) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return null;
        }
        String base = maSanPham.trim();
        String[] exts = new String[] { "png", "jpg", "jpeg" };

        for (String ext : exts) {
            String relative = "images/anhSanPham/" + base + "." + ext;
            String absoluteClasspath = "/" + relative;

            try {
                BufferedImage bImage = null;

                // 1) Try classpath (jar/IDE)
                java.net.URL url = getClass().getResource(absoluteClasspath);
                if (url == null) {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl == null)
                        cl = getClass().getClassLoader();
                    url = (cl != null) ? cl.getResource(relative) : null;
                }

                if (url != null) {
                    bImage = ImageIO.read(url);
                } else {
                    // 2) Fallback: load trực tiếp từ filesystem
                    java.nio.file.Path p1 = java.nio.file.Paths.get("src", "main", "resources", relative);
                    java.nio.file.Path p2 = java.nio.file.Paths.get(System.getProperty("user.dir", ""), "src", "main",
                            "resources", relative);
                    java.nio.file.Path chosen = java.nio.file.Files.exists(p1) ? p1
                            : (java.nio.file.Files.exists(p2) ? p2 : null);

                    if (chosen != null) {
                        bImage = ImageIO.read(chosen.toFile());
                    }
                }

                if (bImage != null) {
                    Image scaled = bImage.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                    return new ImageIcon(scaled);
                }
            } catch (Exception e) {
                // Ignore and try next extension
            }
        }

        return null;
    }

    private int parseIntOrZero(String s) {
        if (s == null)
            return 0;
        String v = s.trim();
        if (v.isEmpty())
            return 0;
        v = v.replaceAll("[^0-9\\-]+", "");
        if (v.isEmpty() || "-".equals(v))
            return 0;
        return Integer.parseInt(v);
    }

    private double parseDoubleOrZero(String s) {
        if (s == null)
            return 0;
        String v = s.trim();
        if (v.isEmpty())
            return 0;
        // remove thousand separators like '.' or ',' and currency symbols
        v = v.replaceAll("[^0-9\\-]+", "");
        if (v.isEmpty() || "-".equals(v))
            return 0;
        return Double.parseDouble(v);
    }

    private void suaSanPham() {
        if (sanPhamDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            sanPhamDangChon.setTenSanPham(txtTenSP.getText().trim());
            sanPhamDangChon.setHoatChat(txtHoatChat.getText().trim());
            // Tồn kho được cập nhật từ Lô, không sửa tại màn Sản phẩm
            sanPhamDangChon.setDonGiaCoBan(parseDoubleOrZero(txtDonGia.getText()));
            sanPhamDangChon.setMoTa(txtMoTa.getText().trim());
            sanPhamDangChon.setLoaiSanPham(LoaiSanPham.valueOf((String) cbLoaiSP.getSelectedItem()));

            boolean success = sanPhamDAO.capNhat(sanPhamDangChon);
            if (success) {
                luuDonViQuyDoi(sanPhamDangChon.getMaSanPham());
                luuAnhSanPham(sanPhamDangChon.getMaSanPham());
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachSanPham(); // Refresh danh sách
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaSanPham() {
        if (sanPhamDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn xóa sản phẩm " + sanPhamDangChon.getTenSanPham() + "?",
                "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            sanPhamDangChon.setTrangThaiKinhDoanh(false);
            boolean success = sanPhamDAO.capNhat(sanPhamDangChon);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa sản phẩm thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachSanPham();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lamMoiForm() {
        sanPhamDangChon = null;
        selectedImageFile = null;
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtHoatChat.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        txtMoTa.setText("");
        cbLoaiSP.setSelectedIndex(0);
        lblImageRight.setIcon(null);
        lblImageRight.setText("Chưa có ảnh");
        txtHeSoQuyDoi.setText("");
        if (donViQuyDoiModel != null) {
            donViQuyDoiModel.setRowCount(0);
        }
    }

    private void luuDonViQuyDoi(String maSP) {
        SanPham sp = sanPhamDAO.timTheoMa(maSP);
        if (sp == null)
            return;

        List<DonViQuyDoi> dsHienCo = donViQuyDoiDAO.timTheoMaSanPham(maSP);
        List<String> dsTenDonViTrenBang = new ArrayList<>();

        for (int i = 0; i < donViQuyDoiModel.getRowCount(); i++) {
            String tenHienThi = donViQuyDoiModel.getValueAt(i, 0).toString();
            int heSo = Integer.parseInt(donViQuyDoiModel.getValueAt(i, 1).toString());
            DonVi donViEnum = getDonViTuTenHienThi(tenHienThi);
            dsTenDonViTrenBang.add(donViEnum.name());

            DonViQuyDoi dvCu = null;
            for (DonViQuyDoi dv : dsHienCo) {
                if (dv.getTenDonVi() == donViEnum) {
                    dvCu = dv; break;
                }
            }

            if (dvCu != null) {
                dvCu.setHeSoQuyDoi(heSo);
                donViQuyDoiDAO.capNhat(dvCu);
            } else {
                DonViQuyDoi dvMoi = new DonViQuyDoi();
                dvMoi.setMaDonVi(donViQuyDoiDAO.taoMaDonViTuDong());
                dvMoi.setTenDonVi(donViEnum);
                dvMoi.setHeSoQuyDoi(heSo);
                dvMoi.setSanPham(sp);
                donViQuyDoiDAO.them(dvMoi);
            }
        }

        for (DonViQuyDoi dv : dsHienCo) {
            if (!dsTenDonViTrenBang.contains(dv.getTenDonVi().name())) {
                donViQuyDoiDAO.xoa(dv.getMaDonVi());
            }
        }
    }

    private void luuAnhSanPham(String maSanPham) {
        if (selectedImageFile == null) return;
        try {
            File dir1 = new File("src/main/resources/images/anhSanPham");
            if (!dir1.exists()) dir1.mkdirs();
            File dest1 = new File(dir1, maSanPham + ".png");
            BufferedImage bImage = ImageIO.read(selectedImageFile);
            ImageIO.write(bImage, "png", dest1);

            File dir2 = new File("target/classes/images/anhSanPham");
            if (dir2.exists() || dir2.mkdirs()) {
                File dest2 = new File(dir2, maSanPham + ".png");
                ImageIO.write(bImage, "png", dest2);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Helper button
    private RoundedButton createStyledButton(String text, Color bgColor, Color fgColor) {
        RoundedButton btn = new RoundedButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }
}