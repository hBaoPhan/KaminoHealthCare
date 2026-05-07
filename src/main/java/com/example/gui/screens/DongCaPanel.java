package com.example.gui.screens;

import com.example.entity.CaLam;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.entity.enums.TrangThaiCaLam;
import com.example.dao.CaLamDAO;
import com.example.dao.HoaDonDAO;
import com.example.gui.components.RoundedButton;

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

public class DongCaPanel extends JPanel implements ActionListener {

    private final TaiKhoan taiKhoan;
    private final NhanVien nhanVien;
    private final CaLamDAO caLamDAO = new CaLamDAO();
    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private CaLam caHienTai;

    // Components
    private JLabel lblNhanVien, lblMaNhanVien, lblThoiGian;
    private JTextField txtTienHeThong, txtTienTrongKet;
    private JComboBox<String> cbMenhGia;
    private JTextField txtSoLuong;
    private RoundedButton btnNhap, btnDongCa;
    private JTable tblMenhGia;
    private DefaultTableModel modelMenhGia;
    private JTextField txtGhiChu;

    private final String[] columns = { "Mệnh giá", "500.000", "200.000", "100.000", "50.000", "20.000", "10.000",
            "5.000", "2.000", "1.000" };
    private final Map<String, Integer> mapSoLuong = new HashMap<>();
    private final DecimalFormat df = new DecimalFormat("#,###");

    public DongCaPanel(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = taiKhoan.getNhanVien();

        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255));
        setBorder(new EmptyBorder(20, 50, 20, 50));

        initUI();
        loadDuLieuCa();
        updateThoiGian();
    }

    private void initUI() {
        // --- Header ---
        JLabel lblTitle = new JLabel("ĐÓNG CA LÀM VIỆC", SwingConstants.CENTER);
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
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(20, 30, 20, 30)));

        JPanel pnlInputs = new JPanel(new GridBagLayout());
        pnlInputs.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tiền hệ thống
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlInputs.add(new JLabel("Tiền hệ thống:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtTienHeThong = new JTextField();
        txtTienHeThong.setEditable(false);
        txtTienHeThong.setBackground(new Color(230, 230, 230));
        txtTienHeThong.setPreferredSize(new Dimension(300, 30));
        pnlInputs.add(txtTienHeThong, gbc);

        // Tiền trong két
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        pnlInputs.add(new JLabel("Tiền mặt trong két:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtTienTrongKet = new JTextField();
        txtTienTrongKet.setEditable(false);
        pnlInputs.add(txtTienTrongKet, gbc);

        // Mệnh giá
        gbc.gridx = 0;
        gbc.gridy = 2;
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
        gbc.gridy = 3;
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tblMenhGia.getColumnCount(); i++) {
            tblMenhGia.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tblMenhGia);
        scroll.setPreferredSize(new Dimension(800, 80));
        pnl.add(scroll, BorderLayout.CENTER);

        // --- Ghi chú & Nút Đóng ca ---
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(10, 0, 0, 0));

        JPanel pnlGhiChu = new JPanel(new BorderLayout(5, 5));
        pnlGhiChu.setOpaque(false);
        pnlGhiChu.add(new JLabel("Ghi chú:"), BorderLayout.WEST);
        txtGhiChu = new JTextField();
        pnlGhiChu.add(txtGhiChu, BorderLayout.CENTER);

        btnDongCa = new RoundedButton("Đóng ca");
        btnDongCa.setBackground(new Color(255, 69, 69)); // Reddish
        btnDongCa.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnDongCa.setPreferredSize(new Dimension(200, 50));
        btnDongCa.addActionListener(this);

        JPanel pnlButton = new JPanel();
        pnlButton.setOpaque(false);
        pnlButton.add(btnDongCa);

        pnlBottom.add(pnlGhiChu, BorderLayout.NORTH);
        pnlBottom.add(pnlButton, BorderLayout.SOUTH);

        pnl.add(pnlBottom, BorderLayout.SOUTH);

        return pnl;
    }

    public void loadDuLieuCa() {
        caHienTai = caLamDAO.layCaHienTai(nhanVien.getMaNhanVien());
        if (caHienTai != null) {
            java.time.LocalDate ngayCa = caHienTai.getGioBatDau() != null ? caHienTai.getGioBatDau().toLocalDate() : java.time.LocalDate.now();
            java.util.List<com.example.entity.HoaDon> dsHoaDon = hoaDonDAO.timKiem(null, ngayCa);
            
            // Nếu ca làm việc vắt qua ngày mới
            if (!ngayCa.isEqual(java.time.LocalDate.now())) {
                dsHoaDon.addAll(hoaDonDAO.timKiem(null, java.time.LocalDate.now()));
            }

            double doanhThu = 0;
            com.example.dao.ChiTietHoaDonDAO chiTietHoaDonDAO = new com.example.dao.ChiTietHoaDonDAO();
            
            for (com.example.entity.HoaDon hd : dsHoaDon) {
                if (hd.getCa() == null || !caHienTai.getMaCa().equals(hd.getCa().getMaCa())) {
                    continue;
                }
                if (!hd.isTrangThaiThanhToan()) {
                    continue;
                }
                
                double tienHD = 0;
                double tongHienTai = hd.tinhTongTienThanhToan();
                com.example.entity.enums.LoaiHoaDon loai = hd.getLoaiHoaDon();

                if (loai == com.example.entity.enums.LoaiHoaDon.BAN_HANG || loai == null) {
                    tienHD = tongHienTai;
                } else {
                    double tongGoc = 0;
                    if (hd.getHoaDonDoiTra() != null) {
                        String maGoc = hd.getHoaDonDoiTra().getMaHoaDon();
                        com.example.entity.HoaDon hdGoc = hoaDonDAO.timTheoMa(maGoc);
                        if (hdGoc != null) {
                            hdGoc.setDsChiTiet(chiTietHoaDonDAO.layTheoMaHoaDon(maGoc));
                            tongGoc = hdGoc.tinhTongTienThanhToan();
                        }
                    }

                    if (loai == com.example.entity.enums.LoaiHoaDon.DOI_HANG) {
                        tienHD = tongHienTai - tongGoc;
                    } else if (loai == com.example.entity.enums.LoaiHoaDon.TRA_HANG) {
                        tienHD = -tongHienTai;
                    } else {
                        tienHD = tongHienTai;
                    }
                }
                
                doanhThu += tienHD;
            }
            
            txtTienHeThong.setText(df.format(doanhThu));
            btnDongCa.setEnabled(true);
        } else {
            txtTienHeThong.setText("");
            btnDongCa.setEnabled(false);
        }
    }

    private void updateThoiGian() {
        Timer timer = new Timer(1000, e -> {
            lblThoiGian.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        });
        timer.start();
    }

    private void handleNhap() {
        String menhGiaStr = (String) cbMenhGia.getSelectedItem();
        String soLuongStr = txtSoLuong.getText().trim();

        if (soLuongStr.isEmpty())
            return;

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
        txtTienTrongKet.setText(df.format(tong));
    }

    private void handleDongCa() {
        if (caHienTai == null)
            return;

        String tongTienStr = txtTienTrongKet.getText().replaceAll("[^\\d]", "");
        if (tongTienStr.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn chưa nhập tiền trong két. Tiếp tục đóng ca với 0đ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;
            tongTienStr = "0";
        }

        double tienKetCa = Double.parseDouble(tongTienStr);
        double tienHeThong = Double.parseDouble(txtTienHeThong.getText().replaceAll("[^\\d]", ""));

        caHienTai.setGioKetThuc(LocalDateTime.now());
        caHienTai.setTrangThai(TrangThaiCaLam.DONG);
        caHienTai.setTienKetCa(tienKetCa);
        caHienTai.setTienHeThong(tienHeThong);
        caHienTai.setGhiChu(txtGhiChu.getText().trim());

        if (caLamDAO.capNhat(caHienTai)) {
            JOptionPane.showMessageDialog(this, "Đóng ca thành công!");
            // Switch back to home
            Container parent = getParent();
            if (parent instanceof JPanel) {
                CardLayout layout = (CardLayout) parent.getLayout();
                layout.show(parent, "Màn Hình Chính");
            }
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Đóng ca thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnNhap) {
            handleNhap();
        } else if (source == btnDongCa) {
            handleDongCa();
        }
    }

    private void clearFields() {
        txtTienHeThong.setText("");
        txtTienTrongKet.setText("");
        txtGhiChu.setText("");
        txtSoLuong.setText("");
        mapSoLuong.clear();
        for (int i = 1; i < columns.length; i++) {
            modelMenhGia.setValueAt("", 0, i);
        }
    }
}
