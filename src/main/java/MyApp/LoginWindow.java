package MyApp;

import org.mindrot.jbcrypt.BCrypt;
import userWindows.HeadWindow;
import userWindows.HotelRepWindow;
import userWindows.SeniorAdminWindow;
import userWindows.SeniorWindow;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

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

                try (Connection conn = DriverManager.getConnection(
                        "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                        "dev",
                        "dev")) {
                    PreparedStatement ps = conn.prepareStatement("SELECT password_hash, hotelID, role FROM users WHERE username = ?"); // Selects the password hash, hotelID, and role from the users table where the username matches the input
                    ps.setString(1,username);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
                        JOptionPane.showMessageDialog(null, "User not found"); //if the user is not found, an error message is displayed
                    } else if (BCrypt.checkpw(password, rs.getString("password_hash"))) { //if the password is correct, the user is logged in
                        if (rs.getString("role").equals("Hotel Representative")) {
                            new HotelRepWindow(rs.getInt("hotelID")).setVisible(true);
                            dispose();
                        } else if (rs.getString("role").equals("Senior")) {
                            new SeniorWindow("Welcome Senior").setVisible(true);
                            dispose();
                        } else if (rs.getString("role").equals("Senior_Admin")) {
                            new SeniorAdminWindow().setVisible(true);
                            dispose();
                        } else {
                            new HeadWindow().setVisible(true);
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid password"); // if the password is incorrect, an error message is displayed
                    }

                } catch (SQLException e2) { // e2 because we need e for the button error msg and e2 for the database error msg
                    System.err.println("Database error: " + e2.getMessage());
                }


            });




        }
}
