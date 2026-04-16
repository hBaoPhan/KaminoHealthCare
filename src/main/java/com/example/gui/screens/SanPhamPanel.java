package com.example.gui.screens;
import com.example.gui.components.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SanPhamPanel extends JPanel {
    public SanPhamPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255)); // #F1F6FF

        // Sử dụng JSplitPane để chia đôi màn hình (Trái: Danh sách, Phải: Chi tiết)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, createLeftPanel(), createRightPanel());
        splitPane.setResizeWeight(0.65); // Tỉ lệ 65% cho bên trái, 35% cho bên phải
        splitPane.setBorder(null);
        splitPane.setContinuousLayout(true);

        add(splitPane, BorderLayout.CENTER);
    }

    // ==========================================
    // PANEL BÊN TRÁI (Danh sách sản phẩm)
    // ==========================================
    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBackground(new Color(241, 246, 255)); // #F1F6FF
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

<<<<<<< HEAD:src/main/java/com/example/gui/SanPhamPanel.java
        // 1. Top Bar (Danh sách sản phẩm bên trái)
        JPanel topBar = new JPanel(new BorderLayout(15, 0));
        topBar.setBackground(new Color(245, 245, 245));

        JPanel leftBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftBar.setBackground(new Color(245, 245, 245));
=======
        // 1. Top Bar (Tìm kiếm & Danh mục)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topBar.setBackground(new Color(241, 246, 255)); // #F1F6FF

        RoundedTextField txtSearch = new RoundedTextField("Tìm theo tên", 20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
>>>>>>> 206c2038d2eb152ffad3673f6e34d896dcdcddff:src/main/java/com/example/gui/screens/SanPhamPanel.java

        JLabel lblTitle = new JLabel("Danh sách sản phẩm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitle.setForeground(new Color(50, 50, 50));
        leftBar.add(lblTitle);

        JComboBox<String> cbDanhMuc = new JComboBox<>(new String[]{"Tất cả","Thuốc ETC", "Thuốc OTC", "TPCN"});
        cbDanhMuc.setPreferredSize(new Dimension(150, 35));
        cbDanhMuc.setBackground(Color.WHITE);
        cbDanhMuc.addActionListener(e -> JOptionPane.showMessageDialog(
                SanPhamPanel.this,
                "Danh mục đã chọn: " + cbDanhMuc.getSelectedItem(),
                "Lọc theo danh mục",
                JOptionPane.INFORMATION_MESSAGE
        ));
        leftBar.add(cbDanhMuc);

        JComboBox<String> cbSort = new JComboBox<>(new String[]{"Sắp xếp: Mặc định", "Giá tăng dần", "Giá giảm dần"});
        cbSort.setPreferredSize(new Dimension(170, 35));
        cbSort.setBackground(Color.WHITE);
        cbSort.addActionListener(e -> JOptionPane.showMessageDialog(
                SanPhamPanel.this,
                "Sắp xếp: " + cbSort.getSelectedItem(),
                "Sắp xếp theo giá",
                JOptionPane.INFORMATION_MESSAGE
        ));
        leftBar.add(cbSort);

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightBar.setBackground(new Color(245, 245, 245));

        JTextField txtSearch = new JTextField("Tìm kiếm theo mã...");
        txtSearch.setForeground(Color.GRAY);
        txtSearch.setPreferredSize(new Dimension(220, 35));
        txtSearch.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Tìm kiếm theo mã...".equals(txtSearch.getText().trim())) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtSearch.getText().trim().isEmpty()) {
                    txtSearch.setText("Tìm kiếm theo mã...");
                    txtSearch.setForeground(Color.GRAY);
                }
            }
        });

        JButton btnSearch = new RoundedButton("Tìm");
        btnSearch.setPreferredSize(new Dimension(80, 35));
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> JOptionPane.showMessageDialog(
                SanPhamPanel.this,
                "Tìm kiếm theo mã: " + txtSearch.getText().trim(),
                "Tìm kiếm",
                JOptionPane.INFORMATION_MESSAGE
        ));

        rightBar.add(txtSearch);
        rightBar.add(btnSearch);

        topBar.add(leftBar, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // 2. Grid Sản phẩm
<<<<<<< HEAD:src/main/java/com/example/gui/SanPhamPanel.java
        GridLayout gridLayout = new GridLayout(0, 5, 15, 15);
        JPanel gridPanel = new JPanel(gridLayout);
        gridPanel.setBackground(new Color(245, 245, 245));
=======
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 cột, khoảng cách 15px
        gridPanel.setBackground(new Color(241, 246, 255)); // #F1F6FF
>>>>>>> 206c2038d2eb152ffad3673f6e34d896dcdcddff:src/main/java/com/example/gui/screens/SanPhamPanel.java

        // Thêm 50 card sản phẩm mẫu
        gridPanel.add(createProductCard("Hoạt Huyết Trường Phúc", "99.000đ / Hộp", true));
        gridPanel.add(createProductCard("Freezefast Ghiaccio Spray", "210.000đ / Chai", false));
        gridPanel.add(createProductCard("Natto Gold 3000FU", "295.000đ / Hộp", false));
        gridPanel.add(createProductCard("SVR Sebiaclear Moussant", "229.400đ / Hộp", true));
        gridPanel.add(createProductCard("Hoạt Huyết Trường Phúc", "4.800đ / Viên", true));
        gridPanel.add(createProductCard("Giloba 40mg MEGA", "38.333đ / Vỉ", true));
        gridPanel.add(createProductCard("Tanakan", "120.000đ / Hộp", true));
        gridPanel.add(createProductCard("Panadol", "35.000đ / Vỉ", true));
        gridPanel.add(createProductCard("Oresol", "22.000đ / Gói", true));
        gridPanel.add(createProductCard("Hydrocortison", "150.000đ / Tuýp", true));
        gridPanel.add(createProductCard("Vitamin C", "89.000đ / Hộp", true));
        gridPanel.add(createProductCard("Zincovit", "130.000đ / Hộp", true));
        gridPanel.add(createProductCard("Probiotics", "175.000đ / Hộp", false));
        gridPanel.add(createProductCard("Efferalgan", "60.000đ / Hộp", true));
        gridPanel.add(createProductCard("Naproxen", "90.000đ / Hộp", false));
        gridPanel.add(createProductCard("Cetirizine", "55.000đ / Hộp", true));
        gridPanel.add(createProductCard("Dạ dày", "120.000đ / Hộp", true));
        gridPanel.add(createProductCard("Blackmores", "250.000đ / Hộp", true));
        gridPanel.add(createProductCard("Aspirin", "45.000đ / Vỉ", true));
        gridPanel.add(createProductCard("BioGaia", "165.000đ / Hộp", false));
        gridPanel.add(createProductCard("Cefuroxime", "128.000đ / Hộp", true));
        gridPanel.add(createProductCard("Paracetamol", "28.000đ / Vỉ", true));
        gridPanel.add(createProductCard("Pantoprazole", "140.000đ / Hộp", false));
        gridPanel.add(createProductCard("Biotin", "175.000đ / Hộp", true));
        gridPanel.add(createProductCard("Magie", "95.000đ / Hộp", true));
        gridPanel.add(createProductCard("Glucosamine", "230.000đ / Hộp", true));
        gridPanel.add(createProductCard("Collagen", "310.000đ / Hộp", true));
        gridPanel.add(createProductCard("Omega 3", "220.000đ / Hộp", true));
        gridPanel.add(createProductCard("Astaxanthin", "320.000đ / Hộp", false));
        gridPanel.add(createProductCard("Multivitamin", "180.000đ / Hộp", true));
        gridPanel.add(createProductCard("Bleach", "52.000đ / Hộp", true));
        gridPanel.add(createProductCard("Fluoxetine", "210.000đ / Hộp", false));
        gridPanel.add(createProductCard("Vitamin D", "95.000đ / Hộp", true));
        gridPanel.add(createProductCard("Calcium", "138.000đ / Hộp", true));
        gridPanel.add(createProductCard("CoQ10", "285.000đ / Hộp", true));
        gridPanel.add(createProductCard("Aloe Vera", "78.000đ / Hộp", true));
        gridPanel.add(createProductCard("Melatonin", "125.000đ / Hộp", false));
        gridPanel.add(createProductCard("Red Clover", "145.000đ / Hộp", true));
        gridPanel.add(createProductCard("Cinnamon", "90.000đ / Hộp", true));
        gridPanel.add(createProductCard("Collagen", "200.000đ / Hộp", true));
        gridPanel.add(createProductCard("Lutein", "240.000đ / Hộp", true));
        gridPanel.add(createProductCard("Sữa ong chúa", "340.000đ / Hộp", true));
        gridPanel.add(createProductCard("Glutathione", "280.000đ / Hộp", false));
        gridPanel.add(createProductCard("Hyaluronic", "260.000đ / Hộp", true));
        gridPanel.add(createProductCard("Arginine", "165.000đ / Hộp", true));
        gridPanel.add(createProductCard("Propolis", "185.000đ / Hộp", true));
        gridPanel.add(createProductCard("Echinacea", "155.000đ / Hộp", true));
        gridPanel.add(createProductCard("Siro ho", "47.000đ / Chai", true));
        gridPanel.add(createProductCard("Xịt mũi", "58.000đ / Chai", true));
        gridPanel.add(createProductCard("Kháng viêm", "210.000đ / Hộp", true));
        gridPanel.add(createProductCard("Men vi sinh", "198.000đ / Hộp", true));
        gridPanel.add(createProductCard("Dầu gió", "23.000đ / Chai", true));
        gridPanel.add(createProductCard("Vitamin B", "110.000đ / Hộp", true));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
<<<<<<< HEAD:src/main/java/com/example/gui/SanPhamPanel.java
        scrollPane.setBackground(new Color(245, 245, 245));
        scrollPane.setPreferredSize(new Dimension(0, 700));

        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int viewportWidth = scrollPane.getViewport().getWidth();
                if (viewportWidth > 0) {
                    int minCardWidth = 180 + 15; // width + gap
                    int cols = Math.max(1, Math.min(5, viewportWidth / minCardWidth));
                    if (cols == 0) cols = 1;
                    gridLayout.setColumns(cols);
                    gridPanel.revalidate();
                }
            }
        });
=======
        scrollPane.setBackground(new Color(241, 246, 255)); // #F1F6FF
>>>>>>> 206c2038d2eb152ffad3673f6e34d896dcdcddff:src/main/java/com/example/gui/screens/SanPhamPanel.java

        // 3. Phân trang (Mockup đơn giản)
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(new Color(241, 246, 255)); // #F1F6FF
        paginationPanel.add(new JLabel("• • • •")); 

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(paginationPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    // Helper method tạo thẻ sản phẩm (Product Card)
    private JPanel createProductCard(String name, String price, boolean inStock) {
        RoundedPanel card = new RoundedPanel(14, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
<<<<<<< HEAD:src/main/java/com/example/gui/SanPhamPanel.java
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(180, 220));
        card.setMaximumSize(new Dimension(180, 220));
        card.setMinimumSize(new Dimension(180, 220));
        card.setAlignmentY(Component.TOP_ALIGNMENT);
=======
        card.setBorder(new EmptyBorder(10, 10, 10, 10));
>>>>>>> 206c2038d2eb152ffad3673f6e34d896dcdcddff:src/main/java/com/example/gui/screens/SanPhamPanel.java

        // Ảnh (Mockup)
        JLabel lblImage = new JLabel("Ảnh SP", SwingConstants.CENTER);
        lblImage.setOpaque(true);
        lblImage.setBackground(new Color(230, 230, 230));
        lblImage.setPreferredSize(new Dimension(160, 80));
        lblImage.setMaximumSize(new Dimension(160, 80));
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên SP
        JLabel lblName = new JLabel("<html><div style='text-align: center;'>" + name + "</div></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setPreferredSize(new Dimension(160, 35));
        lblName.setMaximumSize(new Dimension(160, 35));

        // Nút chọn Đơn vị (Hộp / Vỉ / Viên)
        JPanel unitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        unitPanel.setBackground(Color.WHITE);
        unitPanel.setPreferredSize(new Dimension(160, 28));
        unitPanel.setMaximumSize(new Dimension(160, 28));
        String[] units = {"Hộp", "Vỉ", "Viên"};
        for (String u : units) {
            JButton btnU = new JButton(u);
            btnU.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnU.setMargin(new Insets(2, 5, 2, 5));
            btnU.addActionListener(e -> JOptionPane.showMessageDialog(
                    SanPhamPanel.this,
                    "Đã chọn đơn vị: " + u,
                    "Đơn vị tính",
                    JOptionPane.INFORMATION_MESSAGE
            ));
            unitPanel.add(btnU);
        }

        // Giá & Tình trạng
        JLabel lblPrice = new JLabel("Giá: " + price);
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JLabel lblStatus = new JLabel("Tình trạng: " + (inStock ? "Còn hàng" : "Hết hàng"));
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(inStock ? new Color(0, 153, 0) : Color.RED);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setPreferredSize(new Dimension(160, 40));
        infoPanel.setMaximumSize(new Dimension(160, 40));
        infoPanel.add(lblPrice);
        infoPanel.add(lblStatus);

        card.add(lblImage);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(lblName);
        card.add(Box.createRigidArea(new Dimension(0, 5)));
        card.add(unitPanel);
        card.add(Box.createRigidArea(new Dimension(0, 10)));
        card.add(infoPanel);

        return card;
    }

    // ==========================================
    // PANEL BÊN PHẢI (Thông tin chi tiết sản phẩm)
    // ==========================================
    private JPanel createRightPanel() {
        RoundedPanel rightPanel = new RoundedPanel(16, true);
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTitle = new JLabel("THÔNG TIN SẢN PHẨM", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        rightPanel.add(lblTitle, BorderLayout.NORTH);

        // Nội dung form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 5, 10);

        int row = 0;

        // Ảnh và nút chọn ảnh
        JPanel imagePanel = new JPanel(new BorderLayout(5, 5));
        imagePanel.setBackground(Color.WHITE);
        JLabel imgBox = new JLabel("Image Placeholder", SwingConstants.CENTER);
        imgBox.setPreferredSize(new Dimension(150, 100));
        imgBox.setBorder(new LineBorder(Color.LIGHT_GRAY));
        RoundedButton btnSelectImage = new RoundedButton("Chọn ảnh");
        btnSelectImage.setBackground(new Color(153, 225, 255));
<<<<<<< HEAD:src/main/java/com/example/gui/SanPhamPanel.java
        btnSelectImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh", "jpg", "jpeg", "png", "gif"));
            int result = chooser.showOpenDialog(SanPhamPanel.this);
            if (result == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(
                        SanPhamPanel.this,
                        "Đã chọn tệp: " + chooser.getSelectedFile().getName(),
                        "Chọn ảnh",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
=======
        btnSelectImage.setForeground(Color.BLACK);
>>>>>>> 206c2038d2eb152ffad3673f6e34d896dcdcddff:src/main/java/com/example/gui/screens/SanPhamPanel.java
        
        JPanel btnWrapper = new JPanel();
        btnWrapper.setBackground(Color.WHITE);
        btnWrapper.add(btnSelectImage);
        
        imagePanel.add(imgBox, BorderLayout.CENTER);
        imagePanel.add(btnWrapper, BorderLayout.SOUTH);

        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(imagePanel, gbc);

        // Khôi phục gbc
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;

        // Các trường nhập liệu
        addFormField(formPanel, gbc, row++, "Mã sản phẩm:", new RoundedTextField("HHTP-300MG-H", 15), false);
        addFormField(formPanel, gbc, row++, "Tên sản phẩm:", new RoundedTextField("Hoạt Huyết Trường Phúc", 15), true);
        addFormField(formPanel, gbc, row++, "Hoạt chất:", new RoundedTextField("Thục địa, Ích mẫu, Ngưu tất", 15), true);
        addFormField(formPanel, gbc, row++, "Số lượng tồn:", new RoundedTextField("200", 15), false);
        addFormField(formPanel, gbc, row++, "Đơn giá:", new RoundedTextField("99.000", 15), true);
        
        JComboBox<String> cbLoaiSP = new JComboBox<>(new String[]{"Thuốc ETC", "Thuốc OTC", "TPCN"});
        addFormField(formPanel, gbc, row++, "Loại sản phẩm:", cbLoaiSP, true);

        // Mô tả (JTextArea)
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        formPanel.add(new JLabel("Mô tả:"), gbc);

        JTextArea txtMoTa = new JTextArea("Phòng ngừa và điều trị thiểu năng tuần hoàn não...");
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        JScrollPane scrollMoTa = new JScrollPane(txtMoTa);
        scrollMoTa.setPreferredSize(new Dimension(200, 60));
        
        gbc.gridx = 1; gbc.gridy = row++; gbc.weightx = 0.7;
        formPanel.add(scrollMoTa, gbc);

        // Bảng quy đổi đơn vị
        String[] columns = {"Số lượng", "Đơn vị tính", "->", "Số lượng", "Đơn vị quy đổi"};
        Object[][] data = {
            {"1", "Hộp", "", "3", "Vỉ"},
            {"1", "Vỉ", "", "10", "Viên"}
        };
        JTable table = new JTable(new DefaultTableModel(data, columns));
        JScrollPane scrollTable = new JScrollPane(table);
        scrollTable.setPreferredSize(new Dimension(300, 80));
        
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2; gbc.insets = new Insets(15, 10, 10, 10);
        formPanel.add(scrollTable, gbc);

        rightPanel.add(formPanel, BorderLayout.CENTER);

        // Buttons Panel (Dưới cùng)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnThem = createStyledButton("Thêm", new Color(0, 204, 204), Color.WHITE);
        JButton btnSua = createStyledButton("Sửa", new Color(255, 255, 102), Color.BLACK);
        JButton btnXoa = createStyledButton("Xóa", new Color(255, 102, 102), Color.WHITE);
        JButton btnLamMoi = createStyledButton("Làm mới", new Color(0, 204, 255), Color.WHITE);

        btnThem.addActionListener(e -> JOptionPane.showMessageDialog(
                SanPhamPanel.this,
                "Đã thêm sản phẩm mới.",
                "Thêm sản phẩm",
                JOptionPane.INFORMATION_MESSAGE
        ));
        btnSua.addActionListener(e -> JOptionPane.showMessageDialog(
                SanPhamPanel.this,
                "Đã cập nhật thông tin sản phẩm.",
                "Sửa sản phẩm",
                JOptionPane.INFORMATION_MESSAGE
        ));
        btnXoa.addActionListener(e -> JOptionPane.showMessageDialog(
                SanPhamPanel.this,
                "Đã xóa sản phẩm.",
                "Xóa sản phẩm",
                JOptionPane.INFORMATION_MESSAGE
        ));
        btnLamMoi.addActionListener(e -> JOptionPane.showMessageDialog(
                SanPhamPanel.this,
                "Đã làm mới form.",
                "Làm mới",
                JOptionPane.INFORMATION_MESSAGE
        ));

        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    // Helper method thêm field vào form
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent inputComp, boolean isEditable) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3; gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(new JLabel(labelText), gbc);

        if (inputComp instanceof RoundedTextField) {
            ((RoundedTextField) inputComp).setEditable(isEditable);
            if (!isEditable) inputComp.setBackground(new Color(235, 235, 235));
        } else if (inputComp instanceof JTextField) {
            ((JTextField) inputComp).setEditable(isEditable);
            if (!isEditable) inputComp.setBackground(new Color(230, 230, 230));
        }
        inputComp.setPreferredSize(new Dimension(200, 32));

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 0.7;
        panel.add(inputComp, gbc);
    }

    // Helper method tạo button có màu
    private RoundedButton createStyledButton(String text, Color bgColor, Color fgColor) {
        RoundedButton btn = new RoundedButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }
<<<<<<< HEAD:src/main/java/com/example/gui/SanPhamPanel.java

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

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getForeground());
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 18, 18));
            g2.dispose();
        }
    }
}
=======
}

>>>>>>> 206c2038d2eb152ffad3673f6e34d896dcdcddff:src/main/java/com/example/gui/screens/SanPhamPanel.java
