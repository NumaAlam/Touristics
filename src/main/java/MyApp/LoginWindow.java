package MyApp;

import database.HibernateUtil;
import org.hibernate.HibernateException;
import org.mindrot.jbcrypt.BCrypt;
import userWindows.HeadWindow;
import userWindows.HotelRepWindow;
import userWindows.SeniorAdminWindow;
import userWindows.SeniorWindow;
import users.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginWindow extends JFrame {
    public LoginWindow() {
        setTitle("Lower Austria tourist portal - Login");
        setLayout(new BorderLayout());
        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaled));
        add(logoLabel, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 20));
        panel.setBorder(new EmptyBorder(15,15,15,15));
        JLabel usernameLabel = new JLabel("Username:");
        panel.add(usernameLabel);
        JTextField usernameField = new JTextField();
        panel.add(usernameField);
        JLabel passwordLabel = new JLabel("Password:");
        panel.add(passwordLabel);
        JPasswordField passwordField = new JPasswordField();
        panel.add(passwordField);
        add(panel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setBorder(new EmptyBorder(0,20,20,20));
        JButton loginButton = new JButton("Login");
        southPanel.add(loginButton);
        add(southPanel, BorderLayout.SOUTH);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isBlank()) {
                JOptionPane.showMessageDialog(null, "Please enter a username");
                return;
            }

            try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
                User user = session
                        .createQuery("from User where username = :username", User.class)
                        .setParameter("username", username)
                        .uniqueResult();

                if (user == null) {
                    JOptionPane.showMessageDialog(null, "User or password invalid");
                    return;
                }

                if (!BCrypt.checkpw(password, user.getPasswordHash())) {
                    JOptionPane.showMessageDialog(null, "User or password invalid");
                    return;
                }

                // Store the current user's role globally for permission checks in other windows.
                MyApp.Session.currentRole = user.getRole();

                // Store delete permission globally for features such as deleting hotel data.
                MyApp.Session.canDelete = Boolean.TRUE.equals(user.getCanDelete());

                // US25: Store the assigned hotel ID only for hotel representatives.
                // Other roles must not keep a hotel restriction from a previous login.
                if (user.getRole().equals("Hotel Representative")) {
                    MyApp.Session.currentHotelId = user.getHotelID();

                    new HotelRepWindow(user.getHotelID()).setVisible(true);
                    dispose();

                } else {
                    // US25: Clear the hotel restriction for all other roles.
                    // This prevents a hotel ID from a previous hotel representative login from being reused.
                    MyApp.Session.currentHotelId = null;

                    if (user.getRole().equals("Senior")) {
                        new SeniorWindow("Welcome Senior").setVisible(true);
                        dispose();

                    } else if (user.getRole().equals("Senior_Admin")) {
                        new SeniorAdminWindow().setVisible(true);
                        dispose();

                    } else {
                        new HeadWindow().setVisible(true);
                        dispose();
                    }
                }

            } catch (HibernateException ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Database error: " + ex.getMessage(),
                        "Database error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
        getRootPane().setDefaultButton(loginButton);
        pack();
        setLocationRelativeTo(null);
    }
}
