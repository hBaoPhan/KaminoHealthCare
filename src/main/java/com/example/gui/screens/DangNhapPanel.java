package com.example.gui.screens;

import com.example.gui.components.*;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.entity.enums.ChucVu;
import com.example.connectDB.ConnectDB;
import com.example.dao.NhanVienDAO;
import com.example.dao.TaiKhoanDAO;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import com.example.entity.enums.ChucVu;

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
    private NhanVienDAO nhanVienDAO;

    public DangNhapPanel() {
        // 1. Cấu hình cửa sổ chính
        setTitle("KAMINO Healthcare - Hệ Thống Quản Lý Nhà Thuốc");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // 2. Tạo Panel bên trái (Nền xanh, Logo, Hình ảnh)
        JPanel leftPanel = createLeftPanel();
        add(leftPanel, BorderLayout.WEST);

        // 3. Tạo Panel bên phải (Nền trắng, Form nhập liệu)
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);

        // 4. Sự kiện UI cơ bản
        setupBasicUIEvents();

        // Kết nối Database khi mở ứng dụng
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(COLOR_PRIMARY);
        panel.setPreferredSize(new Dimension(500, 600));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(80, 40, 50, 40));

        JLabel lblLogo = new JLabel("KAMINO HEALTHCARE");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblLogo.setForeground(Color.WHITE);
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lblLogo);

        panel.add(Box.createVerticalStrut(30));

        ImageIcon pharmacistIcon = loadIcon("/images/icon/logo.png");
        if (pharmacistIcon != null) {
            Image scaledImage = pharmacistIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
            lblPharmacistImage = new JLabel(new ImageIcon(scaledImage));
            lblPharmacistImage.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblPharmacistImage);
        } else {
            lblPharmacistImage = new JLabel("[Hình ảnh Dược sĩ]");
            lblPharmacistImage.setFont(new Font("Segoe UI", Font.ITALIC, 16));
            lblPharmacistImage.setForeground(Color.WHITE);
            lblPharmacistImage.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(lblPharmacistImage);
        }
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setOpaque(false);

        JLabel lblTitle = new JLabel("Đăng Nhập");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblTitle);

        form.add(Box.createVerticalStrut(30));

        // --- Ô TÀI KHOẢN ---
        txtUsername = new RoundedTextField("Tài khoản", 20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsername.setMaximumSize(new Dimension(362, 40));
        txtUsername.setPreferredSize(new Dimension(362, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtUsername);

        form.add(Box.createVerticalStrut(15));

        // --- Ô MẬT KHẨU CÓ CON MẮT BÊN TRONG ---
        // --- Ô MẬT KHẨU CÓ CON MẮT BÊN TRONG ---
        txtPassword = new RoundedPasswordField("Mật khẩu", 20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setMaximumSize(new Dimension(362, 40));
        txtPassword.setPreferredSize(new Dimension(362, 40));
        txtPassword.setAlignmentX(Component.LEFT_ALIGNMENT);

        // 1. TẮT LAYOUT (Sử dụng tọa độ tuyệt đối)
        txtPassword.setLayout(null);

        // 2. Vẫn giữ padding 35px bên phải để chữ gõ không đâm xuyên qua con mắt
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                txtPassword.getBorder(),
                BorderFactory.createEmptyBorder(0, 0, 0, 35)));

        // 3. Khởi tạo icon mắt (Đồng bộ kích thước 24x24 cho đẹp)
        ImageIcon eyeIcon = loadIcon("/images/icon/hide.png");
        if (eyeIcon != null) {
            Image scaledEye = eyeIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            lblHidePassword = new JLabel(new ImageIcon(scaledEye));
        } else {
            lblHidePassword = new JLabel("👁");
            lblHidePassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        }
        lblHidePassword.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 4. SET TỌA ĐỘ THỦ CÔNG CHO CON MẮT (X, Y, Width, Height)
        // Chiều ngang ô là 362. Rộng icon 24. Cách lề phải 10px -> X = 362 - 24 - 10 =
        // 328
        // Chiều cao ô là 40. Cao icon 24. Căn giữa dọc -> Y = (40 - 24) / 2 = 8
        lblHidePassword.setBounds(328, 8, 24, 24);

        // 5. Thêm con mắt vào text field
        txtPassword.add(lblHidePassword);

        form.add(txtPassword);

        // --- QUÊN MẬT KHẨU ---
        JPanel forgotRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotRow.setOpaque(false);
        forgotRow.setMaximumSize(new Dimension(362, 25)); // Chỉnh lại cho bằng kích thước ô text
        forgotRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblForgotPassword = new JLabel("Quên mật khẩu?");
        lblForgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblForgotPassword.setForeground(COLOR_LINK);
        lblForgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotRow.add(lblForgotPassword);
        form.add(forgotRow);

        form.add(Box.createVerticalStrut(25));

        // --- NÚT ĐĂNG NHẬP ---
        btnLogin = new RoundedButton("Đăng Nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnLogin.setBackground(COLOR_PRIMARY);
        btnLogin.setMaximumSize(new Dimension(362, 50));
        btnLogin.setPreferredSize(new Dimension(362, 50));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(this);
        form.add(btnLogin);

        wrapper.add(form);
        return wrapper;
    }

    private void setupBasicUIEvents() {
        lblHidePassword.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isPasswordHidden) {
                    txtPassword.setEchoChar((char) 0);
                    lblHidePassword.setIcon(loadScaledIcon("/images/icon/view.png", 24, 24));
                } else {
                    txtPassword.setEchoChar('\u2022');
                    lblHidePassword.setIcon(loadScaledIcon("/images/icon/hide.png", 24, 24));
                }
                isPasswordHidden = !isPasswordHidden;
            }
        });

        // Thiết lập phím tắt Enter để kích hoạt nút đăng nhập
        getRootPane().setDefaultButton(btnLogin);
    }

    private ImageIcon loadScaledIcon(String path, int w, int h) {
        ImageIcon icon = loadIcon(path);
        if (icon != null) {
            Image scaled = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }

    private ImageIcon loadIcon(String path) {
        URL imgUrl = getClass().getResource(path);
        if (imgUrl != null)
            return new ImageIcon(imgUrl);
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnLogin)) {
            // 1. Lấy thông tin từ UI
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword());

            // 2. Kiểm tra rỗng
            if (username.isEmpty() || username.equals("Tài khoản") || password.isEmpty()
                    || password.equals("Mật khẩu")) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu!", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Gọi DAO xử lý
            TaiKhoanDAO dao = new TaiKhoanDAO();
            TaiKhoan tk = dao.timTheoMa(username);

            if (tk.getNhanVien().isTrangThai() == false) {
                JOptionPane.showMessageDialog(this, "Tài khoản đã bị khóa, vui lòng liên hệ quản lý", "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (tk != null) {
                String dbPassword = tk.getMatKhau();
                boolean isMatch = false;

                // Kiểm tra BCrypt hoặc so sánh thường
                if (dbPassword != null && dbPassword.startsWith("$2")) {
                    try {
                        isMatch = BCrypt.checkpw(password, dbPassword);
                    } catch (Exception ex) {
                        isMatch = false;
                    }
                } else {
                    isMatch = password.equals(dbPassword);
                }
                if (isMatch) {
                    SwingUtilities.invokeLater(() -> {
                        ThanhDieuHuongPanel mainFrame = new ThanhDieuHuongPanel(tk);
                        mainFrame.setVisible(true);
                    });
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Mật khẩu không chính xác!", "Lỗi đăng nhập",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Tài khoản không tồn tại!", "Lỗi đăng nhập",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
