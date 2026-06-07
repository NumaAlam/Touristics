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



/**
 * Swing window for adding new hotel master data records (User Story US3).
 * Provides input fields for all 10 hotel attributes (category, name, owner,
 * contact, address, city, citycode, phone, noRooms, noBeds) and persists
 * validated input into the hotels table via Hibernate.
 *
 * Validation is delegated to HotelValidator. On successful save, the user
 * receives a confirmation pop-up and the window closes automatically.
 *
 * @author Deniz Kuskan
 * @since Sprint 1
 */
public class AddHotelWindow extends JFrame {

    /**
     * Constructs and displays the Add Hotel window.
     * Builds the form layout with 10 labeled input fields and a save button.
     * The save action validates input, persists the Hotel entity via Hibernate,
     * and closes the window on success.
     */
    public AddHotelWindow() {

        setTitle("Lower Austria Tourist Portal — Add Hotel");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaled));
        add(logoLabel, BorderLayout.NORTH);


        JPanel panel = new JPanel(new GridLayout(12, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));


        JComboBox<String> categoryField = new JComboBox<>(new String[]{"*****", "****", "***", "**", "*"});
        JTextField nameField = new JTextField();
        JTextField ownerField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField cityField = new JTextField();
        JTextField citycodeField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField noRoomField = new JTextField();
        JTextField noBedField = new JTextField();

        //US30 Checkbox Text
        JCheckBox gdprConfirmationCheckBox = new JCheckBox(
                "<html>Please review the entered hotel data carefully before saving. " +
                        "By confirming, you declare that the information is correct, " +
                        "that you are authorized to submit or change this data for your assigned hotel, " +
                        "and that the data may be processed for the purposes of the NOE-TO tourism portal.</html>"
        );
        //US 30 Button only visible for Hotel Representative
        gdprConfirmationCheckBox.setVisible("Hotel Representative".equals(MyApp.Session.currentRole));


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

        // US30: Displays the confirmation checkbox when a hotel representative adds hotel data.
        panel.add(new JLabel("Confirmation"));
        panel.add(gdprConfirmationCheckBox);

        add(panel, BorderLayout.CENTER);
        //Save Button
       JButton saveButton = new JButton("Save");
       add(saveButton, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);



        // Save handler: validates all fields, rejects blank or non-positive numeric input,
        // then performs an INSERT into the hotels table via PreparedStatement.
        // Errors are surfaced to the user through JOptionPane dialogs
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

           // US30: Prevents hotel representatives from submitting hotel data without explicit GDPR confirmation.
           if ("Hotel Representative".equals(MyApp.Session.currentRole)
                   && !gdprConfirmationCheckBox.isSelected()) {

               JOptionPane.showMessageDialog(
                       null,
                       "Please confirm the correctness and authorization statement before saving.",
                       "Confirmation required",
                       JOptionPane.WARNING_MESSAGE
               );
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

           int noRoomAsNumber = Integer.parseInt(noRoom);
           int noBedAsNumber = Integer.parseInt(noBed);

           // Build the Hotel entity
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

               // Persist the hotel — Hibernate fills in the generated ID
               session.persist(hotel);
               session.flush(); // ensures hotel.getId() is populated before we use it



               // Create the corresponding user account
               int newHotelId = hotel.getId();
               String username = "User" + newHotelId;
               String rawPassword = "Password" + newHotelId;
               String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

               User user = new User();
               user.setUsername(username);
               user.setPasswordHash(hashedPassword);
               user.setHotelID(newHotelId);
               user.setRole("Hotel Representative");

               session.persist(user);
               tx.commit();

               JOptionPane.showMessageDialog(null,
                       "Success!" + "\n" +
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
