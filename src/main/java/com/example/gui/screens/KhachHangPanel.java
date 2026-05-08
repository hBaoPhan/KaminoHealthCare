package com.example.gui.screens;

import com.example.dao.KhachHangDAO;
import com.example.entity.KhachHang;
import com.example.entity.enums.TrangThaiKhachHang;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class KhachHangPanel extends JPanel {

    // =========================================================================
    // VÙNG 1: KHAI BÁO BIẾN (UI COMPONENTS & DATA)
    // =========================================================================

    // --- Khai báo các thành phần giao diện ---
    private JTextField txtSearch, txtId, txtTen, txtSdt;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTongSo;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    // --- Khai báo biến xử lý Database và Dữ liệu ---
    private KhachHangDAO khDAO = new KhachHangDAO();
    private List<KhachHang> dsKhachHang = new ArrayList<>();


    // =========================================================================
    // VÙNG 2: HÀM KHỞI TẠO (CONSTRUCTOR)
    // =========================================================================

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
        
        // 4. Load dữ liệu và cài đặt các sự kiện
        taiLaiDanhSach();
        setupListeners();
    }


    // =========================================================================
    // VÙNG 3: KHỞI TẠO GIAO DIỆN (UI BUILDING)
    // =========================================================================

    /** Tạo thanh công cụ tìm kiếm và lọc phía trên */
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topBar.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setToolTipText("Nhập tên hoặc số điện thoại...");

        topBar.add(new JLabel("Tìm kiếm: "));
        topBar.add(txtSearch);
        return topBar;
    }

    /** Tạo Form nhập liệu thông tin khách hàng (Bên trái) */
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
        txtId = new JTextField();
        txtId.setEnabled(false);
        inputPanel.add(txtId);

        inputPanel.add(new JLabel("Tên khách hàng:"));
        inputPanel.add(txtTen = new JTextField()); // Đã gán vào biến txtTen

        inputPanel.add(new JLabel("Số điện thoại:"));
        inputPanel.add(txtSdt = new JTextField());

        formPanel.add(inputPanel, BorderLayout.NORTH);

        // Panel chứa các nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(50, 0, 0, 0));

        btnThem = createColorButton("Thêm", new Color(21, 215, 221));
        btnSua = createColorButton("Cập nhật", new Color(255, 243, 108));
        btnXoa = createColorButton("Xóa", new Color(255, 89, 89));
        btnLamMoi = createColorButton("Làm mới", new Color(0, 213, 255));
        buttonPanel.add(btnThem);
        buttonPanel.add(btnSua);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLamMoi);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);

        return formPanel;
    }

    /** Tạo bảng danh sách khách hàng (Bên phải) */
    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true), "DANH SÁCH KHÁCH HÀNG");
        titledBorder.setTitleFont(new Font("Segoe UI", Font.BOLD, 18));
        tableContainer.setBorder(BorderFactory.createCompoundBorder(titledBorder, new EmptyBorder(10, 10, 10, 10)));

        String[] columns = { "Mã KH", "Tên khách hàng", "Số điện thoại", "KH Thành viên" };
        model = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);

        lblTongSo = new JLabel("Tổng số khách hàng: 0");
        lblTongSo.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblTongSo.setBorder(new EmptyBorder(5, 0, 0, 0));
        lblTongSo.setHorizontalAlignment(SwingConstants.RIGHT);
        tableContainer.add(lblTongSo, BorderLayout.SOUTH);

        return tableContainer;
    }


    // =========================================================================
    // VÙNG 4: SỰ KIỆN & LOAD DỮ LIỆU (EVENTS & DATA BINDING)
    // =========================================================================

    /** Cài đặt các bộ lắng nghe sự kiện (Click, Nhập liệu,...) */
    private void setupListeners() {
        // Sự kiện click chọn dòng trên bảng
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    txtId.setText(model.getValueAt(row, 0).toString());
                    txtTen.setText(model.getValueAt(row, 1).toString());
                    txtSdt.setText(model.getValueAt(row, 2).toString());
                }
            }
        });

        // Sự kiện gõ phím tìm kiếm (Live Search)
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { locVaTimKiem(); }
            public void removeUpdate(DocumentEvent e) { locVaTimKiem(); }
            public void changedUpdate(DocumentEvent e) { locVaTimKiem(); }
        });

        // Sự kiện nhấn Enter trên ô tìm kiếm
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (table.getRowCount() > 0) {
                        table.setRowSelectionInterval(0, 0); 
                        table.dispatchEvent(new MouseEvent(table, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false));
                    } else {
                        JOptionPane.showMessageDialog(KhachHangPanel.this, "Không tìm thấy khách hàng này!");
                    }
                }
            }
        });

        // Sự kiện các nút chức năng
        btnLamMoi.addActionListener(e -> lamMoiForm());
        btnThem.addActionListener(e -> themKhachHang());
        btnSua.addActionListener(e -> suaKhachHang());
        btnXoa.addActionListener(e -> xoaKhachHang());
    }

    /** Lấy dữ liệu mới nhất từ CSDL và hiển thị lại lên bảng */
    public void taiLaiDanhSach() {
        dsKhachHang = khDAO.layTatCa();
        locVaTimKiem();
        lamMoiForm();
    }

    /** Xử lý logic lọc khách hàng theo chữ tìm kiếm và trạng thái */
    private void locVaTimKiem() {
        String keyword = txtSearch.getText().toLowerCase().trim();

        model.setRowCount(0); 
        int count = 0;

        for (KhachHang kh : dsKhachHang) {
            // LOGIC ẨN KHÁCH HÀNG: Nếu tên có chữ [ĐÃ XÓA] thì bỏ qua, không vẽ lên bảng
            if (kh.getTenKhachHang().startsWith("[ĐÃ XÓA]")) {
                continue;
            }

            String khtvUI = kh.getTrangThai() == TrangThaiKhachHang.KHACH_HANG_THANH_VIEN ? "Có" : "Không";
            
            boolean matchRole = kh.getTrangThai() == TrangThaiKhachHang.KHACH_HANG_THANH_VIEN;
            
            boolean matchKeyword = kh.getTenKhachHang().toLowerCase().contains(keyword) 
                                || (kh.getSdt() != null && kh.getSdt().contains(keyword));

            if (matchRole && matchKeyword) {
                model.addRow(new Object[]{
                    kh.getMaKhachHang(), kh.getTenKhachHang(), kh.getSdt(), khtvUI
                });
                count++;
            }
        }
        lblTongSo.setText("Tổng số khách hàng: " + count);
    }

    /** Xóa trắng form nhập liệu để chuẩn bị nhập mới */
    private void lamMoiForm() {
        txtSearch.setText("");
        txtTen.setText("");
        txtSdt.setText("");
        table.clearSelection();
        phatSinhMaTuDong();
        txtTen.requestFocus();
    }


    // =========================================================================
    // VÙNG 5: LOGIC NGHIỆP VỤ CHÍNH (CORE CRUD LOGIC)
    // =========================================================================

    /** Thêm khách hàng mới vào hệ thống */
    private void themKhachHang() {
        if (!validateData()) return;
        
        String sdt = txtSdt.getText().trim();
        KhachHang khCheck = khDAO.timTheoSdt(sdt);
        if (khCheck != null) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã tồn tại trong hệ thống (Thuộc về: " + khCheck.getTenKhachHang() + ")!");
            return;
        }

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(txtId.getText());
        kh.setTenKhachHang(txtTen.getText().trim());
        kh.setSdt(txtSdt.getText().trim());
        
        // Mặc định luôn là khách hàng thành viên khi thêm mới
        kh.setTrangThai(TrangThaiKhachHang.KHACH_HANG_THANH_VIEN); 

        if (khDAO.them(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành viên thành công!");
            taiLaiDanhSach();
        }
    }

    /** Cập nhật thông tin khách hàng đang được chọn */
    private void suaKhachHang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật từ bảng!");
            return;
        }
        
        if (txtId.getText().equals("KL_LE")) {
            JOptionPane.showMessageDialog(this, "Không thể cập nhật Khách Lẻ mặc định của hệ thống!");
            return;
        }

        if (!validateData()) return;

        String sdtMoi = txtSdt.getText().trim();
        KhachHang khCheck = khDAO.timTheoSdt(sdtMoi);
        if (khCheck != null && !khCheck.getMaKhachHang().equals(txtId.getText())) {
             JOptionPane.showMessageDialog(this, "Số điện thoại này đang được sử dụng bởi khách hàng khác!");
             return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn cập nhật thông tin khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            KhachHang kh = new KhachHang();
            kh.setMaKhachHang(txtId.getText());
            kh.setTenKhachHang(txtTen.getText().trim());
            kh.setSdt(sdtMoi);
            
            // Giữ nguyên trạng thái (Thành viên / Khách lẻ) cũ từ CSDL
            KhachHang khCu = khDAO.timTheoMa(txtId.getText());
            kh.setTrangThai(khCu.getTrangThai());

            if (khDAO.capNhat(kh)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                taiLaiDanhSach();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        }
    }

    /** Ẩn khách hàng (chuyển sang khách lẻ và đánh dấu đã xóa) */
    private void xoaKhachHang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần ẩn!");
            return;
        }
        
        String maKH = txtId.getText();
        // Không cho phép ẩn khách lẻ mặc định của hệ thống
        if (maKH.equals("KL_LE")) {
            JOptionPane.showMessageDialog(this, "Không thể thao tác trên Khách lẻ mặc định!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn ẩn khách hàng này?\n(Khách hàng sẽ bị chuyển thành Khách lẻ và ẩn khỏi danh sách hiển thị)", 
            "Xác nhận ẩn khách hàng", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Lấy đối tượng khách hàng từ database để cập nhật
            KhachHang kh = khDAO.timTheoMa(maKH);
            if (kh != null) {
                // 1. Đánh dấu tên để hàm lọc locVaTimKiem() tự động ẩn khỏi bảng
                kh.setTenKhachHang("[ĐÃ XÓA] " + kh.getTenKhachHang()); 
                
                // 2. Chuyển trạng thái sang Khách lẻ theo yêu cầu của bạn
                kh.setTrangThai(TrangThaiKhachHang.KHACH_LE); 
                
                // 3. Gọi hàm cập nhật để lưu thay đổi vào SQL
                if (khDAO.capNhat(kh)) {
                    JOptionPane.showMessageDialog(this, "Đã ẩn và chuyển trạng thái khách hàng thành công!");
                    taiLaiDanhSach(); // Load lại danh sách để cập nhật giao diện
                } else {
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật dữ liệu!");
                }
            }
        }
    }


    // =========================================================================
    // VÙNG 6: CÁC HÀM HỖ TRỢ (HELPER LOGIC)
    // =========================================================================

    /** Tự động sinh mã khách hàng tiếp theo (VD: TV000005) */
    private void phatSinhMaTuDong() {
        int maxId = 0;
        for (KhachHang kh : dsKhachHang) {
            if (kh.getMaKhachHang().startsWith("TV")) {
                try {
                    int num = Integer.parseInt(kh.getMaKhachHang().substring(2));
                    if (num > maxId) maxId = num;
                } catch (Exception e) {}
            }
        }
        txtId.setText(String.format("TV%06d", maxId + 1)); 
    }

    /** Kiểm tra tính hợp lệ của dữ liệu trước khi thêm/sửa */
    private boolean validateData() {
        if (txtTen.getText().trim().isEmpty() || txtSdt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ Tên và Số điện thoại!");
            return false;
        }
        if (!txtSdt.getText().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải bao gồm đúng 10 chữ số!");
            return false;
        }
        return true;
    }

    /** Hàm tiện ích tạo nút bấm có màu nền và hiệu ứng Hover */
    private JButton createColorButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(bgColor);
        btn.setForeground(Color.BLACK);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(100, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bgColor.darker());
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }
}