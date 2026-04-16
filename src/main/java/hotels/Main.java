package hotels;

import java.io.FileNotFoundException;
import java.util.List;

public class Main {
    static void main(String args[]) throws FileNotFoundException {

        List<Hotel> hotels = Hotellimporter.loadHotelsFromFile();

        for (int i = 0; i < hotels.size(); i++) {
            System.out.println(hotels.get(i));
        }
    }


}
