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
}