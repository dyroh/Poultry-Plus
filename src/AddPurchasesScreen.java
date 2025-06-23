import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddPurchaseScreen extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus"; // Replace with your database URL
    private static final String USERNAME = "root"; // Replace with your database username
    private static final String PASSWORD = "Tanya@03"; // Replace with your database password

    private JTextField dateField;
    private JTextField itemField;
    private JTextField quantityField;
    private JTextField priceField;

    public AddPurchaseScreen() {
        setTitle("Add Purchase");
        setSize(400, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color whatsappDarkGrey = new Color(54, 57, 63);
        getContentPane().setBackground(whatsappDarkGrey);

        JPanel addPurchasePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        addPurchasePanel.setBackground(whatsappDarkGrey);

        JLabel titleLabel = new JLabel("Add Purchase Record");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        addPurchasePanel.add(titleLabel, gbc);

        JLabel dateLabel = new JLabel("Date (yyyy-mm-dd):");
        dateLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        addPurchasePanel.add(dateLabel, gbc);

        dateField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPurchasePanel.add(dateField, gbc);

        JLabel itemLabel = new JLabel("Item:");
        itemLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        addPurchasePanel.add(itemLabel, gbc);

        itemField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPurchasePanel.add(itemField, gbc);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        addPurchasePanel.add(quantityLabel, gbc);

        quantityField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPurchasePanel.add(quantityField, gbc);

        JLabel priceLabel = new JLabel("Price:");
        priceLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        addPurchasePanel.add(priceLabel, gbc);

        priceField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addPurchasePanel.add(priceField, gbc);

        Color whatsappDarkGreen = new Color(18, 140, 73);

        JButton addButton = new JButton("Add");
        addButton.setBackground(whatsappDarkGreen);
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPurchaseRecord();
            }
        });

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        gbc.fill = GridBagConstraints.NONE;
        addPurchasePanel.add(addButton, gbc);

        add(addPurchasePanel, BorderLayout.CENTER);
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

                // Insert the new purchase record into the database
                insertPurchase(date, item, itemQuantity, itemPrice);

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
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for quantity.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Double.parseDouble(price);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid numeric value for price.", "Invalid Price", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    // Method to insert a purchase record into the database
    private void insertPurchase(String date, String item, int quantity, double price) throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO purchases (purchase_date, item, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, date);
                statement.setString(2, item);
                statement.setInt(3, quantity);
                statement.setDouble(4, price);
                statement.executeUpdate();
            }
        }
    }

    // Method to establish a database connection
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
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AddPurchaseScreen addPurchaseScreen = new AddPurchaseScreen();
                addPurchaseScreen.setVisible(true);
            }
        });
    }
}