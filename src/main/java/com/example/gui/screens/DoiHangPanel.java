package com.example.gui.screens;

import com.example.service.ChiTietHoaDonService;
import com.example.service.DonViQuyDoiService;
import com.example.service.LoService;
import com.example.service.HoaDonService;
import com.example.service.SanPhamService;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

    private HoaDonService hoaDonService = new HoaDonService();
    private ChiTietHoaDonService chiTietHoaDonService = new ChiTietHoaDonService();
    private SanPhamService sanPhamService = new SanPhamService();
    private DonViQuyDoiService donViQuyDoiService = new DonViQuyDoiService();

    private HoaDon hoaDonGocHienTai = null;
    private List<ChiTietHoaDon> chiTietHoaDonGocList = new ArrayList<>();
    private double tongTienHoaDonGocBanDau = 0;
    private TaiKhoan taiKhoanDangNhap;
    private final StringBuilder barcodeBuffer = new StringBuilder();
    private long lastKeyTime = 0;

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
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!", "Chưa chọn dòng",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        tblHoaDonGoc = createTable(true);
        tblSanPham = createTable(false);

        tblHoaDonGoc.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblHoaDonGoc.getSelectedRow();
                if (row != -1 && chiTietHoaDonGocList != null && row < chiTietHoaDonGocList.size()) {
                    ChiTietHoaDon ct = chiTietHoaDonGocList.get(row);
                    if (ct.getDonViQuyDoi().getSanPham().getLoaiSanPham().name().equals("ETC")) {
                        JOptionPane.showMessageDialog(null, "Đây là thuốc kê đơn (ETC). Không được phép thay đổi!",
                                "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });

        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn gốc", "Tìm hóa đơn:", txtSearchHoaDon, tblHoaDonGoc, null));
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn đổi", "Tìm kiếm sản phẩm:", txtSearchSanPham, tblSanPham,
                btnXoaDong));

        add(pnlLeft, BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);

        initSuggestionPopup();
        initEvents();

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("focusOwner", evt -> {
            if (!isShowing()) {
                return;
            }
            Component focused = (Component) evt.getNewValue();
            if (focused != null && SwingUtilities.isDescendingFrom(focused, DoiHangPanel.this)) {
                boolean isEditableText = (focused instanceof javax.swing.text.JTextComponent) 
                                        && ((javax.swing.text.JTextComponent) focused).isEditable();
                boolean isInteractiveControl = (focused instanceof JComboBox)
                                            || (focused instanceof JCheckBox)
                                            || (focused instanceof JRadioButton)
                                            || (focused instanceof JButton);
                boolean isTableEditing = (tblHoaDonGoc != null && tblHoaDonGoc.isEditing() && SwingUtilities.isDescendingFrom(focused, tblHoaDonGoc))
                                      || (tblSanPham != null && tblSanPham.isEditing() && SwingUtilities.isDescendingFrom(focused, tblSanPham));

                if (!isEditableText && !isInteractiveControl && !isTableEditing) {
                    SwingUtilities.invokeLater(() -> {
                        if (txtSearchSanPham != null && txtSearchSanPham.isShowing()) {
                            txtSearchSanPham.requestFocusInWindow();
                        }
                    });
                }
            }
        });

        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    SwingUtilities.invokeLater(() -> {
                        if (txtSearchSanPham != null && txtSearchSanPham.isShowing()) {
                            txtSearchSanPham.requestFocusInWindow();
                        }
                    });
                }
            }
        });

        // Thiết lập bộ đón bắt phím toàn cục (KeyEventDispatcher) cho máy quét barcode
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (!isShowing()) {
                return false;
            }

            // Ngăn ngừa lỗi đúp sự kiện khi người dùng đang active focus trong các ô nhập văn bản (txtSearchSanPham, txtSearchHoaDon, v.v.)
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            boolean isEditableFocused = (focusOwner instanceof javax.swing.text.JTextComponent) 
                                        && ((javax.swing.text.JTextComponent) focusOwner).isEditable();
            if (isEditableFocused) {
                return false;
            }

            if (e.getID() == KeyEvent.KEY_TYPED) {
                long now = System.currentTimeMillis();
                char c = e.getKeyChar();

                // Nếu khoảng cách giữa 2 ký tự lớn hơn 50ms, coi như nhập liệu thủ công bằng bàn phím
                if (now - lastKeyTime > 50) {
                    barcodeBuffer.setLength(0);
                }
                lastKeyTime = now;

                if (c == '\n') {
                    String barcode = barcodeBuffer.toString().trim();
                    if (!barcode.isEmpty() && barcode.length() >= 5) {
                        DonViQuyDoi dv = donViQuyDoiService.timTheoBarcode(barcode);
                        if (dv != null) {
                            SwingUtilities.invokeLater(() -> {
                                xuLyQuetBarcode(dv);
                                if (txtSearchSanPham != null) {
                                    txtSearchSanPham.setText("");
                                }
                            });
                            barcodeBuffer.setLength(0);
                            return true; // Tiêu hủy sự kiện phím Enter
                        }
                    }
                    barcodeBuffer.setLength(0);
                } else if (Character.isLetterOrDigit(c)) {
                    barcodeBuffer.append(c);
                }
            }
            return false;
        });
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

        txtSearchSanPham.addActionListener(e -> {
            String text = txtSearchSanPham.getText().trim();
            if (!text.isEmpty() && !text.equals("Nhập mã/tên sản phẩm...")) {
                DonViQuyDoi dv = donViQuyDoiService.timTheoBarcode(text);
                if (dv != null) {
                    xuLyQuetBarcode(dv);
                    txtSearchSanPham.setText("");
                } else {
                    SanPham sp = sanPhamService.timTheoMa(text);
                    if (sp != null) {
                        List<DonViQuyDoi> donVis = donViQuyDoiService.timTheoMaSanPham(sp.getMaSanPham());
                        if (!donVis.isEmpty()) {
                            themSanPhamVaoBang(sp, donVis.get(0));
                            txtSearchSanPham.setText("");
                            popupGoiY.setVisible(false);
                        }
                    }
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
                List<SanPham> ds = sanPhamService.timTheoMaHoacTen(text);
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
            if (e.getType() == TableModelEvent.UPDATE && (e.getColumn() == 3 || e.getColumn() == 2 || e.getColumn() == 7)) {
                xuLyKhiThayDoiDonVi(tblHoaDonGoc, e.getFirstRow(), e.getColumn());
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
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String maSP = model.getValueAt(row, 0).toString();

        // LOGIC CHẶN SỐ LƯỢNG ĐỔI VÀ SỐ LƯỢNG LỖI (Bảng hóa đơn gốc)
        if (table == tblHoaDonGoc && (col == 3 || col == 7)) {
            int slTraNhap = Integer.parseInt(model.getValueAt(row, 3).toString());
            int slLoiNhap = Integer.parseInt(model.getValueAt(row, 7).toString());

            ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(row);
            int slDaMua = ctGoc.getSoLuong();

            if (slTraNhap < 0 || slTraNhap > slDaMua) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng đổi phải từ 0 đến " + slDaMua + " (số lượng đã mua)!",
                        "Cảnh báo dữ liệu", JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(() -> model.setValueAt(0, row, 3));
                return;
            }

            if (slLoiNhap < 0 || slLoiNhap > slTraNhap) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng lỗi phải từ 0 đến " + slTraNhap + " (số lượng đổi)!",
                        "Cảnh báo dữ liệu", JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(() -> model.setValueAt(0, row, 7));
                return;
            }
        }

        // LOGIC CẬP NHẬT GIÁ THEO ĐƠN VỊ VÀ TÍNH TIỀN
        String moTaDonVi = model.getValueAt(row, 2).toString();
        DonVi dvEnum = DonVi.tuMoTa(moTaDonVi);
        DonViQuyDoi dv = donViQuyDoiService.timTheoTenVaMaSP(dvEnum.name(), maSP);

        if (dv != null) {
            double giaMoi = dv.getSanPham().getDonGiaCoBan() * dv.getHeSoQuyDoi();
            SwingUtilities.invokeLater(() -> {
                model.setValueAt(giaMoi, row, 4);
                Object slValue = model.getValueAt(row, 3);
                int sl = (slValue != null && !slValue.toString().trim().isEmpty())
                        ? Integer.parseInt(slValue.toString())
                        : 0;
                double thueTiLe = Double.parseDouble(model.getValueAt(row, 5).toString());
                
                if (table == tblHoaDonGoc) {
                    ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(row);
                    int slDaMua = ctGoc.getSoLuong();
                    int slGiuLai = slDaMua - sl;
                    model.setValueAt(slGiuLai * giaMoi * (1 + thueTiLe / 100.0), row, 6);
                } else {
                    model.setValueAt(sl * giaMoi * (1 + thueTiLe / 100.0), row, 6);
                }
                tinhToanToanBoTien();
            });
        }
    }

    private void timKiemHoaDonGoc(String maHoaDon) {
        hoaDonGocHienTai = hoaDonService.layHoaDonDeDoi(maHoaDon);
        if (hoaDonGocHienTai == null) {
            JOptionPane.showMessageDialog(this, "Hóa đơn không hợp lệ hoặc quá hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            resetForm();
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

        List<ChiTietHoaDon> temp = chiTietHoaDonService.layTheoMaHoaDon(maHoaDon);
        chiTietHoaDonGocList = new ArrayList<>();
        for (ChiTietHoaDon ct : temp) {
            if (!ct.isLaQuaTangKem()) {
                chiTietHoaDonGocList.add(ct);
            }
        }
        DefaultTableModel model = (DefaultTableModel) tblHoaDonGoc.getModel();
        model.setRowCount(0);
        tongTienHoaDonGocBanDau = 0;

        for (ChiTietHoaDon ct : chiTietHoaDonGocList) {
            double thueTiLe = ct.getDonViQuyDoi().getSanPham().getThue();
            double tt = ct.getSoLuong() * ct.getDonGia() * (1 + thueTiLe / 100.0);
            model.addRow(new Object[] {
                    ct.getDonViQuyDoi().getSanPham().getMaSanPham(),
                    ct.getDonViQuyDoi().getSanPham().getTenSanPham(),
                    ct.getDonViQuyDoi().getTenDonVi().getMoTa(),
                    ct.getSoLuong(),
                    ct.getDonGia(),
                    thueTiLe,
                    0.0,
                    0
            });
            tongTienHoaDonGocBanDau += tt;
        }
        if (hoaDonGocHienTai.getKhuyenMai() != null
                && hoaDonGocHienTai.getKhuyenMai().getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            tongTienHoaDonGocBanDau = tongTienHoaDonGocBanDau
                    * (1 - hoaDonGocHienTai.getKhuyenMai().getKhuyenMaiPhanTram() / 100.0);
        }
        txtTienGoc.setText(formatVND(tongTienHoaDonGocBanDau));
        tinhToanToanBoTien();
    }

    private void xuLyQuetBarcode(DonViQuyDoi dv) {
        if (dv == null) {
            return;
        }
        // Check if this product is in the original invoice tblHoaDonGoc
        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
        boolean foundInGoc = false;
        for (int i = 0; i < modelGoc.getRowCount(); i++) {
            if (modelGoc.getValueAt(i, 0).equals(dv.getSanPham().getMaSanPham())
                    && modelGoc.getValueAt(i, 2).equals(dv.getTenDonVi().getMoTa())) {
                int slGoc = chiTietHoaDonGocList.get(i).getSoLuong();
                int slTraHienTai = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
                if (slTraHienTai < slGoc) {
                    modelGoc.setValueAt(slTraHienTai + 1, i, 3);
                    xuLyKhiThayDoiDonVi(tblHoaDonGoc, i, 3);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Đã đạt số lượng tối đa có thể đổi cho sản phẩm này trong hóa đơn gốc!",
                            "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                }
                foundInGoc = true;
                break;
            }
        }
        if (!foundInGoc) {
            themSanPhamVaoBang(dv.getSanPham(), dv);
        }
        popupGoiY.setVisible(false);
    }

    private void themSanPhamVaoBang(SanPham sp) {
        themSanPhamVaoBang(sp, null);
    }

    private void themSanPhamVaoBang(SanPham sp, DonViQuyDoi selectedDv) {
        if (sp.getLoaiSanPham().name().equals("ETC")) {
            JOptionPane.showMessageDialog(this, "Thuốc kê đơn không được phép đổi trả!", "Cảnh báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra tồn kho thực tế
        SanPham spDB = sanPhamService.timTheoMa(sp.getMaSanPham());
        if (spDB == null || spDB.getSoLuongTon() <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm này hiện đã hết hàng trong kho!", "Hết hàng",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // KIỂM TRA ĐƠN VỊ QUY ĐỔI TRƯỚC KHI THÊM
        List<DonViQuyDoi> dsDV = donViQuyDoiService.timTheoMaSanPham(sp.getMaSanPham());
        if (dsDV == null || dsDV.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Sản phẩm này chưa được thiết lập Đơn vị quy đổi trong hệ thống! Không thể bán.", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DonViQuyDoi targetDv = selectedDv != null ? selectedDv : dsDV.get(0);
        String donViStr = targetDv.getTenDonVi().getMoTa();

        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(sp.getMaSanPham()) && model.getValueAt(i, 2).equals(donViStr)) {
                int slHienTai = Integer.parseInt(model.getValueAt(i, 3).toString());
                int heSo = targetDv.getHeSoQuyDoi();

                if ((slHienTai + 1) * heSo > spDB.getSoLuongTon()) {
                    JOptionPane.showMessageDialog(this, "Kho không đủ số lượng để đổi thêm!", "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                model.setValueAt(slHienTai + 1, i, 3);
                tinhToanToanBoTien();
                return;
            }
        }

        double donGia = sp.getDonGiaCoBan() * targetDv.getHeSoQuyDoi();
        model.addRow(
                new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), donViStr, 1, donGia, sp.getThue(), 0.0, 0 });
        tinhToanToanBoTien();
    }

    private void tinhToanToanBoTien() {
        double tongTienGocSauThayDoi = tinhTienChoBang(tblHoaDonGoc);
        if (hoaDonGocHienTai != null && hoaDonGocHienTai.getKhuyenMai() != null &&
                hoaDonGocHienTai.getKhuyenMai().getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            tongTienGocSauThayDoi = tongTienGocSauThayDoi * (1 -
                    hoaDonGocHienTai.getKhuyenMai().getKhuyenMaiPhanTram() / 100.0);
        }
        double tongTienHangDoiMoi = tinhTienChoBang(tblSanPham);
        double tongTienHoaDonMoi = tongTienGocSauThayDoi + tongTienHangDoiMoi;

        txtTienDoi.setText(formatVND(tongTienHoaDonMoi));
        double chenhLech = tongTienHoaDonMoi - tongTienHoaDonGocBanDau;
        txtChenhLech.setText(formatVND(chenhLech));

        // Logic làm tròn về đơn vị 1.000 VNĐ
        double soTienLamTron = Math.round(Math.abs(chenhLech) / 1000.0) * 1000;

        if (chenhLech < 0) {
            // TRƯỜNG HỢP HOÀN TIỀN
            txtThanhTienLamTron.setText(formatVND(soTienLamTron));
            txtThanhTienLamTron.setForeground(new Color(220, 53, 69)); // Màu đỏ

            txtKhachDua.setText("0");
            txtKhachDua.setEnabled(false); // Khóa ô nhập tiền khi hoàn tiền
            txtTienThoi.setText(formatVND(soTienLamTron));

            if (pnlThanhTienContainer.getComponentCount() > 0 &&
                    pnlThanhTienContainer.getComponent(0) instanceof JLabel) {
                ((JLabel) pnlThanhTienContainer.getComponent(0)).setText("Số tiền hoàn trả khách:");
            }
        } else {
            // TRƯỜNG HỢP THU THÊM
            txtThanhTienLamTron.setText(formatVND(soTienLamTron));
            txtThanhTienLamTron.setForeground(Color.BLACK);

            txtKhachDua.setEnabled(true);
            if (pnlThanhTienContainer.getComponentCount() > 0 &&
                    pnlThanhTienContainer.getComponent(0) instanceof JLabel) {
                ((JLabel) pnlThanhTienContainer.getComponent(0)).setText("Thành tiền (đã làm tròn):");
            }
            tinhTienThoi();
        }
    }

    private double tinhTienChoBang(JTable table) {
        double total = 0;
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        boolean isGoc = (table == tblHoaDonGoc);
        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                int sl = Integer.parseInt(String.valueOf(model.getValueAt(i, 3)));
                double gia = Double.parseDouble(String.valueOf(model.getValueAt(i, 4)));
                double thueTiLe = Double.parseDouble(String.valueOf(model.getValueAt(i, 5)));

                double tt;
                if (isGoc) {
                    ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(i);
                    int slDaMua = ctGoc.getSoLuong();
                    int slGiuLai = slDaMua - sl;
                    tt = slGiuLai * gia * (1 + thueTiLe / 100.0);
                } else {
                    tt = sl * gia * (1 + thueTiLe / 100.0);
                }
                model.setValueAt(tt, i, 6);
                total += tt;
            } catch (Exception ignored) {
            }
        }
        return total;
    }

    private void tinhTienThoi() {
        try {
            String rawKhachDua = txtKhachDua.getText().trim().replaceAll("[^\\d]", "");
            if (rawKhachDua.isEmpty()) {
                txtTienThoi.setText(formatVND(0));
                return;
            }
            double soKhachDua = Double.parseDouble(rawKhachDua);
            String rawThanhTien = txtThanhTienLamTron.getText().replaceAll("[^\\d]", "");
            double soThanhTien = rawThanhTien.isEmpty() ? 0 : Double.parseDouble(rawThanhTien);
            txtTienThoi.setText(formatVND(Math.max(0, soKhachDua - soThanhTien)));
        } catch (Exception e) {
            txtTienThoi.setText(formatVND(0));
        }
    }

    private ChiTietHoaDon taoChiTietTuDong(DefaultTableModel model, int row, HoaDon hd, int sl) throws Exception {

        String maSP = String.valueOf(model.getValueAt(row, 0));
        String tenDV = String.valueOf(model.getValueAt(row, 2));

        DonViQuyDoi dv = donViQuyDoiService.timTheoTenVaMaSP(DonVi.tuMoTa(tenDV).name(), maSP);
        if (dv == null)
            throw new Exception("Đơn vị '" + tenDV + "' không hợp lệ!");

        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.setHoaDon(hd);
        ct.setDonViQuyDoi(dv);
        ct.setSoLuong(sl);
        ct.setDonGia(dv.getSanPham().getDonGiaCoBan() * dv.getHeSoQuyDoi());
        return ct;
    }

    private void xuLyThanhToan() {
        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();

        // 1. CHẶN NGHIỆP VỤ
        if (modelDoi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ít nhất 1 sản phẩm mới để tiến hành Đổi hàng!\n(Nếu chỉ trả lại hàng, vui lòng sử dụng chức năng Trả hàng).",
                    "Cảnh báo nghiệp vụ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (modelGoc.getRowCount() == 0)
            return;

        try {
            String rawChenhLech = txtChenhLech.getText().replaceAll("[^\\d-]", "");
            double chenhLech = rawChenhLech.isEmpty() ? 0 : Double.parseDouble(rawChenhLech);

            String rawThanhTien = txtThanhTienLamTron.getText().replaceAll("[^\\d]", "");
            double thanhTienPhaiTra = rawThanhTien.isEmpty() ? 0 : Double.parseDouble(rawThanhTien);

            // 2. KIỂM TRA TIỀN KHÁCH ĐƯA
            if (radTienMat.isSelected() && chenhLech > 0) {
                String txtDuaRaw = txtKhachDua.getText().trim().replaceAll("[^\\d]", "");
                double khachDua = txtDuaRaw.isEmpty() ? 0 : Double.parseDouble(txtDuaRaw);
                if (khachDua < thanhTienPhaiTra) {
                    JOptionPane.showMessageDialog(this, "Tiền khách đưa không đủ!");
                    return;
                }
            }

            // 3. KHỞI TẠO THÔNG TIN HÓA ĐƠN MỚI — Ủy quyền sinh mã cho HoaDonService
            LocalDateTime now = LocalDateTime.now();
            String maHoaDonMoi = hoaDonService.sinhMaHoaDon(LoaiHoaDon.DOI_HANG);

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

            CaLam ca = hoaDonService.layCaHienTai(taiKhoanDangNhap.getNhanVien().getMaNhanVien());
            if (ca == null) {
                JOptionPane.showMessageDialog(this, "Chưa mở ca làm việc!");
                return;
            }
            hdMoi.setCa(ca);

            // --- 4. THUẬT TOÁN "DELTA": CHỈ TÁC ĐỘNG PHẦN CHÊNH LỆCH ---
            List<SuPhanBoLo> dsTraLai = new ArrayList<>();
            List<ChiTietHoaDon> dsChiTietMoi = new ArrayList<>();
            List<SuPhanBoLo> dsPhanBoMoi = new ArrayList<>();

            // BƯỚC A: XỬ LÝ HÀNG BẢNG GỐC (Hoàn đúng số lượng trả)
            for (int i = 0; i < modelGoc.getRowCount(); i++) {
                int slTra = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
                ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(i);
                int slDaMua = ctGoc.getSoLuong();
                int slGiuLai = slDaMua - slTra;
                
                Object loiVal = modelGoc.getValueAt(i, 7);
                int slLoi = (loiVal != null && !loiVal.toString().trim().isEmpty()) 
                        ? Integer.parseInt(loiVal.toString()) : 0;
 
                if (slLoi > slTra) {
                    JOptionPane.showMessageDialog(this, "Số lượng lỗi không được vượt quá số lượng trả lại!");
                    return;
                }
 
                // 1. Ghi nhận hàng giữ lại vào Hóa đơn mới (không đụng đến Lô).
                // Nếu slGiuLai = 0 nhưng slTra > 0, ta vẫn thêm ChiTietHoaDon với soLuong = 0 để làm mốc lưu SuPhanBoLo cho hàng trả.
                ChiTietHoaDon ctMoi = null;
                if (slGiuLai > 0) {
                    ctMoi = taoChiTietTuDong(modelGoc, i, hdMoi, slGiuLai);
                    dsChiTietMoi.add(ctMoi);
                } else if (slTra > 0) {
                    ctMoi = taoChiTietTuDong(modelGoc, i, hdMoi, 0);
                    dsChiTietMoi.add(ctMoi);
                }
 
                // 2. Trả lại kho CHÍNH XÁC số lượng khách trả
                if (slTra > 0 && ctMoi != null) {
                    int heSoQuyDoi = ctGoc.getDonViQuyDoi().getHeSoQuyDoi();
                    int slCanHoanNhoNhat = slTra * heSoQuyDoi; 
                    int slLoiNhoNhat = slLoi * heSoQuyDoi; 
 
                    List<SuPhanBoLo> dsPhanBoBanDau = ctGoc.getDsPhanBoLo();
 
                    if (dsPhanBoBanDau != null) {
                        for (SuPhanBoLo pbGoc : dsPhanBoBanDau) {
                            if (slCanHoanNhoNhat <= 0)
                                break;
 
                            int luongGocTrongDBDaQuyDoi = pbGoc.getSoLuong(); // Đã sửa bug nhân hệ số quy đổi ở đây
                            int hoanThucTe = Math.min(slCanHoanNhoNhat, luongGocTrongDBDaQuyDoi);
 
                            // Phân bổ phần lỗi
                            int hoanLoi = Math.min(slLoiNhoNhat, hoanThucTe);
                            if (hoanLoi > 0) {
                                SuPhanBoLo traLaiLoi = new SuPhanBoLo();
                                traLaiLoi.setLo(pbGoc.getLo());
                                traLaiLoi.setSoLuong(hoanLoi);
                                traLaiLoi.setLoi(true);
                                traLaiLoi.setChiTietHoaDon(ctMoi); // Liên kết với ctMoi của hóa đơn mới
                                dsTraLai.add(traLaiLoi);
                                slLoiNhoNhat -= hoanLoi;
                            }
 
                            // Phân bổ phần bình thường
                            int hoanNormal = hoanThucTe - hoanLoi;
                            if (hoanNormal > 0) {
                                SuPhanBoLo traLaiNormal = new SuPhanBoLo();
                                traLaiNormal.setLo(pbGoc.getLo());
                                traLaiNormal.setSoLuong(hoanNormal);
                                traLaiNormal.setLoi(false);
                                traLaiNormal.setChiTietHoaDon(ctMoi); // Liên kết với ctMoi của hóa đơn mới
                                dsTraLai.add(traLaiNormal);
                            }
 
                            slCanHoanNhoNhat -= hoanThucTe;
                        }
                    }
                }
            }

            // BƯỚC B: XỬ LÝ HÀNG MUA MỚI (Trừ kho đúng số mua thêm)
            LoService loService = new LoService();
            for (int i = 0; i < modelDoi.getRowCount(); i++) {
                int slMoi = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                ChiTietHoaDon ctMoi = taoChiTietTuDong(modelDoi, i, hdMoi, slMoi);
                ChiTietHoaDon targetCt = ctMoi;

                // Kiểm tra gộp dòng (Nếu mua thêm sản phẩm trùng với SP giữ lại)
                boolean isMerged = false;
                for (ChiTietHoaDon existing : dsChiTietMoi) {
                    if (existing.getDonViQuyDoi().getMaDonVi().equals(ctMoi.getDonViQuyDoi().getMaDonVi())) {
                        existing.setSoLuong(existing.getSoLuong() + ctMoi.getSoLuong());
                        targetCt = existing;
                        isMerged = true;
                        break;
                    }
                }

                if (!isMerged) {
                    dsChiTietMoi.add(ctMoi);
                }

                // Chạy FEFO lấy Lô ĐÚNG bằng số lượng mới mua thêm
                int soLuongCanTru = ctMoi.getSoLuong() * ctMoi.getDonViQuyDoi().getHeSoQuyDoi();
                List<Lo> dsLo = loService.layDanhSachLoKhaDung(ctMoi.getDonViQuyDoi().getMaDonVi());

                if (dsLo == null || dsLo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Sản phẩm '"
                            + ctMoi.getDonViQuyDoi().getSanPham().getTenSanPham() + "' không có lô khả dụng!");
                    return;
                }

                for (Lo lo : dsLo) {
                    if (soLuongCanTru <= 0)
                        break;
                    int tru = Math.min(soLuongCanTru, lo.getSoLuongSanPham());
                    dsPhanBoMoi.add(new SuPhanBoLo(targetCt, lo, tru));
                    soLuongCanTru -= tru;
                }

                if (soLuongCanTru > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Sản phẩm '" + ctMoi.getDonViQuyDoi().getSanPham().getTenSanPham() + "' không đủ tồn kho!");
                    return;
                }
            }

            // 5. THỰC THI GIAO DỊCH QUA SERVICE
            if (hoaDonService.luuHoaDonDoiHang(hdMoi, dsTraLai, dsChiTietMoi, dsPhanBoMoi)) {
                double tienKhachDua = 0;
                double tienThoi = 0;
                try {
                    String kd = txtKhachDua.getText().replaceAll("[^\\d]", "");
                    tienKhachDua = kd.isEmpty() ? 0 : Double.parseDouble(kd);
                    
                    String tl = txtTienThoi.getText().replaceAll("[^\\d]", "");
                    tienThoi = tl.isEmpty() ? 0 : Double.parseDouble(tl);
                } catch (Exception ex) {
                    // Bỏ qua lỗi parse
                }
                
                // Hiển thị trực quan hóa đơn đổi hàng xem trước và hỏi in ấn
                com.example.utils.InHoaDonPOS.inHoaDon(hdMoi, dsChiTietMoi, tienKhachDua, tienThoi);
                
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi hệ thống: Giao dịch không thể hoàn tất!", "Lỗi SQL",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi nghiệp vụ: " + ex.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        txtMaHoaDonGoc.setText("");
        txtNgayTao.setText("");
        txtTenKhachHang.setText("");
        txtNguoiTao.setText("");
        txtSearchHoaDon.setText("Nhập mã hóa đơn gốc...");
        txtSearchHoaDon.setForeground(Color.GRAY);
        txtTienGoc.setText(formatVND(0));
        txtTienDoi.setText(formatVND(0));
        txtChenhLech.setText(formatVND(0));
        txtThanhTienLamTron.setText(formatVND(0));
        txtKhachDua.setText("");
        txtTienThoi.setText(formatVND(0));
        hoaDonGocHienTai = null;
        chiTietHoaDonGocList.clear();
        tongTienHoaDonGocBanDau = 0;
        ((DefaultTableModel) tblHoaDonGoc.getModel()).setRowCount(0);
        ((DefaultTableModel) tblSanPham.getModel()).setRowCount(0);
    }

    private JTable createTable(boolean isGoc) {
        String[] cols = isGoc 
                ? new String[] { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng Đổi", "Đơn giá", "Thuế", "Thành tiền", "Số lượng lỗi" }
                : new String[] { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền", "Số lượng lỗi" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // Xác định xem bảng này có phải là tblHoaDonGoc không dựa vào đối tượng
                boolean isBangGoc = (this == tblHoaDonGoc.getModel());

                if (chiTietHoaDonGocList != null && isBangGoc && row < chiTietHoaDonGocList.size()) {
                    // Thuốc ETC không được sửa bất cứ gì ở bảng gốc
                    if (chiTietHoaDonGocList.get(row).getDonViQuyDoi().getSanPham().getLoaiSanPham().name()
                            .equals("ETC"))
                        return false;
                    // Vấn đề 3: Bảng gốc chỉ được sửa số lượng đổi (cột 3) và số lượng lỗi (cột 7)
                    return col == 3 || col == 7;
                }
                // Bảng sản phẩm mới thì được sửa đơn vị (col 2) và số lượng (col 3)
                return col == 2 || col == 3;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(35);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r, int c) {
                if (v instanceof Number)
                    v = formatVND(((Number) v).doubleValue());
                return super.getTableCellRendererComponent(t, v, s, h, r, c);
            }
        };
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r, int c) {
                if (v != null)
                    v = String.format("%.1f %%", Double.parseDouble(v.toString()));
                setHorizontalAlignment(JLabel.CENTER);
                return super.getTableCellRendererComponent(t, v, s, h, r, c);
            }
        });
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellEditor(new DynamicUnitCellEditor());
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());
        table.getColumnModel().getColumn(7).setCellEditor(new QuantitySpinnerEditor());
        return table;
    }

    private JPanel createTablePanel(String title, String label, JTextField txt, JTable table, JButton extraBtn) {
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
        if (extraBtn != null)
            s.add(extraBtn);
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
        addInputRow(c, "Tiền HĐ gốc:", txtTienGoc = new JTextField("0 VNĐ"), g, r++);
        addInputRow(c, "Tiền HĐ đổi:", txtTienDoi = new JTextField("0 VNĐ"), g, r++);
        addInputRow(c, "Chênh lệch:", txtChenhLech = new JTextField("0 VNĐ"), g, r++);
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
        txtThanhTienLamTron = new JTextField("0 VNĐ");
        txtThanhTienLamTron.setEditable(false);
        txtThanhTienLamTron.setBackground(new Color(245, 245, 245));
        txtKhachDua = new JTextField();
        txtTienThoi = new JTextField("0 VNĐ");
        txtTienThoi.setEditable(false);
        txtTienThoi.setBackground(new Color(245, 245, 245));
        pnlThanhTienContainer = new JPanel(new BorderLayout(10, 0));
        pnlThanhTienContainer.setOpaque(false);
        JLabel lblThanhTien = new JLabel("Thành tiền (đã làm tròn):");
        lblThanhTien.setPreferredSize(new Dimension(150, 25));
        pnlThanhTienContainer.add(lblThanhTien, BorderLayout.WEST);
        pnlThanhTienContainer.add(txtThanhTienLamTron, BorderLayout.CENTER);
        GridBagConstraints gc = new GridBagConstraints() {
            {
                fill = 1;
                weightx = 1;
                gridy = 0;
                insets = new Insets(0, 0, 15, 0);
            }
        };
        cash.add(pnlThanhTienContainer, gc);
        addInputRow(cash, "Tiền khách đưa:", txtKhachDua, gc, 1);
        addInputRow(cash, "Tiền thối lại:", txtTienThoi, gc, 2);
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

        JLabel l = new JLabel("", SwingConstants.CENTER); // Bỏ chữ mặc định, căn giữa
        l.setPreferredSize(new Dimension(0, 150));
        l.setOpaque(true);
        l.setBackground(Color.WHITE);
        l.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        // Load và hiển thị ảnh QR
        try {
            java.net.URL imgURL = getClass().getResource("/images/QR.jpg");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                // Thu nhỏ ảnh về kích thước 140x140 cho vừa vặn với chiều cao 150 của Panel
                Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                l.setIcon(new ImageIcon(img));
            } else {
                l.setText("Không tìm thấy ảnh QR"); // Báo lỗi trên UI nếu mất file
            }
        } catch (Exception e) {
            l.setText("Lỗi tải ảnh");
        }

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

    private String formatVND(double amount) {
        if (amount == 0)
            return "0 VNĐ";
        return String.format(new java.util.Locale("vi", "VN"), "%,.0f VNĐ", amount).replace(",", ".");
    }

    private class DynamicUnitCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JComboBox<DonVi> cb = new JComboBox<>();

        public DynamicUnitCellEditor() {
            cb.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof DonVi)
                        setText(((DonVi) value).getMoTa());
                    return this;
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) {
            cb.removeAllItems();
            List<DonViQuyDoi> ds = donViQuyDoiService.timTheoMaSanPham(t.getValueAt(r, 0).toString());
            for (DonViQuyDoi dv : ds)
                cb.addItem(dv.getTenDonVi());
            for (int i = 0; i < cb.getItemCount(); i++)
                if (cb.getItemAt(i).getMoTa().equals(v)) {
                    cb.setSelectedIndex(i);
                    break;
                }
            return cb;
        }

        @Override
        public Object getCellEditorValue() {
            Object selected = cb.getSelectedItem();
            if (selected != null && selected instanceof DonVi) {
                return ((DonVi) selected).getMoTa();
            }
            return ""; 
        }
    }

    private class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner s = new JSpinner();

        @Override
        public Component getTableCellEditorComponent(JTable t, Object v, boolean sl, int r, int c) {
            int currentVal = 0;
            try {
                currentVal = Integer.parseInt(v.toString());
            } catch (Exception ex) {}

            int max = 9999;
            int min = 0;
            
            if (t == tblHoaDonGoc) {
                String maSP = t.getValueAt(r, 0).toString();
                // Tìm số lượng gốc đã mua
                for (ChiTietHoaDon ct : chiTietHoaDonGocList) {
                    if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                        max = ct.getSoLuong();
                        break;
                    }
                }
                min = 0;
            } else {
                // tblSanPham (mua mới) thì min = 1
                min = 1;
            }

            s.setModel(new SpinnerNumberModel(currentVal, min, max, 1));
            return s;
        }

        @Override
        public Object getCellEditorValue() {
            return s.getValue();
        }
    }
}