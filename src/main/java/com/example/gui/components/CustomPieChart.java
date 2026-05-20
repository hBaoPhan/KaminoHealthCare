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
        boolean hasSpaceForLegend = width > 280;
        int legendWidth = hasSpaceForLegend ? 180 : 0;

        int size = Math.min(width - legendWidth - 30, height - 30);
        if (size < 50) size = 50;

        int cx = hasSpaceForLegend ? (width - legendWidth) / 2 : width / 2;
        int cy = height / 2;

        int x = cx - size / 2;
        int y = cy - size / 2;

        int startAngle = 90; // Start at top

        for (int i = 0; i < values.size(); i++) {
            double val = values.get(i);
            int arcAngle = (int) Math.round((val / total) * 360.0);

            // Draw slice
            g2.setColor(COLORS[i % COLORS.length]);
            g2.fillArc(x, y, size, size, startAngle, arcAngle);

            startAngle += arcAngle;
        }

        // Draw inner white circle for donut effect
        int innerSize = (int) (size * 0.55);
        int ix = cx - innerSize / 2;
        int iy = cy - innerSize / 2;
        g2.setColor(Color.WHITE);
        g2.fillOval(ix, iy, innerSize, innerSize);

        // Draw total in the middle
        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(120, 130, 140));
        String totalText = "TỔNG";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(totalText, cx - fm.stringWidth(totalText) / 2, cy - 3);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        g2.setColor(new Color(50, 60, 70));
        String totalValText = pctDf.format(total);
        if (total > 1000) {
            DecimalFormat df = new DecimalFormat("#,###");
            totalValText = df.format(total);
        }
        fm = g2.getFontMetrics();
        g2.drawString(totalValText, cx - fm.stringWidth(totalValText) / 2, cy + 11);

        // Draw Legend
        if (hasSpaceForLegend) {
            int legendX = width - legendWidth + 10;
            int itemHeight = 22;
            int legendHeight = values.size() * itemHeight;
            int legendY = Math.max(15, (height - legendHeight) / 2);

            for (int i = 0; i < values.size(); i++) {
                double val = values.get(i);
                double percentage = (val / total) * 100.0;
                String text = labels.get(i) + " (" + pctDf.format(percentage) + "%)";

                int itemY = legendY + i * itemHeight;

                // Draw color box
                g2.setColor(COLORS[i % COLORS.length]);
                g2.fillRoundRect(legendX, itemY + 4, 12, 12, 3, 3);

                // Draw text
                g2.setColor(new Color(70, 80, 90));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                // Truncate text if it's too long for the legend space
                String displayText = text;
                FontMetrics fmLegend = g2.getFontMetrics();
                int maxTextWidth = width - legendX - 25;
                if (fmLegend.stringWidth(displayText) > maxTextWidth) {
                    while (displayText.length() > 5 && fmLegend.stringWidth(displayText + "...") > maxTextWidth) {
                        displayText = displayText.substring(0, displayText.length() - 1);
                    }
                    displayText = displayText + "...";
                }

                g2.drawString(displayText, legendX + 20, itemY + 14);
            }
        }

        g2.dispose();
    }
}
