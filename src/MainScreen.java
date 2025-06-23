import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainScreen extends JPanel {
    private JLabel totalChicksLabel;
    private AppController mainFrame;

    public MainScreen(AppController mainFrame, Connection connection) {
        this.mainFrame = mainFrame;

        setLayout(new GridBagLayout());
        setBackground(mainFrame.isDarkTheme() ? new Color(54, 57, 63) : new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Welcome to Poultry Plus!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(mainFrame.isDarkTheme() ? Color.WHITE : Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        totalChicksLabel = new JLabel("Total number of chicks: Loading...");
        totalChicksLabel.setForeground(mainFrame.isDarkTheme() ? Color.WHITE : Color.BLACK);
        gbc.gridy = 1;
        add(totalChicksLabel, gbc);

        JButton updateButton = new JButton("Update Chicks");
        styleButton(updateButton);
        gbc.gridy = 2;
        add(updateButton, gbc);

        // Button to view chick update history
        JButton viewHistoryButton = new JButton("View Chick Update History");
        styleButton(viewHistoryButton);
        gbc.gridy = 3;
        add(viewHistoryButton, gbc);

        updateButton.addActionListener(e -> mainFrame.getCardLayout().show(mainFrame.getCardPanel(), "UpdateChicks"));
        viewHistoryButton.addActionListener(e -> saveChickUpdateHistoryToFile());

        updateTotalChicks();
    }

    // Update the total chicks displayed by querying the database
    public void updateTotalChicks() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT total_chicks FROM chicks ORDER BY updated_at DESC LIMIT 1");
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int totalChicks = rs.getInt("total_chicks");
                totalChicksLabel.setText("Total number of chicks: " + totalChicks);
            } else {
                totalChicksLabel.setText("Total number of chicks: No Data");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating chick count: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Method to save chick update history to a text file and open it
    private void saveChickUpdateHistoryToFile() {
        String query = "SELECT total_chicks, update_reason, updated_at FROM chicks ORDER BY updated_at DESC";
        File file = new File("chick_update_history.txt");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery();
             FileWriter writer = new FileWriter(file)) {

            while (rs.next()) {
                int totalChicks = rs.getInt("total_chicks");
                String reason = rs.getString("update_reason");
                String updatedAt = rs.getTimestamp("updated_at").toString();
                String logEntry = "Date: " + updatedAt + ", Total Chicks: " + totalChicks + ", Reason: " + reason;
                writer.write(logEntry + "\n");
            }

            JOptionPane.showMessageDialog(this, "Chick update history saved to chick_update_history.txt", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Open the text file after saving
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(this, "File saved but cannot be opened automatically.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing chick update history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Styling button according to the current theme
    private void styleButton(JButton button) {
        button.setBackground(new Color(18, 140, 73));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(0, 128, 0), 2));
        button.setFont(new Font("Arial", Font.BOLD, 14));
    }
}
