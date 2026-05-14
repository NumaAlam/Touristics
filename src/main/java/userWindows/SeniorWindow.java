package userWindows;


import MyApp.LoginWindow;
import MyApp.Menu;

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
            new Menu().setVisible(true);
            dispose();
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        JPanel southPanel = new JPanel();
        southPanel.add(backButton);
        southPanel.add(menuButton);
        add(southPanel, BorderLayout.SOUTH);
    }
}
