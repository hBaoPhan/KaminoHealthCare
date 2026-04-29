package com.example.gui.screens;

import com.example.gui.components.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import com.example.entity.enums.ChucVu;
import com.example.entity.TaiKhoan;

@SuppressWarnings("serial")
public class ThanhDieuHuongPanel extends JFrame implements MouseListener, ActionListener {

	private JPanel contentPanel;
	private CardLayout cardLayout;
	private JLabel lblTenTaiKhoan;
	private JLabel lblChucVu;
	private Color textHoverColor = Color.decode("#1A73E8");
	private Color textDefaultColor = Color.BLACK;
	private Font customFont = new Font("Segoe UI", Font.BOLD, 12);
	private RoundedButton btnDangXuat, btnMoCa, btnKetCa;
	private KhachHangPanel pnlKhachHang;
	private NhanVienPanel pnlNhanVien;
	private Color sidebarColor = Color.WHITE;
	private HoaDonPanel pnlHoaDon;
	private ManHinhChinhPanel pnlTrangChu;
	private ThongKePanel pnlThongKe;
	private boolean isQuanLy;
	private List<MenuLabel> menuLabels = new ArrayList<>();
	private List<MenuItem> menuStructure = new ArrayList<>();
	private JPanel sidebar;
	private JPanel pLogout;

	private Border menuPadding = BorderFactory.createEmptyBorder(10, 25, 10, 25);
	private Color selectedBg = Color.decode("#D2E3FC");
	private Border selectedBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#174EA6")),
			menuPadding);
	private TroGiupPanel pnlTroGiup;

	public ThanhDieuHuongPanel(TaiKhoan taiKhoan) {
		// KIỂM TRA DỮ LIỆU ĐẦU VÀO ĐỂ TRÁNH LỖI NULL POINTER
		if (taiKhoan == null || taiKhoan.getNhanVien() == null) {
			JOptionPane.showMessageDialog(null, "Lỗi dữ liệu đăng nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
			return;
		}

		setTitle("Kamino Healthcare - Hệ Thống Quản Lý Nhà Thuốc");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		sidebar = new JPanel();
		sidebar.setBackground(sidebarColor);
		sidebar.setOpaque(true);
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

		JScrollPane scrollPane = new JScrollPane(sidebar);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(220, getHeight()));

		add(scrollPane, BorderLayout.WEST);

		// --- PHẦN HEADER SIDEBAR (LOGO + USER INFO) ---
		ImageIcon logoData = loadImageIcon("/images/icon/logo.png");
		JLabel lblLogo = new JLabel(new HiDPIIcon(logoData.getImage(), 130, 130));
		lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 10, 0, 10));
		sidebar.add(lblLogo);

		JLabel lblKAMINO = new JLabel("Kamino Healthcare");
		lblKAMINO.setFont(new Font("Segoe UI", Font.BOLD, 17));
		lblKAMINO.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblKAMINO.setBorder(BorderFactory.createEmptyBorder(10, 10, 7, 10));
		sidebar.add(lblKAMINO);

		lblTenTaiKhoan = new JLabel("Xin chào, " + taiKhoan.getNhanVien().getTenNhanVien());
		lblTenTaiKhoan.setFont(new Font("Segoe UI", Font.PLAIN, 13));
		lblTenTaiKhoan.setAlignmentX(Component.CENTER_ALIGNMENT);
		sidebar.add(lblTenTaiKhoan);

		isQuanLy = taiKhoan.getNhanVien().getChucVu() == ChucVu.NHAN_VIEN_QUAN_LY;
		lblChucVu = new JLabel(isQuanLy ? "Quản Lý" : "Dược Sĩ");
		lblChucVu.setForeground(Color.decode("#00A651"));
		lblChucVu.setFont(new Font("Segoe UI", Font.BOLD, 12));
		lblChucVu.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblChucVu.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
		sidebar.add(lblChucVu);

		sidebar.add(Box.createVerticalStrut(10));

		// --- KHỞI TẠO CẤU TRÚC MENU ---
		initMenuStructure(taiKhoan);
		initLogoutPanel();
		renderSidebar();

		// --- PHẦN NỘI DUNG CHÍNH (CARD LAYOUT) ---
		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		
		contentPanel.add(pnlTrangChu = new ManHinhChinhPanel(taiKhoan), "Màn Hình Chính");
		contentPanel.add(pnlHoaDon = new HoaDonPanel(), "Quản Lý Hóa Đơn");
		contentPanel.add(new BanHangPanel(taiKhoan), "Bán Hàng");
		contentPanel.add(new DoiHangPanel(taiKhoan), "Đổi Hàng");
		contentPanel.add(new TraHangPanel(), "Trả Hàng");
		contentPanel.add(pnlKhachHang = new KhachHangPanel(), "Khách Hàng");
		contentPanel.add(new SanPhamPanel(), "Quản Lý Sản Phẩm");
		
		if (isQuanLy) {
			contentPanel.add(new LoPanel(), "Quản Lý Lô");
			contentPanel.add(new KhuyenMaiPanel(), "Khuyến Mãi");
			contentPanel.add(new DonThuocPanel(), "Quản Lý Đơn Thuốc");
			contentPanel.add(pnlNhanVien = new NhanVienPanel(), "Quản Lý Nhân Viên");
			contentPanel.add(new TaiKhoanPanel(), "Quản Lý Tài Khoản");
			contentPanel.add(new CaLamPanel(), "Quản Lý Ca Làm");
			contentPanel.add(pnlThongKe = new ThongKePanel(), "Thống Kê");
		}
		contentPanel.add(pnlTroGiup = new TroGiupPanel(), "Trợ Giúp");

		add(contentPanel, BorderLayout.CENTER);
	}

	private void initLogoutPanel() {
		btnMoCa = new RoundedButton("Mở Ca");
		btnMoCa.setBackground(Color.decode("#28A745"));
		btnMoCa.addActionListener(this);

		btnKetCa = new RoundedButton("Kết Ca");
		btnKetCa.setBackground(Color.decode("#FD7E14"));
		btnKetCa.addActionListener(this);

		btnDangXuat = new RoundedButton("Đăng Xuất");
		btnDangXuat.setBackground(Color.decode("#DC3545"));
		btnDangXuat.addActionListener(this);

		pLogout = new JPanel(new GridLayout(3, 1, 0, 6));
		pLogout.setBackground(sidebarColor);
		pLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
		pLogout.setBorder(new EmptyBorder(5, 20, 10, 20));

		pLogout.add(btnMoCa);
		pLogout.add(btnKetCa);
		pLogout.add(btnDangXuat);
	}

	private void renderSidebar() {
		// GIỮ LẠI HEADER (LOGO + INFO), XÓA CÁC MỤC MENU CŨ
		// Index 0: Logo, 1: Name, 2: Welcome, 3: ChucVu, 4: Spacer
		for (int i = sidebar.getComponentCount() - 1; i >= 5; i--) {
			sidebar.remove(i);
		}
		menuLabels.clear();

		for (MenuItem item : menuStructure) {
			addMenuItemToSidebar(item);
			if (item.isExpanded) {
				for (MenuItem child : item.children) {
					addMenuItemToSidebar(child);
				}
			}
		}

		sidebar.add(Box.createVerticalGlue());
		sidebar.add(pLogout);
		sidebar.add(Box.createVerticalStrut(10));

		sidebar.revalidate();
		sidebar.repaint();
	}

	private void addMenuItemToSidebar(MenuItem item) {
		MenuLabel label = new MenuLabel(item);
		label.setFont(customFont);
		if (item.iconPath != null) {
			label.setIcon(getIconForTab(item.name));
		}
		label.setIconTextGap(15);
		int leftPad = item.isChild ? 50 : 25;
		label.setBorder(BorderFactory.createEmptyBorder(10, leftPad, 10, 25));
		label.addMouseListener(this);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
		label.setOpaque(true);
		label.setBackground(sidebarColor);

		if (!item.children.isEmpty()) {
			String arrow = item.isExpanded ? "   ▲" : "   ▼";
			label.setText("<html>" + item.name + "<span style='font-size:8px;'>" + arrow + "</span></html>");
		}

		sidebar.add(label);
		sidebar.add(Box.createVerticalStrut(2));
		menuLabels.add(label);
	}

	private void capNhatDuLieuKhiDoiThe() {
		if (pnlKhachHang != null) pnlKhachHang.taiLaiDanhSach();
		if (pnlHoaDon != null) pnlHoaDon.taiLaiDanhSach();
		if (pnlTrangChu != null) {
			pnlTrangChu.loadThongKeData();
			pnlTrangChu.layDuLieuChoHoatDongGanDay();
		}
		if (isQuanLy && pnlNhanVien != null) {
			pnlNhanVien.taiLaiDanhSach();
			pnlThongKe.capNhatDuLieuThongKe();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		MenuLabel clickedLabel = (MenuLabel) e.getSource();
		MenuItem item = clickedLabel.getMenuItem();

		if (!item.children.isEmpty()) {
			item.isExpanded = !item.isExpanded;
			renderSidebar();
			return;
		}

		for (MenuLabel ml : menuLabels) {
			if (ml == clickedLabel) {
				ml.setBorder(selectedBorder);
				ml.setBackground(selectedBg);
			} else {
				int pad = ml.getMenuItem().isChild ? 50 : 25;
				ml.setBorder(BorderFactory.createEmptyBorder(10, pad, 10, 25));
				ml.setBackground(sidebarColor);
			}
		}

		cardLayout.show(contentPanel, item.name);
		capNhatDuLieuKhiDoiThe();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if (o.equals(btnDangXuat)) {
			if (JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đăng xuất?", "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
				new DangNhapPanel().setVisible(true);
				this.dispose();
			}
		} else if (o.equals(btnMoCa)) {
			JOptionPane.showMessageDialog(this, "Chức năng Mở ca đang được phát triển.");
		} else if (o.equals(btnKetCa)) {
			JOptionPane.showMessageDialog(this, "Chức năng Kết ca đang được phát triển.");
		}
	}

	private void initMenuStructure(TaiKhoan taiKhoan) {
		menuStructure.add(new MenuItem("Màn Hình Chính", "home.png"));

		MenuItem hoaDon = new MenuItem("Hóa Đơn", "invoice.png");
		hoaDon.children.add(new MenuItem("Quản Lý Hóa Đơn", null, true));
		hoaDon.children.add(new MenuItem("Bán Hàng", null, true));
		hoaDon.children.add(new MenuItem("Đổi Hàng", null, true));
		hoaDon.children.add(new MenuItem("Trả Hàng", null, true));
		menuStructure.add(hoaDon);

		MenuItem sanPham = new MenuItem("Sản Phẩm", "product.png");
		sanPham.children.add(new MenuItem("Quản Lý Sản Phẩm", null, true));
		if (isQuanLy) {
			sanPham.children.add(new MenuItem("Quản Lý Lô", null, true));
			sanPham.children.add(new MenuItem("Quản Lý Đơn Thuốc", null, true));
		}
		menuStructure.add(sanPham);

		menuStructure.add(new MenuItem("Khuyến Mãi", "coupon.png"));
		menuStructure.add(new MenuItem("Khách Hàng", "customer.png"));

		if (isQuanLy) {
			MenuItem nhanVien = new MenuItem("Nhân Viên", "staff.png");
			nhanVien.children.add(new MenuItem("Quản Lý Nhân Viên", null, true));
			nhanVien.children.add(new MenuItem("Quản Lý Tài Khoản", null, true));
			nhanVien.children.add(new MenuItem("Quản Lý Ca Làm", null, true));
			menuStructure.add(nhanVien);
			menuStructure.add(new MenuItem("Thống Kê", "chart.png"));
		}
		menuStructure.add(new MenuItem("Trợ Giúp", "help.png"));
	}

	// --- GIỮ NGUYÊN CÁC LỚP HELPER ICON VÀ RENDERING CỦA BẠN ---
	private Icon getIconForTab(String tabName) {
		String fileName = switch (tabName) {
			case "Màn Hình Chính" -> "home.png";
			case "Hóa Đơn" -> "invoice.png";
			case "Sản Phẩm" -> "product.png";
			case "Khách Hàng" -> "customer.png";
			case "Nhân Viên" -> "staff.png";
			case "Thống Kê" -> "chart.png";
			case "Trợ Giúp" -> "help.png";
			case "Khuyến Mãi" -> "coupon.png";
			default -> "dot.png";
		};

		ImageIcon icon = loadImageIcon("/images/icon/" + fileName);
		return (icon != null) ? new HiDPIIcon(icon.getImage(), 24, 24) : null;
	}

	private ImageIcon loadImageIcon(String classpathPath) {
		java.net.URL url = getClass().getResource(classpathPath);
		return (url != null) ? new ImageIcon(url) : new ImageIcon("src/main/resources" + classpathPath);
	}

	private static class MenuItem {
		String name, iconPath;
		boolean isChild, isExpanded = false;
		List<MenuItem> children = new ArrayList<>();
		MenuItem(String name, String iconPath) { this(name, iconPath, false); }
		MenuItem(String name, String iconPath, boolean isChild) {
			this.name = name; this.iconPath = iconPath; this.isChild = isChild;
		}
	}

	private static class MenuLabel extends JLabel {
		private final MenuItem menuItem;
		MenuLabel(MenuItem item) { super(item.name); this.menuItem = item; }
		public MenuItem getMenuItem() { return menuItem; }
	}

	private static class HiDPIIcon implements Icon {
		private final Image image;
		private final int width, height;
		public HiDPIIcon(Image image, int width, int height) {
			this.image = image; this.width = width; this.height = height;
		}
		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.drawImage(image, x, y, width, height, null);
			g2.dispose();
		}
		@Override public int getIconWidth() { return width; }
		@Override public int getIconHeight() { return height; }
	}

	@Override public void mousePressed(MouseEvent e) {}
	@Override public void mouseReleased(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) { if (e.getSource() instanceof JLabel) ((JLabel)e.getSource()).setForeground(textHoverColor); }
	@Override public void mouseExited(MouseEvent e) { if (e.getSource() instanceof JLabel) ((JLabel)e.getSource()).setForeground(textDefaultColor); }
}