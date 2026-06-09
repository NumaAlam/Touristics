package userWindows;

import MyApp.LoginWindow;
import US6.TransactionEntryWindow;
import US10.TransactionListWindow;
import us4_us5.HotelOverviewWindow;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class HotelRepWindow extends JFrame {

    private final int hotelID;
    private final String hotelName;

    public HotelRepWindow(int hotelID) {
        this.hotelID = hotelID;
        this.hotelName = loadHotelName(hotelID);

        setTitle("Lower Austria Tourist Portal — Hotel Representative Portal");
        initializeUI();
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaled));
        add(logoLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ===== HEADER =====
        setTitle("NOE-TO Hotel Representative Portal");

        // ===== CENTER =====
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(6, 1, 15, 15));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));

        JLabel hotelInfo = new JLabel("Assigned Hotel: " + hotelName, SwingConstants.CENTER);
        hotelInfo.setFont(new Font("Arial", Font.PLAIN, 16));

        JButton viewHotelDataButton = new JButton("View My Hotel Data");
        JButton addTransactionButton = new JButton("Add Transaction Data");
        JButton viewTransactionsButton = new JButton("View Transaction Data");
        JButton logoutButton = new JButton("Logout");
        JButton helpButton = new JButton("Help");


        // ===== BUTTON ACTIONS =====

        // US24/US25: Opens the hotel overview restricted to the representative's assigned hotel.
        // This ensures that the hotel representative can only see and access their own hotel data.
        viewHotelDataButton.addActionListener(e -> {
            try {
                HotelOverviewWindow window = new HotelOverviewWindow(hotelID);
                window.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Could not open hotel overview.");
            }
        });

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
                        new TransactionListWindow(hotelID);

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
        centerPanel.add(viewHotelDataButton); // US24/US25: Adds the button that allows the hotel representative to view their own hotel master data.
        centerPanel.add(addTransactionButton);
        centerPanel.add(viewTransactionsButton);
        centerPanel.add(logoutButton);
        centerPanel.add(helpButton);


        mainPanel.add(centerPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
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
