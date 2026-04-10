package com.example.gui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class KhuyenMaiPanel extends JPanel {
    public KhuyenMaiPanel() {
         setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Khuyến mãi", SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(label, BorderLayout.CENTER);
    }
    
}
