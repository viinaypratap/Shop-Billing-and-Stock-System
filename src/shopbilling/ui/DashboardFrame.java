package shopbilling.ui;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class DashboardFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final JLabel pageTitle = new JLabel("Dashboard");
    private final DashboardPanel dashboard = new DashboardPanel(this::showHistory);
    private final ProductManagementFrame productPage = new ProductManagementFrame();
    private final BillingFrame billingPage = new BillingFrame();
    private final CustomerFrame customerPage = new CustomerFrame();
    private final BillHistoryFrame historyPage = new BillHistoryFrame();
    private final String welcomeName;
    private final Map<String, JButton> navigation = new LinkedHashMap<>();

    public DashboardFrame(String username) {
        super("Shop Billing and Stock System"); welcomeName = username == null || username.isBlank() ? "Admin" : username;
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(1280, 760); setMinimumSize(new Dimension(1100, 650)); setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BACKGROUND); setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST); add(buildWorkspace(), BorderLayout.CENTER);
        content.add(dashboard, "Dashboard"); content.add(productPage, "Products"); content.add(billingPage, "Billing"); content.add(customerPage, "Customers"); content.add(historyPage, "Bill History");
        showPage("Dashboard");
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel(new BorderLayout()); side.setBackground(AppTheme.DARK_NAVY); side.setPreferredSize(new Dimension(224, 10));
        JPanel brand = new JPanel(new BorderLayout(11, 0)); brand.setBackground(AppTheme.DARK_NAVY); brand.setBorder(BorderFactory.createEmptyBorder(25, 20, 26, 15));
        JLabel icon = new JLabel(new UiIcon("brand", Color.WHITE, 30));
        JLabel name = new JLabel("<html><b>SHOP BILLING</b><br>AND STOCK SYSTEM</html>"); name.setForeground(Color.WHITE); name.setFont(new Font("Segoe UI", Font.PLAIN, 12)); brand.add(icon, BorderLayout.WEST); brand.add(name, BorderLayout.CENTER); side.add(brand, BorderLayout.NORTH);
        JPanel menu = new JPanel(); menu.setBackground(AppTheme.DARK_NAVY); menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS)); menu.setBorder(BorderFactory.createEmptyBorder(4, 11, 10, 11));
        addNav(menu, "Dashboard", "home"); addNav(menu, "Products", "products"); addNav(menu, "Billing", "billing"); addNav(menu, "Customers", "customers"); addNav(menu, "Bill History", "history"); side.add(menu, BorderLayout.CENTER);
        JButton logout = sidebarButton("Logout", "logout"); logout.addActionListener(e -> { dispose(); new LoginFrame().setVisible(true); });
        JPanel bottom = new JPanel(new BorderLayout()); bottom.setBackground(AppTheme.DARK_NAVY); bottom.setBorder(BorderFactory.createEmptyBorder(10, 11, 18, 11)); bottom.add(logout); side.add(bottom, BorderLayout.SOUTH);
        return side;
    }
    private void addNav(JPanel menu, String label, String icon) { JButton button = sidebarButton(label, icon); navigation.put(label, button); button.addActionListener(e -> showPage(label)); menu.add(button); menu.add(Box.createVerticalStrut(5)); }
    private JButton sidebarButton(String label, String iconName) {
        JButton b = new JButton(label, new UiIcon(iconName, new Color(215, 226, 239), 18)); b.setHorizontalAlignment(SwingConstants.LEFT); b.setIconTextGap(13);
        b.setFont(AppTheme.BODY_BOLD); b.setForeground(new Color(226, 234, 244)); b.setBackground(AppTheme.DARK_NAVY); b.setOpaque(true); b.setFocusPainted(false); b.setBorder(BorderFactory.createEmptyBorder(11, 12, 11, 8));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    private JPanel buildWorkspace() {
        JPanel work = new JPanel(new BorderLayout()); work.setBackground(AppTheme.BACKGROUND);
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(AppTheme.DARK_NAVY); header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER)); header.setPreferredSize(new Dimension(10, 72));
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); pageTitle.setForeground(AppTheme.TEXT); pageTitle.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0)); header.add(pageTitle, BorderLayout.WEST);
        JPanel user = new JPanel(new FlowLayout(FlowLayout.RIGHT, 11, 16)); user.setBackground(AppTheme.DARK_NAVY);
        JLabel avatar = new JLabel(welcomeName.substring(0, 1).toUpperCase(), SwingConstants.CENTER); avatar.setOpaque(true); avatar.setBackground(new Color(6, 78, 59)); avatar.setForeground(AppTheme.TEXT); avatar.setFont(AppTheme.BODY_BOLD); avatar.setPreferredSize(new Dimension(34, 34));
        JLabel welcome = new JLabel("Welcome, " + welcomeName); welcome.setFont(AppTheme.BODY_BOLD); welcome.setForeground(AppTheme.TEXT); user.add(welcome); user.add(avatar); user.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 22)); header.add(user, BorderLayout.EAST);
        work.add(header, BorderLayout.NORTH); content.setBackground(AppTheme.BACKGROUND); work.add(content, BorderLayout.CENTER);
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 7)); footer.setBackground(AppTheme.BACKGROUND); footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER));
        JLabel credit = new JLabel("Developed by Vinay Pratap | PIET24CR031"); credit.setFont(new Font("Segoe UI", Font.PLAIN, 11)); credit.setForeground(AppTheme.MUTED); footer.add(credit); work.add(footer, BorderLayout.SOUTH);
        return work;
    }
    private void showPage(String page) {
        pageTitle.setText(page.equals("Billing") ? "Create New Bill" : page.equals("Products") ? "Product Management" : page.equals("Customers") ? "Customer Management" : page);
        cards.show(content, page); navigation.forEach((name, button) -> { button.setBackground(name.equals(page) ? AppTheme.BLUE : AppTheme.DARK_NAVY); button.setForeground(Color.WHITE); });
        if (page.equals("Dashboard")) dashboard.refreshData(); if (page.equals("Bill History")) historyPage.refreshData();
        if (page.equals("Products")) productPage.refreshData(); if (page.equals("Customers")) customerPage.refreshData(); if (page.equals("Billing")) billingPage.refreshChoices();
    }
    private void showHistory() { showPage("Bill History"); }
}
