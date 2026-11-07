package demo;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

final class AppTheme {
    static void style(Component component) {
        component.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (component instanceof Container container)
            for (Component child : container.getComponents()) style(child);
    }
}
