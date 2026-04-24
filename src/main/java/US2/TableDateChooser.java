package US2;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDateChooser extends JFrame {

    private static final HotelChoice nullHotel = new HotelChoice(-1, "No Selection");
    private static final int nullYear = 0;
    private static final String nullMonth = "No Selection";
    private static final String nullCat = "No Selection";

    public TableDateChooser () {
        setTitle("Please choose the information for your Transaction data!");
        setSize(200, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JButton button = new JButton("OK");

        HotelChoice[] loaded = loadHotelNames();
        HotelChoice[] hotels = new HotelChoice[loaded.length + 1];
        hotels[0] = nullHotel;
        System.arraycopy(loaded, 0, hotels, 1, loaded.length);
        JComboBox<HotelChoice> hotelBox = new JComboBox<>(hotels);

        JComboBox<String> catBox = new JComboBox<>(new String[] {
                nullCat, "*", "**", "***", "****", "*****"
        });

        Integer[] years = new Integer[2075 - 1899 + 2];
        years[0] = nullYear;
        for (int i = 1; i < years.length; i++) {
            years[i] = 1899 + (i-1);
        }
        JComboBox<Integer> yearBox = new JComboBox<>(years);

        JComboBox<String> monthBox = new JComboBox<>(new String[] {
                nullMonth, "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        });

        JPanel panel = new JPanel(new GridLayout(4,2,10,10));
        panel.add(new JLabel("Year:"));
        panel.add(yearBox);
        panel.add(new JLabel("Month:"));
        panel.add(monthBox);
        panel.add(new JLabel("Category:"));
        panel.add(catBox);
        panel.add(new JLabel("Hotel:"));
        panel.add(hotelBox);

        add(panel, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);

        button.addActionListener(e -> {
            HotelChoice selectedHotel = (HotelChoice) hotelBox.getSelectedItem();
            int hotelId = selectedHotel.getId();
            int year = (int) yearBox.getSelectedItem();
            int month = monthBox.getSelectedIndex() + 1;
            String category = (String) catBox.getSelectedItem();
            dispose();
            new Table(hotelId, year, month, category).setVisible(true);
        });
    }

    private HotelChoice[] loadHotelNames() {
        List<HotelChoice> hotels = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(
                "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                "dev", "dev");
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT id, name FROM hotels");
            while (rs.next()) {
                hotels.add(new HotelChoice(rs.getInt(1), rs.getString(2)));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return hotels.toArray(new HotelChoice[0]);
    }
}

