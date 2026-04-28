package com.example.gui.screens;

import com.example.dao.*;
import com.example.entity.*;
import com.example.entity.enums.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DoiHangPanel extends JPanel {

    private JTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien, txtKhachDua, txtTienThoi;
    private JTextField txtSearchHoaDon, txtSearchSanPham;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JRadioButton radTienMat, radChuyenKhoan;
    private JPanel pnlDynamicContent;
    private JTable tblHoaDonGoc, tblSanPham;

    // --- DAO ---
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private SuPhanBoLoDAO suPhanBoLoDAO = new SuPhanBoLoDAO();
    private LoDAO loDAO = new LoDAO();

    private HoaDon hoaDonGocHienTai = null;

    public DoiHangPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        txtSearchHoaDon = new JTextField(15);
        txtSearchSanPham = new JTextField(15);

        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);

        pnlLeft.add(createTablePanel("Chi tiết hóa đơn gốc", "Tìm hóa đơn:", txtSearchHoaDon, "Nhập mã hóa đơn gốc", tblHoaDonGoc = createTable()));
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn đổi", "Tìm kiếm sản phẩm:", txtSearchSanPham, "Nhập mã/tên sản phẩm", tblSanPham = createTable()));

        add(pnlLeft, BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);

        initEvents();
    }

    private void initEvents() {
        txtSearchHoaDon.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemHoaDonGoc(txtSearchHoaDon.getText().trim());
                }
            }
        });

        txtSearchSanPham.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemVaThemSanPhamMoi(txtSearchSanPham.getText().trim());
                }
            }
        });

        tblHoaDonGoc.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                tinhToanChenhLech();
            }
        });

        tblSanPham.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.INSERT) {
                tinhToanChenhLech();
            }
        });

        txtKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhTienThoi(); }
            public void removeUpdate(DocumentEvent e) { tinhTienThoi(); }
            public void changedUpdate(DocumentEvent e) { tinhTienThoi(); }
        });

        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    private void timKiemHoaDonGoc(String maHoaDon) {
        hoaDonGocHienTai = hoaDonDAO.timTheoMa(maHoaDon);
        if (hoaDonGocHienTai == null) {
            JOptionPane.showMessageDialog(this, "Hóa đơn không tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        txtMaHoaGoc.setText(hoaDonGocHienTai.getMaHoaDon());
        if (hoaDonGocHienTai.getKhachHang() != null) {
            txtTenKhachHang.setText(hoaDonGocHienTai.getKhachHang().getTenKhachHang());
        }
        if (hoaDonGocHienTai.getThoiGianTao() != null) {
            txtNgayTao.setText(hoaDonGocHienTai.getThoiGianTao().toString());
        }
        
        txtMaHoaDon.setText("HDD" + System.currentTimeMillis()); 
        
        List<ChiTietHoaDon> listCTHD = chiTietHoaDonDAO.layTheoMaHoaDon(maHoaDon);
        DefaultTableModel model = (DefaultTableModel) tblHoaDonGoc.getModel();
        model.setRowCount(0);

        for(ChiTietHoaDon ct : listCTHD) {
            Object[] row = {
                ct.getDonViQuyDoi().getSanPham().getMaSanPham(),
                ct.getDonViQuyDoi().getSanPham().getTenSanPham(),
                ct.getDonViQuyDoi().getTenDonVi() != null ? ct.getDonViQuyDoi().getTenDonVi().toString() : "",
                ct.getSoLuong(),
                ct.getDonGia(),
                ct.getDonViQuyDoi().getSanPham().getThue(),
                ct.tinhThanhTien()
            };
            model.addRow(row);
        }
        tinhToanChenhLech();
    }

    private void timKiemVaThemSanPhamMoi(String tuKhoa) {
        SanPham sp = sanPhamDAO.timTheoMa(tuKhoa); 
        if (sp != null) {
            DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
            Object[] row = {
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                "HOP", // Sửa lại thành tên Enum DonVi mặc định
                1,
                sp.getDonGiaCoBan(), // SỬA: Đã khớp với thuộc tính Entity SanPham
                sp.getThue(), 
                sp.getDonGiaCoBan()
            };
            model.addRow(row);
            tinhToanChenhLech();
            txtSearchSanPham.setText(""); 
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm!", "Lỗi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void tinhToanChenhLech() {
        double tienGoc = 0; 
        double tienDoi = 0; 

        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
        for (int i = 0; i < modelGoc.getRowCount(); i++) {
            int soLuong = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
            double donGia = Double.parseDouble(modelGoc.getValueAt(i, 4).toString());
            double thueSuat = Double.parseDouble(modelGoc.getValueAt(i, 5).toString());
            double thanhTien = (soLuong * donGia) + ((soLuong * donGia) * (thueSuat / 100.0));
            modelGoc.setValueAt(thanhTien, i, 6);
            tienGoc += thanhTien;
        }

        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
        for (int i = 0; i < modelDoi.getRowCount(); i++) {
            int soLuong = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
            double donGia = Double.parseDouble(modelDoi.getValueAt(i, 4).toString());
            double thueSuat = Double.parseDouble(modelDoi.getValueAt(i, 5).toString());
            double thanhTien = (soLuong * donGia) + ((soLuong * donGia) * (thueSuat / 100.0));
            modelDoi.setValueAt(thanhTien, i, 6); 
            tienDoi += thanhTien;
        }

        double chenhLech = tienDoi - tienGoc;
        
        txtTienGoc.setText(String.format("%.0f", tienGoc));
        txtTienDoi.setText(String.format("%.0f", tienDoi));
        
        if (chenhLech > 0) {
            txtChenhLech.setText(String.format("%.0f", chenhLech));
            txtThanhTien.setText(String.format("%.0f", chenhLech));
            txtKhachDua.setEditable(true);
        } else { 
            txtChenhLech.setText("0");
            txtThanhTien.setText("0");
            txtKhachDua.setText("0");
            txtKhachDua.setEditable(false);
        }
        
        tinhTienThoi();
    }

    private void tinhTienThoi() {
        try {
            double khachDua = txtKhachDua.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtKhachDua.getText().trim());
            double thanhTien = Double.parseDouble(txtThanhTien.getText().trim());
            
            if (thanhTien > 0 && khachDua >= thanhTien) {
                txtTienThoi.setText(String.format("%.0f", khachDua - thanhTien));
            } else {
                txtTienThoi.setText("0");
            }
        } catch (NumberFormatException ex) {
            txtTienThoi.setText("0");
        }
    }

    private void xuLyThanhToan() {
        if (tblSanPham.getRowCount() == 0 && tblHoaDonGoc.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần đổi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Tạo Hóa Đơn
            HoaDon hdDoi = new HoaDon();
            hdDoi.setMaHoaDon(txtMaHoaDon.getText());
            hdDoi.setThoiGianTao(LocalDateTime.now());
            hdDoi.setTrangThaiThanhToan(true);
            
            // SỬA: Cập nhật Enum loại hóa đơn đổi hàng
            hdDoi.setLoaiHoaDon(LoaiHoaDon.DOI_HANG); 
            hdDoi.setGhiChu(txtGhiChu.getText());
            
            // Lấy phương thức thanh toán từ RadioButton
            hdDoi.setPhuongThucThanhToan(radTienMat.isSelected() ? PhuongThucThanhToan.TIEN_MAT : PhuongThucThanhToan.CHUYEN_KHOAN);

            // SỬA: Liên kết hóa đơn gốc vào hóa đơn đổi mới
            if (hoaDonGocHienTai != null) {
                hdDoi.setHoaDonDoiTra(hoaDonGocHienTai);
                if (hoaDonGocHienTai.getKhachHang() != null) {
                    hdDoi.setKhachHang(hoaDonGocHienTai.getKhachHang());
                }
            }

            // LƯU Ý GHI NHẬN CHÊNH LỆCH: Bạn cần thêm thuộc tính `tienKhachDua` vào entity HoaDon.java
            // để dòng code dưới đây không bị lỗi và lưu lại được tiền thực tế khách đã đưa.
            // double tienThucNhan = txtKhachDua.getText().isEmpty() ? 0 : Double.parseDouble(txtKhachDua.getText().trim());
            // hdDoi.setTienKhachDua(tienThucNhan); 

            // 2. Thu thập hàng trả để cộng kho
            List<SuPhanBoLo> dsTraLai = new ArrayList<>();
            DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
            for (int i = 0; i < modelGoc.getRowCount(); i++) {
                String maDonVi = modelGoc.getValueAt(i, 2).toString(); 
                int soLuongTra = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
                
                List<SuPhanBoLo> phanBoCu = suPhanBoLoDAO.layPhanBoLoCuaChiTiet(txtMaHoaGoc.getText(), maDonVi);
                if (!phanBoCu.isEmpty()) {
                    SuPhanBoLo spTra = phanBoCu.get(0);
                    spTra.setSoLuong(soLuongTra);
                    dsTraLai.add(spTra);
                }
            }

            // 3. Thu thập hàng mới và phân bổ lô xuất kho
            List<ChiTietHoaDon> dsChiTietMoi = new ArrayList<>();
            List<SuPhanBoLo> dsPhanBoMoi = new ArrayList<>();
            DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
            
            for (int i = 0; i < modelDoi.getRowCount(); i++) {
                String maSP = modelDoi.getValueAt(i, 0).toString();
                int soLuongMua = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                double donGia = Double.parseDouble(modelDoi.getValueAt(i, 4).toString());
                
                ChiTietHoaDon ctMoi = new ChiTietHoaDon();
                
                // Giả lập lấy DonViQuyDoi
                DonViQuyDoi donVi = new DonViQuyDoi();
                donVi.setMaDonVi(maSP + "_DV1"); // Thay logic sinh mã đơn vị nếu cần
                ctMoi.setDonViQuyDoi(donVi); 
                
                ctMoi.setSoLuong(soLuongMua);
                ctMoi.setDonGia(donGia);
                ctMoi.setLaQuaTangKem(false); // SỬA: Phù hợp cấu trúc Entity ChiTietHoaDon
                dsChiTietMoi.add(ctMoi);

                List<Lo> loKhaDung = loDAO.layDanhSachLoKhaDung(maSP); 
                int soLuongCanTru = soLuongMua;
                
                for (Lo lo : loKhaDung) {
                    if (soLuongCanTru <= 0) break;
                    
                    // SỬA: Sử dụng getSoLuongSanPham
                    int truTuLoNay = Math.min(lo.getSoLuongSanPham(), soLuongCanTru); 
                    soLuongCanTru -= truTuLoNay;
                    
                    SuPhanBoLo phanBoMoi = new SuPhanBoLo();
                    phanBoMoi.setChiTietHoaDon(ctMoi);
                    phanBoMoi.setLo(lo);
                    phanBoMoi.setSoLuong(truTuLoNay);
                    dsPhanBoMoi.add(phanBoMoi);
                }
                
                if (soLuongCanTru > 0) {
                    throw new RuntimeException("Sản phẩm " + maSP + " không đủ tồn kho!");
                }
            }

            // 4. Lưu CSDL
//            boolean ketQua = hoaDonDAO.luuGiaoDichDoiHang(hdDoi, dsTraLai, dsChiTietMoi, dsPhanBoMoi);
//
//            if (ketQua) {
//                JOptionPane.showMessageDialog(this, "Đổi hàng và Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
//                resetForm();
//            } else {
//                JOptionPane.showMessageDialog(this, "Lưu giao dịch thất bại! Đã khôi phục dữ liệu.", "Lỗi", JOptionPane.ERROR_MESSAGE);
//            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý: " + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void resetForm() {
        txtMaHoaGoc.setText("");
        txtMaHoaDon.setText("");
        txtNgayTao.setText("");
        txtTenKhachHang.setText("");
        txtGhiChu.setText("");
        txtKhachDua.setText("");
        hoaDonGocHienTai = null;
        
        ((DefaultTableModel) tblHoaDonGoc.getModel()).setRowCount(0);
        ((DefaultTableModel) tblSanPham.getModel()).setRowCount(0);
        tinhToanChenhLech();
    }

    // ================ CÁC HÀM TẠO GIAO DIỆN (GIỮ NGUYÊN) ================

    private JTable createTable() {
        String[] columns = { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        table.getColumnModel().getColumn(2).setCellEditor(new UnitOfMeasureCellEditor());
        table.getColumnModel().getColumn(3).setCellRenderer(new QuantitySpinnerRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());

        return table;
    }

    private JPanel createTablePanel(String title, String labelSearch, JTextField txtSearch, String placeholder, JTable table) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlHeader.add(lblTitle, BorderLayout.WEST);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel(labelSearch);
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        txtSearch.setPreferredSize(new Dimension(200, 30));
        txtSearch.setToolTipText(placeholder); 
        
        pnlSearch.add(lblSearch);
        pnlSearch.add(txtSearch);
        pnlHeader.add(pnlSearch, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(scrollPane, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createInfoPanel() {
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setPreferredSize(new Dimension(420, 0));
        pnlMain.setBackground(Color.WHITE);
        pnlMain.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        JLabel lblTitle = new JLabel("Hóa đơn đổi hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        pnlMain.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 15, 4, 15);
        gbc.weightx = 1.0;

        int r = 0;
        addInputRow(pnlContent, "Mã hóa gốc:", txtMaHoaGoc = new JTextField(), gbc, r++);
        addInputRow(pnlContent, "Mã hóa đơn:", txtMaHoaDon = new JTextField(), gbc, r++);
        addInputRow(pnlContent, "Ngày tạo:", txtNgayTao = new JTextField(), gbc, r++);
        addInputRow(pnlContent, "Người tạo:", txtNguoiTao = new JTextField("Phan Hoài Bảo"), gbc, r++);
        addInputRow(pnlContent, "Khách hàng:", txtTenKhachHang = new JTextField(), gbc, r++);

        gbc.gridy = r++;
        pnlContent.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(2, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlContent.add(new JScrollPane(txtGhiChu), gbc);

        addInputRow(pnlContent, "Tiền HĐ gốc:", txtTienGoc = new JTextField("0"), gbc, r++);
        addInputRow(pnlContent, "Tiền HĐ đổi:", txtTienDoi = new JTextField("0"), gbc, r++);
        addInputRow(pnlContent, "Chênh lệch:", txtChenhLech = new JTextField("0"), gbc, r++);
        addInputRow(pnlContent, "Thuế:", txtThue = new JTextField("0"), gbc, r++);
        addInputRow(pnlContent, "Thành tiền:", txtThanhTien = new JTextField("0"), gbc, r++);

        // --- PT THANH TOÁN ---
        gbc.gridy = r++;
        JPanel pnlRadio = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        pnlRadio.setOpaque(false);
        pnlRadio.add(new JLabel("PT Thanh toán: "));
        radTienMat = new JRadioButton("Tiền mặt", true);
        radChuyenKhoan = new JRadioButton("Chuyển khoản");
        ButtonGroup bg = new ButtonGroup();
        bg.add(radTienMat); bg.add(radChuyenKhoan);
        pnlRadio.add(radTienMat); pnlRadio.add(radChuyenKhoan);
        pnlContent.add(pnlRadio, gbc);

        // --- NỘI DUNG ĐỘNG (TIỀN MẶT / QR) ---
        gbc.gridy = r++;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        pnlDynamicContent = new JPanel(new CardLayout());
        pnlDynamicContent.setOpaque(false);
        
        JPanel pnlCash = new JPanel(new GridBagLayout());
        pnlCash.setOpaque(false);
        GridBagConstraints gbcCash = new GridBagConstraints();
        gbcCash.fill = GridBagConstraints.HORIZONTAL;
        gbcCash.weightx = 1.0;
        gbcCash.insets = new Insets(0, 0, 8, 0);
        
        txtKhachDua = new JTextField();
        txtTienThoi = new JTextField();
        txtTienThoi.setEditable(false);
        txtTienThoi.setBackground(new Color(245, 245, 245));

        addInputRow(pnlCash, "Tiền khách đưa:", txtKhachDua, gbcCash, 0);
        addInputRow(pnlCash, "Tiền thối lại:", txtTienThoi, gbcCash, 1);
        pnlDynamicContent.add(pnlCash, "CASH");

        pnlDynamicContent.add(createQRPanel(), "QR");
        pnlContent.add(pnlDynamicContent, gbc);

        radTienMat.addActionListener(e -> ((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "CASH"));
        radChuyenKhoan.addActionListener(e -> ((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "QR"));

        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThanhToan.setPreferredSize(new Dimension(0, 50));

        pnlMain.add(pnlContent, BorderLayout.CENTER);
        pnlMain.add(btnThanhToan, BorderLayout.SOUTH);

        setupReadOnlyFields();
        return pnlMain;
    }

    private JPanel createQRPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setOpaque(false);
        pnl.setBorder(BorderFactory.createTitledBorder("Mã QR Thanh Toán"));
        JLabel lblQR = new JLabel("HÌNH ẢNH MÃ QR", SwingConstants.CENTER);
        lblQR.setPreferredSize(new Dimension(0, 150));
        lblQR.setOpaque(true);
        lblQR.setBackground(Color.WHITE);
        lblQR.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnl.add(lblQR, BorderLayout.CENTER);
        return pnl;
    }

    private void addInputRow(JPanel pnl, String labelText, JTextField txt, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(120, 25));
        rowPanel.add(lbl, BorderLayout.WEST);
        rowPanel.add(txt, BorderLayout.CENTER);
        pnl.add(rowPanel, gbc);
    }

    private void setupReadOnlyFields() {
        JTextField[] readonly = { txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang, 
                                  txtTienGoc, txtTienDoi, txtChenhLech, txtThue, txtThanhTien };
        for (JTextField f : readonly) {
            f.setEditable(false);
            f.setBackground(new Color(245, 245, 245));
        }
    }

    // ================ CÁC LỚP HỖ TRỢ BẢNG ================
    private class UnitOfMeasureCellEditor extends DefaultCellEditor {
        public UnitOfMeasureCellEditor() {
            super(new JComboBox<>(new String[]{"HOP", "VI", "VIEN", "CHAI", "TUYP"})); // Đã đổi theo enum DonVi
            setClickCountToStart(1);
        }
    }

    private class QuantitySpinnerRenderer extends DefaultTableCellRenderer {
        public QuantitySpinnerRenderer() { setHorizontalAlignment(SwingConstants.RIGHT); }
    }

    private class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        public QuantitySpinnerEditor() {
            ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setHorizontalAlignment(JTextField.RIGHT);
        }
        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            spinner.setValue(v != null ? Integer.parseInt(v.toString()) : 1);
            return spinner;
        }
        @Override
        public Object getCellEditorValue() { return spinner.getValue(); }
    }
}