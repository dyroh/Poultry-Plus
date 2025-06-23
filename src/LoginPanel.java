import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private AppController mainFrame;

    public LoginPanel(AppController mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(userLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(passLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(loginButton, gbc);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Authenticate using mainFrame's method
            if (mainFrame.authenticate(username, password)) {
                mainFrame.getCardLayout().show(mainFrame.getCardPanel(), "Home");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid login credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                logLoginAttempt(username, "failed");
            }
        });
    }

    // Log login attempts to a file
    private void logLoginAttempt(String username, String status) {
        String logMessage = username + " attempted login and " + status + " on " + new java.util.Date();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("login_attempts.log", true))) {
            writer.write(logMessage);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

    // Reset login form fields
    public void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
    }
}
