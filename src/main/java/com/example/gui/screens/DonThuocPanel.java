package com.example.gui.screens;

import com.example.dao.DonThuocDAO;
import com.example.entity.DonThuoc;
import com.example.gui.components.RoundedButton;
import com.example.gui.components.RoundedPanel;
import com.example.gui.components.RoundedTextField;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class DonThuocPanel extends JPanel {

    private final Color COLOR_BG = new Color(241, 246, 255); // #F1F6FF
    private final Color COLOR_CARD_BG = Color.WHITE;
    private final Color COLOR_PRIMARY = new Color(0, 200, 83);
    private final Color COLOR_DANGER = new Color(220, 53, 69);
    private final Color COLOR_WARNING = new Color(255, 193, 7);
    private final Color COLOR_BORDER = new Color(230, 230, 230);
    private final Color COLOR_TEXT_DIM = new Color(110, 110, 110);

    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_TEXT = new Font("Segoe UI", Font.PLAIN, 14);

    private final DonThuocDAO donThuocDAO;
    private DefaultTableModel model;
    private JTable table;

    private RoundedTextField txtSearch;
    private DatePicker searchDatePicker;
    
    private RoundedTextField txtMaDonThuoc, txtTenBacSi, txtCoSoKhamBenh;
    private DatePicker formDatePicker;

    public DonThuocPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(COLOR_BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        donThuocDAO = new DonThuocDAO();

        JPanel centerPanel = new JPanel(new BorderLayout(0, 25));
        centerPanel.setOpaque(false);

        centerPanel.add(createFilterPanel(), BorderLayout.NORTH);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);
        add(createDetailPanel(), BorderLayout.SOUTH);

        taiLaiDanhSach();
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        txtSearch = new RoundedTextField("Mã / Tên BS", 15);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setFont(FONT_TEXT);

        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setFontValidDate(FONT_TEXT);

        searchDatePicker = new DatePicker(dateSettings);
        searchDatePicker.setPreferredSize(new Dimension(200, 35));
        searchDatePicker.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        searchDatePicker.setBackground(Color.WHITE);
        searchDatePicker.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1, true));

        RoundedButton btnSearch = new RoundedButton("Tìm kiếm");
        btnSearch.setFont(FONT_TEXT);
        btnSearch.setBackground(COLOR_CARD_BG);
        btnSearch.setPreferredSize(new Dimension(100, 35));

        RoundedButton btnRefresh = new RoundedButton("Làm mới");
        btnRefresh.setFont(FONT_TEXT);
        btnRefresh.setBackground(COLOR_CARD_BG);
        btnRefresh.setPreferredSize(new Dimension(100, 35));

        leftPanel.add(txtSearch);
        leftPanel.add(searchDatePicker);
        leftPanel.add(btnSearch);
        leftPanel.add(btnRefresh);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        RoundedButton btnAdd = new RoundedButton("Thêm");
        btnAdd.setFont(FONT_LABEL);
        btnAdd.setBackground(COLOR_PRIMARY);
        btnAdd.setPreferredSize(new Dimension(100, 35));

        RoundedButton btnUpdate = new RoundedButton("Sửa");
        btnUpdate.setFont(FONT_LABEL);
        btnUpdate.setBackground(COLOR_WARNING);
        btnUpdate.setPreferredSize(new Dimension(100, 35));

        RoundedButton btnDelete = new RoundedButton("Xóa");
        btnDelete.setFont(FONT_LABEL);
        btnDelete.setBackground(COLOR_DANGER);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setPreferredSize(new Dimension(100, 35));

        rightPanel.add(btnAdd);
        rightPanel.add(btnUpdate);
        rightPanel.add(btnDelete);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        // Events
        btnSearch.addActionListener(e -> timKiem());
        btnRefresh.addActionListener(e -> lamMoiDLRST());
        btnAdd.addActionListener(e -> themDonThuoc());
        btnUpdate.addActionListener(e -> suaDonThuoc());
        btnDelete.addActionListener(e -> xoaDonThuoc());

        return panel;
    }

    private JPanel createTablePanel() {
        RoundedPanel panel = new RoundedPanel(16, true);
        panel.setLayout(new BorderLayout());
        panel.setBackground(COLOR_CARD_BG);

        String[] columns = { "Mã đơn thuốc", "Tên bác sĩ", "Cơ sở khám bệnh", "Ngày kê đơn" };
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
        JPanel panel = new JPanel(new GridLayout(2, 2, 40, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        txtMaDonThuoc = new RoundedTextField(1);
        txtTenBacSi = new RoundedTextField(1);
        txtCoSoKhamBenh = new RoundedTextField(1);
        
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("dd/MM/yyyy");
        dateSettings.setFontValidDate(FONT_TEXT);
        formDatePicker = new DatePicker(dateSettings);
        formDatePicker.getComponentDateTextField().setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        panel.add(createFieldGroup("Mã đơn thuốc:", txtMaDonThuoc));
        panel.add(createFieldGroup("Tên bác sĩ:", txtTenBacSi));
        panel.add(createFieldGroup("Cơ sở khám bệnh:", txtCoSoKhamBenh));
        
        JPanel dateGroup = new JPanel(new BorderLayout(15, 0));
        dateGroup.setOpaque(false);
        JLabel lblDate = new JLabel("Ngày kê đơn:");
        lblDate.setFont(FONT_LABEL);
        lblDate.setPreferredSize(new Dimension(150, 30));
        dateGroup.add(lblDate, BorderLayout.WEST);
        dateGroup.add(formDatePicker, BorderLayout.CENTER);
        panel.add(dateGroup);

        return panel;
    }

    private JPanel createFieldGroup(String label, RoundedTextField field) {
        JPanel group = new JPanel(new BorderLayout(15, 0));
        group.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_LABEL);
        lbl.setPreferredSize(new Dimension(150, 30));

        group.add(lbl, BorderLayout.WEST);
        group.add(field, BorderLayout.CENTER);

        return group;
    }

    private void taiLaiDanhSach() {
        model.setRowCount(0);
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<DonThuoc> ds = donThuocDAO.layTatCa();
        for (DonThuoc dt : ds) {
            String ngayKe = dt.getNgayKeDon() != null ? dt.getNgayKeDon().format(dtf) : "";
            model.addRow(new Object[]{
                    dt.getMaDonThuoc(),
                    dt.getTenBacSi(),
                    dt.getCoSoKhamBenh(),
                    ngayKe
            });
        }
    }
    
    private void lamMoiDLRST() {
        txtSearch.setText("Mã / Tên BS");
        searchDatePicker.clear();
        lamMoiForm();
        taiLaiDanhSach();
    }

    private void lamMoiForm() {
        txtMaDonThuoc.setText("");
        txtTenBacSi.setText("");
        txtCoSoKhamBenh.setText("");
        formDatePicker.clear();
        table.clearSelection();
    }

    private void hienThiChiTiet() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String ma = (String) model.getValueAt(row, 0);
            DonThuoc dt = donThuocDAO.timTheoMa(ma);
            if (dt != null) {
                txtMaDonThuoc.setText(dt.getMaDonThuoc());
                txtTenBacSi.setText(dt.getTenBacSi());
                txtCoSoKhamBenh.setText(dt.getCoSoKhamBenh());
                formDatePicker.setDate(dt.getNgayKeDon());
            }
        }
    }

    private void themDonThuoc() {
        String ma = txtMaDonThuoc.getText().trim();
        String ten = txtTenBacSi.getText().trim();
        String coSo = txtCoSoKhamBenh.getText().trim();
        LocalDate ngay = formDatePicker.getDate();

        if (ma.isEmpty() || ten.isEmpty() || coSo.isEmpty() || ngay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (donThuocDAO.timTheoMa(ma) != null) {
            JOptionPane.showMessageDialog(this, "Mã đơn thuốc đã tồn tại!");
            return;
        }

        DonThuoc dt = new DonThuoc(ma, ten, coSo, ngay);
        if (donThuocDAO.them(dt)) {
            JOptionPane.showMessageDialog(this, "Thêm đơn thuốc thành công!");
            taiLaiDanhSach();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    private void suaDonThuoc() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn thuốc cần sửa!");
            return;
        }

        String ma = txtMaDonThuoc.getText().trim();
        String ten = txtTenBacSi.getText().trim();
        String coSo = txtCoSoKhamBenh.getText().trim();
        LocalDate ngay = formDatePicker.getDate();

        if (ten.isEmpty() || coSo.isEmpty() || ngay == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        DonThuoc dt = new DonThuoc(ma, ten, coSo, ngay);
        if (donThuocDAO.capNhat(dt)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            taiLaiDanhSach();
            lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
        }
    }

    private void xoaDonThuoc() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đơn thuốc cần xóa!");
            return;
        }

        String ma = (String) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa đơn thuốc " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (donThuocDAO.xoa(ma)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                taiLaiDanhSach();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }
    
    private void timKiem() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        if (keyword.equals("mã / tên bs")) keyword = "";
        LocalDate searchDate = searchDatePicker.getDate();
        
        model.setRowCount(0);
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<DonThuoc> ds = donThuocDAO.layTatCa();
        
        for (DonThuoc dt : ds) {
            boolean matchKeyword = keyword.isEmpty() || 
                                   dt.getMaDonThuoc().toLowerCase().contains(keyword) || 
                                   dt.getTenBacSi().toLowerCase().contains(keyword);
            boolean matchDate = searchDate == null || (dt.getNgayKeDon() != null && dt.getNgayKeDon().equals(searchDate));
            
            if (matchKeyword && matchDate) {
                String ngayKe = dt.getNgayKeDon() != null ? dt.getNgayKeDon().format(dtf) : "";
                model.addRow(new Object[]{
                        dt.getMaDonThuoc(),
                        dt.getTenBacSi(),
                        dt.getCoSoKhamBenh(),
                        ngayKe
                });
            }
        }
    }
}
