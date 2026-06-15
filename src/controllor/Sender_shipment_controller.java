package controllor;

import Model.userData;
import view.SenderOrderCancellation;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class Sender_shipment_controller {
    
    private final SenderOrderCancellation shipmentView;
    private final userData currentUser;

    public Sender_shipment_controller(SenderOrderCancellation shipmentView, userData currentUser) {
        this.shipmentView = shipmentView;
        this.currentUser = currentUser;
        
        // 1. Sync the top-right corner labels instantly
        this.shipmentView.setUsernameLabel(currentUser.getUsername());
        this.shipmentView.setRoleLabel(currentUser.getRole());
        
        // 2. Connect the navigation buttons
        this.shipmentView.addDashboardListener(new BackToDashboardListener());
        this.shipmentView.addLogoutListener(new LogoutListener());
        this.shipmentView.addCreateOrderListener(new CreateOrderListener());
        this.shipmentView.addMyProfileListener(new NavigateToProfileFromShipments());
        // You will add your Search, Edit, and Cancel button listeners here later!
    
        this.shipmentView.addSearchListener(new SearchOrderListener());

        
        // --- ADD THIS NEW LINE ---
        this.shipmentView.addCancelOrderListener(new CancelOrderListener());
    
    }

    public void open() {
        this.shipmentView.setVisible(true);
    }

public void close() {
        if (this.shipmentView != null) {
            this.shipmentView.dispose(); // This permanently destroys the My Shipments window!
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================

    class BackToDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); // Close the Shipments page
            
            // Re-open the main Dashboard
            view.Sender_Dashboard dashboardView = new view.Sender_Dashboard();
            controllor.UserController dashboardController = new controllor.UserController(dashboardView, currentUser);
            dashboardController.open();
        }
    }

    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }

    class CreateOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the current My Shipments window
            close(); 
            
            // 2. Create the Create Order View
            view.OrderSubmissionForm orderView = new view.OrderSubmissionForm();
            
            // 3. Pass the view AND the current user to the Create Order Controller
            controllor.UserController orderController = new controllor.UserController(orderView, currentUser);
            
            // 4. Open the new page
            orderController.open();
        }
    }
    class NavigateToProfileFromShipments implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); // Closes the Shipments window completely
            
            view.Sender_profile profileView = new view.Sender_profile();
            new controllor.ProfileController(profileView, currentUser).open();
        }
    }
//class SearchOrderListener implements ActionListener {
//        @Override
//        public void actionPerformed(ActionEvent e) {
//            
//            String rawSearchInput = shipmentView.getSearchTrackingId();
//            
//            // SUPER CLEANER: Strips out EVERYTHING except the numbers!
//            // If they type "# Enter Tracking ID 253893 ", it perfectly extracts "253893"
//            String cleanTrackingId = rawSearchInput.replaceAll("[^0-9]", "");
//
//            if (cleanTrackingId.isEmpty()) {
//                javax.swing.JOptionPane.showMessageDialog(shipmentView, "Please enter a valid numeric Tracking ID.");
//                return;
//            }
//            
//            if (cleanTrackingId.length() !=6){
//            javax.swing.JOptionPane.showMessageDialog(shipmentView, "Please enter a valid numeric Tracking ID of 6 digit");
//                return;
//            }
//
//            // --- CONSOLE DEBUGGING ---
//            System.out.println("=== SEARCH INITIATED ===");
//            System.out.println("Searching Tracking ID: [" + cleanTrackingId + "]");
//            System.out.println("Logged-in Sender ID: [" + currentUser.getUserID() + "]");
//
//            DAO.OrderDAO orderDao = new DAO.OrderDAO();
//            Model.Order foundOrder = orderDao.getOrderByTrackingIdAndSender(cleanTrackingId, currentUser.getUserID()); 
//
//            if (foundOrder != null) {
//                System.out.println("Match Found! Populating UI...");
//                shipmentView.populateOrderDetails(foundOrder, currentUser);
//            } else {
//                System.out.println("No Match Found in Database.");
//                javax.swing.JOptionPane.showMessageDialog(shipmentView, "Order not found! Please check the Tracking ID.");
//            }
//        }
//    }
class CancelOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            // 1. Check if there is actually an order loaded on the screen
            String loadedTrackingId = shipmentView.getLoadedTrackingId();
            
            if (loadedTrackingId == null || loadedTrackingId.trim().isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(shipmentView, 
                        "Please search for an order to cancel first.", 
                        "No Order Selected", 
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. The Yes/No Double-Check Popup
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                    shipmentView, 
                    "Are you absolutely sure you want to cancel Order #" + loadedTrackingId + "?", 
                    "Confirm Cancellation", 
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE
            );

            // 3. Process the Cancellation if they clicked "Yes"
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                
                DAO.OrderDAO orderDao = new DAO.OrderDAO();
                boolean success = orderDao.cancelOrder(loadedTrackingId, currentUser.getUserID());
                
                if (success) {
                    javax.swing.JOptionPane.showMessageDialog(shipmentView, 
                            "Success! Order #" + loadedTrackingId + " has been cancelled.", 
                            "Order Cancelled", 
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                            
                    // Clear the screen so they know it's gone
                    shipmentView.clearOrderDetails(); 
                    
                } else {
                    javax.swing.JOptionPane.showMessageDialog(shipmentView, 
                            "Failed to cancel the order. It may have already been shipped.", 
                            "Error", 
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

class SearchOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            String rawSearchInput = shipmentView.getSearchTrackingId();
            String cleanTrackingId = rawSearchInput.replaceAll("[^0-9]", "");

            if (cleanTrackingId.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(shipmentView, "Please enter a valid numeric Tracking ID.");
                return;
            }

            DAO.OrderDAO orderDao = new DAO.OrderDAO();
            Model.Order foundOrder = orderDao.getOrderByTrackingIdAndSender(cleanTrackingId, currentUser.getUserID()); 

            if (foundOrder != null) {
                
                // --- NEW LOGIC: Check if the order is cancelled! ---
                if ("cancelled".equalsIgnoreCase(foundOrder.getStatus())) {
                    
                    javax.swing.JOptionPane.showMessageDialog(shipmentView, 
                            "Order #" + cleanTrackingId + " has been cancelled.", 
                            "Order Cancelled", 
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                            
                    // Clear the screen so they don't see old data
                    shipmentView.clearOrderDetails(); 
                    
                } else {
                    // If it is NOT cancelled (e.g., "pending"), load the data normally!
                    shipmentView.populateOrderDetails(foundOrder, currentUser);
                }
                
            } else {
                javax.swing.JOptionPane.showMessageDialog(shipmentView, "Order not found! Please check the Tracking ID.");
            }
        }
    }
}