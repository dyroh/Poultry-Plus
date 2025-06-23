import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class ChangePasswordPanel extends JPanel {
    private JPasswordField oldPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmNewPasswordField;
    private AppController controller;
    private static final String LOG_FILE = "login_attempts.log"; // Log file path

    public ChangePasswordPanel(AppController controller) {
        this.controller = controller;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel oldPasswordLabel = new JLabel("Old Password:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        add(oldPasswordLabel, gbc);

        oldPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(oldPasswordField, gbc);

        JLabel newPasswordLabel = new JLabel("New Password:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(newPasswordLabel, gbc);

        newPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(newPasswordField, gbc);

        JLabel confirmNewPasswordLabel = new JLabel("Confirm New Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(confirmNewPasswordLabel, gbc);

        confirmNewPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(confirmNewPasswordField, gbc);

        JButton submitButton = new JButton("Submit");
        styleButton(submitButton);  // Apply styling
        gbc.gridx = 1;
        gbc.gridy = 3;
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePassword();
            }
        });
    }

    // Method to handle the password change process
    private void changePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmNewPassword = new String(confirmNewPasswordField.getPassword());

        if (validateFields(oldPassword, newPassword, confirmNewPassword)) {
            if (authenticateOldPassword(oldPassword)) {
                if (isValidPassword(newPassword)) {
                    if (newPassword.equals(confirmNewPassword)) {
                        updatePasswordInDatabase(newPassword);
                        JOptionPane.showMessageDialog(this, "Password changed successfully!");
                        logAction("Password changed successfully for Admin");
                        SwingUtilities.getWindowAncestor(this).dispose(); // Close the change password window
                    } else {
                        JOptionPane.showMessageDialog(this, "New passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long and contain both letters and numbers.", "Invalid Password", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Old password is incorrect.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Validate input fields
    private boolean validateFields(String oldPassword, String newPassword, String confirmNewPassword) {
        if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // Authenticate the old password
    private boolean authenticateOldPassword(String oldPassword) {
        // Assuming username is "Admin"
        String username = "Admin";
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT password FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return oldPassword.equals(storedPassword);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error authenticating old password: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Validates the new password for strength (at least 6 characters, both letters and numbers)
    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$");
    }

    // Method to update the password in the database (JDBC implementation)
    private void updatePasswordInDatabase(String newPassword) {
        String query = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, "Admin"); // Assuming admin is the current logged-in user
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating password in the database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            logAction("Error updating password: " + e.getMessage());
        }
    }

    // Log the actions into a log file
    private void logAction(String action) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(LOG_FILE, true)))) {
            out.println(action + " - " + new java.util.Date());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error logging action: " + e.getMessage());
        }
    }

    // Button styling method (matching the WhatsApp theme)
    private void styleButton(JButton button) {
        button.setBackground(new Color(18, 140, 73)); // WhatsApp green background
        button.setForeground(Color.WHITE); // White text
        button.setOpaque(true); // Make sure background is painted
        button.setBorderPainted(false); // No border for a flat look
        button.setFont(new Font("Arial", Font.BOLD, 14)); // Font styling
        button.setFocusPainted(false); // No focus paint
    }
}
