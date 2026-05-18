package occupancies;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class OccupancyPK {
    private int id;
    private int year;
    private int month;
}
