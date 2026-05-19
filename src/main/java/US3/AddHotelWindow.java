package US3;

import database.HibernateUtil;
import hotels.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.mindrot.jbcrypt.BCrypt;
import users.User;

import javax.swing.*;
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
     * Builds the form layout with 10 labelled input fields and a save button.
     * The save action validates input, persists the Hotel entity via Hibernate,
     * and closes the window on success.
     */
    public AddHotelWindow() {

        setTitle("Add Hotel");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));


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

        add(panel, BorderLayout.CENTER);
        //Save Button
       JButton saveButton = new JButton("Save");
       add(saveButton, BorderLayout.SOUTH);

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

           if (HotelValidator.isAnyFieldBlank(category, name, owner, contact, address,
                   city, citycode, phone, noRoom, noBed)) {
               JOptionPane.showMessageDialog(null, "Bitte alle Felder ausfüllen!");
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


           System.out.println("Category: " + category);
           System.out.println("Name: " + name);
           System.out.println("Owner: " + owner);
           System.out.println("Contact: " + contact);
           System.out.println("Address: " + address);
           System.out.println("City: " + city);
           System.out.println("CityCode: " + citycode);
           System.out.println("Phone: " + phone);
           System.out.println("NoRoom: " + noRoom);
           System.out.println("NoBed: " + noBed);

       });








    }
}
