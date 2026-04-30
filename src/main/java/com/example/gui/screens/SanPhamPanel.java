package com.example.gui.screens;

import com.example.dao.SanPhamDAO;
import com.example.entity.SanPham;
import com.example.entity.enums.PhanLoai;
import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * SanPhamPanel - Quản lý sản phẩm
 * Đã hoàn thiện: Mã SP tự sinh + Chọn ảnh cũ / ảnh mới
 */
public class SanPhamPanel extends JPanel {

    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private List<SanPham> danhSachHienTai;

    // Components bên trái
    private JPanel gridPanel;
    private GridLayout gridLayout;
    private JTextField txtSearch;
    private JComboBox<String> cbDanhMuc;

    // Components bên phải
    private JLabel lblImageRight;
    private JTextField txtMaSP, txtTenSP, txtHoatChat, txtSoLuong, txtDonGia;
    private JComboBox<String> cbLoaiSP;
    private JTextArea txtMoTa;

    private SanPham sanPhamDangChon = null;
    private String duongDanAnhTam = null;   // Ảnh mới người dùng chọn từ máy
    private String tenAnhDaChon = null;     // Tên file ảnh cũ đã chọn từ thư viện

    public SanPhamPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);

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
        leftBar.add(new JLabel("Danh sách sản phẩm"));

        cbDanhMuc = new JComboBox<>(new String[]{"Tất cả", "Thuốc ETC", "Thuốc OTC", "TPCN"});
        cbDanhMuc.setPreferredSize(new Dimension(160, 35));
        cbDanhMuc.addActionListener(e -> timKiemVaHienThi());
        leftBar.add(cbDanhMuc);

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightBar.setBackground(new Color(245, 245, 245));

        txtSearch = new JTextField("Tìm kiếm theo mã hoặc tên...", 25);
        txtSearch.setForeground(Color.GRAY);
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
        txtSearch.addActionListener(e -> timKiemVaHienThi());

        JButton btnSearch = new RoundedButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(80, 35));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> timKiemVaHienThi());

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

        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int cols = Math.max(1, Math.min(5, scrollPane.getViewport().getWidth() / 195));
                gridLayout.setColumns(cols);
                gridPanel.revalidate();
            }
        });

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);

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
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // ==================== PHẦN ẢNH ====================
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setBackground(Color.WHITE);

        lblImageRight = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblImageRight.setPreferredSize(new Dimension(160, 120));
        lblImageRight.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        lblImageRight.setOpaque(true);
        lblImageRight.setBackground(new Color(240, 240, 240));

        JPanel btnAnhPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        btnAnhPanel.setBackground(Color.WHITE);

        JButton btnAnhCu = new RoundedButton("Ảnh cũ");
        JButton btnAnhMoi = new RoundedButton("Ảnh mới");

        btnAnhCu.addActionListener(e -> moDialogChonAnhCu());
        btnAnhMoi.addActionListener(e -> chonAnhMoi());

        btnAnhPanel.add(btnAnhCu);
        btnAnhPanel.add(btnAnhMoi);

        imagePanel.add(lblImageRight, BorderLayout.CENTER);
        imagePanel.add(btnAnhPanel, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        formPanel.add(imagePanel, gbc);
        gbc.gridwidth = 1;

        // Các trường nhập liệu
        txtMaSP = new RoundedTextField("", 15);
        txtMaSP.setEditable(false);

        txtTenSP = new RoundedTextField("", 15);
        txtTenSP.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                sinhMaSanPhamTuDong();
            }
        });

        txtHoatChat = new RoundedTextField("", 15);
        txtSoLuong = new RoundedTextField("0", 15);
        txtDonGia = new RoundedTextField("0", 15);

        cbLoaiSP = new JComboBox<>(new String[]{"ETC", "OTC", "TPCN"});
        cbLoaiSP.addActionListener(e -> sinhMaSanPhamTuDong());

        txtMoTa = new JTextArea(4, 25);
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);

        addFormField(formPanel, gbc, row++, "Mã SP:", txtMaSP, false);
        addFormField(formPanel, gbc, row++, "Tên sản phẩm:", txtTenSP, true);
        addFormField(formPanel, gbc, row++, "Hoạt chất:", txtHoatChat, true);
        addFormField(formPanel, gbc, row++, "Số lượng tồn:", txtSoLuong, true);
        addFormField(formPanel, gbc, row++, "Đơn giá:", txtDonGia, true);
        addFormField(formPanel, gbc, row++, "Loại:", cbLoaiSP, true);

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        formPanel.add(new JScrollPane(txtMoTa), gbc);

        panel.add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(12, 10, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnThem = createStyledButton("Thêm", new Color(0, 204, 204), Color.WHITE);
        JButton btnSua = createStyledButton("Sửa", new Color(255, 193, 7), Color.BLACK);
        JButton btnXoa = createStyledButton("Xóa", new Color(220, 53, 69), Color.WHITE);
        JButton btnLamMoi = createStyledButton("Làm mới", new Color(23, 162, 184), Color.WHITE);

        btnThem.addActionListener(e -> themSanPham());
        btnSua.addActionListener(e -> suaSanPham());
        btnXoa.addActionListener(e -> xoaSanPham());
        btnLamMoi.addActionListener(e -> lamMoiForm());

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp, boolean editable) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel(label), gbc);

        if (comp instanceof JTextField) {
            ((JTextField) comp).setEditable(editable);
            if (!editable) comp.setBackground(new Color(235, 235, 235));
        }
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(comp, gbc);
    }

    // ====================== SINH MÃ SẢN PHẨM TỰ ĐỘNG ======================
    private void sinhMaSanPhamTuDong() {
        String tenSP = txtTenSP.getText().trim();
        String loaiStr = (String) cbLoaiSP.getSelectedItem();
        if (tenSP.isEmpty() || loaiStr == null) {
            txtMaSP.setText("");
            return;
        }

        PhanLoai phanLoai = PhanLoai.valueOf(loaiStr);
        String vietTat = generateVietTat(tenSP);
        String prefix = phanLoai.name() + "-" + vietTat + "-";

        int maxNum = 0;
        for (SanPham sp : danhSachHienTai) {
            if (sp.getMaSanPham().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(sp.getMaSanPham().substring(prefix.length()));
                    maxNum = Math.max(maxNum, num);
                } catch (Exception ignored) {}
            }
        }

        String maMoi = prefix + String.format("%03d", maxNum + 1);
        txtMaSP.setText(maMoi);
    }

    private String generateVietTat(String tenSanPham) {
        String[] words = tenSanPham.toUpperCase().trim().split("\\s+");
        StringBuilder sb = new StringBuilder();

        if (words.length == 1) {
            String w = words[0];
            sb.append(w.length() >= 3 ? w.substring(0, 3) : w);
        } else {
            for (int i = 0; i < Math.min(3, words.length); i++) {
                if (!words[i].isEmpty()) {
                    sb.append(words[i].charAt(0));
                }
            }
        }
        return sb.toString();
    }

    // ====================== XỬ LÝ ẢNH ======================
    private void chonAnhMoi() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "png", "jpg", "jpeg"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            duongDanAnhTam = chooser.getSelectedFile().getAbsolutePath();
            tenAnhDaChon = null;
            hienThiPreviewAnh(duongDanAnhTam);
        }
    }

    private void moDialogChonAnhCu() {
        File thuMuc = new File("src/main/resources/images/anhSanPham");
        if (!thuMuc.exists() || thuMuc.listFiles() == null) {
            JOptionPane.showMessageDialog(this, "Thư mục ảnh chưa tồn tại hoặc chưa có ảnh nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        File[] files = thuMuc.listFiles((dir, name) -> name.toLowerCase().matches(".*\\.(png|jpg|jpeg)$"));

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Chọn ảnh cũ");
        dialog.setSize(720, 520);
        dialog.setLocationRelativeTo(this);

        JPanel grid = new JPanel(new GridLayout(0, 4, 12, 12));
        grid.setBorder(new EmptyBorder(15, 15, 15, 15));
        grid.setBackground(Color.WHITE);

        for (File f : files) {
            JPanel card = taoCardAnh(f);
            grid.add(card);
        }

        JScrollPane scroll = new JScrollPane(grid);
        dialog.add(scroll, BorderLayout.CENTER);

        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel();
        bottom.add(btnDong);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel taoCardAnh(File file) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JLabel lblAnh = new JLabel();
        lblAnh.setPreferredSize(new Dimension(140, 110));
        lblAnh.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnh.setOpaque(true);
        lblAnh.setBackground(new Color(245, 245, 245));

        try {
            ImageIcon icon = new ImageIcon(file.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(140, 110, Image.SCALE_SMOOTH);
            lblAnh.setIcon(new ImageIcon(img));
        } catch (Exception ignored) {
            lblAnh.setText("No Image");
        }

        JLabel lblTen = new JLabel(file.getName(), SwingConstants.CENTER);
        lblTen.setFont(new Font("Segoe UI", Font.PLAIN, 10));

        panel.add(lblAnh, BorderLayout.CENTER);
        panel.add(lblTen, BorderLayout.SOUTH);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tenAnhDaChon = file.getName();
                duongDanAnhTam = null;
                hienThiPreviewAnh(file.getAbsolutePath());

                Window w = SwingUtilities.getWindowAncestor(panel);
                if (w != null) w.dispose();
            }
        });

        return panel;
    }

    private void hienThiPreviewAnh(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            Image scaled = icon.getImage().getScaledInstance(160, 120, Image.SCALE_SMOOTH);
            lblImageRight.setIcon(new ImageIcon(scaled));
            lblImageRight.setText("");
        } catch (Exception e) {
            lblImageRight.setIcon(null);
            lblImageRight.setText("Không đọc được ảnh");
        }
    }

    // ====================== CRUD ======================
    private void themSanPham() {
        try {
            String maSP = txtMaSP.getText().trim();
            if (maSP.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên sản phẩm để sinh mã!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (sanPhamDAO.tonTaiMaSanPham(maSP)) {
                JOptionPane.showMessageDialog(this, "Mã sản phẩm đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SanPham sp = new SanPham();
            sp.setMaSanPham(maSP);
            sp.setTenSanPham(txtTenSP.getText().trim());
            sp.setPhanLoai(PhanLoai.valueOf((String) cbLoaiSP.getSelectedItem()));
            sp.setSoLuongTon(Integer.parseInt(txtSoLuong.getText().trim()));
            sp.setDonGiaCoBan(Double.parseDouble(txtDonGia.getText().trim().replace(",", "").replace(".", "")));
            sp.setHoatChat(txtHoatChat.getText().trim());
            sp.setMoTa(txtMoTa.getText().trim());
            sp.setTrangThaiKinhDoanh(true);
            sp.setThue(0.1);

            boolean success = sanPhamDAO.them(sp);

            if (success) {
                boolean luuAnhOK = luuAnhKhiThem(maSP);
                String msg = luuAnhOK ? "Thêm sản phẩm thành công!" : "Thêm sản phẩm thành công nhưng lưu ảnh thất bại!";
                JOptionPane.showMessageDialog(this, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);

                lamMoiForm();
                loadDanhSachSanPham();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean luuAnhKhiThem(String maSP) {
        String tenFileMoi = maSP + ".png";
        try {
            File destDir = new File("src/main/resources/images/anhSanPham");
            if (!destDir.exists()) destDir.mkdirs();

            if (duongDanAnhTam != null) {
                File source = new File(duongDanAnhTam);
                Files.copy(source.toPath(), new File(destDir, tenFileMoi).toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } else if (tenAnhDaChon != null) {
                File source = new File("src/main/resources/images/anhSanPham/" + tenAnhDaChon);
                Files.copy(source.toPath(), new File(destDir, tenFileMoi).toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void suaSanPham() {
        if (sanPhamDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để sửa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            sanPhamDangChon.setTenSanPham(txtTenSP.getText().trim());
            sanPhamDangChon.setHoatChat(txtHoatChat.getText().trim());
            sanPhamDangChon.setSoLuongTon(Integer.parseInt(txtSoLuong.getText().trim()));
            sanPhamDangChon.setDonGiaCoBan(Double.parseDouble(txtDonGia.getText().trim().replace(",", "").replace(".", "")));
            sanPhamDangChon.setMoTa(txtMoTa.getText().trim());
            sanPhamDangChon.setPhanLoai(PhanLoai.valueOf((String) cbLoaiSP.getSelectedItem()));

            boolean success = sanPhamDAO.capNhat(sanPhamDangChon);

            if (success) {
                // Xử lý ảnh khi sửa (nếu có thay đổi)
                if (duongDanAnhTam != null || tenAnhDaChon != null) {
                    luuAnhKhiThem(sanPhamDangChon.getMaSanPham()); // ghi đè ảnh cũ
                }
                JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachSanPham();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaSanPham() {
        if (sanPhamDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận xóa sản phẩm: " + sanPhamDangChon.getTenSanPham() + "?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (sanPhamDAO.xoa(sanPhamDangChon.getMaSanPham())) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadDanhSachSanPham();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void lamMoiForm() {
        sanPhamDangChon = null;
        duongDanAnhTam = null;
        tenAnhDaChon = null;

        txtMaSP.setText("");
        txtTenSP.setText("");
        txtHoatChat.setText("");
        txtSoLuong.setText("0");
        txtDonGia.setText("0");
        txtMoTa.setText("");
        lblImageRight.setIcon(null);
        lblImageRight.setText("Chưa có ảnh");
    }

    // Các hàm load, tìm kiếm, hiển thị grid... (giữ nguyên từ phiên bản trước)
    private void loadDanhSachSanPham() {
        danhSachHienTai = sanPhamDAO.layTatCa();
        hienThiSanPhamLenGrid(danhSachHienTai);
    }

    private void timKiemVaHienThi() {
        String tuKhoa = txtSearch.getText().trim();
        if ("Tìm kiếm theo mã hoặc tên...".equals(tuKhoa)) tuKhoa = "";

        String danhMuc = (String) cbDanhMuc.getSelectedItem();
        PhanLoai phanLoai = null;
        if ("Thuốc ETC".equals(danhMuc)) phanLoai = PhanLoai.ETC;
        else if ("Thuốc OTC".equals(danhMuc)) phanLoai = PhanLoai.OTC;
        else if ("TPCN".equals(danhMuc)) phanLoai = PhanLoai.TPCN;

        danhSachHienTai = sanPhamDAO.timKiemNangCao(tuKhoa, phanLoai);
        hienThiSanPhamLenGrid(danhSachHienTai);
    }

    private void hienThiSanPhamLenGrid(List<SanPham> list) {
        gridPanel.removeAll();
        for (SanPham sp : list) {
            gridPanel.add(createProductCard(sp));
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createProductCard(SanPham sp) {
        // ... (giữ nguyên code tạo card như trước)
        // Tôi rút gọn để tránh dài dòng, bạn có thể copy từ phiên bản cũ
        RoundedPanel card = new RoundedPanel(14, true);
        // ... triển khai card tương tự phiên bản trước
        card.addMouseListener(new MouseAdapter() {
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
        cbLoaiSP.setSelectedItem(sp.getPhanLoai().name());

        // Load ảnh
        String imagePath = "src/main/resources/images/anhSanPham/" + sp.getMaSanPham() + ".png";
        File f = new File(imagePath);
        if (f.exists()) {
            hienThiPreviewAnh(imagePath);
        } else {
            lblImageRight.setIcon(null);
            lblImageRight.setText("Không có ảnh");
        }
    }

    private RoundedButton createStyledButton(String text, Color bg, Color fg) {
        RoundedButton btn = new RoundedButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }
}