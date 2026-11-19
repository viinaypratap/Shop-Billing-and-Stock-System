package shopbilling.ui;

import javax.swing.*;
import java.awt.*;

final class UiIcon implements Icon {
    private final String kind; private final Color color; private final int size;
    UiIcon(String kind, Color color, int size) { this.kind = kind; this.color = color; this.size = size; }
    @Override public int getIconWidth() { return size; }
    @Override public int getIconHeight() { return size; }
    @Override public void paintIcon(Component c, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create(); g.translate(x, y); g.setColor(color);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int m = Math.max(2, size / 5), w = size - 2 * m, h = size - 2 * m;
        switch (kind) {
            case "brand": g.drawLine(m, m + 1, m + 3, h / 2); g.drawLine(m + 3, h / 2, size - m - 2, h / 2); g.drawLine(size - m - 2, h / 2, size - m - 4, size - m); g.drawOval(m + 2, size - m - 3, 3, 3); g.drawOval(size - m - 7, size - m - 3, 3, 3); break;
            case "home": g.drawLine(m, size / 2, size / 2, m); g.drawLine(size / 2, m, size - m, size / 2); g.drawRect(m + 2, size / 2, w - 4, h / 2); break;
            case "products": g.drawRect(m, m, w, h); g.drawLine(size / 2, m, size / 2, size - m); g.drawLine(m, size / 2, size - m, size / 2); break;
            case "billing": g.drawRoundRect(m + 1, m, w - 2, h, 3, 3); g.drawLine(m + 4, m + 5, size - m - 4, m + 5); g.drawLine(m + 4, size / 2, size - m - 4, size / 2); g.drawLine(m + 4, size - m - 5, size - m - 4, size - m - 5); break;
            case "customers": g.drawOval(size / 2 - 4, m, 8, 8); g.drawArc(m + 1, size / 2 - 1, w - 2, h / 2 + 2, 0, 180); break;
            case "history": g.drawOval(m, m, w, h); g.drawLine(size / 2, m + 3, size / 2, size / 2); g.drawLine(size / 2, size / 2, size - m - 3, size / 2 + 3); break;
            case "logout": g.drawRect(m, m, w / 2, h); g.drawLine(size / 2, size / 2, size - m, size / 2); g.drawLine(size - m, size / 2, size - m - 4, size / 2 - 4); g.drawLine(size - m, size / 2, size - m - 4, size / 2 + 4); break;
        }
        g.dispose();
    }
}
