import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddSalesScreen extends JFrame {
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus"; // Replace with your database URL
    private static final String USERNAME = "root"; // Replace with your database username
    private static final String PASSWORD = "Tanya@03"; // Replace with your database password
    private JTextField dateField;
    private JTextField numberOfChicksField;
    private JTextField customerNameField;
    private JTextField customerAddressField;
    private JTextField customerContactField;
    private JTextField amountField;
    private JTextField debtField;

    public AddSalesScreen() {
        setTitle("Add Sales");
        setSize(500, 600); // Height and width of the screen pop up
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color whatsappDarkGrey = new Color(54, 57, 63);
        getContentPane().setBackground(whatsappDarkGrey);

        JPanel addSalesPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        addSalesPanel.setBackground(whatsappDarkGrey);

        JLabel titleLabel = new JLabel("Add Sales Record");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        addSalesPanel.add(titleLabel, gbc);

        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setForeground(Color.WHITE);
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        addSalesPanel.add(dateLabel, gbc);

        dateField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addSalesPanel.add(dateField, gbc);

        JLabel numberOfChicksLabel = new JLabel("Number of Chicks Sold:");
        numberOfChicksLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        addSalesPanel.add(numberOfChicksLabel, gbc);

        numberOfChicksField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addSalesPanel.add(numberOfChicksField, gbc);

        JLabel customerNameLabel = new JLabel("Customer Name:");
        customerNameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        addSalesPanel.add(customerNameLabel, gbc);

        customerNameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addSalesPanel.add(customerNameField, gbc);

        JLabel customerAddressLabel = new JLabel("Customer Address:");
        customerAddressLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 4;
        addSalesPanel.add(customerAddressLabel, gbc);

        customerAddressField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addSalesPanel.add(customerAddressField, gbc);

        JLabel customerContactLabel = new JLabel("Customer Contact:");
        customerContactLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        addSalesPanel.add(customerContactLabel, gbc);

        customerContactField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addSalesPanel.add(customerContactField, gbc);

        JLabel amountLabel = new JLabel("Amount Paid:");
        amountLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 6;
        addSalesPanel.add(amountLabel, gbc);

        amountField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addSalesPanel.add(amountField, gbc);

        JLabel debtLabel = new JLabel("Debt:");
        debtLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 7;
        addSalesPanel.add(debtLabel, gbc);

        debtField = new JTextField(15);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        addSalesPanel.add(debtField, gbc);

        Color whatsappDarkGreen = new Color(18, 140, 73);

        JButton addButton = new JButton("Add");
        addButton.setBackground(whatsappDarkGreen);
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText();
                String numberOfChicks = numberOfChicksField.getText();
                String customerName = customerNameField.getText();
                String customerAddress = customerAddressField.getText();
                String customerContact = customerContactField.getText();
                String amount = amountField.getText();
                String debt = debtField.getText();

                if (!date.isEmpty() && !numberOfChicks.isEmpty() && !customerName.isEmpty() && !customerAddress.isEmpty() && !customerContact.isEmpty() && !amount.isEmpty()) {
                    try {
                        int chicksSold = Integer.parseInt(numberOfChicks);
                        double paidAmount = Double.parseDouble(amount);
                        double debtAmount = debt.isEmpty() ? 0 : Double.parseDouble(debt);

                        // Add logic to insert the new sales record into the database here

                        dispose(); // Close the add sales screen
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(AddSalesScreen.this, "Please enter valid numeric values for chicks, amount, and debt.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(AddSalesScreen.this, "Please fill in all fields.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.NONE;
        addSalesPanel.add(addButton, gbc);

        add(addSalesPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                AddSalesScreen addSalesScreen = new AddSalesScreen();
                addSalesScreen.setVisible(true);
            }
        });
    }
}