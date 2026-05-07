package com.example.gui.screens;

import com.example.dao.CaLamDAO;
import com.example.entity.CaLam;
import com.example.entity.NhanVien;
import com.example.entity.enums.TrangThaiCaLam;
import com.toedter.calendar.JDateChooser; // Cần import thư viện JCalendar

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class CaLamPanel extends JPanel {

    // --- UI Components ---
    private JTextField txtSearch, txtMaCa, txtMaNV, txtTenNV;
    private JDateChooser dateChooserFilter, dateNgayLamForm;
    private JComboBox<String> cbFilterTime;
    private JComboBox<TrangThaiCaLam> cbTrangThai;
    private JSpinner spinGioBatDau, spinGioKetThuc;
    private JButton btnXem, btnThem, btnSua, btnXoa, btnLamMoi;
    private JTable tblDanhSach, tblLichTuan;
    private DefaultTableModel modelDanhSach, modelLichTuan;

    // --- Data Logic ---
    private CaLamDAO caLamDAO = new CaLamDAO();
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private LocalDate selectedDate = LocalDate.now();
    private List<CaLam> listCaTuan = new ArrayList<>();

    public CaLamPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        add(createTopBar(), BorderLayout.NORTH);

        JPanel centerArea = new JPanel(new BorderLayout(20, 0));
        centerArea.setBackground(new Color(245, 245, 245));
        centerArea.setBorder(new EmptyBorder(10, 20, 20, 20));

        centerArea.add(createFormPanel(), BorderLayout.WEST);
        centerArea.add(createRightPanel(), BorderLayout.CENTER);
        add(centerArea, BorderLayout.CENTER);

        setupListeners();
        
        // Mặc định load dữ liệu hôm nay
        dateChooserFilter.setDate(java.sql.Date.valueOf(LocalDate.now()));
        loadDuLieu();
    }

    // ================== GIAO DIỆN (UI) ==================

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        topBar.setBackground(Color.WHITE);

        txtSearch = new JTextField(20);
        txtSearch.setPreferredSize(new Dimension(200, 35));
        txtSearch.setToolTipText("Tìm kiếm theo tên nhân viên...");

        cbFilterTime = new JComboBox<>(new String[]{"Hôm nay", "Ngày mai", "Tuần này", "Tuần sau", "Tùy chọn ngày"});
        cbFilterTime.setPreferredSize(new Dimension(130, 35));

        dateChooserFilter = new JDateChooser();
        dateChooserFilter.setPreferredSize(new Dimension(150, 35));
        dateChooserFilter.setDateFormatString("dd/MM/yyyy");

        btnXem = new JButton("Xem");
        btnXem.setBackground(new Color(0, 213, 255));
        btnXem.setForeground(Color.BLACK);
        btnXem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXem.setPreferredSize(new Dimension(90, 35));

        topBar.add(new JLabel("Tìm kiếm: ")); topBar.add(txtSearch);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(cbFilterTime);
        topBar.add(new JLabel("Ngày: ")); topBar.add(dateChooserFilter);
        topBar.add(btnXem);

        return topBar;
    }

    private JPanel createFormPanel() {
        // Panel bọc ngoài cùng bên trái
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setPreferredSize(new Dimension(430, 0));
        leftWrapper.setBackground(new Color(245, 245, 245));
        
        // CHIÊU THỨ 1: Cắt bớt phần đáy 180px để form "ngang với chữ Mở ca"
        leftWrapper.setBorder(new EmptyBorder(0, 0, 40, 0)); 

        // Khung trắng chứa nội dung
        JPanel contentBox = new JPanel(new BorderLayout());
        contentBox.setBackground(Color.WHITE);
        contentBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), "THÔNG TIN CA LÀM", 
                TitledBorder.CENTER, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 18)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Phần nhập liệu
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 10, 25)); // Khoảng cách dòng 25px cho thoáng
        inputPanel.setBackground(Color.WHITE);

        inputPanel.add(new JLabel("Mã ca:"));
        inputPanel.add(txtMaCa = new JTextField());
        txtMaCa.setEditable(false); txtMaCa.setBackground(new Color(230,230,230));

        inputPanel.add(new JLabel("Mã nhân viên:"));
        inputPanel.add(txtMaNV = new JTextField());

        inputPanel.add(new JLabel("Tên nhân viên:"));
        inputPanel.add(txtTenNV = new JTextField());
        txtTenNV.setEditable(false);

        inputPanel.add(new JLabel("Ngày làm:"));
        dateNgayLamForm = new com.toedter.calendar.JDateChooser(new java.util.Date());
        dateNgayLamForm.setDateFormatString("dd/MM/yyyy");
        inputPanel.add(dateNgayLamForm);

        inputPanel.add(new JLabel("Giờ bắt đầu:"));
        spinGioBatDau = new JSpinner(new SpinnerDateModel());
        spinGioBatDau.setEditor(new JSpinner.DateEditor(spinGioBatDau, "HH:mm"));
        inputPanel.add(spinGioBatDau);

        inputPanel.add(new JLabel("Giờ kết thúc:"));
        spinGioKetThuc = new JSpinner(new SpinnerDateModel());
        spinGioKetThuc.setEditor(new JSpinner.DateEditor(spinGioKetThuc, "HH:mm"));
        inputPanel.add(spinGioKetThuc);

        inputPanel.add(new JLabel("Trạng thái ca:"));
        cbTrangThai = new JComboBox<>(com.example.entity.enums.TrangThaiCaLam.values());
        inputPanel.add(cbTrangThai);

        // Phần 4 nút bấm
        JPanel btnPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setPreferredSize(new Dimension(0, 40)); // Cố định chiều cao nút là 40px
        
        btnThem = createColorButton("Thêm", new Color(21, 215, 221));
        btnSua = createColorButton("Sửa", new Color(255, 243, 108));
        btnXoa = createColorButton("Xóa", new Color(255, 89, 89));
        btnLamMoi = createColorButton("Làm mới", new Color(0, 213, 255));
        
        btnPanel.add(btnThem); btnPanel.add(btnSua); btnPanel.add(btnXoa); btnPanel.add(btnLamMoi);

        // Lắp ráp vào contentBox
        contentBox.add(inputPanel, BorderLayout.NORTH); // Form nhập liệu đẩy lên trên
        contentBox.add(btnPanel, BorderLayout.SOUTH);   // 4 nút bấm neo chặt ở dưới đáy form

        leftWrapper.add(contentBox, BorderLayout.CENTER);

        return leftWrapper;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(0, 20)); 
        rightPanel.setBackground(new Color(245, 245, 245));

        // CẬP NHẬT QUAN TRỌNG: Thêm khoảng trống đáy 180px giống hệt khung bên trái
        // Việc này ép đáy của bảng Lịch làm việc nâng lên ngang bằng với form Thông tin
        rightPanel.setBorder(new EmptyBorder(0, 0, 50, 0));

        // 1. Bảng danh sách ca làm
        JPanel pnlDanhSach = new JPanel(new BorderLayout());
        pnlDanhSach.setBackground(Color.WHITE);
        pnlDanhSach.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), "DANH SÁCH CA LÀM", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16)));
        
        String[] colsDS = {"Mã ca", "Mã NV", "Tên nhân viên", "Ngày làm", "Giờ bắt đầu", "Giờ kết thúc", "Trạng thái"};
        modelDanhSach = new DefaultTableModel(null, colsDS) { public boolean isCellEditable(int r, int c) { return false; } };
        tblDanhSach = new JTable(modelDanhSach);
        tblDanhSach.setRowHeight(28);
        pnlDanhSach.add(new JScrollPane(tblDanhSach));

        // 2. Bảng Lịch làm trong tuần
        JPanel pnlLich = new JPanel(new BorderLayout());
        pnlLich.setBackground(Color.WHITE);
        pnlLich.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), "LỊCH LÀM TRONG TUẦN", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 16)));
        
        // CẬP NHẬT: Giảm chiều cao khung lịch từ 420px xuống 240px 
        // Lịch xẹp xuống sẽ tự động nhường chỗ cho Danh sách ca làm phía trên cao lên
        pnlLich.setPreferredSize(new Dimension(0, 300)); 
        
        String[] colsLich = {"Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật"};
        modelLichTuan = new DefaultTableModel(null, colsLich) { public boolean isCellEditable(int r, int c) { return false; } };
        tblLichTuan = new JTable(modelLichTuan);
        tblLichTuan.setRowHeight(65);
        
        // --- CUSTOM RENDERER: HIGHLIGHT CỘT THEO NGÀY ---
        tblLichTuan.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                DayOfWeek dow = selectedDate.getDayOfWeek();
                int targetCol = dow.getValue() - 1; // Thứ 2 = 1 -> index 0
                
                if (column == targetCol) {
                    c.setBackground(new Color(255, 250, 205)); // Màu vàng nhạt nổi bật
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                } else {
                    c.setBackground(Color.WHITE);
                    c.setFont(c.getFont().deriveFont(Font.PLAIN));
                }
                
                if (isSelected) c.setBackground(table.getSelectionBackground());
                return c;
            }
        });

        pnlLich.add(new JScrollPane(tblLichTuan));

        // Lắp ráp 2 bảng vào phải
        rightPanel.add(pnlDanhSach, BorderLayout.CENTER); 
        rightPanel.add(pnlLich, BorderLayout.SOUTH); 
        
        return rightPanel;
    }

    // ================== LOGIC NGHIỆP VỤ (EVENTS & DAO) ==================

    private void setupListeners() {
        // Combobox filter
        cbFilterTime.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            switch (cbFilterTime.getSelectedIndex()) {
                case 0: dateChooserFilter.setDate(java.sql.Date.valueOf(today)); break; // Hôm nay
                case 1: dateChooserFilter.setDate(java.sql.Date.valueOf(today.plusDays(1))); break; // Ngày mai
                case 2: dateChooserFilter.setDate(java.sql.Date.valueOf(today)); break; // Tuần này (lấy ngày hiện tại)
                case 3: dateChooserFilter.setDate(java.sql.Date.valueOf(today.plusWeeks(1))); break; // Tuần sau
            }
        });

        btnXem.addActionListener(e -> loadDuLieu());
        btnLamMoi.addActionListener(e -> lamMoiForm());
        btnThem.addActionListener(e -> themCaLam());
        btnSua.addActionListener(e -> suaCaLam());
        btnXoa.addActionListener(e -> xoaCaLam());

        // Click Table Danh sách -> Đổ lên Form
        tblDanhSach.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tblDanhSach.getSelectedRow();
                if (r >= 0) {
                    txtMaCa.setText(modelDanhSach.getValueAt(r, 0).toString());
                    txtMaNV.setText(modelDanhSach.getValueAt(r, 1).toString());
                    txtTenNV.setText(modelDanhSach.getValueAt(r, 2).toString());
                    
                    String[] ngayArr = modelDanhSach.getValueAt(r, 3).toString().split("/");
                    LocalDate nl = LocalDate.of(Integer.parseInt(ngayArr[2]), Integer.parseInt(ngayArr[1]), Integer.parseInt(ngayArr[0]));
                    dateNgayLamForm.setDate(java.sql.Date.valueOf(nl));

                    try {
                        Date gioBD = new java.text.SimpleDateFormat("HH:mm").parse(modelDanhSach.getValueAt(r, 4).toString());
                        Date gioKT = new java.text.SimpleDateFormat("HH:mm").parse(modelDanhSach.getValueAt(r, 5).toString());
                        spinGioBatDau.setValue(gioBD);
                        spinGioKetThuc.setValue(gioKT);
                    } catch (Exception ex) {}

                    cbTrangThai.setSelectedItem(TrangThaiCaLam.valueOf(modelDanhSach.getValueAt(r, 6).toString()));
                }
            }
        });
        // 6. Click Table Lịch Tuần -> Đổi ngày, Đổ dữ liệu lên Danh Sách và Form
        tblLichTuan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = tblLichTuan.getSelectedRow();
                int c = tblLichTuan.getSelectedColumn();
                
                if (r >= 0 && c >= 0) {
                    Object val = modelLichTuan.getValueAt(r, c);
                    // Chỉ xử lý nếu ô được click có chứa dữ liệu chữ
                    if (val != null && !val.toString().trim().isEmpty()) {
                        String cellText = val.toString();
                        CaLam clickedCa = null;
                        
                        // Tìm Ca Làm tương ứng trong listCaTuan
                        for (CaLam cl : listCaTuan) {
                            int colIndex = cl.getGioBatDau().getDayOfWeek().getValue() - 1;
                            String text = cl.getNhanVien().getMaNhanVien() + " (" + cl.getGioBatDau().format(timeFormatter) + "-" + cl.getGioKetThuc().format(timeFormatter) + ")";
                            
                            // Khớp đúng cột (thứ) và đúng chuỗi văn bản hiển thị
                            if (colIndex == c && text.equals(cellText)) {
                                clickedCa = cl;
                                break;
                            }
                        }

                        if (clickedCa != null) {
                            // a. Cập nhật ô chọn ngày sang ngày của ca vừa click
                            dateChooserFilter.setDate(java.sql.Date.valueOf(clickedCa.getGioBatDau().toLocalDate()));
                            
                            // b. Load lại toàn bộ dữ liệu (Bảng danh sách sẽ tự lọc ra ngày đó, ô màu vàng sẽ tự chạy sang)
                            loadDuLieu();
                            
                            // c. Tìm và bôi đen đúng dòng chứa ca đó trong Bảng Danh Sách
                            for (int i = 0; i < tblDanhSach.getRowCount(); i++) {
                                if (tblDanhSach.getValueAt(i, 0).toString().equals(clickedCa.getMaCa())) {
                                    tblDanhSach.setRowSelectionInterval(i, i);
                                    
                                    // d. Giả lập một cú click chuột vào bảng danh sách để Form bên trái tự cập nhật
                                    tblDanhSach.dispatchEvent(new MouseEvent(tblDanhSach, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, 0, 0, 1, false));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    private void loadDuLieu() {
        if (dateChooserFilter.getDate() == null) return;
        selectedDate = dateChooserFilter.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        String timKiem = txtSearch.getText().trim();

        // 1. Load bảng Danh sách (Theo ngày chọn)
        List<CaLam> dsNgay = caLamDAO.layCaTheoNgayVaTen(selectedDate, timKiem);
        modelDanhSach.setRowCount(0);
        for (CaLam cl : dsNgay) {
            String tenNV = cl.getNhanVien() != null ? cl.getNhanVien().getTenNhanVien() : "";
            modelDanhSach.addRow(new Object[]{
                cl.getMaCa(), cl.getNhanVien().getMaNhanVien(), tenNV,
                selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                cl.getGioBatDau().format(timeFormatter),
                cl.getGioKetThuc() != null ? cl.getGioKetThuc().format(timeFormatter) : "",
                cl.getTrangThai().name()
            });
        }

        // 2. Load bảng Lịch Tuần
        LocalDate thu2 = selectedDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate chuNhat = selectedDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        
        listCaTuan = caLamDAO.layCaTrongTuan(thu2, chuNhat);
        
        // Sửa Header lịch tuần (Kèm ngày/tháng)
        for (int i = 0; i < 7; i++) {
            // Fix lỗi Thứ CN -> Chủ nhật
            String tenThu = (i == 6) ? "Chủ nhật" : "Thứ " + (i + 2);
            tblLichTuan.getColumnModel().getColumn(i).setHeaderValue(
                tenThu + " (" + thu2.plusDays(i).format(DateTimeFormatter.ofPattern("dd/MM")) + ")"
            );
        }
        tblLichTuan.getTableHeader().repaint();

        modelLichTuan.setRowCount(0);
        // Map data vào 7 cột đơn giản (Demo chia dòng - thực tế có thể phức tạp hơn)
        int maxRows = Math.max(5, listCaTuan.size()); 
        for (int i = 0; i < maxRows; i++) modelLichTuan.addRow(new Object[7]);

        int[] rowIndexes = new int[7]; 
        for (CaLam cl : listCaTuan) {
            int colIndex = cl.getGioBatDau().getDayOfWeek().getValue() - 1;
            String text = cl.getNhanVien().getMaNhanVien() + " (" + cl.getGioBatDau().format(timeFormatter) + "-" + cl.getGioKetThuc().format(timeFormatter) + ")";
            if(rowIndexes[colIndex] < maxRows) {
                modelLichTuan.setValueAt(text, rowIndexes[colIndex]++, colIndex);
            }
        }
    }

    private void lamMoiForm() {
        txtMaCa.setText(""); txtMaNV.setText(""); txtTenNV.setText("");
        dateNgayLamForm.setDate(new Date());
        cbTrangThai.setSelectedItem(TrangThaiCaLam.CHUA_MO);
        tblDanhSach.clearSelection();
    }

    // --- YÊU CẦU: HÀM SINH MÃ TỰ ĐỘNG ---
    public String taoMaCa(LocalDate ngayLam) {
        String prefix = "CA" + ngayLam.format(DateTimeFormatter.ofPattern("ddMMyy"));
        int stt = caLamDAO.laySoLuongCaTrongNgay(prefix) + 1;
        return prefix + String.format("%02d", stt);
    }

    // --- RÀNG BUỘC (VALIDATE) ---
    private boolean validateDuLieu() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Nhân Viên!"); return false;
        }
        
        LocalDate date = dateNgayLamForm.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime start = LocalTime.parse(new java.text.SimpleDateFormat("HH:mm").format(spinGioBatDau.getValue()));
        LocalTime end = LocalTime.parse(new java.text.SimpleDateFormat("HH:mm").format(spinGioKetThuc.getValue()));
        
        if (!start.isBefore(end)) {
            JOptionPane.showMessageDialog(this, "Lỗi: Giờ bắt đầu phải nhỏ hơn Giờ kết thúc!"); return false;
        }
        
        // Kiểm tra trùng ca
        LocalDateTime dtStart = LocalDateTime.of(date, start);
        LocalDateTime dtEnd = LocalDateTime.of(date, end);
        String maCaCheck = txtMaCa.getText().isEmpty() ? null : txtMaCa.getText();

        if (caLamDAO.kiemTraTrungCa(txtMaNV.getText().trim(), dtStart, dtEnd, maCaCheck)) {
            JOptionPane.showMessageDialog(this, "Lỗi: Nhân viên này đã có ca làm việc khác trùng với khung giờ này!"); return false;
        }

        return true;
    }

    private void themCaLam() {
        if (!validateDuLieu()) return;
        
        LocalDate date = dateNgayLamForm.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        CaLam cl = new CaLam();
        cl.setMaCa(taoMaCa(date)); // Sinh mã mới
        cl.setNhanVien(new NhanVien(txtMaNV.getText().trim()));
        
        LocalTime start = LocalTime.parse(new java.text.SimpleDateFormat("HH:mm").format(spinGioBatDau.getValue()));
        LocalTime end = LocalTime.parse(new java.text.SimpleDateFormat("HH:mm").format(spinGioKetThuc.getValue()));
        
        cl.setGioBatDau(LocalDateTime.of(date, start));
        cl.setGioKetThuc(LocalDateTime.of(date, end));
        cl.setTrangThai(TrangThaiCaLam.CHUA_MO); // Ca mới tương lai mặc định là CHUA_MO
        cl.setTienMoCa(0); cl.setTienKetCa(0); cl.setTienHeThong(0); cl.setGhiChu("");

        if (caLamDAO.them(cl)) {
            JOptionPane.showMessageDialog(this, "Thêm ca làm thành công!");
            loadDuLieu(); lamMoiForm();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại. Kiểm tra lại mã nhân viên.");
        }
    }

    private void suaCaLam() {
        if (tblDanhSach.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca làm cần sửa!"); return;
        }
        if (cbTrangThai.getSelectedItem() != TrangThaiCaLam.CHUA_MO) {
            JOptionPane.showMessageDialog(this, "Chỉ được phép sửa các ca làm ở trạng thái CHƯA MỞ (Ca tương lai)!"); return;
        }
        if (!validateDuLieu()) return;

        LocalDate date = dateNgayLamForm.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        CaLam cl = caLamDAO.timTheoMa(txtMaCa.getText());
        cl.setNhanVien(new NhanVien(txtMaNV.getText().trim()));
        
        LocalTime start = LocalTime.parse(new java.text.SimpleDateFormat("HH:mm").format(spinGioBatDau.getValue()));
        LocalTime end = LocalTime.parse(new java.text.SimpleDateFormat("HH:mm").format(spinGioKetThuc.getValue()));
        
        cl.setGioBatDau(LocalDateTime.of(date, start));
        cl.setGioKetThuc(LocalDateTime.of(date, end));
        cl.setTrangThai((TrangThaiCaLam) cbTrangThai.getSelectedItem());

        if (caLamDAO.capNhat(cl)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            loadDuLieu();
        }
    }

    private void xoaCaLam() {
        if (tblDanhSach.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ca làm cần xóa!"); return;
        }
        if (cbTrangThai.getSelectedItem() != TrangThaiCaLam.CHUA_MO) {
            JOptionPane.showMessageDialog(this, "Từ chối! Chỉ được xóa các ca làm TƯƠNG LAI (CHƯA MỞ).\nCa đang mở hoặc đã đóng chứa dữ liệu doanh thu không thể xóa."); return;
        }
        
        if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa ca làm này?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (caLamDAO.xoa(txtMaCa.getText())) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                lamMoiForm(); loadDuLieu();
            }
        }
    }

    private JButton createColorButton(String txt, Color c) {
        JButton b = new JButton(txt);
        b.setBackground(c); b.setFont(new Font("Segoe UI", Font.BOLD, 13)); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}