package us4_us5;

import javax.swing.*;
import java.awt.*;
import java.sql.*;


public class HotelEditWindow extends JFrame {

    private int hotelId;
    private JTextField idField;
    private JTextField categoryField;
    private JTextField nameField;
    private JTextField ownerField;
    private JTextField contactField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField cityCodeField;
    private JTextField phoneField;
    private JTextField noRoomsField;
    private JTextField noBedsField;

    public HotelEditWindow(int hotelId) {
        this.hotelId = hotelId;

        defineFrame();
        initFields();
        addComponents();
        loadHotelData();
        addButtonPanel();
    }

    private void defineFrame() {
        setTitle("Edit Hotel");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initFields() {
        idField = new JTextField();
        categoryField = new JTextField();
        nameField = new JTextField();
        ownerField = new JTextField();
        contactField = new JTextField();
        addressField = new JTextField();
        cityField = new JTextField();
        cityCodeField = new JTextField();
        phoneField = new JTextField();
        noRoomsField = new JTextField();
        noBedsField = new JTextField();

        idField.setEditable(false);
    }

    private void addComponents() {
        JPanel formPanel = new JPanel(new GridLayout(11, 2, 5, 5));

        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Owner:"));
        formPanel.add(ownerField);

        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);

        formPanel.add(new JLabel("Address:"));
        formPanel.add(addressField);

        formPanel.add(new JLabel("City:"));
        formPanel.add(cityField);

        formPanel.add(new JLabel("City Code:"));
        formPanel.add(cityCodeField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Number of Rooms:"));
        formPanel.add(noRoomsField);

        formPanel.add(new JLabel("Number of Beds:"));
        formPanel.add(noBedsField);

        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadHotelData() {
        String sql = """
            SELECT
                id,
                category,
                name,
                owner,
                contact,
                address,
                city,
                cityCode,
                phone,
                noRooms,
                noBeds
            FROM dbo.hotels
            WHERE id = ?;
            """;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, hotelId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    idField.setText(String.valueOf(rs.getInt("id")));
                    categoryField.setText(rs.getString("category"));
                    nameField.setText(rs.getString("name"));
                    ownerField.setText(rs.getString("owner"));
                    contactField.setText(rs.getString("contact"));
                    addressField.setText(rs.getString("address"));
                    cityField.setText(rs.getString("city"));
                    cityCodeField.setText(rs.getString("cityCode"));
                    phoneField.setText(rs.getString("phone"));
                    noRoomsField.setText(String.valueOf(rs.getInt("noRooms")));
                    noBedsField.setText(String.valueOf(rs.getInt("noBeds")));
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "No hotel found for ID: " + hotelId,
                            "Hotel not found",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton saveButton = new JButton("Save changes");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addSaveButtonFunction(saveButton);
        addCloseButtonFunction(closeButton);
    }

    private void addCloseButtonFunction(JButton closeButton) {
        closeButton.addActionListener(e -> {
            dispose();
        });
    }

    private void addSaveButtonFunction(JButton saveButton) {
        saveButton.addActionListener(e -> {
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Do you really want to save the changes?",
                    "Confirm changes",
                    JOptionPane.YES_NO_OPTION
            );

            if (answer == JOptionPane.YES_OPTION) {
                saveHotelData();
            }
        });
    }

    private void saveHotelData() {
        int noRooms;
        int noBeds;

        try {
            noRooms = Integer.parseInt(noRoomsField.getText().trim());
            noBeds = Integer.parseInt(noBedsField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Number of rooms and number of beds must be valid numbers.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (noRooms <= 0 || noBeds <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Number of rooms and number of beds must be greater than 0.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (categoryField.getText().trim().isEmpty()
                || nameField.getText().trim().isEmpty()
                || ownerField.getText().trim().isEmpty()
                || contactField.getText().trim().isEmpty()
                || addressField.getText().trim().isEmpty()
                || cityField.getText().trim().isEmpty()
                || cityCodeField.getText().trim().isEmpty()
                || phoneField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Missing input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String sql = """
            UPDATE dbo.hotels
            SET
                category = ?,
                name = ?,
                owner = ?,
                contact = ?,
                address = ?,
                city = ?,
                cityCode = ?,
                phone = ?,
                noRooms = ?,
                noBeds = ?
            WHERE id = ?;
            """;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev");
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, categoryField.getText().trim());
            stmt.setString(2, nameField.getText().trim());
            stmt.setString(3, ownerField.getText().trim());
            stmt.setString(4, contactField.getText().trim());
            stmt.setString(5, addressField.getText().trim());
            stmt.setString(6, cityField.getText().trim());
            stmt.setString(7, cityCodeField.getText().trim());
            stmt.setString(8, phoneField.getText().trim());
            stmt.setInt(9, noRooms);
            stmt.setInt(10, noBeds);
            stmt.setInt(11, hotelId);

            int updatedRows = stmt.executeUpdate();

            if (updatedRows == 1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Hotel data saved successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No hotel data was updated.",
                        "Update failed",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
