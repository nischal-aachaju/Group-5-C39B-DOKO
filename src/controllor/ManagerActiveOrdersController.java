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
        
        this.activeView.setUsernameLabel(currentUser.getUsername());
        this.activeView.setRoleLabel(currentUser.getRole());
        
        // 1. Load all orders by default when the window first opens
        loadOrdersToTable("All");
        
        // 2. Connect the Filter button
        this.activeView.addFilterListener(new FilterButtonListener());
        this.activeView.addLogoutListener(new LogoutListener());
        this.activeView.addDashboardListener(new DashboardListener());
        this.activeView.addManageUserListener(new OpenManageUserListener());
        this.activeView.addWorkloadListener(new OpenWorkloadListener() );
        this.activeView.addManageOrdersListener(new OpenManageOrdersListener());
        this.activeView.addAssiggnedOrdersListener(new OpenAssiggnedrdersListener() );
        this.activeView.addMyProfileListener(new OpenManagerProfileListener());
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
     class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }
     
    class DashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.Manager_Dashboard dashboardView = new view.Manager_Dashboard();
            new controllor.ManagerController(dashboardView, currentUser).open();
        }
    }
class OpenManageUserListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            
            // 2. Create the User Management View
            view.Useraccountmanagement manageUserView = new view.Useraccountmanagement();
            
            // 3. Pass it entirely to your dedicated Manage User Controller
            controllor.ManageUserController manageUserController = new controllor.ManageUserController(manageUserView, currentUser);
            
            // 4. Open the User Management page!
            manageUserController.open();
        }
}
        class OpenWorkloadListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.Manager_Workload WorkloadView = new view.Manager_Workload();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerWorkloadController managerAssignOrderController = new controllor.ManagerWorkloadController(WorkloadView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerAssignOrderController.open();
        }
    }
class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.ManagerOrderEdit managerOrderEditView = new view.ManagerOrderEdit();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerOrderEditController managerOrderEditController = new controllor.ManagerOrderEditController(managerOrderEditView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerOrderEditController.open();
        }
    }
    class OpenAssiggnedrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.assignedorder assignedorderView = new view.assignedorder();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerAssignOrderController managerAssignOrderController = new controllor.ManagerAssignOrderController(assignedorderView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerAssignOrderController.open();
        }
    }
    class OpenManagerProfileListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the exact Manager Profile View
            view.Manager_profileEdit profileView = new view.Manager_profileEdit();
            
            // 3. Pass it entirely to your dedicated Manager Profile Controller
            controllor.Manager_ProfileController profileController = new controllor.Manager_ProfileController(profileView, currentUser);
            
            // 4. Open the profile page!
            profileController.open();
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