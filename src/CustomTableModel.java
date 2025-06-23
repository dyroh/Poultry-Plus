import javax.swing.table.AbstractTableModel;

public class CustomTableModel extends AbstractTableModel {
    private String[][] data;
    private String[] columnNames;

    public CustomTableModel(String[][] data, String[] columnNames) {
        this.data = data;
        this.columnNames = columnNames;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return data[rowIndex][columnIndex];
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    // Enable editing for specific columns if necessary
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Example: Only allow editing in the "Quantity" and "Price" columns
        // Assuming "Quantity" is column index 2 and "Price" is column index 3
        return columnIndex == 2 || columnIndex == 3;
    }

    // Set new value for an editable cell with validation
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (isValidInput(value, columnIndex)) {
            data[rowIndex][columnIndex] = value.toString();
            fireTableCellUpdated(rowIndex, columnIndex); // Notify listeners that data has changed
        } else {
            System.err.println("Invalid input for column " + getColumnName(columnIndex));
        }
    }

    // Method to validate the data entered for specific columns
    private boolean isValidInput(Object value, int columnIndex) {
        try {
            if (columnIndex == 2) { // Quantity column: ensure it's an integer
                Integer.parseInt(value.toString());
            } else if (columnIndex == 3) { // Price column: ensure it's a valid double
                Double.parseDouble(value.toString());
            }
        } catch (NumberFormatException e) {
            return false; // Invalid input
        }
        return true; // Valid input
    }

    // Method to update the entire data set dynamically (for real-time data updates)
    public void updateData(String[][] newData) {
        this.data = newData;
        fireTableDataChanged(); // Notify listeners that the whole data set has changed
    }

    // Method to add a new row to the table (optional for future extensibility)
    public void addRow(String[] newRow) {
        String[][] tempData = new String[data.length + 1][getColumnCount()];
        System.arraycopy(data, 0, tempData, 0, data.length);
        tempData[data.length] = newRow;
        data = tempData;
        fireTableRowsInserted(data.length - 1, data.length - 1);
    }

    // Method to remove a row from the table (optional for future extensibility)
    public void removeRow(int rowIndex) {
        String[][] tempData = new String[data.length - 1][getColumnCount()];
        for (int i = 0, j = 0; i < data.length; i++) {
            if (i != rowIndex) {
                tempData[j++] = data[i];
            }
        }
        data = tempData;
        fireTableRowsDeleted(rowIndex, rowIndex);
    }

    // Add sorting capabilities (sort by column)
    public void sortDataByColumn(int columnIndex, boolean ascending) {
        java.util.Arrays.sort(data, (row1, row2) -> {
            if (columnIndex == 2 || columnIndex == 3) { // Quantity or Price
                double value1 = Double.parseDouble(row1[columnIndex]);
                double value2 = Double.parseDouble(row2[columnIndex]);
                return ascending ? Double.compare(value1, value2) : Double.compare(value2, value1);
            } else {
                return ascending ? row1[columnIndex].compareTo(row2[columnIndex]) : row2[columnIndex].compareTo(row1[columnIndex]);
            }
        });
        fireTableDataChanged(); // Notify the table that data has changed
    }
}
