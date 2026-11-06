package demo;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** Day 5 frame with repeat settings and in-app reminders. */
public class SchedulerFrame extends JFrame {
    private AppointmentBook book;
    private LocalDate anchor = LocalDate.now();
    private List<Occurrence> visible = List.of();
    private final JComboBox<Appointment.Repeat> repeatBox = new JComboBox<>(Appointment.Repeat.values());
    private final JTextField untilField = new JTextField(9);
    private final JSpinner reminderField = new JSpinner(new SpinnerNumberModel(0, 0, 1440, 1));
    private final Reminders reminders = new Reminders();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Title", "Date", "Start", "End", "Location", "Repeat", "Reminder (min)"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    @SuppressWarnings("unchecked")
    public SchedulerFrame() {
        initComponents();
        JPanel options = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        addOption(options, "Repeat", repeatBox);
        addOption(options, "Repeat until (YYYY-MM-DD)", untilField);
        addOption(options, "Reminder minutes (0 = off)", reminderField);
        untilField.setEnabled(false);
        repeatBox.addActionListener(e -> untilField.setEnabled(repeatBox.getSelectedItem() != Appointment.Repeat.NONE));
        JPanel footer = new JPanel(new java.awt.BorderLayout());
        footer.add(options, java.awt.BorderLayout.NORTH);
        footer.add(statusLabel, java.awt.BorderLayout.SOUTH);
        getContentPane().add(footer, java.awt.BorderLayout.SOUTH);
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        periodBox.setModel(new DefaultComboBoxModel<>(new String[]{"Day", "Week", "Month"}));
        anchorField.setColumns(10);
        table.setModel(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        String[] labels = {"Title", "Appointment date", "Start time", "End time", "Location", "View date"};
        JTextField[] inputs = {titleField,dateField,startField,endField,locationField,anchorField};
        for (int i=0;i<inputs.length;i++) { inputs[i].getAccessibleContext().setAccessibleName(labels[i]); }
        table.getAccessibleContext().setAccessibleName("Appointments");
        addButton.addActionListener(e -> save(false));
        updateButton.addActionListener(e -> save(true));
        deleteButton.addActionListener(e -> deleteSelected());
        clearButton.addActionListener(e -> clear());
        exportButton.addActionListener(e -> exportView());
        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) select(); });
        showButton.addActionListener(e -> showDate());
        anchorField.addActionListener(e -> showDate());
        periodBox.addActionListener(e -> refresh());
        previousButton.addActionListener(e -> move(-1));
        nextButton.addActionListener(e -> move(1));
        todayButton.addActionListener(e -> { anchor=LocalDate.now(); refresh(); });
        try {
            book = new AppointmentBook(Path.of(System.getProperty("scheduler.dataFile","appointments.tsv")));
        } catch (IOException ex) {
            addButton.setEnabled(false);
            message("Cannot load data. File preserved: " + ex.getMessage(),true);
            JOptionPane.showMessageDialog(this,ex.getMessage(),"Could not load appointments",JOptionPane.ERROR_MESSAGE);
        }
        clear();
        refresh();
        if (book != null) {
            message("Loaded " + book.getAll().size() + " appointments. Keep this window open for reminders.", false);
            Timer timer = new Timer(1000, e -> checkReminders());
            addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosed(java.awt.event.WindowEvent event) { timer.stop(); }
            });
            timer.start();
        }
        setMinimumSize(new java.awt.Dimension(1000,440));
        setSize(1120,560);
        setLocationRelativeTo(null);
    }

    private static void addOption(JPanel panel, String text, JComponent input) {
        JLabel label = new JLabel(text);
        label.setLabelFor(input);
        panel.add(label);
        panel.add(input);
    }

    private void checkReminders() {
        List<Occurrence> due = reminders.due(book.getAll(), LocalDateTime.now());
        if (due.isEmpty()) return;
        StringBuilder message = new StringBuilder();
        for (Occurrence occurrence : due) {
            message.append(occurrence.appointment().getTitle()).append('\n').append("Starts ")
                    .append(occurrence.date()).append(" at ").append(occurrence.appointment().getStart())
                    .append("\n\n");
        }
        JTextArea details = new JTextArea(message.toString().stripTrailing(), Math.min(8, due.size() * 3), 34);
        details.setEditable(false);
        details.setLineWrap(true);
        details.setWrapStyleWord(true);
        details.setCaretPosition(0);
        JOptionPane pane = new JOptionPane(new JScrollPane(details), JOptionPane.INFORMATION_MESSAGE);
        JDialog dialog = pane.createDialog(this, "Appointment reminder");
        dialog.setModal(false);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        pane.addPropertyChangeListener(JOptionPane.VALUE_PROPERTY, e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void message(String text, boolean error) {
        statusLabel.setForeground(error ? new Color(170,20,20) : new Color(0,90,35));
        statusLabel.setText(text);
    }

    private void showDate() {
        try { anchor=LocalDate.parse(anchorField.getText().trim()); refresh(); }
        catch (RuntimeException ex) { message("Enter a valid view date as YYYY-MM-DD.",true); }
    }

    private void move(int direction) {
        String period=(String)periodBox.getSelectedItem();
        anchor=switch(period) {
            case "Week" -> anchor.plusWeeks(direction);
            case "Month" -> anchor.plusMonths(direction);
            default -> anchor.plusDays(direction);
        };
        refresh();
    }

    private void refresh() {
        anchorField.setText(anchor.toString());
        visible=book==null ? List.of() : book.view(anchor,(String)periodBox.getSelectedItem());
        model.setRowCount(0);
        for (Occurrence occurrence : visible) model.addRow(occurrence.toTableRow());
        exportButton.setEnabled(!visible.isEmpty());
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void exportView() {
        List<Occurrence> rows = List.copyOf(visible);
        if (rows.isEmpty()) return;
        ZoneId zone = ZoneId.systemDefault();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Export current view (" + zone + ")");
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("iCalendar (*.ics)", "ics"));
        chooser.setSelectedFile(new java.io.File("appointments-" + anchor + ".ics"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        Path target = chooser.getSelectedFile().toPath();
        if (!target.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".ics")) {
            target = target.resolveSibling(target.getFileName() + ".ics");
        }
        boolean exists = Files.exists(target);
        if (exists && JOptionPane.showConfirmDialog(this, "Replace " + target.getFileName() + "?",
                "Replace calendar file", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            CalendarExport.write(target, rows, zone, exists);
            message("Exported " + rows.size() + " appointment(s) to " + target.getFileName() + ".", false);
        } catch (IOException | DateTimeException | IllegalArgumentException ex) {
            message("Could not export: " + ex.getMessage(), true);
        }
    }

    private void select() {
        int row=table.getSelectedRow();
        boolean selected=row>=0 && row<visible.size();
        updateButton.setEnabled(selected && book!=null);
        deleteButton.setEnabled(selected && book!=null);
        if (!selected) return;
        Appointment appointment = visible.get(row).appointment();
        Object[] values=appointment.toTableRow();
        JTextField[] fields={titleField,dateField,startField,endField,locationField};
        for(int i=0;i<fields.length;i++) fields[i].setText(values[i].toString());
        repeatBox.setSelectedItem(appointment.getRepeat());
        untilField.setText(appointment.getRepeat() == Appointment.Repeat.NONE
                ? "" : appointment.getRepeatUntil().toString());
        reminderField.setValue(appointment.getReminderMinutes());
        if (appointment.getRepeat() != Appointment.Repeat.NONE) {
            message("Editing or deleting this row affects the whole series.", false);
        }
    }

    private void clear() {
        table.clearSelection();
        titleField.setText(""); locationField.setText("");
        dateField.setText(anchor.toString()); startField.setText("09:00"); endField.setText("10:00");
        repeatBox.setSelectedItem(Appointment.Repeat.NONE);
        untilField.setText("");
        reminderField.setValue(0);
    }

    private void save(boolean editing) {
        if(book==null) return;
        int row=table.getSelectedRow();
        if(editing && row<0) { message("Select an appointment first.",true); return; }
        try {
            Appointment input=Appointment.fromInput(titleField.getText(),dateField.getText(),
                    startField.getText(),endField.getText(),locationField.getText());
            Appointment.Repeat repeat = (Appointment.Repeat) repeatBox.getSelectedItem();
            LocalDate until = repeat == Appointment.Repeat.NONE ? null : LocalDate.parse(untilField.getText().trim());
            reminderField.commitEdit();
            Appointment a = new Appointment(input.getTitle(), input.getDate(), input.getStart(),
                    input.getEnd(), input.getLocation(), repeat, until, (Integer) reminderField.getValue());
            if(editing) book.update(book.getAll().indexOf(visible.get(row).appointment()),a);
            else book.add(a);
            anchor=a.getDate(); refresh(); clear();
            message((editing ? "Updated" : "Added") + ": " + a.getTitle()+". Saved. Total: "+book.getAll().size(),false);
        } catch (DateTimeException ex) {
            message("Enter a valid repeat end date as YYYY-MM-DD.", true);
        } catch (java.text.ParseException ex) {
            message("Enter a reminder from 0 to 1440 minutes.", true);
        } catch (IllegalArgumentException | IOException ex) { message("Not saved: "+ex.getMessage(),true); }
    }

    private void deleteSelected() {
        int row=table.getSelectedRow();
        if(book==null || row<0) return;
        Appointment a=visible.get(row).appointment();
        String item = a.getRepeat() == Appointment.Repeat.NONE ? a.getTitle() : "the entire series " + a.getTitle();
        if(JOptionPane.showConfirmDialog(this,"Delete " + item + "?", "Delete appointment",
                JOptionPane.YES_NO_OPTION)!=JOptionPane.YES_OPTION) return;
        try {
            book.delete(book.getAll().indexOf(a)); refresh(); clear();
            message("Deleted: " + a.getTitle() + ". Saved.",false);
        } catch(IOException ex) { message("Not deleted: "+ex.getMessage(),true); }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        topPanel = new javax.swing.JPanel();
        topPanel.setLayout(new java.awt.BorderLayout(8, 8));
        navigationPanel = new javax.swing.JPanel();
        navigationPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        viewLabel = new javax.swing.JLabel();
        viewLabel.setText("View");
        navigationPanel.add(viewLabel);
        periodBox = new javax.swing.JComboBox();
        navigationPanel.add(periodBox);
        anchorLabel = new javax.swing.JLabel();
        anchorLabel.setText("Date (YYYY-MM-DD)");
        navigationPanel.add(anchorLabel);
        anchorField = new javax.swing.JTextField();
        anchorField.setText("");
        navigationPanel.add(anchorField);
        showButton = new javax.swing.JButton();
        showButton.setText("Show");
        navigationPanel.add(showButton);
        previousButton = new javax.swing.JButton();
        previousButton.setText("Previous");
        navigationPanel.add(previousButton);
        todayButton = new javax.swing.JButton();
        todayButton.setText("Today");
        navigationPanel.add(todayButton);
        nextButton = new javax.swing.JButton();
        nextButton.setText("Next");
        navigationPanel.add(nextButton);
        topPanel.add(navigationPanel, java.awt.BorderLayout.NORTH);
        fieldsPanel = new javax.swing.JPanel();
        fieldsPanel.setLayout(new java.awt.GridLayout(2, 5, 10, 6));
        titleLabel = new javax.swing.JLabel();
        titleLabel.setText("Title (required)");
        fieldsPanel.add(titleLabel);
        dateLabel = new javax.swing.JLabel();
        dateLabel.setText("Date (YYYY-MM-DD)");
        fieldsPanel.add(dateLabel);
        startLabel = new javax.swing.JLabel();
        startLabel.setText("Start (HH:mm)");
        fieldsPanel.add(startLabel);
        endLabel = new javax.swing.JLabel();
        endLabel.setText("End (HH:mm)");
        fieldsPanel.add(endLabel);
        locationLabel = new javax.swing.JLabel();
        locationLabel.setText("Location (optional)");
        fieldsPanel.add(locationLabel);
        titleField = new javax.swing.JTextField();
        titleField.setText("");
        fieldsPanel.add(titleField);
        dateField = new javax.swing.JTextField();
        dateField.setText("");
        fieldsPanel.add(dateField);
        startField = new javax.swing.JTextField();
        startField.setText("");
        fieldsPanel.add(startField);
        endField = new javax.swing.JTextField();
        endField.setText("");
        fieldsPanel.add(endField);
        locationField = new javax.swing.JTextField();
        locationField.setText("");
        fieldsPanel.add(locationField);
        topPanel.add(fieldsPanel, java.awt.BorderLayout.CENTER);
        actionsPanel = new javax.swing.JPanel();
        actionsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        addButton = new javax.swing.JButton();
        addButton.setText("Add appointment");
        actionsPanel.add(addButton);
        updateButton = new javax.swing.JButton();
        updateButton.setText("Save changes");
        actionsPanel.add(updateButton);
        deleteButton = new javax.swing.JButton();
        deleteButton.setText("Delete selected");
        actionsPanel.add(deleteButton);
        clearButton = new javax.swing.JButton();
        clearButton.setText("Clear selection");
        actionsPanel.add(clearButton);
        exportButton = new javax.swing.JButton();
        exportButton.setText("Export view (.ics)");
        actionsPanel.add(exportButton);
        topPanel.add(actionsPanel, java.awt.BorderLayout.SOUTH);
        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);
        scrollPane = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        scrollPane.setViewportView(table);
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        statusLabel = new javax.swing.JLabel();
        statusLabel.setText("Select an appointment to edit or delete.");
        getContentPane().add(statusLabel, java.awt.BorderLayout.SOUTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Appointment Scheduler - Day 5");
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel navigationPanel;
    private javax.swing.JLabel viewLabel;
    private javax.swing.JComboBox periodBox;
    private javax.swing.JLabel anchorLabel;
    private javax.swing.JTextField anchorField;
    private javax.swing.JButton showButton;
    private javax.swing.JButton previousButton;
    private javax.swing.JButton todayButton;
    private javax.swing.JButton nextButton;
    private javax.swing.JPanel fieldsPanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel startLabel;
    private javax.swing.JLabel endLabel;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JTextField titleField;
    private javax.swing.JTextField dateField;
    private javax.swing.JTextField startField;
    private javax.swing.JTextField endField;
    private javax.swing.JTextField locationField;
    private javax.swing.JPanel actionsPanel;
    private javax.swing.JButton addButton;
    private javax.swing.JButton updateButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton clearButton;
    private javax.swing.JButton exportButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTable table;
    private javax.swing.JLabel statusLabel;
    // End of variables declaration//GEN-END:variables
}
