import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PurchasesPanel extends JPanel {
    private JTable purchasesTable;
    private JLabel totalPurchasesLabel;
    private JButton nextButton, prevButton, addPurchaseButton, deletePurchaseButton;
    private int currentPage = 0;
    private int rowsPerPage = 20;
    private DefaultTableModel tableModel;

    // Database connection details
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Tanya@03";

    public PurchasesPanel(Connection connection) {
        setLayout(new BorderLayout());

        // Define column names according to the database schema
        String[] columnNames = {"Date", "Item", "Quantity", "Price", "Purchase ID"};
        tableModel = new DefaultTableModel(columnNames, 0);

        purchasesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(purchasesTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        totalPurchasesLabel = new JLabel("Total Purchases: Loading...");
        bottomPanel.add(totalPurchasesLabel, BorderLayout.WEST);

        JPanel paginationPanel = new JPanel();
        nextButton = new JButton("Next");
        prevButton = new JButton("Previous");
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);

        bottomPanel.add(paginationPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        addPurchaseButton = new JButton("Add Purchase");
        deletePurchaseButton = new JButton("Delete Purchase");
        styleButton(addPurchaseButton);
        styleButton(deletePurchaseButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addPurchaseButton);
        buttonPanel.add(deletePurchaseButton);
        add(buttonPanel, BorderLayout.NORTH);

        loadTotalPurchasesAsync();
        loadPurchasesDataAsync();

        nextButton.addActionListener(e -> {
            currentPage++;
            loadPurchasesDataAsync();
        });

        prevButton.addActionListener(e -> {
            if (currentPage > 0) currentPage--;
            loadPurchasesDataAsync();
        });

        addPurchaseButton.addActionListener(e -> openAddPurchaseScreen());
        deletePurchaseButton.addActionListener(e -> deleteSelectedPurchase());
    }

    private void loadTotalPurchasesAsync() {
        new SwingWorker<Double, Void>() {
            @Override
            protected Double doInBackground() throws SQLException {
                String query = "SELECT SUM(price * quantity) FROM purchases";
                try (Connection connection = getConnection();
                     PreparedStatement ps = connection.prepareStatement(query);
                     ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getDouble(1) : 0.0;
                }
            }

            @Override
            protected void done() {
                try {
                    totalPurchasesLabel.setText("Total Purchases: $" + get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void loadPurchasesDataAsync() {
        new SwingWorker<List<String[]>, Void>() {
            @Override
            protected List<String[]> doInBackground() throws Exception {
                String query = "SELECT date, item, quantity, price, purchases_id FROM purchases LIMIT ? OFFSET ?";
                List<String[]> data = new ArrayList<>();
                try (Connection connection = getConnection();
                     PreparedStatement ps = connection.prepareStatement(query)) {
                    ps.setInt(1, rowsPerPage);
                    ps.setInt(2, currentPage * rowsPerPage);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        data.add(new String[]{
                                rs.getString("date"),
                                rs.getString("item"),
                                String.valueOf(rs.getInt("quantity")),
                                String.valueOf(rs.getDouble("price")),
                                String.valueOf(rs.getInt("purchases_id"))
                        });
                    }
                }
                return data;
            }

            @Override
            protected void done() {
                try {
                    List<String[]> purchasesData = get();
                    updateTableModel(purchasesData);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    private void updateTableModel(List<String[]> purchasesData) {
        tableModel.setRowCount(0); // Clear existing data
        for (String[] row : purchasesData) {
            tableModel.addRow(row);
        }
    }

    private void openAddPurchaseScreen() {
        AddPurchasesScreen addPurchasesScreen = new AddPurchasesScreen();
        addPurchasesScreen.setVisible(true);

        addPurchasesScreen.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                loadPurchasesDataAsync();
                loadTotalPurchasesAsync();
            }
        });
    }

    private void deleteSelectedPurchase() {
        int selectedRow = purchasesTable.getSelectedRow();
        if (selectedRow != -1) {
            int purchaseId = Integer.parseInt(purchasesTable.getValueAt(selectedRow, 4).toString());
            String query = "DELETE FROM purchases WHERE purchases_id = ?";
            try (Connection connection = getConnection();
                 PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, purchaseId);
                ps.executeUpdate();
                loadPurchasesDataAsync();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a purchase to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(18, 140, 73));
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
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
}
