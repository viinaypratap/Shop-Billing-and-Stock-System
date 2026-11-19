package shopbilling.ui;

import shopbilling.dao.BillDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class BillHistoryFrame extends JPanel {
    private static final Logger LOG = Logger.getLogger(BillHistoryFrame.class.getName()); private final BillDAO dao = new BillDAO();
    private final JTextField from = field(), to = field();
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Bill Number", "Date", "Customer", "Total"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    private final DefaultTableModel detailsModel = new DefaultTableModel(new Object[]{"Product", "Qty", "Price", "Subtotal"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
    private final JTable table = new JTable(model), details = new JTable(detailsModel); private final JLabel detailsTotal = new JLabel("Total: ₹0.00"); private List<Object[]> allRows = new ArrayList<>();

    public BillHistoryFrame() {
        setLayout(new BorderLayout(17, 17)); setBackground(AppTheme.BACKGROUND); setBorder(BorderFactory.createEmptyBorder(22, 25, 24, 25));
        add(AppTheme.pageHeading("Bill History", "View and review completed sales."), BorderLayout.NORTH);
        JPanel body = new JPanel(new BorderLayout(0, 14)); body.setOpaque(false); body.add(buildFilters(), BorderLayout.NORTH);
        JPanel split = new JPanel(new GridBagLayout()); split.setOpaque(false); GridBagConstraints g = new GridBagConstraints(); g.fill = GridBagConstraints.BOTH; g.weighty = 1;
        g.gridx = 0; g.weightx = .59; g.insets = new Insets(0, 0, 0, 14); split.add(billList(), g); g.gridx = 1; g.weightx = .41; g.insets = new Insets(0, 0, 0, 0); split.add(itemDetails(), g); body.add(split, BorderLayout.CENTER); add(body, BorderLayout.CENTER);
        AppTheme.styleTable(table); AppTheme.styleNumericColumn(table, 4); AppTheme.styleTable(details); AppTheme.styleNumericColumn(details, 1); AppTheme.styleNumericColumn(details, 2); AppTheme.styleNumericColumn(details, 3); table.getColumnModel().getColumn(0).setMinWidth(0); table.getColumnModel().getColumn(0).setMaxWidth(0); table.getColumnModel().getColumn(0).setPreferredWidth(0);
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) loadDetails(); }); refreshData();
    }
    private JPanel buildFilters() {
        JPanel p = AppTheme.panel(); p.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10)); p.setBorder(AppTheme.panelBorder());
        p.add(label("From")); from.setColumns(11); from.setPreferredSize(new Dimension(130, 36)); p.add(from); p.add(label("To")); to.setColumns(11); to.setPreferredSize(new Dimension(130, 36)); p.add(to);
        JButton load = AppTheme.button("Load", AppTheme.BLUE), reset = AppTheme.button("Clear dates", AppTheme.WHITE); p.add(load); p.add(reset);
        JLabel hint = new JLabel("yyyy-MM-dd"); hint.setForeground(AppTheme.MUTED); hint.setFont(new Font("Segoe UI", Font.PLAIN, 11)); p.add(hint);
        load.addActionListener(e -> filter()); reset.addActionListener(e -> { from.setText(""); to.setText(""); filter(); }); return p;
    }
    private JPanel billList() {
        JPanel p = AppTheme.panel(); p.setLayout(new BorderLayout(0, 10)); p.setBorder(AppTheme.panelBorder()); JLabel title = new JLabel("Completed bills"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); p.add(title, BorderLayout.NORTH); p.add(new JScrollPane(table), BorderLayout.CENTER); return p;
    }
    private JPanel itemDetails() {
        JPanel p = AppTheme.panel(); p.setLayout(new BorderLayout(0, 10)); p.setBorder(AppTheme.panelBorder()); JLabel title = new JLabel("Selected bill details"); title.setFont(AppTheme.SECTION); title.setForeground(AppTheme.TEXT); p.add(title, BorderLayout.NORTH); p.add(new JScrollPane(details), BorderLayout.CENTER);
        detailsTotal.setFont(new Font("Segoe UI", Font.BOLD, 16)); detailsTotal.setForeground(AppTheme.NAVY); detailsTotal.setHorizontalAlignment(SwingConstants.RIGHT); p.add(detailsTotal, BorderLayout.SOUTH); return p;
    }
    private JLabel label(String text) { JLabel l = new JLabel(text); l.setFont(AppTheme.BODY_BOLD); l.setForeground(AppTheme.TEXT); return l; }
    public void refreshData() { try { allRows = dao.history(); filter(); } catch (Exception ex) { error("Could not load bill history.", ex); } }
    private void filter() {
        try {
            LocalDate start = from.getText().isBlank() ? null : LocalDate.parse(from.getText().trim()); LocalDate end = to.getText().isBlank() ? null : LocalDate.parse(to.getText().trim());
            if (start != null && end != null && end.isBefore(start)) throw new IllegalArgumentException("The To date must be on or after the From date.");
            model.setRowCount(0); detailsModel.setRowCount(0); detailsTotal.setText("Total: ₹0.00");
            NumberFormat money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
            for (Object[] row : allRows) {
                LocalDate date = ((java.sql.Timestamp)row[2]).toLocalDateTime().toLocalDate();
                if ((start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end))) {
                    Object[] display = row.clone(); display[2] = ((java.sql.Timestamp)row[2]).toLocalDateTime().format(dateFormat); display[4] = money.format(row[4]); model.addRow(display);
                }
            }
        } catch (DateTimeParseException ex) { warning("Enter dates in yyyy-MM-dd format."); }
        catch (IllegalArgumentException ex) { warning(ex.getMessage()); }
    }
    private void loadDetails() {
        try {
            int viewRow = table.getSelectedRow(), row = table.convertRowIndexToModel(viewRow); int id = (int)model.getValueAt(row, 0); List<Object[]> rows = dao.billItems(id); detailsModel.setRowCount(0);
            NumberFormat money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"));
            for (Object[] item : rows) detailsModel.addRow(new Object[]{item[1], item[3], money.format(item[2]), money.format(item[4])});
            detailsTotal.setText("Total: " + model.getValueAt(row, 4));
        } catch (Exception ex) { error("Could not load selected bill items.", ex); }
    }
    private void warning(String m) { JOptionPane.showMessageDialog(this, m, "Bill history", JOptionPane.WARNING_MESSAGE); }
    private void error(String m, Exception e) { LOG.log(Level.SEVERE, m, e); JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
    private static JTextField field() { JTextField f = new JTextField(); AppTheme.styleField(f); return f; }
}
