package US10;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.Year;

/**
 * US10 – Shows all transactional data for a selected hotel with optional
 * date range filtering, so senior users can verify data correctness.
 */
public class TransactionListWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> hotelComboBox;
    private JComboBox<String> fromYearComboBox;
    private JComboBox<String> fromMonthComboBox;
    private JComboBox<String> toYearComboBox;
    private JComboBox<String> toMonthComboBox;

    // Keeps hotel DB IDs in sync with the dropdown index so we can query by ID
    private final java.util.ArrayList<Integer> hotelIDs = new java.util.ArrayList<>();

    private static final String DB_URL = "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;";
    private static final String DB_USER = "dev";
    private static final String DB_PASS = "dev";

    public TransactionListWindow() {
        defineFrame();
        initComponents();
        addComponents();
        addActions();
        backButton();
    }

    private void defineFrame() {
        setTitle("Transactional Data per Hotel");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        // --- Table ---
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);

        model.addColumn("Year");
        model.addColumn("Month");
        model.addColumn("Rooms");
        model.addColumn("Used Rooms");
        model.addColumn("Beds");
        model.addColumn("Used Beds");

        // Load all hotels from DB into dropdown; IDs are stored in parallel so we can map selection to DB ID
        hotelComboBox = new JComboBox<>();
        hotelIDs.clear();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String sql = "SELECT id, name FROM hotels ORDER BY name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                hotelComboBox.addItem(rs.getString("name"));
                hotelIDs.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // --- FROM Year/Month ---
        fromYearComboBox = new JComboBox<>();
        fromYearComboBox.addItem("All");
        for (int i = Year.now().getValue(); i >= 1900; i--) {
            fromYearComboBox.addItem(String.valueOf(i));
        }

        fromMonthComboBox = new JComboBox<>();
        fromMonthComboBox.addItem("All");
        fromMonthComboBox.addItem("January");
        fromMonthComboBox.addItem("February");
        fromMonthComboBox.addItem("March");
        fromMonthComboBox.addItem("April");
        fromMonthComboBox.addItem("May");
        fromMonthComboBox.addItem("June");
        fromMonthComboBox.addItem("July");
        fromMonthComboBox.addItem("August");
        fromMonthComboBox.addItem("September");
        fromMonthComboBox.addItem("October");
        fromMonthComboBox.addItem("November");
        fromMonthComboBox.addItem("December");

        // --- TO Year/Month ---
        toYearComboBox = new JComboBox<>();
        toYearComboBox.addItem("All");
        for (int i = Year.now().getValue(); i >= 1900; i--) {
            toYearComboBox.addItem(String.valueOf(i));
        }

        toMonthComboBox = new JComboBox<>();
        toMonthComboBox.addItem("All");
        toMonthComboBox.addItem("January");
        toMonthComboBox.addItem("February");
        toMonthComboBox.addItem("March");
        toMonthComboBox.addItem("April");
        toMonthComboBox.addItem("May");
        toMonthComboBox.addItem("June");
        toMonthComboBox.addItem("July");
        toMonthComboBox.addItem("August");
        toMonthComboBox.addItem("September");
        toMonthComboBox.addItem("October");
        toMonthComboBox.addItem("November");
        toMonthComboBox.addItem("December");
    }

    private void addComponents() {
        // --- Filter panel ---
        JPanel filterPanel = new JPanel();

        filterPanel.add(new JLabel("Hotel:"));
        filterPanel.add(hotelComboBox);

        filterPanel.add(Box.createHorizontalStrut(20));

        filterPanel.add(new JLabel("From Year:"));
        filterPanel.add(fromYearComboBox);
        filterPanel.add(new JLabel("From Month:"));
        filterPanel.add(fromMonthComboBox);

        filterPanel.add(Box.createHorizontalStrut(10));

        filterPanel.add(new JLabel("To Year:"));
        filterPanel.add(toYearComboBox);
        filterPanel.add(new JLabel("To Month:"));
        filterPanel.add(toMonthComboBox);

        add(filterPanel, BorderLayout.NORTH);

        // --- Table ---
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addActions() {
        // Every dropdown triggers a refresh so the table always reflects the current filter selection
        hotelComboBox.addActionListener(e -> refreshTable());
        fromYearComboBox.addActionListener(e -> refreshTable());
        fromMonthComboBox.addActionListener(e -> refreshTable());
        toYearComboBox.addActionListener(e -> refreshTable());
        toMonthComboBox.addActionListener(e -> refreshTable());

        // Load initial data if hotels exist
        if (hotelComboBox.getItemCount() > 0) {
            refreshTable();
        }
    }

    private void refreshTable() {
        if (hotelComboBox.getSelectedIndex() < 0 || hotelIDs.isEmpty()) {
            return;
        }

        int hotelId = hotelIDs.get(hotelComboBox.getSelectedIndex());

        int fromYear = 0;
        if (!"All".equals(fromYearComboBox.getSelectedItem())) {
            fromYear = Integer.parseInt((String) fromYearComboBox.getSelectedItem());
        }
        int fromMonth = fromMonthComboBox.getSelectedIndex(); // 0 = All, 1 = Jan, ...

        int toYear = 0;
        if (!toYearComboBox.getSelectedItem().equals("All")) {
            toYear = Integer.parseInt((String) toYearComboBox.getSelectedItem());
        }
        int toMonth = toMonthComboBox.getSelectedIndex(); // 0 = All, 1 = Jan, ...


        // Validate that FROM date is not after TO date to prevent empty or misleading results
        if (fromYear != 0 && toYear != 0) {
            if (fromYear > toYear || (fromYear == toYear && fromMonth > toMonth && toMonth != 0)) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date range: From must be before To.",
                        "Invalid Range",
                        JOptionPane.WARNING_MESSAGE);
                model.setRowCount(0);
                return;
            }
        }
        fillTable(hotelId, fromYear, fromMonth, toYear, toMonth);
    }



    // Builds SQL dynamically based on active filters; uses PreparedStatement to prevent SQL injection
    private void fillTable(int hotelId, int fromYear, int fromMonth, int toYear, int toMonth) {
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            StringBuilder sql = new StringBuilder(
                    "SELECT o.year, o.month, o.rooms, o.usedRooms, o.beds, o.usedBeds " +
                            "FROM occupancies o " +
                            "WHERE o.id = ?");

            // FROM filter: (year > X OR (year = X AND month >= Y)) handles cross-year ranges correctly
            if (fromYear != 0 && fromMonth != 0) {
                sql.append(" AND (o.year > ? OR (o.year = ? AND o.month >= ?))");
            } else if (fromYear != 0) {
                sql.append(" AND o.year >= ?");
            } else if (fromMonth != 0) {
                sql.append(" AND o.month >= ?");
            }

            // TO filter
            if (toYear != 0 && toMonth != 0) {
                sql.append(" AND (o.year < ? OR (o.year = ? AND o.month <= ?))");
            } else if (toYear != 0) {
                sql.append(" AND o.year <= ?");
            } else if (toMonth != 0) {
                sql.append(" AND o.month <= ?");
            }

            sql.append(" ORDER BY o.year DESC, o.month DESC");

            PreparedStatement ps = conn.prepareStatement(sql.toString());

            int paramIndex = 1;
            ps.setInt(paramIndex++, hotelId);

            // FROM params
            if (fromYear != 0 && fromMonth != 0) {
                ps.setInt(paramIndex++, fromYear);
                ps.setInt(paramIndex++, fromYear);
                ps.setInt(paramIndex++, fromMonth);
            } else if (fromYear != 0) {
                ps.setInt(paramIndex++, fromYear);
            } else if (fromMonth != 0) {
                ps.setInt(paramIndex++, fromMonth);
            }

            // TO params
            if (toYear != 0 && toMonth != 0) {
                ps.setInt(paramIndex++, toYear);
                ps.setInt(paramIndex++, toYear);
                ps.setInt(paramIndex++, toMonth);
            } else if (toYear != 0) {
                ps.setInt(paramIndex++, toYear);
            } else if (toMonth != 0) {
                ps.setInt(paramIndex++, toMonth);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("year"),
                        getMonthName(rs.getInt("month")),
                        rs.getInt("rooms"),
                        rs.getInt("usedRooms"),
                        rs.getInt("beds"),
                        rs.getInt("usedBeds")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Converts month number (1-12) to name for readable table display
    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        if (month >= 1 && month <= 12) {
            return months[month];
        }
        return String.valueOf(month);
    }

    private void backButton() {
        JButton backButton = new JButton("Close Table");
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.setBackground(new Color(175, 175, 255));
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> dispose());
    }
}