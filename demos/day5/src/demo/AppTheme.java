package demo;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.JTextComponent;

final class AppTheme {
    static final Color BACKGROUND = new Color(242, 248, 250);
    static final Color SURFACE = Color.WHITE;
    static final Color CYAN = new Color(0, 204, 255);
    static final Color TEXT = new Color(25, 43, 52);
    static final Color MUTED = new Color(99, 116, 126);
    static final Color LINE = new Color(183, 204, 214);
    static final Color INK = new Color(0, 92, 116);

    static void install() {
        UIManager.put("control", SURFACE);
        UIManager.put("text", SURFACE);
        UIManager.put("controlText", TEXT);
        UIManager.put("textText", TEXT);
        UIManager.put("textHighlight", CYAN);
        UIManager.put("textHighlightText", Color.BLACK);
        UIManager.put("Button.disabledText", MUTED);
        UIManager.put("ComboBox.disabledForeground", MUTED);
        UIManager.put("ComboBox.disabledBackground", SURFACE);
    }

    static void style(Component component) {
        component.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        component.setForeground(TEXT);
        if (component instanceof JPanel || component instanceof JViewport) component.setBackground(BACKGROUND);
        if (component instanceof JLabel label) {
            label.setForeground(TEXT);
            label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        }
        if (component instanceof JTextComponent input) {
            input.setBackground(SURFACE);
            input.setCaretColor(INK);
            input.setSelectionColor(CYAN);
            input.setSelectedTextColor(Color.BLACK);
            input.setDisabledTextColor(MUTED);
            input.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LINE), BorderFactory.createEmptyBorder(7, 8, 7, 8)));
        }
        if (component instanceof JButton button) {
            button.setBackground(SURFACE);
            button.setForeground(INK);
            button.setFocusPainted(false);
            button.setMargin(new Insets(8, 12, 8, 12));
        }
        if (component instanceof JComboBox<?> box) box.setBackground(SURFACE);
        if (component instanceof JSeparator separator) separator.setForeground(LINE);
        if (component instanceof JScrollPane scroll) scroll.setBorder(BorderFactory.createLineBorder(LINE));
        if (component instanceof Container container)
            for (Component child : container.getComponents()) style(child);
    }

    static void welcome(Container container) {
        container.setBackground(Color.BLACK);
        for (Component child : container.getComponents()) {
            if (child instanceof JLabel) child.setForeground(CYAN);
            if (child instanceof Container nested) welcome(nested);
        }
    }

    static void primary(JButton button) {
        button.setBackground(CYAN);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
    }

    static void table(JTable table) {
        table.setBackground(SURFACE);
        table.setForeground(TEXT);
        table.setSelectionBackground(CYAN);
        table.setSelectionForeground(Color.BLACK);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setBackground(new Color(20, 26, 31));
                setForeground(CYAN);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                setBorder(BorderFactory.createEmptyBorder(9, 8, 9, 8));
                return this;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setBackground(selected ? CYAN : row % 2 == 0 ? SURFACE : new Color(233, 245, 249));
                setForeground(selected ? Color.BLACK : TEXT);
                setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
                return this;
            }
        });
    }
}
