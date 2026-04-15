package com.example.gui.screens;

import javax.swing.JLabel;
import javax.swing.JPanel;
import com.example.entity.TaiKhoan;

public class BanPanel extends JPanel {
    public BanPanel(TaiKhoan taiKhoan) {
        add(new JLabel("Màn hình Quản lý Bàn"));
    }
    public void capNhatDuLieuBanPanel() {}
}
