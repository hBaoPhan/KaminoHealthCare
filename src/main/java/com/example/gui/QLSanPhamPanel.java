package com.example.gui;

import javax.swing.*;
import java.awt.*;

public class QLSanPhamPanel extends JPanel {
    public QLSanPhamPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Quản lý Sản phẩm (Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
