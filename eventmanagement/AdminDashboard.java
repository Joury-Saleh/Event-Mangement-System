/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eventmanagement;

/**
 *
 * @author asus
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import DatabaseConnection.DatabaseConnection;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 1 , 0 , 10));

        JPanel title = new JPanel(new FlowLayout());
        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 20));
        title.add(titleLabel);

        JButton showUsersButton = new JButton("Show Users");
        JButton showEventsButton = new JButton("Show Events");
        JButton reportButton = new JButton("View Report Summary");

        add(title);
        add(showUsersButton);
        add(showEventsButton);
        add(reportButton);

        // زر عرض المستخدمين
        showUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserListWindow();
            }
        });

        // زر عرض الفعاليات
        showEventsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EventListWindow();
            }
        });

        // زر عرض التقرير المختصر
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ReportWindow();
            }
        });

        setVisible(true);
    }
    
    public class UserListWindow extends JFrame{
       
        public UserListWindow() {
        setTitle("User List");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"Username", "Email", "Role", "Created At"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT username, email, role, created_at FROM users";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String username = rs.getString("username");
                String email = rs.getString("email");
                String role = rs.getString("role");
                String created = rs.getString("created_at");

                model.addRow(new Object[]{username, email, role, created});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
        }
          
        JButton back = new JButton("Back");
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        add(back, BorderLayout.SOUTH);
        
        back.addActionListener(e->dispose());
        setVisible(true);
    }


    }
    
    public class EventListWindow extends JFrame {
         public EventListWindow() {
        setTitle("Event List");
        setSize(800, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        String[] columnNames = {"Event Name", "Date", "Time", "Location", "Available Seats", "Description"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT name, date, time, location, available_seats, description FROM events";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String date = rs.getString("date");
                String time = rs.getString("time");
                String location = rs.getString("location");
                int seats = rs.getInt("available_seats");
                String description = rs.getString("description");

                model.addRow(new Object[]{name, date, time, location, seats, description});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + e.getMessage());
        }

                  
        JButton back = new JButton("Back");
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        //add(back, BorderLayout.SOUTH);
                       
             JPanel buttonPanel = new JPanel(new FlowLayout());
             JButton addEventButton = new JButton("Add Event");
             JButton editEventButton = new JButton("Edit Selected Event");
             JButton deleteEventButton = new JButton("Delete Selected Event");

             buttonPanel.add(addEventButton);
             buttonPanel.add(editEventButton);
             buttonPanel.add(deleteEventButton);

             add(buttonPanel, BorderLayout.NORTH); 

            //  زر الرجوع
             add(back, BorderLayout.SOUTH);

             addEventButton.addActionListener(e -> {
            new AddEditEventDialog(null, model); // null = حدث جديد
            });
             
             editEventButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
        
                  new AddEditEventDialog(table, model);
                } else {
                JOptionPane.showMessageDialog(this, "Please select an event to edit.");
      }
});
             
             deleteEventButton.addActionListener(e -> {
    int selectedRow = table.getSelectedRow();
    if (selectedRow >= 0) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String eventName = model.getValueAt(selectedRow, 0).toString();
            try (Connection conn = DatabaseConnection.connect()) {
                String sql = "DELETE FROM events WHERE name = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, eventName);
                stmt.executeUpdate();
                model.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Event deleted.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select an event to delete.");
    }
});



        
        back.addActionListener(e->dispose());
        setVisible(true);
    }

    }
    
    class AddEditEventDialog extends JDialog {

        private JTextField nameField, dateField, timeField, locationField, seatsField;
        private JTextArea descriptionArea;
        private JButton saveButton, cancelButton;
        private JTable eventTable;
        private DefaultTableModel tableModel;
        private int editingRow = -1;

        public AddEditEventDialog(JTable table, DefaultTableModel model) {
            this.eventTable = table;
            this.tableModel = model;

            if (table != null && table.getSelectedRow() >= 0) {
                editingRow = table.getSelectedRow();
            }

            setTitle(editingRow >= 0 ? "Edit Event" : "Add Event");
            setModal(true);
            setSize(400, 400);
            setLocationRelativeTo(null);
            setLayout(new GridLayout(7, 2, 5, 5));

            nameField = new JTextField();
            dateField = new JTextField("yyyy-mm-dd");
            timeField = new JTextField("HH:mm:ss");
            locationField = new JTextField();
            seatsField = new JTextField();
            descriptionArea = new JTextArea(3, 20);

            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");

            add(new JLabel("Event Name:"));
            add(nameField);
            add(new JLabel("Date:"));
            add(dateField);
            add(new JLabel("Time:"));
            add(timeField);
            add(new JLabel("Location:"));
            add(locationField);
            add(new JLabel("Available Seats:"));
            add(seatsField);
            add(new JLabel("Description:"));
            add(new JScrollPane(descriptionArea));
            add(saveButton);
            add(cancelButton);

            if (editingRow >= 0) {
                // تعبئة البيانات في حال التعديل
                nameField.setText(table.getValueAt(editingRow, 0).toString());
                dateField.setText(table.getValueAt(editingRow, 1).toString());
                timeField.setText(table.getValueAt(editingRow, 2).toString());
                locationField.setText(table.getValueAt(editingRow, 3).toString());
                seatsField.setText(table.getValueAt(editingRow, 4).toString());
                descriptionArea.setText(table.getValueAt(editingRow, 5).toString());
            }

            cancelButton.addActionListener(e -> dispose());

            saveButton.addActionListener(e -> saveEvent());

            setVisible(true);
        }

        private void saveEvent() {
            String name = nameField.getText().trim();
            String date = dateField.getText().trim();
            String time = timeField.getText().trim();
            String location = locationField.getText().trim();
            String seatsText = seatsField.getText().trim();
            String description = descriptionArea.getText().trim();
            
            if (name.isEmpty() || date.isEmpty() || time.isEmpty() || 
        location.isEmpty() || seatsText.isEmpty() || description.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please fill all fields.", "ERROR", JOptionPane.ERROR_MESSAGE);
        return; }
            
            int seats;
            try {
                seats = Integer.parseInt(seatsField.getText().trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Available seats must be a number.");
                return;
            }
                
            try (Connection conn = DatabaseConnection.connect()) {
                if (editingRow >= 0) {
                    // تعديل
                    String oldName = tableModel.getValueAt(editingRow, 0).toString();
                    String sql = "UPDATE events SET name=?, date=?, time=?, location=?, available_seats=?, description=? WHERE name=?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, date);
                    stmt.setString(3, time);
                    stmt.setString(4, location);
                    stmt.setInt(5, seats);
                    stmt.setString(6, description);
                    stmt.setString(7, oldName);
                    stmt.executeUpdate();

                    // تحديث الجدول
                    tableModel.setValueAt(name, editingRow, 0);
                    tableModel.setValueAt(date, editingRow, 1);
                    tableModel.setValueAt(time, editingRow, 2);
                    tableModel.setValueAt(location, editingRow, 3);
                    tableModel.setValueAt(seats, editingRow, 4);
                    tableModel.setValueAt(description, editingRow, 5);

                    JOptionPane.showMessageDialog(this, "Event updated successfully.");
                } else {
                    // إضافة
                    String sql = "INSERT INTO events (name, date, time, location, available_seats, description) VALUES (?, ?, ?, ?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setString(1, name);
                    stmt.setString(2, date);
                    stmt.setString(3, time);
                    stmt.setString(4, location);
                    stmt.setInt(5, seats);
                    stmt.setString(6, description);
                    stmt.executeUpdate();

                    tableModel.addRow(new Object[]{name, date, time, location, seats, description});
                    JOptionPane.showMessageDialog(this, "Event added successfully.");
                }

                dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    }

    
    public class ReportWindow extends JFrame {
        
        private JLabel userCountLabel;
        private JLabel eventCountLabel;
        private JTable userTable;
        private JTable eventTable;

        public ReportWindow() {
            setTitle("Report Summary");
            setSize(900, 600);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10, 10));

            // العنوان + ملخص
            JPanel topPanel = new JPanel(new GridLayout(2, 1));
            
            //JPanel title = new JPanel(new FlowLayout());
            JLabel titleLabel = new JLabel("Report Summary");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
            //title.add(titleLabel);
            
            JPanel countPanel = new JPanel(new GridLayout(1, 2));
            userCountLabel = new JLabel("Total Users: ", SwingConstants.CENTER);
            eventCountLabel = new JLabel("Total Events: ", SwingConstants.CENTER);

            countPanel.add(userCountLabel);
            countPanel.add(eventCountLabel);

            topPanel.add(titleLabel);
            topPanel.add(countPanel);

            add(topPanel, BorderLayout.NORTH);

            // الجداول
            JPanel tablePanel = new JPanel(new GridLayout(2, 1, 5, 5));

            // جدول المستخدمين
            userTable = new JTable(new DefaultTableModel(
                    new String[]{"Username", "Email", "Role", "Created At"}, 0
            ));
            JScrollPane userScroll = new JScrollPane(userTable);
            userScroll.setBorder(BorderFactory.createTitledBorder("Users"));

            // جدول الفعاليات
            eventTable = new JTable(new DefaultTableModel(
    new String[]{"Event Name", "Date", "Time", "Available Seats", "Registered Count"}, 0
)); 

            JScrollPane eventScroll = new JScrollPane(eventTable);
            eventScroll.setBorder(BorderFactory.createTitledBorder("Events"));

            tablePanel.add(userScroll);
            tablePanel.add(eventScroll);

            add(tablePanel, BorderLayout.CENTER);

            // زر الرجوع
            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> dispose());
            add(backButton, BorderLayout.SOUTH);

            //  عرض البيانات
            loadSummaryData();
            loadUserTable();
            loadEventTable();

            setVisible(true);
        }

        private void loadSummaryData() {
            try (Connection conn = DatabaseConnection.connect()) {
                PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) AS total FROM users");
                ResultSet rs1 = stmt1.executeQuery();
                if (rs1.next()) {
                    userCountLabel.setText("Total Users: " + rs1.getInt("total"));
                }

                PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) AS total FROM events");
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    eventCountLabel.setText("Total Events: " + rs2.getInt("total"));
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading summary: " + e.getMessage());
            }
        }

        private void loadUserTable() {
            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            model.setRowCount(0);

            try (Connection conn = DatabaseConnection.connect()) {
                String sql = "SELECT username, email, role, created_at FROM users";
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getString("created_at")
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage());
            }
        }

        private void loadEventTable() {
    DefaultTableModel model = (DefaultTableModel) eventTable.getModel();
    model.setRowCount(0);

    try (Connection conn = DatabaseConnection.connect()) {
        String sql = """
            SELECT e.name, e.date, e.time, e.available_seats, 
                   COUNT(r.registration_id) AS registered_count
            FROM events e
            LEFT JOIN registrations r ON e.event_id = r.event_id
            GROUP BY e.event_id
        """;
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("name"),
                rs.getString("date"),
                rs.getString("time"),
                rs.getInt("available_seats"),
                rs.getInt("registered_count")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error loading events: " + e.getMessage());
    }
}

    }
}
        
    


