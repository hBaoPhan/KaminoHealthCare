package com.example.gui.components;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CustomBarChart extends JComponent {

    private List<String> labels = new ArrayList<>();
    private List<Integer> values = new ArrayList<>();
    private final Color BAR_COLOR = new Color(0, 150, 214); // Cyan Blue
    private final Color GRID_COLOR = new Color(240, 240, 240);
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 10);
    private final Font VALUE_FONT = new Font("Segoe UI", Font.BOLD, 11);

    public CustomBarChart() {
        setBackground(Color.WHITE);
        setOpaque(true);
    }

    public void setValues(List<String> labels, List<Integer> values) {
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

        int leftMargin = 35;
        int rightMargin = 20;
        int topMargin = 25;
        int bottomMargin = 40;

        int chartWidth = width - leftMargin - rightMargin;
        int chartHeight = height - topMargin - bottomMargin;

        if (chartWidth <= 0 || chartHeight <= 0) {
            g2.dispose();
            return;
        }

        if (labels.isEmpty() || values.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("Không có dữ liệu", width / 2 - 40, height / 2);
            g2.dispose();
            return;
        }

        // Find max value
        int maxVal = 10;
        for (int v : values) {
            if (v > maxVal) maxVal = v;
        }

        // Round maxVal to a clean number
        maxVal = (int) (Math.ceil((double) maxVal / 10) * 10);

        // Draw horizontal grid lines and Y axis
        int numLines = 4;
        g2.setFont(LABEL_FONT);
        for (int i = 0; i <= numLines; i++) {
            double ratio = (double) i / numLines;
            int y = height - bottomMargin - (int) (ratio * chartHeight);
            int val = (int) (ratio * maxVal);

            g2.setColor(GRID_COLOR);
            g2.drawLine(leftMargin, y, width - rightMargin, y);

            g2.setColor(new Color(120, 130, 140));
            g2.drawString(String.valueOf(val), 5, y + 4);
        }

        // Draw Bars
        int numBars = values.size();
        int barGap = 20;
        int totalGaps = (numBars - 1) * barGap;
        int totalBarWidth = chartWidth - totalGaps;
        int barWidth = Math.max(15, totalBarWidth / numBars);

        for (int i = 0; i < numBars; i++) {
            int val = values.get(i);
            String label = labels.get(i);

            int x = leftMargin + i * (barWidth + barGap) + barGap / 2;
            int barH = (int) ((double) val / maxVal * chartHeight);
            int y = height - bottomMargin - barH;

            // Draw bar
            g2.setColor(BAR_COLOR);
            g2.fillRect(x, y, barWidth, barH);

            // Draw value text
            g2.setColor(new Color(50, 60, 70));
            g2.setFont(VALUE_FONT);
            FontMetrics fm = g2.getFontMetrics();
            String valStr = String.valueOf(val);
            g2.drawString(valStr, x + (barWidth - fm.stringWidth(valStr)) / 2, y - 6);

            // Draw label
            g2.setFont(LABEL_FONT);
            fm = g2.getFontMetrics();
            
            // Truncate label if too long
            String displayLabel = label;
            if (fm.stringWidth(displayLabel) > barWidth + barGap) {
                // Try wrapping or truncating
                if (displayLabel.length() > 10) {
                    displayLabel = displayLabel.substring(0, 8) + "..";
                }
            }
            
            int lblX = x + (barWidth - fm.stringWidth(displayLabel)) / 2;
            g2.setColor(new Color(80, 90, 100));
            g2.drawString(displayLabel, lblX, height - bottomMargin + 18);
        }

        g2.dispose();
    }
}
