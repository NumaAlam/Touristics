package MyApp;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {
        public LoginWindow() {
            setTitle("Login");
            setSize(300, 150);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JButton button = new JButton("Login");
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            JPanel panel = new JPanel(new GridLayout(2,2,10,10));

            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            panel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            add(panel, BorderLayout.CENTER);
            add(button, BorderLayout.SOUTH);

            button.addActionListener(e -> {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                if (username.isBlank()) {
                    JOptionPane.showMessageDialog(null, "Please enter a username");
                    return;
                }
                if (username.equals("Junior") && password.equals("Junior")) {
                    new JuniorWindow("Welcome Junior").setVisible(true);
                    dispose();
                } else if (username.equals("Senior") && password.equals("Senior")) {
                    new SeniorWindow("Welcome Senior").setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }


                System.out.println("Login");
            });




        }
}
