package com.example.gui.screens;

import com.example.entity.CaLam;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.entity.enums.TrangThaiCaLam;
import com.example.dao.CaLamDAO;
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
    private final CaLamDAO caLamDAO = new CaLamDAO();

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
        CaLam cl = caLamDAO.layCaHienTai(nhanVien.getMaNhanVien());
        if (cl != null) {
            txtTienMatDauCa.setText(df.format(cl.getTienMoCa()));
            btnMoCa.setEnabled(false);
            btnMoCa.setToolTipText("Bạn đang có ca làm việc chưa đóng");
        } else {
            txtTienMatDauCa.setText("");
            btnMoCa.setEnabled(true);
            btnMoCa.setToolTipText(null);
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
        if (caLamDAO.layCaHienTai(nhanVien.getMaNhanVien()) != null) {
            JOptionPane.showMessageDialog(this,
                    "Bạn đang có một ca làm việc chưa đóng. Vui lòng kết ca trước khi mở ca mới!",
                    "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tongTienStr = txtTienMatDauCa.getText().replaceAll("[^\\d]", "");
        if (tongTienStr.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn chưa nhập tiền mặt đầu ca. Tiếp tục mở ca với 0đ?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION)
                return;
            tongTienStr = "0";
        }

        double tienMoCa = Double.parseDouble(tongTienStr);

        CaLam caLam = new CaLam();
        // Generate a simple ID or use a sequence. For now, let's just use
        // timestamp-based ID or similar.
        caLam.setMaCa("CL" + Long.toString(System.currentTimeMillis() / 1000, 36).toUpperCase());
        caLam.setNhanVien(nhanVien);
        caLam.setGioBatDau(LocalDateTime.now());
        caLam.setTrangThai(TrangThaiCaLam.DANG_MO);
        caLam.setTienMoCa(tienMoCa);
        caLam.setTienHeThong(0); // Initialize
        caLam.setTienKetCa(0);
        caLam.setGhiChu("");

        if (caLamDAO.them(caLam)) {
            JOptionPane.showMessageDialog(this, "Mở ca thành công!");
            // Switch back to home screen or dashboard
            Container parent = getParent();
            if (parent instanceof JPanel) {
                CardLayout layout = (CardLayout) parent.getLayout();
                layout.show(parent, "Màn Hình Chính");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Mở ca thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
