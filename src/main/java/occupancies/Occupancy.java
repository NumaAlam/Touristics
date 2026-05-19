package occupancies;

import hotels.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "occupancies")
@IdClass(OccupancyPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Occupancy {

    @Id
    @ManyToOne
    @JoinColumn(name = "id")
    private Hotel hotel;

    @Id
    @Column(name = "year")
    private int year;

    @Id
    @Column(name = "month")
    private int month;

    @Column(name = "rooms")
    private int rooms;

    @Column(name = "usedRooms")
    private int usedRooms;

    @Column(name = "beds")
    private int beds;

    @Column(name = "usedBeds")
    private int usedBeds;

    public String toCSV() {
        return hotel.getId() + "," + year + "," + month + "," + rooms + "," + usedRooms + "," +
                beds + "," + usedBeds;
    }
}
