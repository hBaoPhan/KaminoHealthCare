package com.example.gui.screens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.example.service.ChiTietHoaDonService;
import com.example.service.HoaDonService;

import java.awt.*;
import java.text.DecimalFormat;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.TaiKhoan;
import com.example.gui.components.RoundedButton;
import com.example.gui.components.RoundedPanel;
import com.example.gui.components.RoundedTextField;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.sql.Timestamp;

import javax.swing.table.TableCellEditor;

public class TraHangPanel extends JPanel {

    // =========================================================================
    // VÙNG 1: KHAI BÁO BIẾN (UI COMPONENTS & DATA)
    // =========================================================================

    // --- Các thành phần giao diện ---
    private RoundedTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private RoundedTextField txtTienGoc, txtTienTra, txtChenhLech, txtThue, txtThanhTien, txtTienTraLai;
    private RoundedTextField txtSearch;
    private JTextArea txtGhiChu;
    private RoundedButton btnThanhToan;
    private JCheckBox chkTienMat, chkChuyenKhoan;

    // --- Biến xử lý dữ liệu và Database ---
    private HoaDonService hoaDonService = new HoaDonService();
    private ChiTietHoaDonService chiTietHoaDonService = new ChiTietHoaDonService();
    private DefaultTableModel model; // Để đổ dữ liệu vào bảng
    private List<ChiTietHoaDon> dsChiTietGoc = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("###,###,### VND");
    private HoaDon hd;
    private TaiKhoan taiKhoan;
    private com.example.entity.NhanVien nhanVien;
    private JTable table;
    private final StringBuilder barcodeBuffer = new StringBuilder();
    private long lastKeyTime = 0;

    // =========================================================================
    // VÙNG 2: HÀM KHỞI TẠO (CONSTRUCTOR)
    // =========================================================================

    public TraHangPanel() {
        this(null);
    }

    public TraHangPanel(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
        this.nhanVien = (taiKhoan != null) ? taiKhoan.getNhanVien() : null;
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // --- PHẦN BÊN TRÁI: DANH SÁCH SẢN PHẨM HÓA ĐƠN TRẢ ---
        add(createTablePanel("Danh sách sản phẩm hóa đơn trả", "Mã hóa đơn"), BorderLayout.CENTER);

        // --- PHẦN BÊN PHẢI: THÔNG TIN HÓA ĐƠN TRẢ HÀNG ---
        add(createInfoPanel(), BorderLayout.EAST);

        // Thiết lập bộ đón bắt phím toàn cục (KeyEventDispatcher) cho máy quét barcode
        // tìm hóa đơn
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (!isShowing()) {
                return false;
            }

            // Ngăn ngừa lỗi đúp sự kiện khi người dùng đang active focus trong các ô nhập
            // văn bản có thể chỉnh sửa
            Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
            boolean isEditableFocused = (focusOwner instanceof javax.swing.text.JTextComponent)
                    && ((javax.swing.text.JTextComponent) focusOwner).isEditable();
            if (isEditableFocused) {
                return false;
            }

            if (e.getID() == java.awt.event.KeyEvent.KEY_TYPED) {
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
                        SwingUtilities.invokeLater(() -> {
                            hienThiSanPhamHoaDon(barcode);
                            if (txtSearch != null) {
                                txtSearch.setText(barcode);
                                txtSearch.setForeground(Color.BLACK);
                            }
                        });
                        barcodeBuffer.setLength(0);
                        return true; // Tiêu hủy sự kiện phím Enter
                    }
                    barcodeBuffer.setLength(0);
                } else if (Character.isLetterOrDigit(c) || c == '-') {
                    barcodeBuffer.append(c);
                }
            }
            return false;
        });

        // Tự động focus vào ô tìm kiếm khi vào trang
        this.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & java.awt.event.HierarchyEvent.SHOWING_CHANGED) != 0) {
                if (isShowing()) {
                    SwingUtilities.invokeLater(() -> {
                        if (txtSearch != null && txtSearch.isShowing()) {
                            txtSearch.requestFocusInWindow();
                        }
                    });
                }
            }
        });
    }

    // =========================================================================
    // VÙNG 3: KHỞI TẠO GIAO DIỆN (UI BUILDING)
    // =========================================================================

    /**
     * Tạo Panel chứa bảng dữ liệu và thanh tìm kiếm phía trên
     */
    private JPanel createTablePanel(String title, String placeholder) {
        RoundedPanel pnl = new RoundedPanel(16);
        pnl.setLayout(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);

        // Header Panel (Tiêu đề + Ô tìm kiếm)
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Panel chứa Ô nhập và Nút Tìm
        JPanel pnlSearchAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlSearchAction.setOpaque(false);

        txtSearch = new RoundedTextField(placeholder, 15);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String ma = txtSearch.getText().trim();
                    if (!ma.isEmpty() && !ma.equals(placeholder)) {
                        hienThiSanPhamHoaDon(ma);
                    }
                }
            }
        });
        txtSearch.setPreferredSize(new Dimension(180, 30));

        RoundedButton btnSearch = new RoundedButton("Tìm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setPreferredSize(new Dimension(65, 30));
        btnSearch.addActionListener(e -> {
            String ma = txtSearch.getText().trim();
            if (!ma.isEmpty() && !ma.equals(placeholder)) {
                hienThiSanPhamHoaDon(ma);
            }
        });

        pnlSearchAction.add(txtSearch);
        pnlSearchAction.add(btnSearch);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlSearchAction, BorderLayout.EAST);

        // Bảng dữ liệu
        String[] columns = { "Mã sản phẩm", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền",
                "Số lượng lỗi" };
        Object[][] data = {};
        model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 7; // Chỉ cho phép sửa cột số lượng (col 3) và số lượng lỗi (col 7)
            }
        };
        table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());
        table.getColumnModel().getColumn(7).setCellEditor(new QuantitySpinnerEditor());

        // Lắng nghe sự kiện người dùng gõ sửa số lượng
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Chỉ xử lý khi cột số 3 (Số lượng) hoặc cột số 7 (Số lượng lỗi) bị thay đổi
                if (e.getType() == TableModelEvent.UPDATE && (e.getColumn() == 3 || e.getColumn() == 7)) {
                    int row = e.getFirstRow();
                    try {
                        int soLuongMoi = Integer.parseInt(model.getValueAt(row, 3).toString());
                        int soLuongLoiMoi = Integer.parseInt(model.getValueAt(row, 7).toString());
                        String maSP = model.getValueAt(row, 0).toString();

                        // Lấy số lượng mua gốc
                        int soLuongGoc = 0;
                        for (ChiTietHoaDon ct : dsChiTietGoc) {
                            if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                                soLuongGoc = ct.getSoLuongBan();
                                break;
                            }
                        }

                        // Bắt lỗi nhập bậy
                        if (soLuongMoi <= 0 || soLuongMoi > soLuongGoc) {
                            JOptionPane.showMessageDialog(null,
                                    "Số lượng trả phải lớn hơn 0 và tối đa là " + soLuongGoc);
                            model.setValueAt(soLuongGoc, row, 3); // Hoàn nguyên số cũ
                            return;
                        }

                        if (soLuongLoiMoi < 0 || soLuongLoiMoi > soLuongMoi) {
                            JOptionPane.showMessageDialog(null, "Số lượng lỗi phải từ 0 đến " + soLuongMoi);
                            model.setValueAt(0, row, 7); // Hoàn nguyên về 0
                            return;
                        }
                        // Cập nhật cột Thành tiền trong JTable cho dòng này
                        double donGia = 0;
                        double thueSuat = 0;
                        for (ChiTietHoaDon ct : dsChiTietGoc) {
                            if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                                donGia = ct.getDonGia();
                                thueSuat = ct.getDonViQuyDoi().getSanPham().getThue();
                                break;
                            }
                        }
                        double thanhTienMoiRow = soLuongMoi * donGia * (1 + thueSuat / 100.0);
                        model.setValueAt(df.format(thanhTienMoiRow), row, 6);
                        // Kích hoạt tính tiền lại toàn bộ
                        tinhToanTienHoanTra();
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Vui lòng chỉ nhập số nguyên!");
                    }
                }
            }
        });

        // Menu chuột phải để XÓA mặt hàng khỏi danh sách trả
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuXoa = new JMenuItem("Xóa khỏi danh sách trả");
        popupMenu.add(menuXoa);
        table.setComponentPopupMenu(popupMenu);

        menuXoa.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                model.removeRow(selectedRow);
                tinhToanTienHoanTra(); // Xóa xong phải tính lại tiền
            }
        });

        pnl.add(pnlHeader, BorderLayout.NORTH);
        pnl.add(new JScrollPane(table), BorderLayout.CENTER);
        return pnl;
    }

    /**
     * Tạo Panel chứa thông tin hóa đơn và thanh toán bên phải
     */
    private JPanel createInfoPanel() {
        RoundedPanel pnlMain = new RoundedPanel(16);
        pnlMain.setLayout(new BorderLayout());
        pnlMain.setPreferredSize(new Dimension(380, 0));
        pnlMain.setBackground(new Color(248, 248, 248));

        JLabel lblTitle = new JLabel("Hóa đơn trả hàng", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        pnlMain.add(lblTitle, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new GridBagLayout());
        pnlContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(3, 15, 3, 15);
        gbc.weightx = 1.0;

        int r = 0;
        // Phần thông tin chung
        txtMaHoaGoc = new RoundedTextField(15);
        txtMaHoaGoc.setText("HDB27032026001");
        addInputRow(pnlContent, "Mã hóa gốc", txtMaHoaGoc, gbc, r++);

        txtMaHoaDon = new RoundedTextField(15);
        txtMaHoaDon.setText("HDT27032026001");
        addInputRow(pnlContent, "Mã hóa đơn", txtMaHoaDon, gbc, r++);

        txtNgayTao = new RoundedTextField(15);
        txtNgayTao.setText("27/03/2026");
        addInputRow(pnlContent, "Ngày tạo", txtNgayTao, gbc, r++);

        String tenNguoiTao = (nhanVien != null) ? nhanVien.getTenNhanVien() : "Phan Hoai Bao";
        txtNguoiTao = new RoundedTextField(15);
        txtNguoiTao.setText(tenNguoiTao);
        addInputRow(pnlContent, "Người tạo", txtNguoiTao, gbc, r++);

        txtTenKhachHang = new RoundedTextField(15);
        txtTenKhachHang.setText("Tran Tan Tai");
        addInputRow(pnlContent, "Tên khách hàng", txtTenKhachHang, gbc, r++);

        gbc.gridy = r++;
        JLabel lblGhiChu = new JLabel("Ghi chú:");
        lblGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlContent.add(lblGhiChu, gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(6, 20);
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtGhiChu.setLineWrap(true);
        txtGhiChu.setWrapStyleWord(true);
        txtGhiChu.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChu);
        scrollGhiChu.setPreferredSize(new Dimension(200, 80));
        scrollGhiChu.setMinimumSize(new Dimension(200, 80));
        pnlContent.add(scrollGhiChu, gbc);

        gbc.gridy = r++;
        pnlContent.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        // Phần tính toán tiền
        txtTienGoc = new RoundedTextField(15);
        txtTienGoc.setText("220.500Đ");
        addInputRow(pnlContent, "Tiền hóa đơn gốc :", txtTienGoc, gbc, r++);

        txtTienTra = new RoundedTextField(15);
        txtTienTra.setText("105.750Đ");
        addInputRow(pnlContent, "Tiền hóa đơn trả :", txtTienTra, gbc, r++);

        txtChenhLech = new RoundedTextField(15);
        txtChenhLech.setText("Không");
        addInputRow(pnlContent, "Khuyến mãi đã áp dụng:", txtChenhLech, gbc, r++);

        txtThue = new RoundedTextField(15);
        txtThue.setText("5.250Đ");
        addInputRow(pnlContent, "Tổng tiền thuế:", txtThue, gbc, r++);

        txtThanhTien = new RoundedTextField(15);
        txtThanhTien.setText("114.750Đ");
        addInputRow(pnlContent, "Thành tiền :", txtThanhTien, gbc, r++);

        // Phương thức thanh toán mặc định là tiền mặt (không cho chọn trên UI)

        txtTienTraLai = new RoundedTextField(15);
        txtTienTraLai.setText("114.750Đ");
        addInputRow(pnlContent, "Tiền trả lại:", txtTienTraLai, gbc, r++);

        // Nút Thanh Toán
        btnThanhToan = new RoundedButton("Thanh Toán");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setPreferredSize(new Dimension(0, 45));

        // Sự kiện Thanh toán và Trả hàng
        btnThanhToan.addActionListener(e -> {
            if (table != null && table.isEditing()) {
                table.getCellEditor().stopCellEditing();
            }
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có sản phẩm nào để trả hàng!", "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn hoàn tất giao dịch trả hàng này không?\nTổng tiền trả khách: "
                            + txtTienTraLai.getText(),
                    "Xác nhận trả hàng",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                HoaDon hoaDonTra = new HoaDon();
                hoaDonTra.setThoiGianTao(java.time.LocalDateTime.now());
                hoaDonTra.setMaHoaDon(txtMaHoaDon.getText()); // Mã mới: HDT...

                if (hd != null) {
                    hoaDonTra.setKhachHang(hd.getKhachHang());
                    hoaDonTra.setKhuyenMai(hd.getKhuyenMai());
                }

                HoaDon hdGoc = new HoaDon();
                hdGoc.setMaHoaDon(txtMaHoaGoc.getText());
                hoaDonTra.setHoaDonDoiTra(hdGoc);
                hoaDonTra.setGhiChu(txtGhiChu.getText());

                com.example.entity.NhanVien activeNv = (nhanVien != null) ? nhanVien
                        : new com.example.entity.NhanVien("QL001");
                hoaDonTra.setNhanVien(activeNv);

                hoaDonTra.setLoaiHoaDon(com.example.entity.enums.LoaiHoaDon.TRA_HANG);
                hoaDonTra.setPhuongThucThanhToan(com.example.entity.enums.PhuongThucThanhToan.TIEN_MAT);
                com.example.entity.CaLam ca = hoaDonService.layCaHienTai(activeNv.getMaNhanVien());
                if (ca == null) {
                    JOptionPane.showMessageDialog(this, "Chưa mở ca làm việc!");
                    return;
                }
                hoaDonTra.setCa(ca);

                // 2. Gom danh sách sản phẩm thực tế từ bảng vào hóa đơn
                List<ChiTietHoaDon> dsTra = new ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    String maSP = model.getValueAt(i, 0).toString();
                    int slTra = Integer.parseInt(model.getValueAt(i, 3).toString());
                    int slLoi = Integer.parseInt(model.getValueAt(i, 7).toString());

                    if (slLoi > slTra) {
                        JOptionPane.showMessageDialog(this, "Số lượng lỗi không được vượt quá số lượng trả!");
                        return;
                    }

                    // Tìm lại thông tin gốc để lấy Đơn vị quy đổi và Đơn giá
                    for (ChiTietHoaDon ctGoc : dsChiTietGoc) {
                        if (ctGoc.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                            ChiTietHoaDon ctMoi = new ChiTietHoaDon();
                            ctMoi.setDonViQuyDoi(ctGoc.getDonViQuyDoi());
                            ctMoi.setSoLuongBan(slTra);
                            ctMoi.setSoLuongLoi(slLoi);
                            ctMoi.setDonGia(ctGoc.getDonGia());
                            dsTra.add(ctMoi);
                            break;
                        }
                    }
                }
                hoaDonTra.setDsChiTiet(dsTra);

                // BƯỚC BỔ SUNG: Lấy danh sách Lô đã bán từ hóa đơn gốc để hoàn trả đúng lô.
                // Ủy quyền cho HoaDonService — không viết SQL trực tiếp trong tầng UI.
                List<com.example.entity.SuPhanBoLo> dsPhanBoTra = hoaDonService.layDanhSachPhanBoLoCanTra(
                        txtMaHoaGoc.getText().trim(), dsTra);

                if (hoaDonService.luuHoaDonTraHang(hoaDonTra, dsPhanBoTra)) {
                    double tienHoanLai = 0;
                    try {
                        String raw = txtTienTraLai.getText().replaceAll("[^\\d]", "");
                        tienHoanLai = raw.isEmpty() ? 0 : Double.parseDouble(raw);
                    } catch (Exception ex) {
                        // Bỏ qua lỗi parse
                    }

                    // Hiển thị trực quan hóa đơn trả hàng xem trước và hỏi in ấn
                    com.example.utils.InHoaDonPOS.inHoaDon(hoaDonTra, dsTra, tienHoanLai, 0);

                    // Xóa sạch dữ liệu trên giao diện để làm hóa đơn mới
                    lamMoiGiaoDien();
                } else {
                    JOptionPane.showMessageDialog(TraHangPanel.this, "Lỗi khi lưu dữ liệu vào hệ thống!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }); // Kết thúc sự kiện btnThanhToan

        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(10, 15, 20, 15));
        pnlBottom.add(btnThanhToan, BorderLayout.CENTER);

        pnlMain.add(pnlContent, BorderLayout.CENTER);
        pnlMain.add(pnlBottom, BorderLayout.SOUTH);

        setupStyles();
        return pnlMain;
    }

    // =========================================================================
    // VÙNG 4: XỬ LÝ DỮ LIỆU & TÍNH TOÁN (DATA PROCESSING)
    // =========================================================================

    /** Hiển thị sản phẩm của hóa đơn gốc lên bảng dựa vào mã nhập */
    private void hienThiSanPhamHoaDon(String maHD) {
        // Lấy hóa đơn — layHoaDonDeDoi() đã kiểm tra: đã TT, còn hạn 7 ngày, chưa đổi
        // trả
        this.hd = hoaDonService.layHoaDonDeDoi(maHD);

        if (this.hd == null) {
            // Phân biệt: HD không tồn tại vs HD không đủ điều kiện
            HoaDon hdCheck = hoaDonService.timTheoMa(maHD);
            if (hdCheck == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn có mã: " + maHD, "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hóa đơn này không đủ điều kiện đổi trả!\n(Lý do: Có thể đã quá hạn 7 ngày, chưa thanh toán, hoặc đã được đổi/trả trước đó)",
                        "Từ chối", JOptionPane.WARNING_MESSAGE);
            }
            lamMoiGiaoDien();
            return;
        }

        dsChiTietGoc = chiTietHoaDonService.layTheoMaHoaDon(maHD);

        // --- XỬ LÝ LỖI DUPLICATE ---
        // Không load các sản phẩm là Quà Tặng (isLaQuaTangKem = true) lên danh sách trả
        // hàng.
        dsChiTietGoc.removeIf(ChiTietHoaDon::isLaQuaTangKem);

        // --- KHÔNG CHO TRẢ THUỐC ETC ---
        dsChiTietGoc.removeIf(
                ct -> ct.getDonViQuyDoi().getSanPham().getLoaiSanPham() == com.example.entity.enums.LoaiSanPham.ETC);

        this.hd.setDsChiTiet(dsChiTietGoc);

        // Điền thông tin lên giao diện
        txtMaHoaGoc.setText(this.hd.getMaHoaDon());
        txtTenKhachHang.setText(this.hd.getKhachHang() != null ? this.hd.getKhachHang().getTenKhachHang() : "Khách lẻ");
        txtTienGoc.setText(df.format(this.hd.tinhTongTienThanhToan()));

        // Ngày tạo hiện tại và Tự sinh mã trả
        txtNgayTao.setText(new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(new java.util.Date()));
        txtMaHoaDon.setText(tuSinhMaHoaDonTra());

        // Đổ dữ liệu vào bảng
        model.setRowCount(0);
        for (ChiTietHoaDon ct : dsChiTietGoc) {
            model.addRow(new Object[] {
                    ct.getDonViQuyDoi().getSanPham().getMaSanPham(),
                    ct.getDonViQuyDoi().getSanPham().getTenSanPham(),
                    ct.getDonViQuyDoi().getTenDonVi().getMoTa(),
                    ct.getSoLuongBan(),
                    df.format(ct.getDonGia()),
                    df.format(ct.tinhTienThue()),
                    df.format(ct.tinhThanhTien()),
                    0
            });
        }
        tinhToanTienHoanTra();
    }

    /** Tính toán tổng số tiền hoàn trả lại cho khách */
    private void tinhToanTienHoanTra() {
        double tongTienTra = 0;
        double tongThueTra = 0;

        // Lặp qua tất cả các dòng còn lại trên bảng
        for (int i = 0; i < model.getRowCount(); i++) {
            String maSP = model.getValueAt(i, 0).toString();
            int soLuongTra = Integer.parseInt(model.getValueAt(i, 3).toString());

            // Tìm đối tượng gốc tương ứng để lấy đơn giá và thuế suất
            for (ChiTietHoaDon ct : dsChiTietGoc) {
                if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                    double donGia = ct.getDonGia();
                    double thueSuat = ct.getDonViQuyDoi().getSanPham().getThue();

                    double tienTra = soLuongTra * donGia;
                    double thue = tienTra * (thueSuat / 100.0);

                    tongTienTra += tienTra;
                    tongThueTra += thue;
                    break;
                }
            }
        }

        double thanhTien = tongTienTra + tongThueTra;

        // Xử lý giảm trừ tiền trả lại nếu hóa đơn gốc đã áp dụng khuyến mãi giảm %
        double soTienGiamKM = 0;
        if (this.hd != null && this.hd.getKhuyenMai() != null) {
            if (this.hd.getKhuyenMai().getLoaiKhuyenMai() == com.example.entity.enums.LoaiKhuyenMai.PHAN_TRAM) {
                soTienGiamKM = thanhTien * (this.hd.getKhuyenMai().getKhuyenMaiPhanTram() / 100.0);
            }
        }
        double tienTraLaiKhach = thanhTien - soTienGiamKM;
        // Cập nhật lên các ô nhập liệu bên phải
        txtTienTra.setText(df.format(tongTienTra));
        txtThue.setText(df.format(tongThueTra));
        txtThanhTien.setText(df.format(thanhTien));
        txtTienTraLai.setText(df.format(tienTraLaiKhach));

        // Hiển thị Khuyến mãi đã áp dụng ở txtChenhLech
        if (this.hd != null && this.hd.getKhuyenMai() != null) {
            String tenKM = this.hd.getKhuyenMai().getTenKhuyenMai();
            if (this.hd.getKhuyenMai().getLoaiKhuyenMai() == com.example.entity.enums.LoaiKhuyenMai.PHAN_TRAM) {
                txtChenhLech.setText(tenKM + " (-" + this.hd.getKhuyenMai().getKhuyenMaiPhanTram() + "%)");
            } else {
                txtChenhLech.setText(tenKM);
            }
        } else {
            txtChenhLech.setText("Không");
        }
    }

    // =========================================================================
    // VÙNG 5: LOGIC LÀM MỚI (RESET LOGIC)
    // =========================================================================

    /** Làm mới toàn bộ giao diện về trạng thái rỗng */
    private void lamMoiGiaoDien() {
        txtSearch.setText("");

        txtMaHoaGoc.setText("");
        txtMaHoaDon.setText("");
        txtTenKhachHang.setText("");
        txtGhiChu.setText("");

        // Reset các ô tiền
        String zero = "0 VND";
        txtTienGoc.setText(zero);
        txtTienTra.setText(zero);
        txtThue.setText(zero);
        txtThanhTien.setText(zero);
        txtTienTraLai.setText(zero);
        txtChenhLech.setText("Không");

        // Dọn dẹp dữ liệu logic
        model.setRowCount(0);
        this.hd = null;
        if (dsChiTietGoc != null) {
            dsChiTietGoc.clear();
        }
    }

    // =========================================================================
    // VÙNG 6: CÁC HÀM HỖ TRỢ VÀ INNER CLASS (HELPERS & COMPONENT)
    // =========================================================================

    private void addInputRow(JPanel pnl, String labelText, RoundedTextField txt, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setPreferredSize(new Dimension(165, 35));
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rowPanel.add(lbl, BorderLayout.WEST);
        rowPanel.add(txt, BorderLayout.CENTER);
        pnl.add(rowPanel, gbc);
    }

    /** Cấu hình màu nền mờ cho các textfield chỉ đọc */
    private void setupStyles() {
        RoundedTextField[] readonly = { txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang,
                txtTienGoc, txtTienTra, txtChenhLech, txtThue, txtThanhTien, txtTienTraLai };
        for (RoundedTextField f : readonly) {
            f.setEditable(false);
            f.setBackground(new Color(235, 235, 235));
            f.setHorizontalAlignment(JTextField.LEFT);
        }
    }

    /** Hàm tự sinh mã hóa đơn trả — ủy quyền cho HoaDonService */
    private String tuSinhMaHoaDonTra() {
        return hoaDonService.sinhMaHoaDon(com.example.entity.enums.LoaiHoaDon.TRA_HANG);
    }

    // ====================================================================
    // Lớp hỗ trợ tạo nút Tăng/Giảm (Spinner) cho cột Số lượng trong bảng
    // ====================================================================
    private class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner = new JSpinner();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            String maSP = table.getValueAt(row, 0).toString();
            int soLuongGoc = 9999;

            // Tìm số lượng tối đa khách đã mua trong hóa đơn gốc
            for (ChiTietHoaDon ct : dsChiTietGoc) {
                if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                    soLuongGoc = ct.getSoLuongBan();
                    break;
                }
            }

            int currentVal = 0;
            try {
                currentVal = Integer.parseInt(value.toString());
            } catch (Exception ex) {
            }

            int min = (column == 3) ? 1 : 0;
            spinner.setModel(new SpinnerNumberModel(currentVal, min, soLuongGoc, 1));
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            try {
                spinner.commitEdit();
            } catch (Exception ignored) {
            }
            return spinner.getValue();
        }
    }
}