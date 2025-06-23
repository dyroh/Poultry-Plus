import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddPurchasesScreen extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Tanya@03";

    private JTextField dateField;
    private JTextField itemField;
    private JTextField quantityField;
    private JTextField priceField;

    public AddPurchasesScreen() {
        setTitle("Add Purchase");
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set theme color
        Color whatsappDarkGrey = new Color(54, 57, 63);
        getContentPane().setBackground(whatsappDarkGrey);

        JPanel addPurchasePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        addPurchasePanel.setBackground(whatsappDarkGrey);

        setupForm(addPurchasePanel, gbc);

        // Add Button with theme
        Color whatsappDarkGreen = new Color(18, 140, 73);
        JButton addButton = new JButton("Add");
        addButton.setBackground(whatsappDarkGreen);
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);

        addButton.addActionListener(e -> addPurchaseRecord());

        gbc.gridy = 6;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        addPurchasePanel.add(addButton, gbc);

        add(addPurchasePanel, BorderLayout.CENTER);
    }

    private void setupForm(JPanel panel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Add Purchase Record");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        panel.add(titleLabel, gbc);

        // Form fields setup
        setupField(panel, gbc, "Date (yyyy-mm-dd):", dateField = new JTextField(15), 1);
        setupField(panel, gbc, "Item:", itemField = new JTextField(15), 2);
        setupField(panel, gbc, "Quantity:", quantityField = new JTextField(15), 3);
        setupField(panel, gbc, "Price:", priceField = new JTextField(15), 4);
    }

    private void setupField(JPanel panel, GridBagConstraints gbc, String labelText, JTextField field, int row) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    private void addPurchaseRecord() {
        String date = dateField.getText();
        String item = itemField.getText();
        String quantity = quantityField.getText();
        String price = priceField.getText();

        if (validateFields(date, item, quantity, price)) {
            try {
                int itemQuantity = Integer.parseInt(quantity);
                double itemPrice = Double.parseDouble(price);

                insertPurchaseRecord(date, item, itemQuantity, itemPrice);

                JOptionPane.showMessageDialog(this, "Purchase record added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the add purchase screen
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding purchase record to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private boolean validateFields(String date, String item, String quantity, String price) {
        if (date.isEmpty() || item.isEmpty() || quantity.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        try {
            new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date (yyyy-mm-dd).", "Invalid Date", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(quantity);
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void insertPurchaseRecord(String date, String item, int quantity, double price) throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO purchases (date, item, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, date);
                ps.setString(2, item);
                ps.setInt(3, quantity);
                ps.setDouble(4, price);
                ps.executeUpdate();
            }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AddPurchasesScreen addPurchasesScreen = new AddPurchasesScreen();
            addPurchasesScreen.setVisible(true);
        });
    }
}
