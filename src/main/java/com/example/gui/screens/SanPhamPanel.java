package com.example.gui.screens;

import com.example.service.DonViQuyDoiService;
import com.example.service.SanPhamService;
import com.example.service.SuPhanBoLoService;
import com.example.service.CaLamService;
import com.example.entity.DonViQuyDoi;
import com.example.entity.SanPham;
import com.example.entity.SuPhanBoLo;
import com.example.entity.CaLam;
import com.example.entity.TaiKhoan;
import com.example.entity.enums.DonVi;
import com.example.entity.enums.LoaiSanPham;
import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

public class SanPhamPanel extends JPanel {

    private SanPhamService sanPhamService = new SanPhamService();
    private DonViQuyDoiService donViQuyDoiService = new DonViQuyDoiService();
    private List<SanPham> danhSachSanPham = new ArrayList<>();

    // Cache ảnh sản phẩm: maSanPham -> ImageIcon (kích thước 160x90)
    private final java.util.Map<String, ImageIcon> imageCache = new java.util.concurrent.ConcurrentHashMap<>();
    // Cache đơn vị quy đổi: maSanPham -> List<DonViQuyDoi>
    private final java.util.Map<String, List<DonViQuyDoi>> donViCache = new java.util.HashMap<>();

    // Các biến phục vụ phần Sản phẩm lỗi
    private final SuPhanBoLoService suPhanBoLoService = new SuPhanBoLoService();
    private final CaLamService caLamService = new CaLamService();
    private List<SuPhanBoLo> danhSachLoi = new ArrayList<>();
    private SuPhanBoLo hangLoiDangChon = null;
    private CardLayout tabCardLayout;
    private JPanel tabCardPanel;
    private boolean isTabNormalActive = true;

    // Components
    private JPanel gridPanel;
    private GridLayout gridLayout;
    private JTextField txtSearch;
    private JComboBox<String> cbDanhMuc;
    private JPanel rightPanel;
    private JLabel lblImageRight;
    private JTextField txtMaSP, txtTenSP, txtHoatChat, txtSoLuong, txtDonGia, txtThue;
    private JComboBox<String> cbLoaiSP;
    private JTextArea txtMoTa;

    // Components cho Tab Sản Phẩm Lỗi
    private JPanel gridPanelLoi;
    private GridLayout gridLayoutLoi;
    private JTextField txtSearchLoi;
    private JComboBox<String> cbDanhMucLoi;
    private JComboBox<String> cbSortTimeLoi;
    private JPanel rightPanelLoi;
    private JLabel lblImageRightLoi;
    private JTextField txtMaHoaDonLoi, txtMaSPLoi, txtTenSPLoi, txtHoatChatLoi, txtSoLuongLoi, txtDonGiaLoi, txtSoLoLoi,
            txtHSDLoi;
    private JComboBox<String> cbLoaiSPLoi;
    private JTextArea txtMoTaLoi;
    private JButton btnTraNSX;

    // Đơn vị quy đổi UI
    private JTextField txtHeSoQuyDoi, txtBarcodeDonVi;
    private JComboBox<DonVi> cbDonViQuyDoi;
    private JButton btnThemDonVi;
    private JButton btnXoaDonVi;

    private JTable tblDonViQuyDoi;
    private DefaultTableModel donViQuyDoiModel;
    private SanPham sanPhamDangChon = null;
    private File selectedImageFile = null;
    private JPopupMenu searchPopup;
    private boolean isUpdatingSearch = false;
    private TaiKhoan taiKhoan;

    public SanPhamPanel(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255));

        tabCardLayout = new CardLayout();
        tabCardPanel = new JPanel(tabCardLayout);
        tabCardPanel.setOpaque(false);

        // Tab 1: Sản phẩm
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);
        tabCardPanel.add(splitPane, "NORMAL");

        // Tab 2: Sản phẩm lỗi
        JSplitPane splitPaneLoi = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createLeftPanelLoi(), createRightPanelLoi());
        splitPaneLoi.setResizeWeight(0.65);
        splitPaneLoi.setBorder(null);
        splitPaneLoi.setContinuousLayout(true);
        tabCardPanel.add(splitPaneLoi, "LOI");

        JPanel tabBarPanel = createTabBar(tabCardLayout, tabCardPanel);
        add(tabBarPanel, BorderLayout.NORTH);
        add(tabCardPanel, BorderLayout.CENTER);

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

        cbDanhMuc = new JComboBox<>(new String[] { "Tất cả", "Thuốc ETC", "Thuốc OTC", "TPCN", "Mỹ phẩm" });
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
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSearch();
            }
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
            fileChooser
                    .setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "jpeg"));
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
        txtThue = new RoundedTextField("", 15);
        txtMoTa = new JTextArea(3, 20);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        cbLoaiSP = new JComboBox<>(new String[] { "ETC", "OTC", "TPCN", "MY_PHAM" });

        addFormField(formPanel, gbc, row++, "Mã sản phẩm:", txtMaSP, false);
        addFormField(formPanel, gbc, row++, "Tên sản phẩm:", txtTenSP, true);
        addFormField(formPanel, gbc, row++, "Hoạt chất:", txtHoatChat, true);
        // Tồn kho được cập nhật từ Lô, không nhập tay ở màn Sản phẩm
        addFormField(formPanel, gbc, row++, "Số lượng tồn:", txtSoLuong, false);
        addFormField(formPanel, gbc, row++, "Đơn giá:", txtDonGia, true);
        addFormField(formPanel, gbc, row++, "Loại:", cbLoaiSP, true);
        addFormField(formPanel, gbc, row++, "Thuế (%):", txtThue, true);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JScrollPane moTaScroll = new JScrollPane(txtMoTa);
        moTaScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(moTaScroll, gbc);
        row++;

        // Hệ số quy đổi (Thêm mới)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Hệ số quy đổi:"), gbc);

        JPanel pnlHeSo = new JPanel(new GridBagLayout());
        pnlHeSo.setBackground(Color.WHITE);
        GridBagConstraints subGbc = new GridBagConstraints();
        subGbc.fill = GridBagConstraints.HORIZONTAL;
        subGbc.insets = new Insets(2, 0, 2, 0);

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

        txtBarcodeDonVi = new RoundedTextField("Mã vạch", 10);
        txtBarcodeDonVi.setPreferredSize(new Dimension(100, 32));
        txtBarcodeDonVi.setForeground(Color.GRAY);
        txtBarcodeDonVi.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Mã vạch".equals(txtBarcodeDonVi.getText())) {
                    txtBarcodeDonVi.setText("");
                    txtBarcodeDonVi.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtBarcodeDonVi.getText().trim().isEmpty()) {
                    txtBarcodeDonVi.setText("Mã vạch");
                    txtBarcodeDonVi.setForeground(Color.GRAY);
                }
            }
        });

        btnThemDonVi = new RoundedButton("+ Thêm");
        if (taiKhoan.getNhanVien().getChucVu() != com.example.entity.enums.ChucVu.NHAN_VIEN_QUAN_LY) btnThemDonVi.setVisible(false);
        btnThemDonVi.setBackground(new Color(40, 167, 69)); // Premium Green
        btnThemDonVi.setForeground(Color.WHITE);
        btnThemDonVi.setPreferredSize(new Dimension(100, 32));
        btnThemDonVi.addActionListener(e -> themDonViVaoBang());

        btnXoaDonVi = new RoundedButton("- Xóa");
        if (taiKhoan.getNhanVien().getChucVu() != com.example.entity.enums.ChucVu.NHAN_VIEN_QUAN_LY) btnXoaDonVi.setVisible(false);
        btnXoaDonVi.setBackground(new Color(220, 53, 69)); // Premium Red
        btnXoaDonVi.setForeground(Color.WHITE);
        btnXoaDonVi.setPreferredSize(new Dimension(100, 32));
        btnXoaDonVi.addActionListener(e -> xoaDonViKhoiBang());

        // Hàng 1: Đầu vào (Sử dụng GridBagLayout để co giãn tự động)
        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setBackground(Color.WHITE);
        GridBagConstraints inputGbc = new GridBagConstraints();
        inputGbc.fill = GridBagConstraints.HORIZONTAL;
        inputGbc.insets = new Insets(0, 0, 0, 5);

        inputGbc.gridx = 0;
        inputGbc.weightx = 0.2;
        pnlInputs.add(txtHeSoQuyDoi, inputGbc);

        inputGbc.gridx = 1;
        inputGbc.weightx = 0.3;
        pnlInputs.add(cbDonViQuyDoi, inputGbc);

        inputGbc.gridx = 2;
        inputGbc.weightx = 0.5;
        inputGbc.insets = new Insets(0, 0, 0, 0); // Không có khoảng cách bên phải cho phần tử cuối
        pnlInputs.add(txtBarcodeDonVi, inputGbc);

        // Hàng 2: Các nút bấm (Sử dụng GridLayout chia đều diện tích)
        JPanel pnlButtons = new JPanel(new GridLayout(1, 2, 8, 0));
        pnlButtons.setBackground(Color.WHITE);
        pnlButtons.add(btnThemDonVi);
        pnlButtons.add(btnXoaDonVi);

        subGbc.gridx = 0;
        subGbc.gridy = 0;
        subGbc.weightx = 1.0;
        pnlHeSo.add(pnlInputs, subGbc);

        subGbc.gridx = 0;
        subGbc.gridy = 1;
        subGbc.weightx = 1.0;
        subGbc.insets = new Insets(6, 0, 0, 0); // Khoảng cách giữa dòng nhập và dòng nút bấm
        pnlHeSo.add(pnlButtons, subGbc);

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
                new Object[] { "Đơn vị", "Số lượng quy đổi", "Mã vạch" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDonViQuyDoi = new JTable(donViQuyDoiModel);
        tblDonViQuyDoi.setRowHeight(28);
        tblDonViQuyDoi.getTableHeader().setReorderingAllowed(false);
        tblDonViQuyDoi.setFillsViewportHeight(true);

        // Đặt kích thước cột phù hợp để không bị cắt chữ trong panel thông tin
        tblDonViQuyDoi.getColumnModel().getColumn(0).setPreferredWidth(75);
        tblDonViQuyDoi.getColumnModel().getColumn(1).setPreferredWidth(110);
        tblDonViQuyDoi.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane donViScroll = new JScrollPane(tblDonViQuyDoi);
        donViScroll.setPreferredSize(new Dimension(300, 250));
        donViScroll.setMaximumSize(new Dimension(300, 250));

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(donViScroll, gbc);
        row++;

        // reset fill mặc định cho các field phía sau (nếu có)
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBorder(null);
        formScroll.getViewport().setBackground(Color.WHITE);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(formScroll, BorderLayout.CENTER);

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

        if (taiKhoan.getNhanVien().getChucVu() == com.example.entity.enums.ChucVu.NHAN_VIEN_QUAN_LY) {
            buttonPanel.add(btnThem);
            buttonPanel.add(btnSua);
            buttonPanel.add(btnXoa);
        }
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
        // Xóa cache khi reload toàn bộ để bảo đảm dữ liệu luôn mới nhất
        imageCache.clear();
        donViCache.clear();
        danhSachSanPham = sanPhamService.laySanPhamDangKinhDoanh();
        // Pre-fetch tất cả DonViQuyDoi cho toàn bộ sản phẩm trong background
        // để tránh N query riêng lẻ khi tạo từng card
        new javax.swing.SwingWorker<java.util.Map<String, List<DonViQuyDoi>>, Void>() {
            @Override
            protected java.util.Map<String, List<DonViQuyDoi>> doInBackground() {
                java.util.Map<String, List<DonViQuyDoi>> map = new java.util.HashMap<>();
                for (SanPham sp : danhSachSanPham) {
                    List<DonViQuyDoi> dvList = donViQuyDoiService.timTheoMaSanPham(sp.getMaSanPham());
                    if (dvList != null) {
                        map.put(sp.getMaSanPham(), dvList);
                    }
                }
                return map;
            }
            @Override
            protected void done() {
                try {
                    donViCache.putAll(get());
                } catch (Exception ignored) {}
                hienThiSanPhamLenGrid(danhSachSanPham);
                loadDanhSachLoi();
            }
        }.execute();
    }

    private void updateSearch() {
        if (isUpdatingSearch)
            return;

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
                if (count >= 10)
                    break;
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
                    (danhMuc.equals("TPCN") && sp.getLoaiSanPham() == LoaiSanPham.TPCN) ||
                    (danhMuc.equals("Mỹ phẩm") && sp.getLoaiSanPham() == LoaiSanPham.MY_PHAM);

            if (khopTuKhoa && khopDanhMuc) {
                ketQua.add(sp);
            }
        }

        hienThiSanPhamLenGrid(ketQua);
    }

    private void hienThiSanPhamLenGrid(List<SanPham> danhSach) {
        gridPanel.removeAll();

        for (SanPham sp : danhSach) {
            // Lấy DonViQuyDoi từ cache, nếu chưa có thì query rồi cache lại
            List<DonViQuyDoi> dvList = donViCache.computeIfAbsent(
                    sp.getMaSanPham(),
                    k -> {
                        List<DonViQuyDoi> r = donViQuyDoiService.timTheoMaSanPham(k);
                        return r != null ? r : new ArrayList<>();
                    });
            JPanel card = createProductCard(sp, dvList);
            gridPanel.add(card);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createProductCard(SanPham sp, List<DonViQuyDoi> dsDVPreloaded) {
        RoundedPanel card = new RoundedPanel(14, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(12, 10, 12, 10)));
        card.setPreferredSize(new Dimension(180, 270));
        card.setMaximumSize(new Dimension(180, 270));
        card.setMinimumSize(new Dimension(180, 270));

        // 1. Ảnh sản phẩm đặt trong khung bo góc màu trắng, viền xám nhạt
        RoundedPanel imgContainer = new RoundedPanel(10, false);
        imgContainer.setLayout(new BorderLayout());
        imgContainer.setBackground(Color.WHITE);
        imgContainer.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
        imgContainer.setPreferredSize(new Dimension(160, 90));
        imgContainer.setMaximumSize(new Dimension(160, 90));
        imgContainer.setMinimumSize(new Dimension(160, 90));
        imgContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblImage = new JLabel("", SwingConstants.CENTER);
        lblImage.setText("Ảnh SP");
        lblImage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblImage.setForeground(Color.GRAY);
        imgContainer.add(lblImage, BorderLayout.CENTER);

        // Load ảnh bất đồng bộ — không chặn EDT, giúp grid render tức thì
        loadProductImageAsync(sp.getMaSanPham(), 160, 90, lblImage);

        // 2. Tên sản phẩm
        JLabel lblName = new JLabel("<html><div style='text-align: center; width: 140px; font-family: Segoe UI;'>"
                + sp.getTenSanPham() + "</div></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(33, 37, 41));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setPreferredSize(new Dimension(160, 36));
        lblName.setMaximumSize(new Dimension(160, 36));

        // 3. Badges hiển thị đơn vị quy đổi
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        badgePanel.setBackground(Color.WHITE);
        badgePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        badgePanel.setPreferredSize(new Dimension(160, 26));
        badgePanel.setMaximumSize(new Dimension(160, 26));

        // 4. Panel chứa thông tin (Giá, Tình trạng, Số lượng tồn)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setPreferredSize(new Dimension(160, 60));
        infoPanel.setMaximumSize(new Dimension(160, 60));

        JLabel lblPrice = new JLabel("", SwingConstants.LEFT);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrice.setForeground(new Color(33, 37, 41));
        lblPrice.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStatus = new JLabel("", SwingConstants.LEFT);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStock = new JLabel("", SwingConstants.LEFT);
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStock.setForeground(new Color(100, 100, 100));
        lblStock.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(lblPrice);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(lblStatus);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(lblStock);

        // Sử dụng dsDV được truyền vào từ cache (tránh query DB riêng lẻ cho từng card)
        final List<DonViQuyDoi> dsDV = (dsDVPreloaded != null) ? new ArrayList<>(dsDVPreloaded) : new ArrayList<>();
        // Sắp xếp đơn vị từ lớn đến nhỏ (Hộp -> Vỉ -> Viên)
        dsDV.sort((d1, d2) -> Integer.compare(d2.getHeSoQuyDoi(), d1.getHeSoQuyDoi()));

        final DonViQuyDoi[] activeDV = { dsDV.isEmpty() ? null : dsDV.get(0) };

        // Hàm cập nhật trạng thái UI động của Card khi chọn đơn vị quy đổi khác nhau
        Runnable updateCardUI = new Runnable() {
            @Override
            public void run() {
                badgePanel.removeAll();

                if (activeDV[0] != null) {
                    double priceVal = sp.getDonGiaCoBan() * activeDV[0].getHeSoQuyDoi();
                    lblPrice.setText(String.format("Giá: %,.0fđ / %s", priceVal, activeDV[0].getTenDonVi().getMoTa()));
                } else {
                    lblPrice.setText(String.format("Giá: %,.0fđ", sp.getDonGiaCoBan()));
                }

                if (sp.getSoLuongTon() > 0) {
                    lblStatus.setText("<html>Tình trạng: <font color='#28a745'><b>Còn hàng</b></font></html>");
                } else {
                    lblStatus.setText("<html>Tình trạng: <font color='#dc3545'><b>Hết hàng</b></font></html>");
                }

                // Tìm đơn vị bé nhất (hệ số quy đổi = 1) để hiển thị số lượng tồn chính xác
                // nhất
                DonViQuyDoi smallestUnit = null;
                for (DonViQuyDoi dv : dsDV) {
                    if (dv.getHeSoQuyDoi() == 1) {
                        smallestUnit = dv;
                        break;
                    }
                }
                String smallestUnitName = (smallestUnit != null) ? smallestUnit.getTenDonVi().getMoTa() : "Đơn vị";
                lblStock.setText("Số lượng tồn: " + sp.getSoLuongTon() + " " + smallestUnitName);

                for (final DonViQuyDoi dv : dsDV) {
                    final boolean isActive = dv.equals(activeDV[0]);
                    RoundedPanel badge = new RoundedPanel(8, false) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(getBackground());
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                            if (isActive) {
                                g2.setColor(new Color(0, 123, 255));
                                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                            }
                            g2.dispose();
                        }
                    };
                    badge.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
                    badge.setOpaque(false);
                    badge.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    JLabel badgeLabel = new JLabel(dv.getTenDonVi().getMoTa());
                    badgeLabel.setFont(new Font("Segoe UI", isActive ? Font.BOLD : Font.PLAIN, 11));
                    if (isActive) {
                        badge.setBackground(Color.WHITE);
                        badgeLabel.setForeground(new Color(0, 123, 255));
                    } else {
                        badge.setBackground(new Color(245, 245, 245));
                        badgeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                        badgeLabel.setForeground(new Color(120, 120, 120));
                    }
                    badge.add(badgeLabel);

                    badge.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            e.consume();
                            activeDV[0] = dv;
                            // Cập nhật giao diện card
                            Runnable updateUI = (Runnable) card.getClientProperty("updateUI");
                            if (updateUI != null)
                                updateUI.run();
                            hienThiChiTietSanPham(sp);
                        }
                    });

                    badgePanel.add(badge);
                }

                badgePanel.revalidate();
                badgePanel.repaint();
            }
        };

        // Gắn updateCardUI làm property để có thể gọi lại từ bên trong listener
        card.putClientProperty("updateUI", updateCardUI);
        updateCardUI.run();

        card.add(imgContainer);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblName);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(badgePanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(infoPanel);

        // Click event cho toàn bộ card
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
        txtThue.setText(String.format("%.1f", sp.getThue()));
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

        String barcode = txtBarcodeDonVi.getText().trim();
        if ("Mã vạch".equals(barcode)) {
            barcode = "";
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

        donViQuyDoiModel.addRow(new Object[] { hienThiTenDonVi(dv), soLuong, barcode });
        txtHeSoQuyDoi.setText("");
        txtBarcodeDonVi.setText("Mã vạch");
        txtBarcodeDonVi.setForeground(Color.GRAY);
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

        List<DonViQuyDoi> ds = donViQuyDoiService.timTheoMaSanPham(sp.getMaSanPham());
        if (ds == null || ds.isEmpty()) {
            return;
        }

        for (DonViQuyDoi dv : ds) {
            if (dv != null && dv.getTenDonVi() != null) {
                donViQuyDoiModel.addRow(new Object[] {
                        hienThiTenDonVi(dv.getTenDonVi()),
                        dv.getHeSoQuyDoi(),
                        dv.getBarcode() != null ? dv.getBarcode() : ""
                });
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
            String maMoi = sanPhamService.taoMaSanPhamTuDong(phanLoai, ten);
            if (sanPhamService.tonTaiMaSanPham(maMoi)) {
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
            sp.setThue(parseDoubleOrZero(txtThue.getText()));

            boolean success = sanPhamService.them(sp);
            if (success) {
                luuDonViQuyDoi(maMoi);
                luuAnhSanPham(maMoi);
                JOptionPane.showMessageDialog(this, "Thêm sản phẩm thành công: " + maMoi, "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachSanPham();

                // chọn lại item vừa thêm (nếu có)
                SanPham spMoi = sanPhamService.timTheoMa(maMoi);
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

    /**
     * Load ảnh bất đồng bộ trên background thread.
     * Hiển thị ảnh trên lblImage sau khi load xong (không block EDT).
     * Kết quả được cache trong imageCache để tái sử dụng.
     */
    private void loadProductImageAsync(String maSanPham, int w, int h, JLabel lblImage) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) return;
        // Kiểm tra cache trước — nếu đã load thì hiển thị ngay không cần worker
        ImageIcon cached = imageCache.get(maSanPham + "_" + w + "x" + h);
        if (cached != null) {
            lblImage.setIcon(cached);
            lblImage.setText("");
            return;
        }
        // Nếu đây là sentinel "not found" thì bỏ qua
        if (imageCache.containsKey(maSanPham + "_NONE")) return;

        new javax.swing.SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() {
                return loadProductImageIcon(maSanPham, w, h);
            }
            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        imageCache.put(maSanPham + "_" + w + "x" + h, icon);
                        lblImage.setIcon(icon);
                        lblImage.setText("");
                    } else {
                        // Đánh dấu sentinel để không load lại nữa
                        imageCache.put(maSanPham + "_NONE", null);
                    }
                } catch (Exception ignored) {}
            }
        }.execute();
    }

    /**
     * Load và scale ảnh sản phẩm đồng bộ (chỉ dùng khi cần kết quả ngay, vd: panel thông tin bên phải).
     * Dùng Graphics2D thay cho SCALE_SMOOTH để scale nhanh hơn.
     */
    private ImageIcon loadProductImageIcon(String maSanPham, int w, int h) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return null;
        }
        // Kiểm tra cache trước
        String cacheKey = maSanPham + "_" + w + "x" + h;
        if (imageCache.containsKey(cacheKey)) return imageCache.get(cacheKey);

        String base = maSanPham.trim();
        String[] exts = new String[] { "png", "jpg", "jpeg" };

        for (String ext : exts) {
            String fileName = base + "." + ext;

            // 1) Thử load từ Classpath (Jar/Target)
            String resourcePath = "images/anhSanPham/" + fileName;
            java.net.URL url = getClass().getClassLoader().getResource(resourcePath);
            if (url == null) {
                url = getClass().getResource("/" + resourcePath);
            }

            try {
                BufferedImage bImage = null;
                if (url != null) {
                    bImage = ImageIO.read(url);
                } else {
                    // 2) Fallback: Load trực tiếp từ Filesystem (Dùng cho môi trường dev)
                    java.nio.file.Path[] paths = {
                            java.nio.file.Paths.get("src", "main", "resources", "images", "anhSanPham", fileName),
                            java.nio.file.Paths.get(System.getProperty("user.dir", ""), "src", "main", "resources",
                                    "images", "anhSanPham", fileName),
                            java.nio.file.Paths.get("target", "classes", "images", "anhSanPham", fileName)
                    };

                    for (java.nio.file.Path p : paths) {
                        if (java.nio.file.Files.exists(p)) {
                            bImage = ImageIO.read(p.toFile());
                            break;
                        }
                    }
                }

                if (bImage != null) {
                    // Dùng Graphics2D thay cho SCALE_SMOOTH để tránh block thread
                    BufferedImage scaled = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = scaled.createGraphics();
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.drawImage(bImage, 0, 0, w, h, null);
                    g2d.dispose();
                    ImageIcon icon = new ImageIcon(scaled);
                    imageCache.put(cacheKey, icon);
                    return icon;
                }
            } catch (Exception e) {
                // Tiếp tục thử định dạng khác nếu lỗi
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
            sanPhamDangChon.setThue(parseDoubleOrZero(txtThue.getText()));
            sanPhamDangChon.setMoTa(txtMoTa.getText().trim());
            sanPhamDangChon.setLoaiSanPham(LoaiSanPham.valueOf((String) cbLoaiSP.getSelectedItem()));

            boolean success = sanPhamService.capNhat(sanPhamDangChon);
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
            boolean success = sanPhamService.capNhat(sanPhamDangChon);
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
        txtThue.setText("");
        txtMoTa.setText("");
        cbLoaiSP.setSelectedIndex(0);
        lblImageRight.setIcon(null);
        lblImageRight.setText("Chưa có ảnh");
        txtHeSoQuyDoi.setText("");
        if (txtBarcodeDonVi != null) {
            txtBarcodeDonVi.setText("Mã vạch");
            txtBarcodeDonVi.setForeground(Color.GRAY);
        }
        if (donViQuyDoiModel != null) {
            donViQuyDoiModel.setRowCount(0);
        }
    }

    private void luuDonViQuyDoi(String maSP) {
        SanPham sp = sanPhamService.timTheoMa(maSP);
        if (sp == null)
            return;

        List<DonViQuyDoi> dsHienCo = donViQuyDoiService.timTheoMaSanPham(maSP);
        List<String> dsTenDonViTrenBang = new ArrayList<>();

        for (int i = 0; i < donViQuyDoiModel.getRowCount(); i++) {
            String tenHienThi = donViQuyDoiModel.getValueAt(i, 0).toString();
            int heSo = Integer.parseInt(donViQuyDoiModel.getValueAt(i, 1).toString());
            Object barcodeObj = donViQuyDoiModel.getValueAt(i, 2);
            String barcodeVal = (barcodeObj != null) ? barcodeObj.toString().trim() : "";

            DonVi donViEnum = getDonViTuTenHienThi(tenHienThi);
            dsTenDonViTrenBang.add(donViEnum.name());

            DonViQuyDoi dvCu = null;
            for (DonViQuyDoi dv : dsHienCo) {
                if (dv.getTenDonVi() == donViEnum) {
                    dvCu = dv;
                    break;
                }
            }

            if (dvCu != null) {
                dvCu.setHeSoQuyDoi(heSo);
                dvCu.setBarcode(barcodeVal.isEmpty() ? null : barcodeVal);
                donViQuyDoiService.capNhat(dvCu);
            } else {
                DonViQuyDoi dvMoi = new DonViQuyDoi();
                dvMoi.setMaDonVi(donViQuyDoiService.taoMaDonViTuDong());
                dvMoi.setTenDonVi(donViEnum);
                dvMoi.setHeSoQuyDoi(heSo);
                dvMoi.setSanPham(sp);
                dvMoi.setBarcode(barcodeVal.isEmpty() ? null : barcodeVal);
                donViQuyDoiService.them(dvMoi);
            }
        }

        for (DonViQuyDoi dv : dsHienCo) {
            if (!dsTenDonViTrenBang.contains(dv.getTenDonVi().name())) {
                boolean xoaThanhCong = donViQuyDoiService.xoa(dv.getMaDonVi());
                if (!xoaThanhCong) {
                    throw new RuntimeException("Không thể xóa đơn vị '" + hienThiTenDonVi(dv.getTenDonVi())
                            + "' vì đơn vị này đã phát sinh giao dịch hoặc đang được áp dụng trong chương trình quà tặng!");
                }
            }
        }
    }

    private void luuAnhSanPham(String maSanPham) {
        if (selectedImageFile == null)
            return;
        try {
            File dir1 = new File("src/main/resources/images/anhSanPham");
            if (!dir1.exists())
                dir1.mkdirs();
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

    // ====================== TAB SẢN PHẨM LỖI ======================

    private JPanel createTabBar(CardLayout cardLayout, JPanel cardPanel) {
        JPanel container = new JPanel(new GridBagLayout());
        container.setBackground(new Color(241, 246, 255));
        container.setBorder(new EmptyBorder(10, 15, 0, 15));

        JPanel bar = new JPanel(new GridLayout(1, 2, 5, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 235, 245));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(280, 40));
        bar.setBorder(new EmptyBorder(3, 3, 3, 3));

        JButton btnTabNormal = new JButton("Sản phẩm") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isTabNormalActive) {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(26, 115, 232));
                    g2.fillRect(15, getHeight() - 4, getWidth() - 30, 4);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        JButton btnTabLoi = new JButton("Sản phẩm lỗi") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (!isTabNormalActive) {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(26, 115, 232));
                    g2.fillRect(15, getHeight() - 4, getWidth() - 30, 4);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        for (JButton btn : new JButton[] { btnTabNormal, btnTabLoi }) {
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
            btn.setForeground(new Color(50, 50, 50));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        btnTabNormal.addActionListener(e -> {
            isTabNormalActive = true;
            cardLayout.show(cardPanel, "NORMAL");
            btnTabNormal.repaint();
            btnTabLoi.repaint();
            loadDanhSachSanPham();
        });

        btnTabLoi.addActionListener(e -> {
            isTabNormalActive = false;
            cardLayout.show(cardPanel, "LOI");
            btnTabNormal.repaint();
            btnTabLoi.repaint();
            loadDanhSachLoi();
        });

        bar.add(btnTabNormal);
        bar.add(btnTabLoi);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        container.add(bar, gbc);

        return container;
    }

    private JPanel createLeftPanelLoi() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(241, 246, 255));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Bar
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(new Color(245, 245, 245));

        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBar.setBackground(new Color(245, 245, 245));

        JLabel lblTitle = new JLabel("Danh sách sản phẩm lỗi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(50, 50, 50));
        leftBar.add(lblTitle);

        cbDanhMucLoi = new JComboBox<>(new String[] { "Tất cả", "Thuốc ETC", "Thuốc OTC", "TPCN", "Mỹ phẩm" });
        cbDanhMucLoi.setPreferredSize(new Dimension(140, 35));
        cbDanhMucLoi.addActionListener(e -> locVaHienThiLoi());
        leftBar.add(cbDanhMucLoi);

        cbSortTimeLoi = new JComboBox<>(new String[] { "Thời gian lưu: Mới nhất", "Thời gian lưu: Cũ nhất" });
        cbSortTimeLoi.setPreferredSize(new Dimension(170, 35));
        cbSortTimeLoi.addActionListener(e -> locVaHienThiLoi());
        leftBar.add(cbSortTimeLoi);

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightBar.setBackground(new Color(245, 245, 245));

        txtSearchLoi = new JTextField("Tìm kiếm theo mã HD hoặc mã SP...");
        txtSearchLoi.setForeground(Color.GRAY);
        txtSearchLoi.setPreferredSize(new Dimension(220, 35));

        txtSearchLoi.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Tìm kiếm theo mã HD hoặc mã SP...".equals(txtSearchLoi.getText().trim())) {
                    txtSearchLoi.setText("");
                    txtSearchLoi.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearchLoi.getText().trim().isEmpty()) {
                    txtSearchLoi.setText("Tìm kiếm theo mã HD hoặc mã SP...");
                    txtSearchLoi.setForeground(Color.GRAY);
                }
            }
        });
        txtSearchLoi.addActionListener(e -> locVaHienThiLoi());
        txtSearchLoi.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                locVaHienThiLoi();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                locVaHienThiLoi();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                locVaHienThiLoi();
            }
        });

        JButton btnSearchLoi = new RoundedButton("Tìm");
        btnSearchLoi.setPreferredSize(new Dimension(80, 35));
        btnSearchLoi.setBackground(new Color(0, 123, 255));
        btnSearchLoi.setForeground(Color.WHITE);
        btnSearchLoi.addActionListener(e -> locVaHienThiLoi());

        rightBar.add(txtSearchLoi);
        rightBar.add(btnSearchLoi);

        topBar.add(leftBar, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // Grid Panel
        gridLayoutLoi = new GridLayout(0, 5, 15, 15);
        gridPanelLoi = new JPanel(gridLayoutLoi);
        gridPanelLoi.setBackground(new Color(245, 245, 245));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(245, 245, 245));
        wrapperPanel.add(gridPanelLoi, BorderLayout.NORTH);

        JScrollPane scrollPaneLoi = new JScrollPane(wrapperPanel);
        scrollPaneLoi.setBorder(null);
        scrollPaneLoi.getVerticalScrollBar().setUnitIncrement(16);

        // Responsive grid
        scrollPaneLoi.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int viewportWidth = scrollPaneLoi.getViewport().getWidth();
                if (viewportWidth > 0) {
                    int cols = Math.max(1, Math.min(5, viewportWidth / 195));
                    gridLayoutLoi.setColumns(cols);
                    gridPanelLoi.revalidate();
                }
            }
        });

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPaneLoi, BorderLayout.CENTER);

        return leftPanel;
    }

    private JPanel createRightPanelLoi() {
        RoundedPanel panel = new RoundedPanel(16, true);
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lblTitle = new JLabel("THÔNG TIN SẢN PHẨM LỖI", SwingConstants.CENTER);
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
        lblImageRightLoi = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImageRightLoi.setPreferredSize(new Dimension(150, 100));
        lblImageRightLoi.setBorder(new LineBorder(Color.LIGHT_GRAY));
        lblImageRightLoi.setOpaque(true);
        lblImageRightLoi.setBackground(new Color(240, 240, 240));
        imagePanel.add(lblImageRightLoi, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(imagePanel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        // Form fields
        txtMaHoaDonLoi = new RoundedTextField("", 15);
        txtMaSPLoi = new RoundedTextField("", 15);
        txtTenSPLoi = new RoundedTextField("", 15);
        txtHoatChatLoi = new RoundedTextField("", 15);
        txtSoLuongLoi = new RoundedTextField("", 15);
        txtDonGiaLoi = new RoundedTextField("", 15);
        txtSoLoLoi = new RoundedTextField("", 15);
        txtHSDLoi = new RoundedTextField("", 15);
        txtMoTaLoi = new JTextArea(3, 20);
        txtMoTaLoi.setLineWrap(true);
        txtMoTaLoi.setWrapStyleWord(true);
        txtMoTaLoi.setEditable(false);
        txtMoTaLoi.setBackground(new Color(235, 235, 235));

        cbLoaiSPLoi = new JComboBox<>(new String[] { "ETC", "OTC", "TPCN", "MY_PHAM" });
        cbLoaiSPLoi.setEnabled(false);

        addFormFieldLoi(formPanel, gbc, row++, "Mã đơn hàng:", txtMaHoaDonLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Mã sản phẩm:", txtMaSPLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Tên sản phẩm:", txtTenSPLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Hoạt chất:", txtHoatChatLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Số lượng lỗi:", txtSoLuongLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Đơn giá bán:", txtDonGiaLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Số lô:", txtSoLoLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Hạn sử dụng:", txtHSDLoi);
        addFormFieldLoi(formPanel, gbc, row++, "Phân loại:", cbLoaiSPLoi);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JScrollPane moTaLoiScroll = new JScrollPane(txtMoTaLoi);
        moTaLoiScroll.setPreferredSize(new Dimension(200, 80));
        formPanel.add(moTaLoiScroll, gbc);
        row++;

        JScrollPane formScrollLoi = new JScrollPane(formPanel);
        formScrollLoi.setBorder(null);
        formScrollLoi.getViewport().setBackground(Color.WHITE);
        formScrollLoi.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollLoi.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(formScrollLoi, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 1, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnTraNSX = createStyledButton("Trả về NSX", new Color(40, 167, 69), Color.WHITE);
        btnTraNSX.setPreferredSize(new Dimension(150, 40));
        btnTraNSX.addActionListener(e -> traVeNSX());
        buttonPanel.add(btnTraNSX);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        rightPanelLoi = panel;
        return panel;
    }

    private void addFormFieldLoi(JPanel panel, GridBagConstraints gbc, int row, String labelText,
            JComponent inputComp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        panel.add(new JLabel(labelText), gbc);

        if (inputComp instanceof JTextField) {
            ((JTextField) inputComp).setEditable(false);
            inputComp.setBackground(new Color(235, 235, 235));
        }
        inputComp.setPreferredSize(new Dimension(200, 32));

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(inputComp, gbc);
    }

    private JPanel createProductCardLoi(SuPhanBoLo spb) {
        SanPham sp = spb.getChiTietHoaDon().getDonViQuyDoi().getSanPham();
        DonViQuyDoi dv = spb.getChiTietHoaDon().getDonViQuyDoi();
        int heSo = dv.getHeSoQuyDoi();
        int soLuongLoi = spb.getSoLuongPhanBo() / heSo;
        double donGia = spb.getChiTietHoaDon().getDonGia();

        RoundedPanel card = new RoundedPanel(14, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(225, 225, 225), 1, true),
                new EmptyBorder(12, 10, 12, 10)));
        card.setPreferredSize(new Dimension(180, 270));
        card.setMaximumSize(new Dimension(180, 270));
        card.setMinimumSize(new Dimension(180, 270));

        // 1. Ảnh sản phẩm
        RoundedPanel imgContainer = new RoundedPanel(10, false);
        imgContainer.setLayout(new BorderLayout());
        imgContainer.setBackground(Color.WHITE);
        imgContainer.setBorder(BorderFactory.createLineBorder(new Color(225, 225, 225), 1));
        imgContainer.setPreferredSize(new Dimension(160, 90));
        imgContainer.setMaximumSize(new Dimension(160, 90));
        imgContainer.setMinimumSize(new Dimension(160, 90));
        imgContainer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblImage = new JLabel("", SwingConstants.CENTER);
        ImageIcon icon = loadProductImageIcon(sp.getMaSanPham(), 160, 90);
        if (icon != null) {
            lblImage.setIcon(icon);
        } else {
            lblImage.setIcon(null);
            lblImage.setText("Ảnh SP");
            lblImage.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblImage.setForeground(Color.GRAY);
        }
        imgContainer.add(lblImage, BorderLayout.CENTER);

        // 2. Tên sản phẩm
        JLabel lblName = new JLabel("<html><div style='text-align: center; width: 140px; font-family: Segoe UI;'>"
                + sp.getTenSanPham() + "</div></html>", SwingConstants.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(new Color(33, 37, 41));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setPreferredSize(new Dimension(160, 36));
        lblName.setMaximumSize(new Dimension(160, 36));

        // 3. Badges hiển thị đơn vị quy đổi
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        badgePanel.setBackground(Color.WHITE);
        badgePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        badgePanel.setPreferredSize(new Dimension(160, 26));
        badgePanel.setMaximumSize(new Dimension(160, 26));

        RoundedPanel badge = new RoundedPanel(8, false);
        badge.setLayout(new FlowLayout(FlowLayout.CENTER, 6, 2));
        badge.setBackground(new Color(245, 245, 245));
        badge.setOpaque(false);
        JLabel badgeLabel = new JLabel(hienThiTenDonVi(dv.getTenDonVi()));
        badgeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        badgeLabel.setForeground(new Color(120, 120, 120));
        badge.add(badgeLabel);
        badgePanel.add(badge);

        // 4. Panel chứa thông tin (Giá, Số lượng tồn)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.setPreferredSize(new Dimension(160, 60));
        infoPanel.setMaximumSize(new Dimension(160, 60));

        JLabel lblPrice = new JLabel(String.format("Giá: %,.0fđ / %s", donGia, hienThiTenDonVi(dv.getTenDonVi())),
                SwingConstants.LEFT);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrice.setForeground(new Color(220, 53, 69));
        lblPrice.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStock = new JLabel("Số lượng lỗi: " + soLuongLoi + " " + hienThiTenDonVi(dv.getTenDonVi()),
                SwingConstants.LEFT);
        lblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStock.setForeground(new Color(100, 100, 100));
        lblStock.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(lblPrice);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(lblStock);

        card.add(imgContainer);
        card.add(Box.createRigidArea(new Dimension(0, 8)));
        card.add(lblName);
        card.add(Box.createRigidArea(new Dimension(0, 6)));
        card.add(badgePanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(infoPanel);

        // Click event cho toàn bộ card
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hienThiChiTietLoi(spb);
            }
        });

        return card;
    }

    private void hienThiChiTietLoi(SuPhanBoLo spb) {
        this.hangLoiDangChon = spb;
        SanPham sp = spb.getChiTietHoaDon().getDonViQuyDoi().getSanPham();
        DonViQuyDoi dv = spb.getChiTietHoaDon().getDonViQuyDoi();
        int heSo = dv.getHeSoQuyDoi();

        txtMaHoaDonLoi.setText(spb.getChiTietHoaDon().getHoaDon().getMaHoaDon());
        txtMaSPLoi.setText(sp.getMaSanPham());
        txtTenSPLoi.setText(sp.getTenSanPham());
        txtHoatChatLoi.setText(sp.getHoatChat() != null ? sp.getHoatChat() : "");
        txtSoLuongLoi.setText(String.valueOf(spb.getSoLuongPhanBo() / heSo) + " " + hienThiTenDonVi(dv.getTenDonVi()));
        txtDonGiaLoi.setText(String.format("%,.0f đ", spb.getChiTietHoaDon().getDonGia()));
        txtSoLoLoi.setText(spb.getLo().getSoLo() != null ? spb.getLo().getSoLo() : "");

        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        txtHSDLoi.setText(spb.getLo().getNgayHetHan() != null ? spb.getLo().getNgayHetHan().format(dtf) : "");

        txtMoTaLoi.setText(sp.getMoTa() != null ? sp.getMoTa() : "");
        cbLoaiSPLoi.setSelectedItem(sp.getLoaiSanPham().name());

        ImageIcon icon = loadProductImageIcon(sp.getMaSanPham(), 150, 100);
        if (icon != null) {
            lblImageRightLoi.setIcon(icon);
            lblImageRightLoi.setText("");
        } else {
            lblImageRightLoi.setIcon(null);
            lblImageRightLoi.setText("Không có ảnh");
        }
    }

    private void lamMoiFormLoi() {
        this.hangLoiDangChon = null;
        txtMaHoaDonLoi.setText("");
        txtMaSPLoi.setText("");
        txtTenSPLoi.setText("");
        txtHoatChatLoi.setText("");
        txtSoLuongLoi.setText("");
        txtDonGiaLoi.setText("");
        txtSoLoLoi.setText("");
        txtHSDLoi.setText("");
        txtMoTaLoi.setText("");
        cbLoaiSPLoi.setSelectedIndex(0);
        lblImageRightLoi.setIcon(null);
        lblImageRightLoi.setText("Chưa có ảnh");
    }

    public void loadDanhSachLoi() {
        danhSachLoi = suPhanBoLoService.layDanhSachLoi();
        locVaHienThiLoi();
    }

    private void locVaHienThiLoi() {
        String keyword = txtSearchLoi.getText().trim().toLowerCase();
        if ("tìm kiếm theo mã hd hoặc mã sp...".equals(keyword)) {
            keyword = "";
        }

        String targetCategory = (String) cbDanhMucLoi.getSelectedItem();
        boolean sortDesc = cbSortTimeLoi.getSelectedIndex() == 0;

        List<SuPhanBoLo> results = new ArrayList<>();
        for (SuPhanBoLo spb : danhSachLoi) {
            SanPham sp = spb.getChiTietHoaDon().getDonViQuyDoi().getSanPham();

            String maHoaDon = spb.getChiTietHoaDon().getHoaDon().getMaHoaDon().toLowerCase();
            String maSanPham = sp.getMaSanPham().toLowerCase();
            if (!keyword.isEmpty() && !maHoaDon.contains(keyword) && !maSanPham.contains(keyword)) {
                continue;
            }

            String cat = sp.getLoaiSanPham().name();
            if (!"Tất cả".equals(targetCategory)) {
                if ("Thuốc ETC".equals(targetCategory) && !"ETC".equals(cat))
                    continue;
                if ("Thuốc OTC".equals(targetCategory) && !"OTC".equals(cat))
                    continue;
                if ("TPCN".equals(targetCategory) && !"TPCN".equals(cat))
                    continue;
                if ("Mỹ phẩm".equals(targetCategory) && !"MY_PHAM".equals(cat))
                    continue;
            }

            results.add(spb);
        }

        results.sort((s1, s2) -> {
            LocalDateTime t1 = s1.getChiTietHoaDon().getHoaDon().getThoiGianTao();
            LocalDateTime t2 = s2.getChiTietHoaDon().getHoaDon().getThoiGianTao();
            if (t1 == null)
                return sortDesc ? 1 : -1;
            if (t2 == null)
                return sortDesc ? -1 : 1;
            return sortDesc ? t2.compareTo(t1) : t1.compareTo(t2);
        });

        hienThiLoiLenGrid(results);
    }

    private void traVeNSX() {
        if (hangLoiDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm lỗi cần trả về NSX!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double giaNhapLoo = hangLoiDangChon.getLo().getGiaNhap();
        int slBanDau = new com.example.dao.LoDAO().tinhSoLuongNhapBanDau(hangLoiDangChon.getLo().getMaLo());
        double giaNhapDonVi = slBanDau > 0 ? (giaNhapLoo / slBanDau) : 0;

        int heSo = hangLoiDangChon.getChiTietHoaDon().getDonViQuyDoi().getHeSoQuyDoi();
        int qtySelectedUnit = hangLoiDangChon.getSoLuongPhanBo() / heSo;
        double tongTienHoan = hangLoiDangChon.getSoLuongPhanBo() * giaNhapDonVi;

        String msg = String.format("Bạn có chắc chắn muốn trả sản phẩm lỗi này về Nhà sản xuất không?\n" +
                "Sản phẩm: %s\n" +
                "Đơn vị: %s\n" +
                "Số lượng trả: %d\n" +
                "Đơn giá nhập: %,.0fđ\n" +
                "Tổng tiền hoàn ước tính: %,.0fđ",
                hangLoiDangChon.getChiTietHoaDon().getDonViQuyDoi().getSanPham().getTenSanPham(),
                hienThiTenDonVi(hangLoiDangChon.getChiTietHoaDon().getDonViQuyDoi().getTenDonVi()),
                qtySelectedUnit,
                giaNhapDonVi * heSo,
                tongTienHoan);

        int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác nhận trả về NSX", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = suPhanBoLoService.giaiQuyetHangLoi(
                    hangLoiDangChon.getChiTietHoaDon().getHoaDon().getMaHoaDon(),
                    hangLoiDangChon.getChiTietHoaDon().getDonViQuyDoi().getMaDonVi(),
                    hangLoiDangChon.getLo().getMaLo(),
                    hangLoiDangChon.getChiTietHoaDon().isLaQuaTangKem());

            if (success) {
                CaLam caMo = caLamService.layCaDangMoBatKy();
                String bonusMsg = "";
                if (caMo != null) {
                    caMo.setTienKetCa(caMo.getTienKetCa() + tongTienHoan);
                    caLamService.capNhat(caMo);
                    bonusMsg = String.format("\nSố tiền quỹ ca làm việc được hoàn trả: %,.0fđ.", tongTienHoan);
                } else {
                    bonusMsg = String.format("\nKhông tìm thấy ca làm việc đang mở. Tiền hoàn trả từ NSX: %,.0fđ.",
                            tongTienHoan);
                }

                JOptionPane.showMessageDialog(this,
                        "Đã hoàn tất trả sản phẩm lỗi về nhà sản xuất." + bonusMsg,
                        "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachLoi();
                lamMoiFormLoi();
            } else {
                JOptionPane.showMessageDialog(this, "Xảy ra lỗi khi thực hiện giao dịch hoàn trả!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void hienThiLoiLenGrid(List<SuPhanBoLo> list) {
        gridPanelLoi.removeAll();
        for (SuPhanBoLo spb : list) {
            JPanel card = createProductCardLoi(spb);
            gridPanelLoi.add(card);
        }
        gridPanelLoi.revalidate();
        gridPanelLoi.repaint();
    }
}