package demo;

import java.awt.*;
import javax.swing.*;

public class WelcomeFrame extends JFrame {
    public WelcomeFrame() {
        initComponents();
        AppTheme.style(getContentPane());
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(32, 40, 32, 40));
        heroPanel.setBackground(AppTheme.NAVY);
        heroPanel.setBorder(BorderFactory.createEmptyBorder(30, 32, 30, 32));
        for (JLabel label : new JLabel[]{brandLabel, welcomeLabel, introLabel}) label.setForeground(Color.WHITE);
        brandLabel.setFont(brandLabel.getFont().deriveFont(Font.BOLD, 12f));
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 32f));
        introLabel.setFont(introLabel.getFont().deriveFont(17f));
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        startPanel.setBorder(BorderFactory.createEmptyBorder(4, 32, 0, 32));
        hintLabel.setForeground(AppTheme.NAVY);
        AppTheme.primary(startButton);
        startButton.addActionListener(e -> openScheduler());
        getRootPane().setDefaultButton(startButton);
        setMinimumSize(new Dimension(800, 540));
        setSize(900, 620);
        setLocationRelativeTo(null);
    }

    private void openScheduler() {
        startButton.setEnabled(false);
        try {
            SchedulerFrame scheduler = new SchedulerFrame();
            scheduler.setVisible(true);
            dispose();
        } catch (RuntimeException ex) {
            startButton.setEnabled(true);
            JOptionPane.showMessageDialog(this, "Could not open the scheduler: " + ex.getMessage(),
                    "Appointment Scheduler", JOptionPane.ERROR_MESSAGE);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        heroPanel = new javax.swing.JPanel();
        heroPanel.setLayout(new java.awt.BorderLayout(0, 24));
        brandLabel = new javax.swing.JLabel();
        brandLabel.setText("GROUP 12  /  APPOINTMENT SCHEDULER");
        heroPanel.add(brandLabel, java.awt.BorderLayout.NORTH);
        welcomeLabel = new javax.swing.JLabel();
        welcomeLabel.setText("A little planning. A clearer day.");
        heroPanel.add(welcomeLabel, java.awt.BorderLayout.CENTER);
        introLabel = new javax.swing.JLabel();
        introLabel.setText("Your appointments, organised in one place.");
        heroPanel.add(introLabel, java.awt.BorderLayout.SOUTH);
        getContentPane().add(heroPanel, java.awt.BorderLayout.NORTH);
        featuresPanel = new javax.swing.JPanel();
        featuresPanel.setLayout(new java.awt.GridLayout(3, 1, 0, 18));
        scheduleLabel = new javax.swing.JLabel();
        scheduleLabel.setText("Plan your day, week or month");
        featuresPanel.add(scheduleLabel);
        remindersLabel = new javax.swing.JLabel();
        remindersLabel.setText("Stay on track with reminders and repeat appointments");
        featuresPanel.add(remindersLabel);
        exportLabel = new javax.swing.JLabel();
        exportLabel.setText("Take your schedule with you using calendar export");
        featuresPanel.add(exportLabel);
        getContentPane().add(featuresPanel, java.awt.BorderLayout.CENTER);
        startPanel = new javax.swing.JPanel();
        startPanel.setLayout(new java.awt.GridLayout(2, 1, 0, 18));
        hintLabel = new javax.swing.JLabel();
        hintLabel.setText("Ready to plan your next appointment?");
        startPanel.add(hintLabel);
        startButton = new javax.swing.JButton();
        startButton.setText("Get started  →");
        startPanel.add(startButton);
        getContentPane().add(startPanel, java.awt.BorderLayout.SOUTH);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Appointment Scheduler");
        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel heroPanel;
    private javax.swing.JLabel brandLabel;
    private javax.swing.JLabel welcomeLabel;
    private javax.swing.JLabel introLabel;
    private javax.swing.JPanel featuresPanel;
    private javax.swing.JLabel scheduleLabel;
    private javax.swing.JLabel remindersLabel;
    private javax.swing.JLabel exportLabel;
    private javax.swing.JPanel startPanel;
    private javax.swing.JLabel hintLabel;
    private javax.swing.JButton startButton;
    // End of variables declaration//GEN-END:variables
}
