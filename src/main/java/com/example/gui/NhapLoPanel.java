package com.example.gui;

import javax.swing.*;
import java.awt.*;

public class NhapLoPanel extends JPanel {
    public NhapLoPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Nhập lô (Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
