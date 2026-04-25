package com.example.gui.screens;

import com.example.gui.screens.*;
import com.example.gui.components.*;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import com.example.entity.ChucVu;
import com.example.entity.TaiKhoan;
import javax.swing.border.Border;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

@SuppressWarnings("serial")
public class ThanhDieuHuongPanel extends JFrame implements MouseListener, ActionListener {

	private JPanel contentPanel;
	private CardLayout cardLayout;
	private JLabel lblTenTaiKhoan;
	private JLabel lblChucVu;
	private Color textHoverColor = Color.decode("#1A73E8");
	private Color textDefaultColor = Color.BLACK;
	private Font customFont = new Font("Segoe UI", Font.BOLD, 12);
	private RoundedButton btnDangXuat;
	private RoundedButton btnMoCa;
	private RoundedButton btnKetCa;
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
			BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#174EA6")), // Blue accent line
			menuPadding);

	public ThanhDieuHuongPanel(TaiKhoan taiKhoan) {
		setTitle("Kamino Healthcare - Hệ Thống Quản Lý Nhà Thuốc");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel sidebar = new JPanel();
		sidebar.setBackground(sidebarColor);
		sidebar.setOpaque(true);
		sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
		sidebar.setAlignmentX(Component.CENTER_ALIGNMENT);

		JScrollPane scrollPane = new JScrollPane(sidebar);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Faster scrolling
		scrollPane.setPreferredSize(new Dimension(200, getHeight()));

		add(scrollPane, BorderLayout.WEST);

		ImageIcon logoData = loadImageIcon("/images/icon/logo.png");
		JLabel lblLogo = new JLabel(new HiDPIIcon(logoData.getImage(), 146, 146));
		lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblLogo.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
		sidebar.add(lblLogo);

		JLabel lblKAMINOCOFFEE = new JLabel("Kamino Healthcare");
		lblKAMINOCOFFEE.setFont(new Font(lblKAMINOCOFFEE.getFont().getFontName(), Font.BOLD, 17));

		lblTenTaiKhoan = new JLabel("Xin chào, " + taiKhoan.getNhanVien().getTenNhanVien());
		lblChucVu = new JLabel(taiKhoan.getNhanVien().getChucVu() == ChucVu.NHAN_VIEN_QUAN_LY ? "Quản Lý" : "Dược Sĩ");
		lblChucVu.setForeground(Color.decode("#00A651"));

		lblKAMINOCOFFEE.setBorder(BorderFactory.createEmptyBorder(10, 10, 7, 10));

		lblTenTaiKhoan.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		lblChucVu.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		lblKAMINOCOFFEE.setAlignmentX(Component.CENTER_ALIGNMENT);

		lblTenTaiKhoan.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblChucVu.setAlignmentX(Component.CENTER_ALIGNMENT);

		sidebar.add(lblKAMINOCOFFEE);
		sidebar.add(Box.createVerticalStrut(5));
		sidebar.add(lblTenTaiKhoan);
		sidebar.add(lblChucVu);
		sidebar.add(Box.createVerticalStrut(5));
		sidebar.add(Box.createVerticalStrut(10));

		this.sidebar = sidebar;
		initMenuStructure(taiKhoan);
		initLogoutPanel();
		renderSidebar();

		cardLayout = new CardLayout();

		contentPanel = new JPanel(cardLayout);
		contentPanel.add(pnlTrangChu = new ManHinhChinhPanel(taiKhoan), "Màn Hình Chính");
		contentPanel.add(pnlHoaDon = new HoaDonPanel(), "Quản Lý Hóa Đơn");
		contentPanel.add(new BanHangPanel(), "Bán Hàng");
		contentPanel.add(new DoiHangPanel(), "Đổi Hàng");
		contentPanel.add(new TraHangPanel(), "Trả Hàng");
		contentPanel.add(pnlKhachHang = new KhachHangPanel(), "Khách Hàng");
		isQuanLy = taiKhoan.getNhanVien().getChucVu() == ChucVu.NHAN_VIEN_QUAN_LY;
		contentPanel.add(new SanPhamPanel(), "Quản Lý Sản Phẩm");
		if (isQuanLy) {

			contentPanel.add(new LoPanel(), "Quản Lý Lô");
			contentPanel.add(new KhuyenMaiPanel(), "Khuyến Mãi"); // Tạo 1 KhuyenMaiPanel sau đó thay thế JPanel()
			contentPanel.add(new DonThuocPanel(), "Quản Lý Đơn Thuốc");

			contentPanel.add(pnlNhanVien = new NhanVienPanel(), "Quản Lý Nhân Viên");
			contentPanel.add(new TaiKhoanPanel(), "Quản Lý Tài Khoản"); // Tạo 1 TaiKhoanPanel sau đó thay thế JPanel()
			contentPanel.add(new CaLamPanel(), "Quản Lý Ca Làm");

			contentPanel.add(pnlThongKe = new ThongKePanel(), "Thống Kê");
		}
		contentPanel.add(new JPanel(), "Trợ Giúp");

		add(contentPanel, BorderLayout.CENTER);
	}

	private void initLogoutPanel() {
		// --- Nút Mở ca (xanh lá) ---
		btnMoCa = new RoundedButton("Mở Ca");
		btnMoCa.setBackground(Color.decode("#28A745"));
		btnMoCa.addActionListener(this);

		// --- Nút Kết ca (cam) ---
		btnKetCa = new RoundedButton("Kết Ca");
		btnKetCa.setBackground(Color.decode("#FD7E14"));
		btnKetCa.addActionListener(this);

		// --- Nút Đăng xuất (đỏ) ---
		btnDangXuat = new RoundedButton("Đăng Xuất");
		btnDangXuat.setBackground(Color.decode("#DC3545"));
		btnDangXuat.addActionListener(this);

		// --- Panel chứa 3 nút theo chiều dọc, tự căng full-width ---
		pLogout = new JPanel(new GridLayout(3, 1, 0, 6));
		pLogout.setBackground(sidebarColor);
		pLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
		pLogout.setBorder(new EmptyBorder(5, 20, 10, 20));

		pLogout.add(btnMoCa);
		pLogout.add(btnKetCa);
		pLogout.add(btnDangXuat);
	}

	private void capNhatDuLieuKhiDoiThe() {

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
				ml.setBorder(ml.getMenuItem().isChild ? BorderFactory.createEmptyBorder(10, 45, 10, 25) : menuPadding);
				ml.setBackground(sidebarColor);
			}
		}

		cardLayout.show(contentPanel, item.name);
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
					new DangNhapPanel().setVisible(true);
				});
				this.dispose();
			}
		} else if (o.equals(btnMoCa)) {
			JOptionPane.showMessageDialog(this,
					"Chức năng Mở ca đang được phát triển.",
					"Mở ca",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (o.equals(btnKetCa)) {
			JOptionPane.showMessageDialog(this,
					"Chức năng Kết ca đang được phát triển.",
					"Kết ca",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void switchTo(String action) {
		CardLayout layout = (CardLayout) contentPanel.getLayout();
		capNhatDuLieuKhiDoiThe();
		switch (action) {
			case "Màn hình chính" -> layout.show(contentPanel, "Màn Hình Chính");
			case "Quản lý hóa đơn" -> layout.show(contentPanel, "Hóa Đơn");
			case "Quản lý khách hàng" -> layout.show(contentPanel, "Khách Hàng");
			case "Xem thống kê" -> layout.show(contentPanel, "Thống Kê");
		}
	}

	private void initMenuStructure(TaiKhoan taiKhoan) {
		boolean isQL = taiKhoan.getNhanVien().getChucVu() == ChucVu.NHAN_VIEN_QUAN_LY;

		menuStructure.add(new MenuItem("Màn Hình Chính", "home.png"));

		MenuItem hoaDon = new MenuItem("Hóa Đơn", "invoice.png");
		hoaDon.children.add(new MenuItem("Quản Lý Hóa Đơn", null, true));
		hoaDon.children.add(new MenuItem("Bán Hàng", null, true));
		hoaDon.children.add(new MenuItem("Đổi Hàng", null, true));
		hoaDon.children.add(new MenuItem("Trả Hàng", null, true));
		menuStructure.add(hoaDon);

		MenuItem sanPham = new MenuItem("Sản Phẩm", "product.png");

		if (isQL) {
			sanPham.children.add(new MenuItem("Quản Lý Sản Phẩm", null, true));
			sanPham.children.add(new MenuItem("Quản Lý Lô", null, true));
			sanPham.children.add(new MenuItem("Quản Lý Đơn Thuốc", null, true));
		}

		menuStructure.add(sanPham);
		menuStructure.add(new MenuItem("Khuyến Mãi", "coupon.png"));

		menuStructure.add(new MenuItem("Khách Hàng", "customer.png"));

		if (isQL) {

			MenuItem nhanVien = new MenuItem("Nhân Viên", "staff.png");
			nhanVien.children.add(new MenuItem("Quản Lý Nhân Viên", null, true));
			nhanVien.children.add(new MenuItem("Quản Lý Tài Khoản", null, true));
			nhanVien.children.add(new MenuItem("Quản Lý Ca Làm", null, true));
			menuStructure.add(nhanVien);

			menuStructure.add(new MenuItem("Thống Kê", "chart.png"));
		}

		menuStructure.add(new MenuItem("Trợ Giúp", "help.png"));
	}

	private void renderSidebar() {
		// Cleanly remove all components after the header (logo, names, etc.)
		// Header items are at indices 0 to 6 (logo, name, chucVu, 4 spacers)
		Component[] components = sidebar.getComponents();
		for (int i = components.length - 1; i >= 6; i--) {
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

		// Re-add vertical glue and fixed logout section at the bottom
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
		// Indent children
		int leftPad = item.isChild ? 45 : 25;
		label.setBorder(BorderFactory.createEmptyBorder(10, leftPad, 10, 25));
		label.addMouseListener(this);
		label.setCursor(new Cursor(Cursor.HAND_CURSOR));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		label.setOpaque(true);
		label.setBackground(sidebarColor);

		if (!item.children.isEmpty()) {
			String arrow = item.isExpanded
					? "    <span style='font-size:7px'>▲</span>"
					: "    <span style='font-size:7px'>▼</span>";
			label.setText("<html>" + item.name + arrow + "</html>");
		}

		sidebar.add(label);
		sidebar.add(Box.createVerticalStrut(2));
		menuLabels.add(label);
	}

	private static class MenuItem {
		String name;
		String iconPath;
		boolean isChild;
		boolean isExpanded = false;
		List<MenuItem> children = new ArrayList<>();

		MenuItem(String name, String iconPath) {
			this(name, iconPath, false);
		}

		MenuItem(String name, String iconPath, boolean isChild) {
			this.name = name;
			this.iconPath = iconPath;
			this.isChild = isChild;
		}
	}

	private static class MenuLabel extends JLabel {
		private final MenuItem menuItem;

		MenuLabel(MenuItem item) {
			super(item.name);
			this.menuItem = item;
		}

		public MenuItem getMenuItem() {
			return menuItem;
		}
	}

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
			default -> null;
		};

		if (fileName != null) {
			try {
				Image img = loadImageIcon("/images/icon/" + fileName).getImage();
				return new HiDPIIcon(img, 26, 26);
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
				g2.setColor(Color.GRAY);
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

	private ImageIcon loadImageIcon(String classpathPath) {
		java.net.URL url = getClass().getResource(classpathPath);
		if (url != null) {
			return new ImageIcon(url);
		}
		// Fallback: load from working directory (when running via mvn exec:java from project root)
		String fsPath = "src/main/resources" + classpathPath;
		return new ImageIcon(fsPath);
	}

	// Class to handle HiDPI sharp rendering for images
	private static class HiDPIIcon implements Icon {
		private final Image image;
		private final int width;
		private final int height;

		public HiDPIIcon(Image image, int width, int height) {
			this.image = image;
			this.width = width;
			this.height = height;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(image, x, y, width, height, null);
			g2.dispose();
		}

		@Override
		public int getIconWidth() {
			return width;
		}

		@Override
		public int getIconHeight() {
			return height;
		}
	}
}
