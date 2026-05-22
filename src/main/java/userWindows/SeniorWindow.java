package userWindows;


import MyApp.LoginWindow;
import MyApp.Menu;
import US20.SaveBackup;

import javax.swing.*;
import java.awt.*;

public class SeniorWindow extends JFrame {

    public SeniorWindow(String text) {

        setTitle(text);
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton menuButton = new JButton("Main Menu");
        menuButton.addActionListener(e -> {
            new Menu(null).setVisible(true);
            dispose();
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        JButton backupButton = new JButton("Create Backup");
        backupButton.addActionListener(e -> {
            SaveBackup saveBackup = new SaveBackup();
            boolean success = saveBackup.createBackup();
            if (success) {
                JOptionPane.showMessageDialog(this, "Backup created successfully.");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Backup failed. See console for details.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel southPanel = new JPanel();
        southPanel.add(backButton);
        southPanel.add(menuButton);
        southPanel.add(backupButton);
        add(southPanel, BorderLayout.SOUTH);
    }
}
