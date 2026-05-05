package us4_us5;

import MyApp.Menu; // Needed to return from the hotel overview window back to the main menu.

import javax.swing.*; // Swing components used for the graphical user interface, such as JFrame, JTable, JButton, JPanel and JOptionPane.
import javax.swing.table.DefaultTableModel; // Table model used to define columns and add rows to the JTable.
import java.awt.*; // AWT layout classes, especially BorderLayout, used to arrange GUI components inside the window.
import java.sql.*; // SQL classes used for database connection, SQL statements, result sets and SQL exception handling.

public class HotelOverviewWindow extends JFrame {
    private JTable table; // JTable is the visible table component shown to the user.
    private DefaultTableModel model; // DefaultTableModel stores the table structure and data rows.

    public HotelOverviewWindow() {
        defineFrame(); // Defines the basic frame settings such as title, size and layout.
        initComponents();  // Initializes the table and its columns.
        fillTable();  // Loads hotel data from the database and fills the table.
        addComponents(); // Adds the table to the window.
        addButtonPanel(); // Adds the buttons below the table.
    }

    private void defineFrame() {
        setTitle("Hotel Overview"); // Sets the title displayed in the window header.
        setSize(1100, 500); // Sets the window size large enough to display the hotel overview table.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes the application when this window is closed.
        setLocationRelativeTo(null); // Centers the window on the screen.
        setLayout(new BorderLayout());  // Uses BorderLayout so the table can be placed in the center and buttons at the bottom.
    }

    private void initComponents() {
        model = new DefaultTableModel();  // Creates the table model that stores columns and rows.
        table = new JTable(model); // Creates the visible table based on the table model.
        table.setDefaultEditor(Object.class, null); // Makes the table read-only so users cannot directly edit cells in the overview.
        // Columns required for US4.
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Address");
        model.addColumn("Rooms");
        model.addColumn("Beds");
        // Columns show the latest available transactional data for each hotel.
        model.addColumn("Last Year");
        model.addColumn("Last Month");
        model.addColumn("Used Rooms");
        model.addColumn("Used Beds");

    }

    private void fillTable() {
        // SQL query for US4:
        // It loads all hotels and adds the latest transactional data per hotel.
        // OUTER APPLY ensures that hotels without transactional data are still shown.
        // Three apostrophes to make multi-line Strings
        String sql = """ 
                        SELECT 
                            h.id,
                            h.name,
                            h.address,
                            h.noRooms,
                            h.noBeds,
                            o.year AS lastYear,
                            o.month AS lastMonth,
                            o.usedRooms,
                            o.usedBeds
                        FROM dbo.hotels h
                        OUTER APPLY (
                            SELECT TOP 1
                                occ.year,
                                occ.month,
                                occ.usedRooms,
                                occ.usedBeds
                            FROM dbo.occupancies occ
                            WHERE occ.id = h.id
                            ORDER BY occ.year DESC, occ.month DESC
                            ) o
                            ORDER BY h.id;
                            """;
                // Opens the database connection and executes the SQL query.
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                                "dev",
                                "dev");
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    // Reads every result row and adds it to the table model.
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("address"),
                                rs.getInt("noRooms"),
                                rs.getInt("noBeds"),

                                // getObject is used because these values may be NULL
                                // if no transactional data exists for a hotel yet.
                                rs.getObject("lastYear"),
                                rs.getObject("lastMonth"),
                                rs.getObject("usedRooms"),
                                rs.getObject("usedBeds")


                        });
                    }
                } catch (SQLException e) {
                    // Shows an error dialog if the database connection or SQL query fails.
                    JOptionPane.showMessageDialog(
                            this,
                            "Database error: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
    }

    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table); // Wraps the table inside a scroll pane so the user can scroll through all hotels.
        add(scrollPane, BorderLayout.CENTER);  // Adds the scrollable table to the center of the frame.
    }

    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();  // Panel used to place multiple buttons at the bottom of the window.

        JButton editButton = new JButton("Edit selected Hotel"); // Button for US5: opens the selected hotel in edit mode.
        JButton backButton = new JButton("Back to Menu");   // Button to close the overview window.

        // Button for US6: opens the transactional data entry window.
        JButton transactionButton = new JButton("Add Transaction");

        // Adds all buttons to the button panel.
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);
        //US6
        buttonPanel.add(transactionButton);

        add(buttonPanel, BorderLayout.SOUTH); // Places the button panel at the bottom of the window.
        // Connects button actions to their functions.
        addEditButtonFunction(editButton);
        addBackButtonFunction(backButton);
        //US6
        addTransactionButtonFunction(transactionButton);


    }

    private void addBackButtonFunction(JButton backButton) {
        // Closes the current hotel overview window.
        backButton.addActionListener(e -> {
            dispose();
        });
    }

    private void addEditButtonFunction(JButton editButton) {
        editButton.addActionListener(e -> {
            // Gets the currently selected row in the hotel table.
            // If no row is selected, JTable returns -1.
            int selectedRow = table.getSelectedRow();
            // Prevents opening the edit window without selecting a hotel first.
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a hotel first.",
                        "No hotel selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            // Reads the hotel ID from the first column of the selected row.
            int hotelId = (int) model.getValueAt(selectedRow, 0);
            // Opens US5 edit window and passes the selected hotel ID.
            new HotelEditWindow(hotelId).setVisible(true);
        });
    }



    /**
     * US6 - Wires up the "Add Transaction" button on the hotel list screen.

     * When clicked, the button reads the currently selected row from the hotel
     * table, extracts the hotel ID and name, and opens the TransactionEntryWindow
     * for that hotel.

     * If no row is selected, the user is prompted to select a hotel first.

     * @param transactionButton the button to attach the action listener to
     */
    private void addTransactionButtonFunction(JButton transactionButton) {
        transactionButton.addActionListener(e -> {
            // Get the index of the currently selected row in the hotel table (-1 if none selected)
            int selectedRow = table.getSelectedRow();

            if (selectedRow >= 0) {
                // Extract hotel ID (column 0) and hotel name (column 1) from the selected row
                int hotelID = Integer.parseInt(
                        table.getValueAt(selectedRow, 0).toString()
                );
                String hotelName = table.getValueAt(selectedRow, 1).toString();

                // Open the transactional data entry window for the selected hotel
                new US6.TransactionEntryWindow(hotelID, hotelName).setVisible(true);
            } else {
                // No row selected — prompt the user to select a hotel before proceeding
                JOptionPane.showMessageDialog(this, "Please select a hotel first.");
            }
        });
    }

}

