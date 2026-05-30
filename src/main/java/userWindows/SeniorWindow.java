    package userWindows;


    import MyApp.LoginWindow;
    import MyApp.Session;
    import us4_us5.HotelOverviewWindow;

    import javax.swing.*;
    import javax.swing.border.EmptyBorder;
    import java.awt.*;
    import java.nio.file.Files;
    import java.nio.file.Path;

    public class SeniorWindow extends JFrame {


        public SeniorWindow(String text) {
            setTitle(text);

            setLayout(new BorderLayout());
            ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
            Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled));
            add(logoLabel, BorderLayout.NORTH);

            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new GridLayout(5, 2,10,10));
            centerPanel.setBorder(new EmptyBorder(10,10,00,10));
            add(centerPanel, BorderLayout.CENTER);

            JButton hotelOverviewButton = new JButton("Hotel Overview");
            centerPanel.add(hotelOverviewButton);
            JButton addHotelButton = new JButton("Add Hotel");
            centerPanel.add(addHotelButton);
            JButton deleteHotelButton = new JButton("Delete Hotel");
            centerPanel.add(deleteHotelButton);

            JButton masterDataButton = new JButton("Master Data Summary");
            centerPanel.add(masterDataButton);
            JButton transactionDataButton = new JButton("Transactional Data");
            centerPanel.add(transactionDataButton);
            JButton transactionListButton = new JButton("Transaction List per Hotel");
            centerPanel.add(transactionListButton);
            JButton importTransactionsButton = new JButton("Import Transactions");
            centerPanel.add(importTransactionsButton);

            JButton userManagementButton = new JButton("User Management");
            centerPanel.add(userManagementButton);

            JButton helpButton = new JButton("Help");
            centerPanel.add(helpButton);
            JButton createBackupButton = new JButton("Create Backup");
            centerPanel.add(createBackupButton);

            JPanel southPanel = new JPanel();
            JButton logoutButton = new JButton("Logout");
            southPanel.add(logoutButton);
            add(southPanel, BorderLayout.SOUTH);


            pack();
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);

            hotelOverviewButton.addActionListener(e -> new HotelOverviewWindow().setVisible(true));
            addHotelButton.addActionListener(e -> new US3.AddHotelWindow().setVisible(true));
            deleteHotelButton.addActionListener(e -> new US11.DeleteHotelWindow().setVisible(true));

            masterDataButton.addActionListener(e -> new US1.Table().setVisible(true));
            transactionDataButton.addActionListener(e -> new US2.Table().setVisible(true));
            transactionListButton.addActionListener(e -> new US10.TransactionListWindow(Session.currentHotelId).setVisible(true));
            importTransactionsButton.addActionListener(e -> new US16.ImportWindow().setVisible(true));

            userManagementButton.addActionListener(e -> new us12.UserManagement().setVisible(true));

            helpButton.addActionListener(e -> {
                try {
                    String helpInfo = Files.readString(Path.of("src/main/resources/help.txt"));
                    JOptionPane.showMessageDialog(this, helpInfo, "Help", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "help.txt konnte nicht geladen werden.");
                }
            });
            createBackupButton.addActionListener(e -> {
                createBackupButton.setEnabled(false);
                createBackupButton.setText("Creating Backup...");

                SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                    protected Boolean doInBackground() throws Exception {
                        return new US20.SaveBackup().createBackup();
                    }
                    protected void done() {
                        createBackupButton.setEnabled(true);
                        createBackupButton.setText("Create Backup");
                        try {
                            boolean success = get();
                            if (success) {
                                JOptionPane.showMessageDialog(null, "Backup successfully created.");
                            } else {
                                JOptionPane.showMessageDialog(null, "Backup failed.");
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "Backup failed: " + ex.getMessage());
                        }
                    }
                };
                worker.execute();
            });
            logoutButton.addActionListener(e -> {
                dispose();
                new LoginWindow().setVisible(true);
            });
        }
    }
