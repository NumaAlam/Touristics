package US6;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class TransactionEntryWindow extends JFrame {
    private final int hotelID;
    private final String hotelName;
    private JTextField idField;
    private JTextField nameField;
    private JTextField yearField;
    private JTextField monthField;
    private JTextField roomsField;
    private JTextField bedsField;
    private JTextField roomOccField;
    private JTextField bedOccField;
    private final String url = "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;";
    private final String user = "dev";
    private final String password = "dev";
    public TransactionEntryWindow(int hotelID, String hotelName) {
        this.hotelID = hotelID;
        this.hotelName = hotelName;
        defineFrame();
        initFields();
        addComponents();
        loadHotelCapacity();
    }
    private void defineFrame() {
        setTitle("Transactional Data Entry");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    private void initFields() {
        idField = new JTextField(String.valueOf(hotelID));
        nameField = new JTextField(hotelName);
        yearField = new JTextField();
        monthField = new JTextField();
        roomsField = new JTextField();
        bedsField = new JTextField();
        roomOccField = new JTextField();
        bedOccField = new JTextField();
        idField.setEditable(false);
        nameField.setEditable(false);
        roomsField.setEditable(false);
        bedsField.setEditable(false);
    }
    private void addComponents() {
        JPanel transactionPanel = new JPanel(new GridLayout(8, 2, 10, 10));
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
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveTransactionData());
        add(transactionPanel, BorderLayout.CENTER);
        add(saveButton, BorderLayout.SOUTH);
    }
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
    private void saveTransactionData() {
        String yearText = yearField.getText().trim();
        String monthText = monthField.getText().trim();
        String roomsText = roomsField.getText().trim();
        String bedsText = bedsField.getText().trim();
        String roomOccText = roomOccField.getText().trim();
        String bedOccText = bedOccField.getText().trim();
        if (yearText.isBlank()
                || monthText.isBlank()
                || roomsText.isBlank()
                || bedsText.isBlank()
                || roomOccText.isBlank()
                || bedOccText.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Missing input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int year;
        int month;
        int rooms;
        int beds;
        int usedRooms;
        int usedBeds;
        try {
            year = Integer.parseInt(yearText);
            month = Integer.parseInt(monthText);
            rooms = Integer.parseInt(roomsText);
            beds = Integer.parseInt(bedsText);
            usedRooms = Integer.parseInt(roomOccText);
            usedBeds = Integer.parseInt(bedOccText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Year, month, room occupancy and bed occupancy must be valid numbers.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (year <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Year must be a positive number.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (month < 1 || month > 12) {
            JOptionPane.showMessageDialog(
                    this,
                    "Month must be between 1 and 12.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (rooms <= 0 || beds <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Number of rooms and number of beds must be greater than 0.",
                    "Invalid hotel capacity",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (usedRooms < 0 || usedBeds < 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Room occupancy and bed occupancy cannot be negative.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (usedRooms > rooms) {
            JOptionPane.showMessageDialog(
                    this,
                    "Room occupancy cannot be higher than number of rooms.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (usedBeds > beds) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bed occupancy cannot be higher than number of beds.",
                    "Invalid input",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        if (transactionAlreadyExists(year, month)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Transactional data for this hotel, year and month already exists.",
                    "Duplicate entry",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        int answer = JOptionPane.showConfirmDialog(
                this,
                "Do you really want to save this transactional data?",
                "Confirm save",
                JOptionPane.YES_NO_OPTION
        );
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
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
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Database error while saving transactional data: " + e.getMessage(),
                    "Database error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
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
        return true;
    }
}
