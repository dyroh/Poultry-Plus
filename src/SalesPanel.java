import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SalesPanel extends JPanel {
    private JTable salesTable;
    private JLabel totalSalesLabel;
    private DefaultTableModel tableModel;
    private JTextField searchField; // For search queries
    private JComboBox<String> sortComboBox; // For sorting
    private String[] customerNames; // 1D array for storing customer names

    public SalesPanel(Connection connection) {
        setLayout(new BorderLayout());

        // Column names with sales_id for internal use
        String[] columnNames = {"Sales ID", "Date", "Number of Chicks Sold", "Customer Name", "Customer Address", "Customer Contact", "Amount Paid", "Debt"};

        // Initialize table model and hide sales_id column
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        salesTable = new JTable(tableModel);
        salesTable.removeColumn(salesTable.getColumnModel().getColumn(0)); // Hide sales_id column

        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search by Customer Name:"), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalSalesLabel = new JLabel("Total Sales: Loading...");
        bottomPanel.add(totalSalesLabel, BorderLayout.WEST);

        sortComboBox = new JComboBox<>(new String[]{"Sort by Date (Newest)", "Sort by Date (Oldest)", "Sort by Amount (High to Low)", "Sort by Amount (Low to High)"});
        bottomPanel.add(sortComboBox, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        JButton viewLogButton = new JButton("View Log");
        JButton addSalesButton = new JButton("Add Sale");
        JButton deleteSalesButton = new JButton("Delete Sale");

        actionPanel.add(viewLogButton);
        actionPanel.add(addSalesButton);
        actionPanel.add(deleteSalesButton);

        bottomPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);

        loadTotalSalesAsync();
        loadSalesDataAsync();

        // Action listeners
        searchButton.addActionListener(e -> loadSalesDataAsync());
        sortComboBox.addActionListener(e -> loadSalesDataAsync());
        viewLogButton.addActionListener(e -> saveLogToFile()); // Save log to file on click
        addSalesButton.addActionListener(e -> openAddSalesScreen()); // Open add sales screen on click
        deleteSalesButton.addActionListener(e -> deleteSelectedSale()); // Delete sale on click
    }

    // Method to save logs to a file from database records
    private void saveLogToFile() {
        String query = "SELECT * FROM sales_log"; //  Log table query
        File logFile = new File(System.getProperty("user.home") + "/Desktop/sales_log.txt");

        // Create file if it doesn't exist
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error creating log file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return; // Exit if file can't be created
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery();
             FileWriter writer = new FileWriter(logFile, true)) { // Append mode

            while (rs.next()) {
                String logEntry = "Date: " + rs.getString("date") + ", Event: " + rs.getString("event_description");
                writer.write(logEntry + "\n");
            }
            JOptionPane.showMessageDialog(this, "Log has been saved to " + logFile.getPath(), "Success", JOptionPane.INFORMATION_MESSAGE);

            // Automatically open the file for viewing
            Desktop.getDesktop().open(logFile);

        } catch (SQLException | IOException e) {
            JOptionPane.showMessageDialog(this, "Error writing log file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void loadTotalSalesAsync() {
        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws SQLException {
                String query = "SELECT SUM(amount_paid) FROM sales";
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement ps = connection.prepareStatement(query);
                     ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getDouble(1) : 0.0;
                }
            }

            @Override
            protected void done() {
                try {
                    totalSalesLabel.setText("Total Sales: $" + get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void loadSalesDataAsync() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                String searchQuery = searchField.getText().trim();
                String sortOrder = getSortOrder();

                String query = "SELECT sales_id, Date, Number_of_Chicks_Sold, Customer_Name, Customer_Address, Customer_Contact, Amount_Paid, Debt FROM sales";

                if (!searchQuery.isEmpty()) {
                    query += " WHERE Customer_Name LIKE ?";
                }

                query += sortOrder;

                List<Object[]> data = new ArrayList<>();
                List<String> names = new ArrayList<>(); // Temporary list for customer names
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement ps = connection.prepareStatement(query)) {
                    if (!searchQuery.isEmpty()) {
                        ps.setString(1, "%" + searchQuery + "%");
                    }
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        data.add(new Object[]{
                                rs.getInt("sales_id"), rs.getString("Date"), rs.getInt("Number_of_Chicks_Sold"),
                                rs.getString("Customer_Name"), rs.getString("Customer_Address"),
                                rs.getString("Customer_Contact"), rs.getDouble("Amount_Paid"), rs.getDouble("Debt")
                        });

                        // Collect customer names
                        names.add(rs.getString("Customer_Name"));
                    }

                    // Convert the names list to a 1D array
                    customerNames = names.toArray(new String[0]);
                }
                return data;
            }

            @Override
            protected void done() {
                try {
                    updateTableModel(get());

                    // Example: Print the customer names from the array (optional for debugging)
                    if (customerNames != null) {
                        System.out.println("Customer Names: ");
                        for (String name : customerNames) {
                            System.out.println(name);
                        }
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private String getSortOrder() {
        int selectedIndex = sortComboBox.getSelectedIndex();
        switch (selectedIndex) {
            case 0:
                return " ORDER BY Date DESC";
            case 1:
                return " ORDER BY Date ASC";
            case 2:
                return " ORDER BY Amount_Paid DESC";
            case 3:
                return " ORDER BY Amount_Paid ASC";
            default:
                return "";
        }
    }

    private void updateTableModel(List<Object[]> salesData) {
        tableModel.setRowCount(0);
        for (Object[] row : salesData) {
            tableModel.addRow(row);
        }
    }

    // Open AddSalesScreen to add a new sale
    private void openAddSalesScreen() {
        AddSalesScreen addSalesScreen = new AddSalesScreen();
        addSalesScreen.setVisible(true);
        addSalesScreen.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadSalesDataAsync(); // Refresh data after adding a sale
                loadTotalSalesAsync();
            }
        });
    }

    // Delete selected sale from the database
    private void deleteSelectedSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow != -1) {
            int saleId = Integer.parseInt(tableModel.getValueAt(salesTable.convertRowIndexToModel(selectedRow), 0).toString());
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this sale?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement ps = connection.prepareStatement("DELETE FROM sales WHERE sales_id = ?")) {
                    ps.setInt(1, saleId);
                    ps.executeUpdate();
                    loadSalesDataAsync(); // Reload data after deletion
                    loadTotalSalesAsync(); // Update total sales label
                    JOptionPane.showMessageDialog(this, "Sale deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error deleting sale from database.", "Database Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a sale to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
}
