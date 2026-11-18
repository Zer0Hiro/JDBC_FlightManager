Create a Java program named FlightViewer.java that connects to the MySQL database FlightDB using a secure JDBC connection.
1.
Use Scanner to prompt the user for the MySQL root password.
2.
Prompt the user to enter a target terminal number: 2, 3, 4, or 5.
3.
Use PreparedStatement with ? placeholders to prevent SQL injection.
4.
Execute an INNER JOIN query between Arrivals and FlightRoutes using Arrivals.flight_number = FlightRoutes.flight_number.
5.
Filter the results with Arrivals.terminal = ?, because terminal is a VARCHAR(10).
6.
Sort the results with ORDER BY Arrivals.scheduled ASC.
7.
Bind the terminal parameter using pstmt.setString(1, terminal).
8.
Print the returned flight number, departure city, scheduled time, delay status, and terminal in a clear formatted output.
Include the JDBC driver, MySQL connection URL, resource closing, and appropriate exception handling. Do not concatenate the user's terminal input directly into the SQL query.