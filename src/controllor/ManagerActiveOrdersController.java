/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

import Model.userData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class ManagerActiveOrdersController {
    
    // Ensure this perfectly matches your UI JFrame filename!
    private final view.Manager_active_orders activeView;
    private final userData currentUser;

    public ManagerActiveOrdersController(view.Manager_active_orders activeView, userData currentUser) {
        this.activeView = activeView;
        this.currentUser = currentUser;
        
        // 1. Load all orders by default when the window first opens
        loadOrdersToTable("All");
        
        // 2. Connect the Filter button
        this.activeView.addFilterListener(new FilterButtonListener());
    }

    public void open() {
        this.activeView.setVisible(true);
        this.activeView.setLocationRelativeTo(null);
    }

    public void close() {
        this.activeView.dispose();
    }

    // =========================================================================
    // LOAD DATA LOGIC
    // =========================================================================

    private void loadOrdersToTable(String statusFilter) {
        DAO.OrderDAO dao = new DAO.OrderDAO();
        ResultSet rs = dao.getFilteredOrders(statusFilter);
        
        // Wipe the table clean before injecting new data
        activeView.clearTable();
        
        try {
            while (rs != null && rs.next()) {
                // Must map to your UI columns: 
                // Tracking ID | Customer | Contact | Shipment | Status | Delivery Cost | Price
                
                String trackingId = rs.getString("tracking_id");
                String customer = rs.getString("receiver_name");
                String contact = rs.getString("receiver_contact");
                String shipment = rs.getString("receiver_location");
                String status = rs.getString("status");
                double deliveryCost = rs.getDouble("delivery_cost");
                double totalCost = rs.getDouble("total_cost");
                
                // Inject straight into the table!
                activeView.addTableRow(new Object[]{
                    trackingId, customer, contact, shipment, status, deliveryCost, totalCost
                });
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // FILTER BUTTON LOGIC
    // =========================================================================

    class FilterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // These are your exact database Enum options, plus "All"
            String[] filterOptions = {"All", "pending", "cancelled", "delivered", "intransit", "return"};
            
            // Pop open a dropdown dialog for the Manager
            String selectedFilter = (String) JOptionPane.showInputDialog(
                    activeView,
                    "Select an Order Status to view:",
                    "Filter Orders",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    filterOptions,
                    filterOptions[0] // Default to "All"
            );
            
            // If they picked something (and didn't hit cancel), reload the table!
            if (selectedFilter != null) {
                loadOrdersToTable(selectedFilter);
            }
        }
    }
}