package com.example.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomLineChart extends JComponent {

    private List<String> xLabels = new ArrayList<>();
    private List<Double> revenueData = new ArrayList<>();
    private List<Double> profitData = new ArrayList<>();

    private int hoveredIndex = -1;
    private final DecimalFormat df = new DecimalFormat("#,###");

    private final Color COLOR_REVENUE_LINE = new Color(135, 206, 235); // Light Blue
    private final Color COLOR_REVENUE_FILL = new Color(135, 206, 235, 40);
    private final Color COLOR_PROFIT_LINE = new Color(0, 150, 180); // Teal Blue
    private final Color COLOR_PROFIT_FILL = new Color(0, 150, 180, 40);

    private final Font FONT_AXIS = new Font("Segoe UI", Font.PLAIN, 11);
    private final Font FONT_TOOLTIP = new Font("Segoe UI", Font.BOLD, 12);

    public CustomLineChart() {
        setBackground(Color.WHITE);
        setOpaque(true);

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = findNearestIndex(e.getPoint());
                if (index != hoveredIndex) {
                    hoveredIndex = index;
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredIndex = -1;
                repaint();
            }
        });
    }

    public void setValues(List<String> labels, List<Double> revenues, List<Double> profits) {
        this.xLabels = labels != null ? new ArrayList<>(labels) : new ArrayList<>();
        this.revenueData = revenues != null ? new ArrayList<>(revenues) : new ArrayList<>();
        this.profitData = profits != null ? new ArrayList<>(profits) : new ArrayList<>();
        this.hoveredIndex = -1;
        repaint();
    }

    private int findNearestIndex(Point p) {
        if (xLabels.isEmpty()) return -1;
        int size = xLabels.size();
        int leftMargin = 85;
        int rightMargin = 40;
        int chartWidth = getWidth() - leftMargin - rightMargin;
        if (chartWidth <= 0) return -1;

        double step = (double) chartWidth / (size > 1 ? (size - 1) : 1);
        int bestIdx = -1;
        double bestDist = Double.MAX_VALUE;

        for (int i = 0; i < size; i++) {
            double x = leftMargin + i * step;
            double dist = Math.abs(p.x - x);
            if (dist < bestDist) {
                bestDist = dist;
                bestIdx = i;
            }
        }

        if (bestDist < 25) {
            return bestIdx;
        }
        return -1;
    }

    private boolean isCustomerRegistrationChart() {
        if (profitData.isEmpty()) return true;
        for (double d : profitData) {
            if (d != 0.0) return false;
        }
        return true;
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

        int leftMargin = 85;
        int rightMargin = 40;
        int topMargin = 30;
        int bottomMargin = 40;

        int chartWidth = width - leftMargin - rightMargin;
        int chartHeight = height - topMargin - bottomMargin;

        if (chartWidth <= 0 || chartHeight <= 0) {
            g2.dispose();
            return;
        }

        // Calculate max value for Y scaling
        double maxVal = 10; // default minimum max value
        for (double v : revenueData) {
            if (v > maxVal) maxVal = v;
        }
        for (double v : profitData) {
            if (v > maxVal) maxVal = v;
        }

        // Round maxVal to a nice clean number (e.g. multiples of 1M, 5M, 10M, etc.)
        double basePower = Math.pow(10, Math.floor(Math.log10(maxVal)));
        if (basePower < 1) basePower = 1;
        double factor = Math.ceil(maxVal / basePower);
        maxVal = factor * basePower;

        // Draw horizontal grid lines and Y-axis labels
        int numGridLines = 4;
        g2.setFont(FONT_AXIS);
        for (int i = 0; i <= numGridLines; i++) {
            double ratio = (double) i / numGridLines;
            int y = height - bottomMargin - (int) (ratio * chartHeight);
            double val = ratio * maxVal;

            // Grid Line
            g2.setColor(new Color(235, 238, 243));
            if (i > 0 && i < numGridLines) {
                // Dashed line
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{5.0f}, 0.0f));
                g2.drawLine(leftMargin, y, width - rightMargin, y);
                g2.setStroke(oldStroke);
            } else {
                g2.drawLine(leftMargin, y, width - rightMargin, y);
            }

            // Y-Label
            String label = isCustomerRegistrationChart() ? String.valueOf((int) val) : df.format(val);
            g2.setColor(new Color(120, 130, 140));
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(label);
            g2.drawString(label, leftMargin - labelWidth - 10, y + 4);
        }

        if (xLabels.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("Không có dữ liệu hiển thị", leftMargin + chartWidth / 2 - 60, topMargin + chartHeight / 2);
            g2.dispose();
            return;
        }

        // Map data points
        int size = xLabels.size();
        double step = (double) chartWidth / (size > 1 ? (size - 1) : 1);

        List<Point2D> revenuePoints = new ArrayList<>();
        List<Point2D> profitPoints = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double x = leftMargin + i * step;
            
            double revVal = i < revenueData.size() ? revenueData.get(i) : 0.0;
            double revY = height - bottomMargin - (revVal / maxVal * chartHeight);
            revenuePoints.add(new Point2D.Double(x, revY));

            double prfVal = i < profitData.size() ? profitData.get(i) : 0.0;
            double prfY = height - bottomMargin - (prfVal / maxVal * chartHeight);
            profitPoints.add(new Point2D.Double(x, prfY));
        }

        // Draw X-axis labels
        int maxLabels = 8;
        int skip = Math.max(1, size / maxLabels);
        for (int i = 0; i < size; i += skip) {
            String xLabel = xLabels.get(i);
            FontMetrics fm = g2.getFontMetrics();
            int labelWidth = fm.stringWidth(xLabel);
            int x = (int) (leftMargin + i * step) - labelWidth / 2;
            g2.setColor(new Color(120, 130, 140));
            g2.drawString(xLabel, x, height - bottomMargin + 18);
        }

        int bottomY = height - bottomMargin;

        // Draw Revenue Curve
        drawSmoothCurve(g2, revenuePoints, COLOR_REVENUE_LINE, COLOR_REVENUE_FILL, bottomY);

        // Draw Profit Curve
        if (!isCustomerRegistrationChart()) {
            drawSmoothCurve(g2, profitPoints, COLOR_PROFIT_LINE, COLOR_PROFIT_FILL, bottomY);
        }

        // Renders vertical indicator line and tooltips on hover
        if (hoveredIndex >= 0 && hoveredIndex < size) {
            double hoverX = leftMargin + hoveredIndex * step;

            // Draw vertical guide line
            g2.setColor(new Color(180, 190, 205));
            Stroke oldStroke = g2.getStroke();
            g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{3.0f}, 0.0f));
            g2.drawLine((int) hoverX, topMargin, (int) hoverX, bottomY);
            g2.setStroke(oldStroke);

            // Draw markers on both lines
            double revY = revenuePoints.get(hoveredIndex).getY();
            double prfY = profitPoints.get(hoveredIndex).getY();

            // Revenue point marker
            g2.setColor(COLOR_REVENUE_LINE);
            g2.fillOval((int) hoverX - 5, (int) revY - 5, 10, 10);
            g2.setColor(Color.WHITE);
            g2.fillOval((int) hoverX - 3, (int) revY - 3, 6, 6);

            // Profit point marker
            if (!isCustomerRegistrationChart()) {
                g2.setColor(COLOR_PROFIT_LINE);
                g2.fillOval((int) hoverX - 5, (int) prfY - 5, 10, 10);
                g2.setColor(Color.WHITE);
                g2.fillOval((int) hoverX - 3, (int) prfY - 3, 6, 6);
            }

            // Renders tooltip panel
            drawTooltip(g2, (int) hoverX, (int) Math.min(revY, prfY), hoveredIndex);
        }

        g2.dispose();
    }

    private void drawSmoothCurve(Graphics2D g2, List<Point2D> points, Color lineColor, Color fillColor, int bottomY) {
        if (points.isEmpty()) return;

        Path2D.Double path = new Path2D.Double();
        path.moveTo(points.get(0).getX(), points.get(0).getY());

        if (points.size() == 1) {
            path.lineTo(points.get(0).getX() + 5, points.get(0).getY());
        } else {
            // Spline interpolation
            for (int i = 0; i < points.size() - 1; i++) {
                Point2D p0 = points.get(Math.max(i - 1, 0));
                Point2D p1 = points.get(i);
                Point2D p2 = points.get(i + 1);
                Point2D p3 = points.get(Math.min(i + 2, points.size() - 1));

                for (int t = 1; t <= 10; t++) {
                    double factor = t / 10.0;
                    double x = catmullRom(p0.getX(), p1.getX(), p2.getX(), p3.getX(), factor);
                    double y = catmullRom(p0.getY(), p1.getY(), p2.getY(), p3.getY(), factor);
                    path.lineTo(x, y);
                }
            }
        }

        // Paint Area Fill
        Path2D.Double fillPath = (Path2D.Double) path.clone();
        fillPath.lineTo(points.get(points.size() - 1).getX(), bottomY);
        fillPath.lineTo(points.get(0).getX(), bottomY);
        fillPath.closePath();

        g2.setColor(fillColor);
        g2.fill(fillPath);

        // Paint Stroke Line
        g2.setColor(lineColor);
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.draw(path);
    }

    private double catmullRom(double p0, double p1, double p2, double p3, double t) {
        return 0.5 * (
            (2 * p1) +
            (-p0 + p2) * t +
            (2 * p0 - 5 * p1 + 4 * p2 - p3) * t * t +
            (-p0 + 3 * p1 - 3 * p2 + p3) * t * t * t
        );
    }

    private void drawTooltip(Graphics2D g2, int mouseX, int mouseY, int idx) {
        String title = xLabels.get(idx);
        boolean isCust = isCustomerRegistrationChart();
        String line1 = isCust ? "Đăng ký mới: " + (int) (double) revenueData.get(idx) : "Doanh thu: " + df.format(revenueData.get(idx)) + "đ";
        String line2 = isCust ? null : "Lợi nhuận: " + df.format(profitData.get(idx)) + "đ";

        g2.setFont(FONT_TOOLTIP);
        FontMetrics fm = g2.getFontMetrics();

        int maxStrW = fm.stringWidth(title);
        maxStrW = Math.max(maxStrW, fm.stringWidth(line1));
        if (line2 != null) {
            maxStrW = Math.max(maxStrW, fm.stringWidth(line2));
        }

        int w = maxStrW + 24;
        int h = isCust ? 52 : 70;

        // Position tooltip to avoid going out of bounds
        int x = mouseX + 15;
        if (x + w > getWidth()) {
            x = mouseX - w - 15;
        }
        int y = mouseY - h / 2;
        if (y < 10) {
            y = 10;
        }
        if (y + h > getHeight() - 10) {
            y = getHeight() - h - 10;
        }

        // Draw shadow
        g2.setColor(new Color(0, 0, 0, 15));
        g2.fillRoundRect(x + 2, y + 2, w, h, 8, 8);

        // Draw Tooltip Container
        g2.setColor(new Color(255, 255, 255));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setColor(new Color(210, 220, 235));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(x, y, w, h, 8, 8);

        // Draw text
        int textY = y + 20;
        g2.setColor(new Color(50, 60, 70));
        g2.drawString(title, x + 12, textY);

        g2.setFont(FONT_AXIS);
        textY += 18;
        g2.setColor(COLOR_REVENUE_LINE.darker());
        g2.drawString(line1, x + 12, textY);

        if (line2 != null) {
            textY += 16;
            g2.setColor(COLOR_PROFIT_LINE.darker());
            g2.drawString(line2, x + 12, textY);
        }
    }
}
