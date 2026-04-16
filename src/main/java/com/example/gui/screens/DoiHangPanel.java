package com.example.gui.screens;

import javax.swing.*;
import java.awt.*;

public class DoiHangPanel extends JPanel {
    public DoiHangPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255)); // #F1F6FF
        JLabel label = new JLabel("Màn hình Đổi hàng (Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
