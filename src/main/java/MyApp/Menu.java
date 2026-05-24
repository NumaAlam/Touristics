package MyApp;

import jakarta.persistence.criteria.CriteriaBuilder;
import userWindows.SeniorWindow;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Menu extends JFrame {
    private final Integer hotelID;

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Menu");
    JMenuItem helpMenuItem = new JMenuItem("Help");
    JMenuItem exitMenuItem = new JMenuItem("Exit");
    JMenuItem capacityMenuItem = new JMenuItem("Master Data Summary");
    JMenuItem occupancyMenuItem = new JMenuItem("Transactional Data");
    JMenuItem addHotelMenuItem = new JMenuItem("Add Hotel");
    JMenuItem userManagementMenuItem = new JMenuItem("User Management");

    JMenuItem hotelOverviewMenuItem = new JMenuItem("Hotel Overview");
    JMenuItem deleteHotelMenuItem = new JMenuItem("Delete Hotel");
    JMenuItem transactionListMenuItem = new JMenuItem("Transaction List per Hotel");

    JMenuItem importTransactionMenuItem = new JMenuItem("Import Transactions");

    public Menu(Integer hotelID) {
        this.hotelID = hotelID;

        setSize(300, 200);
        menu.add(helpMenuItem);
        menu.add(capacityMenuItem);
        menu.add(occupancyMenuItem);
        menu.add(addHotelMenuItem);
        menu.add(hotelOverviewMenuItem);
        menu.add(deleteHotelMenuItem);
        menu.add(transactionListMenuItem);
        menu.add(exitMenuItem);
        menu.add(userManagementMenuItem);
        menu.add(importTransactionMenuItem);

        menuBar.add(menu);
        setJMenuBar(menuBar);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        capacityMenuItem.addActionListener(e -> {
           new US1.Table().setVisible(true);
        });
        exitMenuItem.addActionListener(e -> {
            dispose();
            new SeniorWindow("Welcome Senior").setVisible(true);
        });
        occupancyMenuItem.addActionListener(e -> {
            new US2.Table().setVisible(true);
        });

        addHotelMenuItem.addActionListener(e -> {
            new US3.AddHotelWindow().setVisible(true);
        });

        hotelOverviewMenuItem.addActionListener(e -> {
            new us4_us5.HotelOverviewWindow().setVisible(true);
        });

        deleteHotelMenuItem.addActionListener(e -> {
            new US11.DeleteHotelWindow().setVisible(true);
        });

        userManagementMenuItem.addActionListener(e -> {
            new us12.UserManagement().setVisible(true);
        });

        helpMenuItem.addActionListener(e -> {
            try {
                String text = Files.readString(Path.of("src/main/resources/help.txt"));
                JOptionPane.showMessageDialog(this, text, "Help", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "help.txt konnte nicht geladen werden.");
            }
        });

        transactionListMenuItem.addActionListener(e -> {
            new US10.TransactionListWindow(hotelID).setVisible(true);
        });

        importTransactionMenuItem.addActionListener(e -> {
            new US16.ImportWindow().setVisible(true);
        });


    }

}
