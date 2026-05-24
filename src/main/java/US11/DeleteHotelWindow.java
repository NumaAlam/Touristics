package US11;

import database.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * US11 – Lets senior users delete a hotel together with all its linked
 * transactional data, so no occupancy rows are left without a parent hotel.
 * A confirmation dialog ("are you sure") is shown before deletion, as required
 * by the case study.
 *
 * The window supports live filtering by hotel name, category and city so the
 * user can quickly find the right hotel before deleting it. The actual cascade
 * deletion is delegated to HotelDeletionService so the same logic can be reused
 * by the delete button on HotelEditWindow (US4/US5).
 */
public class DeleteHotelWindow extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private JTextField nameFilterField;
    private JComboBox<String> categoryFilterBox;
    private JTextField cityFilterField;
    private JButton clearFilterButton;

    private JButton deleteButton;
    private JButton refreshButton;
    private JButton closeButton;

    public DeleteHotelWindow() {
        // US13 permission pattern: only Senior, Senior_Admin or users with the
        // delete right may open this window. Same check as in HotelEditWindow.
        if (!isDeleteAllowed()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Access denied. You do not have permission to delete hotels.",
                    "Access denied",
                    JOptionPane.ERROR_MESSAGE
            );
            dispose();
            return;
        }

        defineFrame();
        initComponents();
        addComponents();
        addActions();
        fillTable();
    }

    private boolean isDeleteAllowed() {
        return "Senior".equals(MyApp.Session.currentRole)
                || "Senior_Admin".equals(MyApp.Session.currentRole)
                || Boolean.TRUE.equals(MyApp.Session.canDelete);
    }

    private void defineFrame() {
        setTitle("Delete Hotel and linked Transactional Data");
        setSize(1200, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        // --- Table ---
        model = new DefaultTableModel();
        table = new JTable(model);
        table.setDefaultEditor(Object.class, null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Category");
        model.addColumn("Address");
        model.addColumn("City");
        model.addColumn("Rooms");
        model.addColumn("Beds");
        // The two count columns make the cascade impact visible BEFORE the user
        // confirms the deletion.
        model.addColumn("Linked Transactions");
        model.addColumn("Linked Users");

        // --- Filter fields ---
        nameFilterField = new JTextField();
        cityFilterField = new JTextField();
        categoryFilterBox = new JComboBox<>(
                new String[]{"All", "*****", "****", "***", "**", "*"});
        clearFilterButton = new JButton("Clear Filters");

        // --- Action buttons ---
        deleteButton = new JButton("Delete selected Hotel");
        refreshButton = new JButton("Refresh");
        closeButton = new JButton("Close");
    }

    private void addComponents() {
        // --- Filter panel (NORTH) ---
        JPanel filterPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        filterPanel.add(new JLabel("Search name:"));
        filterPanel.add(nameFilterField);
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryFilterBox);

        filterPanel.add(new JLabel("Search city:"));
        filterPanel.add(cityFilterField);
        filterPanel.add(new JLabel("")); // spacer to keep the grid aligned
        filterPanel.add(clearFilterButton);

        add(filterPanel, BorderLayout.NORTH);

        // --- Table (CENTER) ---
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Buttons (SOUTH) ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addActions() {
        // Live filtering: any change in any filter field/box re-runs the query.
        onDocumentChange(nameFilterField, this::refreshTable);
        onDocumentChange(cityFilterField, this::refreshTable);
        categoryFilterBox.addActionListener(e -> refreshTable());

        clearFilterButton.addActionListener(e -> {
            nameFilterField.setText("");
            cityFilterField.setText("");
            categoryFilterBox.setSelectedIndex(0);
            // The document listeners will already trigger refreshTable; this
            // explicit call covers the case where the fields were already empty.
            refreshTable();
        });

        deleteButton.addActionListener(e -> handleDelete());
        refreshButton.addActionListener(e -> refreshTable());
        closeButton.addActionListener(e -> dispose());
    }

    // Small helper so each text field can trigger refreshTable without three
    // duplicate inner classes per field.
    private static void onDocumentChange(JTextField field, Runnable action) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { action.run(); }
            @Override public void removeUpdate(DocumentEvent e) { action.run(); }
            @Override public void changedUpdate(DocumentEvent e) { action.run(); }
        });
    }

    private void refreshTable() {
        model.setRowCount(0);
        fillTable();
    }

    // Loads all hotels (filtered) together with the count of linked occupancies
    // and the count of users assigned to that hotel via HQL subqueries.
    private void fillTable() {
        String nameFilter = nameFilterField.getText().trim().toLowerCase();
        String cityFilter = cityFilterField.getText().trim().toLowerCase();
        String categoryFilter = (String) categoryFilterBox.getSelectedItem();
        boolean hasCategoryFilter = categoryFilter != null && !"All".equals(categoryFilter);

        StringBuilder hql = new StringBuilder("""
                SELECT h.id, h.name, h.category, h.address, h.city,
                       h.noRooms, h.noBeds,
                       (SELECT COUNT(o) FROM Occupancy o WHERE o.hotel.id = h.id),
                       (SELECT COUNT(u) FROM User u WHERE u.hotelID = h.id)
                FROM Hotel h
                WHERE 1 = 1
                """);

        if (!nameFilter.isEmpty()) {
            hql.append(" AND LOWER(h.name) LIKE :nameFilter");
        }
        if (!cityFilter.isEmpty()) {
            hql.append(" AND LOWER(h.city) LIKE :cityFilter");
        }
        if (hasCategoryFilter) {
            hql.append(" AND h.category = :categoryFilter");
        }
        hql.append(" ORDER BY h.id");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Object[]> query = session.createQuery(hql.toString(), Object[].class);

            if (!nameFilter.isEmpty()) {
                query.setParameter("nameFilter", "%" + nameFilter + "%");
            }
            if (!cityFilter.isEmpty()) {
                query.setParameter("cityFilter", "%" + cityFilter + "%");
            }
            if (hasCategoryFilter) {
                query.setParameter("categoryFilter", categoryFilter);
            }

            List<Object[]> rows = query.getResultList();
            for (Object[] row : rows) {
                model.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while loading hotels: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Validates that a hotel is selected, shows the "are you sure" dialog from
    // the case study (including the cascade impact), and delegates the actual
    // deletion to HotelDeletionService.
    private void handleDelete() {
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
        String hotelName = String.valueOf(model.getValueAt(selectedRow, 1));
        long linkedTransactions = ((Number) model.getValueAt(selectedRow, 7)).longValue();
        long linkedUsers = ((Number) model.getValueAt(selectedRow, 8)).longValue();

        String message = String.format(
                "Are you sure you want to delete hotel \"%s\" (ID %d)?%n" +
                        "This will also delete %d linked transactional data record(s)%n" +
                        "and unlink %d user(s) currently assigned to this hotel.%n" +
                        "This action cannot be undone.",
                hotelName, hotelId, linkedTransactions, linkedUsers
        );

        int confirm = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        HotelDeletionService.DeletionResult result =
                HotelDeletionService.deleteHotelWithCascade(hotelId);

        if (result.success) {
            JOptionPane.showMessageDialog(
                    this,
                    String.format(
                            "Hotel \"%s\" was deleted successfully.%n" +
                                    "%d transactional record(s) deleted, %d user(s) unlinked.",
                            hotelName, result.deletedOccupancies, result.unlinkedUsers
                    ),
                    "Deletion successful",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Hibernate error while deleting hotel: " + result.errorMessage,
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
        refreshTable();
    }
}