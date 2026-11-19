package shopbilling.ui;

import shopbilling.dao.ProductDAO;
import shopbilling.model.Product;
import shopbilling.util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class ProductManagementFrame extends JPanel {
    private static final Logger LOG = Logger.getLogger(ProductManagementFrame.class.getName());
    private final ProductDAO dao = new ProductDAO();
    private final JTextField code = field(), name = field(), category = field(), price = field(), quantity = field(), supplier = field(), search = field();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Code", "Product", "Category", "Price", "Stock", "Supplier"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model); private int selectedId = -1;

    public ProductManagementFrame() {
        setLayout(new BorderLayout(18, 18)); setBackground(AppTheme.BACKGROUND); setBorder(BorderFactory.createEmptyBorder(22, 25, 24, 25));
        add(AppTheme.pageHeading("Product Management", "Manage products, pricing and available stock."), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 15)); center.setOpaque(false); center.add(buildForm(), BorderLayout.NORTH); center.add(buildInventory(), BorderLayout.CENTER); add(center, BorderLayout.CENTER);
        AppTheme.styleTable(table); configureColumns(); table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populate(); });
        refreshData();
    }

    private JPanel buildForm() {
        JPanel panel = AppTheme.panel(); panel.setLayout(new BorderLayout(10, 12)); panel.setBorder(AppTheme.panelBorder());
        JLabel title = new JLabel("Product details"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); panel.add(title, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridBagLayout()); fields.setOpaque(false); addField(fields, 0, 0, "Product Code", code); addField(fields, 1, 0, "Product Name", name); addField(fields, 0, 1, "Category", category); addField(fields, 1, 1, "Price", price); addField(fields, 0, 2, "Quantity", quantity); addField(fields, 1, 2, "Supplier", supplier); panel.add(fields, BorderLayout.CENTER);
        JButton add = AppTheme.button("Add Product", AppTheme.GREEN), update = AppTheme.button("Update", AppTheme.BLUE), delete = AppTheme.button("Delete", AppTheme.RED), clear = AppTheme.button("Clear", AppTheme.WHITE);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); actions.setOpaque(false); actions.add(add); actions.add(update); actions.add(delete); actions.add(clear); panel.add(actions, BorderLayout.SOUTH);
        add.addActionListener(e -> save(false)); update.addActionListener(e -> save(true)); delete.addActionListener(e -> remove()); clear.addActionListener(e -> reset()); return panel;
    }

    private JPanel buildInventory() {
        JPanel panel = AppTheme.panel(); panel.setLayout(new BorderLayout(0, 12)); panel.setBorder(AppTheme.panelBorder());
        JPanel head = new JPanel(new BorderLayout()); head.setOpaque(false); JLabel title = new JLabel("Inventory"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); head.add(title, BorderLayout.WEST);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0)); searchBar.setOpaque(false); search.setColumns(18); AppTheme.styleField(search); search.setPreferredSize(new Dimension(220, 36));
        JButton find = AppTheme.button("Search", AppTheme.BLUE), refresh = AppTheme.button("Refresh", AppTheme.WHITE); searchBar.add(search); searchBar.add(find); searchBar.add(refresh); head.add(searchBar, BorderLayout.EAST);
        find.addActionListener(e -> load(search.getText())); search.addActionListener(e -> load(search.getText())); refresh.addActionListener(e -> { search.setText(""); load(""); });
        panel.add(head, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER); return panel;
    }

    private void addField(JPanel p, int col, int row, String label, JTextField input) {
        GridBagConstraints g = new GridBagConstraints(); g.gridy = row; g.insets = new Insets(4, col == 0 ? 0 : 18, 4, 8); g.anchor = GridBagConstraints.WEST;
        g.gridx = col * 2; g.weightx = 0; p.add(label(label), g); g.gridx++; g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1; AppTheme.styleField(input); input.setPreferredSize(new Dimension(180, 36)); p.add(input, g);
    }
    private JLabel label(String text) { JLabel l = new JLabel(text); l.setFont(AppTheme.BODY_BOLD); l.setForeground(AppTheme.TEXT); return l; }
    private void configureColumns() {
        int[] widths = {55, 105, 210, 125, 105, 75, 170}; for (int i = 0; i < widths.length; i++) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        DefaultTableCellRenderer money = new DefaultTableCellRenderer() { @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { Component cell = super.getTableCellRendererComponent(t, NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).format(v), s, f, r, c); if (!s) cell.setBackground(r % 2 == 0 ? AppTheme.WHITE : new Color(25, 37, 56)); cell.setForeground(AppTheme.TEXT); return cell; } };
        money.setHorizontalAlignment(SwingConstants.RIGHT); money.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 12)); table.getColumnModel().getColumn(4).setCellRenderer(money);
        DefaultTableCellRenderer stock = new DefaultTableCellRenderer() { @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { Component x = super.getTableCellRendererComponent(t, v, s, f, r, c); setHorizontalAlignment(SwingConstants.CENTER); if (!s && Integer.parseInt(String.valueOf(v)) <= 5) { setForeground(AppTheme.RED); setFont(AppTheme.BODY_BOLD); } else if (!s) setForeground(AppTheme.TEXT); return x; } };
        table.getColumnModel().getColumn(5).setCellRenderer(stock);
    }
    public void refreshData() { load(search.getText()); }
    private void load(String term) { try { model.setRowCount(0); List<Product> rows = dao.search(term == null ? "" : term.trim()); for (Product p : rows) model.addRow(new Object[]{p.getProductId(), p.getProductCode(), p.getProductName(), p.getCategory(), p.getPrice(), p.getQuantity(), p.getSupplier()}); } catch (Exception ex) { error("Could not load products.", ex); } }
    private void populate() { int row = table.convertRowIndexToModel(table.getSelectedRow()); selectedId = (int) model.getValueAt(row, 0); code.setText(text(1)); name.setText(text(2)); category.setText(text(3)); price.setText(text(4)); quantity.setText(text(5)); supplier.setText(text(6)); }
    private String text(int column) { return String.valueOf(model.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), column)); }
    private void save(boolean update) {
        try {
            if (update && selectedId < 0) throw new IllegalArgumentException("Select a product to update.");
            Product p = new Product(selectedId, ValidationUtil.required(code.getText(), "Product code"), ValidationUtil.required(name.getText(), "Product name"), category.getText().trim(), ValidationUtil.positivePrice(price.getText()), ValidationUtil.nonNegativeInteger(quantity.getText(), "Quantity"), supplier.getText().trim());
            if (update) dao.update(p); else dao.add(p); reset(); load(search.getText()); success(update ? "Product updated." : "Product added.");
        } catch (NumberFormatException ex) { warning("Enter a valid numeric price and whole-number quantity."); }
        catch (IllegalArgumentException ex) { warning(ex.getMessage()); }
        catch (Exception ex) { error("Could not save product. Check that the product code is unique.", ex); }
    }
    private void remove() {
        if (selectedId < 0) { warning("Select a product first."); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this product?", "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            try { dao.delete(selectedId); reset(); load(""); success("Product deleted."); } catch (Exception ex) { error("A product already used in a bill cannot be deleted.", ex); }
        }
    }
    private void reset() { selectedId = -1; code.setText(""); name.setText(""); category.setText(""); price.setText(""); quantity.setText(""); supplier.setText(""); table.clearSelection(); }
    private void warning(String m) { JOptionPane.showMessageDialog(this, m, "Check product details", JOptionPane.WARNING_MESSAGE); }
    private void success(String m) { JOptionPane.showMessageDialog(this, m, "Product management", JOptionPane.INFORMATION_MESSAGE); }
    private void error(String m, Exception e) { LOG.log(Level.SEVERE, m, e); JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private static JTextField field() { JTextField f = new JTextField(); AppTheme.styleField(f); return f; }
}
