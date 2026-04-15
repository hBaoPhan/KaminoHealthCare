package com.example.gui.components;

import javax.swing.JPanel;
import java.awt.*;

/**
 * Panel bo góc dùng chung cho toàn bộ project.
 * Sử dụng thay thế JPanel ở mọi card/container cần bo góc.
 */
public class RoundedPanel extends JPanel {

    private int arc;
    private Color bgColor;
    private Color shadowColor;
    private boolean hasShadow;

    /** Constructor mặc định: bo góc 16px, không đổ bóng */
    public RoundedPanel() {
        this(16, false);
    }

    /** Constructor tùy chỉnh radius */
    public RoundedPanel(int arc) {
        this(arc, false);
    }

    /** Constructor tùy chỉnh radius + bóng đổ */
    public RoundedPanel(int arc, boolean hasShadow) {
        this.arc = arc;
        this.hasShadow = hasShadow;
        this.shadowColor = new Color(0, 0, 0, 20);
        setOpaque(false); // Phải false để vẽ bo góc đúng
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

        int w = getWidth();
        int h = getHeight();

        // Vẽ bóng đổ nhẹ
        if (hasShadow) {
            g2.setColor(shadowColor);
            g2.fillRoundRect(2, 3, w - 4, h - 3, arc, arc);
        }

        // Vẽ nền bo góc
        Color bg = (bgColor != null) ? bgColor : Color.WHITE;
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, w - 1, h - 1, arc, arc);

        g2.dispose();
        super.paintComponent(g);
    }
}
