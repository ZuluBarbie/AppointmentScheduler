package demo;

import java.awt.*;
import javax.swing.*;

final class AppTheme {
    static final Color NAVY = new Color(24, 42, 65);
    static final Color TEAL = new Color(0, 116, 119);
    static final Color BACKGROUND = new Color(243, 247, 250);

    static void install() {
        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("control", BACKGROUND);
        UIManager.put("nimbusBase", NAVY);
        UIManager.put("nimbusFocus", TEAL);
        UIManager.put("nimbusSelectionBackground", TEAL);
    }

    static void style(Component component) {
        component.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        if (component instanceof JPanel panel) panel.setBackground(BACKGROUND);
        if (component instanceof JButton button) {
            button.setMargin(new Insets(9, 15, 9, 15));
            button.setFocusPainted(false);
        }
        if (component instanceof Container container)
            for (Component child : container.getComponents()) style(child);
    }

    static void primary(JButton button) {
        button.setBackground(TEAL);
        button.setForeground(Color.WHITE);
        button.setFont(button.getFont().deriveFont(Font.BOLD));
    }
}
