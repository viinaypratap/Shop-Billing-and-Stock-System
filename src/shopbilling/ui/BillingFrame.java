package shopbilling.ui;

import shopbilling.dao.BillDAO;
import shopbilling.dao.CustomerDAO;
import shopbilling.dao.ProductDAO;
import shopbilling.model.BillItem;
import shopbilling.model.Customer;
import shopbilling.model.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class BillingFrame extends JPanel {
    private static final Logger LOG = Logger.getLogger(BillingFrame.class.getName());
    private final ProductDAO products = new ProductDAO(); private final CustomerDAO customers = new CustomerDAO(); private final BillDAO bills = new BillDAO();
    private final JComboBox<Customer> customerBox = new JComboBox<>(); private final JComboBox<Product> productBox = new JComboBox<>(); private final JTextField qtyField = field("1");
    private final JLabel dateValue = new JLabel(LocalDate.now().toString()), billValue = new JLabel("Assigned when sale completes");
    private final List<BillItem> items = new ArrayList<>();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Code", "Product", "Price", "Qty", "Subtotal"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model); private final JLabel subtotalValue = new JLabel("₹0.00"), totalValue = new JLabel("₹0.00");

    public BillingFrame() {
        setLayout(new BorderLayout(17, 17)); setBackground(AppTheme.BACKGROUND); setBorder(BorderFactory.createEmptyBorder(22, 25, 24, 25));
        add(AppTheme.pageHeading("Create New Bill", "Select a customer, add products and complete the sale."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(15, 15)); body.setOpaque(false); body.add(buildSaleHeader(), BorderLayout.NORTH);
        JPanel middle = new JPanel(new BorderLayout(15, 0)); middle.setOpaque(false); middle.add(buildItems(), BorderLayout.CENTER); middle.add(buildSummary(), BorderLayout.EAST); body.add(middle, BorderLayout.CENTER); add(body, BorderLayout.CENTER);
        AppTheme.styleTable(table); AppTheme.styleNumericColumn(table, 2); AppTheme.styleNumericColumn(table, 3); AppTheme.styleNumericColumn(table, 4); table.getColumnModel().getColumn(0).setPreferredWidth(100); table.getColumnModel().getColumn(1).setPreferredWidth(250); table.getColumnModel().getColumn(2).setPreferredWidth(110); table.getColumnModel().getColumn(3).setPreferredWidth(65); table.getColumnModel().getColumn(4).setPreferredWidth(120); refreshChoices();
    }

    private JPanel buildSaleHeader() {
        JPanel panel = AppTheme.panel(); panel.setBorder(AppTheme.panelBorder()); panel.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints(); g.gridy = 0; g.insets = new Insets(4, 6, 4, 13); g.anchor = GridBagConstraints.WEST;
        panel.add(label("Customer"), g); g.gridx = 1; g.weightx = .45; g.fill = GridBagConstraints.HORIZONTAL; AppTheme.styleField(customerBox); customerBox.setFont(AppTheme.BODY); customerBox.setPreferredSize(new Dimension(230, 38)); panel.add(customerBox, g);
        g.gridx = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE; panel.add(label("Bill No."), g); g.gridx = 3; panel.add(mutedValue(billValue), g);
        g.gridx = 4; panel.add(label("Date"), g); g.gridx = 5; g.weightx = .55; panel.add(mutedValue(dateValue), g); return panel;
    }
    private JPanel buildItems() {
        JPanel panel = AppTheme.panel(); panel.setLayout(new BorderLayout(0, 13)); panel.setBorder(AppTheme.panelBorder());
        JPanel heading = new JPanel(new BorderLayout()); heading.setOpaque(false); JLabel title = new JLabel("Bill items"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); heading.add(title, BorderLayout.WEST);
        JPanel addRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); addRow.setOpaque(false); addRow.add(label("Product")); AppTheme.styleField(productBox); productBox.setFont(AppTheme.BODY); productBox.setPreferredSize(new Dimension(280, 38)); addRow.add(productBox); addRow.add(label("Qty")); qtyField.setColumns(5); addRow.add(qtyField); JButton add = AppTheme.button("Add to Bill", AppTheme.BLUE); addRow.add(add); heading.add(addRow, BorderLayout.EAST);
        add.addActionListener(e -> addItem()); qtyField.addActionListener(e -> addItem()); panel.add(heading, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER);
        JButton remove = AppTheme.button("Remove selected", AppTheme.WHITE); JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); actions.setOpaque(false); actions.add(remove); remove.addActionListener(e -> removeItem()); panel.add(actions, BorderLayout.SOUTH); return panel;
    }
    private JPanel buildSummary() {
        JPanel panel = AppTheme.panel(); panel.setPreferredSize(new Dimension(255, 10)); panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS)); panel.setBorder(AppTheme.panelBorder());
        JLabel title = new JLabel("Bill Summary"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); title.setAlignmentX(Component.LEFT_ALIGNMENT); panel.add(title); panel.add(Box.createVerticalStrut(25));
        summaryRow(panel, "Subtotal", subtotalValue); JSeparator separator = new JSeparator(); separator.setForeground(AppTheme.BORDER); separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1)); separator.setAlignmentX(Component.LEFT_ALIGNMENT); panel.add(Box.createVerticalStrut(13)); panel.add(separator); panel.add(Box.createVerticalStrut(15));
        JLabel totalCaption = new JLabel("TOTAL AMOUNT"); totalCaption.setFont(new Font("Segoe UI", Font.BOLD, 11)); totalCaption.setForeground(AppTheme.MUTED); totalCaption.setAlignmentX(Component.LEFT_ALIGNMENT); panel.add(totalCaption);
        totalValue.setFont(new Font("Segoe UI", Font.BOLD, 27)); totalValue.setForeground(AppTheme.NAVY); totalValue.setAlignmentX(Component.LEFT_ALIGNMENT); panel.add(Box.createVerticalStrut(3)); panel.add(totalValue); panel.add(Box.createVerticalGlue());
        JButton complete = AppTheme.button("COMPLETE SALE", AppTheme.GREEN), clear = AppTheme.button("CLEAR BILL", AppTheme.WHITE), fresh = AppTheme.button("NEW BILL", AppTheme.BLUE);
        for (JButton b : new JButton[]{complete, clear, fresh}) { b.setAlignmentX(Component.LEFT_ALIGNMENT); b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); panel.add(Box.createVerticalStrut(9)); panel.add(b); }
        complete.addActionListener(e -> complete()); clear.addActionListener(e -> clearBill()); fresh.addActionListener(e -> { clearBill(); customerBox.setSelectedIndex(customerBox.getItemCount() > 0 ? 0 : -1); }); return panel;
    }
    private void summaryRow(JPanel p, String text, JLabel value) { JPanel row = new JPanel(new BorderLayout()); row.setOpaque(false); JLabel label = new JLabel(text); label.setFont(AppTheme.BODY); label.setForeground(AppTheme.MUTED); value.setFont(AppTheme.BODY_BOLD); value.setForeground(AppTheme.TEXT); row.add(label, BorderLayout.WEST); row.add(value, BorderLayout.EAST); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28)); row.setAlignmentX(Component.LEFT_ALIGNMENT); p.add(row); }
    private JLabel label(String text) { JLabel l = new JLabel(text); l.setFont(AppTheme.BODY_BOLD); l.setForeground(AppTheme.TEXT); return l; }
    private JLabel mutedValue(JLabel label) { label.setFont(AppTheme.BODY); label.setForeground(AppTheme.MUTED); return label; }

    public void refreshChoices() {
        try { customerBox.removeAllItems(); customerBox.addItem(null); for (Customer c : customers.findAll()) customerBox.addItem(c); productBox.removeAllItems(); for (Product p : products.findAll()) productBox.addItem(p); dateValue.setText(LocalDate.now().toString()); }
        catch (Exception ex) { error("Could not load customer and product lists.", ex); }
    }
    private void addItem() {
        try {
            Product selected = (Product) productBox.getSelectedItem(); if (selected == null) throw new IllegalArgumentException("Select a product.");
            int qty = Integer.parseInt(qtyField.getText().trim()); if (qty <= 0) throw new IllegalArgumentException("Quantity must be greater than 0.");
            Product fresh = products.findById(selected.getProductId()); if (fresh == null) throw new IllegalArgumentException("Product no longer exists.");
            int already = 0; BillItem existing = null;
            for (BillItem item : items) if (item.getProduct().getProductId() == fresh.getProductId()) { already += item.getQuantity(); existing = item; }
            if (qty + already > fresh.getQuantity()) { JOptionPane.showMessageDialog(this, "Available quantity: " + Math.max(0, fresh.getQuantity() - already), "Insufficient Stock", JOptionPane.WARNING_MESSAGE); return; }
            if (existing == null) items.add(new BillItem(fresh, qty)); else existing.setQuantity(existing.getQuantity() + qty);
            refreshTable(); qtyField.setText("1");
        } catch (NumberFormatException ex) { warning("Enter a whole number for quantity."); }
        catch (IllegalArgumentException ex) { warning(ex.getMessage()); }
        catch (Exception ex) { error("Could not check current stock.", ex); }
    }
    private void refreshTable() {
        model.setRowCount(0); double total = 0; NumberFormat money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
        for (BillItem item : items) { total += item.getSubtotal(); model.addRow(new Object[]{item.getProduct().getProductCode(), item.getProduct().getProductName(), money.format(item.getProduct().getPrice()), item.getQuantity(), money.format(item.getSubtotal())}); }
        String display = money.format(total); subtotalValue.setText(display); totalValue.setText(display);
    }
    private void removeItem() { int row = table.getSelectedRow(); if (row < 0) { warning("Select a bill item to remove."); return; } items.remove(table.convertRowIndexToModel(row)); refreshTable(); }
    private void clearBill() { items.clear(); refreshTable(); }
    private void complete() {
        try { Customer customer = (Customer) customerBox.getSelectedItem(); String number = bills.completeSale(customer == null ? null : customer.getCustomerId(), items); JOptionPane.showMessageDialog(this, "Sale completed successfully.\nBill No: " + number + "\nTotal: " + totalValue.getText(), "Sale completed", JOptionPane.INFORMATION_MESSAGE); clearBill(); refreshChoices(); }
        catch (IllegalArgumentException ex) { warning(ex.getMessage()); }
        catch (Exception ex) { error("Sale could not be completed. The transaction was rolled back; refresh stock and try again.", ex); }
    }
    private void warning(String m) { JOptionPane.showMessageDialog(this, m, "Billing", JOptionPane.WARNING_MESSAGE); }
    private void error(String m, Exception ex) { LOG.log(Level.SEVERE, m, ex); JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private static JTextField field(String value) { JTextField f = new JTextField(value); AppTheme.styleField(f); return f; }
}
