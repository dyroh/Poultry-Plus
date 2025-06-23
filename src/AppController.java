import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppController extends JFrame {
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JPanel navigationPanel;
    private JButton[] navButtons;
    private boolean darkTheme = true; // Default to dark theme
    private LoginPanel loginPanel;
    private Connection connection; // Database connection

    public AppController(Connection connection) {
        this.connection = connection; // Initialize connection
        setTitle("Poultry Plus Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create navigation panel (hidden initially)
        navigationPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        navigationPanel.setBackground(new Color(0, 128, 0)); // Green background

        // Create buttons but keep them hidden until login
        createNavButtons();
        setNavButtonsVisible(false); // Initially hidden

        // Create the card panel that will hold different screens
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        // Add screens to card layout
        loginPanel = new LoginPanel(this); // Pass the main controller to LoginPanel
        cardPanel.add(loginPanel, "Login");
        cardPanel.add(new MainScreen(this, connection), "Home");
        cardPanel.add(new SalesPanel(connection), "Sales");
        cardPanel.add(new PurchasesPanel(connection), "Purchases");
        cardPanel.add(new UpdateChicksPanel(this), "UpdateChicks");
        cardPanel.add(new SettingsPanel(this), "Settings");

        add(navigationPanel, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        // Show login screen first
        cardLayout.show(cardPanel, "Login");
    }

    private void createNavButtons() {
        String[] buttonNames = {"Home", "Purchases", "Sales", "Update Chicks", "Settings", "Sign Out"};
        navButtons = new JButton[buttonNames.length];

        for (int i = 0; i < buttonNames.length; i++) {
            JButton button = new JButton(buttonNames[i]);
            styleButton(button);
            button.addActionListener(new NavigationListener(buttonNames[i]));
            navigationPanel.add(button);
            navButtons[i] = button;
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(0, 128, 0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GREEN, 2));
    }

    // Show or hide navigation buttons
    private void setNavButtonsVisible(boolean visible) {
        for (JButton button : navButtons) {
            button.setVisible(visible);
        }
    }

    // Updated authenticate method to validate against the database
    public boolean authenticate(String username, String password) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                setNavButtonsVisible(true); // Show navigation buttons after successful login
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error during login.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    // Handle sign-out process
    public void signOut() {
        setNavButtonsVisible(false); // Hide navigation buttons on sign-out
        loginPanel.resetFields(); // Clear the login fields
        cardLayout.show(cardPanel, "Login"); // Show the login screen
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public JPanel getCardPanel() {
        return cardPanel;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void toggleTheme() {
        darkTheme = !darkTheme;
        SwingUtilities.updateComponentTreeUI(this);
    }

    class NavigationListener implements java.awt.event.ActionListener {
        private String screenName;

        public NavigationListener(String screenName) {
            this.screenName = screenName;
        }

        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (screenName.equals("Sign Out")) {
                signOut();
            } else {
                cardLayout.show(cardPanel, screenName);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try (Connection connection = DatabaseConnection.getConnection()) { // Get database connection
                AppController controller = new AppController(connection);
                controller.setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to connect to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
