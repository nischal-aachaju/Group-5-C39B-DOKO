package controllor;

import Model.userData;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;

public class HistoryController {
    
    // CHANGE 'view.OrdersHistory' TO YOUR ACTUAL HISTORY JFRAME NAME!
    private final view.Sender_Order_History historyView; 
    private final userData currentUser;

    public HistoryController(view.Sender_Order_History historyView, userData currentUser) {
        this.historyView = historyView;
        this.currentUser = currentUser;
        
        // Sync top bar labels (Make sure your History view has these methods!)
        this.historyView.setUsernameLabel(currentUser.getUsername());
        this.historyView.setRoleLabel(currentUser.getRole());
        
        // Connect the back button
        this.historyView.addDashboardListener(new BackToDashboardListener());
        this.historyView.addLogoutListener(new LogoutListener());
        this.historyView.addCreateOrderListener(new NavigateToCreateOrder());
        this.historyView.addMyShipmentsListener(new NavigateToShipments());
        this.historyView.addMyProfileListener(new NavigateToProfile());
    }

    public void open() {
        this.historyView.setVisible(true);
    }

    public void close() {
        if (this.historyView != null) {
            this.historyView.dispose(); // Destroys the window safely
        }
    }

    // --- NAVIGATION LISTENERS ---

    class BackToDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); 
            view.Sender_Dashboard dashboardView = new view.Sender_Dashboard();
            new controllor.UserController(dashboardView, currentUser).open();
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
    // =========================================================================
    // CROSS-NAVIGATION LISTENERS (Sub-page to Sub-page)
    // =========================================================================

    class NavigateToCreateOrder implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); // Destroy the History window
            
            view.OrderSubmissionForm orderView = new view.OrderSubmissionForm();
            new controllor.UserController(orderView, currentUser).open();
        }
    }

    class NavigateToShipments implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); // Destroy the History window
            
            view.SenderOrderCancellation shipmentsView = new view.SenderOrderCancellation();
            new controllor.Sender_shipment_controller(shipmentsView, currentUser).open();
        }
    }

    class NavigateToProfile implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close(); // Destroy the History window
            
            view.Sender_profile profileView = new view.Sender_profile();
            new controllor.ProfileController(profileView, currentUser).open();
        }
    }
}