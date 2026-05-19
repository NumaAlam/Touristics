package hotels;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Entity
@Table(name = "hotels")
@Data
@AllArgsConstructor

public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "name")
    private String name;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "address")
    private String address;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "cityCode", nullable = false)
    private String cityCode;

    @Column(name = "phone", nullable = false)
    private String phone;

    @Column(name = "noRooms", nullable = false)
    private int noRooms;

    @Column(name = "noBeds", nullable = false)
    private int noBeds;

    public Hotel() {
    }


    public String toCSV() {
        return id + "," + category + "," + name + "," + owner + "," + contact + "," +
                address + "," + city + "," + cityCode + "," + phone + "," + noRooms + "," + noBeds;
    }

    @Override
    public String toString() {
        return name;
    }

}
