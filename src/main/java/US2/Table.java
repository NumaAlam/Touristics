package US2;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class Table extends JFrame {

    // static list to store hotel IDs from the database.
    // Index 0 is reserved for "All"
    static ArrayList<Integer> hotelIDs = new ArrayList<>();
    // initialize tables and comboboxes
    JTable table;

    static DefaultTableModel model;

    JPanel panel;

    JComboBox<String> categoryComboBox;

    JComboBox<String> yearComboBox;

    JComboBox<String> monthComboBox;

    JComboBox<String> hotelIDComboBox;

    // initialize constructor
    public Table() {
        defineFrame();
        initComponents();
        addActions();
        fillTable_SQL(-1, null, 0, 0);  // import data from SQL DBS
        addComponents();
        backButton();
    }

    // filter actions
    private void addActions() {
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            if (selectedCategory.equals("All")) {
                selectedCategory = null; // no filter
            }
            String selectedYear;
            if (yearComboBox.getSelectedItem().equals("All")) {
                selectedYear = "0"; // no filter
            } else {
                selectedYear = (String) yearComboBox.getSelectedItem();
            }
            fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
        });

        yearComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            if (selectedCategory.equals("All")) {
                selectedCategory = null;
            }
            String selectedYear = (String) yearComboBox.getSelectedItem();
            if (selectedYear.equals("All")) {
                selectedYear = "0";
            }
            fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
        });

        monthComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            if (selectedCategory.equals("All")) {
                selectedCategory = null;
            }
            String selectedYear;
            if (yearComboBox.getSelectedItem().equals("All")) {
                selectedYear = "0";
            } else {
                selectedYear = (String) yearComboBox.getSelectedItem();
            }
            fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
        });

        hotelIDComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            if (selectedCategory.equals("All")) {
                selectedCategory = null;
            }
            String selectedYear;
            if (yearComboBox.getSelectedItem().equals("All")) {
                selectedYear = "0";
            } else {
                selectedYear = (String) yearComboBox.getSelectedItem();
            }
            fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
        });
    }

    // Define the initialized components of the table. fills the ComboBoxes with the available options.
    private void initComponents() {
        table = new JTable();
        model = new DefaultTableModel();
        table.setDefaultEditor(Object.class, null); // Zellenbearbeitung deaktivieren
        table.setModel(model);
        panel = new JPanel();

        // Category filter with stars descending
        categoryComboBox = new JComboBox<>();
        categoryComboBox.addItem("All");
        categoryComboBox.addItem("*****");
        categoryComboBox.addItem("****");
        categoryComboBox.addItem("***");
        categoryComboBox.addItem("**");
        categoryComboBox.addItem("*");

        // Years 1900 to current year
        yearComboBox = new JComboBox<>();
        yearComboBox.addItem("All");
        for (int i = Year.now().getValue(); i >= 1900; i--) {
            yearComboBox.addItem(String.valueOf(i));
        }

        // fill the month combobox with all months
        monthComboBox = new JComboBox<>();
        monthComboBox.addItem("All");
        monthComboBox.addItem("January");
        monthComboBox.addItem("February");
        monthComboBox.addItem("March");
        monthComboBox.addItem("April");
        monthComboBox.addItem("May");
        monthComboBox.addItem("June");
        monthComboBox.addItem("July");
        monthComboBox.addItem("August");
        monthComboBox.addItem("September");
        monthComboBox.addItem("October");
        monthComboBox.addItem("November");
        monthComboBox.addItem("December");

        // fill the hotel combobox and hotel list with all hotels from the database
        hotelIDComboBox = new JComboBox<>();
        hotelIDComboBox.addItem("All");
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev")) {
            String sql = "SELECT id, name FROM hotels";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            hotelIDs.add(-1); // index 0 is reserved for "All"
            while (rs.next()) {
                hotelIDComboBox.addItem(rs.getString("name"));
                hotelIDs.add(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }

        // adds columns to the table
        model.addColumn("name");
        model.addColumn("category");
        model.addColumn("year");
        model.addColumn("month");
        model.addColumn("rooms");
        model.addColumn("usedRooms");
        model.addColumn("beds");
        model.addColumn("usedBeds");
    }

    // adds the table and the filter panel to the frame
    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.NORTH); // Filter-Panel oben
        panel.add(new JLabel("Category:"));
        panel.add(categoryComboBox);
        panel.add(new JLabel("Year:"));
        panel.add(yearComboBox);
        panel.add(new JLabel("Month:"));
        panel.add(monthComboBox);
        panel.add(new JLabel("Hotel ID:"));
        panel.add(hotelIDComboBox);
    }

    // back button to close the table
    private void backButton() {
        JButton backButton = new JButton("Close Table");
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.setBackground(new Color(175, 175, 255));
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> {
            dispose(); // Fenster schließen und Ressourcen freigeben
        });
    }

    // Dynamically fills the table with data from the database. depending on the selected filters
    private static void fillTable_SQL(int hotelId, String category, int year, int month) {
        model.setRowCount(0); // Vorherige Tabellenzeilen löschen

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev")) {

            // Base SQl Query
            String sql = "SELECT h.name, h.category, o.year, o.month, o.rooms, o.usedRooms, o.beds, o.usedBeds\n" +
                    "FROM occupancies o INNER JOIN hotels h ON o.id = h.id\n" +
                    "WHERE 1=1"; // WHERE part of query started to add the other filters

            int paramIndex = 1;

            // dynamically add filters to the query
            if (hotelId != -1) {
                sql += " AND o.id = ?";
                paramIndex++;
            }
            if (category != null) {
                sql += " AND h.category >= ?"; // shows all hotels with the same category or higher
                paramIndex++;
            }
            if (year != 0) {
                sql += " AND o.year = ?";
                paramIndex++;
            }
            if (month != 0) {
                sql += " AND o.month = ?";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            paramIndex = 1;

            // parameters for the query
            if (hotelId != -1) {
                ps.setInt(paramIndex, hotelId);
                paramIndex++;
            }
            if (category != null) {
                ps.setString(paramIndex, category);
                paramIndex++;
            }
            if (year != 0) {
                ps.setInt(paramIndex, year);
                paramIndex++;
            }
            if (month != 0) {
                ps.setInt(paramIndex, month);
            }

            ResultSet rs = ps.executeQuery();

            // Enter the data into the table
            while (rs.next()) {
                String[] row = new String[8];
                for (int i = 1; i <= 8; i++) {
                    if (i == 4) {
                        row[i-1] = Month.of(rs.getInt("month")).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                    } else {
                        row[i - 1] = rs.getString(i);
                    }
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private void defineFrame() {
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fenster maximiert starten
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(YearMonth.from(LocalDate.now()) + " (final results)");
    }
}