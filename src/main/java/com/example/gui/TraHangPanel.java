package com.example.gui;

import javax.swing.*;
import java.awt.*;

public class TraHangPanel extends JPanel {
    public TraHangPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Trả hàng (Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
