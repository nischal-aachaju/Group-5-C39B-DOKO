package controllor;

import Model.userData;
import DAO.OrderDAO;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import view.WorkLoad;

public class AdminWorkloadController {
    
    private WorkLoad workloadView;
    private final userData currentUser;

    public AdminWorkloadController(WorkLoad workloadView, userData currentUser) {
        this.workloadView = workloadView;
        this.currentUser = currentUser;
        
        // 1. Sync User Profile Tags
        this.workloadView.setUsernameLabel(currentUser.getUsername());
        this.workloadView.setRoleLabel(currentUser.getRole());
        
        // 2. Clear numbers to 0 on startup
        resetWorkloadLabels();
        
        // 3. Attach the Search Engine
        this.workloadView.addSearchListener(new SearchEmployeeWorkloadListener());
        
        // 4. Attach Navigation (Add your specific target pages to these later)
        this.workloadView.addLogoutListener(new LogoutListener());
        this.workloadView.addDashboardListener(new NavToDashboardListener());
        // this.workloadView.addManageOrderListener(...);
        // this.workloadView.addManageUserListener(...);
        // this.workloadView.addPriceConfigListener(...);
    }

    // =========================================================================
    // WINDOW MANAGEMENT (CRASH PREVENTION FIX)
    // =========================================================================
    public void open() {
        if (this.workloadView != null) {
            this.workloadView.setVisible(true);
            this.workloadView.setLocationRelativeTo(null);
        }
    }

    public void safeClose() {
        if (this.workloadView != null) {
            this.workloadView.setVisible(false);
            this.workloadView.dispose();
            this.workloadView = null; // Clears memory to prevent Ghost Windows
        }
    }

    private void resetWorkloadLabels() {
        workloadView.setTotalOrderNumbers("0");
        workloadView.setInTransitOrderNumber("0");
        workloadView.setDeliveryShipmentsNumber("0");
        workloadView.setCancelledOrdersNumber("0");
    }

    // =========================================================================
    // SEARCH ENGINE LOGIC
    // =========================================================================
    class SearchEmployeeWorkloadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            String rawInput = workloadView.getEmployeeIdInput();
            
            // Validate Input
            if (rawInput.isEmpty()) {
                JOptionPane.showMessageDialog(workloadView, "Please enter an Employee ID.", "Input Error", JOptionPane.WARNING_MESSAGE);
                resetWorkloadLabels();
                return;
            }
            
            try {
                int employeeIdToSearch = Integer.parseInt(rawInput);
                
                // Fetch the stats array from the database
                OrderDAO orderDao = new OrderDAO();
                int[] stats = orderDao.getEmployeeWorkloadStats(employeeIdToSearch);
                
                // Index Mapping from DAO: 
                // 0=Total, 1=Active(intransit), 2=Delivered, 3=Cancelled, 4=Returned
                
                // If total is 0, warn the admin that no data exists for this user
                if (stats[0] == 0) {
                    JOptionPane.showMessageDialog(workloadView, "No assignments found for Employee ID: " + employeeIdToSearch, "No Data", JOptionPane.INFORMATION_MESSAGE);
                    resetWorkloadLabels();
                    return;
                }
                
                // Populate the UI Labels dynamically
                workloadView.setTotalOrderNumbers(String.valueOf(stats[0]));
                workloadView.setInTransitOrderNumber(String.valueOf(stats[1]));
                workloadView.setDeliveryShipmentsNumber(String.valueOf(stats[2]));
                workloadView.setCancelledOrdersNumber(String.valueOf(stats[3]));
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(workloadView, "Invalid ID format. Please enter numbers only.", "Error", JOptionPane.ERROR_MESSAGE);
                resetWorkloadLabels();
            }
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            safeClose(); 
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }
    
    class NavToDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            safeClose();
            // Assuming your Admin dashboard is named Admin_Dashboard
             view.Admin_Dashboard dashView = new view.Admin_Dashboard();
             new controllor.AdminController(dashView, currentUser).open();
        }
    }
}