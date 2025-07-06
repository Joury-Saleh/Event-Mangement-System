/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package DatabaseConnection;

/**
 *
 * @author asus
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    public static Connection connect() {
        Connection conn = null;
        try {
            // Configure these settings in DatabaseConnection.java:
            String url = "jdbc:mysql://[YOUR_HOST]:[YOUR_PORT]/[YOUR_DATABASE_NAME]";
            String user = "[YOUR_USERNAME]";
            String password = "[YOUR_PASSWORD]";
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
        return conn;
    }
}


