package code;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

//The Login class provides a Graphical User Interface GUI for user authentication.
 //It verifies credentials against a database via the Dao class.
public class Login extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private Dao dao;

    public Login() {
        // Initialize Database Access Object
        dao = new Dao();

        // Configure Frame settings
        setTitle("IT Trouble Ticket Management App - Login");
        setSize(450, 300);
        setLayout(null); // Using absolute positioning for UI components
        setLocationRelativeTo(null); // Centers the window on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Header Label
        JLabel lblTitle = new JLabel("Login");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setBounds(180, 20, 100, 30);
        add(lblTitle);

        // Username Input Field
        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(60, 80, 100, 25);
        add(lblUser);

        txtUsername = new JTextField();
        txtUsername.setBounds(160, 80, 180, 25);
        add(txtUsername);

        // Password Input Field
        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(60, 120, 100, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(160, 120, 180, 25);
        add(txtPassword);

        // Login Button Styling
        btnLogin = new JButton("Login");
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBounds(160, 170, 100, 30);
        add(btnLogin);

        // Event listener for login attempt
        btnLogin.addActionListener(e -> loginUser());
    }

    // Captures user input, validates against the DAO, and opens the Tickets window upon success.
    private void loginUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // Basic validation for empty fields
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.");
            return;
        }

        // Check credentials against database
        if (dao.validateLogin(username, password)) {
            String role = dao.getUserRole(username, password);
            JOptionPane.showMessageDialog(this, "Login successful as " + role + ".");
            
            // Launch the main Ticket management window and close login screen
            new Tickets(username, role).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.");
        }
    }

    public static void main(String[] args) {
        // Ensure GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }
}