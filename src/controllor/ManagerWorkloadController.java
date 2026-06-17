package controllor;

import Model.userData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

public class ManagerWorkloadController {
    
    // Make sure this perfectly matches your UI JFrame filename!
    private final view.Manager_Workload workloadView;
    private final userData currentUser;

    public ManagerWorkloadController(view.Manager_Workload workloadView, userData currentUser) {
        this.workloadView = workloadView;
        this.currentUser = currentUser;
        
        // 1. Set top right identity labels
        this.workloadView.setUsernameLabel(currentUser.getUsername());
        this.workloadView.setRoleLabel(currentUser.getRole());
        
        // 2. Clear UI to 0s on load
        this.workloadView.setWorkloadStats("0", "0", "0", "0", "0");
        this.workloadView.clearTable();
        
        // 3. Connect the Search button
        this.workloadView.addSearchListener(new SearchWorkloadListener());
        this.workloadView.addDashboardListener(new OpenDashboardListener());
        this.workloadView.addLogoutListener(new LogoutListener());
        this.workloadView.addManageUserListener(new OpenManageUserListener());
        this.workloadView.addMyProfileListener(new OpenManagerProfileListener());
        this.workloadView.addManageOrdersListener(new OpenManageOrdersListener());
        this.workloadView.addAssiggnedOrdersListener(new OpenAssiggnedrdersListener() );
        this.workloadView.addActiveOrdersListener(new OpenActiveOrdersListener() );

    }

    public void open() {
        this.workloadView.setVisible(true);
        this.workloadView.setLocationRelativeTo(null);
    }

    public void close() {
        this.workloadView.dispose();
    }

    // =========================================================================
    // SEARCH & POPULATE LOGIC
    // =========================================================================

    class SearchWorkloadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String searchInput = workloadView.getSearchInput();
            
            if (searchInput.isEmpty()) {
                JOptionPane.showMessageDialog(workloadView, "Please enter an Employee ID to search.");
                return;
            }
            
            try {
                int empId = Integer.parseInt(searchInput);
                DAO.OrderDAO dao = new DAO.OrderDAO();
                
                // --- 1. POPULATE THE STAT CARDS ---
                int[] stats = dao.getEmployeeWorkloadStats(empId);
                workloadView.setWorkloadStats(
                    String.valueOf(stats[0]), // Total
                    String.valueOf(stats[1]), // Active (In-transit)
                    String.valueOf(stats[2]), // Delivered
                    String.valueOf(stats[3]), // Cancelled
                    String.valueOf(stats[4])  // Returned
                );
                
                // --- 2. POPULATE THE TABLE ---
                workloadView.clearTable(); // Clear old search results
                ResultSet rs = dao.getEmployeeAssignedOrdersList(empId);
                
                boolean hasOrders = false;
                if (rs != null) {
                    while (rs.next()) {
                        hasOrders = true;
                        String trackingId = rs.getString("tracking_id");
                        String receiver = rs.getString("receiver_name");
                        String location = rs.getString("receiver_location");
                        String status = rs.getString("status");
                        
                        // Assumes your table has 4 columns. Adjust if needed!
                        workloadView.addTableRow(new Object[]{trackingId, receiver, location, status});
                    }
                }
                
                if (!hasOrders && stats[0] == 0) {
                    JOptionPane.showMessageDialog(workloadView, "No orders found for Employee ID: " + empId);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(workloadView, "Employee ID must be a valid number.");
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(workloadView, "Database Error while fetching workload.");
            }
        }
    }
        class OpenDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the Manager Profile screen
            close(); 
            
            // 2. Route directly back to the Manager Dashboard
            view.Manager_Dashboard dashboardView = new view.Manager_Dashboard();
            
            // Assuming your controller for the manager dashboard is named ManagerController
            new controllor.ManagerController(dashboardView, currentUser).open();
        }
    }
        class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the Manager Profile screen
            close(); 
            
            // 2. Re-open the Login window securely
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
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
        class OpenActiveOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.Manager_active_orders activeorderView = new view.Manager_active_orders();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerActiveOrdersController managerAssignOrderController = new controllor.ManagerActiveOrdersController(activeorderView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerAssignOrderController.open();
        }
    }
}