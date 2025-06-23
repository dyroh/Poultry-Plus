import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SalesPanel extends JPanel {
    private JTable salesTable;
    private static final String URL = "jdbc:mysql://localhost:3306/PoultryPlus";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "your_database_password";

    public SalesPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(54, 57, 63));

        JButton loadSalesButton = new JButton("Load Sales");
        loadSalesButton.setBackground(new Color(18, 140, 73));
        loadSalesButton.setForeground(Color.WHITE);
        add(loadSalesButton, BorderLayout.NORTH);

        salesTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(salesTable);
        add(scrollPane, BorderLayout.CENTER);

        loadSalesButton.addActionListener(e -> loadSales());
    }

    private void loadSales() {
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String query = "SELECT * FROM sales";
            PreparedStatement statement = conn.prepareStatement(query);
            ResultSet rs = statement.executeQuery();

            String[] columnNames = {"Date", "Chicks Sold", "Customer Name", "Amount Paid", "Debt"};
            String[][] data = new String[rs.getFetchSize()][columnNames.length];
            int i = 0;
            while (rs.next()) {
                data[i++] = new String[]{
                        rs.getString("date"),
                        rs.getString("chicks_sold"),
                        rs.getString("customer_name"),
                        rs.getString("amount_paid"),
                        rs.getString("debt")
                };
            }
            salesTable.setModel(new CustomTableModel(data, columnNames));
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading sales!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
