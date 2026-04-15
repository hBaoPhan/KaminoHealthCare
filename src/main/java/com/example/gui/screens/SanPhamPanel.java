package com.example.gui.screens;
import com.example.gui.components.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

public class SanPhamPanel extends JPanel {
    public SanPhamPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

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
        leftPanel.setBackground(new Color(245, 245, 245));
        leftPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Top Bar (Tìm kiếm & Danh mục)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        topBar.setBackground(new Color(245, 245, 245));

        JTextField txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setText(" Tìm theo tên");
        txtSearch.setForeground(Color.GRAY);

        JComboBox<String> cbDanhMuc = new JComboBox<>(new String[]{"Danh mục","Thuốc", "Thực phẩm chức năng", "Dược mỹ phẩm", "Vật dụng y tế"});
        cbDanhMuc.setPreferredSize(new Dimension(150, 35));
        cbDanhMuc.setBackground(Color.WHITE);

        topBar.add(txtSearch);
        topBar.add(cbDanhMuc);

        // 2. Grid Sản phẩm
        JPanel gridPanel = new JPanel(new GridLayout(0, 4, 15, 15)); // 4 cột, khoảng cách 15px
        gridPanel.setBackground(new Color(245, 245, 245));

        // Thêm một số card sản phẩm mẫu giống trong hình
        gridPanel.add(createProductCard("Hoạt Huyết Trường Phúc", "99.000đ / Hộp", true));
        gridPanel.add(createProductCard("Freezefast Ghiaccio Spray", "210.000đ / Chai", false));
        gridPanel.add(createProductCard("Natto Gold 3000FU", "295.000đ / Hộp", false));
        gridPanel.add(createProductCard("SVR Sebiaclear Moussant", "229.400đ / Hộp", true));
        gridPanel.add(createProductCard("Hoạt Huyết Trường Phúc", "4.800đ / Viên", true));
        gridPanel.add(createProductCard("Giloba 40mg MEGA", "38.333đ / Vỉ", true));
        gridPanel.add(createProductCard("Hoạt Huyết Trường Phúc", "99.000đ / Hộp", true));
        gridPanel.add(createProductCard("Giloba 40mg MEGA", "207.480đ / Vỉ", true));
        gridPanel.add(createProductCard("Tanakan", "...", true));
        gridPanel.add(createProductCard("Hoạt Huyết Trường Phúc", "...", true));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(245, 245, 245));

        // 3. Phân trang (Mockup đơn giản)
        JPanel paginationPanel = new JPanel();
        paginationPanel.setBackground(new Color(245, 245, 245));
        paginationPanel.add(new JLabel("• • • •")); 

        leftPanel.add(topBar, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(paginationPanel, BorderLayout.SOUTH);

        return leftPanel;
    }

    // Helper method tạo thẻ sản phẩm (Product Card)
    private JPanel createProductCard(String name, String price, boolean inStock) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(10, 10, 10, 10)
        ));

        // Ảnh (Mockup)
        JLabel lblImage = new JLabel("Ảnh SP", SwingConstants.CENTER);
        lblImage.setOpaque(true);
        lblImage.setBackground(new Color(230, 230, 230));
        lblImage.setPreferredSize(new Dimension(100, 80));
        lblImage.setMaximumSize(new Dimension(150, 80));
        lblImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên SP
        JLabel lblName = new JLabel("<html><div style='text-align: center;'>" + name + "</div></html>");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setPreferredSize(new Dimension(120, 35));

        // Nút chọn Đơn vị (Hộp / Vỉ / Viên)
        JPanel unitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        unitPanel.setBackground(Color.WHITE);
        String[] units = {"Hộp", "Vỉ", "Viên"};
        for (String u : units) {
            JButton btnU = new JButton(u);
            btnU.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            btnU.setMargin(new Insets(2, 5, 2, 5));
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
        btnSelectImage.setForeground(Color.BLACK);
        
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
        addFormField(formPanel, gbc, row++, "Mã sản phẩm:", new JTextField("HHTP-300MG-H"), false);
        addFormField(formPanel, gbc, row++, "Tên sản phẩm:", new JTextField("Hoạt Huyết Trường Phúc"), true);
        addFormField(formPanel, gbc, row++, "Hoạt chất:", new JTextField("Thục địa, Ích mẫu, Ngưu tất"), true);
        addFormField(formPanel, gbc, row++, "Số lượng tồn:", new JTextField("200"), false);
        addFormField(formPanel, gbc, row++, "Đơn giá:", new JTextField("99.000"), true);
        
        JComboBox<String> cbLoaiSP = new JComboBox<>(new String[]{"Thuốc", "Thực phẩm chức năng", "Vật tư y tế"});
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

        buttonPanel.add(createStyledButton("Thêm", new Color(0, 204, 204), Color.WHITE));
        buttonPanel.add(createStyledButton("Sửa", new Color(255, 255, 102), Color.BLACK));
        buttonPanel.add(createStyledButton("Xóa", new Color(255, 102, 102), Color.WHITE));
        buttonPanel.add(createStyledButton("Làm mới", new Color(0, 204, 255), Color.WHITE));

        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    // Helper method thêm field vào form
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent inputComp, boolean isEditable) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3; gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(new JLabel(labelText), gbc);

        if (inputComp instanceof JTextField) {
            ((JTextField) inputComp).setEditable(isEditable);
            if (!isEditable) {
                inputComp.setBackground(new Color(230, 230, 230));
            }
        }
        inputComp.setPreferredSize(new Dimension(200, 28));

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
}

