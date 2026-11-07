package demo;

import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NewJFrame1 extends JFrame {
    public NewJFrame1() {
        setTitle("Time Matters - Appointment Scheduler");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JPanel page = new JPanel(new GridBagLayout());
        page.setBorder(BorderFactory.createEmptyBorder(36, 36, 36, 36));
        JPanel content = new JPanel(new GridBagLayout());
        GridBagConstraints row = new GridBagConstraints();
        row.gridx = 0;
        row.fill = GridBagConstraints.HORIZONTAL;
        row.insets = new Insets(8, 0, 8, 0);
        JLabel title = new JLabel("Time Matters", SwingConstants.CENTER);
        JLabel tagline = new JLabel("YOUR TIME. YOUR SCHEDULE", SwingConstants.CENTER);
        JSeparator divider = new JSeparator();
        JButton open = new JButton("Open scheduler");
        JButton documents = new JButton("Submission files");
        documents.addActionListener(e -> openDocuments());
        JLabel caption = new JLabel("Plan your day with Time Matters", SwingConstants.CENTER);
        JComponent[] items = {title, tagline, divider, open, documents, caption};
        for (int i = 0; i < items.length; i++) {
            row.gridy = i;
            content.add(items[i], row);
        }
        page.add(content);
        setContentPane(page);
        AppTheme.style(page);
        AppTheme.welcome(page);
        title.setFont(new Font("Segoe UI", Font.BOLD, 38));
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        caption.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        divider.setPreferredSize(new Dimension(320, 1));
        open.setPreferredSize(new Dimension(320, 42));
        AppTheme.primary(open);
        documents.setPreferredSize(new Dimension(320, 36));
        documents.setBackground(new Color(20, 26, 31));
        documents.setForeground(AppTheme.CYAN);
        open.addActionListener(e -> {
            open.setEnabled(false);
            try {
                new SchedulerFrame().setVisible(true);
                dispose();
            } catch (RuntimeException ex) {
                open.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Could not open scheduler: " + ex.getMessage(),
                        "Time Matters", JOptionPane.ERROR_MESSAGE);
            }
        });
        getRootPane().setDefaultButton(open);
        setMinimumSize(new Dimension(500, 360));
        setSize(640, 480);
        setLocationRelativeTo(null);
    }

    static Path submissionDirectory() throws IOException {
        Path location = Path.of(System.getProperty("user.dir")).toAbsolutePath();
        while (location != null) {
            Path folder = location.resolve("Submission");
            if (Files.isDirectory(folder)) return folder;
            location = location.getParent();
        }
        throw new IOException("Submission folder not found. Open the app from its project folder.");
    }

    private void openDocuments() {
        try {
            if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                throw new IOException("Open the Submission folder in File Explorer.");
            }
            Desktop.getDesktop().open(submissionDirectory().toFile());
        } catch (IOException | SecurityException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Submission files", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) { Main.main(args); }
}
