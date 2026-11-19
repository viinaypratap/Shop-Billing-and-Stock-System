package shopbilling.ui;

import shopbilling.dao.CustomerDAO;
import shopbilling.model.Customer;
import shopbilling.util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CustomerFrame extends JPanel {
    private static final Logger LOG = Logger.getLogger(CustomerFrame.class.getName());
    private final CustomerDAO dao = new CustomerDAO();
    private final JTextField name = field(), phone = field(), address = field(), search = field();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Customer", "Phone", "Address"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model); private int selected = -1;

    public CustomerFrame() {
        setLayout(new BorderLayout(18, 18)); setBackground(AppTheme.BACKGROUND); setBorder(BorderFactory.createEmptyBorder(22, 25, 24, 25));
        add(AppTheme.pageHeading("Customer Management", "Manage customer information for billing."), BorderLayout.NORTH);
        JPanel center = new JPanel(new BorderLayout(0, 15)); center.setOpaque(false); center.add(buildForm(), BorderLayout.NORTH); center.add(buildCustomerList(), BorderLayout.CENTER); add(center, BorderLayout.CENTER);
        AppTheme.styleTable(table); table.getColumnModel().getColumn(0).setPreferredWidth(55); table.getColumnModel().getColumn(1).setPreferredWidth(230); table.getColumnModel().getColumn(2).setPreferredWidth(150); table.getColumnModel().getColumn(3).setPreferredWidth(400);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) populate(); }); refreshData();
    }
    private JPanel buildForm() {
        JPanel panel = AppTheme.panel(); panel.setLayout(new BorderLayout(10, 12)); panel.setBorder(AppTheme.panelBorder());
        JLabel title = new JLabel("Customer details"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); panel.add(title, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridBagLayout()); fields.setOpaque(false); addField(fields, 0, "Customer Name", name); addField(fields, 1, "Phone", phone); addField(fields, 2, "Address", address); panel.add(fields, BorderLayout.CENTER);
        JButton add = AppTheme.button("Add Customer", AppTheme.GREEN), update = AppTheme.button("Update", AppTheme.BLUE), delete = AppTheme.button("Delete", AppTheme.RED), clear = AppTheme.button("Clear", AppTheme.WHITE);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); buttons.setOpaque(false); buttons.add(add); buttons.add(update); buttons.add(delete); buttons.add(clear); panel.add(buttons, BorderLayout.SOUTH);
        add.addActionListener(e -> save(false)); update.addActionListener(e -> save(true)); delete.addActionListener(e -> remove()); clear.addActionListener(e -> reset()); return panel;
    }
    private JPanel buildCustomerList() {
        JPanel panel = AppTheme.panel(); panel.setLayout(new BorderLayout(0, 12)); panel.setBorder(AppTheme.panelBorder());
        JPanel head = new JPanel(new BorderLayout()); head.setOpaque(false); JLabel title = new JLabel("Customers"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); head.add(title, BorderLayout.WEST);
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0)); searchBar.setOpaque(false); search.setColumns(18); search.setPreferredSize(new Dimension(220, 36));
        JButton find = AppTheme.button("Search", AppTheme.BLUE), refresh = AppTheme.button("Refresh", AppTheme.WHITE); searchBar.add(search); searchBar.add(find); searchBar.add(refresh); head.add(searchBar, BorderLayout.EAST);
        find.addActionListener(e -> load(search.getText())); search.addActionListener(e -> load(search.getText())); refresh.addActionListener(e -> { search.setText(""); load(""); });
        panel.add(head, BorderLayout.NORTH); panel.add(new JScrollPane(table), BorderLayout.CENTER); return panel;
    }
    private void addField(JPanel panel, int row, String label, JTextField field) {
        GridBagConstraints g = new GridBagConstraints(); g.gridy = row; g.gridx = 0; g.insets = new Insets(4, 0, 4, 12); g.anchor = GridBagConstraints.WEST; JLabel l = new JLabel(label); l.setFont(AppTheme.BODY_BOLD); l.setForeground(AppTheme.TEXT); panel.add(l, g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL; AppTheme.styleField(field); panel.add(field, g);
    }
    public void refreshData() { load(search.getText()); }
    private void load(String term) { try { model.setRowCount(0); for (Customer c : dao.search(term == null ? "" : term.trim())) model.addRow(new Object[]{c.getCustomerId(), c.getCustomerName(), c.getPhone(), c.getAddress()}); } catch (Exception ex) { error("Could not load customers.", ex); } }
    private void populate() { int row = table.convertRowIndexToModel(table.getSelectedRow()); selected = (int) model.getValueAt(row, 0); name.setText(value(1)); phone.setText(value(2)); address.setText(value(3)); }
    private String value(int col) { Object x = model.getValueAt(table.convertRowIndexToModel(table.getSelectedRow()), col); return x == null ? "" : x.toString(); }
    private void save(boolean edit) {
        try { if (edit && selected < 0) throw new IllegalArgumentException("Select a customer to update."); Customer c = new Customer(selected, ValidationUtil.required(name.getText(), "Customer name"), ValidationUtil.phone(phone.getText()), address.getText().trim()); if (edit) dao.update(c); else dao.add(c); reset(); load(search.getText()); JOptionPane.showMessageDialog(this, edit ? "Customer updated." : "Customer added.", "Customer management", JOptionPane.INFORMATION_MESSAGE); }
        catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Check customer details", JOptionPane.WARNING_MESSAGE); }
        catch (Exception ex) { error("Could not save customer.", ex); }
    }
    private void remove() {
        if (selected < 0) { JOptionPane.showMessageDialog(this, "Select a customer first.", "Customer management", JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Delete this customer? Existing bills will remain in history.", "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            try { dao.delete(selected); reset(); load(""); } catch (Exception ex) { error("Could not delete customer.", ex); }
        }
    }
    private void reset() { selected = -1; name.setText(""); phone.setText(""); address.setText(""); table.clearSelection(); }
    private void error(String m, Exception e) { LOG.log(Level.SEVERE, m, e); JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private static JTextField field() { JTextField f = new JTextField(); AppTheme.styleField(f); return f; }
}
