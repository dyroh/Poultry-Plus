import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PurchasesPanel extends JPanel {
    private JTable purchasesTable;
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "your_database_password";

    public PurchasesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(54, 57, 63));

        JButton loadPurchasesButton = new JButton("Load Purchases");
        loadPurchasesButton.setBackground(new Color(18, 140, 73));
        loadPurchasesButton.setForeground(Color.WHITE);
        add(loadPurchasesButton, BorderLayout.NORTH);

        purchasesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(purchasesTable);
        add(scrollPane, BorderLayout.CENTER);

        loadPurchasesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPurchases();
            }
        });
    }

    private void loadPurchases() {
        List<String[]> purchases = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM purchases";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String[] purchase = {
                        rs.getString("date"),
                        rs.getString("item"),
                        rs.getString("quantity"),
                        rs.getString("price")
                };
                purchases.add(purchase);
            }

            String[] columnNames = {"Date", "Item", "Quantity", "Price"};
            String[][] data = purchases.toArray(new String[0][]);
            purchasesTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading purchases!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class CustomTableModel extends javax.swing.table.AbstractTableModel {
        private String[][] data;
        private String[] columnNames;

        public CustomTableModel(String[][] data, String[] columnNames) {
            this.data = data;
            this.columnNames = columnNames;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }
    }
}
