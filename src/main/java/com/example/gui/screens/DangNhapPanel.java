package com.example.gui.screens;

import com.example.gui.components.*;

import com.example.entity.enums.ChucVu;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.connectDB.ConnectDB;
import com.example.dao.TaiKhoanDAO;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

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
            Image scaledImage = pharmacistIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
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
     * Dùng BoxLayout thay vì Null Layout để layout manager tự quản lý bounds,
     * tránh lỗi clip góc trái của RoundedTextField.
     */
    private JPanel createRightPanel() {
        // Wrapper: căn giữa form theo chiều ngang và dọc
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);

        // Form panel: BoxLayout Y_AXIS, giống cấu trúc BanHangPanel
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setOpaque(false);

        // --- Tiêu đề "Đăng Nhập" ---
        JLabel lblTitle = new JLabel("Đăng Nhập");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblTitle);

        form.add(Box.createVerticalStrut(30));

        // --- Ô nhập Tài khoản ---
        txtUsername = new RoundedTextField("Tài khoản", 20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setMaximumSize(new Dimension(362, 40));
        txtUsername.setPreferredSize(new Dimension(362, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtUsername);

        form.add(Box.createVerticalStrut(15));

        // --- Ô nhập Mật khẩu + Icon mắt ---
        JPanel passwordRow = new JPanel(new BorderLayout(8, 0));
        passwordRow.setOpaque(false);
        passwordRow.setMaximumSize(new Dimension(400, 38));
        passwordRow.setPreferredSize(new Dimension(400, 38)); // Cố định chiều cao để tránh clip top
        passwordRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new RoundedPasswordField("Mật khẩu", 20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        passwordRow.add(txtPassword, BorderLayout.CENTER);

        // Scale icon eye về 20x20
        ImageIcon eyeIcon = loadIcon("/images/icon/hide.png");
        if (eyeIcon != null) {
            Image scaledEye = eyeIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
            lblHidePassword = new JLabel(new ImageIcon(scaledEye));
        } else {
            lblHidePassword = new JLabel("👁");
        }
        lblHidePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblHidePassword.setPreferredSize(new Dimension(30, 45)); // Width đủ rộng, height = row
        passwordRow.add(lblHidePassword, BorderLayout.EAST);

        form.add(passwordRow);

        form.add(Box.createVerticalStrut(10));

        // --- Quên mật khẩu? ---
        JPanel forgotRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotRow.setOpaque(false);
        forgotRow.setMaximumSize(new Dimension(400, 25));
        forgotRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblForgotPassword = new JLabel("Quên mật khẩu?");
        lblForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblForgotPassword.setForeground(COLOR_LINK);
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotRow.add(lblForgotPassword);
        form.add(forgotRow);

        form.add(Box.createVerticalStrut(25));

        // --- Nút Đăng Nhập ---
        btnLogin = new RoundedButton("Đăng Nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBackground(COLOR_PRIMARY);
        btnLogin.setMaximumSize(new Dimension(400, 50));
        btnLogin.setPreferredSize(new Dimension(400, 50));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(this);
        form.add(btnLogin);

        try {
            ConnectDB.getInstance().connect();
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        wrapper.add(form);
        return wrapper;
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
                    lblHidePassword.setIcon(loadScaledIcon("/images/icon/view.png", 25, 25));
                } else {
                    txtPassword.setEchoChar('\u2022'); // Ẩn mật khẩu
                    lblHidePassword.setIcon(loadScaledIcon("/images/icon/hide.png", 25, 25));
                }
                isPasswordHidden = !isPasswordHidden;
            }
        });
    }

    /**
     * Load icon an toàn và scale về kích thước chỉ định.
     */
    private ImageIcon loadScaledIcon(String path, int w, int h) {
        ImageIcon icon = loadIcon(path);
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }

    /**
     * Hàm hỗ trợ load Icon an toàn.
     */
    private ImageIcon loadIcon(String path) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null) {
            return new ImageIcon(imgUrl);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(btnLogin)) {
            // String username = txtUsername.getText();
            // String password = new String(txtPassword.getPassword());
            //
            // if (username.trim().isEmpty() || password.isEmpty()) {
            // JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật
            // khẩu!", "Thông báo",
            // JOptionPane.WARNING_MESSAGE);
            // return;
            // }
            //
            TaiKhoanDAO dao = new TaiKhoanDAO();
            TaiKhoan tk = dao.timTheoMa("admin");

            //
            // if (tk != null) {
            // String dbPassword = tk.getMatKhau();
            // boolean isMatch = false;
            //
            // // Kiểm tra xem dbPassword có phải là chuỗi hash của BCrypt không
            // // BCrypt hash thường bắt đầu bằng $2a$, $2b$, hoặc $2y$ và có độ dài 60 ký
            //
            // if (dbPassword != null && dbPassword.startsWith("$2") && dbPassword.length()
            // == 60) {
            // try {
            // isMatch = BCrypt.checkpw(password, dbPassword);
            // } catch (Exception ex) {
            // isMatch = false;
            // }
            // } else if (dbPassword != null) {
            // // Fallback cho mật khẩu chưa hash trong quá trình phát triển
            // isMatch = dbPassword.equals(password);
            // }
            boolean isMatch = true;
            if (isMatch) {
                SwingUtilities.invokeLater(() -> {
                    ThanhDieuHuongPanel mainFrame = new ThanhDieuHuongPanel(tk);
                    mainFrame.setVisible(true);
                });
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không chính xác!", "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không chính xác!", "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
