package database;

import org.hibernate.Session;
import users.User;

import java.util.List;

public class HibernateUserTest {
    public static void main(String[] args) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {


            List<User> users = session
                    .createQuery("from User", User.class)
                    .setMaxResults(5)
                    .list();

            users.forEach(System.out::println);
        }

        HibernateUtil.getSessionFactory().close();
    }
}
