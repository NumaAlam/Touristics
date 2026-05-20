package US6;

import database.HibernateUtil;
import hotels.Hotel;
import occupancies.Occupancy;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.swing.*;
import java.awt.*;

/**
 * US6 - Transactional Data Entry Window
 *
 * This window allows a senior user or hotel representative to enter new transactional data
 * for a selected hotel.
 *
 * The hotel ID and hotel name are displayed automatically.
 * The hotel ID, hotel name, number of rooms and number of beds are read-only.
 * The user enters year, month, room occupancy and bed occupancy.
 *
 * The data is saved through Hibernate into the occupancies table.
 */
public class TransactionEntryWindow extends JFrame {

    // Hotel ID and name passed in from the calling screen.
    private final int hotelID;
    private final String hotelName;

    // Form fields.
    private JTextField idField;
    private JTextField nameField;
    private JTextField yearField;
    private JComboBox<String> monthField;
    private JTextField roomsField;
    private JTextField bedsField;
    private JTextField roomOccField;
    private JTextField bedOccField;

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

        monthField = new JComboBox<>(new String[]{
                "January", "February", "March", "April",
                "May", "June", "July", "August",
                "September", "October", "November", "December"
        });

        roomsField = new JTextField();
        bedsField = new JTextField();
        roomOccField = new JTextField();
        bedOccField = new JTextField();

        // These fields are not transactional user input.
        // They are either passed in or loaded from hotel master data.
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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Hotel hotel = session.get(Hotel.class, hotelID);

            if (hotel != null) {
                roomsField.setText(String.valueOf(hotel.getNoRooms()));
                bedsField.setText(String.valueOf(hotel.getNoBeds()));
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No hotel found for ID: " + hotelID,
                        "Hotel not found",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while loading hotel capacity: " + e.getMessage(),
                    "Hibernate error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void saveTransactionData() {
        String yearText = yearField.getText().trim();
        String roomsText = roomsField.getText().trim();
        String bedsText = bedsField.getText().trim();
        String roomOccText = roomOccField.getText().trim();
        String bedOccText = bedOccField.getText().trim();

        if (yearText.isBlank()
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
            month = monthField.getSelectedIndex() + 1;
            rooms = Integer.parseInt(roomsText);
            beds = Integer.parseInt(bedsText);
            usedRooms = Integer.parseInt(roomOccText);
            usedBeds = Integer.parseInt(bedOccText);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Year, room occupancy and bed occupancy must be valid numbers.",
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

        saveOccupancyWithHibernate(year, month, rooms, beds, usedRooms, usedBeds);
    }

    private boolean transactionAlreadyExists(int year, int month) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Long counter = session.createQuery("""
                            SELECT COUNT(o)
                            FROM Occupancy o
                            WHERE o.hotel.id = :hotelID
                              AND o.year = :year
                              AND o.month = :month
                            """, Long.class)
                    .setParameter("hotelID", hotelID)
                    .setParameter("year", year)
                    .setParameter("month", month)
                    .uniqueResult();

            return counter != null && counter > 0;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while checking existing transactional data: " + e.getMessage(),
                    "Hibernate error",
                    JOptionPane.ERROR_MESSAGE
            );

            // Safe default: block saving if the duplicate check fails.
            return true;
        }
    }

    private void saveOccupancyWithHibernate(
            int year,
            int month,
            int rooms,
            int beds,
            int usedRooms,
            int usedBeds
    ) {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Hotel hotel = session.get(Hotel.class, hotelID);

            if (hotel == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No hotel found for ID: " + hotelID,
                        "Hotel not found",
                        JOptionPane.WARNING_MESSAGE
                );

                transaction.rollback();
                return;
            }

            Occupancy occupancy = new Occupancy();
            occupancy.setHotel(hotel);
            occupancy.setYear(year);
            occupancy.setMonth(month);
            occupancy.setRooms(rooms);
            occupancy.setBeds(beds);
            occupancy.setUsedRooms(usedRooms);
            occupancy.setUsedBeds(usedBeds);

            session.persist(occupancy);

            transaction.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Transactional data successfully saved.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            dispose();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while saving transactional data: " + e.getMessage(),
                    "Hibernate error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}