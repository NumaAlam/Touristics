package write;

import hotels.Hotel;
import hotels.Hotellimporter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class SimpleWritingProcess {
    static void main() throws IOException {

        List<Hotel> hotels = Hotellimporter.loadHotelsFromFile();


        writeBackup(hotels);


    }

    private static void writeBackup(List<Hotel> hotels) throws IOException {
        String address = "output/backup.txt";
        Path path = Path.of(address);

        ArrayList<String> SOME_DATA = new ArrayList<>();

        for (Hotel hotel : hotels) {
            SOME_DATA.add(hotel.toCSV());
        }

        boolean append = true;

        Files.write(path, SOME_DATA, StandardCharsets.UTF_8, append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

        JOptionPane.showMessageDialog(null, "File was written successfully!");

        int answer = JOptionPane.showConfirmDialog(null, "Do you want to continue?");
        System.out.println(answer);

        if (answer == 1) {
            Desktop.getDesktop().open(path.toFile());
        }
    }
}
