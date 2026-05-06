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

    // Components Tìm kiếm & Lọc
    private JTextField txtTimKiem;
    private JComboBox<String> cboLocVaiTro, cboLocTrangThai;

    // Components Form bên trái
    private JTextField txtTenDangNhap, txtMatKhau;
    private JComboBox<String> cboNhanVien, cboVaiTro, cboTrangThai;
    private JButton btnThem, btnCapNhat, btnKhoaTK, btnLamMoi;

    // Components Bảng bên phải
    private JTable tableTaiKhoan;
    private DefaultTableModel tableModel;
    private JLabel lblTongSoTaiKhoan;

    // DAO & Dữ liệu
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

        // ================= TOP PANEL (Tìm kiếm & Lọc) =================
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

        // ================= LEFT PANEL (Thông tin tài khoản) =================
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setPreferredSize(new Dimension(350, 0));
        pnlLeft.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY), 
                "THÔNG TIN TÀI KHOẢN", TitledBorder.CENTER, TitledBorder.TOP, 
                new Font("Arial", Font.BOLD, 14)));

        JPanel pnlForm = new JPanel(new GridLayout(5, 2, 10, 20));
        pnlForm.setBorder(new EmptyBorder(20, 10, 20, 10));
        
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

        // ================= BUTTONS =================
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnThem = createButton("Thêm", new Color(0, 191, 255));
        btnCapNhat = createButton("Cập nhật", new Color(255, 215, 0));
        btnKhoaTK = createButton("Khóa TK", new Color(255, 69, 0));
        btnLamMoi = createButton("Làm mới", new Color(50, 205, 50)); 

        pnlButtons.add(btnThem);
        pnlButtons.add(btnCapNhat);
        pnlButtons.add(btnKhoaTK);
        pnlButtons.add(btnLamMoi);
        pnlLeft.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlLeft, BorderLayout.WEST);

        // ================= CENTER PANEL (Danh sách tài khoản) =================
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
        tableTaiKhoan.setRowHeight(25);
        tableTaiKhoan.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        pnlCenter.add(new JScrollPane(tableTaiKhoan), BorderLayout.CENTER);

        // Label tổng số
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTongSoTaiKhoan = new JLabel("Tổng cộng: 0 tài khoản");
        lblTongSoTaiKhoan.setFont(new Font("Arial", Font.ITALIC, 12));
        pnlBottom.add(lblTongSoTaiKhoan);
        pnlCenter.add(pnlBottom, BorderLayout.SOUTH);

        add(pnlCenter, BorderLayout.CENTER);
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        if(bg.equals(new Color(255, 215, 0))) btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        return btn;
    }

    // ================= KHỞI TẠO DỮ LIỆU =================
    private void initData() {
        // 1. Load tất cả nhân viên của hệ thống vào ComboBox
        cboNhanVien.removeAllItems();
        List<NhanVien> dsNV = nhanVienDAO.layTatCa();
        for (NhanVien nv : dsNV) {
            cboNhanVien.addItem(nv.getMaNhanVien() + " - " + nv.getTenNhanVien());
        }

        // 2. Load ComboBox Vai trò
        cboVaiTro.removeAllItems();
        cboVaiTro.addItem(ChucVu.DUOC_SI.getMoTa());
        cboVaiTro.addItem(ChucVu.NHAN_VIEN_QUAN_LY.getMoTa());

        // 3. Load danh sách tài khoản từ DB lên
        loadDataToTable();
    }

    private void loadDataToTable() {
        danhSachTaiKhoanFull = taiKhoanDAO.layTatCa();
        locDuLieu(); // Gọi bộ lọc mặc định để hiển thị
    }

    // ================= XỬ LÝ SỰ KIỆN =================
    private void addEvents() {
        // Tìm kiếm bằng phím Enter
        txtTimKiem.addActionListener(e -> timKiemTaiKhoan());

        // Thay đổi ComboBox bất kỳ sẽ thực hiện lọc
        ActionListener locListener = e -> locDuLieu();
        cboLocVaiTro.addActionListener(locListener);
        cboLocTrangThai.addActionListener(locListener);

        // Click 1 dòng trong danh sách tài khoản
        tableTaiKhoan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                fillDataToForm();
            }
        });

        // Các nút chức năng
        btnThem.addActionListener(e -> themTaiKhoan());
        btnCapNhat.addActionListener(e -> capNhatTaiKhoan());
        btnKhoaTK.addActionListener(e -> khoaTaiKhoan());
        btnLamMoi.addActionListener(e -> lamMoiForm());
    }

    // ================= LOGIC CHI TIẾT =================

    // Xử lý: Lọc theo 2 ComboBox
    private void locDuLieu() {
        tableModel.setRowCount(0);
        String vaiTroLoc = cboLocVaiTro.getSelectedItem().toString();
        String trangThaiLoc = cboLocTrangThai.getSelectedItem().toString();

        int count = 0;
        for (TaiKhoan tk : danhSachTaiKhoanFull) {
            String vaiTroTK = tk.getNhanVien().getChucVu().getMoTa();
            String trangThaiTK = tk.getNhanVien().isTrangThai() ? "Đang hoạt động" : "Bị khóa";

            boolean matchVaiTro = vaiTroLoc.equals("Tất cả vai trò") || vaiTroLoc.equals(vaiTroTK);
            boolean matchTrangThai = trangThaiLoc.equals("Tất cả trạng thái") || trangThaiLoc.equals(trangThaiTK);

            // Kết quả của phép lọc được hiển thị ở bảng
            if (matchVaiTro && matchTrangThai) {
                tableModel.addRow(new Object[]{
                        tk.getTenDangNhap(),
                        tk.getNhanVien().getTenNhanVien(),
                        vaiTroTK,
                        trangThaiTK
                });
                count++;
            }
        }
        // Hiển thị tổng số tài khoản góc dưới bên phải
        lblTongSoTaiKhoan.setText("Tổng cộng: " + count + " tài khoản");
    }

    // Xử lý: Tìm kiếm Enter
    private void timKiemTaiKhoan() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadDataToTable();
            return;
        }

        cboLocVaiTro.setSelectedIndex(0);
        cboLocTrangThai.setSelectedIndex(0);
        
        for (int i = 0; i < tableTaiKhoan.getRowCount(); i++) {
            String tenDN = tableTaiKhoan.getValueAt(i, 0).toString().toLowerCase();
            if (tenDN.equals(keyword)) {
                // Tô đen dòng có tài khoản tìm thấy
                tableTaiKhoan.setRowSelectionInterval(i, i);
                tableTaiKhoan.scrollRectToVisible(tableTaiKhoan.getCellRect(i, 0, true));
                
                // Hiển thị thông tin lên form bên trái
                fillDataToForm();
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản: " + keyword);
    }

    // Xử lý: Click Table -> Fill Form
    private void fillDataToForm() {
        int row = tableTaiKhoan.getSelectedRow();
        if (row >= 0) {
            String tenDN = tableTaiKhoan.getValueAt(row, 0).toString();
            TaiKhoan tk = taiKhoanDAO.timTheoMa(tenDN);

            if (tk != null) {
                txtTenDangNhap.setText(tk.getTenDangNhap());
                txtTenDangNhap.setEnabled(false); // Đã chọn thì khóa không cho sửa Tên Đăng Nhập
                txtMatKhau.setText(tk.getMatKhau());
                
                String nvStr = tk.getNhanVien().getMaNhanVien() + " - " + tk.getNhanVien().getTenNhanVien();
                cboNhanVien.setSelectedItem(nvStr);
                cboNhanVien.setEnabled(false); // Khóa không cho đổi nhân viên

                cboVaiTro.setSelectedItem(tk.getNhanVien().getChucVu().getMoTa());
                cboVaiTro.setEnabled(false); // Vai trò thuộc tính chất của nhân viên nên chỉ hiển thị

                cboTrangThai.setSelectedItem(tk.getNhanVien().isTrangThai() ? "Đang hoạt động" : "Bị khóa");
            }
        }
    }

    // Xử lý: Nút Làm mới (Xóa rỗng)
    private void lamMoiForm() {
        txtTenDangNhap.setText("");
        txtTenDangNhap.setEnabled(true);
        txtMatKhau.setText("");
        
        cboNhanVien.setEnabled(true);
        cboVaiTro.setEnabled(true);
        if(cboNhanVien.getItemCount() > 0) cboNhanVien.setSelectedIndex(0);
        if(cboVaiTro.getItemCount() > 0) cboVaiTro.setSelectedIndex(0);
        if(cboTrangThai.getItemCount() > 0) cboTrangThai.setSelectedIndex(0);
        
        tableTaiKhoan.clearSelection();
        txtTimKiem.setText("");
        cboLocVaiTro.setSelectedIndex(0);
        cboLocTrangThai.setSelectedIndex(0);
        loadDataToTable();
    }

    // Xử lý: Nút Thêm mới
    private void themTaiKhoan() {
        String tenDN = txtTenDangNhap.getText().trim();
        String matKhau = txtMatKhau.getText().trim();
        
        if (tenDN.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Tên đăng nhập và Mật khẩu!");
            return;
        }

        if (taiKhoanDAO.timTheoMa(tenDN) != null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!");
            return;
        }

        String selectedNV = cboNhanVien.getSelectedItem().toString();
        String maNV = selectedNV.split(" - ")[0];

        // YÊU CẦU: Ràng buộc mỗi tài khoản tương ứng với 1 nhân viên thôi
        for (TaiKhoan tk : danhSachTaiKhoanFull) {
            if (tk.getNhanVien().getMaNhanVien().equals(maNV)) {
                JOptionPane.showMessageDialog(this, "Nhân viên này đã được cấp tài khoản hệ thống rồi!");
                return;
            }
        }

        TaiKhoan tkMoi = new TaiKhoan(tenDN, matKhau, new NhanVien(maNV));
        if (taiKhoanDAO.them(tkMoi)) {
            JOptionPane.showMessageDialog(this, "Thêm tài khoản mới thành công!");
            loadDataToTable();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi thêm tài khoản!");
        }
    }

    // Xử lý: Nút Cập nhật
    private void capNhatTaiKhoan() {
        int row = tableTaiKhoan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản từ danh sách để cập nhật!");
            return;
        }

        // YÊU CẦU: Nhớ hỏi xác nhận khi click vào nút trước khi cập nhật
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận cập nhật thông tin tài khoản này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String tenDN = txtTenDangNhap.getText().trim();
            String matKhauMoi = txtMatKhau.getText().trim();
            boolean trangThaiMoi = cboTrangThai.getSelectedItem().toString().equals("Đang hoạt động");

            // Chức năng đổi mật khẩu
            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(tenDN);
            tk.setMatKhau(matKhauMoi);
            taiKhoanDAO.capNhat(tk);

            // Chức năng vô hiệu hóa tài khoản bằng cách set trạng thái hoạt động thành bị khóa
            String maNV = danhSachTaiKhoanFull.stream()
                            .filter(t -> t.getTenDangNhap().equals(tenDN))
                            .findFirst().get().getNhanVien().getMaNhanVien();
                            
            NhanVien nv = nhanVienDAO.timTheoMa(maNV);
            nv.setTrangThai(trangThaiMoi);
            nhanVienDAO.capNhat(nv);

            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadDataToTable();
            lamMoiForm();
        }
    }

    // Xử lý: Nút Khóa TK
    private void khoaTaiKhoan() {
        int row = tableTaiKhoan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần khóa!");
            return;
        }

        // YÊU CẦU: Nhớ hỏi xác nhận
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn KHÓA tài khoản này?", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            String tenDN = tableTaiKhoan.getValueAt(row, 0).toString();
            String maNV = danhSachTaiKhoanFull.stream()
                            .filter(t -> t.getTenDangNhap().equals(tenDN))
                            .findFirst().get().getNhanVien().getMaNhanVien();

            NhanVien nv = nhanVienDAO.timTheoMa(maNV);
            // YÊU CẦU: Set trạng thái hoạt động thành bị khóa
            nv.setTrangThai(false); 
            nhanVienDAO.capNhat(nv);
            
            JOptionPane.showMessageDialog(this, "Đã khóa tài khoản!");
            loadDataToTable();
            lamMoiForm();
        }
    }
}