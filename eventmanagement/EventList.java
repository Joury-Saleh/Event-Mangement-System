package eventmanagement;

import DatabaseConnection.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

//  مكتبات إرسال البريد الإلكتروني الأساسية
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class EventList extends JFrame {
    private JTable eventsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> dateFilterCombo;
    private JComboBox<String> locationFilterCombo;
    private JComboBox<String> seatsFilterCombo;
    private String username;
    
    public EventList() {
        this(null);
    }
    
    public EventList(String username) {
        this.username = username;
        // تكوين النافذة
        setTitle("Event List");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // تصميم عنوان الصفحة
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("Available Events");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        // تصميم قسم البحث والتصفية
        JPanel filterPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        
        // إضافة حقل البحث
        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel searchLabel = new JLabel("Search Events:");
        searchField = new JTextField(15);
        search.add(searchLabel);
        filterPanel.add(search);
        filterPanel.add(searchField);
        
        // إضافة تصفية حسب التاريخ
        JPanel seats = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel seat = new JLabel("Filter by seats NO.:");
        String[] seatOptions = {"All seats", "Only Available (Seats > 0)", "More than 10", "More than 50"};
        seatsFilterCombo = new JComboBox<>(seatOptions);
        seats.add(seat);
        filterPanel.add(seats);
        filterPanel.add(seatsFilterCombo);
        
        // إضافة تصفية حسب الموقع
        JPanel location = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel locationLabel = new JLabel("Filter by Location:");
        locationFilterCombo = new JComboBox<>();
        locationFilterCombo.addItem("All Locations");
        location.add(locationLabel);
        filterPanel.add(location);
        filterPanel.add(locationFilterCombo);
        
        JLabel empty = new JLabel(" ");
        filterPanel.add(empty);
        
        
        // زر البحث
//        JButton searchButton = new JButton("Search");
//        filterPanel.add(searchButton);
        
        
        // زر إعادة تعيين البحث
        JButton resetButton = new JButton("Reset");
        filterPanel.add(resetButton);
        
        // زر حجز فعالية
        JButton registerButton = new JButton("Register for Event");
        filterPanel.add(registerButton);
        
        //زر الغاء حجز فعالية
        JButton cancelRegistrationButton = new JButton("Cancel Registration");
        filterPanel.add(cancelRegistrationButton);

        // زر الرجوع
        JButton logoutButton = new JButton("Logout");
        filterPanel.add(logoutButton);
        
        
        
        // تكوين الجدول
        String[] columnNames = {"Event ID", "Event Name", "Date", "Time", "Location", "Available Seats", "Description"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; //يخلي الجدول غير قابل للتعديل
            }
        };
        
        eventsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        
        // تنظيم عناصر الواجهة
        setLayout(new BorderLayout(10, 10));
        add(titlePanel, BorderLayout.NORTH);
        add(filterPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
        
        // حمل البيانات من قاعدة البيانات
        loadEventData();
        loadFilterOptions();
        
        // إضافة وظائف أزرار التصفية والبحث
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterEvents();
            }
        });
        
//        searchButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                filterEvents();
//            }
//        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                seatsFilterCombo.setSelectedItem("All seats");
                locationFilterCombo.setSelectedItem("All Locations");
                loadEventData();
            }
        });
        
        seatsFilterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterEvents();
            }
        });
        
        locationFilterCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterEvents();
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerForEvent();
            }
        });
        
        cancelRegistrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelRegistration();
            }
        });

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage();
            }
        });
        
        pack();
        setVisible(true);
    }
    
    // بيانات الفعاليات من قاعدة البيانات
    private void loadEventData() {
        tableModel.setRowCount(0); // حذف البيانات القديمة
        
        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT event_id, name, date, time, location, available_seats, description FROM events";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("event_id"),
                    rs.getString("name"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getString("location"),
                    rs.getInt("available_seats"),
                    rs.getString("description")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading events: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // تحميل خيارات التصفية من قاعدة البيانات
    private void loadFilterOptions() {
        try (Connection conn = DatabaseConnection.connect()) {
                                    
            // تحميل المواقع 
            String locationSql = "SELECT DISTINCT location FROM events ORDER BY location";
            PreparedStatement locationStmt = conn.prepareStatement(locationSql);
            ResultSet locationRs = locationStmt.executeQuery();
            
            while (locationRs.next()) {
                locationFilterCombo.addItem(locationRs.getString("location"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading filter options: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // تصفية الفعاليات حسب البحث والفلاتر
    private void filterEvents() {
        tableModel.setRowCount(0); // حذف البيانات القديمة
        String searchText = searchField.getText().toLowerCase();
        String locationFilter = locationFilterCombo.getSelectedItem().toString();
        String seatsFilter = (String) seatsFilterCombo.getSelectedItem();
        
        try (Connection conn = DatabaseConnection.connect()) {
            StringBuilder sqlBuilder = new StringBuilder("SELECT event_id, name, date, time, location, available_seats, description FROM events WHERE 1=1");
            
            // إضافة شروط التصفية
           
            if (!seatsFilter.equals("All seats")) {
                if(seatsFilter.equals("Only Available (Seats > 0)")) {
                    sqlBuilder.append(" AND available_seats > 0");
                } else if (seatsFilter.equals("More than 10")) {
                    sqlBuilder.append(" AND available_seats > 10");
                } else if (seatsFilter.equals("More than 50")) {
                    sqlBuilder.append(" AND available_seats > 50");
                }
            }
        
            
            if (!locationFilter.equals("All Locations")) {
                sqlBuilder.append(" AND location = '").append(locationFilter).append("'");
            }
            
            if (!searchText.isEmpty()) {
                sqlBuilder.append(" AND (LOWER(name) LIKE '%").append(searchText).append("%'")
                         .append(" OR LOWER(description) LIKE '%").append(searchText).append("%')");
            }
            
            PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("event_id"),
                    rs.getString("name"),
                    rs.getString("date"),
                    rs.getString("time"),
                    rs.getString("location"),
                    rs.getInt("available_seats"),
                    rs.getString("description")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering events: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // وظيفة الحجز للفعالية
    private void registerForEvent() {
        int selectedRow = eventsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int eventId = (int) tableModel.getValueAt(selectedRow, 0);
        String eventName = (String) tableModel.getValueAt(selectedRow, 1);
        int availableSeats = (int) tableModel.getValueAt(selectedRow, 5);
        
        if (availableSeats <= 0) {
            JOptionPane.showMessageDialog(this, "Sorry, this event is fully booked.", "No Seats Available", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        new TicketBooking(eventId, eventName);
    }
    
    //وظيفة الغاء حجز الفعالية
    private void cancelRegistration() {
    int selectedRow = eventsTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select an event first.", "No Selection", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int eventId = (int) tableModel.getValueAt(selectedRow, 0);
    String eventName = (String) tableModel.getValueAt(selectedRow, 1);

    String email = JOptionPane.showInputDialog(this, "Enter your email used during registration:");
    if (email == null) {
        return; 
    }
    
    if (email.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Cancellation aborted: email is required.", "Cancelled", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try (Connection conn = DatabaseConnection.connect()) {
        String selectSql = "SELECT ticket_code, attendee_name FROM registrations WHERE event_id = ? AND attendee_email = ?";
        PreparedStatement selectStmt = conn.prepareStatement(selectSql);
        selectStmt.setInt(1, eventId);
        selectStmt.setString(2, email);
        ResultSet rs = selectStmt.executeQuery();

        if (rs.next()) {
            String ticketCode = rs.getString("ticket_code");
            String attendeeName = rs.getString("attendee_name");

            // حذف التسجيل
            String deleteSql = "DELETE FROM registrations WHERE event_id = ? AND attendee_email = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, eventId);
            deleteStmt.setString(2, email);
            deleteStmt.executeUpdate();

            // استرجاع مقعد
            String updateSql = "UPDATE events SET available_seats = available_seats + 1 WHERE event_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, eventId);
            updateStmt.executeUpdate();

            // إرسال بريد الإلغاء
            sendCancellationEmail(email, eventName, attendeeName, ticketCode);

            JOptionPane.showMessageDialog(this, "Registration cancelled and email notification sent.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadEventData(); // تحديث القائمة
        } else {
            JOptionPane.showMessageDialog(this, "No registration found with that email for this event.", "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error cancelling registration: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    // نافذة للحجز
    private class TicketBooking extends JFrame {
        private int eventId;
        private String eventName;
        private JTextField nameField, emailField;
        
        public TicketBooking(int eventId, String eventName) {
            this.eventId = eventId;
            this.eventName = eventName;
            
            setTitle("Book Ticket: " + eventName);
            setSize(400, 250);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLocationRelativeTo(null);
            
            JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            panel.add(new JLabel("Event Name:"));
            panel.add(new JLabel(eventName));
            
            panel.add(new JLabel("Your Name:"));
            nameField = new JTextField();
            panel.add(nameField);
            
            panel.add(new JLabel("Your Email:"));
            emailField = new JTextField();
            panel.add(emailField);
            
            JButton bookButton = new JButton("Book Now");
            JButton cancelButton = new JButton("Cancel");
            
            panel.add(bookButton);
            panel.add(cancelButton);
            
            add(panel);
            
            bookButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    bookTicket();
                }
            });
            
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            
            setVisible(true);
        }
        
        private void bookTicket() {
            String name = nameField.getText();
            String email = emailField.getText();
            
            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.", "Missing Information", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
                        try (Connection conn = DatabaseConnection.connect()) {
                // التحقق من أن البريد الإلكتروني مسجل بالفعل لهذا الحدث
                String checkEmailQuery = "SELECT date FROM events e "
                        + "JOIN registrations r ON e.event_id = r.event_id "
                        + "WHERE e.event_id = ? AND r.attendee_email = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkEmailQuery);
                checkStmt.setInt(1, eventId);
                checkStmt.setString(2, email);
                ResultSet rs = checkStmt.executeQuery();

                            String eventDate = "";
                            if (rs.next()) {
                                // المستخدم مسجل بالفعل
                                JOptionPane.showMessageDialog(this, "You are already registered for this event with this email.", "Already Registered", JOptionPane.WARNING_MESSAGE);
                                return;
                            } else {
                                // استخراج تاريخ الفعالية من جدول الفعاليات
                                String getDateQuery = "SELECT date FROM events WHERE event_id = ?";
                                PreparedStatement dateStmt = conn.prepareStatement(getDateQuery);
                                dateStmt.setInt(1, eventId);
                                ResultSet dateRs = dateStmt.executeQuery();
                                if (dateRs.next()) {
                                    eventDate = dateRs.getString("date");
                                }
                            }
                
                // تسجيل الحضور
                String ticketCode = "TCK-" + System.currentTimeMillis(); 
                String registerSQL = "INSERT INTO registrations (event_id, attendee_name, attendee_email, ticket_code) VALUES (?, ?, ?, ?)";
                PreparedStatement registerStmt = conn.prepareStatement(registerSQL);
                registerStmt.setInt(1, eventId);
                registerStmt.setString(2, name);
                registerStmt.setString(3, email);
                registerStmt.setString(4, ticketCode);
                registerStmt.executeUpdate();

                // تحديث عدد المقاعد المتاحة
                String updateSQL = "UPDATE events SET available_seats = available_seats - 1 WHERE event_id = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
                updateStmt.setInt(1, eventId);
                updateStmt.executeUpdate();
                
                // محاولة إرسال بريد التأكيد
                try {
                    sendConfirmationEmail(email, eventName, eventDate, name, ticketCode);
                    System.out.println("Confirmation email sent successfully to: " + email);
                } catch (Exception emailEx) {
                    System.err.println("Failed to send confirmation email: " + emailEx.getMessage());
                    // استمرار في العملية حتى لو فشل إرسال البريد
                }

                // إظهار التأكيد
                JOptionPane.showMessageDialog(this, 
                    "Registration successful!\n\n"
                    + "Event: " + eventName + "\n"
                    + "Attendee: " + name + "\n"
                    + "Email: " + email + "\n\n"
                    + "Your e-ticket has been sent to your email address.",
                    "Registration Complete", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                loadEventData(); // الرجوع لجدول الفعاليات
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error registering for event: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
   
  private static void sendConfirmationEmail(String toEmail, String eventName, String eventDate, String attendeeName, String ticketNumber) {
    // بيانات الحساب المرسل
    final String username = "yourEmail@example.com";  // بريد المرسل
    final String password = "your password";  //كلمة السر        

    // إعداد خصائص الاتصال
    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    // إنشاء جلسة بريد إلكتروني
    Session session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    });

    try {
        // إنشاء رسالة البريد
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Event Registration Confirmation: " + eventName);
        
        // إنشاء محتوى الرسالة مع الاسموالتاريخ  ورقم التذكرة
        String emailContent = String.format(
            "Dear %s,\n\n" +
            "Thank you for registering for %s!\n\n" +
            "Event Date: %s\n" +
            "Ticket Number: %s\n\n" +
            "We look forward to seeing you there!\n\n" +
            "Best regards,\nEvent Management Team",
            attendeeName, eventName, eventDate, ticketNumber
        );
        
        message.setText(emailContent);
        
        // إرسال الرسالة
        Transport.send(message);
        
        System.out.println("Confirmation email sent successfully to: " + toEmail);
        
    } catch (MessagingException e) {
        System.err.println("Failed to send email: " + e.getMessage());
        e.printStackTrace();
    }
}
  
  private static void sendCancellationEmail(String toEmail, String eventName, String attendeeName, String ticketNumber) {
    final String username = "programmer.cs.87@gmail.com";
    final String password = "jkhl tukh uaoe ipyj";

    Properties props = new Properties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", "smtp.gmail.com");
    props.put("mail.smtp.port", "587");

    Session session = Session.getInstance(props, new Authenticator() {
        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    });

    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject("Event Registration Cancellation: " + eventName);

        String emailContent = String.format(
            "Dear %s,\n\n" +
            "Your registration for the event \"%s\" has been successfully cancelled.\n\n" +
            "Ticket Number: %s\n\n" +
            "If this was a mistake, please re-register.\n\n" +
            "Regards,\nEvent Management Team",
            attendeeName, eventName, ticketNumber
        );

        message.setText(emailContent);
        Transport.send(message);

    } catch (MessagingException e) {
        System.err.println("Failed to send cancellation email: " + e.getMessage());
        e.printStackTrace();
    }
}

    }

    
