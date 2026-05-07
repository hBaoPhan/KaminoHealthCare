package com.example.gui.screens;

import com.example.dao.NhanVienDAO;
import com.example.dao.TaiKhoanDAO;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.entity.enums.ChucVu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanPanel extends JPanel {

    private JTextField txtTimKiem;
    private JComboBox<String> cboLocVaiTro, cboLocTrangThai;
    private JTextField txtTenDangNhap, txtMatKhau;
    private JComboBox<String> cboNhanVien, cboVaiTro, cboTrangThai;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;
    private JTable tableTaiKhoan;
    private DefaultTableModel tableModel;
    private JLabel lblTongSoTaiKhoan;

    private NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private List<TaiKhoan> danhSachTaiKhoanFull = new ArrayList<>();

    public TaiKhoanPanel() {
        initComponents();
        initData();
        addEvents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // TOP PANEL
        JPanel pnlTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlTop.add(new JLabel("Tìm kiếm:"));
        txtTimKiem = new JTextField(20);
        pnlTop.add(txtTimKiem);
        pnlTop.add(new JLabel("Lọc theo:"));
        cboLocVaiTro = new JComboBox<>(new String[]{"Tất cả vai trò", "Dược sĩ", "Nhân viên quản lý"});
        pnlTop.add(cboLocVaiTro);
        cboLocTrangThai = new JComboBox<>(new String[]{"Tất cả trạng thái", "Đang hoạt động", "Bị khóa"});
        pnlTop.add(cboLocTrangThai);
        add(pnlTop, BorderLayout.NORTH);

        // LEFT PANEL (450px)
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setPreferredSize(new Dimension(500, 0));
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "THÔNG TIN TÀI KHOẢN", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14)));

        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 10, 25));
        pnlForm.setBorder(new EmptyBorder(30, 20, 30, 20));
        
        pnlForm.add(new JLabel("Tên đăng nhập:"));
        txtTenDangNhap = new JTextField();
        pnlForm.add(txtTenDangNhap);

        pnlForm.add(new JLabel("Mật khẩu:"));
        txtMatKhau = new JTextField();
        pnlForm.add(txtMatKhau);

        pnlForm.add(new JLabel("Nhân viên:"));
        cboNhanVien = new JComboBox<>();
        pnlForm.add(cboNhanVien);

        pnlForm.add(new JLabel("Vai trò:"));
        cboVaiTro = new JComboBox<>();
        pnlForm.add(cboVaiTro);

        pnlForm.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Đang hoạt động", "Bị khóa"});
        pnlForm.add(cboTrangThai);
        pnlLeft.add(pnlForm, BorderLayout.NORTH);

        // BUTTONS
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnThem = createButton("Thêm", new Color(28, 222, 222));
        btnSua = createButton("Sửa", new Color(255, 240, 100));
        btnXoa = createButton("Xóa", new Color(255, 80, 80));
        btnLamMoi = createButton("Làm mới", new Color(0, 191, 255));

        pnlButtons.add(btnThem);
        pnlButtons.add(btnSua);
        pnlButtons.add(btnXoa);
        pnlButtons.add(btnLamMoi);
        pnlLeft.add(pnlButtons, BorderLayout.SOUTH);
        add(pnlLeft, BorderLayout.WEST);

        // CENTER PANEL
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "DANH SÁCH TÀI KHOẢN", TitledBorder.LEFT, TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14)));

        String[] cols = {"Tên đăng nhập", "Tên nhân viên", "Vai trò", "Trạng thái"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tableTaiKhoan = new JTable(tableModel);
        tableTaiKhoan.setRowHeight(30);
        pnlCenter.add(new JScrollPane(tableTaiKhoan), BorderLayout.CENTER);

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTongSoTaiKhoan = new JLabel("Tổng cộng: 0 tài khoản");
        pnlBottom.add(lblTongSoTaiKhoan);
        pnlCenter.add(pnlBottom, BorderLayout.SOUTH);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setFocusPainted(false);
        return btn;
    }

    private void initData() {
        cboNhanVien.removeAllItems();
        List<NhanVien> dsNV = nhanVienDAO.layTatCa();
        for (NhanVien nv : dsNV) {
            cboNhanVien.addItem(nv.getMaNhanVien() + " - " + nv.getTenNhanVien());
        }
        cboVaiTro.addItem(ChucVu.DUOC_SI.getMoTa());
        cboVaiTro.addItem(ChucVu.NHAN_VIEN_QUAN_LY.getMoTa());
        loadDataToTable();
    }

    private void loadDataToTable() {
        danhSachTaiKhoanFull = taiKhoanDAO.layTatCa();
        locDuLieu();
    }

    private void addEvents() {
        txtTimKiem.addActionListener(e -> timKiemTaiKhoan());
        ActionListener locAction = e -> locDuLieu();
        cboLocVaiTro.addActionListener(locAction);
        cboLocTrangThai.addActionListener(locAction);

        tableTaiKhoan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { fillDataToForm(); }
        });

        btnThem.addActionListener(e -> themTaiKhoan());
        btnSua.addActionListener(e -> suaTaiKhoan());
        btnXoa.addActionListener(e -> xoaTaiKhoan());
        btnLamMoi.addActionListener(e -> lamMoiForm());
    }

    private void locDuLieu() {
        tableModel.setRowCount(0);
        String vLoc = cboLocVaiTro.getSelectedItem().toString();
        String tLoc = cboLocTrangThai.getSelectedItem().toString();
        int count = 0;
        for (TaiKhoan tk : danhSachTaiKhoanFull) {
            String vTK = tk.getNhanVien().getChucVu().getMoTa();
            String tTK = tk.getNhanVien().isTrangThai() ? "Đang hoạt động" : "Bị khóa";
            if ((vLoc.equals("Tất cả vai trò") || vLoc.equals(vTK)) &&
                (tLoc.equals("Tất cả trạng thái") || tLoc.equals(tTK))) {
                tableModel.addRow(new Object[]{tk.getTenDangNhap(), tk.getNhanVien().getTenNhanVien(), vTK, tTK});
                count++;
            }
        }
        lblTongSoTaiKhoan.setText("Tổng cộng: " + count + " tài khoản");
    }

    private void timKiemTaiKhoan() {
        String key = txtTimKiem.getText().trim().toLowerCase();
        if (key.isEmpty()) { loadDataToTable(); return; }
        for (int i = 0; i < tableTaiKhoan.getRowCount(); i++) {
            if (tableTaiKhoan.getValueAt(i, 0).toString().toLowerCase().equals(key)) {
                tableTaiKhoan.setRowSelectionInterval(i, i);
                fillDataToForm(); return;
            }
        }
        JOptionPane.showMessageDialog(this, "Không tìm thấy!");
    }

    private void fillDataToForm() {
        int row = tableTaiKhoan.getSelectedRow();
        if (row >= 0) {
            TaiKhoan tk = taiKhoanDAO.timTheoMa(tableTaiKhoan.getValueAt(row, 0).toString());
            txtTenDangNhap.setText(tk.getTenDangNhap());
            txtTenDangNhap.setEnabled(false);
            txtMatKhau.setText(tk.getMatKhau());
            cboNhanVien.setSelectedItem(tk.getNhanVien().getMaNhanVien() + " - " + tk.getNhanVien().getTenNhanVien());
            cboNhanVien.setEnabled(false);
            cboVaiTro.setSelectedItem(tk.getNhanVien().getChucVu().getMoTa());
            cboTrangThai.setSelectedItem(tk.getNhanVien().isTrangThai() ? "Đang hoạt động" : "Bị khóa");
        }
    }

    private void lamMoiForm() {
        txtTenDangNhap.setText(""); txtTenDangNhap.setEnabled(true);
        txtMatKhau.setText(""); cboNhanVien.setEnabled(true);
        tableTaiKhoan.clearSelection();
        loadDataToTable();
    }

    private void themTaiKhoan() {
        String dn = txtTenDangNhap.getText().trim();
        if (dn.isEmpty() || taiKhoanDAO.timTheoMa(dn) != null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập không hợp lệ hoặc đã tồn tại!"); return;
        }
        String maNV = cboNhanVien.getSelectedItem().toString().split(" - ")[0];
        for (TaiKhoan tk : danhSachTaiKhoanFull) {
            if (tk.getNhanVien().getMaNhanVien().equals(maNV)) {
                JOptionPane.showMessageDialog(this, "Nhân viên này đã có tài khoản!"); return;
            }
        }
        if (taiKhoanDAO.them(new TaiKhoan(dn, txtMatKhau.getText(), new NhanVien(maNV)))) {
            loadDataToTable(); lamMoiForm();
        }
    }

    private void suaTaiKhoan() {
        if (tableTaiKhoan.getSelectedRow() < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Xác nhận sửa?", "Sửa", 0) == 0) {
            TaiKhoan tk = new TaiKhoan(txtTenDangNhap.getText(), txtMatKhau.getText(), null);
            taiKhoanDAO.capNhat(tk);
            NhanVien nv = nhanVienDAO.timTheoMa(cboNhanVien.getSelectedItem().toString().split(" - ")[0]);
            nv.setTrangThai(cboTrangThai.getSelectedItem().equals("Đang hoạt động"));
            nhanVienDAO.capNhat(nv);
            loadDataToTable(); lamMoiForm();
        }
    }

    private void xoaTaiKhoan() {
        if (tableTaiKhoan.getSelectedRow() < 0) return;
        if (JOptionPane.showConfirmDialog(this, "Xóa tài khoản này?", "Xóa", 0) == 0) {
            taiKhoanDAO.xoa(tableTaiKhoan.getValueAt(tableTaiKhoan.getSelectedRow(), 0).toString());
            loadDataToTable(); lamMoiForm();
        }
    }
}