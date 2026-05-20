package com.example.gui.components;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomPieChart extends JComponent {

    private List<String> labels = new ArrayList<>();
    private List<Double> values = new ArrayList<>();

    private final Color[] COLORS = {
            new Color(0, 180, 240),   // Cyan Blue
            new Color(10, 70, 120),   // Dark Blue
            new Color(0, 140, 160),   // Teal Blue
            new Color(110, 160, 70),  // Olive Green
            new Color(240, 150, 40),  // Golden Orange
            new Color(160, 80, 180)   // Purple
    };

    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private final DecimalFormat pctDf = new DecimalFormat("0.0");

    public CustomPieChart() {
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    public void setValues(List<String> labels, List<Double> values) {
        this.labels = labels != null ? new ArrayList<>(labels) : new ArrayList<>();
        this.values = values != null ? new ArrayList<>(values) : new ArrayList<>();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(getBackground());
        g2.fillRect(0, 0, width, height);

        if (labels.isEmpty() || values.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("Không có dữ liệu", width / 2 - 40, height / 2);
            g2.dispose();
            return;
        }

        // Calculate total
        double total = 0.0;
        for (double v : values) {
            total += v;
        }

        if (total == 0) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("Không có số liệu", width / 2 - 40, height / 2);
            g2.dispose();
            return;
        }

        // Calculate dimensions
        int size = Math.min(width, height) - 70;
        int x = (width - size) / 2;
        int y = (height - size) / 2;

        int startAngle = 90; // Start at top

        for (int i = 0; i < values.size(); i++) {
            double val = values.get(i);
            int arcAngle = (int) Math.round((val / total) * 360.0);

            // Draw slice
            g2.setColor(COLORS[i % COLORS.length]);
            g2.fillArc(x, y, size, size, startAngle, arcAngle);

            // Draw label pointing lines
            double alpha = Math.toRadians(startAngle + arcAngle / 2.0);
            double cos = Math.cos(alpha);
            double sin = Math.sin(alpha);

            // Points
            int cx = width / 2;
            int cy = height / 2;
            int r = size / 2;

            int px = (int) (cx + cos * (r * 0.85));
            int py = (int) (cy - sin * (r * 0.85));

            int ex = (int) (cx + cos * (r + 15));
            int ey = (int) (cy - sin * (r + 15));

            // Draw indicator line
            g2.setColor(new Color(180, 190, 205));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(px, py, ex, ey);

            int textLineX = ex + (cos >= 0 ? 10 : -10);
            g2.drawLine(ex, ey, textLineX, ey);

            // Text Label
            g2.setFont(LABEL_FONT);
            g2.setColor(new Color(40, 50, 60));
            double percentage = (val / total) * 100.0;
            String text = labels.get(i) + " (" + pctDf.format(percentage) + "%)";
            FontMetrics fm = g2.getFontMetrics();

            int tx = textLineX + (cos >= 0 ? 5 : -fm.stringWidth(text) - 5);
            g2.drawString(text, tx, ey + 4);

            startAngle += arcAngle;
        }

        // Draw inner white circle for donut effect
        int innerSize = (int) (size * 0.55);
        int ix = (width - innerSize) / 2;
        int iy = (height - innerSize) / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(ix, iy, innerSize, innerSize);

        g2.dispose();
    }
}
