package US2;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.time.LocalDate;
import java.time.YearMonth;

public class Table extends JFrame{
    JTable table;
    static DefaultTableModel model;
    
    public Table(int hotelID, int year, int month, String category) {
        defineFrame();

        initComponents();

        addActions();

        fillTable(hotelID, year, month, category);

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
                        String rooms = (String) table.getValueAt(row, 1);
                        String roomOccupancy = (String) table.getValueAt(row, 2);
                        String beds = (String) table.getValueAt(row, 3);
                        String bedOccupancy = (String) table.getValueAt(row, 4);

                        new EditorWindowTransactionalDate(category, rooms, roomOccupancy, beds, bedOccupancy).setVisible(true);
                    }
                }
            }
        });
    }

    private void initComponents() {
        table = new JTable();
        model = new DefaultTableModel();
        table.setDefaultEditor(Object.class, null);
        table.setModel(model);
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

    private static void fillTable(int hotelID, int year, int month, String category) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev")) {
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("");

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

    private void defineFrame() {
        setSize(520, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(YearMonth.from(LocalDate.now()) + " (final results)");
    }

}
