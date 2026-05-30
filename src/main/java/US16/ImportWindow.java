package US16;

import database.HibernateUtil;
import hotels.Hotel;
import occupancies.Occupancy;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ImportWindow extends JFrame {

    JPanel buttonPanel;

    JComboBox<Hotel> hotelComboBox;

    public ImportWindow() {
        defineFrame();
        initComponents();
        defineButtons();
        addComponents();

        pack();
        setLocationRelativeTo(null);
    }

    private void defineButtons() {
        JButton importButton = new JButton("Import Excel");
        importButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));
            fileChooser.setAcceptAllFileFilterUsed(false);

            int result = fileChooser.showOpenDialog(this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                    JOptionPane.showMessageDialog(this, "Please select an .xlsx file.");
                    return;
                }
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

            //Declare Hash set to store the keys
            HashSet<String> keys = new HashSet<>();
            // Loop through the rows
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null)  {
                    errorList.add("Row " + rowNum + " rejected: Row is empty.");
                    continue;
                }

                Cell yearCell = row.getCell(0);
                if (yearCell == null) {
                    errorList.add("Row " + rowNum + " rejected: Year is empty.");
                    continue;
                } else if (yearCell.getCellType() == CellType.BLANK) {
                    errorList.add("Row " + rowNum + " rejected: Year is blank.");
                    continue;
                } else if (yearCell.getCellType() != CellType.NUMERIC) {
                    errorList.add("Row " + rowNum + " rejected: Year is not numeric.");
                    continue;
                } else if (Math.round(yearCell.getNumericCellValue()) != yearCell.getNumericCellValue()) {
                    errorList.add("Row " + rowNum + " rejected: Year is not an integer.");
                    continue;
                } else if (yearCell.getNumericCellValue() < 1900) {
                    errorList.add("Row " + rowNum + " rejected: Year must be greater than or equal to 1900.");
                    continue;
                }

                Cell monthCell = row.getCell(1);
                if (monthCell == null) {
                    errorList.add("Row " + rowNum + " rejected: Month is empty.");
                    continue;
                } else if (monthCell.getCellType() == CellType.BLANK) {
                    errorList.add("Row " + rowNum + " rejected: Month is blank.");
                    continue;
                } else if (monthCell.getCellType() != CellType.NUMERIC) {
                    errorList.add("Row " + rowNum + " rejected: Month is not numeric.");
                    continue;
                } else if (Math.round(monthCell.getNumericCellValue()) != monthCell.getNumericCellValue()) {
                    errorList.add("Row " + rowNum + " rejected: Month is not an integer.");
                    continue;
                } else if (monthCell.getNumericCellValue() < 1 || monthCell.getNumericCellValue() > 12) {
                    errorList.add("Row " + rowNum + " rejected: Month must be between 1 and 12.");
                    continue;
                }

                int year = (int) yearCell.getNumericCellValue();
                int month = (int) monthCell.getNumericCellValue();
                String key = year + "-" + month;
                if (!keys.add(key)) {
                    errorList.add("Row " + rowNum + " rejected: Duplicate Year-Month combination.");
                    continue;
                }

                Cell uRCell = row.getCell(2);
                if (uRCell == null) {
                    errorList.add("Row " + rowNum + " rejected: Used Rooms is empty.");
                    continue;
                } else if (uRCell.getCellType() == CellType.BLANK) {
                    errorList.add("Row " + rowNum + " rejected: Used Rooms is blank.");
                    continue;
                } else if (uRCell.getCellType() != CellType.NUMERIC) {
                    errorList.add("Row " + rowNum + " rejected: Used Rooms is not numeric.");
                    continue;
                } else if (Math.round(uRCell.getNumericCellValue()) != uRCell.getNumericCellValue()) {
                    errorList.add("Row " + rowNum + " rejected: Used Rooms is not an integer.");
                    continue;
                } else if (uRCell.getNumericCellValue() < 0) {
                    errorList.add("Row " + rowNum + " rejected: Used Rooms must be greater than or equal to 0.");
                    continue;
                }

                Cell uBCell = row.getCell(3);
                if (uBCell == null) {
                    errorList.add("Row " + rowNum + " rejected: Used Beds is empty.");
                    continue;
                } else if (uBCell.getCellType() == CellType.BLANK) {
                    errorList.add("Row " + rowNum + " rejected: Used Beds is blank.");
                    continue;
                } else if (uBCell.getCellType() != CellType.NUMERIC) {
                    errorList.add("Row " + rowNum + " rejected: Used Beds is not numeric.");
                    continue;
                } else if (Math.round(uBCell.getNumericCellValue()) != uBCell.getNumericCellValue()) {
                    errorList.add("Row " + rowNum + " rejected: Used Beds is not an integer.");
                    continue;
                } else if (uBCell.getNumericCellValue() < 0) {
                    errorList.add("Row " + rowNum + " rejected: Used Beds must be greater than or equal to 0.");
                    continue;
                }

                int usedRooms = (int) uRCell.getNumericCellValue();
                int usedBeds = (int) uBCell.getNumericCellValue();

                Occupancy newEntry = new Occupancy();
                newEntry.setYear(year);
                newEntry.setMonth(month);
                newEntry.setUsedRooms(usedRooms);
                newEntry.setUsedBeds(usedBeds);

                newEntry.setHotel(hotel);
                newEntry.setBeds(hotel.getNoBeds());
                newEntry.setRooms(hotel.getNoRooms());

                if (newEntry.getUsedRooms() <= newEntry.getRooms() && newEntry.getUsedBeds() <= newEntry.getBeds()) {
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
        ImageIcon logo = new ImageIcon(getClass().getResource("/2026-LATP_Logo.jpg"));
        Image scaled = logo.getImage().getScaledInstance(480, 120, Image.SCALE_SMOOTH);
        add(new JLabel(new ImageIcon(scaled)), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        centerPanel.add(new JLabel("Select hotel: "), BorderLayout.NORTH);
        centerPanel.add(hotelComboBox, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        add(buttonPanel, BorderLayout.SOUTH);
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
        setTitle("Lower Austria Tourist Portal — Import Transactions");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }
}
