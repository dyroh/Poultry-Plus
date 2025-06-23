import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ForgotPasswordPanel extends JPanel {
    private JTextField emailField;

    public ForgotPasswordPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        setBackground(new Color(54, 57, 63)); // Dark grey background

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(emailLabel, gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        add(emailField, gbc);

        JButton submitButton = createStyledButton("Submit");
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                // Add forgot password logic here, show hint
                JOptionPane.showMessageDialog(ForgotPasswordPanel.this, "Password hint: Some hint here", "Password Hint", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(18, 140, 73)); // Green background
        button.setForeground(Color.GREEN);
        button.setPreferredSize(new Dimension(150, 40));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        return button;
    }
}
