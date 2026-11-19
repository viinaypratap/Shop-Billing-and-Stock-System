package shopbilling.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.*;

/** Shared dark slate and emerald styling for the Swing presentation layer. */
public final class AppTheme {
    public static final Color BACKGROUND = new Color(15, 23, 42);
    public static final Color DARK_NAVY = new Color(17, 24, 39);
    public static final Color WHITE = new Color(30, 41, 59);
    public static final Color INPUT = new Color(39, 52, 73);
    public static final Color NAVY = new Color(248, 250, 252);
    public static final Color BLUE = new Color(16, 185, 129);
    public static final Color BLUE_HOVER = new Color(5, 150, 105);
    public static final Color TEXT = new Color(248, 250, 252);
    public static final Color MUTED = new Color(148, 163, 184);
    public static final Color BORDER = new Color(51, 65, 85);
    public static final Color GREEN = new Color(34, 197, 94);
    public static final Color RED = new Color(239, 68, 68);
    public static final Color AMBER = new Color(245, 158, 11);
    private static final Color ROW_ALT = new Color(25, 37, 56);
    private static final Color SELECTED = new Color(6, 78, 59);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font SECTION = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font PAGE = new Font("Segoe UI", Font.BOLD, 23);

    private AppTheme() { }

    public static void install() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { UIManager.setLookAndFeel(info.getClassName()); break; }
            }
        } catch (Exception ignored) { }
        UIManager.put("defaultFont", BODY);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("OptionPane.background", WHITE);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("OptionPane.messageFont", BODY);
        UIManager.put("OptionPane.buttonFont", BODY_BOLD);
        UIManager.put("control", WHITE); UIManager.put("text", TEXT);
        UIManager.put("nimbusBase", DARK_NAVY); UIManager.put("nimbusBlueGrey", WHITE);
        UIManager.put("nimbusLightBackground", INPUT); UIManager.put("nimbusFocus", BLUE);
        UIManager.put("ComboBox.background", INPUT); UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("TextField.background", INPUT); UIManager.put("TextField.foreground", TEXT);
        UIManager.put("PasswordField.background", INPUT); UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("ScrollPane.background", WHITE); UIManager.put("Viewport.background", WHITE);
        UIManager.put("Table.background", WHITE); UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", SELECTED); UIManager.put("Table.selectionForeground", TEXT);
    }

    public static JButton button(String label, Color background) {
        final boolean secondary = background.equals(WHITE);
        final Color base = secondary ? INPUT : background;
        JButton b = new JButton(label) {
            @Override protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics.create();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = getModel().isPressed() ? (secondary ? BORDER : BLUE_HOVER)
                        : getModel().isRollover() && !secondary ? BLUE_HOVER : base;
                g.setColor(fill); g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); g.dispose();
                super.paintComponent(graphics);
            }
        };
        b.setFont(BODY_BOLD); b.setForeground(secondary ? TEXT : Color.WHITE);
        b.setBackground(base); b.setContentAreaFilled(false); b.setOpaque(false); b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(Math.max(82, b.getPreferredSize().width + 16), 38));
        b.setRolloverEnabled(true);
        return b;
    }

    public static void styleField(JComponent field) {
        field.setFont(BODY); field.setForeground(TEXT); field.setBackground(INPUT);
        if (field instanceof JTextComponent) ((JTextComponent) field).setCaretColor(BLUE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(7, 10, 7, 10)));
        field.setPreferredSize(new Dimension(180, 38));
        if (field instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) field;
            combo.setForeground(TEXT); combo.setBackground(INPUT); combo.setFocusable(true);
        }
    }

    public static JPanel panel() {
        JPanel p = new JPanel(); p.setBackground(WHITE); return p;
    }
    public static Border panelBorder() {
        return BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(15, 17, 15, 17));
    }
    public static JLabel pageTitle(String text) { JLabel l = new JLabel(text); l.setFont(PAGE); l.setForeground(TEXT); return l; }
    public static JLabel subtitle(String text) { JLabel l = new JLabel(text); l.setFont(BODY); l.setForeground(MUTED); return l; }

    public static void styleTable(JTable table) {
        table.setFont(BODY); table.setForeground(TEXT); table.setBackground(WHITE); table.setRowHeight(35);
        table.setShowVerticalLines(false); table.setShowHorizontalLines(true); table.setGridColor(BORDER);
        table.setIntercellSpacing(new Dimension(0, 1)); table.setSelectionBackground(SELECTED);
        table.setSelectionForeground(TEXT); table.setFillsViewportHeight(true); table.setAutoCreateRowSorter(true);
        table.setOpaque(true);
        JTableHeader h = table.getTableHeader(); h.setFont(BODY_BOLD); h.setForeground(TEXT); h.setBackground(DARK_NAVY);
        h.setPreferredSize(new Dimension(h.getPreferredSize().width, 38)); h.setReorderingAllowed(false);
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                label.setOpaque(true); label.setBackground(DARK_NAVY); label.setForeground(MUTED); label.setFont(BODY_BOLD);
                label.setBorder(new EmptyBorder(0, 10, 0, 10)); return label;
            }
        });
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                if (!selected) c.setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                c.setForeground(TEXT); setBorder(new EmptyBorder(0, 10, 0, 10)); return c;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        if (table.getParent() instanceof JViewport) table.getParent().setBackground(WHITE);
    }

    public static void styleNumericColumn(JTable table, int modelColumn) {
        int view = table.convertColumnIndexToView(modelColumn);
        if (view >= 0) {
            DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object value, boolean selected, boolean focus, int row, int column) {
                    Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, column);
                    setHorizontalAlignment(SwingConstants.RIGHT); setBorder(new EmptyBorder(0, 8, 0, 12));
                    if (!selected) c.setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                    c.setForeground(TEXT); return c;
                }
            };
            table.getColumnModel().getColumn(view).setCellRenderer(r);
        }
    }

    public static JPanel pageHeading(String title, String subtitle) {
        JPanel p = new JPanel(); p.setOpaque(false); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(pageTitle(title)); p.add(Box.createVerticalStrut(5)); p.add(subtitle(subtitle)); return p;
    }
}
