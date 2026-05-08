package com.example.gui.screens;

import com.example.entity.TaiKhoan;
import com.example.entity.enums.ChucVu;
import com.example.dao.ChiTietHoaDonDAO;
import com.example.dao.HoaDonDAO;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.enums.LoaiHoaDon;
import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class HoaDonPanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255); // #F1F6FF
    private final Color COLOR_CARD_BG = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(0, 200, 83);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_TEXT_DIM = new Color(110, 110, 110);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_STATS = new Font("Segoe UI", Font.BOLD, 22);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 14);
    private final HoaDonDAO hoaDonDAO;
    private DefaultTableModel model;

    private JLabel lblHoaDonHomNay;
    private JLabel lblHoaDonBanHang;
    private JLabel lblHoaDonDoiHang;
    private JLabel lblHoaDonTraHang;

    private RoundedTextField txtSearch;
    private DatePicker datePicker;
    private JComboBox<String> cboLoaiHoaDon;
    private RoundedButton btnView, btnHuy;
    private JTable table;
    private TaiKhoan taiKhoan;

    private RoundedTextField txtMaHoaDon, txtNguoiTao, txtNgayTao, txtLoaiHoaDon;
    private RoundedTextField txtKhachHang, txtTongTien, txtKhuyenMai, txtTrangThai;

    public HoaDonPanel(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel centerPanel = new JPanel(new BorderLayout(0, 25));
        centerPanel.setOpaque(false);

        centerPanel.add(createStatsPanel(), BorderLayout.NORTH);

        JPanel mainContentPanel = new JPanel(new BorderLayout(0, 20));
        mainContentPanel.setOpaque(false);
        mainContentPanel.add(createFilterPanel(), BorderLayout.NORTH);
        mainContentPanel.add(createTablePanel(), BorderLayout.CENTER);

        centerPanel.add(mainContentPanel, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(createDetailPanel(), BorderLayout.SOUTH);

        hoaDonDAO = new HoaDonDAO();
        taiLaiDanhSach();
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);

        lblHoaDonHomNay = new JLabel("0");
        lblHoaDonBanHang = new JLabel("0");
        lblHoaDonDoiHang = new JLabel("0");
        lblHoaDonTraHang = new JLabel("0");

        panel.add(createStatCard("Hóa đơn hôm nay", lblHoaDonHomNay));
        panel.add(createStatCard("Hóa đơn bán hàng", lblHoaDonBanHang));
        panel.add(createStatCard("Hóa đơn đổi hàng", lblHoaDonDoiHang));
        panel.add(createStatCard("Hóa đơn trả hàng", lblHoaDonTraHang));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel lblValue) {
        RoundedPanel card = new RoundedPanel(20, true);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_TEXT);
        lblTitle.setForeground(COLOR_TEXT_DIM);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblValue.setFont(FONT_STATS);
        lblValue.setForeground(Color.BLACK);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblValue);

        return card;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        txtSearch = new RoundedTextField("Mã", 15);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setFont(FONT_TEXT);

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setFontValidDate(FONT_TEXT);

        datePicker = new DatePicker(dateSettings);
        datePicker.setPreferredSize(new Dimension(200, 35));
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        datePicker.setBackground(Color.WHITE);
        datePicker.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        // --- ComboBox lọc loại hóa đơn ---
        cboLoaiHoaDon = new JComboBox<>(new String[] {
                "Tất cả",
                LoaiHoaDon.BAN_HANG.getMoTa(),
                LoaiHoaDon.DOI_HANG.getMoTa(),
                LoaiHoaDon.TRA_HANG.getMoTa()
        });
        cboLoaiHoaDon.setFont(FONT_TEXT);
        cboLoaiHoaDon.setPreferredSize(new Dimension(160, 35));
        cboLoaiHoaDon.setBackground(Color.WHITE);

        btnView = new RoundedButton("Xem");
        btnView.setFont(FONT_TEXT);
        btnView.setForeground(Color.DARK_GRAY);
        btnView.setBackground(Color.WHITE);
        btnView.setPreferredSize(new Dimension(80, 35));

        btnHuy = new RoundedButton("Hủy hóa đơn");
        btnHuy.setFont(FONT_TEXT);
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setBackground(new Color(220, 53, 69)); // Red color
        btnHuy.setPreferredSize(new Dimension(140, 35));
        // Chỉ hiển thị nút hủy nếu là Quản lý
        btnHuy.setVisible(taiKhoan.getNhanVien().getChucVu() == ChucVu.NHAN_VIEN_QUAN_LY);

        leftPanel.add(txtSearch);
        leftPanel.add(datePicker);
        leftPanel.add(new JLabel("Loại:"));
        leftPanel.add(cboLoaiHoaDon);
        leftPanel.add(btnView);
        leftPanel.add(btnHuy);

        RoundedButton btnPayment = new RoundedButton("Thanh toán");
        btnPayment.setFont(FONT_LABEL);
        btnPayment.setBackground(COLOR_PRIMARY);
        btnPayment.setPreferredSize(new Dimension(120, 40));

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(btnPayment, BorderLayout.EAST);

        // Events
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                taiLaiDanhSach();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                taiLaiDanhSach();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                taiLaiDanhSach();
            }
        });

        datePicker.addDateChangeListener(event -> taiLaiDanhSach());
        cboLoaiHoaDon.addActionListener(e -> taiLaiDanhSach());
        btnView.addActionListener(e -> xemChiTietHoaDon());
        btnHuy.addActionListener(e -> huyHoaDon());
        btnPayment.addActionListener(e -> thanhToanHoaDon());

        return panel;
    }

    private JPanel createTablePanel() {
        RoundedPanel panel = new RoundedPanel(16, true);
        panel.setLayout(new BorderLayout());
        panel.setBackground(COLOR_CARD_BG);

        String[] columns = { "Mã hóa đơn", "Người tạo", "Ngày tạo", "Loại hóa đơn", "Khách hàng", "Tổng tiền",
                "Khuyến mãi", "Trạng thái" };
        model = new DefaultTableModel(columns, 15);
        table = new JTable(model);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);
        table.setFont(FONT_TEXT);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTiet();
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setBackground(new Color(240, 240, 240));
        header.setFont(FONT_LABEL);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 40, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        txtMaHoaDon = new RoundedTextField(1);
        txtNguoiTao = new RoundedTextField(1);
        txtNgayTao = new RoundedTextField(1);
        txtLoaiHoaDon = new RoundedTextField(1);
        txtKhachHang = new RoundedTextField(1);
        txtTongTien = new RoundedTextField(1);
        txtKhuyenMai = new RoundedTextField(1);
        txtTrangThai = new RoundedTextField(1);

        panel.add(createFieldGroup("Mã Hóa Đơn :", txtMaHoaDon));
        panel.add(createFieldGroup("Người Tạo :", txtNguoiTao));

        panel.add(createFieldGroup("Ngày tạo :", txtNgayTao));
        panel.add(createFieldGroup("Loại Hóa Đơn :", txtLoaiHoaDon));

        panel.add(createFieldGroup("Tên Khách Hàng :", txtKhachHang));
        panel.add(createFieldGroup("Tổng Tiền :", txtTongTien));

        panel.add(createFieldGroup("Khuyến mãi :", txtKhuyenMai));
        panel.add(createFieldGroup("Trạng thái thanh toán :", txtTrangThai));

        return panel;
    }

    private JPanel createFieldGroup(String label, RoundedTextField field) {
        JPanel group = new JPanel(new BorderLayout(15, 0));
        group.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setPreferredSize(new Dimension(150, 30));

        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));

        group.add(lbl, BorderLayout.WEST);
        group.add(field, BorderLayout.CENTER);

        return group;
    }

    public void taiLaiDanhSach() {
        if (model == null)
            return;
        model.setRowCount(0);

        String keyword = txtSearch != null ? txtSearch.getText().trim() : "";
        if (keyword.equals("Mã"))
            keyword = "";

        java.time.LocalDate date = datePicker != null ? datePicker.getDate() : null;

        // Lấy loại hóa đơn được chọn từ combobox
        String loaiChon = cboLoaiHoaDon != null ? (String) cboLoaiHoaDon.getSelectedItem() : "Tất cả";

        List<HoaDon> ds = hoaDonDAO.timKiem(keyword, date);

        int homNay = 0, banHang = 0, doiHang = 0, traHang = 0;
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        for (HoaDon h : ds) {
            // Cập nhật stats (luôn đếm toàn bộ, không phụ thuộc filter loại)
            if (h.getThoiGianTao() != null && h.getThoiGianTao().toLocalDate().isEqual(today))
                homNay++;
            if (h.getLoaiHoaDon() == LoaiHoaDon.BAN_HANG)
                banHang++;
            else if (h.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG)
                doiHang++;
            else if (h.getLoaiHoaDon() == LoaiHoaDon.TRA_HANG)
                traHang++;

            // Lọc theo loại hóa đơn được chọn
            if (!"Tất cả".equals(loaiChon)) {
                String moTaLoai = h.getLoaiHoaDon() != null ? h.getLoaiHoaDon().getMoTa() : "";
                if (!loaiChon.equals(moTaLoai))
                    continue;
            }

            String ngayTao = h.getThoiGianTao() != null ? h.getThoiGianTao().format(dtf) : "";
            String nguoiTao = h.getNhanVien() != null ? h.getNhanVien().getTenNhanVien() : "";
            String loaiHoaDon = h.getLoaiHoaDon() != null ? h.getLoaiHoaDon().getMoTa() : "";
            String khachHang = h.getKhachHang() != null ? h.getKhachHang().getTenKhachHang() : "";
            String tongTien = nf.format(h.tinhTongTienThanhToan());
            String khuyenMai = h.getKhuyenMai() != null ? h.getKhuyenMai().getTenKhuyenMai() : "Không có";
            String trangThai = h.isTrangThaiThanhToan() ? "Đã thanh toán" : "Chưa thanh toán";

            model.addRow(new Object[] {
                    h.getMaHoaDon(), nguoiTao, ngayTao, loaiHoaDon,
                    khachHang, tongTien, khuyenMai, trangThai
            });
        }

        if (lblHoaDonHomNay != null) {
            lblHoaDonHomNay.setText(String.valueOf(homNay));
            lblHoaDonBanHang.setText(String.valueOf(banHang));
            lblHoaDonDoiHang.setText(String.valueOf(doiHang));
            lblHoaDonTraHang.setText(String.valueOf(traHang));
        }
    }

    private void huyHoaDon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để hủy.");
            return;
        }

        String trangThai = model.getValueAt(row, 7).toString();
        if ("Đã thanh toán".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Không thể hủy hóa đơn đã thanh toán!", "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maHD = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn hủy hóa đơn " + maHD + "?\nLưu ý: Thao tác này sẽ xóa vĩnh viễn hóa đơn này.",
                "Xác nhận hủy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (hoaDonDAO.huyHoaDon(maHD)) {
                JOptionPane.showMessageDialog(this, "Đã hủy hóa đơn thành công.");
                taiLaiDanhSach();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: Không thể hủy hóa đơn này.");
            }
        }
    }

    private void hienThiChiTiet() {
        int row = table.getSelectedRow();
        if (row >= 0 && txtMaHoaDon != null) {
            txtMaHoaDon.setText(model.getValueAt(row, 0) != null ? model.getValueAt(row, 0).toString() : "");
            txtNguoiTao.setText(model.getValueAt(row, 1) != null ? model.getValueAt(row, 1).toString() : "");
            txtNgayTao.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
            txtLoaiHoaDon.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
            txtKhachHang.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
            txtTongTien.setText(model.getValueAt(row, 5) != null ? model.getValueAt(row, 5).toString() : "");
            txtKhuyenMai.setText(model.getValueAt(row, 6) != null ? model.getValueAt(row, 6).toString() : "");
            txtTrangThai.setText(model.getValueAt(row, 7) != null ? model.getValueAt(row, 7).toString() : "");
        }
    }

    private void xemChiTietHoaDon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để xem chi tiết.");
            return;
        }
        String maHD = model.getValueAt(row, 0).toString();

        HoaDon hd = hoaDonDAO.timTheoMa(maHD);
        if (hd == null)
            return;

        boolean laDoiTra = hd.getLoaiHoaDon() == LoaiHoaDon.DOI_HANG || hd.getLoaiHoaDon() == LoaiHoaDon.TRA_HANG;
        boolean coHoaDonGoc = hd.getHoaDonDoiTra() != null && hd.getHoaDonDoiTra().getMaHoaDon() != null;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết hóa đơn", true);

        if (laDoiTra && coHoaDonGoc) {
            dialog.setSize(1200, 600);
            dialog.setLocationRelativeTo(this);
            JPanel splitPanel = new JPanel(new GridLayout(1, 2, 10, 0));
            splitPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            splitPanel.setBackground(COLOR_BG);

            String maHDGoc = hd.getHoaDonDoiTra().getMaHoaDon();
            splitPanel.add(createInvoiceDetailPanel(maHDGoc, "Hóa đơn gốc"));
            splitPanel.add(createInvoiceDetailPanel(maHD, "Hóa đơn đổi/trả"));

            dialog.add(splitPanel, BorderLayout.CENTER);
        } else {
            dialog.setSize(650, 600);
            dialog.setLocationRelativeTo(this);
            JPanel container = new JPanel(new BorderLayout());
            container.setBorder(new EmptyBorder(10, 10, 10, 10));
            container.setBackground(COLOR_BG);
            container.add(createInvoiceDetailPanel(maHD, "Hóa đơn hiện tại"), BorderLayout.CENTER);
            dialog.add(container, BorderLayout.CENTER);
        }

        dialog.setVisible(true);
    }

    private JPanel createInvoiceDetailPanel(String maHD, String title) {
        HoaDon hd = hoaDonDAO.timTheoMa(maHD);
        if (hd == null)
            return new JPanel();

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_BORDER), title,
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, FONT_LABEL, COLOR_PRIMARY));
        panel.setBackground(Color.WHITE);

        // Header Info
        JPanel headerPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(5, 10, 5, 10));

        String ngayTao = hd.getThoiGianTao() != null
                ? hd.getThoiGianTao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                : "";
        String nguoiTao = hd.getNhanVien() != null ? hd.getNhanVien().getTenNhanVien() : "";
        String khachHang = hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "";
        String loai = hd.getLoaiHoaDon() != null ? hd.getLoaiHoaDon().getMoTa() : "";

        headerPanel.add(new JLabel("<html><b>Mã HĐ:</b> " + maHD + "</html>"));
        headerPanel.add(new JLabel("<html><b>Ngày tạo:</b> " + ngayTao + "</html>"));
        headerPanel.add(new JLabel("<html><b>Người tạo:</b> " + nguoiTao + "</html>"));
        headerPanel.add(new JLabel("<html><b>Khách hàng:</b> " + khachHang + "</html>"));
        headerPanel.add(new JLabel("<html><b>Loại HĐ:</b> " + loai + "</html>"));
        headerPanel.add(new JLabel("<html><b>Trạng thái:</b> "
                + (hd.isTrangThaiThanhToan() ? "Đã thanh toán" : "Chưa thanh toán") + "</html>"));

        panel.add(headerPanel, BorderLayout.NORTH);

        // Table
        ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
        List<ChiTietHoaDon> dsChiTiet = ctDAO.layTheoMaHoaDon(maHD);

        String[] cols = { "Tên SP", "ĐVT", "SL", "Đơn giá", "Thành tiền", "Quà tặng" };
        DefaultTableModel ctModel = new DefaultTableModel(cols, 0);
        NumberFormat nf = NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        double tongTien = 0;
        for (com.example.entity.ChiTietHoaDon ct : dsChiTiet) {
            String tenSP = ct.getDonViQuyDoi() != null && ct.getDonViQuyDoi().getSanPham() != null
                    ? ct.getDonViQuyDoi().getSanPham().getTenSanPham()
                    : "";
            String dvt = ct.getDonViQuyDoi() != null && ct.getDonViQuyDoi().getTenDonVi() != null
                    ? ct.getDonViQuyDoi().getTenDonVi().toString()
                    : "";
            int sl = ct.getSoLuong();
            double thanhTienNum = ct.tinhThanhTien();
            tongTien += thanhTienNum;
            String donGia = nf.format(ct.getDonGia());
            String thanhTien = nf.format(thanhTienNum);
            String quaTang = ct.isLaQuaTangKem() ? "Có" : "Không";

            ctModel.addRow(new Object[] { tenSP, dvt, sl, donGia, thanhTien, quaTang });
        }

        JTable ctTable = new JTable(ctModel);
        ctTable.setRowHeight(30);
        ctTable.setFont(FONT_TEXT);
        ctTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        ctTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(ctTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_BORDER));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Footer Info
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        
        hd.setDsChiTiet(dsChiTiet);
        double tongTienCuoiCung = hd.tinhTongTienThanhToan();
        
        JLabel lblTong = new JLabel("Tổng tiền: " + nf.format(tongTienCuoiCung));
        lblTong.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTong.setForeground(new Color(220, 53, 69)); // Red color
        footerPanel.add(lblTong);

        panel.add(footerPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void thanhToanHoaDon() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một hóa đơn để thanh toán.");
            return;
        }

        String trangThai = model.getValueAt(row, 7).toString();
        if ("Đã thanh toán".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thanh toán.");
            return;
        }

        String maHD = model.getValueAt(row, 0).toString();
        ChiTietHoaDonDAO ctDAO = new ChiTietHoaDonDAO();
        List<ChiTietHoaDon> dsChiTiet = ctDAO.layTheoMaHoaDon(maHD);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận thanh toán: " + maHD,
                true);
        dialog.setSize(850, 550);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Thông tin hóa đơn
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.setBorder(new EmptyBorder(15, 20, 10, 20));
        infoPanel.setBackground(COLOR_CARD_BG);

        infoPanel.add(createLabelInfo("Khách hàng: ", model.getValueAt(row, 4).toString()));
        infoPanel.add(createLabelInfo("Người tạo: ", model.getValueAt(row, 1).toString()));
        infoPanel.add(createLabelInfo("Ngày tạo: ", model.getValueAt(row, 2).toString()));
        infoPanel.add(createLabelInfo("Loại hóa đơn: ", model.getValueAt(row, 3).toString()));
        infoPanel.add(createLabelInfo("Khuyến mãi: ", model.getValueAt(row, 6).toString()));

        JLabel lblTongTien = new JLabel("Tổng tiền: " + model.getValueAt(row, 5).toString());
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTongTien.setForeground(new Color(220, 53, 69)); // Màu đỏ cho nổi bật
        infoPanel.add(lblTongTien);

        // Bảng chi tiết sản phẩm
        String[] cols = { "Tên sản phẩm", "Đơn vị tính", "Số lượng", "Đơn giá", "Thành tiền", "Quà tặng" };
        DefaultTableModel ctModel = new DefaultTableModel(cols, 0);
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        for (ChiTietHoaDon ct : dsChiTiet) {
            String tenSP = ct.getDonViQuyDoi() != null && ct.getDonViQuyDoi().getSanPham() != null
                    ? ct.getDonViQuyDoi().getSanPham().getTenSanPham()
                    : "";
            String dvt = ct.getDonViQuyDoi() != null && ct.getDonViQuyDoi().getTenDonVi() != null
                    ? ct.getDonViQuyDoi().getTenDonVi().toString()
                    : "";
            int sl = ct.getSoLuong();
            String donGia = nf.format(ct.getDonGia());
            String thanhTien = nf.format(ct.tinhThanhTien());
            String quaTang = ct.isLaQuaTangKem() ? "Có" : "Không";

            ctModel.addRow(new Object[] { tenSP, dvt, sl, donGia, thanhTien, quaTang });
        }

        JTable ctTable = new JTable(ctModel);
        ctTable.setRowHeight(35);
        ctTable.setFont(FONT_TEXT);
        ctTable.getTableHeader().setFont(FONT_LABEL);
        ctTable.getTableHeader().setBackground(new Color(240, 240, 240));

        JScrollPane scrollPane = new JScrollPane(ctTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        // Nút bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));

        RoundedButton btnHuy = new RoundedButton("Hủy");
        btnHuy.setPreferredSize(new Dimension(100, 40));
        btnHuy.setBackground(Color.WHITE);
        btnHuy.setFont(FONT_LABEL);
        btnHuy.addActionListener(e -> dialog.dispose());

        RoundedButton btnXacNhan = new RoundedButton("Xác nhận thanh toán");
        btnXacNhan.setBackground(COLOR_PRIMARY);
        btnXacNhan.setForeground(Color.BLACK);
        btnXacNhan.setPreferredSize(new Dimension(180, 40));
        btnXacNhan.setFont(FONT_LABEL);

        btnXacNhan.addActionListener(e -> {
            HoaDon hd = hoaDonDAO.timTheoMa(maHD);
            if (hd != null) {
                hd.setTrangThaiThanhToan(true);
                if (hoaDonDAO.capNhat(hd)) {
                    JOptionPane.showMessageDialog(dialog, "Thanh toán thành công!");
                    dialog.dispose();
                    taiLaiDanhSach();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Lỗi: Không thể cập nhật trạng thái hóa đơn trên cơ sở dữ liệu.");
                }
            }
        });

        btnPanel.add(btnHuy);
        btnPanel.add(btnXacNhan);

        dialog.add(infoPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JLabel createLabelInfo(String title, String value) {
        JLabel label = new JLabel("<html><b>" + title + "</b> " + value + "</html>");
        label.setFont(FONT_TEXT);
        return label;
    }
}
