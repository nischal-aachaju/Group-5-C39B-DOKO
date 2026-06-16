///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
package controllor;
//
///**
// *
// * @author User
// */
//public class ManagerOrderEditController {
//    
//}
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import view.ManagerOrderEdit;



public class ManagerOrderEditController {

   private final view.ManagerOrderEdit view;
   private final Model.userData currentUser; // Must match Model.userData
    // private final OrderModel model; // Uncomment when your Model layer is ready

    /**
     * Constructor injecting the view dependency.
     */
    public ManagerOrderEditController(view.ManagerOrderEdit view, Model.userData currentUser) {
        this.view = view;
        this.currentUser = currentUser;
        this.view.addLogoutListener(new LogoutListener());
        this.view.addDashboardListener(new DashboardListener());
    }

    public void open() {
        this.view.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.view);
        if (window != null) {
            window.dispose();
        } else {
            this.view.setVisible(false);
        }
    }

    // --- Action Handlers ---

    private void handleSearchOrder() {
        String trackingId = view.getTrackingIdtextfield().getText().trim();
        
        if (trackingId.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter a tracking ID to search.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("Fetching details for Tracking ID: " + trackingId);
       
    }

    private void handleSaveOrder() {
        String trackingId = view.getTrackingIdtextfield().getText().trim();
        String receiverName = view.getReceivernametextfield().getText().trim();
        String receiverAddress = view.getReceiveraddresstextfield().getText().trim();
        String receiverEmail = view.getReceiveremailtextfield().getText().trim();
        String senderAddress = view.getSenderaddresstextfield().getText().trim();

        // Basic Validation
        if (receiverName.isEmpty() || receiverAddress.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Receiver Name and Address fields cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Saving updated data for Tracking ID: " + trackingId);
        
        /*
        // Example Model Update:
        boolean updated = model.updateOrder(trackingId, receiverName, receiverAddress, receiverEmail, senderAddress);
        if(updated) {
            JOptionPane.showMessageDialog(view, "Order updated successfully!");
            setFormEditable(false); // Lock fields after successful save
        }
        */
        
        // Temporary feedback until Model is connected
        setFormEditable(false); 
    }

    private void handleReturn() {
        System.out.println("Returning to previous state/view...");
        setFormEditable(false);
        // Clear editable fields if necessary
    }

    private void navigateTo(String viewName) {
        System.out.println("Manager switching dashboard view to: " + viewName);
        // Logic to switch view layers/cards on your main frame goes here
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(view, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("Processing logout procedures...");
            view.dispose(); // Closes the current dashboard window
            // Code to instantiate and open LoginView goes here
        }
    }

    /**
     * Helper to safely toggle form editability state and button availability.
     */
    private void setFormEditable(boolean isEditable) {
        view.getReceivernametextfield().setEditable(isEditable);
        view.getReceiveraddresstextfield().setEditable(isEditable);
        view.getReceiveremailtextfield().setEditable(isEditable);
        view.getSenderaddresstextfield().setEditable(isEditable);
        
        // Control button states to prevent invalid user paths
        view.getSavebutton().setEnabled(isEditable);
        view.getEditbutton().setEnabled(!isEditable);
    }
    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the manager dashboard
            close(); 
            
            // 2. Re-open the Login window safely
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
    class DashboardListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        close();

        view.Manager_Dashboard dashboardView = new view.Manager_Dashboard();
        controllor.ManagerController dashboardController = new controllor.ManagerController(dashboardView,currentUser);
        dashboardController.open();
    }
}
}
