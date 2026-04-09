package com.example.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.example.entity.ChucVu;
import com.example.entity.TaiKhoan;
import javax.swing.border.Border;
import javax.swing.Icon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class ThanhDieuHuong extends JFrame implements MouseListener, ActionListener {

	private JPanel contentPanel;
	private CardLayout cardLayout;
	private JLabel lblTenTaiKhoan;
	private JLabel lblChucVu;
	private Color textHoverColor = Color.decode("#e07b39"); // màu chữ khi hover
	private Color textDefaultColor = Color.BLACK;
	private Font customFont = new Font("Segoe UI", Font.BOLD, 12);
	private JButton btnDangXuat;
	private KhachHangPanel pnlKhachHang;
	private NhanVienPanel pnlNhanVien;
	private Color sidebarColor = Color.WHITE;
	private HoaDonPanel pnlHoaDon;
	private TrangChuPanel pnlTrangChu;
	private ThongKePanel pnlThongKe;
	private boolean isQuanLy;
	private JLabel[] labels;

	private Border menuPadding = BorderFactory.createEmptyBorder(10, 25, 10, 25);
	private Color selectedBg = Color.decode("#F7F4EC");
	private Border selectedBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#54ACD2")), // Simple left accent line
			menuPadding);

	public ThanhDieuHuong(TaiKhoan taiKhoan) {
		setTitle("Kamino Healthcare - Hệ Thống Quản Lý Nhà Thuốc");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel sidebar = new JPanel();
		sidebar.setBackground(Color.WHITE);
		sidebar.setOpaque(true);
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.setAlignmentX(Component.CENTER_ALIGNMENT);
		sidebar.setPreferredSize(new Dimension(200, getHeight()));
		add(sidebar, BorderLayout.WEST);

		ImageIcon LogoIcon = new ImageIcon("src/main/resources/images/logo.png");
		Image scaledImage = LogoIcon.getImage().getScaledInstance(146, 146, Image.SCALE_SMOOTH);
		ImageIcon resizedIcon = new ImageIcon(scaledImage);
		JLabel lblLogo = new JLabel(resizedIcon);
		lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
		sidebar.add(lblLogo);

		JLabel lblKAMINOCOFFEE = new JLabel("Kamino Healthcare");
		lblKAMINOCOFFEE.setFont(new Font(lblKAMINOCOFFEE.getFont().getFontName(), Font.BOLD, 17));

		JLabel lblXinChao = new JLabel("Xin chào,");
		lblTenTaiKhoan = new JLabel(taiKhoan.getNhanVien().getTenNhanVien());
		lblChucVu = new JLabel(taiKhoan.getNhanVien().getChucVu() == ChucVu.NHANVIENQUANLY ? "Quản Lý" : "Dược Sĩ");
		lblChucVu.setForeground(Color.decode("#00A651"));

		lblKAMINOCOFFEE.setBorder(BorderFactory.createEmptyBorder(10, 10, 7, 10));
		lblXinChao.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
		lblTenTaiKhoan.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		lblChucVu.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		lblKAMINOCOFFEE.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblXinChao.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTenTaiKhoan.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblChucVu.setAlignmentX(Component.CENTER_ALIGNMENT);

		sidebar.add(lblKAMINOCOFFEE);
		sidebar.add(Box.createVerticalStrut(10));
		sidebar.add(lblXinChao);
		sidebar.add(lblTenTaiKhoan);
		sidebar.add(lblChucVu);
		sidebar.add(Box.createVerticalStrut(10));
		sidebar.add(Box.createVerticalStrut(20));

		String[] tabs;
		if (taiKhoan.getNhanVien().getChucVu() == ChucVu.NHANVIENQUANLY) {
			tabs = new String[] { "Màn hình chính", "Hóa đơn", "Sản phẩm", "Khách hàng", "Nhân viên",
					"Thống Kê", "Trợ giúp" };
		} else {
			tabs = new String[] { "Màn hình chính", "Sản phẩm", "Khách hàng", "Trợ giúp" };
		}

		labels = new JLabel[tabs.length];

		for (int i = 0; i < tabs.length; i++) {
			final String tab = tabs[i];
			labels[i] = new JLabel(tab);
			labels[i].setFont(customFont);
			labels[i].setIcon(getIconForTab(tab));
			labels[i].setIconTextGap(15);
			labels[i].setBorder(menuPadding);
			labels[i].addMouseListener(this);
			labels[i].setCursor(new Cursor(Cursor.HAND_CURSOR));

			// Allow highlights to fill full width
			labels[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			labels[i].setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
			labels[i].setOpaque(true);
			labels[i].setBackground(Color.WHITE);

			sidebar.add(labels[i]);
			sidebar.add(Box.createVerticalStrut(2));
		}

		cardLayout = new CardLayout();

		contentPanel = new JPanel(cardLayout);
		contentPanel.add(pnlTrangChu = new TrangChuPanel(taiKhoan), "Màn hình chính");
		contentPanel.add(pnlHoaDon = new HoaDonPanel(), "Hóa đơn");
		contentPanel.add(pnlKhachHang = new KhachHangPanel(), "Khách hàng");
		isQuanLy = taiKhoan.getNhanVien().getChucVu() == ChucVu.NHANVIENQUANLY;
		if (isQuanLy) {
			contentPanel.add(new ThucDonPanel(), "Sản phẩm");
			contentPanel.add(pnlNhanVien = new NhanVienPanel(), "Nhân viên");
			contentPanel.add(pnlThongKe = new ThongKePanel(), "Thống Kê");
		}
		contentPanel.add(new JPanel(), "Trợ giúp"); // Temporary stub for Help panel

		sidebar.add(Box.createVerticalGlue());
		// 2. Định nghĩa nút Đăng xuất và đặt style
		btnDangXuat = new JButton("Đăng xuất");
		btnDangXuat.setBackground(Color.decode("#DC3545"));
		btnDangXuat.setForeground(Color.WHITE);
		btnDangXuat.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDangXuat.setBorder(BorderFactory.createLineBorder(Color.red, 5, true));

		// 3. Đặt nút vào Sidebar
		// Dùng JPanel để kiểm soát padding và màu nền xung quanh nút
		JPanel pLogout = new JPanel();
		pLogout.setBackground(sidebarColor);
		pLogout.setLayout(new BorderLayout());
		pLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		pLogout.add(btnDangXuat);
		pLogout.setBorder(new EmptyBorder(0, 20, 0, 20));

		sidebar.add(pLogout);
		sidebar.add(Box.createVerticalStrut(10)); // Khoảng đệm dưới cùng 10px

		// --- CardLayout (CENTER) (GIỮ NGUYÊN) ---

		btnDangXuat.addActionListener(this);
		add(contentPanel, BorderLayout.CENTER);

	}

	private void capNhatDuLieuKhiDoiThe() {
		// pnlBan.capNhatDuLieuBanPanel(); // Removed as Ban is no longer in tabs
		pnlKhachHang.taiLaiDanhSach();
		pnlHoaDon.taiLaiDanhSach();
		pnlTrangChu.loadThongKeData();
		pnlTrangChu.layDuLieuChoHoatDongGanDay();
		if (isQuanLy) {
			pnlNhanVien.taiLaiDanhSach();
			pnlThongKe.capNhatDuLieuThongKe();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		JLabel clickedLabel = (JLabel) e.getSource();
		String tabName = clickedLabel.getText();

		for (JLabel label : labels) {
			if (label == clickedLabel) {
				label.setBorder(selectedBorder);
				label.setBackground(selectedBg);
			} else {
				label.setBorder(menuPadding);
				label.setBackground(Color.WHITE);
			}
		}

		cardLayout.show(contentPanel, tabName);
		capNhatDuLieuKhiDoiThe();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		Component c = e.getComponent();
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			label.setForeground(textHoverColor);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		Component c = e.getComponent();
		if (c instanceof JLabel) {
			JLabel label = (JLabel) c;
			label.setForeground(textDefaultColor);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(btnDangXuat)) {
			int xacNhan = JOptionPane.showConfirmDialog(null,
					"Bạn có chắc chắn muốn đăng xuất?",
					"Xác nhận đăng xuất",
					JOptionPane.YES_NO_OPTION);

			if (xacNhan == JOptionPane.YES_OPTION) {

				EventQueue.invokeLater(() -> {
					new DangNhapGUI().setVisible(true);
				});
				this.dispose();

			}
		}

	}

	public void switchTo(String action) {
		CardLayout layout = (CardLayout) contentPanel.getLayout();
		capNhatDuLieuKhiDoiThe();
		switch (action) {
			case "Màn hình chính" -> layout.show(contentPanel, "Màn hình chính");
			case "Quản lý hóa đơn" -> layout.show(contentPanel, "Hóa đơn");
			case "Quản lý khách hàng" -> layout.show(contentPanel, "Khách hàng");
			case "Xem thống kê" -> layout.show(contentPanel, "Thống Kê");
		}
	}

	private Icon getIconForTab(String tabName) {
		String fileName = switch (tabName) {
			case "Màn hình chính" -> "home.png";
			case "Hóa đơn" -> "invoice.png";
			case "Sản phẩm" -> "product.png";
			case "Khách hàng" -> "customer.png";
			case "Nhân viên" -> "staff.png";
			case "Thống Kê" -> "chart.png";
			case "Trợ giúp" -> "help.png";
			default -> null;
		};

		if (fileName != null) {
			try {
				String path = "src/main/resources/images/" + fileName;
				ImageIcon icon = new ImageIcon(path);

				// High-quality scaling using Graphics2D
				int size = 24;
				BufferedImage scaledImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = scaledImg.createGraphics();

				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2.drawImage(icon.getImage(), 0, 0, size, size, null);
				g2.dispose();

				return new ImageIcon(scaledImg);
			} catch (Exception e) {
				System.err.println("Could not load icon: " + fileName);
			}
		}

		// Fallback to mock icons if file is missing
		return new Icon() {
			@Override
			public void paintIcon(Component c, Graphics g, int x, int y) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color color = Color.GRAY;
				g2.setColor(color);
				g2.fillRoundRect(x, y, 16, 16, 5, 5);
				g2.dispose();
			}

			@Override
			public int getIconWidth() {
				return 24;
			}

			@Override
			public int getIconHeight() {
				return 24;
			}
		};
	}
}