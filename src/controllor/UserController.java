package controllor;

import Model.userData;
import Model.Order;
import view.Sender_Dashboard; 
import view.Sender_profile;
import view.OrderSubmissionForm;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
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
        
        // 1. Set the Name and Role labels on the dashboard
        this.userView.setUsernameLabel(currentUser.getUsername());
        this.userView.setRoleLabel(currentUser.getRole());
        
        // 2. Connect the dashboard sidebar buttons
        this.userView.addLogoutListener(new LogoutListener());
        this.userView.addMyProfileListener(new OpenProfileListener());
        this.userView.addCreateOrderListener(new OpenCreateOrderListener());
    }

    // =========================================================================
    // CONSTRUCTOR 2: Handles direct navigation to the Order Form
    // =========================================================================
    public UserController(OrderSubmissionForm orderView, userData currentUser) {
        this.orderSubmission = orderView;
        this.currentUser = currentUser;
        
        // Sync top bar labels
        this.orderSubmission.setUsernameLabel(currentUser.getUsername());
        this.orderSubmission.setRoleLabel(currentUser.getRole());
        
        // Generate Tracking ID immediately on load
        DAO.OrderDAO orderDao = new DAO.OrderDAO();
        boolean isUnique = false;
        java.util.Random rand = new java.util.Random();
        do {
            int randomNum = 100000 + rand.nextInt(900000); 
            currentTrackingId = String.valueOf(randomNum);
            isUnique = !orderDao.isTrackingIdExists(currentTrackingId);
        } while (!isUnique);
        
        this.orderSubmission.setInitialTrackingId(currentTrackingId);
        
        // Wire up the Order Form listeners
        this.orderSubmission.addSubmitListener(new SubmitOrderListener());
        this.orderSubmission.addDashboardListener(new BackToDashboardFromOrder());
        this.orderSubmission.addLogoutListener(new LogoutFromOrderListener());
        this.orderSubmission.addMyProfileListener(new NavigateToProfileFromOrder());
    }

    // =========================================================================
    // WINDOW MANAGEMENT LOGIC
    // =========================================================================

    public void open() {
        // Smart open: checks which view was handed to the controller and opens it
        if (this.userView != null) {
            this.userView.setVisible(true);
        } else if (this.orderSubmission != null) {
            this.orderSubmission.setVisible(true);
        }
    }

    public void close() {
        if (this.userView != null) {
            Window window = SwingUtilities.getWindowAncestor(this.userView);
            if (window != null) {
                window.dispose();
            } else {
                this.userView.setVisible(false);
            }
        }
    }
    
    // Helper method to close sub-windows safely
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
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
    
    class OpenProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Sender_profile profileView = new view.Sender_profile();
            controllor.ProfileController profileController = new controllor.ProfileController(profileView, currentUser);
            profileController.open();
        }
    }

    class OpenCreateOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); // Closes the current dashboard window safely
            
            orderSubmission = new view.OrderSubmissionForm();
            
            // Sync top bar labels
            orderSubmission.setUsernameLabel(currentUser.getUsername());
            orderSubmission.setRoleLabel(currentUser.getRole());
            
            // --- GENERATE UNIQUE 6-DIGIT TRACKING ID IMMEDIATELY ---
            DAO.OrderDAO orderDao = new DAO.OrderDAO();
            boolean isUnique = false;
            java.util.Random rand = new java.util.Random();

            do {
                int randomNum = 100000 + rand.nextInt(900000); 
                currentTrackingId = String.valueOf(randomNum);
                isUnique = !orderDao.isTrackingIdExists(currentTrackingId);
            } while (!isUnique);

            // Push the generated ID to the UI right away!
            orderSubmission.setInitialTrackingId(currentTrackingId);
            // -------------------------------------------------------
            
            // Connect the order form button actions
            orderSubmission.addSubmitListener(new SubmitOrderListener());
            orderSubmission.addDashboardListener(new BackToDashboardFromOrder());
            orderSubmission.addLogoutListener(new LogoutFromOrderListener());
            orderSubmission.addMyProfileListener(new NavigateToProfileFromOrder());
            
            orderSubmission.setVisible(true);
        }
    }

    // =========================================================================
    // ORDER SUBMISSION FORM LISTENERS
    // =========================================================================

    class SubmitOrderListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            
            // 1. Grab all the current inputs from the UI fields
            String name = orderSubmission.getReceiverNameInput();
            String email = orderSubmission.getReceiverEmailInput();
            String contact = orderSubmission.getReceiverContactInput();
            String location = orderSubmission.getReceiverLocationInput();
            String street = orderSubmission.getStreetInput();
            String description = orderSubmission.getDescriptionInput();
            double weight = orderSubmission.getWeightInput();
            double cost = orderSubmission.getTotalCostInput();

            // Validate mandatory fields
            if (name.isEmpty() || contact.isEmpty() || location.isEmpty() || weight <= 0.0) {
                JOptionPane.showMessageDialog(orderSubmission, "Please fill in all required fields!");
                return;
            }

            // 2. Instantiate the Model with the pre-generated tracking ID
            Model.Order newOrder = new Model.Order(currentTrackingId, name, email, contact, location, street, weight, cost, description);

            // 3. Commit the transaction to the database via DAO mapping layer
            DAO.OrderDAO orderDao = new DAO.OrderDAO();
            boolean success = orderDao.saveOrder(newOrder, currentUser.getUserID()); 

            if (success) {
                // Instantly inject the structured data into the White Bill panel
                orderSubmission.updateBillSection(newOrder); 
                
                JOptionPane.showMessageDialog(orderSubmission, "Order Placed Successfully!\nTracking ID: " + currentTrackingId);
                
                // Clear the form fields so they can type another order
                orderSubmission.clearOrderForm(); 
                
                // --- GENERATE THE NEXT UNIQUE ID FOR CONTINUOUS INPUT ---
                boolean isNewUnique = false;
                java.util.Random rand = new java.util.Random();
                do {
                    int randomNum = 100000 + rand.nextInt(900000); 
                    currentTrackingId = String.valueOf(randomNum);
                    isNewUnique = !orderDao.isTrackingIdExists(currentTrackingId);
                } while (!isNewUnique);

                // Dynamically update the label with the next ID sequence
                orderSubmission.setInitialTrackingId(currentTrackingId);
                // --------------------------------------------------------
                
            } else {
                JOptionPane.showMessageDialog(orderSubmission, "Database Error: Could not save order records.");
            }
        }
    }

    class BackToDashboardFromOrder implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeSubWindow(orderSubmission); 
            
            // Recreate the dashboard safely when going back
            view.Sender_Dashboard newDashboard = new view.Sender_Dashboard();
            controllor.UserController dashboardController = new controllor.UserController(newDashboard, currentUser);
            dashboardController.open(); 
        }
    }

    class LogoutFromOrderListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            closeSubWindow(orderSubmission); // Close order form frame safely
            
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }

    class NavigateToProfileFromOrder implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            closeSubWindow(orderSubmission); // 1. Completely destroys the active Order screen
            
            // 2. Create the Profile View
            view.Sender_profile profileView = new view.Sender_profile();
            
            // 3. Start up the Profile Controller and hand it the view + user data
            controllor.ProfileController profileController = new controllor.ProfileController(profileView, currentUser);
            profileController.open();
        }
    }
}