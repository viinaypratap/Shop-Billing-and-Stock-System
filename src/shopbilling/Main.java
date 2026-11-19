package shopbilling;

import javax.swing.SwingUtilities;
import shopbilling.ui.AppTheme;
import shopbilling.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppTheme.install();
            new LoginFrame().setVisible(true);
        });
    }
}
