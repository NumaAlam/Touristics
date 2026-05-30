package US1;

import MyApp.TableStyler;
import database.HibernateUtil;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

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

        pack();
        setLocationRelativeTo(null);
    }

    // puts the table in a scroll pane and centers it
    private void addComponents() {
        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        add(new JLabel(new ImageIcon(scaled), SwingConstants.CENTER), BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 150));
        add(scrollPane, BorderLayout.CENTER);
    }

    // adds a button to close the table
    private void backButton() {
        JButton backButton = new JButton("Close Table");
        backButton.setPreferredSize(new Dimension(100, 50));
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> {
            dispose();
        });
    }

    // imports table data from DBS via sql
    private void fillTable() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Selects no of establishments, avg no of rooms and avg no of beds – grouped by category
            String sqlString = "SELECT \n" +
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
                    "ORDER BY GROUPING(Combined) ASC, Category DESC;";
            List <Object[]> results = session
                    .createNativeQuery(sqlString, Object[].class)
                    .getResultList();


            // Takes result set metadata and adds column names to the table model
            model.addColumn("Category");
            model.addColumn("Establishments");
            model.addColumn("AverageNoOfRooms");
            model.addColumn("AverageNoOfBeds");

            // enters the result set into the table model

            for (Object[] row : results) {
                model.addRow(row);
            }
        } catch (HibernateException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    // Initializes the table and its components
    private void initComponents() {
        table = new JTable();
        TableStyler.styleTable(table);
        model = new DefaultTableModel();
        table.setDefaultEditor(Object.class, null); // Deactivates cell editing
        table.setModel(model);
    }

    // Defines the frame settings
    private void defineFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Lower Austria Tourist Portal — Master Data Summary");
    }
}