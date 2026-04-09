package com.example.gui;

import javax.swing.*;
import java.awt.*;

public class BanHangPanel extends JPanel {
    public BanHangPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Bán hàng (Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
