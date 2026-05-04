package com.example.gui.screens;

import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.example.dao.*;
import com.example.entity.*;
import com.example.entity.enums.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.text.DecimalFormat;

public class BanHangPanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255); // #F1F6FF
    private final Color COLOR_CARD_BG = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(0, 200, 83); // Green
    private final Color COLOR_SECONDARY = new Color(0, 123, 255); // Blue
    private final Color COLOR_BORDER = new Color(230, 230, 230);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 14);

    private SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private DonViQuyDoiDAO donViQuyDoiDAO = new DonViQuyDoiDAO();
    private KhachHangDAO khachHangDAO = new KhachHangDAO();
    private KhuyenMaiDAO khuyenMaiDAO = new KhuyenMaiDAO();
    private DonThuocDAO donThuocDAO = new DonThuocDAO();
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO chiTietDAO = new ChiTietHoaDonDAO();
    private DefaultTableModel model;
    private JTable table;
    private JPopupMenu searchPopup;

    private JLabel lblTongTienHoaDon;
    private JLabel lblKhuyenMaiLabel;
    private JLabel lblThue;
    private JLabel lblThanhTien;

    private RoundedTextField txtSoDienThoai;
    private JCheckBox chkKhachLe;
    private RoundedTextField txtTenKhachHang;
    private JLabel lblMaHoaDon;
    private JComboBox<String> cboKhuyenMai;
    private JComboBox<String> cboDonThuoc;
    private RoundedTextField txtTienKhachDua;
    private RoundedTextField txtTienThoiLai;
    private JRadioButton rdoTienMat;
    private JRadioButton rdoChuyenKhoan;
    private JTextArea areaNotes;
    private DatePicker datePicker;

    private KhachHang khachHangHienTai = null;
    private List<KhuyenMai> dsKhuyenMai = new java.util.ArrayList<>();
    private List<DonThuoc> dsDonThuoc = new java.util.ArrayList<>();
    private String maHoaDonHienTai = "";
    private NhanVien nhanVienHienTai;

    public BanHangPanel(TaiKhoan taiKhoan) {
        this.nhanVienHienTai = taiKhoan.getNhanVien();
        setLayout(new BorderLayout(20, 0));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(createLeftPanel(), BorderLayout.CENTER);
        add(createRightSidebar(), BorderLayout.EAST);

        SwingUtilities.invokeLater(this::loadHoaDonChuaThanhToan);
    }

    public void loadHoaDonChuaThanhToan() {
        HoaDon hd = hoaDonDAO.layHoaDonChuaThanhToan(nhanVienHienTai.getMaNhanVien());
        if (hd == null) {
            maHoaDonHienTai = sinhMaHoaDon();
            if (lblMaHoaDon != null)
                lblMaHoaDon.setText(maHoaDonHienTai);
            return;
        }

        maHoaDonHienTai = hd.getMaHoaDon();
        if (lblMaHoaDon != null)
            lblMaHoaDon.setText(maHoaDonHienTai);

        if (hd.getKhachHang() != null && !"KH_LE".equals(hd.getKhachHang().getMaKhachHang())) {
            khachHangHienTai = hd.getKhachHang();
            if (txtSoDienThoai != null)
                txtSoDienThoai.setText(khachHangHienTai.getSdt());
            if (txtTenKhachHang != null)
                txtTenKhachHang.setText(khachHangHienTai.getTenKhachHang());
        }

        if (hd.getKhuyenMai() != null && cboKhuyenMai != null) {
            for (int i = 0; i < dsKhuyenMai.size(); i++) {
                if (dsKhuyenMai.get(i).getMaKhuyenMai().equals(hd.getKhuyenMai().getMaKhuyenMai())) {
                    cboKhuyenMai.setSelectedIndex(i + 1);
                    break;
                }
            }
        }

        if (areaNotes != null)
            areaNotes.setText(hd.getGhiChu() != null ? hd.getGhiChu() : "");
        if (hd.getPhuongThucThanhToan() != null && rdoChuyenKhoan != null) {
            if (hd.getPhuongThucThanhToan() == PhuongThucThanhToan.CHUYEN_KHOAN)
                rdoChuyenKhoan.setSelected(true);
            else
                rdoTienMat.setSelected(true);
        }

        if (model != null && hd.getDsChiTiet() != null) {
            model.setRowCount(0);
            DecimalFormat df = new DecimalFormat("#,###");
            for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                DonViQuyDoi dv = ct.getDonViQuyDoi();
                String tenDonVi = dv.getTenDonVi() != null ? dv.getTenDonVi().getMoTa() : dv.getMaDonVi();
                double thue = dv.getSanPham() != null ? dv.getSanPham().getThue() : 0;
                double thanhTien = ct.getSoLuong() * ct.getDonGia() * (1 + thue / 100);
                model.addRow(new Object[] {
                        dv.getSanPham() != null ? dv.getSanPham().getMaSanPham() : "",
                        dv.getSanPham() != null ? dv.getSanPham().getTenSanPham() : "",
                        tenDonVi,
                        ct.getSoLuong(),
                        ct.getDonGia(),
                        thue + "%",
                        thanhTien,
                        dv,
                        ct.isLaQuaTangKem()
                });
            }
        }

        if (hd.getDonThuoc() != null && cboDonThuoc != null) {
            for (int i = 0; i < dsDonThuoc.size(); i++) {
                if (dsDonThuoc.get(i).getMaDonThuoc().equals(hd.getDonThuoc().getMaDonThuoc())) {
                    cboDonThuoc.setSelectedIndex(i + 1);
                    break;
                }
            }
        }
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Danh sách sản phẩm");
        lblTitle.setFont(FONT_TITLE);

        RoundedTextField txtSearch = new RoundedTextField("Mã/Tên sản phẩm", 15);
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.setFont(FONT_TEXT);

        searchPopup = new JPopupMenu();
        searchPopup.setFocusable(false);

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSearch();
            }

            private void updateSearch() {
                String text = txtSearch.getText().trim();
                if (text.isEmpty() || text.equals("Mã/Tên sản phẩm")) {
                    searchPopup.setVisible(false);
                    return;
                }

                SwingUtilities.invokeLater(() -> {
                    List<SanPham> results = sanPhamDAO.timTheoMaHoacTen(text);
                    searchPopup.removeAll();
                    if (results.isEmpty()) {
                        searchPopup.setVisible(false);
                        return;
                    }
                    for (SanPham sp : results) {
                        JMenu item = new JMenu(sp.getMaSanPham() + " - " + sp.getTenSanPham());
                        item.setFont(FONT_TEXT);

                        List<DonViQuyDoi> donVis = donViQuyDoiDAO.timTheoMaSanPham(sp.getMaSanPham());
                        if (donVis.isEmpty()) {
                            JMenuItem defaultUnit = new JMenuItem("Chưa có đơn vị quy đổi, vui lòng thêm");
                            defaultUnit.setFont(FONT_TEXT);

                            item.add(defaultUnit);
                        } else {
                            for (DonViQuyDoi dv : donVis) {
                                JMenuItem unitItem = new JMenuItem(dv.getTenDonVi().getMoTa());
                                unitItem.setFont(FONT_TEXT);
                                unitItem.addActionListener(ev -> {
                                    txtSearch.setText("");
                                    searchPopup.setVisible(false);
                                    addProductToTable(sp, dv);
                                });
                                item.add(unitItem);
                            }
                        }

                        searchPopup.add(item);
                    }
                    if (txtSearch.isShowing()) {
                        searchPopup.show(txtSearch, 0, txtSearch.getHeight());
                        txtSearch.requestFocus();
                    }
                });
            }
        });

        topPanel.add(lblTitle, BorderLayout.WEST);

        // Panel bên phải: [Xóa dòng] [Xóa hết] [Search]
        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightTopPanel.setOpaque(false);

        RoundedButton btnXoaDong = new RoundedButton("Xóa dòng");
        btnXoaDong.setBackground(new Color(108, 117, 125));
        btnXoaDong.setForeground(Color.WHITE);
        btnXoaDong.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoaDong.setPreferredSize(new Dimension(110, 35));
        btnXoaDong.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                model.removeRow(selectedRow);
            } else {
                JOptionPane.showMessageDialog(BanHangPanel.this,
                        "Vui lòng chọn một dòng để xóa!", "Chưa chọn dòng",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        RoundedButton btnXoaHet = new RoundedButton("Xóa hết");
        btnXoaHet.setBackground(new Color(220, 53, 69));
        btnXoaHet.setForeground(Color.WHITE);
        btnXoaHet.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnXoaHet.setPreferredSize(new Dimension(105, 35));
        btnXoaHet.addActionListener(e -> {
            if (model.getRowCount() == 0)
                return;
            int confirm = JOptionPane.showConfirmDialog(BanHangPanel.this,
                    "Bạn có chắc muốn xóa toàn bộ sản phẩm?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                model.setRowCount(0);
            }
        });

        rightTopPanel.add(btnXoaDong);
        rightTopPanel.add(btnXoaHet);
        rightTopPanel.add(txtSearch);
        topPanel.add(rightTopPanel, BorderLayout.EAST);

        RoundedPanel tableContainer = new RoundedPanel(12, true);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBackground(COLOR_CARD_BG);

        String[] columns = { "Mã sản phẩm", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền",
                "OBJ", "IS_GIFT" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                Boolean isGift = (Boolean) getValueAt(row, 8);
                if (isGift != null && isGift)
                    return false; // Không cho sửa quà tặng
                return column == 2 || column == 3; // Unit and Quantity are editable
            }
        };
        model.addTableModelListener(new TableModelListener() {
            private boolean isUpdating = false;

            @Override
            public void tableChanged(TableModelEvent e) {
                if (isUpdating)
                    return;

                int row = e.getFirstRow();
                int col = e.getColumn();

                if (row >= 0 && (col == 2 || col == 3)) {
                    isUpdating = true;
                    try {
                        if (col == 2) {
                            String newUnitStr = model.getValueAt(row, 2).toString();
                            String maSP = model.getValueAt(row, 0).toString();
                            List<DonViQuyDoi> donVis = donViQuyDoiDAO.timTheoMaSanPham(maSP);
                            DonViQuyDoi newDv = null;
                            for (DonViQuyDoi dv : donVis) {
                                if (dv.getTenDonVi().getMoTa().equals(newUnitStr)) {
                                    newDv = dv;
                                    break;
                                }
                            }
                            if (newDv != null) {
                                SanPham sp = newDv.getSanPham();
                                double donGia = sp.getDonGiaCoBan() * newDv.getHeSoQuyDoi();
                                model.setValueAt(donGia, row, 4);
                                model.setValueAt(newDv, row, 7);
                            }
                        }
                        updateRowTotal(row);
                    } finally {
                        isUpdating = false;
                    }
                }

                if (lblTongTienHoaDon != null) {
                    updateSummary();
                    SwingUtilities.invokeLater(() -> luuHoaDon(false));
                }
            }
        });
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_TEXT);
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setFont(FONT_LABEL);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<String>()) {
            private JComboBox<String> comboBox = (JComboBox<String>) getComponent();

            @Override
            public Component getTableCellEditorComponent(JTable t, Object value, boolean isSelected, int row,
                    int column) {
                String maSanPham = t.getValueAt(row, 0).toString();
                List<DonViQuyDoi> donVis = donViQuyDoiDAO.timTheoMaSanPham(maSanPham);
                comboBox.removeAllItems();
                for (DonViQuyDoi dv : donVis) {
                    comboBox.addItem(dv.getTenDonVi().getMoTa());
                }
                comboBox.setSelectedItem(value);
                return comboBox;
            }
        });

        class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
            private JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));

            public SpinnerEditor() {
                spinner.setBorder(null);
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                    int column) {
                if (value != null) {
                    try {
                        spinner.setValue(Integer.parseInt(value.toString()));
                    } catch (Exception e) {
                    }
                }
                return spinner;
            }

            @Override
            public Object getCellEditorValue() {
                return spinner.getValue();
            }
        }

        class SpinnerRenderer implements TableCellRenderer {
            private JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));

            public SpinnerRenderer() {
                spinner.setBorder(null);
                spinner.setOpaque(true);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                if (value != null) {
                    try {
                        spinner.setValue(Integer.parseInt(value.toString()));
                    } catch (Exception e) {
                    }
                }
                if (isSelected) {
                    spinner.setBackground(table.getSelectionBackground());
                } else {
                    spinner.setBackground(table.getBackground());
                }

                Boolean isGift = (Boolean) table.getModel().getValueAt(row, 8);
                if (isGift != null && isGift) {
                    spinner.setForeground(COLOR_PRIMARY);
                    spinner.getEditor().getComponent(0).setFont(spinner.getFont().deriveFont(Font.ITALIC));
                    ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setForeground(COLOR_PRIMARY);
                } else {
                    spinner.setForeground(Color.BLACK);
                    spinner.getEditor().getComponent(0).setFont(spinner.getFont().deriveFont(Font.PLAIN));
                    ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setForeground(Color.BLACK);
                }

                return spinner;
            }
        }

        table.getColumnModel().getColumn(3).setCellEditor(new SpinnerEditor());
        table.getColumnModel().getColumn(3).setCellRenderer(new SpinnerRenderer());

        table.getColumnModel().getColumn(7).setMinWidth(0);
        table.getColumnModel().getColumn(7).setMaxWidth(0);
        table.getColumnModel().getColumn(7).setPreferredWidth(0);

        table.getColumnModel().getColumn(8).setMinWidth(0);
        table.getColumnModel().getColumn(8).setMaxWidth(0);
        table.getColumnModel().getColumn(8).setPreferredWidth(0);

        // Custom Renderer cho màu sắc dòng quà tặng
        TableCellRenderer giftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                Boolean isGift = (Boolean) table.getModel().getValueAt(row, 8);
                if (isGift != null && isGift) {
                    c.setForeground(COLOR_PRIMARY); // Màu xanh lá
                    c.setFont(c.getFont().deriveFont(Font.ITALIC));
                } else {
                    if (!isSelected) {
                        c.setForeground(Color.BLACK);
                    }
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }

                if (isSelected) {
                    c.setBackground(table.getSelectionBackground());
                } else {
                    c.setBackground(table.getBackground());
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 3) { // Không áp dụng cho cột Spinner (số lượng) vì nó có renderer riêng
                table.getColumnModel().getColumn(i).setCellRenderer(giftRenderer);
            }
        }

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tableContainer, BorderLayout.CENTER);
        panel.add(createSummaryBar(), BorderLayout.SOUTH);

        return panel;
    }

    private void addProductToTable(SanPham sp, DonViQuyDoi dv) {
        String donViStr = dv.getTenDonVi().getMoTa();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 0).equals(sp.getMaSanPham()) && model.getValueAt(i, 2).equals(donViStr)) {
                int qty = Integer.parseInt(model.getValueAt(i, 3).toString());
                model.setValueAt(qty + 1, i, 3);
                updateRowTotal(i);
                return;
            }
        }

        double donGia = sp.getDonGiaCoBan() * dv.getHeSoQuyDoi();
        double thue = sp.getThue();
        double thanhTien = donGia + (donGia * thue / 100);

        Object[] row = {
                sp.getMaSanPham(),
                sp.getTenSanPham(),
                donViStr,
                1,
                donGia,
                thue + "%",
                thanhTien,
                dv,
                false // NOT a gift
        };
        model.addRow(row);

        if (sp.getPhanLoai() != null && sp.getPhanLoai() == PhanLoai.ETC) {
            if (cboDonThuoc != null && cboDonThuoc.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this,
                        "Sản phẩm [" + sp.getTenSanPham()
                                + "] là thuốc kê đơn (ETC).\nVui lòng chọn Đơn thuốc cho hóa đơn này!",
                        "Yêu cầu Đơn thuốc", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void updateRowTotal(int row) {
        int qty = Integer.parseInt(model.getValueAt(row, 3).toString());
        double price = Double.parseDouble(model.getValueAt(row, 4).toString());
        double tax = Double.parseDouble(model.getValueAt(row, 5).toString().replace("%", ""));
        double total = qty * price * (1 + tax / 100);
        model.setValueAt(total, row, 6);
    }

    private void updateSummary() {
        double tongTien = 0;
        double thue = 0;

        boolean hasETC = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            int qty = Integer.parseInt(model.getValueAt(i, 3).toString());
            double price = Double.parseDouble(model.getValueAt(i, 4).toString());
            double taxRate = Double.parseDouble(model.getValueAt(i, 5).toString().replace("%", ""));

            double tienSanPham = qty * price;
            double tienThue = tienSanPham * taxRate / 100;

            tongTien += tienSanPham;
            thue += tienThue;

            DonViQuyDoi dv = (DonViQuyDoi) model.getValueAt(i, 7);
            if (dv != null && dv.getSanPham() != null && dv.getSanPham().getPhanLoai() == PhanLoai.ETC) {
                hasETC = true;
            }
        }

        if (cboDonThuoc != null) {
            cboDonThuoc.setEnabled(hasETC);
            if (!hasETC && cboDonThuoc.getSelectedIndex() != 0) {
                cboDonThuoc.setSelectedIndex(0);
            }
        }

        double soTienGiam = 0;
        int idx = (cboKhuyenMai != null) ? cboKhuyenMai.getSelectedIndex() - 1 : -1;
        if (idx >= 0 && idx < dsKhuyenMai.size()) {
            KhuyenMai km = dsKhuyenMai.get(idx);

            // Kiểm tra giá trị đơn hàng tối thiểu
            if (tongTien < km.getGiaTriDonHangToiThieu()) {
                final double minVal = km.getGiaTriDonHangToiThieu();
                SwingUtilities.invokeLater(() -> {
                    if (cboKhuyenMai.getSelectedIndex() != 0) {
                        cboKhuyenMai.setSelectedIndex(0);
                        JOptionPane.showMessageDialog(this, "Đơn hàng chưa đạt giá trị tối thiểu (" +
                                new DecimalFormat("#,### đ").format(minVal) + ") để áp dụng khuyến mãi!");
                    }
                });
            } else {
                if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.PHAN_TRAM) {
                    soTienGiam = tongTien * km.getKhuyenMaiPhanTram() / 100.0;
                }
            }
        }
        double thanhTien = tongTien + thue - soTienGiam;
        DecimalFormat df = new DecimalFormat("#,### đ");
        lblTongTienHoaDon.setText("Tổng tiền hóa đơn : " + df.format(tongTien));
        lblKhuyenMaiLabel.setText("Khuyến mãi : -" + df.format(soTienGiam));
        lblKhuyenMaiLabel.setForeground(COLOR_PRIMARY); // Màu xanh lá
        lblThue.setText("Thuế : " + df.format(thue));
        lblThanhTien.setText("Thành tiền : " + df.format(thanhTien));
        tinhTienThoi();
    }

    private JPanel createSummaryBar() {
        RoundedPanel panel = new RoundedPanel(12, true);
        panel.setLayout(new GridLayout(4, 1, 5, 2));
        panel.setBackground(COLOR_CARD_BG);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        lblTongTienHoaDon = createSummaryLabel("Tổng tiền hóa đơn :", "0 đ");
        lblKhuyenMaiLabel = createSummaryLabel("Khuyến mãi :", "-0 đ");
        lblThue = createSummaryLabel("Thuế :", "0 đ");
        lblThanhTien = new JLabel("Thành tiền : 0 đ");
        lblThanhTien.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblTongTienHoaDon);
        panel.add(lblKhuyenMaiLabel);
        panel.add(lblThue);
        panel.add(lblThanhTien);
        return panel;
    }

    private JLabel createSummaryLabel(String text, String value) {
        JLabel label = new JLabel(text + " " + value);
        label.setFont(FONT_LABEL);
        return label;
    }

    private JPanel createRightSidebar() {
        RoundedPanel wrapper = new RoundedPanel(12, true);
        wrapper.setLayout(new BorderLayout());
        wrapper.setPreferredSize(new Dimension(340, 0));
        wrapper.setBackground(COLOR_CARD_BG);
        wrapper.setBorder(new EmptyBorder(10, 5, 10, 5));

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setOpaque(false);
        sidebar.setBorder(new EmptyBorder(0, 5, 0, 5));

        // --- Thông tin khách hàng ---
        txtSoDienThoai = new RoundedTextField(10);
        sidebar.add(createSectionTitle("Thông tin khách hàng"));
        sidebar.add(createFieldGroup("Số điện thoại", txtSoDienThoai));

        txtSoDienThoai.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                timKhachHang();
            }

            public void removeUpdate(DocumentEvent e) {
                timKhachHang();
            }

            public void changedUpdate(DocumentEvent e) {
                timKhachHang();
            }
        });

        chkKhachLe = new JCheckBox("Khách lẻ");
        chkKhachLe.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkKhachLe.setOpaque(false);
        chkKhachLe.addActionListener(e -> {
            if (chkKhachLe.isSelected()) {
                khachHangHienTai = null;
                txtTenKhachHang.setText("Khách lẻ");
                txtSoDienThoai.setEditable(false);
            } else {
                txtTenKhachHang.setText("");
                txtSoDienThoai.setEditable(true);
            }
        });

        JPanel chkWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        chkWrapper.setOpaque(false);
        chkWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkWrapper.add(chkKhachLe);
        chkWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, chkKhachLe.getPreferredSize().height));
        sidebar.add(chkWrapper);

        txtTenKhachHang = new RoundedTextField(10);
        txtTenKhachHang.setEditable(false);
        txtTenKhachHang.setBackground(new Color(235, 235, 235));
        sidebar.add(createFieldGroup("Tên khách hàng", txtTenKhachHang));

        // --- Thông tin hóa đơn ---
        sidebar.add(createSectionTitle("Thông tin hóa đơn"));
        maHoaDonHienTai = sinhMaHoaDon();
        lblMaHoaDon = new JLabel(maHoaDonHienTai);
        lblMaHoaDon.setFont(FONT_TEXT);
        lblMaHoaDon.setHorizontalAlignment(SwingConstants.CENTER);
        RoundedPanel maHDPanel = new RoundedPanel(8, false);
        maHDPanel.setLayout(new BorderLayout());
        maHDPanel.setBackground(new Color(235, 235, 235));
        maHDPanel.setBorder(new EmptyBorder(4, 8, 4, 8));
        maHDPanel.add(lblMaHoaDon);
        sidebar.add(createFieldGroup("Mã hóa đơn", maHDPanel));

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setFontValidDate(FONT_TEXT);
        datePicker = new DatePicker(dateSettings);
        datePicker.setDate(LocalDate.now());
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        dateFinderStyle(datePicker);
        datePicker.setEnabled(false);
        sidebar.add(createFieldGroup("Ngày tạo", datePicker));
        sidebar.add(createFieldGroup("Người tạo", createReadOnlyField(nhanVienHienTai.getTenNhanVien())));

        // Khuyến mãi
        cboKhuyenMai = new JComboBox<>();
        cboKhuyenMai.addItem("-- Không áp dụng --");
        dsKhuyenMai = khuyenMaiDAO.layTatCa();
        for (KhuyenMai km : dsKhuyenMai) {
            cboKhuyenMai.addItem(km.getTenKhuyenMai());
        }
        cboKhuyenMai.addActionListener(e -> capNhatQuaTang());
        sidebar.add(createFieldGroup("Khuyến mãi", cboKhuyenMai));

        // Đơn thuốc
        cboDonThuoc = new JComboBox<>();
        cboDonThuoc.addItem("-- Không có --");
        cboDonThuoc.setEnabled(false);
        dsDonThuoc = donThuocDAO.layTatCa();
        for (DonThuoc dt : dsDonThuoc) {
            cboDonThuoc.addItem(dt.getMaDonThuoc() + " - BS. " + dt.getTenBacSi());
        }
        sidebar.add(createFieldGroup("Đơn thuốc", cboDonThuoc));

        // Ghi chú
        areaNotes = new JTextArea(2, 20);
        areaNotes.setLineWrap(true);
        areaNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(areaNotes);
        notesScroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        sidebar.add(createFieldGroup("Ghi chú", notesScroll));

        sidebar.add(createPaymentSection());

        wrapper.add(sidebar, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(8, 5, 0, 5));
        RoundedButton btnSave = createActionButton("Tạo / Lưu", COLOR_SECONDARY);
        RoundedButton btnPay = createActionButton("Thanh Toán", COLOR_PRIMARY);
        btnSave.addActionListener(e -> luuHoaDon(true));
        btnPay.addActionListener(e -> thanhToan(btnSave));
        buttonPanel.add(btnSave);
        buttonPanel.add(Box.createVerticalStrut(8));
        buttonPanel.add(btnPay);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);
        return wrapper;
    }

    private JPanel createPaymentSection() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(5, 5, 5, 5)));
        container.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        headerPanel.setOpaque(false);
        JLabel lblPTTT = new JLabel("Phương thức:");
        lblPTTT.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rdoTienMat = new JRadioButton("Tiền mặt", true);
        rdoTienMat.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rdoTienMat.setOpaque(false);
        rdoChuyenKhoan = new JRadioButton("Chuyển khoản");
        rdoChuyenKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rdoChuyenKhoan.setOpaque(false);
        ButtonGroup bg = new ButtonGroup();
        bg.add(rdoTienMat);
        bg.add(rdoChuyenKhoan);
        headerPanel.add(lblPTTT);
        headerPanel.add(rdoTienMat);
        headerPanel.add(rdoChuyenKhoan);

        JPanel cards = new JPanel(new CardLayout());
        cards.setOpaque(false);

        txtTienKhachDua = new RoundedTextField("0", 10);
        txtTienThoiLai = new RoundedTextField("0", 10);
        txtTienThoiLai.setEditable(false);
        txtTienThoiLai.setBackground(new Color(235, 235, 235));
        txtTienKhachDua.getDocument().addDocumentListener(new DocumentListener() {
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
        JPanel pnlTienMat = new JPanel();
        pnlTienMat.setLayout(new BoxLayout(pnlTienMat, BoxLayout.Y_AXIS));
        pnlTienMat.setOpaque(false);
        pnlTienMat.add(createHorizontalGroup("Tiền khách đưa:", txtTienKhachDua));
        pnlTienMat.add(createHorizontalGroup("Tiền thối lại:", txtTienThoiLai));

        JPanel pnlChuyenKhoan = new JPanel(new BorderLayout());
        pnlChuyenKhoan.setOpaque(false);
        JLabel lblQR = new JLabel("Mã QR", SwingConstants.CENTER);
        lblQR.setPreferredSize(new Dimension(80, 80));
        lblQR.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 2, true));
        lblQR.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQR.setForeground(COLOR_PRIMARY);
        JPanel qrContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        qrContainer.setOpaque(false);
        qrContainer.add(lblQR);
        pnlChuyenKhoan.add(qrContainer, BorderLayout.CENTER);

        cards.add(pnlTienMat, "TIEN_MAT");
        cards.add(pnlChuyenKhoan, "CHUYEN_KHOAN");
        container.add(headerPanel);
        container.add(cards);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, container.getPreferredSize().height));
        CardLayout cl = (CardLayout) cards.getLayout();
        rdoTienMat.addActionListener(e -> {
            cl.show(cards, "TIEN_MAT");
            cards.revalidate();
            cards.repaint();
        });
        rdoChuyenKhoan.addActionListener(e -> {
            cl.show(cards, "CHUYEN_KHOAN");
            cards.revalidate();
            cards.repaint();
        });

        return container;
    }

    // ---- Helper methods ----

    private void timKhachHang() {
        String sdt = txtSoDienThoai.getText().trim();
        if (sdt.length() >= 10) {
            KhachHang kh = khachHangDAO.timTheoSdt(sdt);
            if (kh != null) {
                khachHangHienTai = kh;
                txtTenKhachHang.setText(kh.getTenKhachHang());
            } else {
                khachHangHienTai = null;
                txtTenKhachHang.setText("Không tìm thấy");
            }
        } else {
            txtTenKhachHang.setText("");
            khachHangHienTai = null;
        }
    }

    private void tinhTienThoi() {
        if (txtTienKhachDua == null || txtTienThoiLai == null || lblThanhTien == null)
            return;
        try {
            String ttStr = lblThanhTien.getText().replaceAll("[^\\d]", "");
            double thanhTien = ttStr.isEmpty() ? 0 : Double.parseDouble(ttStr);
            String kd = txtTienKhachDua.getText().replaceAll("[^\\d]", "");
            double khachDua = kd.isEmpty() ? 0 : Double.parseDouble(kd);
            double thoi = khachDua - thanhTien;
            DecimalFormat df = new DecimalFormat("#,###");
            txtTienThoiLai.setText(df.format(Math.max(0, thoi)));
        } catch (NumberFormatException ex) {
            txtTienThoiLai.setText("0");
        }
    }

    private String sinhMaHoaDon() {
        int stt = hoaDonDAO.demHoaDonTrongNgay(LoaiHoaDon.BAN_HANG) + 1;
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyy"));
        return String.format("HDB%s%03d", date, stt);
    }

    /** Thu thập chi tiết hóa đơn từ bảng */
    private java.util.List<ChiTietHoaDon> thuThapChiTiet(HoaDon hd) {
        java.util.List<ChiTietHoaDon> dsChiTiet = new java.util.ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.setHoaDon(hd);
            ct.setDonViQuyDoi((DonViQuyDoi) model.getValueAt(i, 7));
            ct.setSoLuong(Integer.parseInt(model.getValueAt(i, 3).toString()));
            ct.setDonGia(Double.parseDouble(model.getValueAt(i, 4).toString()));
            ct.setLaQuaTangKem((Boolean) model.getValueAt(i, 8));
            dsChiTiet.add(ct);
        }
        return dsChiTiet;
    }

    private void capNhatQuaTang() {
        // 1. Tính toán tổng tiền hàng (không tính quà tặng hiện có)
        double tongTienHang = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean isGift = (Boolean) model.getValueAt(i, 8);
            if (isGift == null || !isGift) {
                int qty = Integer.parseInt(model.getValueAt(i, 3).toString());
                double price = Double.parseDouble(model.getValueAt(i, 4).toString());
                tongTienHang += qty * price;
            }
        }

        // 2. Kiểm tra điều kiện áp dụng trước khi thêm quà mới
        int idx = (cboKhuyenMai != null) ? cboKhuyenMai.getSelectedIndex() - 1 : -1;
        if (idx >= 0 && idx < dsKhuyenMai.size()) {
            KhuyenMai km = dsKhuyenMai.get(idx);
            if (tongTienHang < km.getGiaTriDonHangToiThieu()) {
                JOptionPane.showMessageDialog(this, "Đơn hàng chưa đạt giá trị tối thiểu (" +
                        new DecimalFormat("#,### đ").format(km.getGiaTriDonHangToiThieu())
                        + ") để áp dụng khuyến mãi này!");
                cboKhuyenMai.setSelectedIndex(0);
                return;
            }
        }

        // 3. Xóa tất cả quà tặng hiện có trong bảng
        for (int i = model.getRowCount() - 1; i >= 0; i--) {
            Boolean isGift = (Boolean) model.getValueAt(i, 8);
            if (isGift != null && isGift) {
                model.removeRow(i);
            }
        }

        // 4. Nếu khuyến mãi hiện tại là TẶNG_KÈM, thêm quà vào
        idx = (cboKhuyenMai != null) ? cboKhuyenMai.getSelectedIndex() - 1 : -1;
        if (idx >= 0 && idx < dsKhuyenMai.size()) {
            KhuyenMai km = dsKhuyenMai.get(idx);
            if (km.getLoaiKhuyenMai() == LoaiKhuyenMai.TANG_KEM && km.getQuaTangKem() != null) {
                QuaTang qt = km.getQuaTangKem();
                SanPham sp = sanPhamDAO.timTheoMa(qt.getSanPham().getMaSanPham());

                // Lấy đơn vị quy đổi có hệ số 1 (đơn vị cơ bản)
                List<DonViQuyDoi> dsDV = donViQuyDoiDAO.timTheoMaSanPham(sp.getMaSanPham());
                DonViQuyDoi dvCoBan = dsDV.stream()
                        .filter(dv -> dv.getHeSoQuyDoi() == 1)
                        .findFirst()
                        .orElse(dsDV.isEmpty() ? null : dsDV.get(0));

                if (dvCoBan != null) {
                    Object[] row = {
                            sp.getMaSanPham(),
                            sp.getTenSanPham() + " (Quà tặng)",
                            dvCoBan.getTenDonVi().getMoTa(),
                            qt.getSoLuongTang(),
                            0.0, // Giá bằng 0
                            "0%", // Thuế 0% cho quà tặng
                            0.0, // Thành tiền 0
                            dvCoBan,
                            true // IS_GIFT = true
                    };
                    model.addRow(row);
                }
            }
        }
        updateSummary();
    }

    /** Xây dựng đối tượng HoaDon từ UI, trả null nếu lỗi */
    private HoaDon xayDungHoaDon(boolean hienThongBao) {
        if (model.getRowCount() == 0) {
            if (hienThongBao) {
                JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào hóa đơn!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            return null;
        }

        // Kiểm tra ETC
        boolean hasETC = false;
        for (int i = 0; i < model.getRowCount(); i++) {
            DonViQuyDoi dv = (DonViQuyDoi) model.getValueAt(i, 7);
            if (dv != null && dv.getSanPham() != null && dv.getSanPham().getPhanLoai() == PhanLoai.ETC) {
                hasETC = true;
                break;
            }
        }

        if (hasETC && cboDonThuoc != null && cboDonThuoc.getSelectedIndex() <= 0) {
            if (hienThongBao) {
                JOptionPane.showMessageDialog(this,
                        "Hóa đơn chứa thuốc kê đơn (ETC). Vui lòng chọn Đơn thuốc trước khi tiếp tục!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
            return null;
        }

        CaLamDAO caLamDAO = new CaLamDAO();
        CaLam caHienTai = caLamDAO.layCaHienTai(nhanVienHienTai.getMaNhanVien());
        if (caHienTai == null) {
            JOptionPane.showMessageDialog(this, "Bạn chưa mở ca làm việc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        HoaDon hd = new HoaDon();
        hd.setMaHoaDon(maHoaDonHienTai);
        hd.setThoiGianTao(LocalDateTime.now());
        hd.setLoaiHoaDon(LoaiHoaDon.BAN_HANG);
        hd.setTrangThaiThanhToan(false);
        hd.setGhiChu(areaNotes.getText());
        hd.setNhanVien(nhanVienHienTai);
        hd.setCa(caHienTai);
        hd.setPhuongThucThanhToan(rdoTienMat.isSelected()
                ? PhuongThucThanhToan.TIEN_MAT
                : PhuongThucThanhToan.CHUYEN_KHOAN);

        int idx = cboKhuyenMai.getSelectedIndex() - 1;
        if (idx >= 0 && idx < dsKhuyenMai.size()) {
            hd.setKhuyenMai(dsKhuyenMai.get(idx));
        }

        int idxDT = (cboDonThuoc != null) ? cboDonThuoc.getSelectedIndex() - 1 : -1;
        if (idxDT >= 0 && idxDT < dsDonThuoc.size()) {
            hd.setDonThuoc(dsDonThuoc.get(idxDT));
        }

        if (khachHangHienTai != null) {
            hd.setKhachHang(khachHangHienTai);
        } else {
            KhachHang kl = new KhachHang();
            kl.setMaKhachHang("KH_LE");
            hd.setKhachHang(kl);
        }
        return hd;
    }

    /** Tạo / Lưu hóa đơn (không trừ kho) */
    private void luuHoaDon(boolean hienThongBao) {
        // Nếu đang cập nhật UI hoặc bảng trống thì không lưu tự động
        if (model.getRowCount() == 0)
            return;

        HoaDon hd = xayDungHoaDon(hienThongBao);
        if (hd == null)
            return;
        List<ChiTietHoaDon> dsChiTiet = thuThapChiTiet(hd);

        if (hoaDonDAO.luuHoaDon(hd, dsChiTiet)) {
            if (hienThongBao) {
                JOptionPane.showMessageDialog(this, "Lưu hóa đơn thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            if (hienThongBao) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Thanh toán: tạo hóa đơn trước, sau đó xác nhận thanh toán + trừ kho */
    private void thanhToan(RoundedButton btnSave) {
        // Lưu hóa đơn trước khi thanh toán (chế độ im lặng)
        luuHoaDon(false);

        // Kiểm tra hóa đơn đã được lưu chưa
        HoaDon hd = xayDungHoaDon(true);
        if (hd == null)
            return;
        java.util.List<ChiTietHoaDon> dsChiTiet = thuThapChiTiet(hd);

        if (hoaDonDAO.xacNhanThanhToan(maHoaDonHienTai, dsChiTiet)) {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            // Reset form
            model.setRowCount(0);
            maHoaDonHienTai = sinhMaHoaDon();
            lblMaHoaDon.setText(maHoaDonHienTai);
            txtSoDienThoai.setText("");
            txtTenKhachHang.setText("");
            cboKhuyenMai.setSelectedIndex(0);
            if (cboDonThuoc != null)
                cboDonThuoc.setSelectedIndex(0);
            areaNotes.setText("");
            khachHangHienTai = null;
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createHorizontalGroup(String labelText, JComponent component) {

        JPanel group = new JPanel(new BorderLayout(5, 0));
        group.setOpaque(false);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setPreferredSize(new Dimension(110, 28));

        if (component instanceof JTextField) {
            component.setPreferredSize(new Dimension(140, 28));
            ((JTextField) component).setHorizontalAlignment(JTextField.LEFT);
            ((JTextField) component).setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }

        JPanel pad = new JPanel(new BorderLayout());
        pad.setOpaque(false);
        pad.setBorder(new EmptyBorder(2, 5, 2, 5));
        pad.add(lbl, BorderLayout.WEST);
        pad.add(component, BorderLayout.CENTER);

        group.add(pad, BorderLayout.CENTER);
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, group.getPreferredSize().height));
        return group;
    }

    private JPanel createSectionTitle(String title) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_TITLE);
        wrapper.add(lbl, BorderLayout.WEST);
        wrapper.setBorder(new EmptyBorder(5, 0, 2, 0));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, wrapper.getPreferredSize().height));

        return wrapper;
    }

    private JPanel createFieldGroup(String labelText, JComponent component) {
        JPanel group = new JPanel(new BorderLayout(0, 2));
        group.setOpaque(false);
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FONT_LABEL);

        if (component instanceof JTextField || component instanceof JComboBox) {
            component.setPreferredSize(new Dimension(component.getPreferredSize().width, 28));
            if (component instanceof JTextField) {
                ((JTextField) component).setHorizontalAlignment(JTextField.CENTER);
            }
        }

        group.add(lbl, BorderLayout.NORTH);
        group.add(component, BorderLayout.CENTER);
        group.setBorder(new EmptyBorder(0, 0, 5, 0));
        group.setMaximumSize(new Dimension(Integer.MAX_VALUE, group.getPreferredSize().height));

        return group;
    }

    private RoundedTextField createReadOnlyField(String text) {
        RoundedTextField field = new RoundedTextField(text, 10);
        field.setEditable(false);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setBackground(new Color(235, 235, 235));
        return field;
    }

    private void dateFinderStyle(DatePicker datePicker) {
        datePicker.setBackground(Color.WHITE);
        datePicker.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));
        datePicker.getComponentDateTextField().setHorizontalAlignment(JTextField.CENTER);
    }

    private RoundedButton createActionButton(String text, Color bg) {
        RoundedButton btn = new RoundedButton(text);
        btn.setFont(FONT_LABEL);
        btn.setBackground(bg);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

}
