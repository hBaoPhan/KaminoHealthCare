package com.example.gui.screens;

import com.example.service.ChiTietHoaDonService;
import com.example.service.DonViQuyDoiService;
import com.example.service.LoService;
import com.example.service.HoaDonService;
import com.example.service.SanPhamService;
import com.example.service.KhuyenMaiService;
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
import java.text.DecimalFormat;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DoiHangPanel extends JPanel {

    private JTextField txtMaHoaDonGoc, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienDoi, txtKhuyenMaiMoi, txtChenhLech, txtThanhTienLamTron, txtKhachDua,
            txtTienThoi;
    private JTextField txtSearchHoaDon, txtSearchSanPham;
    private JComboBox<String> cboKhuyenMai;
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
    private KhuyenMaiService khuyenMaiService = new KhuyenMaiService();

    private HoaDon hoaDonGocHienTai = null;
    private List<ChiTietHoaDon> chiTietHoaDonGocList = new ArrayList<>();
    private double tongTienHoaDonGocBanDau = 0;
    private TaiKhoan taiKhoanDangNhap;

    private List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
    private boolean isAutoSelectingPromotion = false;
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
                String maSP = tblSanPham.getValueAt(selectedRow, 0).toString();
                SanPham sp = sanPhamService.timTheoMa(maSP);

                if (sp != null && sp.getLoaiSanPham().name().equals("ETC")) {
                    JOptionPane.showMessageDialog(this,
                            "Đây là thuốc kê đơn (ETC) đang được đổi ngang 1-1.\nĐể hủy đổi, vui lòng chỉnh số lượng đổi ở bảng chi tiết hóa đơn gốc về 0!",
                            "Cảnh báo nghiệp vụ", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                ((DefaultTableModel) tblSanPham.getModel()).removeRow(selectedRow);
                autoSelectBestKhuyenMai();
                tinhToanToanBoTien();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn một dòng để xóa!", "Chưa chọn dòng",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        tblHoaDonGoc = createTable(true);
        tblSanPham = createTable(false);

        JPanel pnlLeft = new JPanel(new GridLayout(2, 1, 0, 20));
        pnlLeft.setOpaque(false);
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn gốc", "Tìm hóa đơn:", txtSearchHoaDon, tblHoaDonGoc, null));
        pnlLeft.add(createTablePanel("Chi tiết hóa đơn đổi", "Thêm hàng mới:", txtSearchSanPham, tblSanPham,
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
                boolean isTableEditing = (tblHoaDonGoc != null && tblHoaDonGoc.isEditing()
                        && SwingUtilities.isDescendingFrom(focused, tblHoaDonGoc))
                        || (tblSanPham != null && tblSanPham.isEditing()
                                && SwingUtilities.isDescendingFrom(focused, tblSanPham));

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
                        if (txtSearchHoaDon != null && txtSearchHoaDon.isShowing()) {
                            txtSearchHoaDon.requestFocusInWindow();
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

            // Ngăn ngừa lỗi đúp sự kiện khi người dùng đang active focus trong các ô nhập
            // văn bản (txtSearchSanPham, txtSearchHoaDon, v.v.)
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            boolean isEditableFocused = (focusOwner instanceof javax.swing.text.JTextComponent)
                    && ((javax.swing.text.JTextComponent) focusOwner).isEditable();
            if (isEditableFocused) {
                return false;
            }

            if (e.getID() == KeyEvent.KEY_TYPED) {
                long now = System.currentTimeMillis();
                char c = e.getKeyChar();

                // Nếu khoảng cách giữa 2 ký tự lớn hơn 50ms, coi như nhập liệu thủ công bằng
                // bàn phím
                if (now - lastKeyTime > 50) {
                    barcodeBuffer.setLength(0);
                }
                lastKeyTime = now;

                if (c == '\n') {
                    String barcode = barcodeBuffer.toString().trim();
                    if (!barcode.isEmpty() && barcode.length() >= 5) {
                        if (barcode.toUpperCase().startsWith("HD")) {
                            SwingUtilities.invokeLater(() -> {
                                timKiemHoaDonGoc(barcode);
                                if (txtSearchHoaDon != null) {
                                    txtSearchHoaDon.setText(barcode);
                                    txtSearchHoaDon.setForeground(Color.BLACK);
                                }
                            });
                            barcodeBuffer.setLength(0);
                            return true;
                        } else {
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
                    }
                    barcodeBuffer.setLength(0);
                } else if (Character.isLetterOrDigit(c) || c == '-') {
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
                    if (!kw.isEmpty() && !kw.equals("Nhập mã hóa đơn gốc...")) {
                        if (kw.toUpperCase().startsWith("HD")) {
                            timKiemHoaDonGoc(kw);
                        } else {
                            DonViQuyDoi dv = donViQuyDoiService.timTheoBarcode(kw);
                            if (dv != null) {
                                xuLyQuetBarcode(dv);
                                txtSearchHoaDon.setText("");
                            } else {
                                JOptionPane.showMessageDialog(DoiHangPanel.this,
                                        "Không tìm thấy sản phẩm có mã vạch: " + kw,
                                        "Lỗi mã vạch", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        txtSearchSanPham.addActionListener(e -> {
            String text = txtSearchSanPham.getText().trim();
            if (!text.isEmpty() && !text.equals("Nhập mã/tên sản phẩm...")) {
                if (text.toUpperCase().startsWith("HD")) {
                    timKiemHoaDonGoc(text);
                    txtSearchSanPham.setText("");
                    if (txtSearchHoaDon != null) {
                        txtSearchHoaDon.setText(text);
                        txtSearchHoaDon.setForeground(Color.BLACK);
                    }
                } else {
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
            if (e.getType() == TableModelEvent.UPDATE
                    && (e.getColumn() == 3 || e.getColumn() == 2 || e.getColumn() == 7)) {
                xuLyKhiThayDoiDonVi(tblHoaDonGoc, e.getFirstRow(), e.getColumn());
            }
        });

        tblSanPham.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.UPDATE) {
                if (e.getColumn() == 2)
                    xuLyKhiThayDoiDonVi(tblSanPham, e.getFirstRow(), e.getColumn());
                if (e.getColumn() != 6) {
                    autoSelectBestKhuyenMai();
                    tinhToanToanBoTien();
                }
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

    private void taiLaiDanhSachKhuyenMai() {
        boolean isThanhVien = hoaDonGocHienTai != null && hoaDonGocHienTai.getKhachHang() != null
                && hoaDonGocHienTai.getKhachHang().getTrangThai() == TrangThaiKhachHang.KHACH_HANG_THANH_VIEN;
        taiLaiDanhSachKhuyenMai(isThanhVien);
    }

    private void taiLaiDanhSachKhuyenMai(boolean isThanhVien) {
        if (cboKhuyenMai == null)
            return;

        // Khóa event của ComboBox tạm thời để không bị trigger lỗi tính toán trong lúc
        // xóa item
        isAutoSelectingPromotion = true;

        cboKhuyenMai.removeAllItems();
        cboKhuyenMai.addItem("-- Không áp dụng --");

        // Lấy lại danh sách MỚI NHẤT từ database theo trạng thái thành viên
        dsKhuyenMai = khuyenMaiService.layKhuyenMaiConHan(isThanhVien);
        for (KhuyenMai km : dsKhuyenMai) {
            cboKhuyenMai.addItem(km.getTenKhuyenMai());
        }

        // Mở khóa event trở lại
        isAutoSelectingPromotion = false;
    }

    private void dongBoETC1_1(ChiTietHoaDon ctGoc, int soLuongDoi) {
        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
        String maSP = ctGoc.getDonViQuyDoi().getSanPham().getMaSanPham();

        for (int i = 0; i < modelDoi.getRowCount(); i++) {
            if (modelDoi.getValueAt(i, 0).equals(maSP)) {
                if (soLuongDoi == 0) {
                    modelDoi.removeRow(i);
                } else {
                    modelDoi.setValueAt(soLuongDoi, i, 3);
                }
                return;
            }
        }

        if (soLuongDoi > 0) {
            SanPham sp = ctGoc.getDonViQuyDoi().getSanPham();
            modelDoi.addRow(new Object[] {
                    sp.getMaSanPham(),
                    sp.getTenSanPham(),
                    ctGoc.getDonViQuyDoi().getTenDonVi().getMoTa(),
                    soLuongDoi,
                    ctGoc.getDonGia(),
                    sp.getThue(),
                    0.0,
                    0,
                    false // IS_GIFT
            });
        }
    }

    private void xuLyKhiThayDoiDonVi(JTable table, int row, int col) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        String maSP = model.getValueAt(row, 0).toString();

        if (table == tblHoaDonGoc && (col == 3 || col == 7)) {
            int slTraNhap = Integer.parseInt(model.getValueAt(row, 3).toString());
            int slLoiNhap = Integer.parseInt(model.getValueAt(row, 7).toString());

            ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(row);
            int slDaMua = ctGoc.getSoLuongBan();

            if (slTraNhap < 0 || slTraNhap > slDaMua) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng đổi phải từ 0 đến " + slDaMua + " (số lượng đã mua)!",
                        "Cảnh báo dữ liệu", JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(() -> model.setValueAt(slDaMua, row, 3));
                return;
            }

            if (slLoiNhap < 0 || slLoiNhap > slTraNhap) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng lỗi phải từ 0 đến " + slTraNhap + " (số lượng đổi)!",
                        "Cảnh báo dữ liệu", JOptionPane.ERROR_MESSAGE);
                SwingUtilities.invokeLater(() -> model.setValueAt(0, row, 7));
                return;
            }

            if (ctGoc.getDonViQuyDoi().getSanPham().getLoaiSanPham().name().equals("ETC")) {
                dongBoETC1_1(ctGoc, slTraNhap);
            }
        }

        String moTaDonVi = model.getValueAt(row, 2).toString();
        DonVi dvEnum = DonVi.tuMoTa(moTaDonVi);
        DonViQuyDoi dv = donViQuyDoiService.timTheoTenVaMaSP(dvEnum.name(), maSP);

        if (dv != null) {
            // Lấy giá trị gốc bên ngoài (biến này sẽ trở thành effectively final)
            double giaCoBanBanDau = dv.getSanPham().getDonGiaCoBan() * dv.getHeSoQuyDoi();

            SwingUtilities.invokeLater(() -> {
                // Tạo một biến nội bộ bên trong Lambda để thoải mái thay đổi giá trị
                double giaMoiThucTe = giaCoBanBanDau;

                // Bỏ qua giá nếu là hàng tặng
                boolean isGift = false;
                if (table == tblSanPham) {
                    Boolean giftFlag = (Boolean) model.getValueAt(row, 8);
                    isGift = (giftFlag != null && giftFlag);
                }

                if (isGift) {
                    giaMoiThucTe = 0.0; // Gán lại giá trị thoải mái vì biến này nằm trong khối Lambda
                }

                model.setValueAt(giaMoiThucTe, row, 4);

                Object slValue = model.getValueAt(row, 3);
                int sl = (slValue != null && !slValue.toString().trim().isEmpty())
                        ? Integer.parseInt(slValue.toString())
                        : 0;
                double thueTiLe = Double.parseDouble(model.getValueAt(row, 5).toString().replace("%", ""));

                if (table == tblHoaDonGoc) {
                    ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(row);
                    int slDaMua = ctGoc.getSoLuongBan();
                    int slGiuLai = slDaMua - sl;
                    model.setValueAt(slGiuLai * giaMoiThucTe, row, 6);
                } else {
                    model.setValueAt(sl * giaMoiThucTe, row, 6);
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
        taiLaiDanhSachKhuyenMai();
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
        ((DefaultTableModel) tblSanPham.getModel()).setRowCount(0);
        tongTienHoaDonGocBanDau = 0;

        for (ChiTietHoaDon ct : chiTietHoaDonGocList) {
            double thueTiLe = ct.getDonViQuyDoi().getSanPham().getThue();
            double tt = ct.getSoLuongBan() * ct.getDonGia() * (1 + thueTiLe / 100.0);
            model.addRow(new Object[] {
                    ct.getDonViQuyDoi().getSanPham().getMaSanPham(),
                    ct.getDonViQuyDoi().getSanPham().getTenSanPham(),
                    ct.getDonViQuyDoi().getTenDonVi().getMoTa(),
                    0,
                    ct.getDonGia(),
                    thueTiLe,
                    0.0,
                    0
            });
            tongTienHoaDonGocBanDau += tt;

            // Sync ETC drugs if quantity is loaded > 0
            if (ct.getDonViQuyDoi().getSanPham().getLoaiSanPham().name().equals("ETC")) {
                dongBoETC1_1(ct, 0);
            }
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
        if (hoaDonGocHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm kiếm hóa đơn gốc trước khi thêm sản phẩm!", "Chưa chọn hóa đơn gốc",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        themSanPhamVaoBang(dv.getSanPham(), dv);
        popupGoiY.setVisible(false);
    }

    private void themSanPhamVaoBang(SanPham sp) {
        themSanPhamVaoBang(sp, null);
    }

    private void themSanPhamVaoBang(SanPham sp, DonViQuyDoi selectedDv) {
        if (hoaDonGocHienTai == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm kiếm hóa đơn gốc trước khi thêm sản phẩm!", "Chưa chọn hóa đơn gốc",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (sp.getLoaiSanPham().name().equals("ETC")) {
            JOptionPane.showMessageDialog(this,
                    "Thuốc kê đơn (ETC) không được phép tự ý thêm mới ngoài danh mục đơn thuốc!", "Cảnh báo",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SanPham spDB = sanPhamService.timTheoMa(sp.getMaSanPham());
        if (spDB == null || spDB.getSoLuongTon() <= 0) {
            JOptionPane.showMessageDialog(this, "Sản phẩm này hiện đã hết hàng trong kho!", "Hết hàng",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<DonViQuyDoi> dsDV = donViQuyDoiService.timTheoMaSanPham(sp.getMaSanPham());
        if (dsDV == null || dsDV.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Sản phẩm này chưa được thiết lập Đơn vị quy đổi trong hệ thống!", "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        DonViQuyDoi targetDv = selectedDv != null ? selectedDv : dsDV.get(0);
        String donViStr = targetDv.getTenDonVi().getMoTa();

        DefaultTableModel model = (DefaultTableModel) tblSanPham.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(sp.getMaSanPham()) && model.getValueAt(i, 2).equals(donViStr)) {
                int slHienTai = Integer.parseInt(model.getValueAt(i, 3).toString());
                int heSo = dsDV.get(0).getHeSoQuyDoi();

                if ((slHienTai + 1) * heSo > spDB.getSoLuongTon()) {
                    JOptionPane.showMessageDialog(this, "Kho không đủ số lượng tồn khả dụng!", "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                model.setValueAt(slHienTai + 1, i, 3);
                tinhToanToanBoTien();
                return;
            }
        }

        String dv = dsDV.get(0).getTenDonVi().getMoTa();
        model.addRow(new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), dv, 1, sp.getDonGiaCoBan(), sp.getThue(),
                0.0, 0, false });
    }

    private void autoSelectBestKhuyenMai() {
        if (isAutoSelectingPromotion || cboKhuyenMai == null || dsKhuyenMai == null || dsKhuyenMai.isEmpty())
            return;

        double tongTienMuaMoiBase = 0;
        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();

        // ĐÃ XÓA: Đoạn code chặn hasETC gây lỗi ở đây

        for (int i = 0; i < modelDoi.getRowCount(); i++) {
            Boolean isGift = (Boolean) modelDoi.getValueAt(i, 8);
            if (isGift == null || !isGift) {
                String maSP = modelDoi.getValueAt(i, 0).toString();

                SanPham sp = sanPhamService.timTheoMa(maSP);
                if (sp != null && sp.getLoaiSanPham().name().equals("ETC")) {
                    continue;
                }

                String donVi = modelDoi.getValueAt(i, 2).toString();
                int qty = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                double price = Double.parseDouble(modelDoi.getValueAt(i, 4).toString()); // Đây là giá TRƯỚC thuế

                int slTra = 0;
                for (int j = 0; j < modelGoc.getRowCount(); j++) {
                    if (modelGoc.getValueAt(j, 0).equals(maSP) && modelGoc.getValueAt(j, 2).equals(donVi)) {
                        slTra += Integer.parseInt(modelGoc.getValueAt(j, 3).toString());
                    }
                }

                int slThucMuaMoi = Math.max(0, qty - slTra);
                
                // SỬA LẠI: Chỉ nhân với price (Giá trước thuế)
                tongTienMuaMoiBase += slThucMuaMoi * price; 
            }
        }

        if (tongTienMuaMoiBase <= 0) {
            if (cboKhuyenMai.getSelectedIndex() != 0) {
                isAutoSelectingPromotion = true;
                try {
                    cboKhuyenMai.setSelectedIndex(0);
                } finally {
                    isAutoSelectingPromotion = false;
                }
                capNhatQuaTang();
            }
            return;
        }

        boolean isThanhVien = hoaDonGocHienTai != null && hoaDonGocHienTai.getKhachHang() != null
                && hoaDonGocHienTai.getKhachHang().getTrangThai() == TrangThaiKhachHang.KHACH_HANG_THANH_VIEN;
        int bestIndex = khuyenMaiService.chonKhuyenMaiTotNhat(dsKhuyenMai, tongTienMuaMoiBase, isThanhVien);
        int cboIndex = bestIndex + 1;

        if (cboKhuyenMai.getSelectedIndex() != cboIndex) {
            isAutoSelectingPromotion = true;
            try {
                cboKhuyenMai.setSelectedIndex(cboIndex);
            } finally {
                isAutoSelectingPromotion = false;
            }
            capNhatQuaTang();
        }
    }

    private void capNhatQuaTang() {
        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();

        // Xóa quà tặng cũ
        for (int i = modelDoi.getRowCount() - 1; i >= 0; i--) {
            Boolean isGift = (Boolean) modelDoi.getValueAt(i, 8);
            if (isGift != null && isGift) {
                modelDoi.removeRow(i);
            }
        }

        double tongTienMuaMoiBase = 0;
        for (int i = 0; i < modelDoi.getRowCount(); i++) {
            Boolean isGift = (Boolean) modelDoi.getValueAt(i, 8);
            if (isGift == null || !isGift) {
                String maSP = modelDoi.getValueAt(i, 0).toString();

                SanPham sp = sanPhamService.timTheoMa(maSP);
                if (sp != null && sp.getLoaiSanPham().name().equals("ETC")) {
                    continue;
                }

                String donVi = modelDoi.getValueAt(i, 2).toString();
                int qty = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                double price = Double.parseDouble(modelDoi.getValueAt(i, 4).toString()); // Đây là giá TRƯỚC thuế

                int slTra = 0;
                for (int j = 0; j < modelGoc.getRowCount(); j++) {
                    if (modelGoc.getValueAt(j, 0).equals(maSP) && modelGoc.getValueAt(j, 2).equals(donVi)) {
                        slTra += Integer.parseInt(modelGoc.getValueAt(j, 3).toString());
                    }
                }

                int slThucMuaMoi = Math.max(0, qty - slTra);
                
                // SỬA LẠI: Chỉ nhân với price
                tongTienMuaMoiBase += slThucMuaMoi * price;
            }
        }

        int idx = cboKhuyenMai.getSelectedIndex() - 1;
        if (idx >= 0 && idx < dsKhuyenMai.size()) {
            KhuyenMai km = dsKhuyenMai.get(idx);
            
            // SỬA Ở ĐÂY: Thêm điều kiện tongTienMuaMoiBase <= 0
            if (tongTienMuaMoiBase <= 0 || tongTienMuaMoiBase < km.getGiaTriDonHangToiThieu()) {
                if (!isAutoSelectingPromotion) {
                    JOptionPane.showMessageDialog(this, "Không có sản phẩm mua mới hợp lệ hoặc chưa đạt giá trị tối thiểu (" +
                            new DecimalFormat("#,### đ").format(km.getGiaTriDonHangToiThieu())
                            + ") để áp dụng khuyến mãi!");
                    cboKhuyenMai.setSelectedIndex(0);
                }
                tinhToanToanBoTien();
                return;
            }
        }

        if (isAutoSelectingPromotion) {
            tinhToanToanBoTien();
            return;
        }

        idx = cboKhuyenMai.getSelectedIndex() - 1;
        if (idx >= 0 && idx < dsKhuyenMai.size()) {
            KhuyenMai km = dsKhuyenMai.get(idx);
            if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                QuaTang qt = km.getQuaTangKem();
                DonViQuyDoi dvCoBan = donViQuyDoiService.timTheoMa(qt.getDonViQuyDoi().getMaDonVi());
                if (dvCoBan != null) {
                    SanPham sp = dvCoBan.getSanPham();
                    modelDoi.addRow(new Object[] {
                            sp.getMaSanPham(),
                            sp.getTenSanPham() + " (Quà tặng)",
                            dvCoBan.getTenDonVi().getMoTa(),
                            qt.getSoLuongTang(),
                            0.0,
                            0.0,
                            0.0,
                            0,
                            true
                    });
                }
            }
        }
        tinhToanToanBoTien();
    }

    private void tinhToanToanBoTien() {
        // Cập nhật lại UI bảng (gọi hàm để Render lại giao diện)
        tinhTienChoBang(tblHoaDonGoc);
        tinhTienChoBang(tblSanPham);

        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();

        // 1. Lấy tỉ lệ giảm của hóa đơn gốc
        double tiLeGiamGoc = 0;
        if (hoaDonGocHienTai != null && hoaDonGocHienTai.getKhuyenMai() != null &&
                hoaDonGocHienTai.getKhuyenMai().getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            tiLeGiamGoc = hoaDonGocHienTai.getKhuyenMai().getKhuyenMaiPhanTram();
        }

        // 2. Tính tiền hàng gốc giữ lại (Tính toán độc lập để tránh nhiễu từ UI)
        double tongTienGocSauThayDoi = 0;
        for (int i = 0; i < modelGoc.getRowCount(); i++) {
            int slTra = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
            double price = Double.parseDouble(modelGoc.getValueAt(i, 4).toString());
            double thueTiLe = Double.parseDouble(modelGoc.getValueAt(i, 5).toString().replace("%", ""));
            
            ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(i);
            int slDaMua = ctGoc.getSoLuongBan();
            int slGiuLai = slDaMua - slTra;

            tongTienGocSauThayDoi += slGiuLai * price * (1 - tiLeGiamGoc / 100.0) * (1 + thueTiLe / 100.0);
        }

        // 3. Tính tiền giảm giá mới (Áp dụng trên giá TRƯỚC THUẾ)
        double soTienGiamMoiHienThi = 0; 
        double tiLeGiamMoi = 0;
        int idx = (cboKhuyenMai != null) ? cboKhuyenMai.getSelectedIndex() - 1 : -1;
        
        if (idx >= 0 && idx < dsKhuyenMai.size()) {
            KhuyenMai km = dsKhuyenMai.get(idx);
            double tongTienMuaMoiBasePreTax = 0;

            for (int i = 0; i < modelDoi.getRowCount(); i++) {
                Boolean isGift = (Boolean) modelDoi.getValueAt(i, 8);
                if (isGift == null || !isGift) {
                    String maSP = modelDoi.getValueAt(i, 0).toString();
                    SanPham sp = sanPhamService.timTheoMa(maSP);
                    if (sp != null && sp.getLoaiSanPham().name().equals("ETC")) continue;

                    String donVi = modelDoi.getValueAt(i, 2).toString();
                    int qty = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                    double price = Double.parseDouble(modelDoi.getValueAt(i, 4).toString()); // GIÁ TRƯỚC THUẾ

                    int slTra = 0;
                    for (int j = 0; j < modelGoc.getRowCount(); j++) {
                        if (modelGoc.getValueAt(j, 0).equals(maSP) && modelGoc.getValueAt(j, 2).equals(donVi)) {
                            slTra += Integer.parseInt(modelGoc.getValueAt(j, 3).toString());
                        }
                    }

                    int slThucMuaMoi = Math.max(0, qty - slTra);
                    tongTienMuaMoiBasePreTax += slThucMuaMoi * price;
                }
            }
            
            if (tongTienMuaMoiBasePreTax >= km.getGiaTriDonHangToiThieu() && km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                tiLeGiamMoi = km.getKhuyenMaiPhanTram();
                soTienGiamMoiHienThi = tongTienMuaMoiBasePreTax * (tiLeGiamMoi / 100.0);
            }
        }

        // 4. Tính tổng tiền hóa đơn đổi (Áp dụng: Giảm trên giá trước thuế -> Tính thuế sau)
        double tongTienHangDoiMoiFinal = 0; 
        for (int i = 0; i < modelDoi.getRowCount(); i++) {
            String maSP = modelDoi.getValueAt(i, 0).toString();
            String donVi = modelDoi.getValueAt(i, 2).toString();
            int qty = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
            double price = Double.parseDouble(modelDoi.getValueAt(i, 4).toString());
            double thueTiLe = Double.parseDouble(modelDoi.getValueAt(i, 5).toString().replace("%", ""));
            Boolean isGift = (Boolean) modelDoi.getValueAt(i, 8);

            int slTra = 0;
            for (int j = 0; j < modelGoc.getRowCount(); j++) {
                if (modelGoc.getValueAt(j, 0).equals(maSP) && modelGoc.getValueAt(j, 2).equals(donVi)) {
                    slTra += Integer.parseInt(modelGoc.getValueAt(j, 3).toString());
                }
            }

            int slDoiNgang = Math.min(qty, slTra);
            int slMuaThem = Math.max(0, qty - slTra);

            // a. Phần đổi ngang: Giữ nguyên % KM cũ
            double tienDoiNgangThucTe = slDoiNgang * price * (1 - tiLeGiamGoc / 100.0) * (1 + thueTiLe / 100.0);

            // b. Phần mua thêm:
            double tienMuaThemThucTe = 0;
            if (isGift != null && isGift) {
                tienMuaThemThucTe = 0;
            } else {
                SanPham sp = sanPhamService.timTheoMa(maSP);
                if (sp != null && sp.getLoaiSanPham().name().equals("ETC")) {
                    tienMuaThemThucTe = slMuaThem * price * (1 + thueTiLe / 100.0);
                } else {
                    // CÔNG THỨC CHUẨN: Trừ % KM trước, cộng thuế sau
                    double preTaxMuaThem = slMuaThem * price;
                    double preTaxSauGiam = preTaxMuaThem * (1 - tiLeGiamMoi / 100.0);
                    tienMuaThemThucTe = preTaxSauGiam * (1 + thueTiLe / 100.0);
                }
            }

            tongTienHangDoiMoiFinal += (tienDoiNgangThucTe + tienMuaThemThucTe);
        }

        // 5. Cập nhật giao diện tiền KM
        if (txtKhuyenMaiMoi != null) {
            txtKhuyenMaiMoi.setText("-" + formatVND(soTienGiamMoiHienThi));
            if (soTienGiamMoiHienThi > 0)
                txtKhuyenMaiMoi.setForeground(new Color(40, 167, 69));
            else
                txtKhuyenMaiMoi.setForeground(Color.BLACK);
        }

        // 6. Tính toán chênh lệch cuối cùng
        // LƯU Ý: tongTienHangDoiMoiFinal ĐÃ TỰ ĐỘNG TRỪ TIỀN KM ở trên, nên ta KHÔNG trừ soTienGiamMoiHienThi ở đây nữa.
        double tongTienHoaDonMoi = tongTienGocSauThayDoi + tongTienHangDoiMoiFinal;
        txtTienDoi.setText(formatVND(tongTienHoaDonMoi));
        
        double chenhLech = tongTienHoaDonMoi - tongTienHoaDonGocBanDau;
        txtChenhLech.setText(formatVND(chenhLech));

        // 7. Làm tròn và hiển thị thanh toán
        double soTienLamTron = Math.round(Math.abs(chenhLech) / 1000.0) * 1000;

        if (chenhLech < 0) {
            txtThanhTienLamTron.setText(formatVND(soTienLamTron));
            txtThanhTienLamTron.setForeground(new Color(220, 53, 69));
            txtKhachDua.setText("0");
            txtKhachDua.setEnabled(false);
            txtTienThoi.setText(formatVND(soTienLamTron));

            if (pnlThanhTienContainer.getComponentCount() > 0 &&
                    pnlThanhTienContainer.getComponent(0) instanceof JLabel) {
                ((JLabel) pnlThanhTienContainer.getComponent(0)).setText("Số tiền hoàn trả khách:");
            }
        } else {
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
        double totalCoThue = 0; // Vẫn tính tổng có thuế để hệ thống chốt số liệu
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        boolean isGoc = (table == tblHoaDonGoc);
        
        // 1. Lấy tỉ lệ giảm của hóa đơn gốc (nếu có)
        double tiLeGiamGoc = 0;
        if (hoaDonGocHienTai != null && hoaDonGocHienTai.getKhuyenMai() != null &&
                hoaDonGocHienTai.getKhuyenMai().getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
            tiLeGiamGoc = hoaDonGocHienTai.getKhuyenMai().getKhuyenMaiPhanTram();
        }

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                String maSP = String.valueOf(model.getValueAt(i, 0));
                String donVi = String.valueOf(model.getValueAt(i, 2));
                int sl = Integer.parseInt(String.valueOf(model.getValueAt(i, 3)));
                double gia = Double.parseDouble(String.valueOf(model.getValueAt(i, 4)));
                double thueTiLe = Double.parseDouble(String.valueOf(model.getValueAt(i, 5)).replace("%", ""));

                double ttHienThi = 0; // Để đẩy lên giao diện (Không Thuế)
                double ttThucTe = 0;  // Để cộng vào tổng đơn hàng (Có Thuế)
                
                if (isGoc) {
                    // BẢNG TRÊN: Tính tiền phần hàng khách GIỮ LẠI
                    ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(i);
                    int slDaMua = ctGoc.getSoLuongBan();
                    int slGiuLai = slDaMua - sl; 
                    
                    // Cột hiển thị không nhân thuế
                    ttHienThi = slGiuLai * gia * (1 - tiLeGiamGoc / 100.0);
                    // Tổng tiền ngầm vẫn tính thuế
                    ttThucTe = slGiuLai * gia * (1 + thueTiLe / 100.0) * (1 - tiLeGiamGoc / 100.0);
                } else {
                    // BẢNG DƯỚI: Tính tiền hàng đổi
                    DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
                    int slTra = 0;
                    for (int j = 0; j < modelGoc.getRowCount(); j++) {
                        if (modelGoc.getValueAt(j, 0).equals(maSP) && modelGoc.getValueAt(j, 2).equals(donVi)) {
                            slTra += Integer.parseInt(modelGoc.getValueAt(j, 3).toString());
                        }
                    }
                    
                    int slDoiNgang = Math.min(sl, slTra);
                    int slMuaThem = Math.max(0, sl - slTra);
                    
                    // Cột hiển thị không nhân thuế
                    double tienDoiNgangHienThi = slDoiNgang * gia * (1 - tiLeGiamGoc / 100.0);
                    double tienMuaThemHienThi = slMuaThem * gia;
                    ttHienThi = tienDoiNgangHienThi + tienMuaThemHienThi;

                    // Tổng tiền ngầm vẫn tính thuế
                    double tienDoiNgangThucTe = slDoiNgang * gia * (1 + thueTiLe / 100.0) * (1 - tiLeGiamGoc / 100.0);
                    double tienMuaThemThucTe = slMuaThem * gia * (1 + thueTiLe / 100.0);
                    ttThucTe = tienDoiNgangThucTe + tienMuaThemThucTe;
                }
                
                model.setValueAt(ttHienThi, i, 6); // Cập nhật lên giao diện (Cột Thành tiền)
                totalCoThue += ttThucTe; // Cộng dồn trả về cho hàm tinhToanToanBoTien()
            } catch (Exception ignored) {
            }
        }
        return totalCoThue;
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
        ct.setSoLuongBan(sl);
        ct.setDonGia(dv.getSanPham().getDonGiaCoBan() * dv.getHeSoQuyDoi());
        return ct;
    }

    private void xuLyThanhToan() {
        if (tblHoaDonGoc.isEditing()) {
            tblHoaDonGoc.getCellEditor().stopCellEditing();
        }
        if (tblSanPham.isEditing()) {
            tblSanPham.getCellEditor().stopCellEditing();
        }

        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();

        if (modelDoi.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ít nhất 1 sản phẩm mới để tiến hành Đổi hàng!\n(Nếu chỉ trả lại hàng, vui lòng sử dụng chức năng Trả hàng).",
                    "Cảnh báo nghiệp vụ", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (modelGoc.getRowCount() == 0)
            return;

        try {
            // Kiểm tra ít nhất 1 sản phẩm gốc được chọn để đổi trả (số lượng đổi > 0)
            int tongSlTra = 0;
            for (int i = 0; i < modelGoc.getRowCount(); i++) {
                tongSlTra += Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
            }
            if (tongSlTra == 0) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn ít nhất 1 sản phẩm gốc để đổi trả (số lượng đổi > 0)!",
                        "Cảnh báo nghiệp vụ", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String rawChenhLech = txtChenhLech.getText().replaceAll("[^\\d-]", "");
            double chenhLech = rawChenhLech.isEmpty() ? 0 : Double.parseDouble(rawChenhLech);

            String rawThanhTien = txtThanhTienLamTron.getText().replaceAll("[^\\d]", "");
            double thanhTienPhaiTra = rawThanhTien.isEmpty() ? 0 : Double.parseDouble(rawThanhTien);

            if (radTienMat.isSelected() && chenhLech > 0) {
                String txtDuaRaw = txtKhachDua.getText().trim().replaceAll("[^\\d]", "");
                double khachDua = txtDuaRaw.isEmpty() ? 0 : Double.parseDouble(txtDuaRaw);
                if (khachDua < thanhTienPhaiTra) {
                    JOptionPane.showMessageDialog(this, "Tiền khách đưa không đủ!", "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

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

            // Ghi nhận Mã Khuyến Mãi cho đơn hàng mới nếu có
            int idxKM = cboKhuyenMai.getSelectedIndex() - 1;
            if (idxKM >= 0 && idxKM < dsKhuyenMai.size()) {
                hdMoi.setKhuyenMai(dsKhuyenMai.get(idxKM));
            }

            CaLam ca = hoaDonService.layCaHienTai(taiKhoanDangNhap.getNhanVien().getMaNhanVien());
            if (ca == null) {
                JOptionPane.showMessageDialog(this, "Chưa mở ca làm việc!", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
                return;
            }
            hdMoi.setCa(ca);

            List<SuPhanBoLo> dsTraLai = new ArrayList<>();
            List<ChiTietHoaDon> dsChiTietMoi = new ArrayList<>();
            List<SuPhanBoLo> dsPhanBoMoi = new ArrayList<>();

            // BƯỚC A: XỬ LÝ HÀNG BẢNG GỐC (Nhập kho hàng cũ)
            for (int i = 0; i < modelGoc.getRowCount(); i++) {
                int slTra = Integer.parseInt(modelGoc.getValueAt(i, 3).toString());
                ChiTietHoaDon ctGoc = chiTietHoaDonGocList.get(i);
                int slDaMua = ctGoc.getSoLuongBan();
                int slGiuLai = slDaMua - slTra;

                Object loiVal = modelGoc.getValueAt(i, 7);
                int slLoi = (loiVal != null && !loiVal.toString().trim().isEmpty())
                        ? Integer.parseInt(loiVal.toString())
                        : 0;

                if (slLoi > slTra) {
                    JOptionPane.showMessageDialog(this, "Số lượng lỗi không được vượt quá số lượng trả lại!");
                    return;
                }

                ChiTietHoaDon ctMoi = null;
                if (slGiuLai > 0) {
                    ctMoi = taoChiTietTuDong(modelGoc, i, hdMoi, slGiuLai);
                    dsChiTietMoi.add(ctMoi);
                } else if (slTra > 0) {
                    ctMoi = taoChiTietTuDong(modelGoc, i, hdMoi, 0);
                    dsChiTietMoi.add(ctMoi);
                }

                if (slTra > 0 && ctMoi != null) {
                    int heSoQuyDoi = ctGoc.getDonViQuyDoi().getHeSoQuyDoi();
                    int slCanHoanNhoNhat = slTra * heSoQuyDoi;
                    int slLoiNhoNhat = slLoi * heSoQuyDoi;

                    List<SuPhanBoLo> dsPhanBoBanDau = ctGoc.getDsPhanBoLo();

                    if (dsPhanBoBanDau != null) {
                        for (SuPhanBoLo pbGoc : dsPhanBoBanDau) {
                            if (slCanHoanNhoNhat <= 0)
                                break;
                            int luongGocTrongDBDaQuyDoi = pbGoc.getSoLuongPhanBo();
                            int hoanThucTe = Math.min(slCanHoanNhoNhat, luongGocTrongDBDaQuyDoi);

                            int hoanLoi = Math.min(slLoiNhoNhat, hoanThucTe);
                            if (hoanLoi > 0) {
                                SuPhanBoLo traLaiLoi = new SuPhanBoLo();
                                traLaiLoi.setLo(pbGoc.getLo());
                                traLaiLoi.setSoLuongPhanBo(hoanLoi);
                                traLaiLoi.setLoi(true);
                                traLaiLoi.setChiTietHoaDon(ctMoi);
                                dsTraLai.add(traLaiLoi);
                                slLoiNhoNhat -= hoanLoi;
                            }

                            int hoanNormal = hoanThucTe - hoanLoi;
                            if (hoanNormal > 0) {
                                SuPhanBoLo traLaiNormal = new SuPhanBoLo();
                                traLaiNormal.setLo(pbGoc.getLo());
                                traLaiNormal.setSoLuongPhanBo(hoanNormal);
                                traLaiNormal.setLoi(false);
                                traLaiNormal.setChiTietHoaDon(ctMoi);
                                dsTraLai.add(traLaiNormal);
                            }

                            slCanHoanNhoNhat -= hoanThucTe;
                        }
                    }
                }
            }

            // BƯỚC B: XỬ LÝ HÀNG MUA MỚI (Trừ kho, Bao gồm cả Quà Tặng)
            LoService loService = new LoService();
            for (int i = 0; i < modelDoi.getRowCount(); i++) {
                int slMoi = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                Boolean isGift = (Boolean) modelDoi.getValueAt(i, 8);

                ChiTietHoaDon ctMoi = taoChiTietTuDong(modelDoi, i, hdMoi, slMoi);
                ctMoi.setLaQuaTangKem(isGift != null ? isGift : false);
                if (isGift != null && isGift) {
                    ctMoi.setDonGia(0); // Quà tặng có giá = 0
                }

                ChiTietHoaDon targetCt = ctMoi;

                boolean isMerged = false;
                for (ChiTietHoaDon existing : dsChiTietMoi) {
                    if (existing.getDonViQuyDoi().getMaDonVi().equals(ctMoi.getDonViQuyDoi().getMaDonVi())
                            && existing.isLaQuaTangKem() == ctMoi.isLaQuaTangKem()) {
                        existing.setSoLuongBan(existing.getSoLuongBan() + ctMoi.getSoLuongBan());
                        targetCt = existing;
                        isMerged = true;
                        break;
                    }
                }

                if (!isMerged) {
                    dsChiTietMoi.add(ctMoi);
                }

                int soLuongCanTru = ctMoi.getSoLuongBan() * ctMoi.getDonViQuyDoi().getHeSoQuyDoi();
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

            double soTienLamTron = Math.round(Math.abs(chenhLech) / 1000.0) * 1000;
            double soTienThucTeGiaoDich = (chenhLech < 0) ? -soTienLamTron : soTienLamTron;

            if (hoaDonService.luuHoaDonDoiHang(hdMoi, dsTraLai, dsChiTietMoi, dsPhanBoMoi, soTienThucTeGiaoDich)) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công hóa đơn đổi: " + maHoaDonMoi);

                double tienKhachDua = 0;
                double tienThoi = 0;
                try {
                    String kd = txtKhachDua.getText().replaceAll("[^\\d]", "");
                    tienKhachDua = kd.isEmpty() ? 0 : Double.parseDouble(kd);

                    String tt = txtTienThoi.getText().replaceAll("[^\\d]", "");
                    tienThoi = tt.isEmpty() ? 0 : Double.parseDouble(tt);
                } catch (Exception ignored) {
                }

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
        if (txtKhuyenMaiMoi != null)
            txtKhuyenMaiMoi.setText(formatVND(0));
        txtChenhLech.setText(formatVND(0));
        txtThanhTienLamTron.setText(formatVND(0));
        txtKhachDua.setText("");
        txtTienThoi.setText(formatVND(0));
        cboKhuyenMai.setSelectedIndex(0);
        taiLaiDanhSachKhuyenMai();
        hoaDonGocHienTai = null;
        chiTietHoaDonGocList.clear();
        tongTienHoaDonGocBanDau = 0;
        ((DefaultTableModel) tblHoaDonGoc.getModel()).setRowCount(0);
        ((DefaultTableModel) tblSanPham.getModel()).setRowCount(0);
    }

    private JTable createTable(boolean isGoc) {
        String[] cols = isGoc
                ? new String[] { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng Đổi", "Đơn giá", "Thuế", "Thành tiền",
                        "Số lượng lỗi" }
                : new String[] { "Mã SP", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền",
                        "Số lượng lỗi", "IS_GIFT" };

        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                boolean isBangGoc = (this == tblHoaDonGoc.getModel());

                if (isBangGoc && chiTietHoaDonGocList != null && row < chiTietHoaDonGocList.size()) {
                    return col == 3 || col == 7;
                }

                if (!isBangGoc) {
                    try {
                        Boolean isGift = (Boolean) this.getValueAt(row, 8);
                        if (isGift != null && isGift)
                            return false; // Quà tặng không được sửa

                        String maSP = this.getValueAt(row, 0).toString();
                        SanPham sp = sanPhamService.timTheoMa(maSP);
                        if (sp != null && sp.getLoaiSanPham().name().equals("ETC")) {
                            return false;
                        }
                    } catch (Exception e) {
                        return false;
                    }
                    return col == 2 || col == 3;
                }
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(35);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r, int c) {
                if (v instanceof Number)
                    v = formatVND(((Number) v).doubleValue());
                Component comp = super.getTableCellRendererComponent(t, v, s, h, r, c);

                if (!isGoc) {
                    Boolean isGift = (Boolean) t.getModel().getValueAt(r, 8);
                    if (isGift != null && isGift) {
                        comp.setForeground(new Color(40, 167, 69)); // Màu Xanh lá cho quà
                        comp.setFont(comp.getFont().deriveFont(Font.ITALIC));
                    } else {
                        if (!s)
                            comp.setForeground(Color.BLACK);
                        comp.setFont(comp.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return comp;
            }
        };
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);

        DefaultTableCellRenderer giftRenderer = new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, h, r, c);
                if (!isGoc) {
                    Boolean isGift = (Boolean) t.getModel().getValueAt(r, 8);
                    if (isGift != null && isGift) {
                        comp.setForeground(new Color(40, 167, 69));
                        comp.setFont(comp.getFont().deriveFont(Font.ITALIC));
                    } else {
                        if (!s)
                            comp.setForeground(Color.BLACK);
                        comp.setFont(comp.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return comp;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i == 4 || i == 6) {
                table.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
            } else if (i == 5) {
                table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean h, int r,
                            int c) {
                        if (v != null)
                            v = String.format("%.1f %%", Double.parseDouble(v.toString().replace("%", "")));
                        setHorizontalAlignment(JLabel.CENTER);
                        Component comp = super.getTableCellRendererComponent(t, v, s, h, r, c);
                        if (!isGoc) {
                            Boolean isGift = (Boolean) t.getModel().getValueAt(r, 8);
                            if (isGift != null && isGift) {
                                comp.setForeground(new Color(40, 167, 69));
                                comp.setFont(comp.getFont().deriveFont(Font.ITALIC));
                            } else {
                                if (!s)
                                    comp.setForeground(Color.BLACK);
                                comp.setFont(comp.getFont().deriveFont(Font.PLAIN));
                            }
                        }
                        return comp;
                    }
                });
            } else if (i != 3 && i != 7) {
                table.getColumnModel().getColumn(i).setCellRenderer(giftRenderer);
            }
        }

        table.getColumnModel().getColumn(2).setCellEditor(new DynamicUnitCellEditor());
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());
        table.getColumnModel().getColumn(7).setCellEditor(new QuantitySpinnerEditor());

        if (!isGoc) {
            table.getColumnModel().getColumn(8).setMinWidth(0);
            table.getColumnModel().getColumn(8).setMaxWidth(0);
            table.getColumnModel().getColumn(8).setPreferredWidth(0);
            
            table.getColumnModel().getColumn(7).setMinWidth(0);
            table.getColumnModel().getColumn(7).setMaxWidth(0);
            table.getColumnModel().getColumn(7).setPreferredWidth(0);
        }

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

        // Khởi tạo ComboBox Khuyến Mãi
        cboKhuyenMai = new JComboBox<>();
        cboKhuyenMai.addItem("-- Không áp dụng --");
        dsKhuyenMai = khuyenMaiService.layKhuyenMaiConHan(false);
        for (KhuyenMai km : dsKhuyenMai) {
            cboKhuyenMai.addItem(km.getTenKhuyenMai());
        }
        cboKhuyenMai.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                // Truyền isSelected và cellHasFocus là false để không hiện màu xanh khi hover
                super.getListCellRendererComponent(list, value, index, false, false);

                if (index >= 0) {
                    setEnabled(false); // Làm mờ các dòng trong danh sách xổ xuống
                } else {
                    setEnabled(true); // Ô hiển thị chính vẫn rõ nét
                }

                if (index > 0 && dsKhuyenMai != null && index - 1 < dsKhuyenMai.size()) {
                    double tongTienMuaMoiBase = 0;
                    if (tblSanPham != null && tblSanPham.getModel() != null && tblHoaDonGoc != null
                            && tblHoaDonGoc.getModel() != null) {
                        DefaultTableModel modelDoi = (DefaultTableModel) tblSanPham.getModel();
                        DefaultTableModel modelGoc = (DefaultTableModel) tblHoaDonGoc.getModel();
                        for (int i = 0; i < modelDoi.getRowCount(); i++) {
                            Boolean isGift = (Boolean) modelDoi.getValueAt(i, 8);
                            if (isGift == null || !isGift) {
                                String maSP = modelDoi.getValueAt(i, 0).toString();

                                // Thuốc ETC đổi 1-1 thì không áp dụng KM
                                SanPham sp = sanPhamService.timTheoMa(maSP);
                                if (sp != null && sp.getLoaiSanPham().name().equals("ETC")) {
                                    continue;
                                }

                                String donVi = modelDoi.getValueAt(i, 2).toString();
                                int qty = Integer.parseInt(modelDoi.getValueAt(i, 3).toString());
                                double price = Double.parseDouble(modelDoi.getValueAt(i, 4).toString());

                                int slTra = 0;
                                for (int j = 0; j < modelGoc.getRowCount(); j++) {
                                    if (modelGoc.getValueAt(j, 0).equals(maSP)
                                            && modelGoc.getValueAt(j, 2).equals(donVi)) {
                                        slTra += Integer.parseInt(modelGoc.getValueAt(j, 3).toString());
                                    }
                                }

                                int slThucMuaMoi = Math.max(0, qty - slTra);
                                tongTienMuaMoiBase += slThucMuaMoi * price;
                            }
                        }
                    }
                    KhuyenMai km = dsKhuyenMai.get(index - 1);
                    // Hiện badge cho KM ưu đãi thành viên
                    String prefix = km.isUuDaiThanhVien() ? "★ [Thành viên] " : "";
                    String text = prefix + km.getTenKhuyenMai();
                    if (tongTienMuaMoiBase >= km.getGiaTriDonHangToiThieu()) {
                        if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                            double giam = tongTienMuaMoiBase * km.getKhuyenMaiPhanTram() / 100.0;
                            text += " (Giảm " + new DecimalFormat("#,### đ").format(giam) + ")";
                        } else {
                            text += " (Tặng quà)";
                        }
                    } else {
                        text += " (Chưa đủ điều kiện)";
                    }
                    setText(text);
                    // Tô vàng cho KM thành viên để phân biệt
                    if (km.isUuDaiThanhVien()) {
                        setForeground(new Color(180, 120, 0));
                    }
                }
                return this;
            }
        });
        cboKhuyenMai.addActionListener(e -> {
            if (!isAutoSelectingPromotion) {
                autoSelectBestKhuyenMai();
            } else {
                capNhatQuaTang();
            }
        });

        g.gridy = r++;
        c.add(new JLabel("Ghi chú:"), g);
        g.gridy = r++;
        txtGhiChu = new JTextArea(2, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        c.add(new JScrollPane(txtGhiChu), g);
        addInputRow(c, "Tiền HĐ gốc:", txtTienGoc = new JTextField("0 VNĐ"), g, r++);
        addInputRow(c, "Tiền HĐ đổi:", txtTienDoi = new JTextField("0 VNĐ"), g, r++);
        addInputRow(c, "KM mới:", cboKhuyenMai, g, r++);
        addInputRow(c, "Tiền KM giảm:", txtKhuyenMaiMoi = new JTextField("0 VNĐ"), g, r++);
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

        JLabel l = new JLabel("", SwingConstants.CENTER);
        l.setPreferredSize(new Dimension(0, 150));
        l.setOpaque(true);
        l.setBackground(Color.WHITE);
        l.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        try {
            java.net.URL imgURL = getClass().getResource("/images/QR.jpg");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
                l.setIcon(new ImageIcon(img));
            } else {
                l.setText("Không tìm thấy ảnh QR");
            }
        } catch (Exception e) {
            l.setText("Lỗi tải ảnh");
        }

        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private void addInputRow(JPanel p, String lbl, JComponent t, GridBagConstraints g, int r) {
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
                txtKhuyenMaiMoi, txtChenhLech };
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
            } catch (Exception ex) {
            }
            int max = 9999;
            int min = 0;

            if (t == tblHoaDonGoc) {
                String maSP = t.getValueAt(r, 0).toString();
                for (ChiTietHoaDon ct : chiTietHoaDonGocList) {
                    if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                        max = ct.getSoLuongBan();
                        break;
                    }
                }
                min = 0;
            } else {
                min = 1;
            }
            s.setModel(new SpinnerNumberModel(currentVal, min, max, 1));
            return s;
        }

        @Override
        public Object getCellEditorValue() {
            try {
                s.commitEdit();
            } catch (Exception ignored) {
            }
            return s.getValue();
        }
    }
}