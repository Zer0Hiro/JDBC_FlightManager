import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Updates the delay status for a flight using a parameterized SQL statement.
 *
 * <p>The previous implementation built the SQL command by concatenating
 * {@code userInput} into the query text. Because that value came directly from
 * the user, an attacker could enter SQL syntax, such as a quote followed by
 * additional SQL, and change the meaning of the command. This is a SQL
 * injection vulnerability: input intended to be data could instead be
 * interpreted as executable SQL.
 *
 * <p>{@link PreparedStatement} prevents this vulnerability by keeping the SQL
 * structure and the user-supplied value separate. The {@code ?} placeholder is
 * parsed as a parameter marker, and {@link PreparedStatement#setString(int,
 * String)} sends the value as data that is escaped and handled according to
 * the JDBC driver. Consequently, quotes and SQL keywords in the input cannot
 * alter the {@code UPDATE} statement. The try-with-resources block also closes
 * the connection and prepared statement reliably.
 */
public class SimpleFlightUpdater {
    /**
     * Prevents instantiation of this utility class.
     */
    private SimpleFlightUpdater() {
    }

    /**
     * Reads the new status and updates flight {@code LX354} securely.
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

        try (Connection connection = DriverManager.getConnection(url, "root", password);
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE Arrivals SET delay_status = ? WHERE flight_number = 'LX354'")) {
            statement.setString(1, userInput);
            int affectedRows = statement.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);
        }
    }
}
