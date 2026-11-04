package demo;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Opens the Day 3 appointment window on Swing's event thread. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Using default appearance: " + ex.getMessage());
            }
            new SchedulerFrame().setVisible(true);
        });
    }
}
