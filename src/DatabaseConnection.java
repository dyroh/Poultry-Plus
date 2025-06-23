import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database credentials
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";  // Database URL
    private static final String USERNAME = "root"; // MySQL username
    private static final String PASSWORD = "Tanya@03"; // MySQL password

    private static Connection connection = null;

    // Method to establish a connection to the MySQL database
    public static Connection getConnection() throws SQLException {
        // Check if the connection is already open
        if (connection != null && !connection.isClosed()) {
            return connection;
        }

        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a connection using the URL, username, and password
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            //System.out.println("Connected to the database successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Include it in your library path!");
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found!", e);
        } catch (SQLException e) {
            System.err.println("Connection to the database failed. Check the URL, username, and password.");
            e.printStackTrace();
            throw e;
        }

        return connection;
    }

    // Method to close the connection safely
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed successfully.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Failed to close the connection.");
            }
        }
    }
}