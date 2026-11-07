package demo;

import java.awt.*;
import javax.swing.*;

public class WelcomeFrame extends JFrame {
    public WelcomeFrame() {
        initComponents();
        AppTheme.style(getContentPane());
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));
        welcomeLabel.setFont(welcomeLabel.getFont().deriveFont(Font.BOLD, 24f));
        featuresPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        startButton.addActionListener(e -> openScheduler());
        getRootPane().setDefaultButton(startButton);
        setMinimumSize(new Dimension(620, 420));
        setSize(680, 460);
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
        brandLabel.setText("Group 12");
        heroPanel.add(brandLabel, java.awt.BorderLayout.NORTH);
        welcomeLabel = new javax.swing.JLabel();
        welcomeLabel.setText("Welcome to Appointment Scheduler");
        heroPanel.add(welcomeLabel, java.awt.BorderLayout.CENTER);
        introLabel = new javax.swing.JLabel();
        introLabel.setText("Manage your appointments and reminders.");
        heroPanel.add(introLabel, java.awt.BorderLayout.SOUTH);
        getContentPane().add(heroPanel, java.awt.BorderLayout.NORTH);
        featuresPanel = new javax.swing.JPanel();
        featuresPanel.setLayout(new java.awt.GridLayout(3, 1, 0, 18));
        scheduleLabel = new javax.swing.JLabel();
        scheduleLabel.setText("View appointments by day, week or month.");
        featuresPanel.add(scheduleLabel);
        remindersLabel = new javax.swing.JLabel();
        remindersLabel.setText("Add reminders and repeat appointments.");
        featuresPanel.add(remindersLabel);
        exportLabel = new javax.swing.JLabel();
        exportLabel.setText("Export appointments to your calendar.");
        featuresPanel.add(exportLabel);
        getContentPane().add(featuresPanel, java.awt.BorderLayout.CENTER);
        startPanel = new javax.swing.JPanel();
        startPanel.setLayout(new java.awt.GridLayout(2, 1, 0, 18));
        hintLabel = new javax.swing.JLabel();
        hintLabel.setText("Choose Get started to open your schedule.");
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
