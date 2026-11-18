import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Updates the delay status for a flight by concatenating user input into SQL.
 *
 * <p>This is the insecure counterpart of {@link SimpleFlightUpdater}. Because
 * {@code userInput} is inserted directly into the query text, input containing
 * SQL syntax (for example a quote followed by additional SQL) changes the
 * meaning of the command. This class is vulnerable to SQL injection and exists
 * only for comparison.
 */
public class SimpleFlightUpdaterUnsafe {
    /**
     * Prevents instantiation of this utility class.
     */
    private SimpleFlightUpdaterUnsafe() {
    }

    /**
     * Reads the new status and updates flight {@code LX354} insecurely.
     *
     * @param args command-line arguments, which are not used
     * @throws SQLException if the database connection or update fails
     */
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter MySQL root password: ");
        String password = scanner.nextLine();
        System.out.print("Enter the new delay status for flight LX354: ");
        String userInput = scanner.nextLine();

        String sql = "UPDATE Arrivals SET delay_status = '" + userInput
                + "' WHERE flight_number = 'LX354'";

        try (Connection connection = DriverManager.getConnection(url, "root", password);
             Statement statement = connection.createStatement()) {
            int affectedRows = statement.executeUpdate(sql);
            System.out.println("Affected rows: " + affectedRows);
        }
    }
}
