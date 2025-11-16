import java.sql.*;

public class TestDB {
    public static void main(String[] args) {
        try {
            // Test 1: Load driver
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ Step 1: JDBC Driver loaded successfully!");
            
            // Test 2: Connect to database
            String url = "jdbc:postgresql://localhost:5432/tryinglocal";
            String user = "postgres";
            String password = "tanmay";
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Step 2: Connected to database successfully!");
            
            // Test 3: Check if table exists
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM feedback");
            if (rs.next()) {
                System.out.println("✅ Step 3: Feedback table exists with " + rs.getInt(1) + " records");
            }
            
            conn.close();
            System.out.println("🎉 All tests passed! JDBC is working correctly.");
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ JDBC Driver not found: " + e.getMessage());
            System.out.println("💡 Make sure postgresql-42.7.8.jar is in your classpath");
        } catch (SQLException e) {
            System.out.println("❌ Database error: " + e.getMessage());
        }
    }
}