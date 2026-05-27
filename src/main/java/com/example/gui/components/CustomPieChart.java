package com.example.gui.components;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseEvent;

public class CustomPieChart extends JComponent {

    private List<String> labels = new ArrayList<>();
    private List<Double> values = new ArrayList<>();

    private boolean showTotal = true;
    private boolean showHover = true;

    private final Color[] COLORS = {
            new Color(0, 180, 240), // Cyan Blue
            new Color(10, 70, 120), // Dark Blue
            new Color(0, 140, 160), // Teal Blue
            new Color(110, 160, 70), // Olive Green
            new Color(240, 150, 40), // Golden Orange
            new Color(160, 80, 180) // Purple
    };

    private final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);
    private final DecimalFormat pctDf = new DecimalFormat("0.0");

    public CustomPieChart() {
        setBackground(Color.WHITE);
        setOpaque(true);
        setToolTipText(""); // Enable tooltips
    }

    public void setShowTotal(boolean showTotal) {
        this.showTotal = showTotal;
        repaint();
    }

    public void setShowHover(boolean showHover) {
        this.showHover = showHover;
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
            int size = Math.min(width - 30, height - 30);
            if (size < 50)
                size = 50;
            int cx = width / 2;
            int cy = height / 2;
            int x = cx - size / 2;
            int y = cy - size / 2;

            g2.setColor(new Color(230, 230, 230));
            g2.fillOval(x, y, size, size);

            int innerSize = (int) (size * 0.55);
            int ix = cx - innerSize / 2;
            int iy = cy - innerSize / 2;
            g2.setColor(Color.WHITE);
            g2.fillOval(ix, iy, innerSize, innerSize);
            FontMetrics fm;
            if (showTotal) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
                g2.setColor(new Color(120, 130, 140));
                String totalText = "TỔNG";
                fm = g2.getFontMetrics();
                g2.drawString(totalText, cx - fm.stringWidth(totalText) / 2, cy - 3);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                g2.setColor(new Color(50, 60, 70));
                String totalValText = "0";
                fm = g2.getFontMetrics();
                g2.drawString(totalValText, cx - fm.stringWidth(totalValText) / 2, cy + 11);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.setColor(Color.GRAY);
            String emptyTxt = "Không có dữ liệu";
            fm = g2.getFontMetrics();
            g2.drawString(emptyTxt, cx - fm.stringWidth(emptyTxt) / 2, y + size + 20);

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
        int maxLegendTextWidth = 0;
        FontMetrics fmLegendCalc = g2.getFontMetrics(new Font("Segoe UI", Font.PLAIN, 12));
        for (int i = 0; i < values.size(); i++) {
            double percentage = (values.get(i) / total) * 100.0;
            String text = labels.get(i) + " (" + pctDf.format(percentage) + "%)";
            int tw = fmLegendCalc.stringWidth(text);
            if (tw > maxLegendTextWidth)
                maxLegendTextWidth = tw;
        }

        int desiredLegendWidth = maxLegendTextWidth + 40; // Add padding for color box and margins
        boolean hasSpaceForLegend = width > desiredLegendWidth + 100;
        int legendWidth = hasSpaceForLegend ? desiredLegendWidth : 0;

        int size = Math.min(width - legendWidth - 30, height - 30);
        if (size < 50)
            size = 50;

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

        // Draw total in the middle if enabled
        if (showTotal) {
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
        }

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

    @Override
    public String getToolTipText(MouseEvent e) {
        if (!showHover || labels.isEmpty() || values.isEmpty())
            return null;

        double total = 0.0;
        for (double v : values)
            total += v;
        if (total == 0)
            return null;

        int width = getWidth();
        int height = getHeight();

        int maxLegendTextWidth = 0;
        FontMetrics fmLegendCalc = getFontMetrics(new Font("Segoe UI", Font.PLAIN, 12));
        for (int i = 0; i < values.size(); i++) {
            double percentage = (values.get(i) / total) * 100.0;
            String text = labels.get(i) + " (" + pctDf.format(percentage) + "%)";
            int tw = fmLegendCalc.stringWidth(text);
            if (tw > maxLegendTextWidth)
                maxLegendTextWidth = tw;
        }

        int desiredLegendWidth = maxLegendTextWidth + 40;
        boolean hasSpaceForLegend = width > desiredLegendWidth + 100;
        int legendWidth = hasSpaceForLegend ? desiredLegendWidth : 0;

        int size = Math.min(width - legendWidth - 30, height - 30);
        if (size < 50)
            size = 50;

        int cx = hasSpaceForLegend ? (width - legendWidth) / 2 : width / 2;
        int cy = height / 2;

        double radius = size / 2.0;
        double innerRadius = radius * 0.55;

        int mx = e.getX();
        int my = e.getY();

        double dx = mx - cx;
        double dy = my - cy;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist >= innerRadius && dist <= radius) {
            double angleRad = Math.atan2(-dy, dx);
            double angleDeg = Math.toDegrees(angleRad);
            if (angleDeg < 0)
                angleDeg += 360;

            double currentAngle = 90.0;
            for (int i = 0; i < values.size(); i++) {
                double val = values.get(i);
                double sweep = (val / total) * 360.0;

                double normalizedStart = currentAngle % 360;
                double normalizedEnd = (currentAngle + sweep) % 360;

                boolean inSlice = false;
                if (normalizedStart <= normalizedEnd) {
                    if (angleDeg >= normalizedStart && angleDeg <= normalizedEnd)
                        inSlice = true;
                } else {
                    if (angleDeg >= normalizedStart || angleDeg <= normalizedEnd)
                        inSlice = true;
                }

                if (inSlice) {
                    DecimalFormat df = new DecimalFormat("#,###.##");
                    return labels.get(i) + ": " + df.format(val);
                }
                currentAngle += sweep;
            }
        }

        return null;
    }
}
