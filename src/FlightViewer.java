import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;

/**
 * Displays the arrivals for a single terminal by joining {@code Arrivals} and {@code FlightRoutes}
 * in {@code FlightDB}. The terminal is bound through a {@link PreparedStatement} placeholder and is
 * never concatenated into the SQL text.
 */
public class FlightViewer {

    /** MySQL Connector/J driver class (bundled in lib/mysql-connector-j-9.5.0.jar). */
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /** JDBC URL for FlightDB; uses TLS when the server offers it. */
    private static final String URL =
            "jdbc:mysql://localhost:3306/FlightDB?sslMode=PREFERRED&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem";

    private static final String USER = "root";

    private static final Set<String> VALID_TERMINALS = Set.of("2", "3", "4", "5");

    private static final String SQL =
            "SELECT Arrivals.flight_number, FlightRoutes.from_city, Arrivals.scheduled, " +
            "Arrivals.delay_status, Arrivals.terminal " +
            "FROM Arrivals " +
            "INNER JOIN FlightRoutes ON Arrivals.flight_number = FlightRoutes.flight_number " +
            "WHERE Arrivals.terminal = ? " +
            "ORDER BY Arrivals.scheduled ASC";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Please enter your MySQL root password: ");
            String password = scanner.nextLine();

            String terminal = promptTerminal(scanner);

            Class.forName(DRIVER);

            try (Connection conn = DriverManager.getConnection(URL, USER, password);
                 PreparedStatement pstmt = conn.prepareStatement(SQL)) {

                pstmt.setString(1, terminal);

                try (ResultSet rs = pstmt.executeQuery()) {
                    printBoard(rs, terminal);
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC driver not found. Add lib/mysql-connector-j-9.5.0.jar to the classpath.");
        } catch (SQLException e) {
            System.err.println("Database error!");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
        }
    }

    /** Re-prompts until the user enters one of the valid terminals (2, 3, 4, or 5). */
    private static String promptTerminal(Scanner scanner) {
        while (true) {
            System.out.print("Enter target terminal (2, 3, 4, or 5): ");
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("No terminal entered.");
            }
            String input = scanner.nextLine().trim();
            if (VALID_TERMINALS.contains(input)) {
                return input;
            }
            System.out.println("Invalid terminal. Please enter 2, 3, 4, or 5.");
        }
    }

    private static void printBoard(ResultSet rs, String terminal) throws SQLException {
        String line = "-".repeat(78);
        System.out.println("\n --- Arrivals Board: Terminal " + terminal + " ---");
        System.out.printf("%-10s | %-18s | %-10s | %-20s | %-8s%n",
                "Flight", "From", "Scheduled", "Status", "Terminal");
        System.out.println(line);

        int rowCount = 0;
        while (rs.next()) {
            System.out.printf("%-10s | %-18s | %-10s | %-20s | %-8s%n",
                    rs.getString("flight_number"),
                    rs.getString("from_city"),
                    rs.getString("scheduled"),
                    rs.getString("delay_status"),
                    rs.getString("terminal"));
            rowCount++;
        }

        System.out.println(line);
        System.out.println(rowCount == 0
                ? "No flights found for terminal " + terminal + "."
                : "Total flights displayed: " + rowCount);
    }
}
