package hotels;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    static void main(String args[]) throws FileNotFoundException {

        String path = "src/main/resources/hotels.txt";
        Scanner sc = new Scanner(new File(path));

        while (sc.hasNextLine()) {
            System.out.println(sc.nextLine());
        }
    }
}
