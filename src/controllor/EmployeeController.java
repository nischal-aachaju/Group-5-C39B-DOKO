package controllor;

import Model.userData;
import view.Employee_Dashboard; 

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.SwingUtilities;

public class EmployeeController {
    
    private final Employee_Dashboard employeeView;
    private final userData currentUser;

    public EmployeeController(Employee_Dashboard employeeView, userData currentUser) {
        this.employeeView = employeeView;
        this.currentUser = currentUser;
        
        // 1. Instantly set the Name and Role labels on the screen
        this.employeeView.setUsernameLabel(currentUser.getUsername());
        this.employeeView.setRoleLabel(currentUser.getRole());
        
        // 2. Load the live database metrics and table data instantly
        loadDashboardMetrics();
        loadRecentOrders();
        
        // 3. Connect the navigation buttons
        this.employeeView.addLogoutListener(new LogoutListener());
        this.employeeView.addMyShipmentsListener(new MyShipmentListener());
        this.employeeView.addMyProfileListener(new OpenEmployeeProfileListener());
        this.employeeView.addManageOrdersListener(new OpenManageOrdersListener());
        this.employeeView.addOrdersHistoryListener(new OpenOrdersHistoryListener());
    }

    public void open() {
        this.employeeView.setVisible(true);
        this.employeeView.setLocationRelativeTo(null); // Centers window safely
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.employeeView);
        if (window != null) {
            window.dispose();
        } else {
            this.employeeView.setVisible(false);
        }
    }

    // =========================================================================
    // DATA LOADING LOGIC
    // =========================================================================

    private void loadDashboardMetrics() {
        DAO.OrderDAO dao = new DAO.OrderDAO();
        
        // Fetch stats specifically for this employee
        int[] stats = dao.getEmployeeDashboardStats(currentUser.getUserID());
        
        // Inject into the View (Total, Active, Delivered, Returned, Cancelled)
        employeeView.setDashboardStats(
            String.valueOf(stats[0]), 
            String.valueOf(stats[1]), 
            String.valueOf(stats[2]), 
            String.valueOf(stats[3]), 
            String.valueOf(stats[4])  
        );
    }

    private void loadRecentOrders() {
        DAO.OrderDAO dao = new DAO.OrderDAO();
        
        // Fetch the 5 most recent assigned orders
        ResultSet rs = dao.getRecentEmployeeOrders(currentUser.getUserID(), 5);
        
        employeeView.clearRecentOrdersTable();
        
        try {
            if (rs != null) {
                while (rs.next()) {
                    // Match UI Columns: Tracking ID | Customer | Contact | Destination | Status
                    String trackingId = rs.getString("tracking_id");
                    String customer = rs.getString("receiver_name");
                    String contact = rs.getString("receiver_contact");
                    String destination = rs.getString("receiver_location");
                    String status = rs.getString("status");
                    
                    employeeView.addRecentOrderRow(new Object[]{trackingId, customer, contact, destination, status});
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
    
    class OpenEmployeeProfileListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Employee_profile profileView = new view.Employee_profile();
            controllor.Employee_ProfileController profileController = new controllor.Employee_ProfileController(profileView, currentUser);
            profileController.open();
        }
    }
    
    class OpenOrdersHistoryListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Employee_Order_History profileView = new view.Employee_Order_History();
            controllor.EmployeeOrderHistoryController profileController = new controllor.EmployeeOrderHistoryController(profileView, currentUser);
            profileController.open();
        }
    }
    
    class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.EmployeeOrderEdit profileView = new view.EmployeeOrderEdit();
            controllor.EmployeeOrderEditController profileController = new controllor.EmployeeOrderEditController(profileView, currentUser);
            profileController.open();
        }
    }
    
    class MyShipmentListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.EmployeeOrderCancellation EmployeeOrder = new view.EmployeeOrderCancellation();
            controllor.EmployeeOrderCancellationController EmployeeOrderController = new controllor.EmployeeOrderCancellationController(EmployeeOrder, currentUser);
            EmployeeOrderController.open();
        }
    }
}