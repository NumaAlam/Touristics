package occupancies;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class Occupancy {

    private final int id;
    private final int rooms;
    private final int usedRooms;
    private final int beds;
    private final int usedBeds;
    private final int year;
    private final int month;
}
