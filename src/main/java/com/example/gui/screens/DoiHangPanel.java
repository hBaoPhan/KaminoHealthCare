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
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DoiHangPanel extends JPanel {

    private JTextField txtMaHoaDonGoc, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienDoi, txtChenhLech, txtThanhTienLamTron, txtKhachDua, txtTienThoi;
    private JTextField txtSearchHoaDon, txtSearchSanPham;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan, btnXoaDong;
    private JRadioButton radTienMat, radChuyenKhoan;
    private JPanel pnlDynamicContent, pnlThanhTienContainer;
    private JTable tblHoaDonGoc, tblSanPham;

    private JPopupMenu popupGoiY;
    private JList<SanPham> listGoiY;
    private DefaultListModel<SanPham> modelGoiY;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private DonViQuyDoiDAO donViQuyDoiDAO = new DonViQuyDoiDAO();

    private HoaDon hoaDonGocHienTai = null;
    private List<ChiTietHoaDon> chiTietHoaDonGocList = new ArrayList<>();
    private double tongTienHoaDonGocBanDau = 0;
    private TaiKhoan taiKhoanDangNhap;

    public DoiHangPanel(TaiKhoan tk) {
        this.taiKhoanDangNhap = tk;
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        txtSearchHoaDon = new JTextField(15);
        setupPlaceholder(txtSearchHoaDon, "Nhập mã hóa đơn gốc...");

        txtSearchSanPham = new JTextField(15);
        setupPlaceholder(txtSearchSanPham, "Nhập mã/tên sản phẩm...");
        
        btnXoaDong = new JButton("Xóa dòng");
        btnXoaDong.setBackground(new Color(108, 117, 125));
        btnXoaDong.setForeground(Color.WHITE);
        btnXoaDong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoaDong.setPreferredSize(new Dimension(110, 35));
        
        btnXoaDong.addActionListener(e -> {
            int selectedRow = tblSanPham.getSelectedRow();
            if (selectedRow >= 0) {
                ((DefaultTableModel) tblSanPham.getModel()).removeRow(selectedRow);
                tinhToanToanBoTien();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!", "Chưa chọn dòng", JOptionPane.WARNING_MESSAGE);
            }
        });

        tblHoaDonGoc = createTable(); 
        tblSanPham = createTable();
        
        tblHoaDonGoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblHoaDonGoc.getSelectedRow();
                if (row != -1 && chiTietHoaDonGocList != null && row < chiTietHoaDonGocList.size()) {
                    ChiTietHoaDon ct = chiTietHoaDonGocList.get(row);
                    if (ct.getDonViQuyDoi().getSanPham().getLoaiSanPham().name().equals("ETC")) {
                        JOptionPane.showMessageDialog(null, "Đây là thuốc kê đơn (ETC). Không được phép thay đổi!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        
        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn gốc", "Tìm hóa đơn:", txtSearchHoaDon, tblHoaDonGoc, null));
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn đổi", "Tìm kiếm sản phẩm:", txtSearchSanPham, tblSanPham, btnXoaDong));

        add(pnlLeft, BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);

        initSuggestionPopup();
        initEvents();
    }

    private void setupPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText(""); textField.setForeground(Color.BLACK);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setForeground(Color.GRAY); textField.setText(placeholder);
                }
            }
        });
    }

    private void initSuggestionPopup() {
        popupGoiY = new JPopupMenu();
        modelGoiY = new DefaultListModel<>();
        listGoiY = new JList<>(modelGoiY);
        listGoiY.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SanPham) {
                    SanPham sp = (SanPham) value;
                    setText(sp.getMaSanPham() + " - " + sp.getTenSanPham());
                }
                return this;
            }
        });
        popupGoiY.add(new JScrollPane(listGoiY));
        popupGoiY.setFocusable(false);
        listGoiY.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    SanPham selected = listGoiY.getSelectedValue();
                    if (selected != null) {
                        themSanPhamVaoBang(selected);
                        popupGoiY.setVisible(false);
                    }
                }
            }
        });
    }

    private void initEvents() {
        txtSearchHoaDon.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String kw = txtSearchHoaDon.getText().trim();
                    if (!kw.isEmpty() && !kw.equals("Nhập mã hóa đơn gốc...")) timKiemHoaDonGoc(kw);
                }
            }
        });

        txtSearchSanPham.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { showPopup(); }
            public void removeUpdate(DocumentEvent e) { showPopup(); }
            public void changedUpdate(DocumentEvent e) { showPopup(); }
            private void showPopup() {
                String text = txtSearchSanPham.getText().trim();
                if (text.isEmpty() || text.equals("Nhập mã/tên sản phẩm...")) {
                    popupGoiY.setVisible(false); return;
                }
                List<SanPham> ds = sanPhamDAO.timTheoMaHoacTen(text);
                if (!ds.isEmpty()) {
                    modelGoiY.clear();
                    for (SanPham sp : ds) modelGoiY.addElement(sp);
                    popupGoiY.show(txtSearchSanPham, 0, txtSearchSanPham.getHeight());
                    txtSearchSanPham.requestFocus();
                } else { popupGoiY.setVisible(false); }
            }
        });

        tblHoaDonGoc.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && (e.getColumn() == 3 || e.getColumn() == 2)) {
                xuLyKhiThayDoiDonVi(tblHoaDonGoc, e.getFirstRow(), e.getColumn());
            }
        });

        tblSanPham.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.UPDATE) {
                if (e.getColumn() == 2) xuLyKhiThayDoiDonVi(tblSanPham, e.getFirstRow(), e.getColumn());
                if (e.getColumn() != 6) tinhToanToanBoTien();
            }
        });

        txtKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhTienThoi(); }
            public void removeUpdate(DocumentEvent e) { tinhTienThoi(); }
            public void changedUpdate(DocumentEvent e) { tinhTienThoi(); }
        });

        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    private void xuLyKhiThayDoiDonVi(JTable table, int row, int col) {
        if (col != 2 && col != 3) return; 
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (row < 0 || row >= model.getRowCount()) return;

        String maSP = model.getValueAt(row, 0).toString();
        String moTaDonVi = model.getValueAt(row, 2).toString(); 
        DonVi dvEnum = DonVi.tuMoTa(moTaDonVi);
        if (dvEnum == null) return;

        DonViQuyDoi dv = donViQuyDoiDAO.timTheoTenVaMaSP(dvEnum.name(), maSP);
        if (dv != null) {
            double giaMoi = dv.getSanPham().getDonGiaCoBan() * dv.getHeSoQuyDoi();
            SwingUtilities.invokeLater(() -> {
                if (row < model.getRowCount()) {
                    model.setValueAt(giaMoi, row, 4);
                    int sl = Integer.parseInt(model.getValueAt(row, 3).toString());
                    double thueTiLe = Double.parseDouble(model.getValueAt(row, 5).toString());
                    double thanhTienMoi = sl * giaMoi * (1 + thueTiLe);
                    model.setValueAt(thanhTienMoi, row, 6);
                    tinhToanToanBoTien();
                }
            });
        }
    }

    private void timKiemHoaDonGoc(String maHoaDon) {
        hoaDonGocHienTai = hoaDonDAO.layHoaDonDeDoi(maHoaDon);
        if (hoaDonGocHienTai == null) {
            JOptionPane.showMessageDialog(this, "Hóa đơn không hợp lệ hoặc quá hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        txtMaHoaDonGoc.setText(hoaDonGocHienTai.getMaHoaDon());
        txtTenKhachHang.setText(hoaDonGocHienTai.getKhachHang() != null ? hoaDonGocHienTai.getKhachHang().getTenKhachHang() : "Khách vãng lai");
        txtNgayTao.setText(hoaDonGocHienTai.getThoiGianTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        txtNguoiTao.setText(hoaDonGocHienTai.getNhanVien() != null ? hoaDonGocHienTai.getNhanVien().getTenNhanVien() : "N/A");
        
        chiTietHoaDonGocList = chiTietHoaDonDAO.layTheoMaHoaDon(maHoaDon);
        DefaultTableModel model = (DefaultTableModel) tblHoaDonGoc.getModel();
        model.setRowCount(0);
        tongTienHoaDonGocBanDau = 0;

        for(ChiTietHoaDon ct : chiTietHoaDonGocList) {
            double thueTiLe = ct.getDonViQuyDoi().getSanPham().getThue();
            double tt = ct.getSoLuong() * ct.getDonGia() * (1 + thueTiLe);
            model.addRow(new Object[]{
                ct.getDonViQuyDoi().getSanPham().getMaSanPham(), 
                ct.getDonViQuyDoi().getSanPham().getTenSanPham(), 
                ct.getDonViQuyDoi().getTenDonVi().getMoTa(),
                ct.getSoLuong(), 
                ct.getDonGia(), 
                thueTiLe, 
                tt
            });
            tongTienHoaDonGocBanDau += tt;
        }
        txtTienGoc.setText(formatVND(tongTienHoaDonGocBanDau));
        tinhToanToanBoTien();
    }

    private void themSanPhamVaoBang(SanPham sp) {
        if (sp.getLoaiSanPham().name().equals("ETC")) {
            JOptionPane.showMessageDialog(this, "Thuốc kê đơn không được phép đổi trả!", "Cảnh báo", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(sp.getMaSanPham())) {
                int slHienTai = Integer.parseInt(model.getValueAt(i, 3).toString());
                model.setValueAt(slHienTai + 1, i, 3);
                return;
            }
        }
        List<DonViQuyDoi> ds = donViQuyDoiDAO.timTheoMaSanPham(sp.getMaSanPham());
        String dv = ds.isEmpty() ? DonVi.VIEN.getMoTa() : ds.get(0).getTenDonVi().getMoTa();
        model.addRow(new Object[]{sp.getMaSanPham(), sp.getTenSanPham(), dv, 1, sp.getDonGiaCoBan(), sp.getThue(), 0.0});
    }

    private void tinhToanToanBoTien() {
        double tongTienGocHienTai = tinhTienChoBang(tblHoaDonGoc); 
        double tongTienDoiMoi = tinhTienChoBang(tblSanPham); 
        double tongTienHoaDonMoi = tongTienGocHienTai + tongTienDoiMoi;
        
        txtTienDoi.setText(formatVND(tongTienHoaDonMoi)); 
        double chenhLech = tongTienHoaDonMoi - tongTienHoaDonGocBanDau;
        txtChenhLech.setText(formatVND(chenhLech));

        if (radTienMat.isSelected() && chenhLech > 1000) {
            double lamTron = Math.round(chenhLech / 1000.0) * 1000;
            txtThanhTienLamTron.setText(formatVND(lamTron));
        } else { 
            txtThanhTienLamTron.setText(formatVND(Math.max(0, chenhLech))); 
        }
        tinhTienThoi();
    }

    private double tinhTienChoBang(JTable table) {
        double total = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int sl = Integer.parseInt(model.getValueAt(i, 3).toString());
            double gia = Double.parseDouble(model.getValueAt(i, 4).toString());
            double thueTiLe = Double.parseDouble(model.getValueAt(i, 5).toString());
            double tt = sl * gia * (1 + thueTiLe);
            model.setValueAt(tt, i, 6);
            total += tt;
        }
        return total;
    }

    private void tinhTienThoi() {
        try {
            String rawKhachDua = txtKhachDua.getText().trim().replaceAll("[^\\d]", "");
            if (rawKhachDua.isEmpty()) {
                txtTienThoi.setText(formatVND(0)); return;
            }
            double soKhachDua = Double.parseDouble(rawKhachDua);
            String rawThanhTien = txtThanhTienLamTron.getText().replaceAll("[^\\d]", "");
            double soThanhTien = rawThanhTien.isEmpty() ? 0 : Double.parseDouble(rawThanhTien);
            txtTienThoi.setText(formatVND(Math.max(0, soKhachDua - soThanhTien)));
        } catch (Exception e) { txtTienThoi.setText(formatVND(0)); }
    }

    private ChiTietHoaDon taoChiTietTuDong(DefaultTableModel model, int row, HoaDon hd) throws Exception {
        String maSP = model.getValueAt(row, 0).toString();
        String tenDV = model.getValueAt(row, 2).toString();
        int sl = Integer.parseInt(model.getValueAt(row, 3).toString());
        DonViQuyDoi dv = donViQuyDoiDAO.timTheoTenVaMaSP(DonVi.tuMoTa(tenDV).name(), maSP);
        if (dv == null) throw new Exception("Đơn vị '" + tenDV + "' không hợp lệ!");
        
        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setHoaDon(hd); ct.setDonViQuyDoi(dv); ct.setSoLuong(sl);
        ct.setDonGia(dv.getSanPham().getDonGiaCoBan() * dv.getHeSoQuyDoi());
        return ct;
    }

    private void xuLyThanhToan() {
        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
        if (modelDoi.getRowCount() == 0 && modelGoc.getRowCount() == 0) return;

        try {
            double chenhLech = Double.parseDouble(txtChenhLech.getText().replaceAll("[^\\d-]", ""));
            double thanhTienPhaiTra = Double.parseDouble(txtThanhTienLamTron.getText().replaceAll("[^\\d]", ""));
            
            if (radTienMat.isSelected()) {
                String txtDuaRaw = txtKhachDua.getText().trim().replaceAll("[^\\d]", "");
                double khachDua = txtDuaRaw.isEmpty() ? 0 : Double.parseDouble(txtDuaRaw);
                if (chenhLech > 0 && khachDua < thanhTienPhaiTra) {
                    JOptionPane.showMessageDialog(this, "Tiền khách đưa không đủ!"); return;
                }
            }

            LocalDateTime now = LocalDateTime.now();
            String ngayThangNam = now.format(DateTimeFormatter.ofPattern("ddMMyy"));
            int stt = hoaDonDAO.laySoLuongHoaDonTrongNgay("HDD", ngayThangNam) + 1;
            String maHoaDonMoi = String.format("HDD%s%03d", ngayThangNam, stt);

            HoaDon hdMoi = new HoaDon();
            hdMoi.setMaHoaDon(maHoaDonMoi); hdMoi.setThoiGianTao(now);
            hdMoi.setNhanVien(taiKhoanDangNhap.getNhanVien());
            hdMoi.setKhachHang(hoaDonGocHienTai.getKhachHang());
            hdMoi.setLoaiHoaDon(LoaiHoaDon.DOI_HANG);
            hdMoi.setHoaDonDoiTra(hoaDonGocHienTai); hdMoi.setTrangThaiThanhToan(true);
            hdMoi.setPhuongThucThanhToan(radTienMat.isSelected() ? PhuongThucThanhToan.TIEN_MAT : PhuongThucThanhToan.CHUYEN_KHOAN);
            hdMoi.setGhiChu(txtGhiChu.getText());

            CaLam ca = new CaLamDAO().layCaHienTai(taiKhoanDangNhap.getNhanVien().getMaNhanVien());
            if (ca == null) { JOptionPane.showMessageDialog(this, "Chưa mở ca làm việc!"); return; }
            hdMoi.setCa(ca);

            Map<String, ChiTietHoaDon> mapMerge = new HashMap<>();
            for (int i = 0; i < modelGoc.getRowCount(); i++) {
                int sl = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
                if (sl > 0) {
                    ChiTietHoaDon ct = taoChiTietTuDong(modelGoc, i, hdMoi);
                    mapMerge.put(ct.getDonViQuyDoi().getMaDonVi(), ct);
                }
            }
            for (int i = 0; i < modelDoi.getRowCount(); i++) {
                ChiTietHoaDon ctMoi = taoChiTietTuDong(modelDoi, i, hdMoi);
                String maDV = ctMoi.getDonViQuyDoi().getMaDonVi();
                if (mapMerge.containsKey(maDV)) {
                    mapMerge.get(maDV).setSoLuong(mapMerge.get(maDV).getSoLuong() + ctMoi.getSoLuong());
                } else { mapMerge.put(maDV, ctMoi); }
            }

            List<ChiTietHoaDon> dsFinal = new ArrayList<>(mapMerge.values());
            if (hoaDonDAO.luuHoaDon(hdMoi, dsFinal)) {
                if (hoaDonDAO.xacNhanThanhToan(hdMoi.getMaHoaDon(), dsFinal)) {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công: " + maHoaDonMoi);
                    resetForm();
                }
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void resetForm() {
        txtMaHoaDonGoc.setText(""); txtNgayTao.setText(""); txtTenKhachHang.setText(""); txtNguoiTao.setText("");
        txtSearchHoaDon.setText("Nhập mã hóa đơn gốc..."); txtSearchHoaDon.setForeground(Color.GRAY);
        txtTienGoc.setText(formatVND(0)); txtTienDoi.setText(formatVND(0)); txtChenhLech.setText(formatVND(0));
        txtThanhTienLamTron.setText(formatVND(0)); txtKhachDua.setText(""); txtTienThoi.setText(formatVND(0));
        hoaDonGocHienTai = null; chiTietHoaDonGocList.clear(); tongTienHoaDonGocBanDau = 0;
        ((DefaultTableModel) tblHoaDonGoc.getModel()).setRowCount(0);
        ((DefaultTableModel) tblSanPham.getModel()).setRowCount(0);
    }

    private JTable createTable() {
        String[] cols = { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) {
                if (chiTietHoaDonGocList != null && row < chiTietHoaDonGocList.size()) {
                    if (chiTietHoaDonGocList.get(row).getDonViQuyDoi().getSanPham().getLoaiSanPham().name().equals("ETC")) return false;
                }
                return col == 2 || col == 3;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(35);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r, int c) {
                if (v instanceof Number) v = formatVND(((Number) v).doubleValue());
                return super.getTableCellRendererComponent(t, v, s, h, r, c);
            }
        };
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r, int c) {
                if (v != null) v = String.format("%.1f %%", Double.parseDouble(v.toString()) * 100);
                setHorizontalAlignment(JLabel.CENTER);
                return super.getTableCellRendererComponent(t, v, s, h, r, c);
            }
        });
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellEditor(new DynamicUnitCellEditor());
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());
        return table;
    }

    private JPanel createTablePanel(String title, String label, JTextField txt, JTable table, JButton extraBtn) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5)); pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JPanel hdr = new JPanel(new BorderLayout()); hdr.setOpaque(false); hdr.setBorder(new EmptyBorder(10, 10, 5, 10));
        JLabel lbl = new JLabel(title); lbl.setFont(new Font("Segoe UI", Font.BOLD, 18)); hdr.add(lbl, BorderLayout.WEST);
        JPanel s = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0)); s.setOpaque(false);
        s.add(new JLabel(label)); s.add(txt); if (extraBtn != null) s.add(extraBtn);
        hdr.add(s, BorderLayout.EAST); pnl.add(hdr, BorderLayout.NORTH); pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createInfoPanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setPreferredSize(new Dimension(420, 0));
        p.setBackground(Color.WHITE); p.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JLabel title = new JLabel("Hóa đơn đổi hàng", 0); title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(15, 0, 15, 0)); p.add(title, BorderLayout.NORTH);
        JPanel c = new JPanel(new GridBagLayout()); c.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints(); g.fill = 1; g.insets = new Insets(6, 15, 6, 15); g.weightx = 1.0;
        int r = 0;
        addInputRow(c, "Mã hóa đơn gốc:", txtMaHoaDonGoc = new JTextField(), g, r++);
        addInputRow(c, "Ngày tạo:", txtNgayTao = new JTextField(), g, r++);
        addInputRow(c, "Người tạo:", txtNguoiTao = new JTextField(), g, r++);
        addInputRow(c, "Khách hàng:", txtTenKhachHang = new JTextField(), g, r++);
        g.gridy = r++; c.add(new JLabel("Ghi chú:"), g);
        g.gridy = r++; txtGhiChu = new JTextArea(2, 20); txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        c.add(new JScrollPane(txtGhiChu), g);
        addInputRow(c, "Tiền HĐ gốc:", txtTienGoc = new JTextField("0 VNĐ"), g, r++);
        addInputRow(c, "Tiền HĐ đổi:", txtTienDoi = new JTextField("0 VNĐ"), g, r++);
        addInputRow(c, "Chênh lệch:", txtChenhLech = new JTextField("0 VNĐ"), g, r++);
        g.gridy = r++; JPanel rad = new JPanel(new FlowLayout(0, 0, 10)); rad.setOpaque(false);
        rad.add(new JLabel("PT Thanh toán: ")); rad.add(radTienMat = new JRadioButton("Tiền mặt", true));
        rad.add(radChuyenKhoan = new JRadioButton("Chuyển khoản")); ButtonGroup bg = new ButtonGroup();
        bg.add(radTienMat); bg.add(radChuyenKhoan); c.add(rad, g);
        g.gridy = r++; g.weighty = 1.0; pnlDynamicContent = new JPanel(new CardLayout()); pnlDynamicContent.setOpaque(false);
        JPanel cash = new JPanel(new GridBagLayout()); cash.setOpaque(false);
        txtThanhTienLamTron = new JTextField("0 VNĐ"); txtThanhTienLamTron.setEditable(false); txtThanhTienLamTron.setBackground(new Color(245, 245, 245));
        txtKhachDua = new JTextField(); txtTienThoi = new JTextField("0 VNĐ"); txtTienThoi.setEditable(false); txtTienThoi.setBackground(new Color(245, 245, 245));
        pnlThanhTienContainer = new JPanel(new BorderLayout(10, 0)); pnlThanhTienContainer.setOpaque(false);
        JLabel lblThanhTien = new JLabel("Thành tiền (đã làm tròn):"); lblThanhTien.setPreferredSize(new Dimension(150, 25));
        pnlThanhTienContainer.add(lblThanhTien, BorderLayout.WEST); pnlThanhTienContainer.add(txtThanhTienLamTron, BorderLayout.CENTER);
        GridBagConstraints gc = new GridBagConstraints() {{fill=1;weightx=1;gridy=0;insets=new Insets(0,0,15,0);}};
        cash.add(pnlThanhTienContainer, gc); addInputRow(cash, "Tiền khách đưa:", txtKhachDua, gc, 1); addInputRow(cash, "Tiền thối lại:", txtTienThoi, gc, 2);
        pnlDynamicContent.add(cash, "CASH"); pnlDynamicContent.add(createQRPanel(), "QR"); c.add(pnlDynamicContent, g);
        radTienMat.addActionListener(e -> {((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "CASH"); tinhToanToanBoTien();});
        radChuyenKhoan.addActionListener(e -> {((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "QR"); tinhToanToanBoTien();});
        btnThanhToan = new JButton("THANH TOÁN"); btnThanhToan.setBackground(new Color(40, 167, 69)); btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18)); btnThanhToan.setPreferredSize(new Dimension(0, 50));
        p.add(c, BorderLayout.CENTER); p.add(btnThanhToan, BorderLayout.SOUTH);
        setupReadOnlyFields(); return p;
    }

    private JPanel createQRPanel() {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        JLabel l = new JLabel("HÌNH ẢNH MÃ QR", 0); l.setPreferredSize(new Dimension(0, 150)); l.setOpaque(true);
        l.setBackground(Color.WHITE); l.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.add(l, BorderLayout.CENTER); return p;
    }

    private void addInputRow(JPanel p, String lbl, JTextField t, GridBagConstraints g, int r) {
        g.gridy = r; JPanel row = new JPanel(new BorderLayout(10, 0)); row.setOpaque(false);
        JLabel l = new JLabel(lbl); l.setPreferredSize(new Dimension(120, 25));
        row.add(l, BorderLayout.WEST); row.add(t, BorderLayout.CENTER); p.add(row, g);
    }

    private void setupReadOnlyFields() {
        JTextField[] rs = { txtMaHoaDonGoc, txtNgayTao, txtNguoiTao, txtTenKhachHang, txtTienGoc, txtTienDoi, txtChenhLech };
        for (JTextField f : rs) { f.setEditable(false); f.setBackground(new Color(245, 245, 245)); }
    }

    private String formatVND(double amount) {
        if (amount == 0) return "0 VNĐ";
        return String.format(new java.util.Locale("vi", "VN"), "%,.0f VNĐ", amount).replace(",", ".");
    }
    
    private class DynamicUnitCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JComboBox<DonVi> cb = new JComboBox<>();
        public DynamicUnitCellEditor() {
            cb.setRenderer(new DefaultListCellRenderer() {
                @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof DonVi) setText(((DonVi) value).getMoTa());
                    return this;
                }
            });
        }
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            cb.removeAllItems();
            List<DonViQuyDoi> ds = donViQuyDoiDAO.timTheoMaSanPham(t.getValueAt(r, 0).toString());
            for (DonViQuyDoi dv : ds) cb.addItem(dv.getTenDonVi());
            for (int i = 0; i < cb.getItemCount(); i++) if (cb.getItemAt(i).getMoTa().equals(v)) { cb.setSelectedIndex(i); break; }
            return cb;
        }
        @Override public Object getCellEditorValue() { return ((DonVi) cb.getSelectedItem()).getMoTa(); }
    }

    private class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner s = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sl, int r, int c) {
            s.setValue(v); return s;
        }
        @Override public Object getCellEditorValue() { return s.getValue(); }
    }
}