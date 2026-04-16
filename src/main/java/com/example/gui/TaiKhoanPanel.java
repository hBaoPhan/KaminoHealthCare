package com.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TaiKhoanPanel extends JPanel {

    public TaiKhoanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 245, 245));

        // 1. Khu vực trên cùng: Tìm kiếm & Lọc
        add(createTopPanel(), BorderLayout.NORTH);

        // 2. Khu vực trung tâm
        JPanel pnlCenter = new JPanel(new BorderLayout(15, 0));
        pnlCenter.setBackground(new Color(245, 245, 245));
        
        pnlCenter.add(createFormPanel(), BorderLayout.WEST);
        pnlCenter.add(createTablePanel(), BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);
    }

    // --- TẠO THANH TÌM KIẾM VÀ LỌC ---
    private JPanel createTopPanel() {
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlTop.setBackground(new Color(245, 245, 245));
        
        JTextField txtTimKiem = new JTextField(25);
        txtTimKiem.setToolTipText("Nhập tên đăng nhập hoặc tên nhân viên...");
        
        JComboBox<String> cmbLocVaiTro = new JComboBox<>(new String[]{"Tất cả vai trò", "Quản lý", "Dược sĩ"});
        cmbLocVaiTro.setPreferredSize(new Dimension(120, 30));
        
        JComboBox<String> cmbLocTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang hoạt động", "Bị khóa"});
        cmbLocTrangThai.setPreferredSize(new Dimension(120, 30));
        
        pnlTop.add(new JLabel("Tìm kiếm:"));
        pnlTop.add(txtTimKiem);
        pnlTop.add(new JLabel("Lọc theo:"));
        pnlTop.add(cmbLocVaiTro);
        pnlTop.add(cmbLocTrangThai);
        
        return pnlTop;
    }

    // --- TẠO FORM THÔNG TIN TÀI KHOẢN ---
    private JPanel createFormPanel() {
        JPanel pnlForm = new JPanel(new BorderLayout());
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(20, 20, 20, 20)));
        pnlForm.setPreferredSize(new Dimension(380, 0));

        // 1. Tiêu đề
        JLabel lblTitle = new JLabel("THÔNG TIN TÀI KHOẢN", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0)); // Thêm khoảng cách dưới tiêu đề
        pnlForm.add(lblTitle, BorderLayout.NORTH);

        // 2. Form nhập liệu
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 5, 12, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Dimension sizeCbo = new Dimension(250, 30);

        JTextField txtTenDangNhap = new JTextField(15);
        JPasswordField txtMatKhau = new JPasswordField(15);
        
        JComboBox<String> cmbNhanVien = new JComboBox<>(new String[]{"NV001 - Hoài Bảo", "NV002 - Trần Tấn Tài", "NV003 - Kiều Đỗ Thủy Tiên"});
        cmbNhanVien.setPreferredSize(sizeCbo); 
        cmbNhanVien.setMinimumSize(sizeCbo); // <-- THÊM DÒNG NÀY ĐỂ ÉP CỨNG

        JComboBox<String> cmbVaiTro = new JComboBox<>(new String[]{"Dược sĩ", "Quản lý"});
        cmbVaiTro.setPreferredSize(sizeCbo); 
        cmbVaiTro.setMinimumSize(sizeCbo); // <-- THÊM DÒNG NÀY ĐỂ ÉP CỨNG

        JComboBox<String> cmbTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Bị khóa"});
        cmbTrangThai.setPreferredSize(sizeCbo); 
        cmbTrangThai.setMinimumSize(sizeCbo); // <-- THÊM DÒNG NÀY ĐỂ ÉP CỨNG
     

        String[] labels = {"Tên đăng nhập:", "Mật khẩu:", "Nhân viên:", "Vai trò:", "Trạng thái:"};
        Component[] inputs = {txtTenDangNhap, txtMatKhau, cmbNhanVien, cmbVaiTro, cmbTrangThai};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0.3; 
            pnlInput.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.weightx = 0.7; 
            
            if (inputs[i] instanceof JComboBox) {
                gbc.fill = GridBagConstraints.NONE; 
                gbc.anchor = GridBagConstraints.WEST; 
            } else {
                gbc.fill = GridBagConstraints.HORIZONTAL; 
            }
            pnlInput.add(inputs[i], gbc);
        }
        
        // --- THỦ THUẬT ÉP FORM LÊN TRÊN ---
        // Tạo một Panel bọc ngoài và đặt pnlInput lên phía trên (NORTH) của nó
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setBackground(Color.WHITE);
        pnlWrapper.add(pnlInput, BorderLayout.NORTH);
        
        // Thêm Panel bọc ngoài vào phần giữa của pnlForm
        pnlForm.add(pnlWrapper, BorderLayout.CENTER);

        // 3. Các nút chức năng
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 15));
        pnlButtons.setBackground(Color.WHITE);
        
        JButton btnThem = createHoverButton("Thêm", new Color(0, 204, 255));
        JButton btnSua = createHoverButton("Cập nhật", new Color(255, 235, 59));
        JButton btnKhoa = createHoverButton("Khóa TK", new Color(255, 82, 82));
        JButton btnLamMoi = createHoverButton("Làm mới", new Color(220, 220, 220));

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnKhoa);
        pnlButtons.add(btnLamMoi);

        pnlForm.add(pnlButtons, BorderLayout.SOUTH);

        return pnlForm;
    }

    // --- TẠO BẢNG DANH SÁCH TÀI KHOẢN ---
    private JPanel createTablePanel() {
        JPanel pnlTableContent = new JPanel(new BorderLayout());
        pnlTableContent.setBackground(Color.WHITE);
        pnlTableContent.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                new EmptyBorder(20, 10, 10, 10)));

        JLabel lblTitle = new JLabel("DANH SÁCH TÀI KHOẢN");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnlTableContent.add(lblTitle, BorderLayout.NORTH);

        String[] columns = {"Tên đăng nhập", "Tên nhân viên", "Vai trò", "Trạng thái"};
        Object[][] data = {
                {"admin", "Hoài Bảo", "Quản lý", "Đang hoạt động"},
                {"tantai_nv", "Trần Tấn Tài", "Dược sĩ", "Đang hoạt động"},
                {"thuytien_kho", "Kiều Đỗ Thủy Tiên", "Dược sĩ", "Đang hoạt động"},
                {"minhnhat22", "Nguyễn Minh Nhật", "Dược sĩ", "Bị khóa"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns);
        JTable table = new JTable(model);
        
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(240, 240, 240));
        table.setDefaultEditor(Object.class, null); 

        JScrollPane scrollPane = new JScrollPane(table);
        pnlTableContent.add(scrollPane, BorderLayout.CENTER);

        return pnlTableContent;
    }

    // --- HÀM TẠO NÚT BẤM CÓ HIỆU ỨNG HOVER ---
    private JButton createHoverButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(85, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 

        Color hoverColor = bgColor.darker();

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

    // --- HÀM MAIN ĐỂ CHẠY THỬ NGHIỆM ĐỘC LẬP ---
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Test Giao Diện: TaiKhoanPanel");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 600); 
            frame.setLocationRelativeTo(null); 
            
            frame.add(new TaiKhoanPanel());
            
            frame.setVisible(true);
        });
    }
}