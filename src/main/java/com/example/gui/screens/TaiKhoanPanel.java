package com.example.gui.screens;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TaiKhoanPanel extends JPanel {
    public TaiKhoanPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Tài khoản", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }

}
