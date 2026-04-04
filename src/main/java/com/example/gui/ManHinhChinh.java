package com.example.gui;

import java.awt.Dimension;
import java.sql.SQLException;

import javax.swing.JFrame;

import com.example.connectDB.ConnectDB;

public class ManHinhChinh extends JFrame {

   public ManHinhChinh() {
      try {
         ConnectDB.getInstance().connect1();
      } catch (SQLException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      this.setSize(new Dimension(300, 400));
   }

}
