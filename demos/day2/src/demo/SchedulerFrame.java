package demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/** Day 2 interface: add validated appointments to an in-memory schedule. */
public class SchedulerFrame extends JFrame {
    private final List<Appointment> appointments = new ArrayList<>();
    private final JTextField titleField = new JTextField();
    private final JTextField dateField = new JTextField(LocalDate.now().toString());
    private final JTextField startField = new JTextField("09:00");
    private final JTextField endField = new JTextField("10:00");
    private final JTextField locationField = new JTextField();
    private final JLabel feedback = new JLabel("Enter the details, then choose Add appointment.");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Title", "Date", "Start", "End", "Location"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) { return false; }
    };

    public SchedulerFrame() {
        super("Appointment Scheduler - Day 2: Add Appointments");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel(new BorderLayout(8, 10));
        JLabel heading = new JLabel("My schedule");
        heading.setFont(heading.getFont().deriveFont(20f));
        top.add(heading, BorderLayout.NORTH);
        JPanel fields = new JPanel(new GridLayout(2, 5, 10, 4));
        String[] labels = {"Title (required)", "Date (YYYY-MM-DD)", "Start (HH:mm)",
            "End (HH:mm)", "Location (optional)"};
        for (String label : labels) { fields.add(new JLabel(label)); }
        JTextField[] inputs = {titleField, dateField, startField, endField, locationField};
        for (int i = 0; i < inputs.length; i++) {
            inputs[i].getAccessibleContext().setAccessibleName(labels[i]);
            fields.add(inputs[i]);
        }
        top.add(fields, BorderLayout.CENTER);
        JPanel actions = new JPanel(new BorderLayout(12, 0));
        JButton addButton = new JButton("Add appointment");
        addButton.addActionListener(event -> addAppointment());
        actions.add(addButton, BorderLayout.WEST);
        feedback.getAccessibleContext().setAccessibleName("Appointment status");
        actions.add(feedback, BorderLayout.CENTER);
        top.add(actions, BorderLayout.SOUTH);
        content.add(top, BorderLayout.NORTH);

        JTable table = new JTable(model);
        table.getAccessibleContext().setAccessibleName("Appointments");
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        content.add(new JLabel("Appointments are kept only until this window closes."),
                BorderLayout.SOUTH);
        setContentPane(content);
        getRootPane().setDefaultButton(addButton);
        setMinimumSize(new Dimension(960, 440));
        setSize(1100, 540);
        setLocationRelativeTo(null);
    }

    private void addAppointment() {
        try {
            Appointment appointment = Appointment.fromInput(titleField.getText(),
                    dateField.getText(), startField.getText(), endField.getText(),
                    locationField.getText());
            // Validation completes before either the collection or table changes.
            appointments.add(appointment);
            model.addRow(appointment.toTableRow());
            feedback.setForeground(new Color(0, 100, 30));
            feedback.setText("Added: " + appointment.getTitle() + ". Total: " + appointments.size());
            titleField.setText("");
            locationField.setText("");
            titleField.requestFocusInWindow();
        } catch (IllegalArgumentException ex) {
            feedback.setForeground(new Color(170, 20, 20));
            feedback.setText(ex.getMessage());
        }
    }
}
