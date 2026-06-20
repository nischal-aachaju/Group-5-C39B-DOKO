/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class EmployeeOrderCancellationController {

    private final view.EmployeeOrderCancellation view;
    private final userData currentUser;
    private String currentTrackingId = null; // Remembers the searched order

    public EmployeeOrderCancellationController(view.EmployeeOrderCancellation view, userData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        
        // 1. Setup Identity and lock the form
        this.view.setTopBar(currentUser.getUsername(), currentUser.getRole());
        this.view.lockDisplayFields(); 
        this.view.setStatusButtonsEnabled(false); // Turn off buttons until they search!
        
        // 2. Connect Core Actions
        this.view.addSearchListener(new SearchOrderListener());
        
        // We reuse the same listener logic for all 3 status buttons, just passing the specific status!
        this.view.addCancelListener(new StatusUpdateListener("cancelled"));
        this.view.addReturnListener(new StatusUpdateListener("return"));
        this.view.addDeliveredListener(new StatusUpdateListener("delivered"));
        
        // 3. Connect Navigation
        this.view.addDashboardListener(new DashboardNavListener());
        this.view.addLogoutListener(new LogoutNavListener());
        this.view.addMyProfileListener(new OpenEmployeeProfileListener());
        this.view.addManageOrdersListener(new OpenManageOrdersListener());
        this.view.addOrdersHistoryListener(new OpenOrdersHistoryListener());
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
    // SEARCH ACTION
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
            
            // SECURITY: Ensure the employee only pulls data actively assigned to them
            ResultSet rs = dao.getEmployeeOrderByTrackingId(trackingId, currentUser.getUserID()); 
            
            try {
                if (rs != null && rs.next()) {
                    currentTrackingId = trackingId;
                    
                    // Inject data into the locked view fields
                    view.setOrderDetails(
                        trackingId,
                        rs.getString("receiver_name"),
                        rs.getString("receiver_email"),
                        rs.getString("street"),
                        rs.getString("receiver_location"),
                        String.valueOf(rs.getDouble("total_cost"))
                    );
                    
                    // Unlock the Cancel/Return/Delivered buttons now that we have a valid order!
                    view.setStatusButtonsEnabled(true); 
                    
                } else {
                    JOptionPane.showMessageDialog(view, "Order not found, or it is not currently assigned to you.", "Search Failed", JOptionPane.WARNING_MESSAGE);
                    currentTrackingId = null;
                    view.setStatusButtonsEnabled(false);
                }
            } catch (java.sql.SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

   // =========================================================================
    // STATUS UPDATE ACTION (Dynamic for Cancel/Return/Delivered)
    // =========================================================================

    class StatusUpdateListener implements ActionListener {
        private final String newStatus;

        // Constructor asks what status this specific button is supposed to set
        public StatusUpdateListener(String newStatus) {
            this.newStatus = newStatus;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (currentTrackingId == null) return;
            
            // 1. Ask for confirmation before changing the database
            int confirm = JOptionPane.showConfirmDialog(
                    view, 
                    "Are you sure you want to mark Order #" + currentTrackingId + " as " + newStatus.toUpperCase() + "?", 
                    "Confirm Status Update", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                DAO.OrderDAO dao = new DAO.OrderDAO();
                
                // Reusing the update method we built earlier!
                boolean success = dao.updateOrderStatus(currentTrackingId, newStatus);
                
                if (success) {
                    JOptionPane.showMessageDialog(view, "Order " + currentTrackingId + " successfully marked as " + newStatus.toUpperCase() + "!");
                    
                    // ====================================================================
                    // EMAIL TRIGGER: FETCH CUSTOMER INFO AND SEND STATUS UPDATE
                    // ====================================================================
                    final String safeTrackingId = currentTrackingId; // Save for the background thread
                    
                    try (java.sql.ResultSet rs = dao.getOrderByTrackingId(safeTrackingId)) {
                        if (rs != null && rs.next()) {
                            // Read the email and name from the database!
                            final String recName = rs.getString("receiver_name");
                            final String recEmail = rs.getString("receiver_email");
                            
                            // Grab Employee details
                            final String empName = currentUser.getUsername();
                            final String empPhone = currentUser.getPhone();
                            
                            // Fire the background thread so the app doesn't freeze!
                            new Thread(() -> {
                                try {
                                    controllor.EmailService.sendStatusUpdateEmail(
                                        recEmail, 
                                        recName, 
                                        safeTrackingId, 
                                        newStatus, 
                                        empName, 
                                        empPhone
                                    );
                                } catch (Exception ex) {
                                    System.out.println("Email failed to send: " + ex.getMessage());
                                }
                            }).start();
                        }
                    } catch (Exception ex) {
                        System.out.println("Could not fetch order details for email: " + ex.getMessage());
                    }
                    // ====================================================================
                    
                    // Reset the form so they don't accidentally click another button
                    view.setOrderDetails("", "", "", "", "", "");
                    currentTrackingId = null;
                    view.setStatusButtonsEnabled(false);
                    
                } else {
                    JOptionPane.showMessageDialog(view, "Failed to update database status.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    // ===============================================================
    // NAVIGATION
    // =========================================================================

    class DashboardNavListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
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
}