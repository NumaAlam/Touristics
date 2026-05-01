    package US2;

    import MyApp.Menu;

    import javax.swing.*;
    import javax.swing.table.DefaultTableModel;
    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.sql.*;
    import java.time.LocalDate;
    import java.time.Year;
    import java.time.YearMonth;
    import java.util.ArrayList;

    public class Table extends JFrame{
        static ArrayList<Integer> hotelIDs = new ArrayList<>();
        JTable table;
        static DefaultTableModel model;
        JPanel panel;
        JComboBox<String> categoryComboBox;
        JComboBox<String> yearComboBox;
        JComboBox<String> monthComboBox;
        JComboBox<String> hotelIDComboBox;

        public Table() {
            defineFrame();

            initComponents();

            addActions();

            fillTable_SQL(-1, null, 0, 0);

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

            categoryComboBox.addActionListener(e -> {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                if (selectedCategory.equals("All")) {
                    selectedCategory = null;
                }
                String selectedYear;
                if (yearComboBox.getSelectedItem().equals("All")) {
                    selectedYear = "0";
                } else {
                    selectedYear = (String) yearComboBox.getSelectedItem();
                }
                fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
            });

            yearComboBox.addActionListener(e -> {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                if (selectedCategory.equals("All")) {
                    selectedCategory = null;
                }
                String selectedYear = (String) yearComboBox.getSelectedItem();
                if (selectedYear.equals("All")) {
                    selectedYear = "0";
                }
                fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
            });

            monthComboBox.addActionListener(e -> {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                if (selectedCategory.equals("All")) {
                    selectedCategory = null;
                }
                String selectedYear;
                if (yearComboBox.getSelectedItem().equals("All")) {
                    selectedYear = "0";
                } else {
                    selectedYear = (String) yearComboBox.getSelectedItem();
                }
                fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
            });

            hotelIDComboBox.addActionListener(e -> {
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                if (selectedCategory.equals("All")) {
                    selectedCategory = null;
                }
                String selectedYear;
                if (yearComboBox.getSelectedItem().equals("All")) {
                    selectedYear = "0";
                } else {
                    selectedYear = (String) yearComboBox.getSelectedItem();
                }
                fillTable_SQL(hotelIDs.get(hotelIDComboBox.getSelectedIndex()), selectedCategory, Integer.parseInt(selectedYear), monthComboBox.getSelectedIndex());
            });
        }

        private void initComponents() {
            table = new JTable();
            model = new DefaultTableModel();
            table.setDefaultEditor(Object.class, null);
            table.setModel(model);
            panel = new JPanel();
            categoryComboBox = new JComboBox<>();
            categoryComboBox.addItem("All");
            categoryComboBox.addItem("*****");
            categoryComboBox.addItem("****");
            categoryComboBox.addItem("***");
            categoryComboBox.addItem("**");
            categoryComboBox.addItem("*");


            yearComboBox = new JComboBox<>();
            yearComboBox.addItem("All");
            for (int i = Year.now().getValue(); i >= 1900; i--) {
                yearComboBox.addItem(String.valueOf(i));
            }

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

            hotelIDComboBox = new JComboBox<>();
            hotelIDComboBox.addItem("All");
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                    "dev",
                    "dev")) {
                String sql = "SELECT id, name FROM hotels";
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                hotelIDs.add(-1);
                while (rs.next()) {
                    hotelIDComboBox.addItem(rs.getString("name"));
                    hotelIDs.add(rs.getInt("id"));
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }

            model.addColumn("name");
            model.addColumn("category");
            model.addColumn("year");
            model.addColumn("month");
            model.addColumn("rooms");
            model.addColumn("usedRooms");
            model.addColumn("beds");
            model.addColumn("usedBeds");
        }

        private void addComponents() {
            JScrollPane scrollPane = new JScrollPane(table);
            add(scrollPane, BorderLayout.CENTER);
            add(panel, BorderLayout.NORTH);
            panel.add(new JLabel("Category:"));
            panel.add(categoryComboBox);
            panel.add(new JLabel("Year:"));
            panel.add(yearComboBox);
            panel.add(new JLabel("Month:"));
            panel.add(monthComboBox);
            panel.add(new JLabel("Hotel ID:"));
            panel.add(hotelIDComboBox);

        }

        private void backButton() {
            JButton backButton = new JButton("Close Table");
            backButton.setPreferredSize(new Dimension(100, 50));
            backButton.setBackground(new Color(175 ,175,255));
            add(backButton, BorderLayout.SOUTH);
            backButton.addActionListener(e -> {
                dispose();
            });
        }

        private static void fillTable_SQL(int hotelId, String category, int year, int month) {
            model.setRowCount(0);
            try (Connection conn = DriverManager.getConnection(
                    "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                    "dev",
                    "dev")) {

                String sql = "SELECT h.name, h.category, o.year, o.month, o.rooms, o.usedRooms, o.beds, o.usedBeds\n" +
                        "FROM occupancies o INNER JOIN hotels h ON o.id = h.id\n" +
                        "WHERE 1=1";

                int paramIndex = 1;

                if (hotelId != -1) {
                    sql += " AND o.id = ?";
                    paramIndex++;
                }
                if (category != null) {
                    sql += " AND h.category >= ?";
                    paramIndex++;
                }
                if (year != 0) {
                    sql += " AND o.year = ?";
                    paramIndex++;
                }
                if (month != 0) {
                    sql += " AND o.month = ?";
                }

                PreparedStatement ps = conn.prepareStatement(sql);
                paramIndex = 1;

                if (hotelId != -1) {
                    ps.setInt(paramIndex, hotelId);
                    paramIndex++;
                }
                if (category != null) {
                    ps.setString(paramIndex, category);
                    paramIndex++;
                }
                if (year != 0) {
                    ps.setInt(paramIndex, year);
                    paramIndex++;
                }
                if (month != 0) {
                    ps.setInt(paramIndex, month);
                }


                ResultSet rs = ps.executeQuery();



                while (rs.next()) {
                    String[] row = new String[8];
                    for (int i = 1; i <= 8; i++) {
                        row[i - 1] = rs.getString(i);
                    }
                    model.addRow(row);
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }

        private void defineFrame() {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setTitle(YearMonth.from(LocalDate.now()) + " (final results)");
        }

    }
