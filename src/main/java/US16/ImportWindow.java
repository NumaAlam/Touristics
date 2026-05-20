package US16;

import database.HibernateUtil;
import hotels.Hotel;
import occupancies.Occupancy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ImportWindow extends JFrame {

    JPanel buttonPanel;

    JComboBox<Hotel> hotelComboBox;

    public ImportWindow() {
        defineFrame();
        initComponents();
        defineButtons();
        addComponents();

    }

    private void defineButtons() {
        JButton importButton = new JButton("Import Excel");
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                readExcelFile(selectedFile, (Hotel) hotelComboBox.getSelectedItem());

            }
        });
        buttonPanel.add(importButton);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
        });
        buttonPanel.add(backButton);

        JButton infoButton = new JButton("Info");
        infoButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Please select a hotel and import an Excel file consisting of 4 columns: Year, Month, Used Rooms, Used Beds.");
        });
        buttonPanel.add(infoButton);
    }

    private void readExcelFile(File selectedFile, Hotel hotel) {

        List<Occupancy> occupancyList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(selectedFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Get the first sheet
            Sheet sheet = workbook.getSheetAt(0);
            // Loop through the rows
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) continue;
                Cell yearCell = row.getCell(0);
                if (yearCell == null
                        || yearCell.getCellType() == CellType.BLANK
                        || yearCell.getCellType() != CellType.NUMERIC) {
                    continue;
                }
                int year = (int) row.getCell(0).getNumericCellValue();
                int month = (int) row.getCell(1).getNumericCellValue();
                int usedRooms = (int) row.getCell(2).getNumericCellValue();
                int usedBeds = (int) row.getCell(3).getNumericCellValue();

                Occupancy newEntry = new Occupancy();
                newEntry.setYear(year);
                newEntry.setMonth(month);
                newEntry.setUsedRooms(usedRooms);
                newEntry.setUsedBeds(usedBeds);

                newEntry.setHotel(hotel);
                newEntry.setBeds(hotel.getNoBeds());
                newEntry.setRooms(hotel.getNoRooms());

                if (year <= 0) {
                    errorList.add("Row " + rowNum + " rejected: Year must be greater than 0.");
                } else if (month < 1 || month > 12) {
                    errorList.add("Row " + rowNum + " rejected: Month must be between 1 and 12.");
                } else if (newEntry.getUsedRooms() <= newEntry.getRooms() && newEntry.getUsedBeds() <= newEntry.getBeds()) {
                    occupancyList.add(newEntry);
                } else {
                    errorList.add("Row " + rowNum + " rejected: Used capacity exceeds hotel maximums.");
                }


            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();

            Hotel managedHotel = session.find(Hotel.class, hotel.getId());
            if (managedHotel == null) {
                JOptionPane.showMessageDialog(this, "Selected hotel no longer exists.");
                return;
            }

            for (Occupancy occupancy : occupancyList) {
                occupancy.setHotel(managedHotel);
                session.persist(occupancy);
            }
            session.getTransaction().commit();

            StringBuilder msg = new StringBuilder();
            msg.append(occupancyList.size()).append(" rows imported.");
            if (!errorList.isEmpty()) {
                msg.append("\n\nRejected rows:\n").append(String.join("\n", errorList));
            }
            JOptionPane.showMessageDialog(this, msg.toString());

        } catch (HibernateException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
        }
    }

    private void addComponents() {
        add(buttonPanel, BorderLayout.SOUTH);
        add(new JLabel("Select hotel: "), BorderLayout.NORTH);
        add(hotelComboBox, BorderLayout.CENTER);
    }

    private void initComponents() {
        buttonPanel = new JPanel();
        hotelComboBox = new JComboBox<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession();) {
            List<Hotel> hotels = session
                    .createQuery("from Hotel", Hotel.class)
                    .list();
            for (Hotel hotel : hotels) {
                hotelComboBox.addItem(hotel);
            }

        } catch (HibernateException e) {
            System.err.println("Database error: " + e.getMessage());
        }

    }

    private void defineFrame() {
        setTitle("Import Data");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
}
