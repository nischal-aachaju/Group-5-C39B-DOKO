package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class ManagerOrderEditController {

    private final view.ManagerOrderEdit view;
    private final userData currentUser;
    
    // Track the currently searched ID so we know what to save
    private String currentTrackingId = null; 

    public ManagerOrderEditController(view.ManagerOrderEdit view, userData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        
        // 1. Lock the form and disable the save button on startup
        this.view.setFormEditable(false);
        
        // 2. Connect core feature buttons
        this.view.addSearchListener(new SearchOrderListener());
        this.view.addEditListener(new EditOrderListener());
        this.view.addSaveListener(new SaveOrderListener());
        
        // 3. Connect navigation buttons
        this.view.addLogoutListener(new LogoutListener());
        this.view.addDashboardListener(new DashboardListener());
        this.view.addReturnOrderListener(new ReturnOrderListener()); // Assuming Return goes to dashboard
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
    // SEARCH, EDIT, AND SAVE LOGIC
    // =========================================================================

    class SearchOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String trackingId = view.getSearchInput();
            
            if (trackingId.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Please enter a Tracking ID to search.");
                return;
            }
            
            DAO.OrderDAO dao = new DAO.OrderDAO();
            ResultSet rs = dao.getOrderByTrackingId(trackingId);
            
            try {
                if (rs != null && rs.next()) {
                    currentTrackingId = trackingId; // Store it for saving later
                    
                    String name = rs.getString("receiver_name");
                    String email = rs.getString("receiver_email");
                    String contact = rs.getString("receiver_contact");
                    String senderAddr = rs.getString("street"); // Adjust if your sender address is a different column
                    String receiverAddr = rs.getString("receiver_location");
                    String cost = String.valueOf(rs.getDouble("total_cost"));
                    
                    // Inject into UI and lock it
                    view.setOrderDetails(name, email, contact, senderAddr, receiverAddr, cost);
                    view.setFormEditable(false);
                    
                } else {
                    JOptionPane.showMessageDialog(view, "No order found with Tracking ID: " + trackingId);
                    currentTrackingId = null;
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view, "Database Error during search.");
            }
        }
    }

    class EditOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId == null) {
                JOptionPane.showMessageDialog(view, "Please search for a valid order first before editing.");
                return;
            }
            // Unlock the allowed fields and turn on the Save button
            view.setFormEditable(true);
        }
    }

    class SaveOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId == null) { return; }
            
            String newName = view.getUpdatedName();
            String newContact = view.getUpdatedContact();
            String newAddress = view.getUpdatedAddress();
            String costString = view.getUpdatedCost();
            
            if (newName.isEmpty() || newAddress.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Name and Address fields cannot be empty.");
                return;
            }
            
            try {
                double newCost = Double.parseDouble(costString);
                
                DAO.OrderDAO dao = new DAO.OrderDAO();
                boolean success = dao.updateOrderDetails(currentTrackingId, newName, newContact, newAddress, newCost);
                
                if (success) {
                    JOptionPane.showMessageDialog(view, "Order " + currentTrackingId + " successfully updated!");
                    view.setFormEditable(false); // Lock it back down after saving
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update database.");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(view, "Total Cost must be a valid number.");
            }
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
    // =========================================================================
    // RETURN ORDER STATUS LOGIC
    // =========================================================================

    class ReturnOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Make sure they actually searched for an order first
            if (currentTrackingId == null) {
                JOptionPane.showMessageDialog(view, "Please search for an order first.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // 2. Ask for confirmation before changing the database
            int confirm = JOptionPane.showConfirmDialog(
                    view, 
                    "Are you sure you want to mark Order #" + currentTrackingId + " as 'return'?", 
                    "Confirm Return", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            
            // 3. If they click YES, update the database
            if (confirm == JOptionPane.YES_OPTION) {
                DAO.OrderDAO dao = new DAO.OrderDAO();
                
                // Update the status column strictly to "return"
                boolean success = dao.updateOrderStatus(currentTrackingId, "return");
                
                if (success) {
                    JOptionPane.showMessageDialog(view, "Order " + currentTrackingId + " successfully marked as RETURNED.");
                    view.setFormEditable(false); // Lock the form
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update database status.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}