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
import javax.swing.JOptionPane;
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
    private JTextField txtSearch, txtId, txtTen, txtSdt, txtCccd;
    private JComboBox<String> cbChucVuFilter, cbChucVu, cbTrangThai;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongSo;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    // Biến kết nối Database
    private com.example.dao.NhanVienDAO nvDAO = new com.example.dao.NhanVienDAO();
    private java.util.List<com.example.entity.NhanVien> dsNhanVien = new java.util.ArrayList<>();
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
        taiLaiDanhSach();
        setupListeners();
    }

   private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topBar.setBackground(Color.WHITE);

        txtSearch = new JTextField("  Tìm theo tên...", 25);
        txtSearch.setPreferredSize(new Dimension(250, 35));

        cbChucVuFilter = new JComboBox<>(new String[] { "Tất cả vai trò", "Dược sĩ", "Nhân viên quản lý" });
        cbChucVuFilter.setPreferredSize(new Dimension(150, 35));
        cbChucVuFilter.setBackground(Color.WHITE);

        topBar.add(txtSearch);
        topBar.add(cbChucVuFilter);
        return topBar;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), "THÔNG TIN NHÂN VIÊN");
        titledBorder.setTitleJustification(TitledBorder.CENTER);
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        formPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(20, 30, 20, 30)));

        // Sửa thành 6 hàng để thêm trường Trạng thái
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 25));
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Mã nhân viên:"));
        txtId = new JTextField();
        txtId.setEnabled(false); 
        txtId.setBackground(new Color(230, 230, 230));
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Tên nhân viên:"));
        inputPanel.add(txtTen = new JTextField());

        inputPanel.add(new JLabel("Số điện thoại:"));
        inputPanel.add(txtSdt = new JTextField());

        inputPanel.add(new JLabel("CCCD:"));
        inputPanel.add(txtCccd = new JTextField());

        inputPanel.add(new JLabel("Chức vụ:"));
        inputPanel.add(cbChucVu = new JComboBox<>(new String[] { "Dược sĩ", "Nhân viên quản lý" }));

        inputPanel.add(new JLabel("Trạng thái:"));
        inputPanel.add(cbTrangThai = new JComboBox<>(new String[] { "Đang hoạt động", "Bị khóa" }));

        formPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(30, 0, 0, 0));

        buttonPanel.add(btnThem = createColorButton("Thêm", new Color(21, 215, 221)));
        buttonPanel.add(btnSua = createColorButton("Sửa", new Color(255, 243, 108)));
        buttonPanel.add(btnXoa = createColorButton("Xóa", new Color(255, 89, 89)));
        buttonPanel.add(btnLamMoi = createColorButton("Làm mới", new Color(0, 213, 255)));

        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), "DANH SÁCH NHÂN VIÊN");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        tableContainer.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        String[] columns = { "Mã nhân viên", "Tên nhân viên", "Số điện thoại", "CCCD", "Chức vụ", "Trạng thái" };
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Khóa bảng không cho sửa gõ trực tiếp
        };
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        // Thêm label đếm tổng số
        lblTongSo = new JLabel("Tổng số nhân viên: 0");
        lblTongSo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblTongSo.setBorder(new EmptyBorder(5, 0, 0, 0));
        tableContainer.add(lblTongSo, BorderLayout.SOUTH);

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

   // ==========================================
    // PHẦN LOGIC: SỰ KIỆN VÀ THAO TÁC DATABASE
    // ==========================================
    private void setupListeners() {
        // 1. Click vào bảng hiện lên form
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(model.getValueAt(row, 0).toString());
                    txtTen.setText(model.getValueAt(row, 1).toString());
                    txtSdt.setText(model.getValueAt(row, 2).toString());
                    txtCccd.setText(model.getValueAt(row, 3).toString());
                    cbChucVu.setSelectedItem(model.getValueAt(row, 4).toString());
                    cbTrangThai.setSelectedItem(model.getValueAt(row, 5).toString());
                }
            }
        });

        // 2. Lọc bằng ComboBox
        cbChucVuFilter.addActionListener(e -> locVaTimKiem());

        // 3. Tìm kiếm theo chữ (Live search)
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { locVaTimKiem(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { locVaTimKiem(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { locVaTimKiem(); }
        });
        
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().trim().equals("Tìm theo tên...")) { txtSearch.setText(""); }
            }
        });

        // 4. Nhấn Enter tô đen dòng tìm thấy
        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER && table.getRowCount() > 0) {
                    table.setRowSelectionInterval(0, 0); // Bôi đen dòng đầu
                    table.dispatchEvent(new MouseEvent(table, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false)); // Giả lập click
                }
            }
        });

        // 5. Gắn sự kiện cho nút
        btnLamMoi.addActionListener(e -> lamMoiForm());
        btnThem.addActionListener(e -> themNhanVien());
        btnSua.addActionListener(e -> suaNhanVien());
        btnXoa.addActionListener(e -> khoaNhanVien());
        
        // Tự đổi mã khi chọn chức vụ (Lúc thêm mới)
        cbChucVu.addActionListener(e -> {
            if(table.getSelectedRow() == -1) phatSinhMaTuDong();
        });
    }

    public void taiLaiDanhSach() {
        dsNhanVien = nvDAO.layTatCa();
        locVaTimKiem();
        lamMoiForm();
    }

    private void locVaTimKiem() {
        String keyword = txtSearch.getText().toLowerCase().trim();
        if (keyword.equals("tìm theo tên...")) keyword = "";
        String roleFilter = cbChucVuFilter.getSelectedItem().toString();

        model.setRowCount(0);
        int count = 0;
        for (com.example.entity.NhanVien nv : dsNhanVien) {
            String chucVuUI = nv.getChucVu() == com.example.entity.enums.ChucVu.DUOC_SI ? "Dược sĩ" : "Nhân viên quản lý";
            String trangThaiUI = nv.isTrangThai() ? "Đang hoạt động" : "Bị khóa";

            boolean matchRole = roleFilter.equals("Tất cả vai trò") || roleFilter.equals(chucVuUI);
            boolean matchName = nv.getTenNhanVien().toLowerCase().contains(keyword);

            if (matchRole && matchName) {
                model.addRow(new Object[]{ nv.getMaNhanVien(), nv.getTenNhanVien(), nv.getSdt(), nv.getCccd(), chucVuUI, trangThaiUI });
                count++;
            }
        }
        lblTongSo.setText("Tổng số nhân viên: " + count);
    }

    private void lamMoiForm() {
        txtSearch.setText("  Tìm theo tên...");
        txtTen.setText(""); txtSdt.setText(""); txtCccd.setText("");
        cbChucVu.setSelectedIndex(0); cbTrangThai.setSelectedIndex(0); cbChucVuFilter.setSelectedIndex(0);
        table.clearSelection();
        phatSinhMaTuDong();
    }

    private void phatSinhMaTuDong() {
        String prefix = cbChucVu.getSelectedIndex() == 0 ? "DS" : "QL";
        int maxId = 0;
        for (com.example.entity.NhanVien nv : dsNhanVien) {
            if (nv.getMaNhanVien().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(nv.getMaNhanVien().substring(2));
                    if (num > maxId) maxId = num;
                } catch (Exception e) {}
            }
        }
        txtId.setText(String.format("%s%03d", prefix, maxId + 1));
    }

    private void themNhanVien() {
        if (txtTen.getText().trim().isEmpty() || txtSdt.getText().trim().isEmpty() || txtCccd.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!"); return;
        }
        com.example.entity.NhanVien nv = new com.example.entity.NhanVien();
        nv.setMaNhanVien(txtId.getText());
        nv.setTenNhanVien(txtTen.getText().trim());
        nv.setSdt(txtSdt.getText().trim());
        nv.setCccd(txtCccd.getText().trim());
        nv.setChucVu(cbChucVu.getSelectedIndex() == 0 ? com.example.entity.enums.ChucVu.DUOC_SI : com.example.entity.enums.ChucVu.NHAN_VIEN_QUAN_LY);
        nv.setTrangThai(cbTrangThai.getSelectedIndex() == 0);

        if (nvDAO.them(nv)) {
            // Tự sinh tài khoản 
            String username = (cbChucVu.getSelectedIndex() == 0 ? "duocsi_" : "admin_") + nv.getMaNhanVien();
            try {
                java.sql.Connection con = com.example.connectDB.ConnectDB.getConnection();
                java.sql.PreparedStatement pst = con.prepareStatement("INSERT INTO TaiKhoan (tenDangNhap, matKhau, maNhanVien) VALUES (?, ?, ?)");
                pst.setString(1, username); pst.setString(2, "123456"); pst.setString(3, nv.getMaNhanVien());
                pst.executeUpdate();
            } catch (Exception ex) { ex.printStackTrace(); }
            
            JOptionPane.showMessageDialog(this, "Thêm nhân viên và tạo tài khoản (" + username + ") thành công!");
            taiLaiDanhSach();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi! CCCD có thể bị trùng lặp.");
        }
    }

    private void suaNhanVien() {
        if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên từ bảng!"); return; }
        if (JOptionPane.showConfirmDialog(this, "Xác nhận cập nhật?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            com.example.entity.NhanVien nv = new com.example.entity.NhanVien();
            nv.setMaNhanVien(txtId.getText());
            nv.setTenNhanVien(txtTen.getText().trim());
            nv.setSdt(txtSdt.getText().trim());
            nv.setCccd(txtCccd.getText().trim());
            nv.setChucVu(cbChucVu.getSelectedIndex() == 0 ? com.example.entity.enums.ChucVu.DUOC_SI : com.example.entity.enums.ChucVu.NHAN_VIEN_QUAN_LY);
            nv.setTrangThai(cbTrangThai.getSelectedIndex() == 0);

            if (nvDAO.capNhat(nv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!"); taiLaiDanhSach();
            } else { JOptionPane.showMessageDialog(this, "Cập nhật thất bại!"); }
        }
    }

    private void khoaNhanVien() {
        if (table.getSelectedRow() == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!"); return; }
        if (txtId.getText().equals("QL001")) { JOptionPane.showMessageDialog(this, "Cảnh báo: Không thể khóa tài khoản Quản lý gốc!"); return; }
        
        if (JOptionPane.showConfirmDialog(this, "Bạn muốn khóa (xóa) nhân viên này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            com.example.entity.NhanVien nv = nvDAO.timTheoMa(txtId.getText());
            nv.setTrangThai(false);
            if (nvDAO.capNhat(nv)) {
                JOptionPane.showMessageDialog(this, "Đã khóa thành công!"); taiLaiDanhSach();
            }
        }
    }
}