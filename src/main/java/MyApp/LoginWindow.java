package MyApp;

import database.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mindrot.jbcrypt.BCrypt;
import userWindows.HeadWindow;
import userWindows.HotelRepWindow;
import userWindows.SeniorAdminWindow;
import userWindows.SeniorWindow;
import users.User;

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

                try (Session session = HibernateUtil.getSessionFactory().openSession();) {
                    User user = session
                            .createQuery("from User where username = :username", User.class)
                            .setParameter("username", username)
                            .uniqueResult();
                    if (user == null) {
                        JOptionPane.showMessageDialog(null, "User or password invalid"); //if the user is not found, an error message is displayed
                    } else if (BCrypt.checkpw(password, user.getPasswordHash())) { //if the password is correct, the user is logged in
                        MyApp.Session.currentRole = user.getRole();
                        MyApp.Session.canDelete = Boolean.TRUE.equals(user.getCanDelete());

                        if (user.getRole().equals("Hotel Representative")) {
                            new HotelRepWindow(user.getHotelID()).setVisible(true);
                            dispose();
                        } else if (user.getRole().equals("Senior")) {
                            new SeniorWindow("Welcome Senior").setVisible(true);
                            dispose();
                        } else if (user.getRole().equals("Senior_Admin")) {
                            new SeniorAdminWindow().setVisible(true);
                            dispose();
                        } else {
                            new HeadWindow().setVisible(true);
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "User or password invalid"); // if the password is incorrect, an error message is displayed
                    }

                } catch (HibernateException ex) {
                    System.err.println("Database error: " + ex.getMessage());
                }


            });




        }
}
