package com.example.gui;

import com.formdev.flatlaf.FlatLightLaf; // Thư viện tạo giao diện hiện đại

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;

/**
 * Chỉ tạo Giao diện (UI) Form Đăng nhập Kamino Healthcare.
 * Thiết kế dựa trên hình ảnh Figma. Chưa có xử lý logic.
 */
public class DangNhapGUI extends JFrame {

    // --- Components để hiển thị ---
    private RoundedTextField txtUsername;
    private RoundedPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblForgotPassword, lblRegister, lblHidePassword, lblPharmacistImage;
    private boolean isPasswordHidden = true;

    // --- Bảng màu từ thiết kế Figma ---
    private final Color COLOR_PRIMARY = new Color(0x54ACD2); // Xanh lá cây ForestGreen
    private final Color COLOR_TEXT_HINT = new Color(150, 150, 150); // Màu chữ gợi ý xám
    private final Color COLOR_LINK = new Color(0, 102, 204); // Màu xanh dương cho liên kết

    public DangNhapGUI() {
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
        panel.setPreferredSize(new Dimension(450, 600)); // Độ rộng cố định
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); // Sắp xếp theo chiều dọc
        panel.setBorder(new EmptyBorder(80, 40, 50, 40)); // Padding

        // Logo "KAMINO Healthcare"
        JLabel lblLogo = new JLabel("KAMINO Healthcare");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 36)); // Font hiện đại, đậm, lớn
        lblLogo.setForeground(Color.WHITE); // Chữ màu trắng
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa
        panel.add(lblLogo);

        panel.add(Box.createVerticalStrut(30)); // Khoảng cách dọc

        // Hình ảnh minh họa Dược sĩ (Sẽ cần file ảnh thực tế sau)
        ImageIcon pharmacistIcon = loadIcon("/images/logo.jpg");
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
        lblHidePassword = new JLabel(loadIcon("/images/eye.jpg"));
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
        btnLogin = new JButton("Đăng Nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBackground(COLOR_PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false); // Xóa viền khi focus
        btnLogin.setBorderPainted(false); // Xóa viền mặc định
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Bo góc cho nút (Sẽ được FlatLaf xử lý tự động nếu cấu hình)
        btnLogin.setBounds(60, currentY, 400, 50);
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

    // ========================================================================
    // --- Lớp tùy chỉnh (Custom Components) để vẽ ô nhập liệu bo góc ---
    // ========================================================================

    class RoundedTextField extends JTextField {
        private Shape shape;
        private String hint;

        public RoundedTextField(String hint, int size) {
            super(size);
            this.hint = hint;
            setOpaque(false); // Để vẽ nền tùy chỉnh bo góc
            setForeground(COLOR_TEXT_HINT); // Màu chữ gợi ý ban đầu
            setText(hint);

            // Xử lý logic hiển thị Hint Text (Chữ gợi ý)
            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (getText().equals(hint)) {
                        setText(""); // Xóa hint khi user click vào
                        setForeground(Color.BLACK); // Đổi màu chữ sang đen
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (getText().isEmpty()) {
                        setText(hint); // Hiện lại hint nếu user bỏ trống
                        setForeground(COLOR_TEXT_HINT);
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Làm mịn nét
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15); // Vẽ nền bo góc 15px
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200)); // Màu viền xám nhạt
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15); // Vẽ viền bo góc
            g2.dispose();
        }
    }

    class RoundedPasswordField extends JPasswordField {
        private Shape shape;
        private String hint;

        public RoundedPasswordField(String hint, int size) {
            super(size);
            this.hint = hint;
            setOpaque(false);
            setForeground(COLOR_TEXT_HINT);
            setText(hint);
            setEchoChar((char) 0); // Hiện hint ban đầu (không ẩn)

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (new String(getPassword()).equals(hint)) {
                        setText(""); // Xóa hint
                        setForeground(Color.BLACK);
                        setEchoChar('•'); // Bắt đầu ẩn ký tự khi nhập
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (new String(getPassword()).isEmpty()) {
                        setText(hint); // Hiện lại hint
                        setForeground(COLOR_TEXT_HINT);
                        setEchoChar((char) 0); // Không ẩn hint
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            g2.dispose();
        }
    }

    /**
     * Hàm main để chạy thử giao diện.
     */
    public static void main(String[] args) {
        // Thiết lập FlatLaf để có giao diện hiện đại, phẳng
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());

            // Tùy chỉnh bo góc mặc định cho nút nếu dùng FlatLaf
            UIManager.put("Button.arc", 15);

        } catch (Exception ex) {
            System.err.println("Failed to initialize FlatLaf Look and Feel");
        }

        // Chạy giao diện trên Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new DangNhapGUI().setVisible(true);
        });
    }
}