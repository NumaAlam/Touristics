package hotels;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor

public class Hotel {
    int id;
    String category;
    String name;
    String owner;
    String contact;
    String address;
    String city;
    String cityCode;
    String phone;
    int noRooms;
    int noBeds;


    public String toCSV() {
        return ""+id+","+category+","+name+","+owner+","+contact+","+address+","+city+","+cityCode+","+phone+","+noRooms+","+noBeds+"";
    }

}
