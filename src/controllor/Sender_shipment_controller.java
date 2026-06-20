package controllor;

import Model.userData;
import view.SenderOrderCancellation;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

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
        this.shipmentView.addOrdersHistoryListener(new NavigateToHistoryFromShipments());
    
        this.shipmentView.addSearchListener(new SearchOrderListener());
        this.shipmentView.addCancelOrderListener(new CancelOrderListener());
    }

    public void open() {
        this.shipmentView.setVisible(true);
        this.shipmentView.setLocationRelativeTo(null);
    }

    public void close() {
        if (this.shipmentView != null) {
            this.shipmentView.dispose(); 
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================

    class BackToDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
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
            close(); 
            view.OrderSubmissionForm orderView = new view.OrderSubmissionForm();
            controllor.UserController orderController = new controllor.UserController(orderView, currentUser);
            orderController.open();
        }
    }
    
    class NavigateToProfileFromShipments implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Sender_profile profileView = new view.Sender_profile();
            new controllor.ProfileController(profileView, currentUser).open();
        }
    }
    
    class NavigateToHistoryFromShipments implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Sender_Order_History historyView = new view.Sender_Order_History();
            new controllor.HistoryController(historyView, currentUser).open();
        }
    }

    // =========================================================================
    // CANCEL & SEARCH LISTENERS
    // =========================================================================

    class CancelOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            
            String loadedTrackingId = shipmentView.getLoadedTrackingId();
            
            if (loadedTrackingId == null || loadedTrackingId.trim().isEmpty()) {
                JOptionPane.showMessageDialog(shipmentView, 
                        "Please search for an order to cancel first.", 
                        "No Order Selected", 
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    shipmentView, 
                    "Are you absolutely sure you want to cancel Order #" + loadedTrackingId + "?", 
                    "Confirm Cancellation", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                DAO.OrderDAO orderDao = new DAO.OrderDAO();
                
                // ====================================================================
                // NEW: FETCH THE ORDER DETAILS FIRST (We need the receiver's email!)
                // ====================================================================
                Model.Order orderToCancel = orderDao.getOrderByTrackingIdAndSender(loadedTrackingId, currentUser.getUserID());
                
                if (orderToCancel == null) {
                    JOptionPane.showMessageDialog(shipmentView, "Order not found or you don't have permission to cancel it.");
                    return;
                }
                
                // Execute the database update
                boolean success = orderDao.cancelOrder(loadedTrackingId, currentUser.getUserID());
                
                if (success) {
                    JOptionPane.showMessageDialog(shipmentView, 
                            "Success! Order #" + loadedTrackingId + " has been cancelled.", 
                            "Order Cancelled", 
                            JOptionPane.INFORMATION_MESSAGE);
                            
                    // ====================================================================
                    // NEW: SEND CANCELLATION EMAIL IN BACKGROUND
                    // ====================================================================
                    final String recEmail = orderToCancel.getReceiverEmail();
                    final String recName = orderToCancel.getReceiverName();
                    final String safeTrackingId = loadedTrackingId;
                    
                    new Thread(() -> {
                        try {
                            // Passing nulls for employee name/phone because a driver isn't assigned to a cancelled order
                            controllor.EmailService.sendStatusUpdateEmail(
                                recEmail, 
                                recName, 
                                safeTrackingId, 
                                "cancelled", 
                                null, 
                                null
                            );
                        } catch (Exception ex) {
                            System.out.println("Email failed to send: " + ex.getMessage());
                        }
                    }).start();
                    // ====================================================================
                            
                    shipmentView.clearOrderDetails(); 
                    
                } else {
                    JOptionPane.showMessageDialog(shipmentView, 
                            "Failed to cancel the order. It may have already been shipped.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(shipmentView, "Please enter a valid numeric Tracking ID.");
                return;
            }

            DAO.OrderDAO orderDao = new DAO.OrderDAO();
            Model.Order foundOrder = orderDao.getOrderByTrackingIdAndSender(cleanTrackingId, currentUser.getUserID()); 

            if (foundOrder != null) {
                if ("cancelled".equalsIgnoreCase(foundOrder.getStatus())) {
                    JOptionPane.showMessageDialog(shipmentView, 
                            "Order #" + cleanTrackingId + " has been cancelled.", 
                            "Order Cancelled", 
                            JOptionPane.WARNING_MESSAGE);
                            
                    shipmentView.clearOrderDetails(); 
                    
                } else {
                    shipmentView.populateOrderDetails(foundOrder, currentUser);
                }
            } else {
                JOptionPane.showMessageDialog(shipmentView, "Order not found! Please check the Tracking ID.");
            }
        }
    }
}