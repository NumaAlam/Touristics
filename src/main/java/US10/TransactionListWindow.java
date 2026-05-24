package US10;

import database.HibernateUtil;
import hotels.Hotel;
import occupancies.Occupancy;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Year;
import java.util.List;

/**
 * US10 – Shows all transactional data for a selected hotel with optional
 * date range filtering, so senior users can verify data correctness.
 */
public class TransactionListWindow extends JFrame {

    private final Integer hotelID;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> hotelComboBox;
    private JComboBox<String> fromYearComboBox;
    private JComboBox<String> fromMonthComboBox;
    private JComboBox<String> toYearComboBox;
    private JComboBox<String> toMonthComboBox;

    // Keeps hotel DB IDs in sync with the dropdown index so we can query by ID
    private final java.util.ArrayList<Integer> hotelIDs = new java.util.ArrayList<>();

    public TransactionListWindow(Integer hotelID) {
        this.hotelID = hotelID;

        defineFrame();
        initComponents();
        addComponents();
        addActions();
        backButton();
    }

    private void defineFrame() {
        setTitle("Transactional Data per Hotel");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        // --- Table ---
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);

        model.addColumn("Year");
        model.addColumn("Month");
        model.addColumn("Rooms");
        model.addColumn("Used Rooms");
        model.addColumn("Beds");
        model.addColumn("Used Beds");

        // Load hotels into dropdown via Hibernate; IDs are stored in parallel so we can map selection to DB ID
        hotelComboBox = new JComboBox<>();
        hotelIDs.clear();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Hotel> hotels;
            if (hotelID == null) {
                // Senior or other role with no hotel restriction — load all hotels
                hotels = session.createQuery("from Hotel order by name", Hotel.class).list();
            } else {
                // Hotel rep — restrict to their hotel
                Hotel h = session.get(Hotel.class, hotelID);
                hotels = (h == null) ? List.of() : List.of(h);
            }
            for (Hotel h : hotels) {
                hotelComboBox.addItem(h.getName());
                hotelIDs.add(h.getId());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        // --- FROM Year/Month ---
        fromYearComboBox = new JComboBox<>();
        fromYearComboBox.addItem("All");
        for (int i = Year.now().getValue(); i >= 1900; i--) {
            fromYearComboBox.addItem(String.valueOf(i));
        }

        fromMonthComboBox = new JComboBox<>();
        fromMonthComboBox.addItem("All");
        fromMonthComboBox.addItem("January");
        fromMonthComboBox.addItem("February");
        fromMonthComboBox.addItem("March");
        fromMonthComboBox.addItem("April");
        fromMonthComboBox.addItem("May");
        fromMonthComboBox.addItem("June");
        fromMonthComboBox.addItem("July");
        fromMonthComboBox.addItem("August");
        fromMonthComboBox.addItem("September");
        fromMonthComboBox.addItem("October");
        fromMonthComboBox.addItem("November");
        fromMonthComboBox.addItem("December");

        // --- TO Year/Month ---
        toYearComboBox = new JComboBox<>();
        toYearComboBox.addItem("All");
        for (int i = Year.now().getValue(); i >= 1900; i--) {
            toYearComboBox.addItem(String.valueOf(i));
        }

        toMonthComboBox = new JComboBox<>();
        toMonthComboBox.addItem("All");
        toMonthComboBox.addItem("January");
        toMonthComboBox.addItem("February");
        toMonthComboBox.addItem("March");
        toMonthComboBox.addItem("April");
        toMonthComboBox.addItem("May");
        toMonthComboBox.addItem("June");
        toMonthComboBox.addItem("July");
        toMonthComboBox.addItem("August");
        toMonthComboBox.addItem("September");
        toMonthComboBox.addItem("October");
        toMonthComboBox.addItem("November");
        toMonthComboBox.addItem("December");
    }

    private void addComponents() {
        // --- Filter panel ---
        JPanel filterPanel = new JPanel();

        if (hotelID == null) {
            // Senior sees everything
            filterPanel.add(new JLabel("Hotel:"));
            filterPanel.add(hotelComboBox);
            filterPanel.add(Box.createHorizontalStrut(20));
            filterPanel.add(new JLabel("From Year:"));
            filterPanel.add(fromYearComboBox);
            filterPanel.add(new JLabel("From Month:"));
            filterPanel.add(fromMonthComboBox);
            filterPanel.add(Box.createHorizontalStrut(10));
            filterPanel.add(new JLabel("To Year:"));
            filterPanel.add(toYearComboBox);
            filterPanel.add(new JLabel("To Month:"));
            filterPanel.add(toMonthComboBox);
        }

        add(filterPanel, BorderLayout.NORTH);

        // --- Table ---
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addActions() {
        // Every dropdown triggers a refresh so the table always reflects the current filter selection
        if (hotelID == null) {
            hotelComboBox.addActionListener(e -> refreshTable());
            fromYearComboBox.addActionListener(e -> refreshTable());
            fromMonthComboBox.addActionListener(e -> refreshTable());
            toYearComboBox.addActionListener(e -> refreshTable());
            toMonthComboBox.addActionListener(e -> refreshTable());
        }

        // Load initial data if hotels exist
        if (hotelComboBox.getItemCount() > 0) {
            refreshTable();
        }
    }

    private void refreshTable() {
        if (hotelComboBox.getSelectedIndex() < 0 || hotelIDs.isEmpty()) {
            return;
        }

        int hotelId = hotelIDs.get(hotelComboBox.getSelectedIndex());

        int fromYear = 0;
        if (!"All".equals(fromYearComboBox.getSelectedItem())) {
            fromYear = Integer.parseInt((String) fromYearComboBox.getSelectedItem());
        }
        int fromMonth = fromMonthComboBox.getSelectedIndex(); // 0 = All, 1 = Jan, ...

        int toYear = 0;
        if (!"All".equals(toYearComboBox.getSelectedItem())) {
            toYear = Integer.parseInt((String) toYearComboBox.getSelectedItem());
        }
        int toMonth = toMonthComboBox.getSelectedIndex(); // 0 = All, 1 = Jan, ...

        // Validate that FROM date is not after TO date to prevent empty or misleading results
        if (fromYear != 0 && toYear != 0) {
            if (fromYear > toYear || (fromYear == toYear && fromMonth > toMonth && toMonth != 0)) {
                JOptionPane.showMessageDialog(this,
                        "Invalid date range: From must be before To.",
                        "Invalid Range",
                        JOptionPane.WARNING_MESSAGE);
                model.setRowCount(0);
                return;
            }
        }
        fillTable(hotelId, fromYear, fromMonth, toYear, toMonth);
    }

    // Builds HQL dynamically based on active filters; uses named parameters to prevent injection
    private void fillTable(int hotelId, int fromYear, int fromMonth, int toYear, int toMonth) {
        model.setRowCount(0);

        StringBuilder hql = new StringBuilder(
                "from Occupancy o where o.hotel.id = :hotelId");

        // FROM filter: (year > X OR (year = X AND month >= Y)) handles cross-year ranges correctly
        if (fromYear != 0 && fromMonth != 0) {
            hql.append(" and (o.year > :fromYear or (o.year = :fromYear and o.month >= :fromMonth))");
        } else if (fromYear != 0) {
            hql.append(" and o.year >= :fromYear");
        } else if (fromMonth != 0) {
            hql.append(" and o.month >= :fromMonth");
        }

        // TO filter
        if (toYear != 0 && toMonth != 0) {
            hql.append(" and (o.year < :toYear or (o.year = :toYear and o.month <= :toMonth))");
        } else if (toYear != 0) {
            hql.append(" and o.year <= :toYear");
        } else if (toMonth != 0) {
            hql.append(" and o.month <= :toMonth");
        }

        hql.append(" order by o.year desc, o.month desc");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Occupancy> query = session.createQuery(hql.toString(), Occupancy.class);
            query.setParameter("hotelId", hotelId);

            if (fromYear != 0) query.setParameter("fromYear", fromYear);
            if (fromMonth != 0) query.setParameter("fromMonth", fromMonth);
            if (toYear != 0) query.setParameter("toYear", toYear);
            if (toMonth != 0) query.setParameter("toMonth", toMonth);

            List<Occupancy> results = query.getResultList();
            for (Occupancy o : results) {
                model.addRow(new Object[]{
                        o.getYear(),
                        getMonthName(o.getMonth()),
                        o.getRooms(),
                        o.getUsedRooms(),
                        o.getBeds(),
                        o.getUsedBeds()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Converts month number (1-12) to name for readable table display
    private String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        if (month >= 1 && month <= 12) {
            return months[month];
        }
        return String.valueOf(month);
    }

    private void backButton() {
        JButton backButton = new JButton("Close Table");
        backButton.setPreferredSize(new Dimension(100, 50));
        backButton.setBackground(new Color(175, 175, 255));
        add(backButton, BorderLayout.SOUTH);
        backButton.addActionListener(e -> dispose());
    }
}