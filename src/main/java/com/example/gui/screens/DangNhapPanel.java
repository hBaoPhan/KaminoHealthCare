package com.example.gui.screens;
import com.example.gui.components.*;

import com.example.entity.ChucVu;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.formdev.flatlaf.FlatLightLaf; // Thư viện tạo giao diện hiện đại

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

/**
 * Chỉ tạo Giao diện (UI) Form Đăng nhập Kamino Healthcare.
 * Thiết kế dựa trên hình ảnh Figma. Chưa có xử lý logic.
 */
public class DangNhapPanel extends JFrame implements ActionListener {

    // --- Components để hiển thị ---
    private RoundedTextField txtUsername;
    private RoundedPasswordField txtPassword;
    private RoundedButton btnLogin;
    private JLabel lblForgotPassword, lblRegister, lblHidePassword, lblPharmacistImage;
    private boolean isPasswordHidden = true;

    // --- Bảng màu từ thiết kế Figma ---
    private final Color COLOR_PRIMARY = new Color(0x54ACD2);
    private final Color COLOR_TEXT_HINT = new Color(150, 150, 150);
    private final Color COLOR_LINK = new Color(0, 102, 204);

    public DangNhapPanel() {
        // 1. Cấu hình cửa sổ chính
        setTitle("KAMINO Healthcare - Hệ Thống Quản Lý Nhà Thuốc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600); // Kích thước cửa sổ
        setLocationRelativeTo(null); // Hiển thị giữa màn hình
        setResizable(false); // Không cho phép đổi kích thước
        setLayout(new BorderLayout()); // Sử dụng BorderLayout cho Panel trái/phải

        // 2. Tạo Panel bên trái (Nền xanh, Logo, Hình ảnh)
        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        // 3. Tạo Panel bên phải (Nền trắng, Form nhập liệu)
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);

        // 4. (Tùy chọn) Thêm sự kiện cơ bản cho giao diện (như ẩn/hiện mật khẩu)
        setupBasicUIEvents();
    }

    /**
     * Tạo Panel bên trái với nền xanh và logo.
     */
    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PRIMARY);
        panel.setPreferredSize(new Dimension(500, 600)); // Độ rộng cố định
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Sắp xếp theo chiều dọc
        panel.setBorder(new EmptyBorder(80, 40, 50, 40)); // Padding

        // Logo "KAMINO Healthcare"
        JLabel lblLogo = new JLabel("KAMINO HEALTHCARE");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Font hiện đại, đậm, lớn
        lblLogo.setForeground(Color.WHITE); // Chữ màu trắng
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa
        panel.add(lblLogo);

        panel.add(Box.createVerticalStrut(30)); // Khoảng cách dọc

        // Hình ảnh minh họa Dược sĩ (Sẽ cần file ảnh thực tế sau)

        ImageIcon pharmacistIcon = loadIcon("/images/icon/logo.png");
        if (pharmacistIcon != null) {
            Image scaledImage = pharmacistIcon.getImage().getScaledInstance(350, 350, Image.SCALE_SMOOTH);
            lblPharmacistImage = new JLabel(new ImageIcon(scaledImage));
            lblPharmacistImage.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblPharmacistImage);
        } else {
            // Placeholder nếu không tìm thấy ảnh
            lblPharmacistImage = new JLabel("[Hình ảnh Dược sĩ]");
            lblPharmacistImage.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblPharmacistImage.setForeground(Color.WHITE);
            lblPharmacistImage.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblPharmacistImage);
        }

        return panel;
    }

    /**
     * Tạo Panel bên phải với Form nhập liệu nền trắng.
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(null); // Sử dụng Null Layout để đặt vị trí chính xác như Figma
        panel.setBorder(new EmptyBorder(50, 60, 50, 60));

        int currentY = 100; // Vị trí Y bắt đầu

        // Tiêu đề "Đăng Nhập"
        JLabel lblTitle = new JLabel("Đăng Nhập");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setBounds(60, currentY, 300, 40);
        panel.add(lblTitle);

        currentY += 80;

        // --- Ô nhập Tài khoản (Bo góc) ---
        txtUsername = new RoundedTextField("Tài khoản", 20); // Có chữ gợi ý (Hint)
        txtUsername.setBounds(60, currentY, 400, 45);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // Thêm Padding bên trái để chữ không sát viền
        txtUsername.setBorder(new EmptyBorder(0, 15, 0, 10));
        panel.add(txtUsername);

        currentY += 70;

        // --- Ô nhập Mật khẩu (Bo góc, ẩn ký tự) ---
        txtPassword = new RoundedPasswordField("Mật khẩu", 20);
        txtPassword.setBounds(60, currentY, 400, 45);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        // Padding bên trái cho chữ, Padding bên phải cho Icon con mắt
        txtPassword.setBorder(new EmptyBorder(0, 15, 0, 45));
        panel.add(txtPassword);

        // Icon con mắt để ẩn/hiện mật khẩu
        lblHidePassword = new JLabel(loadIcon("/images/icon/eye.jpg"));
        lblHidePassword.setBounds(425, currentY + 10, 24, 24);
        lblHidePassword.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Con trỏ hình bàn tay
        panel.add(lblHidePassword);

        currentY += 60;

        // --- Quên mật khẩu? (Liên kết màu xanh) ---
        lblForgotPassword = new JLabel("Quên mật khẩu?");
        lblForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblForgotPassword.setForeground(COLOR_LINK);
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgotPassword.setBounds(340, currentY, 120, 20);
        panel.add(lblForgotPassword);

        currentY += 50;

        // --- Nút Đăng Nhập (Lớn, nền xanh, chữ trắng) ---
        btnLogin = new RoundedButton("Đăng Nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBackground(COLOR_PRIMARY);
        btnLogin.setBounds(60, currentY, 400, 50);
        btnLogin.addActionListener(this);
        panel.add(btnLogin);

        currentY += 80;

        // // --- Chưa có tài khoản? Đăng ký (Liên kết) ---
        // JLabel lblNoAccount = new JLabel("Chưa có tài khoản?");
        // lblNoAccount.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        // lblNoAccount.setBounds(150, currentY, 130, 20);
        // panel.add(lblNoAccount);
        //
        // lblRegister = new JLabel("Đăng ký");
        // lblRegister.setFont(new Font("Segoe UI", Font.BOLD, 14));
        // lblRegister.setForeground(COLOR_LINK);
        // lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // lblRegister.setBounds(285, currentY, 70, 20);
        // panel.add(lblRegister);

        return panel;
    }

    /**
     * Chỉ thiết lập các sự kiện giao diện cơ bản (không gọi logic).
     */
    private void setupBasicUIEvents() {
        // Sự kiện click vào icon con mắt để ẩn/hiện mật khẩu
        lblHidePassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPasswordHidden) {
                    txtPassword.setEchoChar((char) 0); // Hiện mật khẩu thực
                    lblHidePassword.setIcon(loadIcon("/com/resources/icons/eye_show.png")); // Đổi icon
                } else {
                    txtPassword.setEchoChar('•'); // Ẩn mật khẩu (dùng dấu chấm)
                    lblHidePassword.setIcon(loadIcon("/com/resources/icons/eye_hide.png")); // Đổi icon
                }
                isPasswordHidden = !isPasswordHidden;
            }
        });
    }

    /**
     * Hàm hỗ trợ load Icon an toàn.
     */
    private ImageIcon loadIcon(String path) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl);
        } else {
            // In lỗi ra console nếu không tìm thấy file, nhưng không làm hỏng giao diện
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(btnLogin)) {
            SwingUtilities.invokeLater(() -> {
                NhanVien nv = new NhanVien("NV001", "Hoài Bảo", "123456789", "0987654321", ChucVu.NHANVIENQUANLY, true);
                TaiKhoan tk = new TaiKhoan("admin", "admin", nv);
                ThanhDieuHuongPanel mainFrame = new ThanhDieuHuongPanel(tk);
                mainFrame.setVisible(true);
            });
            dispose();
        }
    }
}

