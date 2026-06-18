package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EmployeeOrderEditController {

    private final view.EmployeeOrderEdit view; // Ensure this matches your JFrame file name!
    private final userData currentUser;
    private String currentTrackingId = null; 

    public EmployeeOrderEditController(view.EmployeeOrderEdit view, userData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        
        // 1. Set Identity and lock the form
        this.view.setTopBar(currentUser.getUsername(), currentUser.getRole());
        this.view.setFormEditable(false); 
        
        // 2. Connect Actions
        this.view.addSearchListener(new SearchOrderListener());
        this.view.addEditListener(new EditOrderListener());
        this.view.addSaveListener(new SaveOrderListener());
        
        // 3. Connect Navigation
        this.view.addDashboardListener(new DashboardNavListener());
        this.view.addLogoutListener(new LogoutNavListener());
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
    // CRUD ACTIONS
    // =========================================================================

    class SearchOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String trackingId = view.getSearchInput();
            
            if (trackingId.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please enter a Tracking ID.");
                return;
            }
            
            DAO.OrderDAO dao = new DAO.OrderDAO();
            
            // SECURITY: Pass the logged-in user ID to ensure they own the order
            ResultSet rs = dao.getEmployeeOrderByTrackingId(trackingId, currentUser.getUserID()); 
            
            try {
                if (rs != null && rs.next()) {
                    currentTrackingId = trackingId;
                    
                    view.setOrderDetails(
                        trackingId,
                        rs.getString("receiver_name"),
                        rs.getString("receiver_email"),
                        rs.getString("street"),
                        rs.getString("receiver_location"),
                        String.valueOf(rs.getDouble("total_cost"))
                    );
                    
                    view.setFormEditable(false); // Lock it until they click 'Edit'
                } else {
                    JOptionPane.showMessageDialog(view, "Order not found, or it is not assigned to you.");
                    currentTrackingId = null;
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    class EditOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId != null) {
                view.setFormEditable(true); // Unlocks Name, Email, Address, and Cost
            } else {
                JOptionPane.showMessageDialog(view, "Search for an order first.");
            }
        }
    }

    class SaveOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId == null) return;
            
            String newName = view.getUpdatedName();
            String newEmail = view.getUpdatedEmail();
            String newAddress = view.getUpdatedAddress();
            
            if (newName.isEmpty() || newAddress.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Name and Address cannot be empty.");
                return;
            }
            
            try {
                double newCost = Double.parseDouble(view.getUpdatedCost());
                DAO.OrderDAO dao = new DAO.OrderDAO();
                
                if (dao.employeeUpdateOrderDetails(currentTrackingId, newName, newEmail, newAddress, newCost)) {
                    JOptionPane.showMessageDialog(view, "Order details successfully updated!");
                    view.setFormEditable(false); // Lock back down after saving
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update database.");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Total Cost must be a valid number.");
            }
        }
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    class DashboardNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            // Note: Change these names if your files are named differently!
            view.Employee_Dashboard dashView = new view.Employee_Dashboard();
            new controllor.EmployeeController(dashView, currentUser).open();
        }
    }

    class LogoutNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            new controllor.LoginController(new view.login()).open();
        }
    }
}