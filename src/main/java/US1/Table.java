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

    public Table() {
            defineFrame();

            initComponents();

            addActions();

            fillTable();

            addComponents();

            backButton();


    }

    private void addActions() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        String category = (String) table.getValueAt(row, 0);
                        String numberOfEstablishments = (String) table.getValueAt(row, 1);
                        String rooms = (String) table.getValueAt(row, 2);
                        String beds = (String) table.getValueAt(row, 3);

                        new EditorWindowMasterTable(category, numberOfEstablishments, rooms, beds).setVisible(true);
                    }
                }
            }
        });
    }


    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void backButton() {
        JButton backButton = new JButton("Close Table");
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> {
            dispose();
            new Menu().setVisible(true);
        });
    }

    private void fillTable() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT \n" +
                    "    ISNULL(Combined, 'Total') AS Category,\n" +
                    "    COUNT(*) AS Establishments,\n" +
                    "    SUM(noRooms) AS Rooms,\n" +
                    "    SUM(noBeds) AS Beds\n" +
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

            // 1. Spaltennamen dynamisch holen
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                // Holt die Namen aus dem "AS ..." Teil deines SQLs
                model.addColumn(metaData.getColumnLabel(i));
            }

            // 2. Zeilen dynamisch füllen
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

    private void initComponents() {
        table = new JTable();
        model = new DefaultTableModel();
        table.setDefaultEditor(Object.class, null);
        table.setModel(model);
    }

    private void defineFrame() {
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Current Hotel Capacity. Date - " + java.time.LocalDate.now());
    }
}
