package com.example.gui.components;

import javax.swing.JTextField;
import java.awt.*;

/**
 * TextField bo góc dùng chung cho toàn bộ project.
 * Hỗ trợ placeholder text (hint), auto-clear khi focus.
 */
public class RoundedTextField extends JTextField {

    private static final int ARC = 12;
    private final String hint;

    /** Constructor không có hint */
    public RoundedTextField(int columns) {
        this("", columns);
    }

    /** Constructor với hint/placeholder */
    public RoundedTextField(String hint, int columns) {
        super(columns);
        this.hint = hint;
        init();
    }

    private void init() {
        setOpaque(false);
        setBorder(new javax.swing.border.EmptyBorder(5, 12, 5, 12));

        if (!hint.isEmpty()) {
            setForeground(new Color(150, 150, 150));
            setText(hint);

            addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent e) {
                    if (getText().equals(hint)) {
                        setText("");
                        setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(java.awt.event.FocusEvent e) {
                    if (getText().isEmpty()) {
                        setForeground(new Color(150, 150, 150));
                        setText(hint);
                    }
                }
            });
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Vẽ nền bo góc
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, ARC, ARC);
        g2.dispose();
        super.paintComponent(g);
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
