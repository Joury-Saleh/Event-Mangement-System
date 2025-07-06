/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package eventmanagement;

/**
 *
 * @author asus
 */
import java.sql.ResultSet;
import DatabaseConnection.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import java.util.*;
import java.util.regex.Pattern;

public class LoginPage extends JFrame {
    
     // Map to store login attempts for each username
    private static Map<String, Integer> loginAttempts = new HashMap<>();
    // Maximum allowed login attempts before locking
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    
    public LoginPage() {
        setTitle("Login Page");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // توسيط النافذة على الشاشة
        
        // إضافة Panel  مع هامش
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome!"); 
        welcomeLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));
        welcomeLabel.setForeground(new Color(0, 102, 204)); // لون أزرق 
        welcomePanel.add(welcomeLabel);
        
        
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 15));
        
      
       
         JPanel usernamePanel = new JPanel(new FlowLayout());

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        JTextField userText = new JTextField("أدخل اسم المستخدم",25);
        //userText.setText("أدخل اسم المستخدم أو الايميل");
        usernamePanel.add(userLabel);
        usernamePanel.add(userText);
        
        JPanel passwordPanel = new JPanel(new FlowLayout());
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        JPasswordField passwordText = new JPasswordField("أدخل كلمة السر", 25);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordText);
        
        
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JCheckBox rememberCheckbox = new JCheckBox("Remember me");
        rememberCheckbox.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        checkboxPanel.add(rememberCheckbox);
        
      
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Sign Up");
        
        
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        signupButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(120, 35));
        signupButton.setPreferredSize(new Dimension(120, 35));
        loginButton.setBackground(new Color(0, 128, 255));
        loginButton.setForeground(Color.WHITE);
        
        buttonsPanel.add(loginButton);
        buttonsPanel.add(signupButton);
        
        
        formPanel.add(usernamePanel);
        formPanel.add(passwordPanel);
        formPanel.add(checkboxPanel);
        formPanel.add(buttonsPanel);
        
        mainPanel.add(welcomePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
       
                    
                

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userText.getText();
                String password = new String(passwordText.getPassword());

                if (username.isEmpty() || password.isEmpty() ||  username.equals("أدخل اسم المستخدم") || 
                    password.equals("أدخل كلمة السر")) {
                    JOptionPane.showMessageDialog(LoginPage.this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                } 
                
                try (Connection conn = DatabaseConnection.connect()) {
            String sql = "SELECT role, full_name FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

           if (rs.next()) {
    // إذا كان تسجيل الدخول ناجحاً، امسح عدد المحاولات الفاشلة (إعادة تعيين العداد)
    loginAttempts.remove(username);
    
    String role = rs.getString("role");
    switch (role) {
        case "admin":
             new AdminDashboard();
             break;
        case "organizer":
            new OrganizerPanel();
            break;
        case "attendee":
            new EventList(username);
            break;
    }
    dispose();
} else {
    // زيادة عدد محاولات تسجيل الدخول الفاشلة للمستخدم
    int attempts = loginAttempts.getOrDefault(username, 0) + 1;
    loginAttempts.put(username, attempts);
    
    if (attempts >= MAX_LOGIN_ATTEMPTS) {
        JOptionPane.showMessageDialog(LoginPage.this,
                "The allowed limit for failed login attempts has been exceeded. Your account has been temporarily locked.",
                "Account Locked", JOptionPane.WARNING_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(LoginPage.this,
                "The username or password is incorrect. Remaining attempts:" + (MAX_LOGIN_ATTEMPTS - attempts),
                "Login Failed", JOptionPane.ERROR_MESSAGE);
    }
}
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(LoginPage.this,
                    "Error: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

        });
        
        
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUpPage();
            }
        });
        
        setVisible(true);
    }
    

    public class SignUpPage extends JFrame {
        public SignUpPage() {
            setTitle("Sign Up Page");
            //setSize(450, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null); // توسيط النافذة على الشاشة
            
           
            JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
            mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
            
          
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel titleLabel = new JLabel("Create New Account");
            titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 28));
            titleLabel.setForeground(new Color(0, 102, 204)); // نفس لون العنوان في صفحة تسجيل الدخول
            titlePanel.add(titleLabel);
            
          
            JPanel formPanel = new JPanel(new GridLayout(7, 1, 0, 15));
            
            
            //last name
            JPanel lastnamePanel = new JPanel(new FlowLayout());
            JLabel lastnameLabel = new JLabel("Last Name:");
            lastnameLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            JTextField lastnemText = new JTextField("الاسم الأخير" ,25);
            lastnamePanel.add(lastnameLabel);
            lastnamePanel.add(lastnemText);
            
            //firt name
            JPanel firstnamePanel = new JPanel(new FlowLayout());
            JLabel firstnameLabel = new JLabel("First Name:");
           firstnameLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            JTextField firstnemText = new JTextField("الاسم الأول" ,25);
            firstnamePanel.add(firstnameLabel);
            firstnamePanel.add(firstnemText);
            
            //user name
            JPanel usernamePanel = new JPanel(new FlowLayout());
            JLabel userLabel = new JLabel("Username:");
            userLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            JTextField userText = new JTextField("اسم المستخدم" ,25);
            usernamePanel.add(userLabel);
            usernamePanel.add(userText);
            
            // email
            JPanel emailPanel = new JPanel(new FlowLayout());
            JLabel emailLabel = new JLabel("Email:");
            emailLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            JTextField emailText = new JTextField("البريد الإلكتروني" ,25);
            emailPanel.add(emailLabel);
            emailPanel.add(emailText);
            
            // password
            JPanel passwordPanel = new JPanel(new FlowLayout());
            JLabel passLabel = new JLabel("Password:");
            passLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            JPasswordField passText = new JPasswordField("كلمة السر" ,25);
            passwordPanel.add(passLabel);
            passwordPanel.add(passText);
            
            // confirmpassword
            JPanel confirmPassPanel = new JPanel(new FlowLayout());
            JLabel confirmPassLabel = new JLabel("Confirm Password:");
            confirmPassLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            JPasswordField confirmPassText = new JPasswordField("تأكيد كلمة السر" ,20);
            confirmPassPanel.add(confirmPassLabel);
            confirmPassPanel.add(confirmPassText);
            
            // Role 
            JPanel rolePanel = new JPanel(new FlowLayout());
            JLabel roleLabel = new JLabel("Select Role:");
            roleLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
            String[] roles = {"organizer", "attendee"};
            JComboBox<String> roleComboBox = new JComboBox<>(roles);
            rolePanel.add(roleLabel);
            rolePanel.add(roleComboBox);
            
            
            formPanel.add(lastnamePanel);
            formPanel.add(firstnamePanel);
            formPanel.add(usernamePanel);
            formPanel.add(emailPanel);
            formPanel.add(passwordPanel);
            formPanel.add(confirmPassPanel);
            formPanel.add(rolePanel);
            
            //craete button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JButton signUpButton = new JButton("Create Account");
            signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
            signUpButton.setPreferredSize(new Dimension(150, 40));
            signUpButton.setBackground(new Color(0, 128, 255));//button background color
            signUpButton.setForeground(Color.WHITE);// button font color
            buttonPanel.add(signUpButton);
            
            //note down the page
            JPanel notePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            JLabel noteLabel = new JLabel("Already have an account? Click Cancel to return to Login");
            noteLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            notePanel.add(noteLabel);
            
           //cancel button
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setPreferredSize(new Dimension(100, 30));
            notePanel.add(cancelButton);
            
            //group buttons with notes
            JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            bottomPanel.add(buttonPanel);
            bottomPanel.add(notePanel);
            
            mainPanel.add(titlePanel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);         
            mainPanel.add(bottomPanel, BorderLayout.SOUTH);
            
          
            setContentPane(mainPanel);
            
         
            signUpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String lastname = lastnemText.getText();
                    String firstname = firstnemText.getText();
                    String username = userText.getText();
                    String email = emailText.getText();
                    String password = new String(passText.getPassword());
                    String confirmPassword = new String(confirmPassText.getPassword());
                    String role = roleComboBox.getSelectedItem().toString();
                    String fullName = firstname + " " + lastname;
                    
                    if (lastname.isEmpty() || firstname.isEmpty() ||username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||  lastname.equals("الاسم الأخير") || firstname.equals("الاسم الأول") ||
                        username.equals("اسم المستخدم") || email.equals("البريد الإلكتروني") ||
                        password.equals("كلمة السر") || confirmPassword.equals("تأكيد كلمة السر")) {
                        JOptionPane.showMessageDialog(SignUpPage.this, "Please complete all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } if (!password.equals(confirmPassword)) {
                        JOptionPane.showMessageDialog(SignUpPage.this, "Password and confirmation do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    } // if (password.length() < 8 || !password.contains("[_#*@]") || !password.contains("\\d")  || !password.contains("[a-zA-Z]")) {
                    if (!isPasswordValid(password)) {
                        JOptionPane.showMessageDialog(SignUpPage.this, "Password must:\n" +
"     * - Be at least 8 characters long\n" +
"     * - Contain at least one letter\n" +
"     * - Contain at least one number\n" +
"     * - Contain at least one special character from (_#*@)", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }  if (!email.contains("@") || !email.contains(".")) {
                        JOptionPane.showMessageDialog(SignUpPage.this, "Invalid email format.");
                        return;
                    } 
                        try (Connection conn = DatabaseConnection.connect()) {
                        String sql = "INSERT INTO users (username, password, email, full_name, role) VALUES (?, ?, ?, ?, ?)";
                        PreparedStatement pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, username);
                        pstmt.setString(2, password);
                        pstmt.setString(3, email);
                        pstmt.setString(4, fullName);
                        pstmt.setString(5, role);
                        pstmt.executeUpdate();

                        JOptionPane.showMessageDialog(SignUpPage.this, "Account created and saved Successly!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                        } catch (SQLException ex) {
                            
                                if (ex.getErrorCode() == 1062) { // MySQL duplicate entry error code
                                String errorMessage = ex.getMessage();

                                if (errorMessage.contains("username")) {
                                    JOptionPane.showMessageDialog(SignUpPage.this,"Username already taken!" , "Error", JOptionPane.ERROR_MESSAGE);
                                } else if (errorMessage.contains("email")) {
                                 JOptionPane.showMessageDialog(SignUpPage.this,"Email already registered!" , "Error", JOptionPane.ERROR_MESSAGE);

                                    
                                }
                            } else {
                                JOptionPane.showMessageDialog(SignUpPage.this, "Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);

                           
                        //JOptionPane.showMessageDialog(SignUpPage.this, "❌ Failed to save to database.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                        
                        
                    }

                    
                }
            });
            
            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });
            pack();
            setVisible(true);
        }
  
    }
           private static boolean isPasswordValid(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        // Check for at least one letter
        boolean hasLetter = Pattern.compile("[a-zA-Z]").matcher(password).find();
        
        // Check for at least one digit
        boolean hasDigit = Pattern.compile("\\d").matcher(password).find();
        
        // Check for at least one special character (_#*@)
        boolean hasSpecial = Pattern.compile("[_#*@]").matcher(password).find();
        
        return hasLetter && hasDigit && hasSpecial;
    }
  
}



