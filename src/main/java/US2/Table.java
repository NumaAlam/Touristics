package US2;

import database.HibernateUtil;
import hotels.Hotel;
import jakarta.persistence.TypedQuery;
import occupancies.Occupancy;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class Table extends JFrame {

    // static list to store hotel IDs from the database.
    // Index 0 is reserved for "All"
    // initialize tables and comboboxes
    JTable table;

    static DefaultTableModel model;

    JPanel panel;

    JComboBox<String> categoryComboBox;

    JComboBox<String> yearComboBox;

    JComboBox<String> monthComboBox;

    JComboBox<Hotel> hotelIDComboBox;

    // initialize constructor
    public Table() {
        defineFrame();
        initComponents();
        addActions();
        fillTable_Hibernate(-1, null, 0, 0);  // import data from SQL DBS
        addComponents();
        backButton();
    }

    // filter actions
    private void addActions() {

        categoryComboBox.addActionListener(e -> {
            updateTable();
        });

        yearComboBox.addActionListener(e -> {
            updateTable();
        });

        monthComboBox.addActionListener(e -> {
            updateTable();
        });

        hotelIDComboBox.addActionListener(e -> {
            updateTable();
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
        Hotel allOption = new Hotel();
        allOption.setName("All");
        allOption.setId(-1);
        hotelIDComboBox.addItem(allOption);
        try (Session session = HibernateUtil.getSessionFactory().openSession();) {
            List<Hotel> hotels = session
                    .createQuery("from Hotel", Hotel.class)
                    .list();
            for (Hotel hotel : hotels) {
                hotelIDComboBox.addItem(hotel);
            }

        } catch (HibernateException e) {
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
        panel.add(new JLabel("Hotel name:"));
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
    private void fillTable_Hibernate(int hotelId, String category, int year, int month) {
        model.setRowCount(0); // clear previous rows

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            StringBuilder hql = new StringBuilder("FROM Occupancy o JOIN FETCH o.hotel WHERE 1=1");

            // Dynamically append the filters to the HQL string
            if (hotelId != -1) {
                hql.append(" AND o.hotel.id = :hotelId");
            }
            if (category != null) {
                hql.append(" AND o.hotel.category >= :category");
            }
            if (year != 0) {
                hql.append(" AND o.year = :year");
            }
            if (month != 0) {
                hql.append(" AND o.month = :month");
            }

            hql.append("ORDER BY name ASC");
            // Create the query and set the parameters
            TypedQuery<Occupancy> query = session.createQuery(hql.toString(), Occupancy.class);
            if (hotelId != -1) {
                query.setParameter("hotelId", hotelId);
            }
            if (category != null) {
                query.setParameter("category", category);
            }
            if (year != 0) {
                query.setParameter("year", year);
            }
            if (month != 0) {
                query.setParameter("month", month);
            }

            // Execute the query and populate the table
            List<Occupancy> results = query.getResultList();
            for (Occupancy occupancy : results) {
                model.addRow(new Object[]{
                        occupancy.getHotel().getName(),
                        occupancy.getHotel().getCategory(),
                        occupancy.getYear(),
                        Month.of(occupancy.getMonth()).getDisplayName(TextStyle.FULL, Locale.ENGLISH),
                        occupancy.getRooms(),
                        occupancy.getUsedRooms(),
                        occupancy.getBeds(),
                        occupancy.getUsedBeds()
                });
            }


        } catch (HibernateException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }

    private void defineFrame() {
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fenster maximiert starten
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle(YearMonth.from(LocalDate.now()) + " (final results)");
    }

    private void updateTable() {
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if ("All".equals(selectedCategory)) {
            selectedCategory = null;
        }
        String selectedYear = (String) yearComboBox.getSelectedItem();
        if ("All".equals(selectedYear)) {
            selectedYear = "0";
        }
        fillTable_Hibernate(((Hotel) hotelIDComboBox.getSelectedItem()).getId(), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
    }

}