package shopbilling.ui;

import shopbilling.dao.BillDAO;
import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class LoginFrame extends JFrame {
    private static final Logger LOG = Logger.getLogger(LoginFrame.class.getName());
    private final JTextField username = new JTextField();
    private final JPasswordField password = new JPasswordField();

    public LoginFrame() {
        super("Shop Billing and Stock System | Sign in");
        setDefaultCloseOperation(EXIT_ON_CLOSE); setSize(520, 610); setMinimumSize(new Dimension(480, 560)); setLocationRelativeTo(null);
        getContentPane().setBackground(AppTheme.BACKGROUND);
        JPanel outer = new JPanel(new GridBagLayout()); outer.setBackground(AppTheme.BACKGROUND); outer.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));
        JPanel card = new JPanel(); card.setBackground(AppTheme.WHITE); card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(AppTheme.BORDER), BorderFactory.createEmptyBorder(34, 38, 30, 38)));
        JLabel icon = new JLabel(new UiIcon("brand", AppTheme.BLUE, 46)); icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel title = new JLabel("SHOP BILLING"); title.setFont(new Font("Segoe UI", Font.BOLD, 23)); title.setForeground(AppTheme.NAVY); title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel subtitle = new JLabel("AND STOCK SYSTEM"); subtitle.setFont(new Font("Segoe UI", Font.BOLD, 14)); subtitle.setForeground(AppTheme.MUTED); subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel tagline = new JLabel("Retail billing and inventory management"); tagline.setFont(AppTheme.BODY); tagline.setForeground(AppTheme.MUTED); tagline.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(icon); card.add(Box.createVerticalStrut(14)); card.add(title); card.add(Box.createVerticalStrut(3)); card.add(subtitle); card.add(Box.createVerticalStrut(8)); card.add(tagline); card.add(Box.createVerticalStrut(32));
        addLabel(card, "Username"); AppTheme.styleField(username); username.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); card.add(username); card.add(Box.createVerticalStrut(17));
        addLabel(card, "Password"); AppTheme.styleField(password); password.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); card.add(password); card.add(Box.createVerticalStrut(25));
        JButton login = AppTheme.button("LOGIN", AppTheme.BLUE); login.setAlignmentX(Component.CENTER_ALIGNMENT); login.setPreferredSize(new Dimension(200, 42)); login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        card.add(login); card.add(Box.createVerticalStrut(24));
        JLabel footer = new JLabel("Developed by Vinay Pratap | PIET24CR031"); footer.setFont(new Font("Segoe UI", Font.PLAIN, 11)); footer.setForeground(AppTheme.MUTED); footer.setAlignmentX(Component.CENTER_ALIGNMENT); card.add(footer);
        outer.add(card, new GridBagConstraints()); add(outer);
        login.addActionListener(e -> authenticate()); password.addActionListener(e -> authenticate());
    }

    private void addLabel(JPanel panel, String text) { JLabel label = new JLabel(text); label.setFont(AppTheme.BODY_BOLD); label.setForeground(AppTheme.TEXT); label.setAlignmentX(Component.LEFT_ALIGNMENT); label.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0)); panel.add(label); }

    private void authenticate() {
        try {
            if (new BillDAO().login(username.getText().trim(), new String(password.getPassword()))) {
                String user = username.getText().trim(); dispose(); new DashboardFrame(user).setVisible(true);
            } else JOptionPane.showMessageDialog(this, "Invalid username or password.", "Sign in", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Login failed", ex);
            JOptionPane.showMessageDialog(this, "Could not connect to the database. Check the MySQL settings and Connector/J classpath.", "Database connection", JOptionPane.ERROR_MESSAGE);
        }
    }
}
