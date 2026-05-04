package US6;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * US6 - Transactional Data Entry Window

 * This window allows a senior user of NOE-TO to enter new transactional data
 * (room and bed occupancy per month) for a specific hotel.

 * The window is pre-loaded with the hotel's ID and name (read-only).
 * Room and bed capacity are automatically fetched from the database.
 * The user only needs to enter: year, month, room occupancy, and bed occupancy.

 * On save, the data is validated and inserted into the dbo.occupancies table.
 * Duplicate entries for the same hotel, year and month are rejected.
 */
public class TransactionEntryWindow extends JFrame {

    // Hotel ID and name passed in from the calling screen (e.g. hotel list)
    private final int hotelID;
    private final String hotelName;

    // Input fields displayed in the form
    private JTextField idField;        // Hotel ID — read-only, set from constructor
    private JTextField nameField;      // Hotel name — read-only, set from constructor
    private JTextField yearField;      // Year of the occupancy entry — user input
    private JTextField monthField;     // Month of the occupancy entry (1–12) — user input
    private JTextField roomsField;     // Total number of rooms — auto-loaded from DB, read-only
    private JTextField bedsField;      // Total number of beds — auto-loaded from DB, read-only
    private JTextField roomOccField;   // Number of rooms occupied — user input
    private JTextField bedOccField;    // Number of beds occupied — user input

    // Database connection settings for the NOE-TO development database
    private final String url = "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;";
    private final String user = "dev";
    private final String password = "dev";

    /**
     * Constructor — opens the transactional entry window for the given hotel.
     * Initialises the frame, sets up all fields, and loads hotel capacity from the DB.

     * @param hotelID   the ID of the hotel for which data is being entered
     * @param hotelName the name of the hotel, displayed read-only in the form
     */
    public TransactionEntryWindow(int hotelID, String hotelName) {
        this.hotelID = hotelID;
        this.hotelName = hotelName;
        defineFrame();       // Set window properties (title, size, layout)
        initFields();        // Create and configure all input fields
        addComponents();     // Build the form layout and add the save button
        loadHotelCapacity(); // Fetch rooms and beds from DB and fill read-only fields
    }

    /**
     * Configures the basic properties of the JFrame window:
     * title, size, close behaviour, position on screen, and layout manager.
     */
    private void defineFrame() {
        setTitle("Transactional Data Entry");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Closes only this window, not the whole app
        setLocationRelativeTo(null); // Centers the window on screen
        setLayout(new BorderLayout());
    }

    /**
     * Creates all input fields and sets their initial values and editability.
     * Hotel ID, name, rooms and beds are read-only — they are set programmatically.
     * Year, month, room occupancy and bed occupancy are editable by the user.
     */
    private void initFields() {
        idField      = new JTextField(String.valueOf(hotelID));
        nameField    = new JTextField(hotelName);
        yearField    = new JTextField();
        monthField   = new JTextField();
        roomsField   = new JTextField();
        bedsField    = new JTextField();
        roomOccField = new JTextField();
        bedOccField  = new JTextField();

        // These fields are display-only — the user cannot change them
        idField.setEditable(false);
        nameField.setEditable(false);
        roomsField.setEditable(false); // Filled automatically by loadHotelCapacity()
        bedsField.setEditable(false);  // Filled automatically by loadHotelCapacity()
    }

    /**
     * Builds the form layout using a GridLayout (8 rows, 2 columns).
     * Each row contains a label and the corresponding input field.
     * A Save button is placed at the bottom of the window.
     */
    private void addComponents() {
        JPanel transactionPanel = new JPanel(new GridLayout(8, 2, 10, 10));

        // Add label-field pairs in display order
        transactionPanel.add(new JLabel("Hotel ID:"));
        transactionPanel.add(idField);
        transactionPanel.add(new JLabel("Hotel Name:"));
        transactionPanel.add(nameField);
        transactionPanel.add(new JLabel("Year:"));
        transactionPanel.add(yearField);
        transactionPanel.add(new JLabel("Month:"));
        transactionPanel.add(monthField);
        transactionPanel.add(new JLabel("Number of Rooms:"));
        transactionPanel.add(roomsField);
        transactionPanel.add(new JLabel("Number of Beds:"));
        transactionPanel.add(bedsField);
        transactionPanel.add(new JLabel("Room Occupancy:"));
        transactionPanel.add(roomOccField);
        transactionPanel.add(new JLabel("Bed Occupancy:"));
        transactionPanel.add(bedOccField);

        transactionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Save button triggers the full validation and DB insert process
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveTransactionData());

        add(transactionPanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }

    /**
     * Fetches the hotel's total room and bed capacity from the database
     * and populates the read-only roomsField and bedsField.

     * Queries: dbo.hotels WHERE id = hotelID
     * If the hotel is not found, a warning dialog is shown.
     * If a DB error occurs, an error dialog is shown.
     */
    private void loadHotelCapacity() {
        String sql = """
                SELECT noRooms, noBeds
                FROM dbo.hotels
                WHERE id = ?;
                """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hotelID);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Populate the read-only capacity fields with values from the DB
                    roomsField.setText(String.valueOf(rs.getInt("noRooms")));
                    bedsField.setText(String.valueOf(rs.getInt("noBeds")));
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "No hotel found for ID: " + hotelID,
                            "Hotel not found",
                            JOptionPane.WARNING_MESSAGE
                    );
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while loading hotel capacity: " + e.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Validates all user input and saves the transactional data to the database.

     * Validation steps (in order):
     *  1. All fields must be filled — no blank values allowed
     *  2. Year, month, room occupancy and bed occupancy must be valid integers
     *  3. Year must be a positive number
     *  4. Month must be between 1 and 12
     *  5. Rooms and beds must be greater than 0
     *  6. Occupancy values cannot be negative
     *  7. Room occupancy cannot exceed total rooms; bed occupancy cannot exceed total beds
     *  8. No duplicate entry for the same hotel, year and month (checked via DB)

     * If all validations pass, a confirmation dialog is shown before inserting.
     * On success, the window is closed. On DB error, an error dialog is shown.
     *
     * Inserts into: dbo.occupancies (id, rooms, usedRooms, beds, usedBeds, year, month)
     */
    private void saveTransactionData() {

        // Read and trim all field values
        String yearText    = yearField.getText().trim();
        String monthText   = monthField.getText().trim();
        String roomsText   = roomsField.getText().trim();
        String bedsText    = bedsField.getText().trim();
        String roomOccText = roomOccField.getText().trim();
        String bedOccText  = bedOccField.getText().trim();

        // Step 1: Check that no required field is empty
        if (yearText.isBlank() || monthText.isBlank() || roomsText.isBlank()
                || bedsText.isBlank() || roomOccText.isBlank() || bedOccText.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Missing input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Step 2: Parse all fields to integers — show error if any are non-numeric
        int year, month, rooms, beds, usedRooms, usedBeds;
        try {
            year      = Integer.parseInt(yearText);
            month     = Integer.parseInt(monthText);
            rooms     = Integer.parseInt(roomsText);
            beds      = Integer.parseInt(bedsText);
            usedRooms = Integer.parseInt(roomOccText);
            usedBeds  = Integer.parseInt(bedOccText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Year, month, room occupancy and bed occupancy must be valid numbers.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Step 3: Year must be positive
        if (year <= 0) {
            JOptionPane.showMessageDialog(
                    this, "Year must be a positive number.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 4: Month must be a valid calendar month (1–12)
        if (month < 1 || month > 12) {
            JOptionPane.showMessageDialog(
                    this, "Month must be between 1 and 12.", "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 5: Room and bed capacity must be positive (should always be, since loaded from DB)
        if (rooms <= 0 || beds <= 0) {
            JOptionPane.showMessageDialog(
                    this, "Number of rooms and number of beds must be greater than 0.",
                    "Invalid hotel capacity", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 6: Occupancy values cannot be negative
        if (usedRooms < 0 || usedBeds < 0) {
            JOptionPane.showMessageDialog(
                    this, "Room occupancy and bed occupancy cannot be negative.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 7a: Room occupancy cannot exceed total room capacity
        if (usedRooms > rooms) {
            JOptionPane.showMessageDialog(
                    this, "Room occupancy cannot be higher than number of rooms.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 7b: Bed occupancy cannot exceed total bed capacity
        if (usedBeds > beds) {
            JOptionPane.showMessageDialog(
                    this, "Bed occupancy cannot be higher than number of beds.",
                    "Invalid input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 8: Check for duplicate — same hotel, year and month must not already exist in DB
        if (transactionAlreadyExists(year, month)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Transactional data for this hotel, year and month already exists.",
                    "Duplicate entry",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Ask the user to confirm before writing to the database
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you really want to save this transactional data?",
                "Confirm save",
                JOptionPane.YES_NO_OPTION
        );
        if (answer != JOptionPane.YES_OPTION) {
            return; // User cancelled — do nothing
        }

        // Insert the validated occupancy record into dbo.occupancies
        String sql = """
                INSERT INTO dbo.occupancies
                    (id, rooms, usedRooms, beds, usedBeds, year, month)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?);
                """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hotelID);
            ps.setInt(2, rooms);
            ps.setInt(3, usedRooms);
            ps.setInt(4, beds);
            ps.setInt(5, usedBeds);
            ps.setInt(6, year);
            ps.setInt(7, month);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Transactional data successfully saved.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose(); // Close the window after successful save
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while saving transactional data: " + e.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    /**
     * Checks whether a transactional record already exists in dbo.occupancies
     * for this hotel, year and month combination.

     * Used to prevent duplicate entries before attempting an INSERT.
     * Returns true (block the save) also if a DB error occurs, as a safe default.

     * @param year  the year to check
     * @param month the month to check
     * @return true if a record already exists or a DB error occurred; false if safe to insert
     */
    private boolean transactionAlreadyExists(int year, int month) {
        String sql = """
                SELECT COUNT(*) AS counter
                FROM dbo.occupancies
                WHERE id = ? AND year = ? AND month = ?;
                """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, hotelID);
            ps.setInt(2, year);
            ps.setInt(3, month);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Returns true if at least one matching record exists
                    return rs.getInt("counter") > 0;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while checking existing transactional data: " + e.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        // Default to true (treat as duplicate) if the check could not be completed
        return true;
    }
}