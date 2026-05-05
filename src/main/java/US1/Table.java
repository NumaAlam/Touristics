package US1;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class Table extends JFrame {

    JTable table;

    DefaultTableModel model;

    // initialize constructor
    public Table() {
        defineFrame();
        initComponents();
        fillTable();        // import data from SQL DBS
        addComponents();
        backButton();
    }

    // puts the table in a scroll pane and centers it
    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    // adds a button to close the table
    private void backButton() {
        JButton backButton = new JButton("Close Table");
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.setBackground(new Color(175, 175, 255));
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> {
            dispose();
        });
    }

    // imports table data from DBS via sql
    private void fillTable() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev")) {
            Statement stmt = conn.createStatement();

            // Selects no of establishments, avg no of rooms and avg no of beds – grouped by category
            ResultSet rs = stmt.executeQuery("SELECT \n" +
                    "    ISNULL(Combined, 'Total') AS Category,\n" +
                    "    COUNT(*) AS Establishments,\n" +
                    "    AVG(noRooms) AS AverageNoOfRooms,\n" +
                    "    AVG(noBeds) AS AverageNoOfBeds\n" +
                    "FROM (\n" +
                    "    SELECT \n" +
                    "        CASE \n" +
                    "            WHEN category IN ('*', '**') THEN '* & **' \n" +
                    "            ELSE category \n" +
                    "        END AS Combined,\n" +
                    "        noRooms,\n" +
                    "        noBeds\n" +
                    "    FROM hotels\n" +
                    ") AS Sub\n" +
                    "GROUP BY ROLLUP(Combined)\n" +
                    "ORDER BY GROUPING(Combined) ASC, Category DESC;");

            // Takes result set metadata and adds column names to the table model
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnLabel(i));
            }

            // enters the result set into the table model
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                model.addRow(row);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    // Initializes the table and its components
    private void initComponents() {
        table = new JTable();
        model = new DefaultTableModel();
        table.setDefaultEditor(Object.class, null); // Deactivates cell editing
        table.setModel(model);
    }

    // Defines the frame settings
    private void defineFrame() {
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Current Hotel Capacity. Date - " + java.time.LocalDate.now());
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the window
    }
}