package controllor;

import Model.userData;
import view.Admin_Dashboard;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.SwingUtilities;

public class AdminController {
    
    private final Admin_Dashboard adminView;
    private final userData currentUser;

    public AdminController(Admin_Dashboard adminView, userData currentUser) {
        this.adminView = adminView;
        this.currentUser = currentUser;
        
        // 1. Instantly set the Name and Role labels on the screen
        this.adminView.setUsernameLabel(currentUser.getUsername());
        this.adminView.setRoleLabel(currentUser.getRole());
        
        // 2. Load all dashboard data instantly (Metrics & Recent Orders Table)
        loadDashboardMetrics();
        loadRecentOrders();
        
        // 3. Connect navigation buttons
        this.adminView.addLogoutListener(new LogoutListener());
        this.adminView.addMyProfileListener(new MyProfileListener()); 
        
        // Note: Using your exact method name from your View class
        this.adminView.addManageOrdersistener(new ManageOrdersListener()); 
    }

    public void open() {
        this.adminView.setVisible(true);
        this.adminView.setLocationRelativeTo(null); // Centers the window
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.adminView);
        if (window != null) {
            window.dispose();
        } else {
            this.adminView.setVisible(false);
        }
    }

    // =========================================================================
    // DATA LOADING
    // =========================================================================

    private void loadDashboardMetrics() {
        DAO.OrderDAO oDao = new DAO.OrderDAO();
        DAO.userDAO uDao = new DAO.userDAO();
        
        // Fetch order stats
        int[] stats = oDao.getAdminDashboardStats();
        
        // Fetch employee stats
        int activeEmployees = uDao.getActiveEmployeeCount();
        
        // Inject into View
        adminView.setDashboardStats(
            String.valueOf(stats[0]), // Total
            String.valueOf(stats[1]), // Active / Intransit
            String.valueOf(stats[2]), // Pending
            String.valueOf(stats[3]), // Delivered
            String.valueOf(stats[4]), // Cancelled
            String.valueOf(stats[5]), // Returned
            String.valueOf(activeEmployees) // Active Employees
        );
    }

    private void loadRecentOrders() {
        DAO.OrderDAO dao = new DAO.OrderDAO();
        
        // Fetch the 10 most recent orders
        ResultSet rs = dao.getRecentOrders(10);
        
        adminView.clearRecentOrdersTable();
        
        try {
            if (rs != null) {
                while (rs.next()) {
                    String trackingId = rs.getString("tracking_id");
                    String receiver = rs.getString("receiver_name");
                    String contact = rs.getString("receiver_contact");
                    String status = rs.getString("status");
                    String cost = String.valueOf(rs.getDouble("total_cost"));
                    
                    // Add row to the UI table
                    adminView.addRecentOrderRow(new Object[]{trackingId, receiver, contact, status, cost});
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================

    // --- Action Listener for Logout Button ---
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the Admin dashboard
            close(); 
            
            // 2. Re-open the Login window safely
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
    
    class MyProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
    
            view.NewAdmin_Profile admin_profile = new view.NewAdmin_Profile();
            controllor.AdminProfileController ac = new controllor.AdminProfileController(admin_profile, currentUser);
            ac.open();
        }
    }
    
    class ManageOrdersListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
    
            view.AdminOrderEdit ManageOrder = new view.AdminOrderEdit();
            controllor.AdminManageOrderController moc = new controllor.AdminManageOrderController(ManageOrder, currentUser);
            moc.open();
        }
    }
}