package com.example.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class NhanVienPanel extends JPanel {

    public NhanVienPanel() {
        // 1. Thiết lập layout chính
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // 2. Thêm thanh tìm kiếm (Top Bar)
        add(createTopBar(), BorderLayout.NORTH);

        // 3. Khu vực giữa: Chia đôi Form và Bảng
        JPanel centerArea = new JPanel(new GridLayout(1, 2, 20, 0));
        centerArea.setBackground(new Color(245, 245, 245));
        centerArea.setBorder(new EmptyBorder(10, 20, 20, 20));

        centerArea.add(createFormPanel());
        centerArea.add(createTablePanel());

        add(centerArea, BorderLayout.CENTER);
    }

    // --- PHẦN TÌM KIẾM ---
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topBar.setBackground(Color.WHITE);

        JTextField txtSearch = new JTextField("  Tìm theo tên...", 25);
        txtSearch.setPreferredSize(new Dimension(250, 35));

        JComboBox<String> cbChucVuFilter = new JComboBox<>(new String[] { "Chức vụ", "Quản lý", "Dược sĩ" });
        cbChucVuFilter.setPreferredSize(new Dimension(150, 35));
        cbChucVuFilter.setBackground(Color.WHITE);

        topBar.add(txtSearch);
        topBar.add(cbChucVuFilter);
        return topBar;
    }

    // --- FORM NHẬP LIỆU NHÂN VIÊN ---
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), "THÔNG TIN NHÂN VIÊN");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(20, 30, 20, 30)));

        // Cấu trúc 5 hàng theo hình ảnh (Mã, Tên, SĐT, CCCD, Chức vụ)
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 25));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Mã nhân viên:"));
        JTextField txtId = new JTextField();
        txtId.setEnabled(false); // Disable mã NV
        txtId.setBackground(new Color(230, 230, 230));
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Tên nhân viên:"));
        inputPanel.add(new JTextField());

        inputPanel.add(new JLabel("Số điện thoại:"));
        inputPanel.add(new JTextField());

        inputPanel.add(new JLabel("CCCD:"));
        inputPanel.add(new JTextField());

        inputPanel.add(new JLabel("Chức vụ:"));
        inputPanel.add(new JComboBox<>(new String[] { "Quản lý", "Dược sĩ" }));

        formPanel.add(inputPanel, BorderLayout.NORTH);

        // Panel chứa các nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(50, 0, 0, 0));

        buttonPanel.add(createColorButton("Thêm", new Color(21, 215, 221)));
        buttonPanel.add(createColorButton("Sửa", new Color(255, 243, 108)));
        buttonPanel.add(createColorButton("Xóa", new Color(255, 89, 89)));
        buttonPanel.add(createColorButton("Làm mới", new Color(0, 213, 255)));

        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        return formPanel;
    }

    // --- BẢNG DANH SÁCH NHÂN VIÊN ---
    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), "DANH SÁCH NHÂN VIÊN");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        tableContainer.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        // Các cột theo hình: Mã NV, Tên NV, SĐT, Chức vụ
        String[] columns = { "Mã nhân viên", "Tên nhân viên", "Số điện thoại", "Chức vụ" };
        Object[][] data = {
                { "QL001", "Phan Hoài Bảo", "0987 665 456", "Quản lý" },
                { "DS001", "Nguyễn Xuân Trường", "0398 161 056", "Dược sĩ" },
                { "DS002", "Nguyễn Minh Nhật", "0987 654 321", "Dược sĩ" },
                { "DS003", "Trần Tấn Tài", "0678 997 278", "Dược sĩ" },
                { "DS004", "Nguyễn Thành Long", "0986 245 765", "Dược sĩ" }
        };

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        return tableContainer;
    }

    // --- HÀM TẠO NÚT BẤM (HOVER & COLOR) ---
    private JButton createColorButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.BLACK); // Giữ chữ đen theo phong cách hình ảnh

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(100, 35));

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    public void taiLaiDanhSach() {
        // Code tải lại dữ liệu nhân viên từ Database
    }

    // Hàm main để test độc lập
    // public static void main(String[] args) {
    // try {
    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    // } catch (Exception e) {}

    // SwingUtilities.invokeLater(() -> {
    // JFrame frame = new JFrame("Quản Lý Nhân Viên");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setSize(1200, 800);
    // frame.add(new NhanVienPanel());
    // frame.setLocationRelativeTo(null);
    // frame.setVisible(true);
    // });
    // }
}