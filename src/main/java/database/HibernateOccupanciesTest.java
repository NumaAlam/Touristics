package database;

import occupancies.Occupancy;
import org.hibernate.Session;

import java.util.List;

public class HibernateOccupanciesTest {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {


            List<Occupancy> occupancies = session
                    .createQuery("from Occupancy", Occupancy.class)
                    .setMaxResults(5)
                    .list();

            occupancies.forEach(System.out::println);
        }

        HibernateUtil.getSessionFactory().close();
    }
}
