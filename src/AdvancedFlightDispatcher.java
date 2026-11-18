import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class AdvancedFlightDispatcher {
    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/FlightDB?allowLoadLocalInfile=true&allowPublicKeyRetrieval=true&serverTimezone=Asia/Jerusalem&useSSL=false";
    private static final String DB_USER = "root";

    public static void updateMultipleDelays(Connection conn, List<Integer> arrivalIds, String newStatus)
            throws SQLException {
        String sql = "UPDATE Arrivals SET delay_status = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);

            for (Integer id : arrivalIds) {
                pstmt.setInt(2, id);
                pstmt.addBatch();
            }

            int[] results = pstmt.executeBatch();
            int totalAffectedRows = 0;
            for (int result : results) {
                if (result > 0) {
                    totalAffectedRows += result;
                }
            }

            System.out.println("Batch delay update complete: " + totalAffectedRows
                    + " row(s) affected; requested status=\"" + newStatus + "\".");
        }
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the database password: ");
            String password = scanner.nextLine();

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, password)) {
                updateMultipleDelays(
                        conn,
                        Arrays.asList(1, 3, 5),
                        "Landed - Final");
            } catch (SQLException e) {
                System.err.println("Database operation failed: " + e.getMessage());
            }
        }
    }
}
