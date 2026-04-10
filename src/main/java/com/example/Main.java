package com.example;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.example.entity.ChucVu;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.example.gui.DangNhapPanel;
import com.example.gui.ThanhDieuHuong;
import com.formdev.flatlaf.FlatLightLaf;

public class Main {
    public static void main(String[] args) {
        setupLookAndFeel();
  
        SwingUtilities.invokeLater(() -> {
           DangNhapPanel mainFrame = new DangNhapPanel();
           mainFrame.setVisible(true);
        });
     }
  
     private static void setupLookAndFeel() {
        try {
           UIManager.setLookAndFeel(new FlatLightLaf());
           UIManager.put("Button.arc", 10);
           UIManager.put("Component.arc", 10);
           UIManager.put("TextComponent.arc", 10);
        } catch (Exception e) {
           e.printStackTrace();
        }
     }
  
     // Maintain constructor for compatibility if needed elsewhere
     public void setVisible(boolean visible) {
        if (visible)
           main(new String[] {});
     }
}