/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eventmanagement;

/**
 *
 * @author asus
 */


import DatabaseConnection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;


public class OrganizerPanel extends JFrame {
    private JLabel welcomeLabel;
    private JButton addEventButton, viewEventsButton, logoutButton;

    public OrganizerPanel() {
        setTitle("Organizer Dashboard");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // العناصر
        JPanel welcome = new JPanel(new FlowLayout());
        welcomeLabel = new JLabel("Welcome !");
        welcomeLabel.setFont(new Font("Times new Roman", Font.BOLD, 16));
        welcome.add(welcomeLabel);
        
        addEventButton = new JButton("Add New Event");
        viewEventsButton = new JButton("View My Events");
        logoutButton = new JButton("Logout");

        // لوحة الأزرار
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.add(addEventButton);
        buttonPanel.add(viewEventsButton);
        buttonPanel.add(logoutButton);

        // الإطار الرئيسي
        setLayout(new BorderLayout(10, 10));
        add(welcome, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        // الحدث: زر "إضافة فعالية"
        addEventButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               new AddEventPage();
            }
        });

        // الحدث: زر "عرض الفعاليات"
        viewEventsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               new ViewEventsPage ();
            }
        });

        // الحدث: زر "تسجيل الخروج"
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage();
                
            }
        });

        setVisible(true);
    }

    
     public class AddEventPage extends JFrame{
         private JTextField nameField, dateField, timeField, locationField, seatsField;
         private JButton addButton , backButton ;

         public AddEventPage() {
             setTitle("Add New Event");
             setSize(400, 350);
             setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
             setLocationRelativeTo(null);

             // إنشاء الحقول
             nameField = new JTextField(15);
             dateField = new JTextField(15); // مثال: 2025-05-15
             timeField = new JTextField(15); // مثال: 14:00:00
             locationField = new JTextField(15);
             seatsField = new JTextField(15);
             addButton = new JButton("add Event");
              backButton = new JButton("Back");
              
             JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
             panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

             panel.add(new JLabel("Event Name:"));
             panel.add(nameField);
             panel.add(new JLabel("Event Date (YYYY-MM-DD):"));
             panel.add(dateField);
             panel.add(new JLabel("Event Time (HH:MM:SS):"));
             panel.add(timeField);
             panel.add(new JLabel("Event Location:"));
             panel.add(locationField);
             panel.add(new JLabel("Available Seats:"));
             panel.add(seatsField);
             panel.add(backButton);
             panel.add(addButton);

             add(panel);

             // زر الإضافة
             addButton.addActionListener(e -> {
                 String name = nameField.getText();
                 String date = dateField.getText();
                 String time = timeField.getText();
                 String location = locationField.getText();
                 String seatsStr = seatsField.getText();

                 if (name.isEmpty() || date.isEmpty() || time.isEmpty() || location.isEmpty() || seatsStr.isEmpty()) {
                     JOptionPane.showMessageDialog(this, "Please Fill All Fields.", "Error", JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                 try {
                     int seats = Integer.parseInt(seatsStr);

                     try (Connection conn = DatabaseConnection.connect()) {

                         String sql = "INSERT INTO Events (name, date, time, location, available_seats) VALUES (?, ?, ?, ?, ?)";
                         PreparedStatement stmt = conn.prepareStatement(sql);
                         stmt.setString(1, name);
                         stmt.setString(2, date);
                         stmt.setString(3, time);
                         stmt.setString(4, location);
                         stmt.setInt(5, seats);

                         int result = stmt.executeUpdate();
                         if (result > 0) {
                             JOptionPane.showMessageDialog(this, "Event Has Been Added Succesfully!");
                             dispose(); 
                         }

                     } catch (SQLException ex) {
                         ex.printStackTrace();
                         JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                     }

                 } catch (NumberFormatException ex) {
                     JOptionPane.showMessageDialog(this, "Seats must be a number!", "ERROR", JOptionPane.ERROR_MESSAGE);
                 }
             });
                
             // زر الرجوع
             backButton.addActionListener(e -> {
                 dispose();
             });

             setVisible(true);
         }

         
 }
     
     public class ViewEventsPage extends JFrame {
         
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private JButton backButton;

    public ViewEventsPage() {
        setTitle("My Events");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // أسماء الأعمدة
        String[] columnNames = {"Event Name", "Date", "Location", "Seats"};
        tableModel = new DefaultTableModel(columnNames, 0);
        eventsTable = new JTable(tableModel);
        
         
            // إضافة زر الرجوع
            backButton = new JButton("Back");
            backButton.addActionListener(e -> {
                dispose(); // إغلاق هذه النافذة والعودة للنافذة السابقة
            });
            
             JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(backButton);

        // تعبئة البيانات من قاعدة البيانات
        loadEventData();

        add(new JScrollPane(eventsTable), BorderLayout.CENTER);
         
            add(buttonPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadEventData() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT name, date, location, available_seats FROM events";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String date = rs.getString("date");
                String location = rs.getString("location");
                int seats = rs.getInt("available_seats");

                Object[] row = {name, date, location, seats};
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading events: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

   
}
} 
