package controllor;

import Model.userData;
import Model.Order;
import view.Sender_Dashboard; 
import view.Sender_profile;
import view.OrderSubmissionForm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class UserController {
    
    private Sender_Dashboard userView;         
    private OrderSubmissionForm orderSubmission; 
    private final userData currentUser;        
    private String currentTrackingId;
    
    // =========================================================================
    // CONSTRUCTOR 1: Handles the Main Dashboard
    // =========================================================================
    public UserController(Sender_Dashboard userView, userData currentUser) {
        this.userView = userView;
        this.currentUser = currentUser;
        
        this.userView.setUsernameLabel(currentUser.getUsername());
        this.userView.setRoleLabel(currentUser.getRole());
        
        this.userView.addLogoutListener(new LogoutListener());
        this.userView.addMyProfileListener(new OpenProfileListener());
        this.userView.addCreateOrderListener(new OpenCreateOrderListener());
        this.userView.addMyShipmentsListener(new OpenMyShipmentsListener());
        this.userView.addOrdersHistoryListener(new OpenOrdersHistoryListener());
        
        DAO.OrderDAO OrderDao = new DAO.OrderDAO();
        java.util.Map<String, Integer> stats = OrderDao.getDashboardStats(currentUser.getUserID());
        
        java.util.List<Model.Order> recentOrders = OrderDao.getRecent5OrdersBySender(currentUser.getUserID());
        this.userView.populateRecentOrdersTable(recentOrders);
        
        this.userView.updateDashboardStats(
            stats.getOrDefault("total", 0),
            stats.getOrDefault("pending", 0),
            stats.getOrDefault("cancelled", 0),
            stats.getOrDefault("delivered", 0),
            stats.getOrDefault("intransit", 0),
            stats.getOrDefault("return", 0)
        );
    }

    // =========================================================================
    // CONSTRUCTOR 2: Handles direct navigation to the Order Form
    // =========================================================================
    public UserController(OrderSubmissionForm orderView, userData currentUser) {
        this.orderSubmission = orderView;
        this.currentUser = currentUser;
        
        this.orderSubmission.setUsernameLabel(currentUser.getUsername());
        this.orderSubmission.setRoleLabel(currentUser.getRole());
        
        // --- ADDED: Populate Branch Dropdown on Startup ---
        DAO.BranchDAO bDao = new DAO.BranchDAO();
        this.orderSubmission.populateBranchDropdown(bDao.getAllBranches());
        // --------------------------------------------------
        
        DAO.OrderDAO orderDao = new DAO.OrderDAO();
        boolean isUnique = false;
        java.util.Random rand = new java.util.Random();
        do {
            int randomNum = 100000 + rand.nextInt(900000); 
            currentTrackingId = String.valueOf(randomNum);
            isUnique = !orderDao.isTrackingIdExists(currentTrackingId);
        } while (!isUnique);
        
        this.orderSubmission.setInitialTrackingId(currentTrackingId);
        
        this.orderSubmission.addSubmitListener(new SubmitOrderListener());
        this.orderSubmission.addDashboardListener(new BackToDashboardFromOrder());
        this.orderSubmission.addLogoutListener(new LogoutFromOrderListener());
        this.orderSubmission.addMyProfileListener(new NavigateToProfileFromOrder());
        this.orderSubmission.addOrdersHistoryListener(new NavigateToHistoryFromOrder());
        this.orderSubmission.addMyShipmentsListener(new NavigateToShipmentsFromOrder()); 
    }

    // =========================================================================
    // BULLETPROOF WINDOW MANAGEMENT LOGIC
    // =========================================================================

    public void open() {
        if (this.userView != null) {
            this.userView.setVisible(true);
            this.userView.setLocationRelativeTo(null);
        } else if (this.orderSubmission != null) {
            this.orderSubmission.setVisible(true);
            this.orderSubmission.setLocationRelativeTo(null);
        }
    }

    public void close() {
        if (this.userView != null) {
            this.userView.dispose();
        } else if (this.orderSubmission != null) {
            this.orderSubmission.dispose();
        }
    }
    
    private void closeSubWindow(JFrame frame) {
        if (frame != null) {
            frame.dispose();
        }
    }

    // =========================================================================
    // DASHBOARD NAVIGATION LISTENERS
    // =========================================================================

    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }
    
    class OpenProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Sender_profile profileView = new view.Sender_profile();
            new controllor.ProfileController(profileView, currentUser).open();
        }
    }

    class OpenCreateOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            orderSubmission = new view.OrderSubmissionForm();
            new controllor.UserController(orderSubmission, currentUser).open();
        }
    }

    class OpenMyShipmentsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.SenderOrderCancellation shipmentView = new view.SenderOrderCancellation();
            new controllor.Sender_shipment_controller(shipmentView, currentUser).open();
        }
    }

    // =========================================================================
    // ORDER SUBMISSION FORM LISTENERS
    // =========================================================================

    class SubmitOrderListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            
            // --- ADDED: Grab the selected Branch ID before checking anything else ---
            int selectedBranchId = orderSubmission.getSelectedBranchId();
            if (selectedBranchId == -1) {
                JOptionPane.showMessageDialog(orderSubmission, "Please select a Drop-off Branch!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // ------------------------------------------------------------------------

            String name = orderSubmission.getReceiverNameInput();
            String email = orderSubmission.getReceiverEmailInput();
            String contact = orderSubmission.getReceiverContactInput();
            String location = orderSubmission.getReceiverLocationInput();
            String street = orderSubmission.getStreetInput();
            String description = orderSubmission.getDescriptionInput();
            double weight = orderSubmission.getWeightInput();
            double cost = orderSubmission.getTotalCostInput();

            if (name.isEmpty() || contact.isEmpty() || location.isEmpty() || weight <= 0.0) {
                JOptionPane.showMessageDialog(orderSubmission, "Please fill in all required fields!");
                return;
            }

            Model.Order newOrder = new Model.Order(currentTrackingId, name, email, contact, location, street, weight, cost, description);
            DAO.OrderDAO orderDao = new DAO.OrderDAO();
            
            // If the order successfully saves to the database...
            if (orderDao.saveOrder(newOrder, currentUser.getUserID())) {
                
                // --- ADDED: Instantly link this new order to the selected branch ---
                DAO.BranchDAO branchDao = new DAO.BranchDAO();
                branchDao.assignOrderToBranch(selectedBranchId, currentTrackingId);
                // -------------------------------------------------------------------
                
                orderSubmission.updateBillSection(newOrder); 
                JOptionPane.showMessageDialog(orderSubmission, "Order Placed and Assigned Successfully!\nTracking ID: " + currentTrackingId);
                orderSubmission.clearOrderForm(); 
                
                boolean isNewUnique = false;
                java.util.Random rand = new java.util.Random();
                do {
                    int randomNum = 100000 + rand.nextInt(900000); 
                    currentTrackingId = String.valueOf(randomNum);
                    isNewUnique = !orderDao.isTrackingIdExists(currentTrackingId);
                } while (!isNewUnique);

                orderSubmission.setInitialTrackingId(currentTrackingId);
                
            } else {
                JOptionPane.showMessageDialog(orderSubmission, "Database Error: Could not save order records.");
            }
        }
    }

    class BackToDashboardFromOrder implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeSubWindow(orderSubmission); 
            view.Sender_Dashboard newDashboard = new view.Sender_Dashboard();
            new controllor.UserController(newDashboard, currentUser).open(); 
        }
    }

    class LogoutFromOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeSubWindow(orderSubmission); 
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }

    class NavigateToProfileFromOrder implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            closeSubWindow(orderSubmission); 
            view.Sender_profile profileView = new view.Sender_profile();
            new controllor.ProfileController(profileView, currentUser).open();
        }
    }

    class NavigateToShipmentsFromOrder implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeSubWindow(orderSubmission); 
            view.SenderOrderCancellation shipmentsView = new view.SenderOrderCancellation();
            new controllor.Sender_shipment_controller(shipmentsView, currentUser).open();
        }
    }
    
    class OpenOrdersHistoryListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Sender_Order_History historyView = new view.Sender_Order_History();
            new controllor.HistoryController(historyView, currentUser).open();
        }
    }
    
    class NavigateToHistoryFromOrder implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeSubWindow(orderSubmission); 
            view.Sender_Order_History historyView = new view.Sender_Order_History();
            new controllor.HistoryController(historyView, currentUser).open();
        }
    }
}