package com.example.gui.components;

import javax.swing.JPasswordField;
import java.awt.*;

/**
 * PasswordField bo góc dùng chung, hỗ trợ hint/placeholder.
 * Hiện hint dưới dạng text thường, ẩn ký tự khi người dùng bắt đầu nhập.
 */
public class RoundedPasswordField extends JPasswordField {

    private static final int ARC = 12;
    private final String hint;

    public RoundedPasswordField(String hint, int columns) {
        super(columns);
        this.hint = hint;
        init();
    }

    private void init() {
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(5, 15, 5, 45));
        setForeground(new Color(150, 150, 150));
        setText(hint);
        setEchoChar((char) 0); // Hiện hint dạng text bình thường

        addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (new String(getPassword()).equals(hint)) {
                    setText("");
                    setForeground(Color.BLACK);
                    setEchoChar('•');
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (new String(getPassword()).isEmpty()) {
                    setEchoChar((char) 0);
                    setForeground(new Color(150, 150, 150));
                    setText(hint);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Clip vùng vẽ thành hình bo góc — ngăn FlatLaf vẽ nền chữ nhật đè lên góc
        g2.clip(new java.awt.geom.RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), ARC, ARC));

        // Vẽ nền bo góc
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Truyền g2 đã clip cho super — FlatLaf sẽ bị giới hạn trong vùng bo góc
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(210, 210, 210));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        g2.dispose();
    }
}
