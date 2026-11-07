package demo;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Noluthando Basi (GitHub: ZuluBarbie)
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Using default appearance: " + ex.getMessage());
            }
            AppTheme.install();
            new NewJFrame1().setVisible(true);
        });
    }
}
