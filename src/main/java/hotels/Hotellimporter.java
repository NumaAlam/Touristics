package hotels;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Hotellimporter {
    public static List<Hotel> loadHotelsFromFile() throws FileNotFoundException {
        List<Hotel> hotels = new ArrayList<>();
        Scanner sc = new Scanner(new File("src/main/resources/hotels.txt"));

        sc.nextLine(); // skip the header line

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(",");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].replace("\"", "");
            }
            Hotel hotel = new Hotel(Integer.parseInt(parts[0]), parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7], parts[8], Integer.parseInt(parts[9]), Integer.parseInt(parts[10]));
            hotels.add(hotel);
        }
        return hotels;
    }
}
