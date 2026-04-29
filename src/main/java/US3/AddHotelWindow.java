package US3;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddHotelWindow extends JFrame {

    public AddHotelWindow() {

        setTitle("Add Hotel");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JPanel panel = new JPanel(new GridLayout(11, 2, 10, 10));


        JTextField categoryField = new JTextField();
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

       saveButton.addActionListener(e -> {


           String category = categoryField.getText();
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

           String sql = "INSERT INTO hotels (category, name, owner, contact, address, city, cityCode, phone, noRooms, noBeds) "
                   + "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

           try (Connection conn = DriverManager.getConnection(
                   "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                   "dev", "dev");
                PreparedStatement ps = conn.prepareStatement(sql)) {


               ps.setString(1, category);
               ps.setString(2, name);
               ps.setString(3, owner);
               ps.setString(4, contact);
               ps.setString(5,address );
               ps.setString(6, city);
               ps.setString(7, citycode);
               ps.setString(8,phone );
               ps.setInt(9, noRoomAsNumber);
               ps.setInt(10, noBedAsNumber);
               // TODO: Hier musst DU die restlichen 9 Felder selbst einfügen!
               // Reihenfolge wie im SQL: name, owner, contact, address, city, cityCode, phone, noRooms, noBeds
               // setString für Text, setInt für Zahlen


               ps.executeUpdate();

               JOptionPane.showMessageDialog(null, "Hotel erfolgreich gespeichert!");
               dispose();

           } catch (SQLException ex) {
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
