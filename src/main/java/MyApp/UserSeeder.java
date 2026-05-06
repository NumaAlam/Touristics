package MyApp;

import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.sql.*;

public class UserSeeder {
    /*
     * ================================================
     *   WARNING: DO NOT RUN THIS CLASS EVER AGAIN!
     *   Database has already been seeded.
     *   Running this will create duplicate users!
     * ================================================
     */
    public static void main(String[] args) {
        try {
            Connection dbsconnection = DriverManager.getConnection(
                    "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                    "dev",
                    "dev"
            );
            Statement statement = dbsconnection.createStatement();
            // get the hotel ids
            ResultSet resultSet = statement.executeQuery("SELECT id FROM hotels");
            // prepare insert to create hotel representatives
            PreparedStatement ps = dbsconnection.prepareStatement("INSERT INTO users (username, password_hash, hotelID, role) VALUES (?, ?, ?, ?);");

            // add the hotel representatives with user = User + hotel id and password = Password + hotel id
            while(resultSet.next()) {
                ps.setString(1, "User" + resultSet.getInt("id"));
                // BCrypt.hashpw() hashes the password with a randomly generated salt before storing it.
                // This means even if the database is compromised, the actual passwords cannot be read.
                ps.setString(2, BCrypt.hashpw("Password" + resultSet.getInt("id"), BCrypt.gensalt()));
                ps.setInt(3, resultSet.getInt("id"));
                ps.setString(4, "Hotel Representative");
                ps.executeUpdate();
            }

            // add the HEAD
            ps.setString(1, "HeadUser");
            ps.setString(2, BCrypt.hashpw("HeadUserPassword", BCrypt.gensalt()));
            ps.setNull(3, Types.INTEGER);
            ps.setString(4, "Head");
            ps.executeUpdate();

            // add the Senior Admin
            ps.setString(1, "Senior_Admin");
            ps.setString(2, BCrypt.hashpw("Senior_AdminPassword", BCrypt.gensalt()));
            ps.setNull(3, Types.INTEGER);
            ps.setString(4, "Senior_Admin");
            ps.executeUpdate();

            // add the Senior
            ps.setString(1, "Senior");
            ps.setString(2, BCrypt.hashpw("SeniorPassword", BCrypt.gensalt()));
            ps.setNull(3, Types.INTEGER);
            ps.setString(4, "Senior");
            ps.executeUpdate();



        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
}
