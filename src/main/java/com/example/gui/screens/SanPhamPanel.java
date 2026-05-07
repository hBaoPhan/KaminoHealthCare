package com.example.gui.screens;

import com.example.dao.SanPhamDAO;
import com.example.entity.SanPham;
import com.example.entity.enums.LoaiSanPham;
import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class SanPhamPanel extends JPanel {

    private SanPhamDAO sanPhamDAO = new SanPhamDAO();
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
    private SanPham sanPhamDangChon = null;

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
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Tìm kiếm theo mã hoặc tên...".equals(txtSearch.getText().trim())) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Tìm kiếm theo mã hoặc tên...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });
        txtSearch.addActionListener(e -> locVaHienThiSanPham());

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

        JScrollPane scrollPane = new JScrollPane(gridPanel);
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
        btnSelectImage.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Ảnh được lấy tự động theo mã sản phẩm.\n"
                        + "Bạn có thể thay ảnh bằng cách thay file tại:\n"
                        + "src/main/resources/images/anhSanPham/{maSanPham}.png",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE));

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

    private void loadDanhSachSanPham() {
        danhSachSanPham = sanPhamDAO.layTatCa();
        hienThiSanPhamLenGrid(danhSachSanPham);
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
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);
        ImageIcon icon = loadProductImageIcon(sp.getMaSanPham(), 160, 80);
        if (icon != null) {
            lblImage.setIcon(icon);
        } else {
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
            String classpathPath = "/images/anhSanPham/" + base + "." + ext;
            java.net.URL url = getClass().getResource(classpathPath);

            ImageIcon raw;
            if (url != null) {
                raw = new ImageIcon(url);
            } else {
                // Fallback khi chạy trực tiếp từ source (resources chưa lên classpath)
                raw = new ImageIcon("src/main/resources" + classpathPath);
            }

            if (raw.getIconWidth() > 0 && raw.getIconHeight() > 0) {
                Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
                return new ImageIcon(scaled);
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
            sanPhamDangChon.setSoLuongTon(parseIntOrZero(txtSoLuong.getText()));
            sanPhamDangChon.setDonGiaCoBan(parseDoubleOrZero(txtDonGia.getText()));
            sanPhamDangChon.setMoTa(txtMoTa.getText().trim());
            sanPhamDangChon.setLoaiSanPham(LoaiSanPham.valueOf((String) cbLoaiSP.getSelectedItem()));

            boolean success = sanPhamDAO.capNhat(sanPhamDangChon);
            if (success) {
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
            boolean success = sanPhamDAO.xoa(sanPhamDangChon.getMaSanPham());
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
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtHoatChat.setText("");
        txtSoLuong.setText("");
        txtDonGia.setText("");
        txtMoTa.setText("");
        cbLoaiSP.setSelectedIndex(0);
        lblImageRight.setIcon(null);
        lblImageRight.setText("Chưa có ảnh");
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