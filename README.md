# Event Management System

A comprehensive Java-based event management system with multi-role dashboards and email notification capabilities.

## Features

- **Multi-Role Dashboard System**
  - Admin Dashboard: Complete system management and oversight
  - Organizer Dashboard: Event creation and management
  - User Dashboard: Event browsing and registration

- **Email Notification System**
  - Automated confirmation emails upon event registration
  - Ticket code generation and delivery

- **User Management**
  - Role-based access control (Admin, Organizer, Attendee)
  - Secure user authentication and registration

- **Event Management**
  - Event creation, editing, and deletion
  - Seat availability tracking
  - Registration management with unique ticket codes

## Prerequisites

Before running the application, ensure you have the following installed:

- **Java Development Kit (JDK)** 8 or higher
- **MySQL Database Server**
- **MySQL Connector/J** (JDBC Driver)
- **Java Mail API Library** (JavaMail)
- **NetBeans IDE** (recommended)

## Required Dependencies

### Java Mail Library Setup

⚠️ **IMPORTANT**: You must download and configure the Java Mail library for email functionality.

**Required Libraries:**
```java
// Email sending core libraries
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
```

**Download Instructions:**
1. Download JavaMail API from Oracle's official website
2. Add the following JAR files to your project classpath:
   - `mail.jar`
   - `activation.jar`
3. If using an IDE, add these libraries to your project dependencies

## Database Setup

### MySQL Database Configuration

1. **Create Database:**
   ```sql
   CREATE DATABASE event_management;
   USE event_management;
   ```

2. **Create Required Tables:**

   **Users Table:**
   ```sql
   CREATE TABLE users (
       user_id INT PRIMARY KEY AUTO_INCREMENT,
       username VARCHAR(50) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       email VARCHAR(100) UNIQUE NOT NULL,
       first_name VARCHAR(100) NOT NULL,
       last_name VARCHAR(100) NOT NULL,
       role ENUM('admin', 'organizer', 'attendee') NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   ```

   **Events Table:**
   ```sql
   CREATE TABLE Events (
       event_id INT AUTO_INCREMENT PRIMARY KEY,
       name VARCHAR(100) NOT NULL,
       date DATE NOT NULL,
       time TIME NOT NULL,
       location VARCHAR(100) NOT NULL,
       available_seats INT NOT NULL,
       description TEXT
   );
   ```

   **Registrations Table:**
   ```sql
   CREATE TABLE registrations (
       registration_id INT AUTO_INCREMENT PRIMARY KEY,
       event_id INT NOT NULL,
       user_id INT,
       attendee_name VARCHAR(100) NOT NULL,
       attendee_email VARCHAR(100) NOT NULL,
       ticket_code VARCHAR(20) NOT NULL UNIQUE, 
       registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (event_id) REFERENCES events(event_id),
       FOREIGN KEY (user_id) REFERENCES users(user_id)
   );
   ```

3. **Update Database Connection:**
   The system uses a dedicated `DatabaseConnection` class located in the `DatabaseConnection` package. Update the connection parameters as needed:
   
## Installation & Setup

1. **Clone/Download the Project**
 
2. **Database Setup**
   - Install MySQL and create the database
   - Run the SQL scripts provided above
   - Update database connection settings in your configuration

3. **Install Java Mail Library**
   - Download JavaMail API
   - Add JAR files to your project classpath
   - Configure email settings (SMTP server details)

4. **Compile and Run**


## Email Configuration

The email functionality is implemented in the `sendConfirmationEmail` function located in:
- **File:** `EventList.java`
- **Line:** 496

To configure email settings:
1. Update SMTP server details in your configuration
2. Set up email credentials for sending notifications
3. Test the email functionality after setup

## User Roles

### Admin
- Full system access and management
- User management and role assignment
- System-wide event oversight
- Analytics and reporting

### Organizer
- Event creation and management
- Registration tracking
- Attendee communication
- Event analytics

### Attendee/User
- Browse available events
- Register for events
- Receive confirmation emails
- Manage personal registrations

## Usage

1. **Start the Application**
   - Launch the main application
   - The system will prompt for database connection

2. **Login/Register**
   - Create an account or login with existing credentials
   - System will redirect based on user role

3. **Dashboard Navigation**
   - Use role-specific dashboard features
   - Access events, registrations, and management tools

### Database Connection

The system uses a dedicated `DatabaseConnection` class for MySQL connectivity:

**Location:** `DatabaseConnection/DatabaseConnection.java`

**Default Configuration:**
```java
// Configure these settings in DatabaseConnection.java:
String url = "jdbc:mysql://[YOUR_HOST]:[YOUR_PORT]/[YOUR_DATABASE_NAME]";
String user = "[YOUR_USERNAME]";
String password = "[YOUR_PASSWORD]";
```

**To modify connection settings:**
1. Navigate to `DatabaseConnection/DatabaseConnection.java`
2. Update the connection parameters in the `connect()` method
3. Ensure your MySQL server is running on the specified host and port
4. Verify the database name matches your created database

**Required MySQL Connector:**
- Download MySQL Connector/J (JDBC driver)
- Add `mysql-connector-java-x.x.x.jar` to your project classpath
- In NetBeans: Right-click project → Properties → Libraries → Add JAR/Folder
- Automatic email sending upon event registration
- Unique ticket code generation
- Confirmation email with event details
- Integration with JavaMail API

### Database Relationships
- Users can have multiple registrations
- Events can have multiple registrations
- Foreign key constraints maintain data integrity

## Troubleshooting

### Common Issues

1. **Email Not Sending**
   - Verify JavaMail library is properly installed
   - Check SMTP server configuration
   - Ensure email credentials are correct

2. **Database Connection Error**
   - Verify MySQL server is running
   - Check database connection parameters
   - Ensure database and tables exist

3. **Role Access Issues**
   - Verify user roles are correctly set in database
   - Check role-based access control implementation

## Support

For technical support or questions about the system:
1. Check the troubleshooting section above
2. Verify all prerequisites are met
3. Ensure database is properly configured
4. Confirm JavaMail library is correctly installed

## License

This project is for educational use. You’re welcome to modify or expand it.
---

**Note:** This system requires proper configuration of both MySQL database and JavaMail library for full functionality. Please ensure all dependencies are installed before running the application.
