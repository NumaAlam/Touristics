package task;
/*
import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    static void main() throws FileNotFoundException {


        String path = "src/main/resources/hotels.txt";

        Scanner sc = new Scanner(new File(path));
        sc.nextLine();

        while(sc.hasNextLine()) {

            String parts = sc.nextLine().split(",");
           // System.out.println(Arrays.toString(parts));
            // id,category,name,owner,contact,address,city,cityCode,phone,noRooms,noBeds

            System.out.println(parts[2].replaceAll("\",""));

            int id = Integer.parseInt(parts[0]);
            String category = parts[1].replaceAll("\","");
            String name = parts[2].replaceAll("\","");
            String owner = parts[3].replaceAll("\","");
            String contact = parts[4].replaceAll("\","");
            String address = parts[5].replaceAll("\","");
            String city = parts[6].replaceAll("\","");
            String cityCode = parts[7].replaceAll("\","");
            String state = parts[8].replaceAll("\","");
            int noRooms = Integer.parseInt(parts[9].replaceAll("\",""));
            int noBeds = Integer.parseInt(parts[10].replaceAll("\",""));

            Hotel temp = new Hotel(id,category,name,owner,contact,address,city,cityCode,phone,noRooms,noBeds);
            ALL_HOTELS.add(temp);


                    System.out.println("Only hotel name: " + parts[2]);
        }

        ALL_HOTELS.forEach(System.out.println();
    }
}*/
