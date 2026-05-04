package us4_us5;

import MyApp.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HotelOverviewWindow extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public HotelOverviewWindow() {
        defineFrame();
        initComponents();
        fillTable();
        addComponents();
        addButtonPanel();
    }

    private void defineFrame() {
        setTitle("Hotel Overview");
        setSize(1100, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Address");
        model.addColumn("Rooms");
        model.addColumn("Beds");
        model.addColumn("Last Year");
        model.addColumn("Last Month");
        model.addColumn("Used Rooms");
        model.addColumn("Used Beds");

    }

    private void fillTable() {
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
                       
                try (Connection conn = DriverManager.getConnection(
                        "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                                "dev",
                                "dev");
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(sql)) {
                    while (rs.next()) {
                        model.addRow(new Object[]{
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("address"),
                                rs.getInt("noRooms"),
                                rs.getInt("noBeds"),
                                rs.getObject("lastYear"),
                                rs.getObject("lastMonth"),
                                rs.getObject("usedRooms"),
                                rs.getObject("usedBeds")


                        });
                    }
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Database error: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
    }

    private void addComponents() {
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addButtonPanel() {
        JPanel buttonPanel = new JPanel();

        JButton editButton = new JButton("Edit selected Hotel");
        JButton backButton = new JButton("Back to Menu");

        //US6
        JButton transactionButton = new JButton("Add Transaction");

        buttonPanel.add(editButton);
        buttonPanel.add(backButton);
        //US6
        buttonPanel.add(transactionButton);

        add(buttonPanel, BorderLayout.SOUTH);
        addEditButtonFunction(editButton);
        addBackButtonFunction(backButton);
        //US6
        addTransactionButtonFunction(transactionButton);


    }

    private void addBackButtonFunction(JButton backButton) {
        backButton.addActionListener(e -> {
            dispose();
        });
    }

    private void addEditButtonFunction(JButton editButton) {
        editButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a hotel first.",
                        "No hotel selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            int hotelId = (int) model.getValueAt(selectedRow, 0);

            new HotelEditWindow(hotelId).setVisible(true);
        });
    }

    //US 5

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

