package US20;

import database.HibernateUtil;
import hotels.Hotel;
import occupancies.Occupancy;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SaveBackup {

    private static final String BACKUP_DIR = "output";

    public List<Hotel> getAllHotels() {
        List<Hotel> hotels = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            hotels = session.createQuery("from Hotel", Hotel.class).list();
        } catch (HibernateException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return hotels;
    }

    public List<Occupancy> getAllOccupancies() {
        List<Occupancy> occupancies = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            occupancies = session.createQuery("from Occupancy", Occupancy.class).list();
        } catch (HibernateException e) {
            System.err.println("Database error: " + e.getMessage());
        }
        return occupancies;
    }

    public String buildTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
    }

    public void writeCSV(Path path, List<String> lines) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, lines, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /*
     Creates two CSV files (hotels + occupancies) sharing one timestamp.
     Returns true on success, false on any failure.
     */
    public boolean createBackup() {
        try {
            List<Hotel> hotels = getAllHotels();
            List<Occupancy> occupancies = getAllOccupancies();

            if (hotels == null || occupancies == null) {
                return false; // queries already logged the error
            }

            String timestamp = buildTimestamp();

            // hotels file
            List<String> hotelLines = new ArrayList<>();
            hotelLines.add("id,category,name,owner,contact,address,city,cityCode,phone,noRooms,noBeds");
            for (Hotel hotel : hotels) {
                hotelLines.add(hotel.toCSV());
            }
            writeCSV(Paths.get(BACKUP_DIR, "hotels_" + timestamp + ".csv"), hotelLines);

            // occupancies file
            List<String> occupancyLines = new ArrayList<>();
            occupancyLines.add("hotelId,year,month,rooms,usedRooms,beds,usedBeds");
            for (Occupancy occupancy : occupancies) {
                occupancyLines.add(occupancy.toCSV());
            }
            writeCSV(Paths.get(BACKUP_DIR, "occupancies_" + timestamp + ".csv"), occupancyLines);

            return true;
        } catch (IOException e) {
            System.err.println("Backup error: " + e.getMessage());
            return false;
        }
    }
}