package hotels;

import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor

public class Hotel {
    private final int id;
    private final String category;
    private final String name;
    private final String owner;
    private final String contact;
    private final String address;
    private final String city;
    private final String cityCode;
    private final String phone;
    private final int noRooms;
    private final int noBeds;


}
