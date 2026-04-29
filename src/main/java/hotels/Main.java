package hotels;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    static void main(String args[]) throws FileNotFoundException {

        ArrayList<Hotel> ALL_HOTELS = HotelUtilities.getHotels();

        ALL_HOTELS.forEach(System.out::println);
    }


}