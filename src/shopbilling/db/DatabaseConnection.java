package shopbilling.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    public static final String DB_URL = "jdbc:mysql://localhost:3306/shop_billing_db?useSSL=false&serverTimezone=Asia/Kolkata";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "Vinayrajput@7100"; // Set your local MySQL password here.

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }
}
