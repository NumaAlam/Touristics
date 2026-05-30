package us4_us5;

import database.HibernateUtil;
import hotels.Hotel;
import org.hibernate.Session;
import org.hibernate.Transaction;
import users.User;

import javax.swing.*; // Swing components used for the graphical user interface, such as JFrame, JTextField, JButton, JPanel and JOptionPane.
import java.awt.*; // AWT layout classes, especially BorderLayout and GridLayout, used to arrange the edit form.



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

        // US25: Access control for hotel representatives.
        // Representatives may only open the master data of their assigned hotel.
        if (!isHotelAccessAllowed()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Access denied. You can only open your assigned hotel.",
                    "Access denied",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        defineFrame(); // Defines the basic frame settings.
        initFields(); // Creates all text fields.
        addComponents(); // Adds labels and text fields to the form.
        loadHotelData(); // Loads the selected hotel data from the database.
        addButtonPanel(); // Adds Save and Close buttons.
    }

    // US25: Checks whether the currently logged-in user is allowed to access this hotel.
    // Senior users may access all hotels.
    // Hotel representatives may only access the hotel stored in Session.currentHotelId.
    private boolean isHotelAccessAllowed() {
        if ("Hotel Representative".equals(MyApp.Session.currentRole)) {
            return MyApp.Session.currentHotelId != null
                    && MyApp.Session.currentHotelId == hotelId;
        }

        return true;
    }

    private void defineFrame() {
        setTitle("Lower Austria Tourist Portal — Edit Hotel"); // Sets title of the edit window.
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
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            Hotel hotel = session.get(Hotel.class, hotelId);

            if (hotel != null) {
                idField.setText(String.valueOf(hotel.getId()));
                categoryComboBox.setSelectedItem(hotel.getCategory());
                nameField.setText(hotel.getName());
                ownerField.setText(hotel.getOwner());
                contactField.setText(hotel.getContact());
                addressField.setText(hotel.getAddress());
                cityField.setText(hotel.getCity());
                cityCodeField.setText(hotel.getCityCode());
                phoneField.setText(hotel.getPhone());
                noRoomsField.setText(String.valueOf(hotel.getNoRooms()));
                noBedsField.setText(String.valueOf(hotel.getNoBeds()));
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No hotel found for ID: " + hotelId,
                        "Hotel not found",
                        JOptionPane.WARNING_MESSAGE
                );
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while loading hotel data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void addButtonPanel() {

        JPanel buttonPanel = new JPanel(); // Panel used to place Save and Close buttons below the form.

        JButton saveButton = new JButton("Save changes"); // Button for saving changes to the database.
        JButton closeButton = new JButton("Close"); // Button for closing the edit window without saving.
        JButton deleteButton = new JButton("Delete Hotel");
        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);


        if ("Senior".equals(MyApp.Session.currentRole)
                || "Senior_Admin".equals(MyApp.Session.currentRole)
                || Boolean.TRUE.equals(MyApp.Session.canDelete)) {
            buttonPanel.add(deleteButton);
            addDeleteButtonFunction(deleteButton);
        }
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
    }private void addDeleteButtonFunction(JButton deleteButton) {
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this hotel?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Transaction tx = null;
            try (org.hibernate.Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                User user = session.createQuery("from User where hotelID = :id", User.class)
                        .setParameter("id", hotelId)
                        .uniqueResult();
                if (user != null) {
                    session.remove(user);
                }
                Hotel hotel = session.get(Hotel.class, hotelId);
                session.remove(hotel);
                tx.commit();
                JOptionPane.showMessageDialog(this, "Hotel deleted successfully.");
                dispose();
            } catch (Exception ex) {
                if (tx != null) tx.rollback();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }

    private void saveHotelData() {
        // US25: Prevents hotel representatives from saving changes to hotels
        // that are not assigned to their user account.
        if (!isHotelAccessAllowed()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Access denied. You can only edit your assigned hotel.",
                    "Access denied",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

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


        // Saves the edited hotel master data via Hibernate.
        // Hibernate updates the database record when the transaction is committed.
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Hotel hotel = session.get(Hotel.class, hotelId);

            if (hotel == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "No hotel found for ID: " + hotelId,
                        "Hotel not found",
                        JOptionPane.WARNING_MESSAGE
                );
                transaction.rollback();
                return;
            }

            hotel.setCategory(categoryComboBox.getSelectedItem().toString());
            hotel.setName(nameField.getText().trim());
            hotel.setOwner(ownerField.getText().trim());
            hotel.setContact(contactField.getText().trim());
            hotel.setAddress(addressField.getText().trim());
            hotel.setCity(cityField.getText().trim());
            hotel.setCityCode(cityCodeField.getText().trim());
            hotel.setPhone(phoneField.getText().trim());
            hotel.setNoRooms(noRooms);
            hotel.setNoBeds(noBeds);

            transaction.commit();

            JOptionPane.showMessageDialog(
                    this,
                    "Hotel data saved successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while saving hotel data: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
