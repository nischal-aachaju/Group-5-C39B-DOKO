package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EmployeeOrderHistoryController {

    private final view.Employee_Order_History view; // Ensure this matches your JFrame file name!
    private final userData currentUser;

    public EmployeeOrderHistoryController(view.Employee_Order_History view, userData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        
        // 1. Setup Identity
        this.view.setTopBar(currentUser.getUsername(), currentUser.getRole());
        
        // 2. Load all assigned orders on startup
        loadTableData("All");
        
        // 3. Connect Actions
        this.view.addFilterListener(new FilterButtonListener());
        
        // 4. Connect Navigation (Add other dashboard routes here as you build them)
        this.view.addLogoutListener(new LogoutListener());
        // 4. Connect Navigation
        this.view.addDashboardListener(new DashboardListener()); // <-- ADD THIS LINE
        this.view.addLogoutListener(new LogoutListener());
    }

    public void open() {
        this.view.setVisible(true);
        this.view.setLocationRelativeTo(null);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.view);
        if (window != null) {
            window.dispose();
        } else {
            this.view.setVisible(false);
        }
    }

    // =========================================================================
    // LOAD TABLE DATA
    // =========================================================================

    private void loadTableData(String statusFilter) {
        DAO.OrderDAO dao = new DAO.OrderDAO();
        
        // SECURE: Pass the logged-in user's ID to the DAO so they only see their own orders
        ResultSet rs = dao.getEmployeeOrderHistory(currentUser.getUserID(), statusFilter);
        
        view.clearTable();
        
        try {
            if (rs != null) {
                while (rs.next()) {
                    // Match UI Columns: Tracking ID | Customer | Shipment | Status | Date | Price
                    String trackingId = rs.getString("tracking_id");
                    String customer = rs.getString("receiver_name");
                    String shipment = rs.getString("receiver_location");
                    String status = rs.getString("status");
                    String date = rs.getString("order_date"); // Extracts timestamp
                    String price = String.valueOf(rs.getDouble("total_cost"));
                    
                    view.addTableRow(new Object[]{trackingId, customer, shipment, status, date, price});
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(view, "Error loading order history.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // FILTER LOGIC
    // =========================================================================

    class FilterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] filterOptions = {"All", "pending", "intransit", "delivered", "cancelled", "return"};
            
            String selectedFilter = (String) JOptionPane.showInputDialog(
                    view,
                    "Select Order Status to filter by:",
                    "Filter Orders",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    filterOptions,
                    filterOptions[0]
            );
            
            if (selectedFilter != null) {
                loadTableData(selectedFilter);
            }
        }
    }

    // =========================================================================
    // NAVIGATION LOGIC
    // =========================================================================

    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            new controllor.LoginController(new view.login()).open();
        }
    }
    class DashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); // Close the current order history page
            
            // NOTE: Replace 'Employee_Dashboard' and 'EmployeeDashboardController' 
            // with the exact names of your Employee Dashboard files!
            view.Employee_Dashboard dashboardView = new view.Employee_Dashboard();
            controllor.EmployeeController dashboardController = new controllor.EmployeeController(dashboardView, currentUser);
            
            dashboardController.open();
        }
    }
}