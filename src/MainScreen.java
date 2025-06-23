import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainScreen extends JFrame {
    private JLabel totalChicksLabel;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Tanya@03";

    public MainScreen() {
        setTitle("Poultry Plus - Main Screen");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color whatsappDarkGrey = new Color(54, 57, 63);
        getContentPane().setBackground(whatsappDarkGrey);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBackground(whatsappDarkGrey);

        JLabel titleLabel = new JLabel("Welcome to Poultry Plus!");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(20, 5, 20, 5);
        mainPanel.add(titleLabel, gbc);

        // Initialize the total chicks label with a placeholder text
        totalChicksLabel = new JLabel("Total number of chicks: Loading...");
        totalChicksLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        mainPanel.add(totalChicksLabel, gbc);

        // Update button to refresh the number of chicks
        JButton updateButton = new JButton("Update");
        updateButton.setBackground(new Color(18, 140, 73));
        updateButton.setForeground(Color.WHITE);
        updateButton.setOpaque(true);
        updateButton.setBorderPainted(false);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UpdateChicksScreen updateChicksScreen = new UpdateChicksScreen(MainScreen.this);
                updateChicksScreen.setVisible(true);
            }
        });
        gbc.gridy = 2;
        mainPanel.add(updateButton, gbc);

        add(mainPanel, BorderLayout.CENTER);
        add(createBottomNavigation(), BorderLayout.SOUTH);

        // Fetch and display the total number of chicks from the database
        loadTotalChicks();
    }

    // Method to create the bottom navigation bar
    private JPanel createBottomNavigation() {
        JPanel bottomPanel = new JPanel(new GridLayout(1, 4));
        Color whatsappDarkGreen = new Color(18, 140, 73);

        JButton homeButton = new JButton("Home");
        homeButton.setBackground(whatsappDarkGreen);
        homeButton.setForeground(Color.WHITE);
        homeButton.setOpaque(true);
        homeButton.setBorderPainted(false);
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainScreen.this.setVisible(true);
            }
        });

        JButton purchasesButton = new JButton("Purchases");
        purchasesButton.setBackground(whatsappDarkGreen);
        purchasesButton.setForeground(Color.WHITE);
        purchasesButton.setOpaque(true);
        purchasesButton.setBorderPainted(false);
        purchasesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PurchasesScreen purchasesScreen = new PurchasesScreen();
                purchasesScreen.setVisible(true);
                MainScreen.this.setVisible(false); // Hide MainScreen
            }
        });

        JButton salesButton = new JButton("Sales");
        salesButton.setBackground(whatsappDarkGreen);
        salesButton.setForeground(Color.WHITE);
        salesButton.setOpaque(true);
        salesButton.setBorderPainted(false);
        salesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SalesScreen salesScreen = new SalesScreen();
                salesScreen.setVisible(true);
                MainScreen.this.setVisible(false); // Hide MainScreen
            }
        });

        JButton signOutButton = new JButton("Sign Out");
        signOutButton.setBackground(whatsappDarkGreen);
        signOutButton.setForeground(Color.WHITE);
        signOutButton.setOpaque(true);
        signOutButton.setBorderPainted(false);
        signOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LoginScreen loginScreen = new LoginScreen();
                loginScreen.setVisible(true);
                MainScreen.this.dispose(); // Close MainScreen
            }
        });

        bottomPanel.add(homeButton);
        bottomPanel.add(purchasesButton);
        bottomPanel.add(salesButton);
        bottomPanel.add(signOutButton);

        return bottomPanel;
    }

    // Method to fetch the total number of chicks from the database
    private void loadTotalChicks() {
        try (Connection conn = getConnection()) {
            String query = "SELECT COUNT(*) AS total FROM chicks"; // Replace with your actual table name and column
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int totalChicks = resultSet.getInt("total");
                totalChicksLabel.setText("Total number of chicks: " + totalChicks);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching total chicks count", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found!", e);
        }
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    public void updateTotalChicks(int newTotal) {
        totalChicksLabel.setText("Total number of chicks: " + newTotal);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainScreen mainScreen = new MainScreen();
                mainScreen.setVisible(true);
            }
        });
    }
}