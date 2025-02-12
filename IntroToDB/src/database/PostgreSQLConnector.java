package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLConnector {
    private static String servername = "localhost";
    private static Integer port = 5432;
    private static String database = "introToDb";
    private static String username = "postgres";
    private static String password = "postgres";

    public static Connection openConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(
                String.format("jdbc:postgresql://%s:%d/%s",servername,port,database),
                username,
                password);
    }
}
