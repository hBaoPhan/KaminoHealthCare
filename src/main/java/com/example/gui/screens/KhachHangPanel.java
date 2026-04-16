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

public class KhachHangPanel extends JPanel {

    public KhachHangPanel() {
        // 1. Thiết lập layout chính
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // 2. Thêm thanh tìm kiếm (Top Bar)
        add(createTopBar(), BorderLayout.NORTH);

        // 3. Khu vực giữa: Chia đôi Form và Bảng
        JPanel centerArea = new JPanel(new GridLayout(1, 2, 20, 0));
        centerArea.setBackground(Color.WHITE);
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

        JComboBox<String> cbFilter = new JComboBox<>(
                new String[] { "Loại khách hàng", "Khách thành viên", "Khách lẻ" });
        cbFilter.setPreferredSize(new Dimension(150, 35));

        topBar.add(txtSearch);
        topBar.add(cbFilter);
        return topBar;
    }

    // --- FORM NHẬP LIỆU ---
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), "THÔNG TIN KHÁCH HÀNG");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        formPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(20, 20, 20, 20)));

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 25));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Mã khách hàng:"));
        JTextField txtId = new JTextField();
        txtId.setEnabled(false);
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Tên khách hàng:"));
        inputPanel.add(new JTextField());

        inputPanel.add(new JLabel("Số điện thoại:"));
        inputPanel.add(new JTextField());

        inputPanel.add(new JLabel("KHTV:"));
        inputPanel.add(new JComboBox<>(new String[] { "Có", "Không" }));

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

    // --- BẢNG DỮ LIỆU ---
    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), "DANH SÁCH KHÁCH HÀNG");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 16));
        tableContainer.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        String[] columns = { "Mã khách hàng", "Tên khách hàng", "Số điện thoại", "Khách hàng thành viên" };
        Object[][] data = {
                { "TV000001", "Phan Bảo Bối", "0987 665 456", "Có" },
                { "TV000002", "Phan Bảo Bối", "0954 578 331", "Có" },
                { "KL000001", "Võ Hữu Điền", "0786 775 554", "Không" },
                { "TV000001", "Nguyễn Thành Long", "0986 245 765", "Có" }
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

        // 1. Thiết lập màu sắc MẶC ĐỊNH
        btn.setBackground(bgColor); // Màu nền ban đầu
        btn.setForeground(Color.black); // Chữ luôn màu trắng

        // 2. Định dạng Font và Kiểu dáng
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(100, 35));

        // 3. Xóa bỏ các hiệu ứng mặc định của hệ điều hành để hiện Solid color
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // 4. THÊM HIỆU ỨNG HOVER (LÀM ĐẬM MÀU NỀN KHI DI CHUỘT)
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                // Khi chuột di vào: NỀN chuyển sang màu ĐẬM hơn (darker)
                btn.setBackground(bgColor.darker());
                // Đồng thời hiện con trỏ chuột dạng bàn tay
                btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                // Khi chuột di ra: NỀN quay lại màu ban đầu
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    // Giữ lại phương thức này theo yêu cầu của bạn
    public void taiLaiDanhSach() {
        // Code tải lại dữ liệu từ Database sẽ viết ở đây
    }

    // Hàm main để chạy thử giao diện
    // public static void main(String[] args) {
    // try {
    // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    // } catch (Exception e) {}

    // SwingUtilities.invokeLater(() -> {
    // JFrame frame = new JFrame("Quản Lý Khách Hàng");
    // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // frame.setSize(1100, 700);
    // frame.add(new KhachHangPanel());
    // frame.setLocationRelativeTo(null);
    // frame.setVisible(true);
    // });
    // }
}