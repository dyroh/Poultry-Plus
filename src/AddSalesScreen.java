import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddSalesScreen extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Tanya@03";

    private JTextField dateField;
    private JTextField numberOfChicksField;
    private JTextField customerNameField;
    private JTextField customerAddressField;
    private JTextField customerContactField;
    private JTextField amountField;
    private JTextField debtField;

    public AddSalesScreen() {
        setTitle("Add Sales");
        setSize(500, 600);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set theme color
        Color whatsappDarkGrey = new Color(54, 57, 63);
        getContentPane().setBackground(whatsappDarkGrey);

        JPanel addSalesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        addSalesPanel.setBackground(whatsappDarkGrey);

        setupForm(addSalesPanel, gbc);

        // Add Button with theme
        Color whatsappDarkGreen = new Color(18, 140, 73);
        JButton addButton = new JButton("Add");
        addButton.setBackground(whatsappDarkGreen);
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);

        addButton.addActionListener(e -> addSalesRecord());

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        addSalesPanel.add(addButton, gbc);

        add(addSalesPanel, BorderLayout.CENTER);
    }

    private void setupForm(JPanel panel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel("Add Sales Record");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        panel.add(titleLabel, gbc);

        // Form fields setup
        setupField(panel, gbc, "Date (yyyy-mm-dd):", dateField = new JTextField(15), 1);
        setupField(panel, gbc, "Number of Chicks Sold:", numberOfChicksField = new JTextField(15), 2);
        setupField(panel, gbc, "Customer Name:", customerNameField = new JTextField(15), 3);
        setupField(panel, gbc, "Customer Address:", customerAddressField = new JTextField(15), 4);
        setupField(panel, gbc, "Customer Contact:", customerContactField = new JTextField(15), 5);
        setupField(panel, gbc, "Amount Paid:", amountField = new JTextField(15), 6);
        setupField(panel, gbc, "Debt:", debtField = new JTextField(15), 7);
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

    private void addSalesRecord() {
        String date = dateField.getText();
        String numberOfChicks = numberOfChicksField.getText();
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerContact = customerContactField.getText();
        String amount = amountField.getText();
        String debt = debtField.getText();

        if (validateFields(date, numberOfChicks, customerName, customerAddress, customerContact, amount, debt)) {
            try {
                int chicksSold = Integer.parseInt(numberOfChicks);
                double paidAmount = Double.parseDouble(amount);
                double debtAmount = debt.isEmpty() ? 0 : Double.parseDouble(debt);

                insertSalesRecord(date, chicksSold, customerName, customerAddress, customerContact, paidAmount, debtAmount);

                JOptionPane.showMessageDialog(this, "Sales record added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close the add sales screen
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error adding sales record to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private boolean validateFields(String date, String numberOfChicks, String customerName, String customerAddress, String customerContact, String amount, String debt) {
        if (date.isEmpty() || numberOfChicks.isEmpty() || customerName.isEmpty() || customerAddress.isEmpty() || customerContact.isEmpty() || amount.isEmpty()) {
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
            Integer.parseInt(numberOfChicks);
            Double.parseDouble(amount);
            if (!debt.isEmpty()) Double.parseDouble(debt);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void insertSalesRecord(String date, int chicksSold, String customerName, String customerAddress, String customerContact, double amountPaid, double debt) throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "INSERT INTO sales (Date, Number_of_Chicks_Sold, Customer_Name, Customer_Address, Customer_Contact, Amount_Paid, Debt) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, date);
                ps.setInt(2, chicksSold);
                ps.setString(3, customerName);
                ps.setString(4, customerAddress);
                ps.setString(5, customerContact);
                ps.setDouble(6, amountPaid);
                ps.setDouble(7, debt);
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
            AddSalesScreen addSalesScreen = new AddSalesScreen();
            addSalesScreen.setVisible(true);
        });
    }
}
