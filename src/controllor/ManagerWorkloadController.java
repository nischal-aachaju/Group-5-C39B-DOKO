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
}