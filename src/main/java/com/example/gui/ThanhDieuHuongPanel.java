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
import java.util.ArrayList;
import java.util.List;

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
import com.example.entity.KhuyenMai;
import com.example.entity.TaiKhoan;
import javax.swing.border.Border;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public class ThanhDieuHuongPanel extends JFrame implements MouseListener, ActionListener {

	private JPanel contentPanel;
	private CardLayout cardLayout;
	private JLabel lblTenTaiKhoan;
	private JLabel lblChucVu;
	private Color textHoverColor = Color.decode("#1A73E8");
	private Color textDefaultColor = Color.BLACK;
	private Font customFont = new Font("Segoe UI", Font.BOLD, 12);
	private JButton btnDangXuat;
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
	private Color selectedBg = new Color(0xD8F0F9);
	private Border selectedBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 5, 0, 0, Color.decode("#1A73E8")), // Blue accent line
			menuPadding);

	public ThanhDieuHuongPanel(TaiKhoan taiKhoan) {
		setTitle("Kamino Healthcare - Hệ Thống Quản Lý Nhà Thuốc");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel sidebar = new JPanel();
		sidebar.setBackground(Color.WHITE);
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

		ImageIcon logoData = new ImageIcon("src/main/resources/images/logo.png");
		JLabel lblLogo = new JLabel(new HiDPIIcon(logoData.getImage(), 146, 146));
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
		sidebar.add(Box.createVerticalStrut(5));
		sidebar.add(lblXinChao);
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
		contentPanel.add(pnlTrangChu = new ManHinhChinhPanel(taiKhoan), "Màn hình chính");
		contentPanel.add(pnlHoaDon = new HoaDonPanel(), "> QL hóa đơn");
		contentPanel.add(new BanHangPanel(), "> Bán hàng");
		contentPanel.add(new DoiHangPanel(), "> Đổi hàng");
		contentPanel.add(new TraHangPanel(), "> Trả hàng");
		contentPanel.add(pnlKhachHang = new KhachHangPanel(), "Khách hàng");
		isQuanLy = taiKhoan.getNhanVien().getChucVu() == ChucVu.NHANVIENQUANLY;
		contentPanel.add(new SanPhamPanel(), "> QL sản phẩm");
		if (isQuanLy) {
			
			contentPanel.add(new LoPanel(), "> QL lô");
			contentPanel.add(new KhuyenMaiPanel(), "Khuyến Mãi"); // Tạo 1 KhuyenMaiPanel sau đó thay thế JPanel()
            
            contentPanel.add(pnlNhanVien = new NhanVienPanel(), "> QL Nhân Viên");
            contentPanel.add(new TaiKhoanPanel(), "> QL Tài Khoản"); // Tạo 1 TaiKhoanPanel sau đó thay thế JPanel()

			contentPanel.add(pnlThongKe = new ThongKePanel(), "Thống Kê");
		}
		contentPanel.add(new JPanel(), "Trợ giúp");

		add(contentPanel, BorderLayout.CENTER);
	}

	private void initLogoutPanel() {
		btnDangXuat = new JButton("Đăng xuất");
		btnDangXuat.setBackground(Color.decode("#DC3545"));
		btnDangXuat.setForeground(Color.WHITE);
		btnDangXuat.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDangXuat.setBorder(BorderFactory.createLineBorder(Color.red, 5, true));
		btnDangXuat.addActionListener(this);

		pLogout = new JPanel();
		pLogout.setBackground(sidebarColor);
		pLogout.setLayout(new BorderLayout());
		pLogout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
		pLogout.add(btnDangXuat);
		pLogout.setBorder(new EmptyBorder(0, 20, 0, 20));
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
				ml.setBackground(Color.WHITE);
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

	private void initMenuStructure(TaiKhoan taiKhoan) {
		boolean isQL = taiKhoan.getNhanVien().getChucVu() == ChucVu.NHANVIENQUANLY;

		menuStructure.add(new MenuItem("Màn hình chính", "home.png"));

		MenuItem hoaDon = new MenuItem("Hóa đơn", "invoice.png");
		hoaDon.children.add(new MenuItem("> QL hóa đơn", null, true));
		hoaDon.children.add(new MenuItem("> Bán hàng", null, true));
		hoaDon.children.add(new MenuItem("> Đổi hàng", null, true));
		hoaDon.children.add(new MenuItem("> Trả hàng", null, true));
		menuStructure.add(hoaDon);

		MenuItem sanPham = new MenuItem("Sản phẩm", "product.png");
		


		if (isQL) {
			sanPham.children.add(new MenuItem("> QL sản phẩm", null, true));
			sanPham.children.add(new MenuItem("> QL lô", null, true));
		}

		menuStructure.add(sanPham);
		menuStructure.add(new MenuItem("Khuyến Mãi", "coupon.png")); 

		menuStructure.add(new MenuItem("Khách hàng", "customer.png"));

		if (isQL) {
			
			MenuItem nhanVien = new MenuItem("Nhân viên", "staff.png");
			nhanVien.children.add(new MenuItem("> QL Nhân Viên", null, true));
			nhanVien.children.add(new MenuItem("> QL Tài Khoản", null, true));
			menuStructure.add(nhanVien);

			menuStructure.add(new MenuItem("Thống Kê", "chart.png"));
		}

		menuStructure.add(new MenuItem("Trợ giúp", "help.png"));
	}

	private void renderSidebar() {
		// Cleanly remove all components after the header (logo, names, etc.)
		// Header items are at indices 0 to 7
		Component[] components = sidebar.getComponents();
		for (int i = components.length - 1; i >= 8; i--) {
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
		label.setBackground(Color.WHITE);

		if (!item.children.isEmpty()) {
			label.setText(item.name + (item.isExpanded ? "   >" : "   <"));
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
			case "Màn hình chính" -> "home.png";
			case "Hóa đơn" -> "invoice.png";
			case "Sản phẩm" -> "product.png";
			case "Khách hàng" -> "customer.png";
			case "Nhân viên" -> "staff.png";
			case "Thống Kê" -> "chart.png";
			case "Trợ giúp" -> "help.png";
			case "Khuyến Mãi" -> "coupon.png";
			default -> null;
		};

		if (fileName != null) {
			try {
				String path = "src/main/resources/images/" + fileName;
				Image img = new ImageIcon(path).getImage();
				return new HiDPIIcon(img, 24, 24);
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