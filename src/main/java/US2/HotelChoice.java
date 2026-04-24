package US2;

import lombok.Getter;

@Getter

public class HotelChoice {
    private final int id;
    private final String name;

    public HotelChoice(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
