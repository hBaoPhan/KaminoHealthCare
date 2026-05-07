package com.example.gui.screens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.example.dao.ChiTietHoaDonDAO;
import com.example.dao.HoaDonDAO;

import java.awt.*;
import java.text.DecimalFormat;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
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
    private JTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienTra, txtChenhLech, txtThue, txtThanhTien, txtTienTraLai;
    private JTextField txtSearch;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JCheckBox chkTienMat, chkChuyenKhoan;

    // --- Biến xử lý dữ liệu và Database ---
    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO ctHDPDAO = new ChiTietHoaDonDAO();
    private DefaultTableModel model; // Để đổ dữ liệu vào bảng
    private List<ChiTietHoaDon> dsChiTietGoc = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("###,###,### VND");
    private HoaDon hd;


    // =========================================================================
    // VÙNG 2: HÀM KHỞI TẠO (CONSTRUCTOR)
    // =========================================================================

    public TraHangPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        // --- PHẦN BÊN TRÁI: DANH SÁCH SẢN PHẨM HÓA ĐƠN TRẢ ---
        add(createTablePanel("Danh sách sản phẩm hóa đơn trả", "Mã hóa đơn"), BorderLayout.CENTER);

        // --- PHẦN BÊN PHẢI: THÔNG TIN HÓA ĐƠN TRẢ HÀNG ---
        add(createInfoPanel(), BorderLayout.EAST);
    }


    // =========================================================================
    // VÙNG 3: KHỞI TẠO GIAO DIỆN (UI BUILDING)
    // =========================================================================

    /**
     * Tạo Panel chứa bảng dữ liệu và thanh tìm kiếm phía trên
     */
    private JPanel createTablePanel(String title, String placeholder) {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));

        // Header Panel (Tiêu đề + Ô tìm kiếm)
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));

        // Panel chứa Ô nhập và Nút Tìm
        JPanel pnlSearchAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        pnlSearchAction.setOpaque(false);

        txtSearch = new JTextField(15);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String ma = txtSearch.getText().trim();
                    if (!ma.isEmpty()) {
                        hienThiSanPhamHoaDon(ma);
                    }
                }
            }
        });
        txtSearch.setPreferredSize(new Dimension(180, 30));
        txtSearch.setText(placeholder);
        txtSearch.setForeground(Color.GRAY);

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals(placeholder)) {
                    txtSearch.setText("");
                    txtSearch.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setForeground(Color.GRAY);
                    txtSearch.setText(placeholder);
                }
            }
        });

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setBackground(new Color(0, 123, 255));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setPreferredSize(new Dimension(65, 30));
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
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
        String[] columns = { "Mã sản phẩm", "Tên sản phẩm", "Đơn vị", "Số lượng", "Đơn giá", "Thuế", "Thành tiền" };
        Object[][] data = {};
        model = new DefaultTableModel(data, columns){
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Chỉ cho phép sửa cột số lượng
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getColumnModel().getColumn(3).setCellEditor(new QuantitySpinnerEditor());
        
        // Lắng nghe sự kiện người dùng gõ sửa số lượng
        model.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                // Chỉ xử lý khi cột số 3 (Số lượng) bị thay đổi
                if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 3) {
                    int row = e.getFirstRow();
                    try {
                        int soLuongMoi = Integer.parseInt(model.getValueAt(row, 3).toString());
                        String maSP = model.getValueAt(row, 0).toString();
                        
                        // Lấy số lượng mua gốc
                        int soLuongGoc = 0;
                        for (ChiTietHoaDon ct : dsChiTietGoc) {
                            if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                                soLuongGoc = ct.getSoLuong();
                                break;
                            }
                        }
                        
                        // Bắt lỗi nhập bậy
                        if (soLuongMoi <= 0 || soLuongMoi > soLuongGoc) {
                            JOptionPane.showMessageDialog(null, "Số lượng trả phải lớn hơn 0 và tối đa là " + soLuongGoc);
                            model.setValueAt(soLuongGoc, row, 3); // Hoàn nguyên số cũ
                            return;
                        }
                        
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
        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setPreferredSize(new Dimension(380, 0));
        pnlMain.setBackground(new Color(248, 248, 248));
        pnlMain.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

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
        addInputRow(pnlContent, "Mã hóa gốc", txtMaHoaGoc = new JTextField("HDB27032026001"), gbc, r++);
        addInputRow(pnlContent, "Mã hóa đơn", txtMaHoaDon = new JTextField("HDT27032026001"), gbc, r++);
        addInputRow(pnlContent, "Ngày tạo", txtNgayTao = new JTextField("27/03/2026"), gbc, r++);
        addInputRow(pnlContent, "Người tạo", txtNguoiTao = new JTextField("Phan Hoai Bao"), gbc, r++);
        addInputRow(pnlContent, "Tên khách hàng", txtTenKhachHang = new JTextField("Tran Tan Tai"), gbc, r++);

        gbc.gridy = r++;
        pnlContent.add(new JLabel("Chi chú"), gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(4, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlContent.add(new JScrollPane(txtGhiChu), gbc);

        gbc.gridy = r++;
        pnlContent.add(Box.createRigidArea(new Dimension(0, 10)), gbc);

        // Phần tính toán tiền
        addInputRow(pnlContent, "Tiền hóa đơn gốc :", txtTienGoc = new JTextField("220.500Đ"), gbc, r++);
        addInputRow(pnlContent, "Tiền hóa đơn trả :", txtTienTra = new JTextField("105.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Chênh lệch giá :", txtChenhLech = new JTextField("114.750Đ"), gbc, r++);
        addInputRow(pnlContent, "Tổng tiền thuế:", txtThue = new JTextField("5.250Đ"), gbc, r++);
        addInputRow(pnlContent, "Thành tiền :", txtThanhTien = new JTextField("114.750Đ"), gbc, r++);

        // Phương thức thanh toán
        JPanel pnlPayMethod = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlPayMethod.setOpaque(false);
        pnlPayMethod.add(new JLabel("Phương thức thanh toán:"));
        pnlPayMethod.add(chkTienMat = new JCheckBox("Tiền mặt", true));
        pnlPayMethod.add(chkChuyenKhoan = new JCheckBox("Chuyển khoản"));
        gbc.gridy = r++;
        pnlContent.add(pnlPayMethod, gbc);

        addInputRow(pnlContent, "Tiền trả lại:", txtTienTraLai = new JTextField("114.750Đ"), gbc, r++);

        // Nút Thanh Toán
        btnThanhToan = new JButton("Thanh Toán");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnThanhToan.setFocusPainted(false);
        btnThanhToan.setPreferredSize(new Dimension(0, 45));
        
        // Sự kiện Thanh toán và Trả hàng
        btnThanhToan.addActionListener(e -> {  
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có sản phẩm nào để trả hàng!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn hoàn tất giao dịch trả hàng này không?\nTổng tiền trả khách: " + txtTienTraLai.getText(), 
                "Xác nhận trả hàng", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                HoaDon hoaDonTra = new HoaDon();
                hoaDonTra.setThoiGianTao(java.time.LocalDateTime.now());
                hoaDonTra.setMaHoaDon(txtMaHoaDon.getText()); // Mã mới: HDT...
                
                if (hd != null) {
                    hoaDonTra.setKhachHang(hd.getKhachHang());
                }
                
                // Tạo đối tượng hóa đơn gốc và gán mã HD01 vào
                HoaDon hdGoc = new HoaDon();
                hdGoc.setMaHoaDon(txtMaHoaGoc.getText()); 
                hoaDonTra.setHoaDonDoiTra(hdGoc);
                hoaDonTra.setGhiChu(txtGhiChu.getText());
                
                com.example.entity.NhanVien nvTemp = new com.example.entity.NhanVien();
                nvTemp.setMaNhanVien("QL001"); // Tạm thời gán QL001 để test
                hoaDonTra.setNhanVien(nvTemp);
                
                // 2. Gom danh sách sản phẩm thực tế từ bảng vào hóa đơn
                List<ChiTietHoaDon> dsTra = new ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    String maSP = model.getValueAt(i, 0).toString();
                    int slTra = Integer.parseInt(model.getValueAt(i, 3).toString());
                    
                    // Tìm lại thông tin gốc để lấy Đơn vị quy đổi và Đơn giá
                    for (ChiTietHoaDon ctGoc : dsChiTietGoc) {
                        if (ctGoc.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                            ChiTietHoaDon ctMoi = new ChiTietHoaDon();
                            ctMoi.setDonViQuyDoi(ctGoc.getDonViQuyDoi());
                            ctMoi.setSoLuong(slTra);
                            ctMoi.setDonGia(ctGoc.getDonGia());
                            dsTra.add(ctMoi);
                            break;
                        }
                    }
                }
                hoaDonTra.setDsChiTiet(dsTra);

                // =========================================================================
                // BƯỚC BỔ SUNG: Truy vấn CSDL lấy danh sách các Lô đã bán của Hóa đơn gốc
                // để hoàn trả số lượng đúng vào Lô đó.
                // =========================================================================
                List<com.example.entity.SuPhanBoLo> dsPhanBoTra = new ArrayList<>();
                try {
                    java.sql.Connection con = com.example.connectDB.ConnectDB.getConnection();
                    // Lấy các lô mà hóa đơn gốc đã xuất cho một đơn vị sản phẩm cụ thể
                    String sql = "SELECT maLo, soLuong FROM SuPhanBoLo WHERE maHoaDon = ? AND maDonVi = ?";
                    java.sql.PreparedStatement ps = con.prepareStatement(sql);
                    
                    for (ChiTietHoaDon ctMoi : dsTra) {
                        int soLuongCanTra = ctMoi.getSoLuong();
                        
                        ps.setString(1, txtMaHoaGoc.getText().trim()); // Lấy mã hóa đơn gốc
                        ps.setString(2, ctMoi.getDonViQuyDoi().getMaDonVi());
                        
                        java.sql.ResultSet rs = ps.executeQuery();
                        
                        // Nếu 1 sản phẩm bị lấy từ 2 Lô khác nhau, vòng lặp này sẽ chia đúng số lượng trả về từng Lô
                        while (rs.next() && soLuongCanTra > 0) {
                            String maLoGoc = rs.getString("maLo");
                            int slGocTrongLo = rs.getInt("soLuong");
                            
                            // Tính toán số lượng trả vào lô này (không được vượt quá số lượng đã lấy từ lô)
                            int slTraVaoLo = Math.min(soLuongCanTra, slGocTrongLo);
                            
                            com.example.entity.SuPhanBoLo sp = new com.example.entity.SuPhanBoLo();
                            com.example.entity.Lo lo = new com.example.entity.Lo();
                            lo.setMaLo(maLoGoc);
                            sp.setLo(lo);
                            sp.setChiTietHoaDon(ctMoi);
                            sp.setSoLuong(slTraVaoLo); // Số lượng hoàn trả
                            
                            dsPhanBoTra.add(sp);
                            
                            soLuongCanTra -= slTraVaoLo; // Trừ đi phần đã trả vào lô này
                        }
                        rs.close();
                    }
                    ps.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // 3. Gọi DAO để lưu xuống SQL Server
                if (hoaDonDAO.luuHoaDonTraHang(hoaDonTra, dsPhanBoTra)) {
                    JOptionPane.showMessageDialog(TraHangPanel.this, "Thanh toán và hoàn kho thành công!");
                    // Xóa sạch dữ liệu trên giao diện để làm hóa đơn mới
                    lamMoiGiaoDien(); 
                } else {
                    JOptionPane.showMessageDialog(TraHangPanel.this, "Lỗi khi lưu dữ liệu vào hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        // 1. Kiểm tra xem hóa đơn này đã từng đổi trả lần nào chưa
        if (hoaDonDAO.daTungDoiTra(maHD)) {
            JOptionPane.showMessageDialog(this, 
                "Hóa đơn này đã thực hiện đổi/trả trước đó. Mỗi hóa đơn chỉ được đổi trả 01 lần duy nhất!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            lamMoiGiaoDien();
            return;
        }

        // 2. Lấy hóa đơn kèm kiểm tra điều kiện (Thanh toán = 1 và Hạn = 7 ngày)
        this.hd = hoaDonDAO.layHoaDonDeDoi(maHD);

        if (this.hd == null) {
            // Kiểm tra xem thực sự là không có mã này hay là do vi phạm điều kiện 7 ngày
            HoaDon hdCheck = hoaDonDAO.timTheoMa(maHD);
            if (hdCheck == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn có mã: " + maHD, "Lỗi", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Hóa đơn này không đủ điều kiện đổi trả!\n(Lý do: Có thể đã quá hạn 7 ngày hoặc chưa được thanh toán)", 
                    "Từ chối", JOptionPane.WARNING_MESSAGE);
            }
            lamMoiGiaoDien();
            return;
        }

        dsChiTietGoc = ctHDPDAO.layTheoMaHoaDon(maHD);
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
            model.addRow(new Object[]{
                ct.getDonViQuyDoi().getSanPham().getMaSanPham(),
                ct.getDonViQuyDoi().getSanPham().getTenSanPham(),
                ct.getDonViQuyDoi().getTenDonVi().getMoTa(),
                ct.getSoLuong(),
                df.format(ct.getDonGia()),
                df.format(ct.tinhTienThue()),
                df.format(ct.tinhThanhTien())
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

        // Cập nhật lên các ô nhập liệu bên phải
        txtTienTra.setText(df.format(tongTienTra));
        txtThue.setText(df.format(tongThueTra));
        txtThanhTien.setText(df.format(thanhTien));
        txtTienTraLai.setText(df.format(thanhTien));
        txtChenhLech.setText(df.format(thanhTien)); 
    }


    // =========================================================================
    // VÙNG 5: LOGIC LÀM MỚI (RESET LOGIC)
    // =========================================================================

    /** Làm mới toàn bộ giao diện về trạng thái rỗng */
    private void lamMoiGiaoDien() {
        txtSearch.setText("Mã hóa đơn");
        txtSearch.setForeground(Color.GRAY); // Trả về màu nhạt để gợi ý nhập tiếp
        
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
        txtChenhLech.setText(zero);
        
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

    /** Hỗ trợ thêm từng dòng input có nhãn vào Panel */
    private void addInputRow(JPanel pnl, String labelText, JTextField txt, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setOpaque(false);
        JLabel lbl = new JLabel(labelText);
        lbl.setPreferredSize(new Dimension(130, 25));
        rowPanel.add(lbl, BorderLayout.WEST);
        rowPanel.add(txt, BorderLayout.CENTER);
        pnl.add(rowPanel, gbc);
    }

    /** Cấu hình màu nền mờ cho các textfield chỉ đọc */
    private void setupStyles() {
        JTextField[] readonly = { txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang,
                txtTienGoc, txtTienTra, txtChenhLech, txtThue, txtThanhTien, txtTienTraLai };
        for (JTextField f : readonly) {
            f.setEditable(false);
            f.setBackground(new Color(200, 200, 200));
            f.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            f.setHorizontalAlignment(JTextField.LEFT);
        }
    }

    /** Hàm tự sinh mã hóa đơn trả định dạng HDTddMMyyXXX */
    private String tuSinhMaHoaDonTra() {
        String ngayThangNam = new java.text.SimpleDateFormat("ddMMyy").format(new java.util.Date());
        int soThuTuMoi = hoaDonDAO.laySoLuongHoaDonTrongNgay() + 1;
        return "HDT" + ngayThangNam + String.format("%03d", soThuTuMoi);
    }

    // ====================================================================
    // Lớp hỗ trợ tạo nút Tăng/Giảm (Spinner) cho cột Số lượng trong bảng
    // ====================================================================
    private class QuantitySpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private JSpinner spinner = new JSpinner();

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            String maSP = table.getValueAt(row, 0).toString();
            int soLuongGoc = 9999; 
            
            // Tìm số lượng tối đa khách đã mua trong hóa đơn gốc
            for (ChiTietHoaDon ct : dsChiTietGoc) {
                if (ct.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                    soLuongGoc = ct.getSoLuong();
                    break;
                }
            }
            
            int currentVal = 1;
            try {
                currentVal = Integer.parseInt(value.toString());
            } catch (Exception ex) {}

            // Cấu hình Spinner: (Giá trị hiện tại, Min = 1, Max = soLuongGoc, Bước nhảy = 1)
            spinner.setModel(new SpinnerNumberModel(currentVal, 1, soLuongGoc, 1));
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }
}