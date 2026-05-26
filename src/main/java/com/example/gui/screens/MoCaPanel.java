package com.example.gui.screens;

import com.example.entity.CaLam;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.entity.enums.TrangThaiCaLam;
import com.example.service.CaLamService;
import com.example.gui.components.RoundedButton;
import com.example.gui.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MoCaPanel extends JPanel implements ActionListener {

    private final TaiKhoan taiKhoan;
    private final NhanVien nhanVien;
    private final CaLamService caLamService = new CaLamService();

    // Components
    private JLabel lblNhanVien, lblMaNhanVien, lblThoiGian;
    private JTextField txtTienMatDauCa;
    private JComboBox<String> cbMenhGia;
    private JTextField txtSoLuong;
    private RoundedButton btnNhap, btnMoCa;
    private JTable tblMenhGia;
    private DefaultTableModel modelMenhGia;

    private final String[] columns = { "Mệnh giá", "500.000", "200.000", "100.000", "50.000", "20.000", "10.000",
            "5.000", "2.000", "1.000" };
    private final Map<String, Integer> mapSoLuong = new HashMap<>();
    private final DecimalFormat df = new DecimalFormat("#,###");

    public MoCaPanel(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = taiKhoan.getNhanVien();

        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255));
        setBorder(new EmptyBorder(20, 50, 20, 50));

        initUI();
        loadDuLieuCa();
        updateThoiGian();
    }

    public void loadDuLieuCa() {
        CaLam cl = caLamService.layCaHienTai(nhanVien.getMaNhanVien());
        if (cl != null) {
            txtTienMatDauCa.setText(df.format(cl.getTienMoCa()));
            btnMoCa.setEnabled(false);
            btnMoCa.setToolTipText("Bạn đang có ca làm việc chưa đóng");
            
            btnNhap.setEnabled(false);
            txtSoLuong.setEditable(false);
            cbMenhGia.setEnabled(false);
            mapSoLuong.clear();
            updateTable();
        } else {
            txtTienMatDauCa.setText("");
            btnMoCa.setEnabled(true);
            btnMoCa.setToolTipText(null);
            
            btnNhap.setEnabled(true);
            txtSoLuong.setEditable(true);
            cbMenhGia.setEnabled(true);
        }
    }

    private void initUI() {
        // --- Header ---
        JLabel lblTitle = new JLabel("MỞ CA LÀM VIỆC", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(0x3498DB));
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- Main Content ---
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);

        // 1. Employee Info Box
        JPanel pnlInfo = createInfoPanel();
        pnlCenter.add(pnlInfo);
        pnlCenter.add(Box.createVerticalStrut(20));

        // 2. Cash Input Box
        JPanel pnlCash = createCashPanel();
        pnlCenter.add(pnlCash);

        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createInfoPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pnl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font valueFont = new Font("Segoe UI", Font.BOLD, 16);

        // Row 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnl.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        lblNhanVien = new JLabel(nhanVien.getTenNhanVien());
        lblNhanVien.setFont(valueFont);
        pnl.add(lblNhanVien, gbc);

        // Row 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnl.add(new JLabel("Mã nhân viên:"), gbc);
        gbc.gridx = 1;
        lblMaNhanVien = new JLabel(nhanVien.getMaNhanVien());
        lblMaNhanVien.setFont(valueFont);
        pnl.add(lblMaNhanVien, gbc);

        // Row 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        pnl.add(new JLabel("Thời gian:"), gbc);
        gbc.gridx = 1;
        lblThoiGian = new JLabel();
        lblThoiGian.setFont(valueFont);
        pnl.add(lblThoiGian, gbc);

        return pnl;
    }

    private JPanel createCashPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(20, 30, 20, 30)));

        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiền mặt đầu ca
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlInputs.add(new JLabel("Tiền mặt đầu ca:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtTienMatDauCa = new JTextField();
        txtTienMatDauCa.setEditable(false);
        txtTienMatDauCa.setPreferredSize(new Dimension(300, 30));
        pnlInputs.add(txtTienMatDauCa, gbc);

        // Mệnh giá
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        pnlInputs.add(new JLabel("Mệnh giá:"), gbc);
        gbc.gridx = 1;
        String[] menhGias = { "500.000", "200.000", "100.000", "50.000", "20.000", "10.000", "5.000", "2.000",
                "1.000" };
        cbMenhGia = new JComboBox<>(menhGias);
        pnlInputs.add(cbMenhGia, gbc);

        // Số lượng
        gbc.gridx = 2;
        pnlInputs.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 3;
        txtSoLuong = new JTextField();
        pnlInputs.add(txtSoLuong, gbc);

        // Nút Nhập
        gbc.gridx = 3;
        gbc.gridy = 2;
        btnNhap = new RoundedButton("Nhập");
        btnNhap.setBackground(new Color(0x2ECC71));
        btnNhap.setForeground(Color.WHITE);
        btnNhap.addActionListener(this);
        pnlInputs.add(btnNhap, gbc);

        pnl.add(pnlInputs, BorderLayout.NORTH);

        // Table
        modelMenhGia = new DefaultTableModel(new Object[][] {
                { "Số lượng", "", "", "", "", "", "", "", "", "" }
        }, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMenhGia = new JTable(modelMenhGia);
        tblMenhGia.setRowHeight(40);
        tblMenhGia.getTableHeader().setReorderingAllowed(false);

        // Center text in table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblMenhGia.getColumnCount(); i++) {
            tblMenhGia.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tblMenhGia);
        scroll.setPreferredSize(new Dimension(800, 80));
        pnl.add(scroll, BorderLayout.CENTER);

        // Nút Mở ca
        btnMoCa = new RoundedButton("Mở ca");
        btnMoCa.setBackground(new Color(0x2ECC71));
        btnMoCa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnMoCa.setPreferredSize(new Dimension(200, 50));
        btnMoCa.addActionListener(this);

        JPanel pnlBottom = new JPanel();
        pnlBottom.setOpaque(false);
        pnlBottom.add(btnMoCa);
        pnl.add(pnlBottom, BorderLayout.SOUTH);

        return pnl;
    }

    private void updateThoiGian() {
        Timer timer = new Timer(1000, e -> {
            lblThoiGian.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        });
        timer.start();
    }

    private void handleNhap() {
        if (caLamService.layCaHienTai(nhanVien.getMaNhanVien()) != null) {
            JOptionPane.showMessageDialog(this, "Không thể nhập tiền khi đang trong ca làm việc!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String menhGiaStr = (String) cbMenhGia.getSelectedItem();
        String soLuongStr = txtSoLuong.getText().trim();

        if (soLuongStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng!");
            return;
        }

        try {
            int soLuong = Integer.parseInt(soLuongStr);
            if (soLuong < 0)
                throw new NumberFormatException();

            mapSoLuong.put(menhGiaStr, soLuong);
            updateTable();
            tinhTongTien();
            txtSoLuong.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
        }
    }

    private void updateTable() {
        for (int i = 1; i < columns.length; i++) {
            String colName = columns[i];
            Integer val = mapSoLuong.get(colName);
            modelMenhGia.setValueAt(val != null ? val : "", 0, i);
        }
    }

    private void tinhTongTien() {
        double tong = 0;
        for (Map.Entry<String, Integer> entry : mapSoLuong.entrySet()) {
            double giaTri = Double.parseDouble(entry.getKey().replaceAll("[^\\d]", ""));
            tong += giaTri * entry.getValue();
        }
        txtTienMatDauCa.setText(df.format(tong));
    }

    private void handleMoCa() {
        if (caLamService.layCaHienTai(nhanVien.getMaNhanVien()) != null) {
            JOptionPane.showMessageDialog(this,
                    "Bạn đang có một ca làm việc chưa đóng. Vui lòng kết ca trước khi mở ca mới!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra xem hệ thống hiện tại có ca làm việc nào khác đang ở trạng thái DANG_MO hay không
        CaLam caDangMoBatKy = caLamService.layCaDangMoBatKy();
        if (caDangMoBatKy != null) {
            com.example.service.NhanVienService nvService = new com.example.service.NhanVienService();
            com.example.entity.NhanVien nvDangMo = nvService.timTheoMa(caDangMoBatKy.getNhanVien().getMaNhanVien());
            String tenNVDangMo = (nvDangMo != null) ? nvDangMo.getTenNhanVien() : caDangMoBatKy.getNhanVien().getMaNhanVien();
            
            // Tự động xác định chức danh để hiển thị thông báo chính xác
            String chucDanh = (nvDangMo != null && nvDangMo.getChucVu() == com.example.entity.enums.ChucVu.NHAN_VIEN_QUAN_LY) ? "Quản lý" : "Dược sĩ";

            if (nhanVien.getChucVu() == com.example.entity.enums.ChucVu.NHAN_VIEN_QUAN_LY) {
                int choice = JOptionPane.showConfirmDialog(this,
                        "Cảnh báo: " + chucDanh + " [" + tenNVDangMo + "] hiện vẫn chưa đóng ca làm việc.\n" +
                        "Bạn có muốn hệ thống tự động kết toán (đóng ca hộ) cho nhân viên này để mở ca mới không?",
                        "Xác nhận đóng ca hộ", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (choice == JOptionPane.YES_OPTION) {
                    com.example.service.HoaDonService hoaDonService = new com.example.service.HoaDonService();
                    double doanhThuHeThong = hoaDonService.tinhTongDoanhThuCa(caDangMoBatKy.getMaCa());
                    
                    caDangMoBatKy.setTienHeThong(doanhThuHeThong);
                    caDangMoBatKy.setTienKetCa(caDangMoBatKy.getTienMoCa() + doanhThuHeThong); // Chênh lệch bằng 0
                    caDangMoBatKy.setGhiChu("Đóng ca hộ bởi Quản lý " + nhanVien.getTenNhanVien());
                    
                    if (!caLamService.dongCa(caDangMoBatKy)) {
                        JOptionPane.showMessageDialog(this, "Không thể đóng ca hộ cho " + chucDanh.toLowerCase() + " " + tenNVDangMo + "!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(this, "Đã tự động chốt và đóng ca hộ thành công cho " + chucDanh.toLowerCase() + " " + tenNVDangMo + "!");
                } else {
                    return; // Quản lý chọn không đóng hộ -> dừng lại không cho mở ca mới
                }
            } else {
                // Nếu là Dược sĩ, chặn tuyệt đối không cho mở
                JOptionPane.showMessageDialog(this,
                        "Không thể mở ca mới! Hiện tại " + chucDanh + " [" + tenNVDangMo + "] đang có ca làm việc ở trạng thái Đang Mở.\n" +
                        "Vui lòng yêu cầu nhân viên này đóng ca trước khi bàn giao.",
                        "Từ chối mở ca", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // =========================================================================
        // LOGIC MỚI: TÌM CA ĐÃ ĐƯỢC LÊN LỊCH THAY VÌ TỰ ĐỘNG TẠO MỚI
        // =========================================================================
        LocalDateTime now = LocalDateTime.now();
        java.util.List<CaLam> dsCaHomNay = caLamService.layCaTheoNgayVaTen(now.toLocalDate(), nhanVien.getTenNhanVien());
        
        CaLam caDuocLenLich = null;
        for (CaLam cl : dsCaHomNay) {
            // Lọc ra ca của chính nhân viên này và đang ở trạng thái CHƯA MỞ
            if (cl.getNhanVien().getMaNhanVien().equals(nhanVien.getMaNhanVien()) 
                && cl.getTrangThai() == TrangThaiCaLam.CHUA_MO) {
                
                // KIỂM TRA GIỜ: Cho phép mở ca trước tối đa 30 phút và không được mở nếu đã lố giờ kết thúc
                if (now.isAfter(cl.getGioBatDau().minusMinutes(30)) && 
                   (cl.getGioKetThuc() == null || now.isBefore(cl.getGioKetThuc()))) {
                    caDuocLenLich = cl;
                    break;
                }
            }
        }

        // Nếu không tìm thấy ca nào hợp lệ và là Dược sĩ (không phải quản lý), chặn luôn không cho mở!
        if (caDuocLenLich == null && nhanVien.getChucVu() == com.example.entity.enums.ChucVu.DUOC_SI) {
            JOptionPane.showMessageDialog(this, 
                "Bạn không có ca làm việc nào được lên lịch vào khung giờ này!\n(Lưu ý: Chỉ được mở ca trước giờ làm việc tối đa 30 phút)", 
                "Từ chối mở ca", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // =========================================================================

        // 2. Xử lý tính toán tiền đầu ca
        String tongTienStr = txtTienMatDauCa.getText().replaceAll("[^\\d]", "");
        if (tongTienStr.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn chưa nhập tiền mặt đầu ca. Tiếp tục mở ca với 0đ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            tongTienStr = "0";
        }
        double tienMoCa = Double.parseDouble(tongTienStr);

        boolean success = false;
        String maCaMo = "";

        if (caDuocLenLich != null) {
            // 3. THỰC THI: CẬP NHẬT CA LÀM ĐÃ LÊN LỊCH (UPDATE)
            caDuocLenLich.setTienMoCa(tienMoCa);
            caDuocLenLich.setTrangThai(TrangThaiCaLam.DANG_MO);
            caDuocLenLich.setGioBatDau(now); // Đè lại giờ bắt đầu bằng thời gian mở ca thực tế
            caDuocLenLich.setTienHeThong(0);
            caDuocLenLich.setTienKetCa(0);
            success = caLamService.capNhat(caDuocLenLich);
            maCaMo = caDuocLenLich.getMaCa();
        } else {
            // 3. THỰC THI: TẠO MỚI CA LÀM CHO QUẢN LÝ (INSERT)
            CaLam caLamMoi = new CaLam();
            String prefix = "CA" + now.format(DateTimeFormatter.ofPattern("ddMMyy"));
            int stt = caLamService.laySoLuongCaTrongNgay(prefix) + 1;
            String maCa = String.format("%s%02d", prefix, stt);
            
            caLamMoi.setMaCa(maCa);
            caLamMoi.setNhanVien(nhanVien);
            caLamMoi.setGioBatDau(now);
            caLamMoi.setTrangThai(TrangThaiCaLam.DANG_MO);
            caLamMoi.setTienMoCa(tienMoCa);
            caLamMoi.setTienHeThong(0);
            caLamMoi.setTienKetCa(0);
            caLamMoi.setGhiChu("Quản lý tự tạo ca mới (không cần lịch làm)");
            success = caLamService.them(caLamMoi);
            maCaMo = maCa;
        }

        if (success) {
            JOptionPane.showMessageDialog(this, "Mở ca thành công! (Mã ca: " + maCaMo + ")");
            // Chuyển màn hình
            Container parent = getParent();
            if (parent instanceof JPanel) {
                CardLayout layout = (CardLayout) parent.getLayout();
                layout.show(parent, "Màn Hình Chính");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Mở ca thất bại do lỗi hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnNhap) {
            handleNhap();
        } else if (source == btnMoCa) {
            handleMoCa();
        }
    }
}
