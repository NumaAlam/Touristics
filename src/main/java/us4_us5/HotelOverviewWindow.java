package us4_us5;

import database.HibernateUtil;
import MyApp.Menu; // Needed to return from the hotel overview window back to the main menu.
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;


import javax.swing.*; // Swing components used for the graphical user interface, such as JFrame, JTable, JButton, JPanel and JOptionPane.
import javax.swing.table.DefaultTableModel; // Table model used to define columns and add rows to the JTable.
import java.awt.*; // AWT layout classes, especially BorderLayout, used to arrange GUI components inside the window.
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class HotelOverviewWindow extends JFrame {
    private JTable table; // JTable is the visible table component shown to the user.
    private DefaultTableModel model; // DefaultTableModel stores the table structure and data rows.
    private JTextField nameFilterField;
    private JTextField addressFilterField;

    private Integer restrictedHotelId;

    public HotelOverviewWindow() {
        this(null);
    }

    public HotelOverviewWindow(Integer restrictedHotelId) {
        this.restrictedHotelId = restrictedHotelId;

        defineFrame(); // Defines the basic frame settings such as title, size and layout.
        initComponents();  // Initializes the table and its columns.
        addFilterPanel();
        fillTable();  // Loads hotel data from the database and fills the table.
        addComponents(); // Adds the table to the window.
        addButtonPanel(); // Adds the buttons below the table.

    }

    private void addFilterPanel() {
        JPanel filterPanel = new JPanel(new GridLayout(2, 3, 5, 5));

        nameFilterField = new JTextField();
        addressFilterField = new JTextField();

        JButton filterButton = new JButton("Apply Filter");
        JButton clearButton = new JButton("Clear Filter");

        filterPanel.add(new JLabel("Filter by name:"));
        filterPanel.add(nameFilterField);
        filterPanel.add(filterButton);

        filterPanel.add(new JLabel("Filter by address:"));
        filterPanel.add(addressFilterField);
        filterPanel.add(clearButton);

        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(filterPanel, BorderLayout.NORTH);

        filterButton.addActionListener(e -> refreshTable());

        clearButton.addActionListener(e -> {
            nameFilterField.setText("");
            addressFilterField.setText("");
            refreshTable();
        });
    }

    private void defineFrame() {
        setTitle("Hotel Overview"); // Sets the title displayed in the window header.
        setSize(1100, 500); // Sets the window size large enough to display the hotel overview table.
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        StringBuilder sql = new StringBuilder("""
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
            WHERE 1 = 1
            """);

        String nameFilter = "";
        String addressFilter = "";

        if (nameFilterField != null) {
            nameFilter = nameFilterField.getText().trim().toLowerCase();
        }

        if (addressFilterField != null) {
            addressFilter = addressFilterField.getText().trim().toLowerCase();
        }

        if (!nameFilter.isEmpty()) {
            sql.append(" AND LOWER(h.name) LIKE :nameFilter ");
        }

        if (!addressFilter.isEmpty()) {
            sql.append(" AND LOWER(h.address) LIKE :addressFilter ");
        }

        if (restrictedHotelId != null) {
            sql.append(" AND h.id = :restrictedHotelId ");
        }

        sql.append(" ORDER BY h.id ");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery<Object[]> query = session.createNativeQuery(sql.toString(), Object[].class);

            if (!nameFilter.isEmpty()) {
                query.setParameter("nameFilter", "%" + nameFilter + "%");
            }

            if (!addressFilter.isEmpty()) {
                query.setParameter("addressFilter", "%" + addressFilter + "%");
            }

            if (restrictedHotelId != null) {
                query.setParameter("restrictedHotelId", restrictedHotelId);
            }

            List<Object[]> rows = query.getResultList();

            for (Object[] row : rows) {
                model.addRow(new Object[]{
                        row[0], // ID
                        row[1], // Name
                        row[2], // Address
                        row[3], // Rooms
                        row[4], // Beds
                        row[5], // Last Year
                        row[6], // Last Month
                        row[7], // Used Rooms
                        row[8]  // Used Beds
                });
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while loading hotel overview: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Refresh Button
    private void refreshTable() {
        model.setRowCount(0);
        fillTable();
    }

    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table); // Wraps the table inside a scroll pane so the user can scroll through all hotels.
        add(scrollPane, BorderLayout.CENTER);  // Adds the scrollable table to the center of the frame.
    }

    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton editButton = new JButton("Edit selected Hotel");
        JButton transactionButton = new JButton("Add Transaction");
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back to Menu");

        buttonPanel.add(editButton);
        buttonPanel.add(transactionButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addEditButtonFunction(editButton);
        addTransactionButtonFunction(transactionButton);
        addRefreshButtonFunction(refreshButton);
        addBackButtonFunction(backButton);
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

            HotelEditWindow editWindow = new HotelEditWindow(hotelId);

                    editWindow.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            refreshTable();
                        }
                    });

                    editWindow.setVisible(true);
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

                US6.TransactionEntryWindow transactionWindow =
                        new US6.TransactionEntryWindow(hotelID, hotelName);

                transactionWindow.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        refreshTable();
                    }
                });

                transactionWindow.setVisible(true);
            } else {
                // No row selected — prompt the user to select a hotel before proceeding
                JOptionPane.showMessageDialog(this, "Please select a hotel first.");
            }
        });
    }

    // refresh button
    private void addRefreshButtonFunction(JButton refreshButton) {
        refreshButton.addActionListener(e -> refreshTable());
    }

}

