package database;

import hotels.Hotel;
import org.hibernate.Session;

import java.util.List;

public class HibernateHotelTest {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<Hotel> hotels = session
                    .createQuery("from Hotel", Hotel.class)
                    .setMaxResults(5)
                    .list();

            hotels.forEach(System.out::println);
        }

        HibernateUtil.getSessionFactory().close();
    }
}
