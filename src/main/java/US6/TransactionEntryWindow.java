package US6;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import US3.HotelValidator;

public class TransactionEntryWindow  extends JFrame {

    public TransactionEntryWindow(int hotelID, String hotelName) {
        setTitle("Transactional Data Entry");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel transactionPanel = new JPanel(new GridLayout(7, 2, 10, 10));

        JTextField idField = new JTextField(String.valueOf(hotelID));
        idField.setEditable(false);

        JTextField nameField = new JTextField(hotelName);
        nameField.setEditable(false);

        JTextField roomsField = new JTextField();
        JTextField bedsField = new JTextField();
        JTextField roomOccField = new JTextField();
        JTextField bedOccField = new JTextField();

        transactionPanel.add(new JLabel("Hotel ID:"));
        transactionPanel.add(idField);

        transactionPanel.add(new JLabel("Hotel Name:"));
        transactionPanel.add(nameField);

        transactionPanel.add(new JLabel("Number of Rooms:"));
        transactionPanel.add(roomsField);

        transactionPanel.add(new JLabel("Number of Beds:"));
        transactionPanel.add(bedsField);

        transactionPanel.add(new JLabel("Room Occupancy:"));
        transactionPanel.add(roomOccField);

        transactionPanel.add(new JLabel("Bed Occupancy:"));
        transactionPanel.add(bedOccField);

        JButton saveButton = new JButton("Save");

        saveButton.addActionListener(e -> {
            String roomsText = roomsField.getText().trim();
            String bedsText = bedsField.getText().trim();
            String roomOccText = roomOccField.getText().trim();
            String bedOccText = bedOccField.getText().trim();

            // 1. Validierung
            if(roomsText.isBlank()
            || bedsText.isBlank()
                || roomOccText.isBlank()
                || bedOccText.isBlank()) {
                JOptionPane.showMessageDialog(this, "Please fill all the fields!");
                return;
            }

            if (!HotelValidator.isPositiveNumber(roomsText)
                    || !HotelValidator.isPositiveNumber(bedsText)
                    || !HotelValidator.isPositiveNumber(roomOccText)
                    || !HotelValidator.isPositiveNumber(bedOccText)) {
                JOptionPane.showMessageDialog(this, "All values must be positive numbers!");
                return;
            }

            // 2. Wenn alles ok ist -> parsen
            int rooms = Integer.parseInt(roomsText);
            int beds = Integer.parseInt(bedsText);
            int roomOcc = Integer.parseInt(roomOccText);
            int bedOcc = Integer.parseInt(bedOccText);

            int year = java.time.LocalDate.now().getYear();
            int month = java.time.LocalDate.now().getMonthValue();

            String sql = "INSERT INTO occupancies (id, rooms, usedrooms, beds, usedbeds, year, month) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                    "dev",
                    "dev");
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, hotelID);
                ps.setInt(2, rooms);
                ps.setInt(3, roomOcc);
                ps.setInt(4, beds);
                ps.setInt(5, bedOcc);
                ps.setInt(6, year);
                ps.setInt(7, month);

                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Transaction Successfully saved!");
                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error:" + ex.getMessage());
            }

//
        });
        add(transactionPanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);

    }


}


