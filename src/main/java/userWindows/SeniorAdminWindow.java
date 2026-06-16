package userWindows;

import us12.UserManagement;

import javax.swing.*;
import java.awt.*;

public class SeniorAdminWindow extends JFrame {

    public SeniorAdminWindow() {
        setTitle("Lower Austria Tourist Portal — Senior Admin");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaled));
        add(logoLabel, BorderLayout.NORTH);  // ← fehlte!

        JPanel panel = new JPanel();
        JButton userButton = new JButton("User Management");
        panel.add(userButton);
        add(panel, BorderLayout.CENTER);

        userButton.addActionListener(e -> new UserManagement().setVisible(true));

        pack();
        setLocationRelativeTo(null);
    }
    }
