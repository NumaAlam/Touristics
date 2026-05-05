package us4_us5;

import javax.swing.*; // Swing components used for the graphical user interface, such as JFrame, JTextField, JButton, JPanel and JOptionPane.
import java.awt.*; // AWT layout classes, especially BorderLayout and GridLayout, used to arrange the edit form.
import java.sql.*; // SQL classes used for database connection, PreparedStatement, ResultSet and SQL exception handling.


public class HotelEditWindow extends JFrame {

    private int hotelId; // Stores ID of the hotel selected in HotelOverviewWindow.

    // Text fields for all hotel master data fields.
    private JTextField idField;
    private JComboBox<String> categoryComboBox;
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
        this.hotelId = hotelId; // Saves selected hotel ID for loading and updating the correct database record.

        defineFrame(); // Defines the basic frame settings.
        initFields(); // Creates all text fields.
        addComponents(); // Adds labels and text fields to the form.
        loadHotelData(); // Loads the selected hotel data from the database.
        addButtonPanel(); // Adds Save and Close buttons.
    }

    private void defineFrame() {
        setTitle("Edit Hotel"); // Sets title of the edit window.
        setSize(500, 500); // Sets window size for the full master data form.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only closes this edit window, not the whole application.
        setLocationRelativeTo(null); // Centers the window on the screen.
        setLayout(new BorderLayout()); // Uses BorderLayout to place the form in the center and buttons at the bottom.
    }

    private void initFields() {
        // Creates text fields for all master data fields of the hotel.
        idField = new JTextField();
        categoryComboBox = new JComboBox<>(new String[]{"*****", "****", "***", "**", "*"});
        nameField = new JTextField();
        ownerField = new JTextField();
        contactField = new JTextField();
        addressField = new JTextField();
        cityField = new JTextField();
        cityCodeField = new JTextField();
        phoneField = new JTextField();
        noRoomsField = new JTextField();
        noBedsField = new JTextField();

        idField.setEditable(false); // The hotel ID is automatically assigned and must not be edited by the user.
    }

    private void addComponents() {
        // Creates a form layout with 11 rows and 2 columns:
        // one label and one text field per hotel master data field.
        JPanel formPanel = new JPanel(new GridLayout(11, 2, 5, 5));

        // Adds all labels and their matching text fields to the form.
        formPanel.add(new JLabel("ID:"));
        formPanel.add(idField);

        formPanel.add(new JLabel("Category:"));
        formPanel.add(categoryComboBox);

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

        // Adds padding around the form so the fields do not touch the window border.
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adds the form to the center of the window.
        add(formPanel, BorderLayout.CENTER);
    }

    private void loadHotelData() {
        // SQL query for loading the complete master data record of one selected hotel.
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
        // Uses PreparedStatement because the hotel ID is passed as a parameter.
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Sets the selected hotel ID as the parameter for the WHERE clause.
            stmt.setInt(1, hotelId);

            // Executes the query and reads the result.
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Fills the form fields with the hotel data from the database.
                    idField.setText(String.valueOf(rs.getInt("id")));
                    categoryComboBox.setSelectedItem(rs.getString("category"));
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
                    // Shows a warning if no hotel exists for the selected ID.
                    JOptionPane.showMessageDialog(
                            this,
                            "No hotel found for ID: " + hotelId,
                            "Hotel not found",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }

        } catch (SQLException e) {
            // Shows an error dialog if loading the hotel data fails.
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addButtonPanel() {

        JPanel buttonPanel = new JPanel(); // Panel used to place Save and Close buttons below the form.

        JButton saveButton = new JButton("Save changes"); // Button for saving changes to the database.
        JButton closeButton = new JButton("Close"); // Button for closing the edit window without saving.

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        // Places the button panel at the bottom of the window.
        add(buttonPanel, BorderLayout.SOUTH);

        // Connects the buttons to their functions.
        addSaveButtonFunction(saveButton);
        addCloseButtonFunction(closeButton);
    }

    private void addCloseButtonFunction(JButton closeButton) {
        // Closes only the edit window.
        closeButton.addActionListener(e -> {
            dispose();
        });
    }

    private void addSaveButtonFunction(JButton saveButton) {
        saveButton.addActionListener(e -> {
            // Makes it so changes are only saved after explicit confirmation.
            int answer = JOptionPane.showConfirmDialog(
                    this,
                    "Do you really want to save the changes?",
                    "Confirm changes",
                    JOptionPane.YES_NO_OPTION
            );
            // Only save data if the user confirms the action.
            if (answer == JOptionPane.YES_OPTION) {
                saveHotelData();
            }
        });
    }

    private void saveHotelData() {
        int noRooms;
        int noBeds;

        // Validates that the room and bed fields contain valid integer values.
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

        // Prevents impossible hotel capacity values.
        if (noRooms <= 0 || noBeds <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Number of rooms and number of beds must be greater than 0.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Checks that all required text fields are filled in before saving.
        if (nameField.getText().trim().isEmpty()
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

        // SQL update statement for saving changes to the selected hotel master data record.
        // The hotel ID is used in the WHERE clause so only one hotel is updated.
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

            // Assigns all edited field values to the SQL placeholders.
            stmt.setString(1, categoryComboBox.getSelectedItem().toString());
            stmt.setString(2, nameField.getText().trim());
            stmt.setString(3, ownerField.getText().trim());
            stmt.setString(4, contactField.getText().trim());
            stmt.setString(5, addressField.getText().trim());
            stmt.setString(6, cityField.getText().trim());
            stmt.setString(7, cityCodeField.getText().trim());
            stmt.setString(8, phoneField.getText().trim());
            stmt.setInt(9, noRooms);
            stmt.setInt(10, noBeds);

            // Uses the original selected hotel ID to identify the correct database record.
            stmt.setInt(11, hotelId);

            // Executes the update and returns the number of affected rows.
            int updatedRows = stmt.executeUpdate();

            // A successful update should change exactly one hotel row.
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
            // Shows an error dialog if saving the hotel data fails.
            JOptionPane.showMessageDialog(
                    this,
                    "Database error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
