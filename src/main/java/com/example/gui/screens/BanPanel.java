package com.example.gui.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.example.entity.TaiKhoan;

public class BanPanel extends JPanel {
    public BanPanel(TaiKhoan taiKhoan) {
        setLayout(new BorderLayout());
        setBackground(new Color(241, 246, 255)); // #F1F6FF
        JLabel label = new JLabel("Màn hình Quản lý Bàn", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
    public void capNhatDuLieuBanPanel() {}
}
