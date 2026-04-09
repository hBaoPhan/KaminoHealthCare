package com.example.gui;

import javax.swing.*;
import java.awt.*;

public class DoiHangPanel extends JPanel {
    public DoiHangPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Đổi hàng (Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
