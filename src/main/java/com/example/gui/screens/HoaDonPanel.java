package com.example.gui.screens;

import com.example.dao.HoaDonDAO;
import com.example.entity.HoaDon;
import com.example.gui.components.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
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

    public HoaDonPanel() {
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

        RoundedTextField txtSearch = new RoundedTextField("Mã", 15);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setFont(FONT_TEXT);

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setFontValidDate(FONT_TEXT);

        DatePicker datePicker = new DatePicker(dateSettings);
        datePicker.setPreferredSize(new Dimension(200, 35));
        datePicker.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        datePicker.setBackground(Color.WHITE);
        datePicker.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        RoundedButton btnView = new RoundedButton("Xem  👁");
        btnView.setFont(FONT_TEXT);
        btnView.setForeground(Color.DARK_GRAY);
        btnView.setBackground(Color.WHITE);
        btnView.setPreferredSize(new Dimension(80, 35));

        leftPanel.add(txtSearch);
        leftPanel.add(datePicker);
        leftPanel.add(btnView);

        RoundedButton btnPayment = new RoundedButton("Thanh toán");
        btnPayment.setFont(FONT_LABEL);
        btnPayment.setBackground(COLOR_PRIMARY);
        btnPayment.setPreferredSize(new Dimension(120, 40));

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(btnPayment, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        RoundedPanel panel = new RoundedPanel(16, true);
        panel.setLayout(new BorderLayout());
        panel.setBackground(COLOR_CARD_BG);

        String[] columns = { "Mã hóa đơn", "Người tạo", "Ngày tạo", "Loại hóa đơn", "Khách hàng", "Tổng tiền", "Khuyến mãi", "Trạng thái" };
        model = new DefaultTableModel(columns, 15);
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setShowGrid(true);
        table.setGridColor(COLOR_BORDER);
        table.setFont(FONT_TEXT);

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

        panel.add(createFieldGroup("Mã Hóa Đơn :"));
        panel.add(createFieldGroup("Người Tạo :"));

        panel.add(createFieldGroup("Ngày tạo :"));
        panel.add(createFieldGroup("Loại Hóa Đơn :"));

        panel.add(createFieldGroup("Tên Khách Hàng :"));
        panel.add(createFieldGroup("Tổng Tiền :"));

        panel.add(createFieldGroup("Khuyến mãi :"));
        panel.add(createFieldGroup("Trạng thái thanh toán :"));

        return panel;
    }

    private JPanel createFieldGroup(String label) {
        JPanel group = new JPanel(new BorderLayout(15, 0));
        group.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setPreferredSize(new Dimension(150, 30));

        RoundedTextField field = new RoundedTextField(1);
        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));

        group.add(lbl, BorderLayout.WEST);
        group.add(field, BorderLayout.CENTER);

        return group;
    }

    public void taiLaiDanhSach() {
        if (model == null) return;
        model.setRowCount(0); // Xóa dữ liệu cũ
        List<HoaDon> ds = hoaDonDAO.layTatCa();
        
        int homNay = 0;
        int banHang = 0;
        int doiHang = 0;
        int traHang = 0;
        
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("vi", "VN"));

        for (HoaDon h : ds) {
            String ngayTao = h.getThoiGianTao() != null ? h.getThoiGianTao().format(dtf) : "";
            String nguoiTao = h.getNhanVien() != null ? h.getNhanVien().getTenNhanVien() : "";
            String loaiHoaDon = h.getLoaiHoaDon() != null ? h.getLoaiHoaDon().getMoTa() : "";
            String khachHang = h.getKhachHang() != null ? h.getKhachHang().getTenKhachHang() : "";
            String tongTien = nf.format(h.tinhTongTienThanhToan());
            String khuyenMai = h.getKhuyenMai() != null ? h.getKhuyenMai().getTenKhuyenMai() : "Không có";
            String trangThai = h.isTrangThaiThanhToan() ? "Đã thanh toán" : "Chưa thanh toán";
            
            model.addRow(new Object[] {
                h.getMaHoaDon(),
                nguoiTao,
                ngayTao,
                loaiHoaDon,
                khachHang,
                tongTien,
                khuyenMai,
                trangThai
            });
            
            if (h.getThoiGianTao() != null && h.getThoiGianTao().toLocalDate().isEqual(today)) {
                homNay++;
            }
            
            if (h.getLoaiHoaDon() == com.example.entity.LoaiHoaDon.BAN_HANG) {
                banHang++;
            } else if (h.getLoaiHoaDon() == com.example.entity.LoaiHoaDon.DOI_HANG) {
                doiHang++;
            } else if (h.getLoaiHoaDon() == com.example.entity.LoaiHoaDon.TRA_HANG) {
                traHang++;
            }
        }
        
        if (lblHoaDonHomNay != null) {
            lblHoaDonHomNay.setText(String.valueOf(homNay));
            lblHoaDonBanHang.setText(String.valueOf(banHang));
            lblHoaDonDoiHang.setText(String.valueOf(doiHang));
            lblHoaDonTraHang.setText(String.valueOf(traHang));
        }
    }
}
