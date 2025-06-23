import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateChicksPanel extends JPanel {
    private JTextField chicksField, reasonField;
    private Main mainFrame;
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "your_database_password";

    public UpdateChicksPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        setBackground(new Color(54, 57, 63)); // Dark grey

        JLabel chickLabel = new JLabel("New Chick Count:");
        chickLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(chickLabel, gbc);

        chicksField = new JTextField(15);
        gbc.gridx = 1;
        add(chicksField, gbc);

        JLabel reasonLabel = new JLabel("Reason:");
        reasonLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(reasonLabel, gbc);

        reasonField = new JTextField(15);
        gbc.gridx = 1;
        add(reasonField, gbc);

        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(18, 140, 73));
        updateButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(updateButton, gbc);

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String chickCount = chicksField.getText();
                String reason = reasonField.getText();

                if (updateChickCount(chickCount, reason)) {
                    JOptionPane.showMessageDialog(UpdateChicksPanel.this, "Chick count updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    mainFrame.showScreen("Home");
                }
            }
        });
    }

    private boolean updateChickCount(String chickCount, String reason) {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "UPDATE chicks SET count = ?, reason = ? WHERE id = 1";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, chickCount);
            statement.setString(2, reason);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
