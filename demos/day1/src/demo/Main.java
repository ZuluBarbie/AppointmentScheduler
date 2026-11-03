package demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/** Day 1 group demonstration: a window and an empty schedule. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Using default appearance: " + ex.getMessage());
            }
            JFrame frame = new JFrame("Appointment Scheduler - Day 1: Basic Window");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            JPanel content = new JPanel(new BorderLayout(12, 12));
            content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
            JLabel heading = new JLabel("My schedule - " + LocalDate.now()
                    .format(DateTimeFormatter.ofPattern("d MMMM yyyy")));
            heading.setFont(heading.getFont().deriveFont(20f));
            content.add(heading, BorderLayout.NORTH);

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Title", "Date", "Start", "End", "Location"}, 0) {
                @Override
                public boolean isCellEditable(int row, int column) { return false; }
            };
            JTable table = new JTable(model);
            table.setFillsViewportHeight(true);
            table.setRowHeight(28);
            content.add(new JScrollPane(table), BorderLayout.CENTER);
            content.add(new JLabel("No appointments."),
                    BorderLayout.SOUTH);

            frame.setContentPane(content);
            frame.setMinimumSize(new Dimension(650, 320));
            frame.setSize(850, 460);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
