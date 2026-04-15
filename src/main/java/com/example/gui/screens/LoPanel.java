package com.example.gui.screens;

import javax.swing.*;
import java.awt.*;

public class LoPanel extends JPanel {
    public LoPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình lô (Đang phát triển)", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
}
