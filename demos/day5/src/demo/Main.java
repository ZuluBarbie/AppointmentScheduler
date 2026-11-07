package demo;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Noluthando (GitHub: ZuluBarbie)
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                for (UIManager.LookAndFeelInfo theme : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(theme.getName())) { UIManager.setLookAndFeel(theme.getClassName()); break; }
                }
                AppTheme.install();
            } catch (Exception ex) {
                System.err.println("Using default appearance: " + ex.getMessage());
            }
            new WelcomeFrame().setVisible(true);
        });
    }
}
