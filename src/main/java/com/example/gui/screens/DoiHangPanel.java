package com.example.gui.screens;

import com.example.dao.*;
import com.example.entity.*;
import com.example.entity.enums.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
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
import java.util.List;

public class DoiHangPanel extends JPanel {

    private JTextField txtMaHoaDonGoc, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienDoi, txtChenhLech, txtThanhTienLamTron, txtKhachDua, txtTienThoi;
    private JTextField txtSearchHoaDon, txtSearchSanPham;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JRadioButton radTienMat, radChuyenKhoan;
    private JPanel pnlDynamicContent, pnlThanhTienContainer;
    private JTable tblHoaDonGoc, tblSanPham;

    // --- Suggestion Components ---
    private JPopupMenu popupGoiY;
    private JList<SanPham> listGoiY;
    private DefaultListModel<SanPham> modelGoiY;

    // --- DAO ---
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO chiTietHoaDonDAO = new ChiTietHoaDonDAO();
    private SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private SuPhanBoLoDAO suPhanBoLoDAO = new SuPhanBoLoDAO();
    private LoDAO loDAO = new LoDAO();
    private DonViQuyDoiDAO donViQuyDoiDAO = new DonViQuyDoiDAO();

    // --- State ---
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

        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn gốc", "Tìm hóa đơn:", txtSearchHoaDon,
                tblHoaDonGoc = createTable()));
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn đổi", "Tìm kiếm sản phẩm:", txtSearchSanPham,
                tblSanPham = createTable()));

        add(pnlLeft, BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);

        initSuggestionPopup();
        initEvents();
    }

    private void setupPlaceholder(JTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    private void initSuggestionPopup() {
        popupGoiY = new JPopupMenu();
        modelGoiY = new DefaultListModel<>();
        listGoiY = new JList<>(modelGoiY);
        listGoiY.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
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
            @Override
            public void mouseClicked(MouseEvent e) {
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
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String kw = txtSearchHoaDon.getText().trim();
                    if (!kw.isEmpty() && !kw.equals("Nhập mã hóa đơn gốc..."))
                        timKiemHoaDonGoc(kw);
                }
            }
        });

        txtSearchSanPham.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                showPopup();
            }

            public void removeUpdate(DocumentEvent e) {
                showPopup();
            }

            public void changedUpdate(DocumentEvent e) {
                showPopup();
            }

            private void showPopup() {
                String text = txtSearchSanPham.getText().trim();
                if (text.isEmpty() || text.equals("Nhập mã/tên sản phẩm...")) {
                    popupGoiY.setVisible(false);
                    return;
                }
                List<SanPham> ds = sanPhamDAO.timTheoMaHoacTen(text);
                if (!ds.isEmpty()) {
                    modelGoiY.clear();
                    for (SanPham sp : ds)
                        modelGoiY.addElement(sp);
                    popupGoiY.show(txtSearchSanPham, 0, txtSearchSanPham.getHeight());
                    txtSearchSanPham.requestFocus();
                } else {
                    popupGoiY.setVisible(false);
                }
            }
        });

        tblHoaDonGoc.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && (e.getColumn() == 3 || e.getColumn() == 2)) {
                xuLyKhiThayDoiDonVi(tblHoaDonGoc, e.getFirstRow(), e.getColumn());
                tinhToanToanBoTien();
            }
        });

        tblSanPham.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.UPDATE) {
                if (e.getColumn() == 2)
                    xuLyKhiThayDoiDonVi(tblSanPham, e.getFirstRow(), e.getColumn());
                if (e.getColumn() != 6)
                    tinhToanToanBoTien();
            }
        });

        txtKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                tinhTienThoi();
            }

            public void removeUpdate(DocumentEvent e) {
                tinhTienThoi();
            }

            public void changedUpdate(DocumentEvent e) {
                tinhTienThoi();
            }
        });

        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    private void xuLyKhiThayDoiDonVi(JTable table, int row, int col) {
        if (col != 2)
            return;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String maSP = model.getValueAt(row, 0).toString();
        String tenDV = model.getValueAt(row, 2).toString();
        DonViQuyDoi dv = donViQuyDoiDAO.timTheoTenVaMaSP(tenDV, maSP);
        if (dv != null) {
            double giaMoi = dv.getSanPham().getDonGiaCoBan() * dv.getHeSoQuyDoi();
            model.setValueAt(giaMoi, row, 4);
        }
    }

    private void timKiemHoaDonGoc(String maHoaDon) {
        hoaDonGocHienTai = hoaDonDAO.layHoaDonDeDoi(maHoaDon);
        if (hoaDonGocHienTai == null) {
            JOptionPane.showMessageDialog(this, "Hóa đơn không hợp lệ hoặc quá hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        txtMaHoaDonGoc.setText(hoaDonGocHienTai.getMaHoaDon());
        txtTenKhachHang
                .setText(hoaDonGocHienTai.getKhachHang() != null ? hoaDonGocHienTai.getKhachHang().getTenKhachHang()
                        : "Khách vãng lai");
        txtNgayTao
                .setText(hoaDonGocHienTai.getThoiGianTao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        txtNguoiTao.setText(
                hoaDonGocHienTai.getNhanVien() != null ? hoaDonGocHienTai.getNhanVien().getTenNhanVien() : "N/A");

        chiTietHoaDonGocList = chiTietHoaDonDAO.layTheoMaHoaDon(maHoaDon);
        DefaultTableModel model = (DefaultTableModel) tblHoaDonGoc.getModel();
        model.setRowCount(0);
        tongTienHoaDonGocBanDau = 0;

        for (ChiTietHoaDon ct : chiTietHoaDonGocList) {
            double tt = ct.getSoLuong() * ct.getDonGia() * (1 + ct.getDonViQuyDoi().getSanPham().getThue() / 100);
            model.addRow(new Object[] { ct.getDonViQuyDoi().getSanPham().getMaSanPham(),
                    ct.getDonViQuyDoi().getSanPham().getTenSanPham(),
                    ct.getDonViQuyDoi().getTenDonVi().name(), ct.getSoLuong(), ct.getDonGia(),
                    ct.getDonViQuyDoi().getSanPham().getThue(), tt });
            tongTienHoaDonGocBanDau += tt;
        }
        txtTienGoc.setText(String.format("%.0f", tongTienHoaDonGocBanDau));
        tinhToanToanBoTien();
    }

    private void themSanPhamVaoBang(SanPham sp) {
        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(sp.getMaSanPham())) {
                model.setValueAt((int) model.getValueAt(i, 3) + 1, i, 3);
                txtSearchSanPham.setText("");
                return;
            }
        }
        List<DonViQuyDoi> ds = donViQuyDoiDAO.timTheoMaSanPham(sp.getMaSanPham());
        String dv = ds.isEmpty() ? "VIEN" : ds.get(0).getTenDonVi().name();
        model.addRow(
                new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), dv, 1, sp.getDonGiaCoBan(), sp.getThue(), 0.0 });
        txtSearchSanPham.setText("");
    }

    private void tinhToanToanBoTien() {
        double tongTienMoi = tinhTienChoBang(tblHoaDonGoc) + tinhTienChoBang(tblSanPham);
        txtTienDoi.setText(String.format("%.0f", tongTienMoi));
        double chenhLech = tongTienMoi - tongTienHoaDonGocBanDau;
        txtChenhLech.setText(String.format("%.0f", chenhLech));

        if (radTienMat.isSelected() && chenhLech > 0) {
            double lamTron = Math.round(chenhLech / 1000.0) * 1000;
            txtThanhTienLamTron.setText(String.format("%.0f", lamTron));
        } else {
            txtThanhTienLamTron.setText(String.format("%.0f", Math.max(0, chenhLech)));
        }
        tinhTienThoi();
    }

    private double tinhTienChoBang(JTable table) {
        double total = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            int sl = Integer.parseInt(model.getValueAt(i, 3).toString());
            double gia = Double.parseDouble(model.getValueAt(i, 4).toString());
            double thue = Double.parseDouble(model.getValueAt(i, 5).toString());
            double tt = sl * gia * (1 + thue / 100);
            model.setValueAt(tt, i, 6);
            total += tt;
        }
        return total;
    }

    private void tinhTienThoi() {
        try {
            double dua = txtKhachDua.getText().isEmpty() ? 0 : Double.parseDouble(txtKhachDua.getText());
            double thanhTien = Double.parseDouble(txtThanhTienLamTron.getText());
            txtTienThoi.setText(thanhTien > 0 ? String.format("%.0f", Math.max(0, dua - thanhTien)) : "0");
        } catch (Exception e) {
            txtTienThoi.setText("0");
        }
    }

    private void xuLyThanhToan() {
        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
        if (modelDoi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm mới để đổi!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double chenhLech = Double.parseDouble(txtChenhLech.getText());
            double thanhTienPhaiTra = Double.parseDouble(txtThanhTienLamTron.getText());
            double khachDua = txtKhachDua.getText().trim().isEmpty() ? 0
                    : Double.parseDouble(txtKhachDua.getText().trim());

            if (chenhLech > 0 && khachDua < thanhTienPhaiTra) {
                JOptionPane.showMessageDialog(this, "Tiền khách đưa không đủ để thanh toán phần chênh lệch!", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            String ngayThangNam = now.format(DateTimeFormatter.ofPattern("ddMMyy"));
            String prefix = "HDD";
            int sttTiepTheo = hoaDonDAO.laySoLuongHoaDonTrongNgay(prefix, ngayThangNam) + 1;
            String maHoaDonMoi = String.format("%s%s%03d", prefix, ngayThangNam, sttTiepTheo);

            HoaDon hdMoi = new HoaDon();
            hdMoi.setMaHoaDon(maHoaDonMoi);
            hdMoi.setThoiGianTao(now);
            hdMoi.setNhanVien(taiKhoanDangNhap.getNhanVien());
            hdMoi.setKhachHang(hoaDonGocHienTai.getKhachHang());
            hdMoi.setLoaiHoaDon(LoaiHoaDon.DOI_HANG);
            hdMoi.setHoaDonDoiTra(hoaDonGocHienTai);
            hdMoi.setTrangThaiThanhToan(true);
            hdMoi.setPhuongThucThanhToan(
                    radTienMat.isSelected() ? PhuongThucThanhToan.TIEN_MAT : PhuongThucThanhToan.CHUYEN_KHOAN);
            hdMoi.setGhiChu(txtGhiChu.getText());

            CaLamDAO caLamDAO = new CaLamDAO();
            CaLam caHienTai = caLamDAO.layCaHienTai(taiKhoanDangNhap.getNhanVien().getMaNhanVien());
            if (caHienTai == null) {
                JOptionPane.showMessageDialog(this, "Bạn chưa mở ca làm việc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            hdMoi.setCa(caHienTai);

            List<SuPhanBoLo> dsTraLai = new ArrayList<>();
            DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();

            for (int i = 0; i < modelGoc.getRowCount(); i++) {
                int slHienTaiTrenBang = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
                ChiTietHoaDon ctBanDau = chiTietHoaDonGocList.get(i);
                int slKhachTraLai = ctBanDau.getSoLuong() - slHienTaiTrenBang;

                if (slKhachTraLai > 0) {
                    List<SuPhanBoLo> phanBoCu = suPhanBoLoDAO.layPhanBoLoCuaChiTiet(
                            hoaDonGocHienTai.getMaHoaDon(), ctBanDau.getDonViQuyDoi().getMaDonVi());

                    if (!phanBoCu.isEmpty()) {
                        SuPhanBoLo spbTra = new SuPhanBoLo();
                        spbTra.setLo(phanBoCu.get(0).getLo());
                        spbTra.setSoLuong(slKhachTraLai);
                        dsTraLai.add(spbTra);
                    }
                }
            }

            List<ChiTietHoaDon> dsChiTietMoi = new ArrayList<>();
            List<SuPhanBoLo> dsPhanBoMoi = new ArrayList<>();

            for (int i = 0; i < modelDoi.getRowCount(); i++) {
                String maSP = modelDoi.getValueAt(i, 0).toString();
                String tenDV = modelDoi.getValueAt(i, 2).toString();
                int slMua = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                double donGia = Double.parseDouble(modelDoi.getValueAt(i, 4).toString());

                DonViQuyDoi dv = donViQuyDoiDAO.timTheoTenVaMaSP(tenDV, maSP);
                if (dv == null) {
                    throw new Exception("Đơn vị '" + tenDV + "' không hợp lệ cho sản phẩm " + maSP);
                }

                ChiTietHoaDon ctMoi = new ChiTietHoaDon();
                ctMoi.setHoaDon(hdMoi);
                ctMoi.setDonViQuyDoi(dv);
                ctMoi.setSoLuong(slMua);
                ctMoi.setDonGia(donGia);

                List<Lo> dsLoKhaDung = loDAO.layDanhSachLoKhaDung(dv.getMaDonVi());
                int slCanTru = slMua;

                for (Lo lo : dsLoKhaDung) {
                    if (slCanTru <= 0)
                        break;
                    int layTuLo = Math.min(lo.getSoLuongSanPham(), slCanTru);

                    SuPhanBoLo spb = new SuPhanBoLo();
                    spb.setChiTietHoaDon(ctMoi);
                    spb.setLo(lo);
                    spb.setSoLuong(layTuLo);
                    dsPhanBoMoi.add(spb);
                    slCanTru -= layTuLo;
                }

                if (slCanTru > 0) {
                    throw new Exception(
                            "Sản phẩm " + maSP + " (" + tenDV + ") không đủ tồn kho trong hệ thống lô hàng!");
                }
                dsChiTietMoi.add(ctMoi);
            }

            if (hoaDonDAO.luuHoaDonDoiHang(hdMoi, dsTraLai, dsChiTietMoi, dsPhanBoMoi)) {
                JOptionPane.showMessageDialog(this,
                        "Thanh toán đổi hàng thành công!\nMã hóa đơn: " + hdMoi.getMaHoaDon(), "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Giao dịch thất bại! Vui lòng kiểm tra lại kết nối CSDL.", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void resetForm() {
        txtMaHoaDonGoc.setText("");
        txtNgayTao.setText("");
        txtTenKhachHang.setText("");
        txtNguoiTao.setText("");
        txtGhiChu.setText("");
        txtKhachDua.setText("");
        hoaDonGocHienTai = null;
        ((DefaultTableModel) tblHoaDonGoc.getModel()).setRowCount(0);
        ((DefaultTableModel) tblSanPham.getModel()).setRowCount(0);
        tinhToanToanBoTien();
    }

    private JTable createTable() {
        String[] cols = { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế (%)", "Thành tiền" };
        JTable table = new JTable(new DefaultTableModel(cols, 0));
        table.setRowHeight(35);
        table.getColumnModel().getColumn(2).setCellEditor(new DynamicUnitCellEditor());
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());
        return table;
    }

    private JPanel createTablePanel(String title, String label, JTextField txt, JTable table) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JPanel hdr = new JPanel(new BorderLayout());
        hdr.setOpaque(false);
        hdr.setBorder(new EmptyBorder(10, 10, 5, 10));
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        hdr.add(lbl, BorderLayout.WEST);
        JPanel s = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        s.setOpaque(false);
        s.add(new JLabel(label));
        s.add(txt);
        hdr.add(s, BorderLayout.EAST);
        pnl.add(hdr, BorderLayout.NORTH);
        pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createInfoPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(420, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        JLabel title = new JLabel("Hóa đơn đổi hàng", 0);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(new EmptyBorder(15, 0, 15, 0));
        p.add(title, BorderLayout.NORTH);
        JPanel c = new JPanel(new GridBagLayout());
        c.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 1;
        g.insets = new Insets(6, 15, 6, 15);
        g.weightx = 1.0;
        int r = 0;
        addInputRow(c, "Mã hóa đơn gốc:", txtMaHoaDonGoc = new JTextField(), g, r++);
        addInputRow(c, "Ngày tạo:", txtNgayTao = new JTextField(), g, r++);
        addInputRow(c, "Người tạo:", txtNguoiTao = new JTextField(), g, r++);
        addInputRow(c, "Khách hàng:", txtTenKhachHang = new JTextField(), g, r++);
        g.gridy = r++;
        c.add(new JLabel("Ghi chú:"), g);
        g.gridy = r++;
        txtGhiChu = new JTextArea(2, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        c.add(new JScrollPane(txtGhiChu), g);
        addInputRow(c, "Tiền HĐ gốc:", txtTienGoc = new JTextField("0"), g, r++);
        addInputRow(c, "Tiền HĐ đổi:", txtTienDoi = new JTextField("0"), g, r++);
        addInputRow(c, "Chênh lệch:", txtChenhLech = new JTextField("0"), g, r++);
        g.gridy = r++;
        JPanel rad = new JPanel(new FlowLayout(0, 0, 10));
        rad.setOpaque(false);
        rad.add(new JLabel("PT Thanh toán: "));
        rad.add(radTienMat = new JRadioButton("Tiền mặt", true));
        rad.add(radChuyenKhoan = new JRadioButton("Chuyển khoản"));
        ButtonGroup bg = new ButtonGroup();
        bg.add(radTienMat);
        bg.add(radChuyenKhoan);
        c.add(rad, g);
        g.gridy = r++;
        g.weighty = 1.0;
        pnlDynamicContent = new JPanel(new CardLayout());
        pnlDynamicContent.setOpaque(false);
        JPanel cash = new JPanel(new GridBagLayout());
        cash.setOpaque(false);
        pnlThanhTienContainer = new JPanel(new BorderLayout(10, 0));
        pnlThanhTienContainer.setOpaque(false);
        pnlThanhTienContainer.add(new JLabel("Thành tiền (làm tròn):") {
            {
                setPreferredSize(new Dimension(130, 25));
            }
        }, BorderLayout.WEST);
        pnlThanhTienContainer.add(txtThanhTienLamTron = new JTextField("0"), BorderLayout.CENTER);
        txtThanhTienLamTron.setEditable(false);
        txtThanhTienLamTron.setBackground(new Color(245, 245, 245));
        GridBagConstraints gc = new GridBagConstraints() {
            {
                fill = 1;
                weightx = 1;
                gridy = 0;
                insets = new Insets(0, 0, 20, 0);
            }
        };
        cash.add(pnlThanhTienContainer, gc);
        addInputRow(cash, "Tiền khách đưa:", txtKhachDua = new JTextField(), gc, 1);
        addInputRow(cash, "Tiền thối lại:", txtTienThoi = new JTextField() {
            {
                setEditable(false);
                setBackground(new Color(245, 245, 245));
            }
        }, gc, 2);
        pnlDynamicContent.add(cash, "CASH");
        pnlDynamicContent.add(createQRPanel(), "QR");
        c.add(pnlDynamicContent, g);
        radTienMat.addActionListener(e -> {
            ((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "CASH");
            tinhToanToanBoTien();
        });
        radChuyenKhoan.addActionListener(e -> {
            ((CardLayout) pnlDynamicContent.getLayout()).show(pnlDynamicContent, "QR");
            tinhToanToanBoTien();
        });
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThanhToan.setPreferredSize(new Dimension(0, 50));
        p.add(c, BorderLayout.CENTER);
        p.add(btnThanhToan, BorderLayout.SOUTH);
        setupReadOnlyFields();
        return p;
    }

    private JPanel createQRPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel("HÌNH ẢNH MÃ QR", 0);
        l.setPreferredSize(new Dimension(0, 150));
        l.setOpaque(true);
        l.setBackground(Color.WHITE);
        l.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private void addInputRow(JPanel p, String lbl, JTextField t, GridBagConstraints g, int r) {
        g.gridy = r;
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        JLabel l = new JLabel(lbl);
        l.setPreferredSize(new Dimension(120, 25));
        row.add(l, BorderLayout.WEST);
        row.add(t, BorderLayout.CENTER);
        p.add(row, g);
    }

    private void setupReadOnlyFields() {
        JTextField[] rs = { txtMaHoaDonGoc, txtNgayTao, txtNguoiTao, txtTenKhachHang, txtTienGoc, txtTienDoi,
                txtChenhLech };
        for (JTextField f : rs) {
            f.setEditable(false);
            f.setBackground(new Color(245, 245, 245));
        }
    }

    private class DynamicUnitCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JComboBox<String> cb = new JComboBox<>();

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            cb.removeAllItems();
            List<DonViQuyDoi> ds = donViQuyDoiDAO.timTheoMaSanPham(t.getValueAt(r, 0).toString());
            for (DonViQuyDoi dv : ds)
                cb.addItem(dv.getTenDonVi().name());
            cb.setSelectedItem(v);
            return cb;
        }

        @Override
        public Object getCellEditorValue() {
            return cb.getSelectedItem();
        }
    }

    private class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner s = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sl, int r, int c) {
            s.setValue(v);
            return s;
        }

        @Override
        public Object getCellEditorValue() {
            return s.getValue();
        }
    }
}