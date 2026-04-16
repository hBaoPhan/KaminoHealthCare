package com.example.gui.components;

import javax.swing.JButton;
import java.awt.*;

/**
 * Nút bấm bo góc dùng chung cho toàn bộ project.
 * Giữ nguyên màu nền, chữ trắng mặc định, bo góc 12px.
 */
public class RoundedButton extends JButton {

    private static final int ARC = 12;
    private Color bgColor;

    public RoundedButton(String text) {
        super(text);
        init();
    }

    private void init() {
        setOpaque(false);        // Tự vẽ nền
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void setBackground(Color bg) {
        this.bgColor = bg;
        super.setBackground(bg);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = (bgColor != null) ? bgColor : getBackground();

        // Hiệu ứng hover: tối hơn một chút khi nhấn
        if (getModel().isPressed()) {
            g2.setColor(bg.darker());
        } else if (getModel().isRollover()) {
            g2.setColor(bg.brighter());
        } else {
            g2.setColor(bg);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ARC, ARC);
        g2.dispose();

        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Không vẽ border mặc định, góc bo đã xử lý trong paintComponent
    }
}
