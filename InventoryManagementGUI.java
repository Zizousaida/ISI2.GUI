import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

class InventoryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String code;
    private int quantity;
    private double price;

    public InventoryItem(String name, String code, int quantity, double price) {
        this.name = name;
        this.code = code;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getQuantity() {
        return quantity;
    }
    
    public double getPrice() {
        return price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Item: " + name + 
               ", Code: " + code + 
               ", Quantity: " + quantity + 
               ", Price: $" + String.format("%.2f", price);
    }
}

public class InventoryManagementGUI extends JFrame {
    private static final long serialVersionUID = 1L;
    private ArrayList<InventoryItem> inventory = new ArrayList<>();
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, codeField, quantityField, priceField;
    
    public InventoryManagementGUI() {
        setTitle("Inventory Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Inventory Item"));
        
        formPanel.add(new JLabel("Item Name:"));
        nameField = new JTextField(20);
        formPanel.add(nameField);
        
        formPanel.add(new JLabel("Item Code:"));
        codeField = new JTextField(10);
        formPanel.add(codeField);
        
        formPanel.add(new JLabel("Quantity:"));
        quantityField = new JTextField(10);
        formPanel.add(quantityField);
        
        formPanel.add(new JLabel("Price ($):"));
        priceField = new JTextField(10);
        formPanel.add(priceField);
        
        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> addItem());
        formPanel.add(addButton);
        
        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(e -> clearFields());
        formPanel.add(clearButton);
        
        // Table panel
        String[] columns = {"Item Name", "Code", "Quantity", "Price ($)"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        inventoryTable = new JTable(tableModel);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(inventoryTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Inventory Items"));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton deleteButton = new JButton("Delete Selected Item");
        deleteButton.addActionListener(e -> deleteItem());
        buttonPanel.add(deleteButton);
        
        JButton updateButton = new JButton("Update Quantity");
        updateButton.addActionListener(e -> updateQuantity());
        buttonPanel.add(updateButton);
        
        JButton saveButton = new JButton("Save Inventory");
        saveButton.addActionListener(e -> saveInventory());
        buttonPanel.add(saveButton);
        
        JButton loadButton = new JButton("Load Inventory");
        loadButton.addActionListener(e -> loadInventory());
        buttonPanel.add(loadButton);
        
        // Add components to main panel
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private void addItem() {
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String priceStr = priceField.getText().trim();
        
        if (name.isEmpty() || code.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if code already exists
        for (InventoryItem item : inventory) {
            if (item.getCode().equals(code)) {
                JOptionPane.showMessageDialog(this, "An item with this code already exists!", 
                                             "Duplicate Code", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative!", 
                                             "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                JOptionPane.showMessageDialog(this, "Price cannot be negative!", 
                                             "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            InventoryItem item = new InventoryItem(name, code, quantity, price);
            inventory.add(item);
            
            // Add to table
            tableModel.addRow(new Object[]{name, code, quantity, String.format("%.2f", price)});
            
            clearFields();
            JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price format!", 
                                         "Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteItem() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                            "Are you sure you want to delete this item?", 
                            "Confirm Deletion", 
                            JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                inventory.remove(selectedRow);
                tableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(this, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void updateQuantity() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow >= 0) {
            InventoryItem item = inventory.get(selectedRow);
            String input = JOptionPane.showInputDialog(this, 
                          "Current quantity: " + item.getQuantity() + "\nEnter new quantity:", 
                          "Update Quantity", 
                          JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.isEmpty()) {
                try {
                    int newQuantity = Integer.parseInt(input);
                    if (newQuantity < 0) {
                        JOptionPane.showMessageDialog(this, "Quantity cannot be negative!", 
                                                     "Input Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    item.setQuantity(newQuantity);
                    tableModel.setValueAt(newQuantity, selectedRow, 2);
                    JOptionPane.showMessageDialog(this, "Quantity updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Invalid quantity format!", 
                                                 "Format Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to update.", "Selection Required", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void saveInventory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Inventory");
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getAbsolutePath();
            if (!fileName.endsWith(".dat")) {
                fileName += ".dat";
            }
            
            try {
                FileOutputStream fileOut = new FileOutputStream(fileName);
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(inventory);
                out.close();
                fileOut.close();
                JOptionPane.showMessageDialog(this, "Inventory saved successfully to " + fileName, 
                                             "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), 
                                             "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadInventory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Inventory");
        
        int userSelection = fileChooser.showOpenDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            
            try {
                FileInputStream fileIn = new FileInputStream(fileToLoad);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                inventory = (ArrayList<InventoryItem>) in.readObject();
                in.close();
                fileIn.close();
                
                // Update table
                updateTable();
                
                JOptionPane.showMessageDialog(this, "Inventory loaded successfully!", 
                                             "Load Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), 
                                             "Load Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateTable() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Add all items to table
        for (InventoryItem item : inventory) {
            tableModel.addRow(new Object[]{
                item.getName(), 
                item.getCode(), 
                item.getQuantity(),
                String.format("%.2f", item.getPrice())
            });
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        codeField.setText("");
        quantityField.setText("");
        priceField.setText("");
        nameField.requestFocus();
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            InventoryManagementGUI app = new InventoryManagementGUI();
            app.setVisible(true);
        });
    }
}
