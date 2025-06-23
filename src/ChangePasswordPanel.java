import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ChangePasswordPanel extends JPanel {
    private JPasswordField oldPasswordField, newPasswordField;

    public ChangePasswordPanel() {
        setLayout(new GridBagLayout());
        setBackground(new Color(54, 57, 63)); // Dark grey background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel oldPasswordLabel = new JLabel("Old Password:");
        oldPasswordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(oldPasswordLabel, gbc);

        oldPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(oldPasswordField, gbc);

        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(newPasswordLabel, gbc);

        newPasswordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(newPasswordField, gbc);

        JButton changeButton = new JButton("Change Password");
        changeButton.setBackground(new Color(18, 140, 73)); // WhatsApp green
        changeButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(changeButton, gbc);

        changeButton.addActionListener(e -> changePassword());
    }

    private void changePassword() {
        String oldPassword = new String(oldPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());

        String query = "UPDATE users SET password = ? WHERE password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, newPassword);
            ps.setString(2, oldPassword);
            int updatedRows = ps.executeUpdate();
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Old password incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
