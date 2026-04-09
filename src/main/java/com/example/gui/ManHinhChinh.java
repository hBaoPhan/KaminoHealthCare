package com.example.gui;

import com.example.entity.ChucVu;
import com.example.entity.NhanVien;
import com.example.entity.TaiKhoan;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class ManHinhChinh {

   public static void main(String[] args) {
      setupLookAndFeel();

      // Mock data for display
      NhanVien nv = new NhanVien("NV001", "Hoài Bảo", "123456789", "0987654321", ChucVu.NHANVIENQUANLY, true);
      TaiKhoan tk = new TaiKhoan("admin", "admin", nv);

      SwingUtilities.invokeLater(() -> {
         ThanhDieuHuong mainFrame = new ThanhDieuHuong(tk);
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
