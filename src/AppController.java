import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel navigationPanel;
    private JButton homeButton, purchasesButton, salesButton, updateButton, changePasswordButton, signOutButton, themeSwitchButton;
    private boolean isDarkTheme = true;  // Track the current theme

    public Main() {
        setTitle("Poultry Plus Application");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize CardLayout and cardPanel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Initialize all panels, passing the Main instance (this) to each panel that needs it
        JPanel loginPanel = new LoginPanel(this);  // Pass Main instance to LoginPanel
        JPanel homePanel = createHomePanel();
        JPanel purchasesPanel = new PurchasesPanel();
        JPanel salesPanel = new SalesPanel();
        JPanel updateChicksPanel = new UpdateChicksPanel(this); // Pass Main instance to UpdateChicksPanel
        JPanel changePasswordPanel = new ChangePasswordPanel();

        // Add panels to the card layout
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(homePanel, "Home");
        cardPanel.add(purchasesPanel, "Purchases");
        cardPanel.add(salesPanel, "Sales");
        cardPanel.add(updateChicksPanel, "UpdateChicks");
        cardPanel.add(changePasswordPanel, "ChangePassword");

        // Add the cardPanel to the frame
        add(cardPanel, BorderLayout.CENTER);

        // Create navigation panel and add to the frame
        navigationPanel = createNavigationPanel();
        add(navigationPanel, BorderLayout.WEST);

        // Disable navigation until login
        disableNavigationButtons();
        cardLayout.show(cardPanel, "Login");
    }

    // Create the side navigation panel
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        updateTheme(panel);  // Apply theme when creating the panel

        // Initialize buttons with actions to switch panels
        homeButton = createStyledButton("Home", e -> cardLayout.show(cardPanel, "Home"));
        purchasesButton = createStyledButton("Purchases", e -> cardLayout.show(cardPanel, "Purchases"));
        salesButton = createStyledButton("Sales", e -> cardLayout.show(cardPanel, "Sales"));
        updateButton = createStyledButton("Update Chicks", e -> cardLayout.show(cardPanel, "UpdateChicks"));
        changePasswordButton = createStyledButton("Change Password", e -> cardLayout.show(cardPanel, "ChangePassword"));
        signOutButton = createStyledButton("Sign Out", e -> signOut());

        themeSwitchButton = createStyledButton("Toggle Theme", e -> toggleTheme());

        // Add buttons to the navigation panel
        panel.add(homeButton);
        panel.add(purchasesButton);
        panel.add(salesButton);
        panel.add(updateButton);
        panel.add(changePasswordButton);
        panel.add(signOutButton);
        panel.add(themeSwitchButton);

        return panel;
    }

    // Helper method to create styled buttons
    private JButton createStyledButton(String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        updateTheme(button);  // Apply theme when creating the button
        button.setPreferredSize(new Dimension(150, 50));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.addActionListener(action);
        return button;
    }

    // Enable navigation buttons after successful login
    public void enableNavigationButtons() {
        homeButton.setEnabled(true);
        purchasesButton.setEnabled(true);
        salesButton.setEnabled(true);
        updateButton.setEnabled(true);
        changePasswordButton.setEnabled(true);
        signOutButton.setEnabled(true);
        themeSwitchButton.setEnabled(true);
    }

    // Disable navigation buttons before login
    public void disableNavigationButtons() {
        homeButton.setEnabled(false);
        purchasesButton.setEnabled(false);
        salesButton.setEnabled(false);
        updateButton.setEnabled(false);
        changePasswordButton.setEnabled(false);
        signOutButton.setEnabled(false);
        themeSwitchButton.setEnabled(false);
    }

    // Sign out action
    private void signOut() {
        disableNavigationButtons();
        cardLayout.show(cardPanel, "Login");  // Return to login screen
    }

    // Home panel creation
    private JPanel createHomePanel() {
        JPanel panel = new JPanel();
        updateTheme(panel);  // Apply theme when creating the panel
        JLabel label = new JLabel("Welcome to Poultry Plus");
        label.setFont(new Font("Arial", Font.BOLD, 24));
        updateTheme(label);  // Apply theme to label
        panel.add(label);
        return panel;
    }

    // Toggle between dark and light themes
    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;  // Switch theme
        applyThemeToAll();  // Apply the new theme to all panels and buttons
    }

    // Apply theme to all components
    private void applyThemeToAll() {
        updateTheme(navigationPanel);
        updateTheme(cardPanel);

        // Apply theme to all buttons
        updateTheme(homeButton);
        updateTheme(purchasesButton);
        updateTheme(salesButton);
        updateTheme(updateButton);
        updateTheme(changePasswordButton);
        updateTheme(signOutButton);
        updateTheme(themeSwitchButton);

        revalidate();
        repaint();
    }

    // Method to update the theme dynamically
    private void updateTheme(JComponent component) {
        if (isDarkTheme) {
            component.setBackground(new Color(54, 57, 63));  // Dark theme background
            component.setForeground(Color.WHITE);
        } else {
            component.setBackground(new Color(245, 245, 245));  // Light theme background
            component.setForeground(Color.BLACK);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }

    // Corrected method to return cardPanel
    public JPanel getCardPanel() {
        return cardPanel;
    }

    // Correct method to return CardLayout
    public CardLayout getCardLayout() {
        return cardLayout;
    }

    // Completed isDarkTheme method
    public boolean isDarkTheme() {
        return isDarkTheme;
    }
}
