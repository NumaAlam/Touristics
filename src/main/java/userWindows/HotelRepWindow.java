package userWindows;

import MyApp.LoginWindow;
import US6.TransactionEntryWindow;
import US10.TransactionListWindow;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HotelRepWindow extends JFrame {

    private final int hotelID;
    private final String hotelName;

    public HotelRepWindow(int hotelID) {
        this.hotelID = hotelID;
        this.hotelName = loadHotelName(hotelID);

        setTitle("NOE-TO Hotel Representative Portal");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeUI();
    }

    private void initializeUI() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ===== HEADER =====
        JLabel titleLabel = new JLabel("Hotel Representative Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // ===== CENTER =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1, 15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JLabel hotelInfo = new JLabel("Assigned Hotel ID: " + hotelID, SwingConstants.CENTER);
        hotelInfo.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton addTransactionButton = new JButton("Add Transaction Data");
        JButton viewTransactionsButton = new JButton("View Transaction Data");
        JButton logoutButton = new JButton("Logout");

        // ===== BUTTON ACTIONS =====

        addTransactionButton.addActionListener(e -> {
            try {

                TransactionEntryWindow window =
                        new TransactionEntryWindow(hotelID, hotelName);

                window.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Could not open transaction entry window.");
            }
        });

        viewTransactionsButton.addActionListener(e -> {
            try {

                TransactionListWindow window =
                        new TransactionListWindow();

                window.setVisible(true);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Could not open transaction list.");
            }
        });

        logoutButton.addActionListener(e -> {
            dispose();
            new LoginWindow().setVisible(true);
        });

        // ===== ADD COMPONENTS =====

        centerPanel.add(hotelInfo);
        centerPanel.add(addTransactionButton);
        centerPanel.add(viewTransactionsButton);
        centerPanel.add(logoutButton);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private String loadHotelName(int hotelID) {

        String hotelName = "";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev",
                "dev")) {

            String sql = "SELECT name FROM hotels WHERE id = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, hotelID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                hotelName = rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hotelName;
    }
}
