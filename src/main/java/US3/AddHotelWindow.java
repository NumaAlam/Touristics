package US3;

import database.HibernateUtil;
import hotels.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import users.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddHotelWindow extends JFrame {

    private JComboBox<String> categoryField;
    private JTextField nameField;
    private JTextField ownerField;
    private JTextField contactField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField citycodeField;
    private JTextField phoneField;
    private JTextField noRoomField;
    private JTextField noBedField;
    private JCheckBox gdprConfirmationCheckBox;
    private JPanel panel;

    public AddHotelWindow() {
        defineFrame();
        initComponents();
        addComponents();

        pack();
        setSize(600, 700);
        setLocationRelativeTo(null);
    }

    private void defineFrame() {
        setTitle("Lower Austria Tourist Portal — Add Hotel");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        panel = new JPanel(new GridLayout(12, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        categoryField = new JComboBox<>(new String[]{"*****", "****", "***", "**", "*"});
        nameField = new JTextField();
        ownerField = new JTextField();
        contactField = new JTextField();
        addressField = new JTextField();
        cityField = new JTextField();
        citycodeField = new JTextField();
        phoneField = new JTextField();
        noRoomField = new JTextField();
        noBedField = new JTextField();

        gdprConfirmationCheckBox = new JCheckBox(
                "<html>Please review the entered hotel data carefully before saving. " +
                        "By confirming, you declare that the information is correct, " +
                        "that you are authorized to submit or change this data for your assigned hotel, " +
                        "and that the data may be processed for the purposes of the NOE-TO tourism portal.</html>"
        );
        gdprConfirmationCheckBox.setVisible("Hotel Representative".equals(MyApp.Session.currentRole));
    }

    private void addComponents() {
        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        add(new JLabel(new ImageIcon(scaled)), BorderLayout.NORTH);

        panel.add(new JLabel("Category"));
        panel.add(categoryField);
        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Owner"));
        panel.add(ownerField);
        panel.add(new JLabel("Contact"));
        panel.add(contactField);
        panel.add(new JLabel("Address"));
        panel.add(addressField);
        panel.add(new JLabel("City"));
        panel.add(cityField);
        panel.add(new JLabel("CityCode"));
        panel.add(citycodeField);
        panel.add(new JLabel("Phone"));
        panel.add(phoneField);
        panel.add(new JLabel("NoRoom"));
        panel.add(noRoomField);
        panel.add(new JLabel("NoBed"));
        panel.add(noBedField);
        panel.add(new JLabel("Confirmation"));
        panel.add(gdprConfirmationCheckBox);

        add(panel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        add(saveButton, BorderLayout.SOUTH);

        addSaveButtonFunction(saveButton);
    }

    private void addSaveButtonFunction(JButton saveButton) {
        saveButton.addActionListener(e -> {

            String category = (String) categoryField.getSelectedItem();
            String name = nameField.getText();
            String owner = ownerField.getText();
            String contact = contactField.getText();
            String address = addressField.getText();
            String city = cityField.getText();
            String citycode = citycodeField.getText();
            String phone = phoneField.getText();
            String noRoom = noRoomField.getText();
            String noBed = noBedField.getText();

            if ("Hotel Representative".equals(MyApp.Session.currentRole)
                    && !gdprConfirmationCheckBox.isSelected()) {
                JOptionPane.showMessageDialog(null,
                        "Please confirm the correctness and authorization statement before saving.",
                        "Confirmation required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (HotelValidator.isAnyFieldBlank(category, name, owner, contact, address,
                    city, citycode, phone, noRoom, noBed)) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields!");
                return;
            }

            if (!HotelValidator.isPositiveNumber(noRoom) || !HotelValidator.isPositiveNumber(noBed)) {
                JOptionPane.showMessageDialog(null, "NoRooms and NoBeds must be positive numbers!");
                return;
            }

            if (!HotelValidator.isPositiveNumber(citycode) || !HotelValidator.isPositiveNumber(phone)) {
                JOptionPane.showMessageDialog(null, "CityCode and Phone must be positive numbers!");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Save hotel \"" + name + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            int noRoomAsNumber = Integer.parseInt(noRoom);
            int noBedAsNumber = Integer.parseInt(noBed);

            Hotel hotel = new Hotel();
            hotel.setCategory(category);
            hotel.setName(name);
            hotel.setOwner(owner);
            hotel.setContact(contact);
            hotel.setAddress(address);
            hotel.setCity(city);
            hotel.setCityCode(citycode);
            hotel.setPhone(phone);
            hotel.setNoRooms(noRoomAsNumber);
            hotel.setNoBeds(noBedAsNumber);

            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {

                tx = session.beginTransaction();

                session.persist(hotel);
                session.flush();

                int newHotelId = hotel.getId();
                String username = "User" + newHotelId;
                String rawPassword = java.util.UUID.randomUUID().toString().substring(0, 8);
                String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

                User user = new User();
                user.setUsername(username);
                user.setPasswordHash(hashedPassword);
                user.setHotelID(newHotelId);
                user.setRole("Hotel Representative");

                session.persist(user);
                tx.commit();

                JOptionPane.showMessageDialog(null,
                        "Success!\n" +
                                "Your Username is: " + username + "\n" +
                                "Your Password is: " + rawPassword);
                dispose();

            } catch (Exception ex) {
                if (tx != null) tx.rollback();
                JOptionPane.showMessageDialog(null, "Datenbank-Fehler: " + ex.getMessage());
            }
        });
    }
}