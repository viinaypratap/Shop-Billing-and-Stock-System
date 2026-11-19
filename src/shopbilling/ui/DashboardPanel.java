package shopbilling.ui;

import shopbilling.dao.BillDAO;
import shopbilling.dao.ProductDAO;
import shopbilling.model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class DashboardPanel extends JPanel {
    private static final Logger LOG = Logger.getLogger(DashboardPanel.class.getName());
    private final Runnable viewAll;
    private final JPanel stats = new JPanel(new GridLayout(1, 4, 14, 0));
    private final DefaultTableModel billsModel = model("Bill Number", "Date", "Customer", "Amount");
    private final DefaultTableModel stockModel = model("Product", "Code", "Available");
    private final JTable bills = new JTable(billsModel), lowStock = new JTable(stockModel);

    public DashboardPanel(Runnable viewAll) {
        this.viewAll = viewAll; setLayout(new BorderLayout(18, 18)); setBackground(AppTheme.BACKGROUND); setBorder(BorderFactory.createEmptyBorder(24, 25, 24, 25));
        stats.setOpaque(false); add(stats, BorderLayout.NORTH);
        JPanel lower = new JPanel(new GridBagLayout()); lower.setOpaque(false); GridBagConstraints g = new GridBagConstraints(); g.fill = GridBagConstraints.BOTH; g.weighty = 1; g.insets = new Insets(0, 0, 0, 14);
        JButton all = AppTheme.button("View all", AppTheme.WHITE); all.addActionListener(e -> this.viewAll.run());
        g.gridx = 0; g.weightx = .69; lower.add(tablePanel("Recent bills", bills, all), g);
        g.gridx = 1; g.weightx = .31; g.insets = new Insets(0, 0, 0, 0); lower.add(tablePanel("Low stock alert", lowStock, null), g);
        add(lower, BorderLayout.CENTER); refreshData();
    }

    private JPanel tablePanel(String title, JTable table, JButton action) {
        JPanel panel = AppTheme.panel(); panel.setLayout(new BorderLayout(0, 12)); panel.setBorder(AppTheme.panelBorder());
        JPanel head = new JPanel(new BorderLayout()); head.setOpaque(false); JLabel label = new JLabel(title); label.setFont(AppTheme.SECTION); label.setForeground(AppTheme.TEXT); head.add(label, BorderLayout.WEST); if (action != null) head.add(action, BorderLayout.EAST);
        AppTheme.styleTable(table); panel.add(head, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER); return panel;
    }

    public void refreshData() {
        try {
            double[] values = new BillDAO().dashboardCounts(); stats.removeAll();
            stats.add(stat("TOTAL PRODUCTS", String.valueOf((long) values[0]), "Items in inventory", AppTheme.BLUE));
            stats.add(stat("LOW STOCK PRODUCTS", String.valueOf((long) values[1]), "5 units or fewer", AppTheme.AMBER));
            stats.add(stat("TODAY'S BILLS", String.valueOf((long) values[2]), "Completed sales", AppTheme.NAVY));
            stats.add(stat("TODAY'S SALES", NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).format(values[3]), "Today's total", AppTheme.GREEN));
            billsModel.setRowCount(0); List<Object[]> rows = new BillDAO().history();
            for (int i = 0; i < Math.min(6, rows.size()); i++) { Object[] row = rows.get(i); String date = ((java.sql.Timestamp)row[2]).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")); billsModel.addRow(new Object[]{row[1], date, row[3], NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).format(row[4])}); }
            stockModel.setRowCount(0); int shown = 0;
            for (Product p : new ProductDAO().findAll()) if (p.getQuantity() <= 5 && shown++ < 7) stockModel.addRow(new Object[]{p.getProductName(), p.getProductCode(), p.getQuantity()});
        } catch (Exception ex) { LOG.log(Level.SEVERE, "Dashboard data load failed", ex); JOptionPane.showMessageDialog(this, "Could not refresh dashboard information. Check the database connection.", "Database error", JOptionPane.ERROR_MESSAGE); }
        stats.revalidate(); stats.repaint();
    }

    private JPanel stat(String label, String value, String note, Color accent) {
        JPanel card = AppTheme.panel(); card.setLayout(new BorderLayout(0, 8)); card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER), BorderFactory.createEmptyBorder(15, 17, 15, 17)));
        JLabel name = new JLabel(label); name.setFont(new Font("Segoe UI", Font.BOLD, 11)); name.setForeground(AppTheme.MUTED);
        JLabel number = new JLabel(value); number.setFont(new Font("Segoe UI", Font.BOLD, 27)); number.setForeground(AppTheme.TEXT);
        JLabel footer = new JLabel(note); footer.setFont(new Font("Segoe UI", Font.PLAIN, 12)); footer.setForeground(accent);
        card.add(name, BorderLayout.NORTH); card.add(number, BorderLayout.CENTER); card.add(footer, BorderLayout.SOUTH); return card;
    }
    private static DefaultTableModel model(String... columns) { return new DefaultTableModel(columns, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } }; }
}
