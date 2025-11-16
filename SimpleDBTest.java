import java.sql.*;

public class SimpleDBTest {
    public static void main(String[] args) {
        System.out.println("Testing JDBC connection...");
        
        try {
            // Method 1: Try loading driver explicitly
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Driver loaded via Class.forName()");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver not found via Class.forName()");
        }
        
        // Method 2: Try direct connection
        String url = "jdbc:postgresql://localhost:5432/tryinglocal";
        String user = "postgres";
        String password = "tanmay";
        
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to database successfully!");
            conn.close();
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}