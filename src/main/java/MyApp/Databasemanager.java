package MyApp;

import java.sql.*;

public class Databasemanager {
    static void main() {

        try {
            Connection dbsconnection = DriverManager.getConnection(
                    "jdbc:sqlserver://185.119.119.126:1433;databaseName=Devparture;encrypt=true;trustServerCertificate=true;",
                    "dev",
                    "dev"
            );

            Statement statement = dbsconnection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM hotels");

            while(resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("id") + ", Name: " + resultSet.getString("name"));
            }
        } catch (SQLException e) {
        System.err.println("Database error: " + e.getMessage());
    }


    }
}
