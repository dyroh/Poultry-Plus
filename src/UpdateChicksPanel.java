import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateChicksPanel extends JPanel {
    private JTextField newTotalField;
    private JTextField reasonField;
    private AppController mainFrame; // Reference to AppController for switching screens

    public UpdateChicksPanel(AppController mainFrame) {
        this.mainFrame = mainFrame;

        // Layout and styling for UpdateChicksPanel
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Label and input field for new total chicks
        JLabel newTotalLabel = new JLabel("New Total:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(newTotalLabel, gbc);

        newTotalField = new JTextField(10);
        gbc.gridx = 1;
        add(newTotalField, gbc);

        // Label and input field for update reason
        JLabel reasonLabel = new JLabel("Reason:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(reasonLabel, gbc);

        reasonField = new JTextField(10);
        gbc.gridx = 1;
        add(reasonField, gbc);

        // Update button
        JButton updateButton = new JButton("Update");
        gbc.gridx = 1;
        gbc.gridy = 2;

        // Set button styles
        updateButton.setBackground(new Color(18, 140, 73)); // WhatsApp green
        updateButton.setForeground(Color.WHITE); // White text
        updateButton.setOpaque(true); // Ensure background is painted
        updateButton.setBorderPainted(false); // Remove border painting for a flat design

        add(updateButton, gbc);

        // Action listener to handle the update process
        updateButton.addActionListener(e -> updateChicks());
    }

    // Method to handle updating chicks in the database
    private void updateChicks() {
        String total = newTotalField.getText();
        String reason = reasonField.getText();

        // Validate input fields before updating the database
        if (validateFields(total, reason)) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO chicks (total_chicks, update_reason) VALUES (?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setInt(1, Integer.parseInt(total));
                    ps.setString(2, reason);
                    ps.executeUpdate();

                    // Show success message and return to home screen
                    JOptionPane.showMessageDialog(this, "Chick count updated successfully!");
                    mainFrame.getCardLayout().show(mainFrame.getCardPanel(), "Home");

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating chicks in the database.",
                            "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database connection error.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Method to validate input fields
    private boolean validateFields(String total, String reason) {
        if (total.isEmpty() || reason.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both the total chicks and a reason.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            Integer.parseInt(total); // Check if total is a valid integer
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the total chicks.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
