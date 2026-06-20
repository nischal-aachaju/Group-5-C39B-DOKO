package controllor;

import Model.userData;
import view.Manager_Dashboard;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.SwingUtilities;

public class ManagerController {
    
    private final Manager_Dashboard managerView;
    private final userData currentUser;

    public ManagerController(Manager_Dashboard managerView, userData currentUser) {
        this.managerView = managerView;
        this.currentUser = currentUser;
        
        // 1. Instantly set the Name and Role labels on the screen
        this.managerView.setUsernameLabel(currentUser.getUsername());
        this.managerView.setRoleLabel(currentUser.getRole());
        
        // 2. Load the live database metrics and table data instantly
        loadDashboardMetrics();
        loadRecentOrders();
        
        // 3. Connect the navigation buttons
        this.managerView.addLogoutListener(new LogoutListener());
        this.managerView.addMyProfileListener(new OpenManagerProfileListener());
        this.managerView.addManageUserListener(new OpenManageUserListener());
        this.managerView.addManageOrdersListener(new OpenManageOrdersListener());
        this.managerView.addAssiggnedOrdersListener(new OpenAssiggnedrdersListener());
        this.managerView.addActiveOrdersListener(new OpenActiveOrdersListener());
        this.managerView.addWorkloadListener(new OpenWorkloadListener());
    }

    public void open() {
        this.managerView.setVisible(true);
        this.managerView.setLocationRelativeTo(null); // Centers window
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.managerView);
        if (window != null) {
            window.dispose();
        } else {
            this.managerView.setVisible(false);
        }
    }

    // =========================================================================
    // DATA LOADING LOGIC (NEW)
    // =========================================================================

    private void loadDashboardMetrics() {
        DAO.OrderDAO oDao = new DAO.OrderDAO();
        DAO.userDAO uDao = new DAO.userDAO();
        
        // Fetch order stats (Reusing the query we built for Admin!)
        int[] stats = oDao.getAdminDashboardStats(); 
        
        // Fetch employee stats
        int activeEmployees = uDao.getActiveEmployeeCount();
        
        // Inject into the View (Total, Active/In-transit, Pending, Delivered, Active Employees)
        managerView.setDashboardStats(
            String.valueOf(stats[0]), // Total
            String.valueOf(stats[1]), // Active (Intransit)
            String.valueOf(stats[2]), // Pending
            String.valueOf(stats[3]), // Delivered
            String.valueOf(activeEmployees) // Employees
        );
    }

    private void loadRecentOrders() {
        DAO.OrderDAO dao = new DAO.OrderDAO();
        
        // Fetch the 5 most recent orders
        ResultSet rs = dao.getRecentOrders(5);
        
        managerView.clearRecentOrdersTable();
        
        try {
            if (rs != null) {
                while (rs.next()) {
                    String trackingId = rs.getString("tracking_id");
                    String receiver = rs.getString("receiver_name");
                    String contact = rs.getString("receiver_contact");
                    String status = rs.getString("status");
                    
                    // Adjust this array based on how many columns your 'OrderTable' actually has!
                    managerView.addRecentOrderRow(new Object[]{trackingId, receiver, contact, status});
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================

    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
    
    class OpenManagerProfileListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Manager_profileEdit profileView = new view.Manager_profileEdit();
            controllor.Manager_ProfileController profileController = new controllor.Manager_ProfileController(profileView, currentUser);
            profileController.open();
        }
    }

    class OpenManageUserListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Useraccountmanagement manageUserView = new view.Useraccountmanagement();
            controllor.ManageUserController manageUserController = new controllor.ManageUserController(manageUserView, currentUser);
            manageUserController.open();
        }
    }

    class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.ManagerOrderEdit managerOrderEditView = new view.ManagerOrderEdit();
            controllor.ManagerOrderEditController managerOrderEditController = new controllor.ManagerOrderEditController(managerOrderEditView, currentUser);
            managerOrderEditController.open();
        }
    }

    class OpenAssiggnedrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.assignedorder assignedorderView = new view.assignedorder();
            controllor.ManagerAssignOrderController managerAssignOrderController = new controllor.ManagerAssignOrderController(assignedorderView, currentUser);
            managerAssignOrderController.open();
        }
    }

    class OpenActiveOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Manager_active_orders activeorderView = new view.Manager_active_orders();
            controllor.ManagerActiveOrdersController managerAssignOrderController = new controllor.ManagerActiveOrdersController(activeorderView, currentUser);
            managerAssignOrderController.open();
        }
    }

    class OpenWorkloadListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Manager_Workload WorkloadView = new view.Manager_Workload();
            controllor.ManagerWorkloadController managerAssignOrderController = new controllor.ManagerWorkloadController(WorkloadView, currentUser);
            managerAssignOrderController.open();
        }
    }
}