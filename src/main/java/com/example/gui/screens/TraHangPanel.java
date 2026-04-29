package com.example.gui.screens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.example.dao.ChiTietHoaDonDAO;
import com.example.dao.HoaDonDAO;
import com.example.entity.ChiTietHoaDon;
import com.example.entity.HoaDon;
import com.example.entity.NhanVien;
// Giả sử bạn có class quản lý phiên đăng nhập, hãy import nó vào
// import com.example.gui.LoginSession; 

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TraHangPanel extends JPanel {

    private JTextField txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang;
    private JTextField txtTienGoc, txtTienTra, txtChenhLech, txtThue, txtThanhTien, txtTienTraLai;
    private JTextField txtSearch;
    private JTextArea txtGhiChu;
    private JButton btnThanhToan;
    private JCheckBox chkTienMat, chkChuyenKhoan;

    private HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private ChiTietHoaDonDAO ctHDPDAO = new ChiTietHoaDonDAO();
    private DefaultTableModel model; 
    private List<ChiTietHoaDon> dsChiTietGoc = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("###,###,### VND");
    private HoaDon hd;

    public TraHangPanel() {
        setLayout(new BorderLayout(15, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));

        add(createTablePanel("Danh sách sản phẩm hóa đơn trả", "Mã hóa đơn"), BorderLayout.CENTER);
        add(createInfoPanel(), BorderLayout.EAST);
        lamMoiGiaoDien();
    }

    // ... (Các hàm createTablePanel giữ nguyên như bản cũ) ...

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
        addInputRow(pnlContent, "Mã hóa gốc", txtMaHoaGoc = new JTextField(), gbc, r++);
        addInputRow(pnlContent, "Mã hóa đơn", txtMaHoaDon = new JTextField(), gbc, r++);
        addInputRow(pnlContent, "Ngày tạo", txtNgayTao = new JTextField(), gbc, r++);
        
        // Hiển thị tên người đang trực ca/đăng nhập
        addInputRow(pnlContent, "Người tạo", txtNguoiTao = new JTextField(), gbc, r++);
        
        addInputRow(pnlContent, "Tên khách hàng", txtTenKhachHang = new JTextField(), gbc, r++);

        gbc.gridy = r++;
        pnlContent.add(new JLabel("Ghi chú"), gbc);
        gbc.gridy = r++;
        txtGhiChu = new JTextArea(4, 20);
        txtGhiChu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlContent.add(new JScrollPane(txtGhiChu), gbc);

        addInputRow(pnlContent, "Tiền hóa đơn gốc :", txtTienGoc = new JTextField("0 VND"), gbc, r++);
        addInputRow(pnlContent, "Tiền hóa đơn trả :", txtTienTra = new JTextField("0 VND"), gbc, r++);
        addInputRow(pnlContent, "Thành tiền :", txtThanhTien = new JTextField("0 VND"), gbc, r++);

        JPanel pnlPayMethod = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlPayMethod.setOpaque(false);
        pnlPayMethod.add(new JLabel("PTTT:"));
        pnlPayMethod.add(chkTienMat = new JCheckBox("Tiền mặt", true));
        pnlPayMethod.add(chkChuyenKhoan = new JCheckBox("Chuyển khoản"));
        gbc.gridy = r++;
        pnlContent.add(pnlPayMethod, gbc);

        addInputRow(pnlContent, "Tiền trả lại:", txtTienTraLai = new JTextField("0 VND"), gbc, r++);

        btnThanhToan = new JButton("Thanh Toán");
        btnThanhToan.setBackground(new Color(40, 167, 69));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        btnThanhToan.addActionListener(e -> {
            if (model.getRowCount() == 0 || this.hd == null) {
                JOptionPane.showMessageDialog(this, "Chưa có thông tin hóa đơn trả!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận thanh toán?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                HoaDon hoaDonTra = new HoaDon();
                hoaDonTra.setMaHoaDon(txtMaHoaDon.getText());
                hoaDonTra.setHoaDonDoiTra(this.hd);
                hoaDonTra.setKhachHang(this.hd.getKhachHang());
                hoaDonTra.setCa(this.hd.getCa());
                hoaDonTra.setGhiChu(txtGhiChu.getText());
                hoaDonTra.setTrangThaiThanhToan(true);

                // CÁCH DÙNG THÔNG TIN ĐĂNG NHẬP (Lấy từ Session hoặc biến tĩnh)
                // Giả sử class DangNhap đã lưu NhanVien vào biến static
                // NhanVien nvDangNhap = com.example.gui.DangNhap.nhanVienHienTai;
                // hoaDonTra.setNhanVien(nvDangNhap); 

                // Tạm thời nếu Trường chưa làm Session, hãy dùng QL001 để khớp với DB của bạn
                NhanVien nv = new NhanVien();
                nv.setMaNhanVien("QL001"); 
                hoaDonTra.setNhanVien(nv);

                if (chkChuyenKhoan.isSelected()) 
                    hoaDonTra.setPhuongThucThanhToan(com.example.entity.enums.PhuongThucThanhToan.CHUYEN_KHOAN);
                else 
                    hoaDonTra.setPhuongThucThanhToan(com.example.entity.enums.PhuongThucThanhToan.TIEN_MAT);

                // Nạp chi tiết hóa đơn (Phải nạp đầy đủ đơn vị quy đổi để tránh lỗi SQL)
                List<ChiTietHoaDon> dsTra = new ArrayList<>();
                for (int i = 0; i < model.getRowCount(); i++) {
                    String maSP = model.getValueAt(i, 0).toString();
                    int sl = Integer.parseInt(model.getValueAt(i, 3).toString());
                    for (ChiTietHoaDon ctGoc : dsChiTietGoc) {
                        if (ctGoc.getDonViQuyDoi().getSanPham().getMaSanPham().equals(maSP)) {
                            ChiTietHoaDon ctMoi = new ChiTietHoaDon();
                            ctMoi.setDonViQuyDoi(ctGoc.getDonViQuyDoi());
                            ctMoi.setSoLuong(sl);
                            ctMoi.setDonGia(ctGoc.getDonGia());
                            dsTra.add(ctMoi);
                            break;
                        }
                    }
                }
                hoaDonTra.setDsChiTiet(dsTra);

                if (hoaDonDAO.luuHoaDonTraHang(hoaDonTra)) {
                    JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                    lamMoiGiaoDien();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu! Kiểm tra khóa ngoại mã nhân viên.");
                }
            }
        });

        pnlMain.add(pnlContent, BorderLayout.CENTER);
        pnlMain.add(btnThanhToan, BorderLayout.SOUTH);
        setupStyles();
        return pnlMain;
    }

    private void hienThiSanPhamHoaDon(String maHD) {
        HoaDon hdMoi = hoaDonDAO.timTheoMa(maHD);
        if (hdMoi == null) { 
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!"); 
            return; 
        }

        // Kiểm tra thời hạn 7 ngày
        if (ChronoUnit.DAYS.between(hdMoi.getThoiGianTao(), LocalDateTime.now()) > 7) {
            JOptionPane.showMessageDialog(this, "Hóa đơn đã quá hạn đổi trả (7 ngày)!");
            return;
        }

        // Kiểm tra đã đổi trả chưa
        if (hoaDonDAO.kiemTraHoaDonDaDoiTra(maHD)) {
            JOptionPane.showMessageDialog(this, "Hóa đơn này đã được thực hiện đổi trả trước đó!");
            return;
        }

        this.hd = hdMoi;
        dsChiTietGoc = ctHDPDAO.layTheoMaHoaDon(maHD);
        
        txtMaHoaGoc.setText(hd.getMaHoaDon());
        txtTenKhachHang.setText(hd.getKhachHang() != null ? hd.getKhachHang().getTenKhachHang() : "Khách lẻ");
        txtTienGoc.setText(df.format(hd.tinhTongTienThanhToan()));
        txtMaHoaDon.setText(tuSinhMaHoaDonTra());
        txtNgayTao.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        // Gán tên nhân viên đang trực ca vào ô hiển thị
        txtNguoiTao.setText("Phan Hoài Bảo"); 

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

    // ... (Các hàm phụ trợ khác giữ nguyên) ...

    private void addInputRow(JPanel pnl, String label, JTextField txt, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        JPanel rPnl = new JPanel(new BorderLayout(10, 0));
        rPnl.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(130, 25));
        rPnl.add(lbl, BorderLayout.WEST);
        rPnl.add(txt, BorderLayout.CENTER);
        pnl.add(rPnl, gbc);
    }

    private void setupStyles() {
        JTextField[] flds = { txtMaHoaGoc, txtMaHoaDon, txtNgayTao, txtNguoiTao, txtTenKhachHang, txtTienGoc, txtTienTra, txtThanhTien, txtTienTraLai };
        for (JTextField f : flds) {
            f.setEditable(false);
            f.setBackground(new Color(235, 235, 235));
            f.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
    }

    private String tuSinhMaHoaDonTra() {
        String ntn = new java.text.SimpleDateFormat("ddMMyy").format(new java.util.Date());
        int stt = hoaDonDAO.laySoLuongHoaDonTrongNgay() + 1;
        return "HDT" + ntn + String.format("%03d", stt);
    }

    private void lamMoiGiaoDien() {
        txtSearch.setText("Mã hóa đơn");
        txtSearch.setForeground(Color.GRAY);
        txtMaHoaGoc.setText(""); txtMaHoaDon.setText(""); txtNgayTao.setText(""); txtTenKhachHang.setText("");
        model.setRowCount(0);
        this.hd = null;
    }
    
    private void tinhToanTienHoanTra() {
        double tong = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            int sl = Integer.parseInt(model.getValueAt(i, 3).toString());
            String val = model.getValueAt(i, 4).toString().replaceAll("[^0-9]", "");
            tong += sl * Double.parseDouble(val);
        }
        txtTienTra.setText(df.format(tong));
        txtThanhTien.setText(df.format(tong));
        txtTienTraLai.setText(df.format(tong));
    }
}