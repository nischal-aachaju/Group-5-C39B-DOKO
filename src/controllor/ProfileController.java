package controllor;

import Model.userData;
import view.Sender_profile;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class ProfileController {
    
    private final Sender_profile userProfile;
    private final userData currentUser;

    public ProfileController(Sender_profile userProfile, userData currentUser) {
        this.userProfile = userProfile;
        this.currentUser = currentUser;
        
        // Run this immediately to populate the text fields!
        initProfileSection();
        // Connect the Dashboard button
        this.userProfile.addDashboardListener(new OpenDashboardListener());
        this.userProfile.addCreateOrderListener(new NavigateToOrderFromProfile());   
        this.userProfile.addMyShipmentsListener(new NavigateToShipmentsFromProfile());

        this.userProfile.addOrdersHistoryListener(new NavigateToHistoryFromProfile());
    }

    public void open() {
        this.userProfile.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.userProfile);
        if (window != null) {
            window.dispose();
        } else {
            this.userProfile.setVisible(false);
        }
    }

    // --- Profile Setup and Logic ---

public void initProfileSection() {
        // 1. Lock the fields by default
        this.userProfile.setProfileEditable(false);
        
        // --- NEW UPDATE: Set the top-right corner labels instantly! ---
        this.userProfile.setUsernameLabel(currentUser.getUsername());
        this.userProfile.setRoleLabel(currentUser.getRole());
        
        // 2. Load the current user's data into the text fields
        this.userProfile.setProfileData(
            String.valueOf(currentUser.getUserID()), 
            currentUser.getUsername(), 
            currentUser.getEmail(), 
            currentUser.getPhone(), 
            currentUser.getAddress()
        );
        
        // 3. Connect the buttons
        this.userProfile.addEditProfileListener(new EditProfileListener());
        this.userProfile.addSaveProfileListener(new SaveProfileListener());
        this.userProfile.addLogoutListener(new LogoutListener());
    }

    class EditProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            userProfile.setProfileEditable(true);
        }
    }

  class SaveProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Only grab the fields that are allowed to change
            String newPhone = userProfile.getUpdatedPhone();
            String newAddress = userProfile.getUpdatedAddress();

            // 2. Send the stripped-down update to the DAO
            DAO.userDAO dao = new DAO.userDAO();
            boolean success = dao.updateProfile(currentUser.getUserID(), newPhone, newAddress);

            // 3. Handle the result
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(userProfile, "Profile Updated Successfully!");
                
                // Update the local object memory
                currentUser.setPhone(newPhone);
                currentUser.setAddress(newAddress);
                
                // Lock the fields back down
                userProfile.setProfileEditable(false);
            } else {
                javax.swing.JOptionPane.showMessageDialog(userProfile, "Failed to update profile. Check database connection.");
            }
        }
    }
 // --- Add this at the bottom of ProfileController.java ---

    class OpenDashboardListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            
            // 1. Close the Profile screen
            close(); 
            
            // 2. Create the Dashboard View
            view.Sender_Dashboard dashboardView = new view.Sender_Dashboard();
            
            // 3. Create the User Controller and pass the exact same user data back!
            controllor.UserController userController = new controllor.UserController(dashboardView, currentUser);
            
            // 4. Open the Dashboard!
            userController.open();
        }
    }
    // --- Add this at the bottom of ProfileController.java ---

    class LogoutListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            
            // 1. Close the Profile screen
            close(); 
            
            // 2. Re-open the Login window securely
            view.login loginView = new view.login();
            controllor.LoginController loginController = new controllor.LoginController(loginView);
            loginController.open();
        }
    }
    class NavigateToOrderFromProfile implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); // 1. Close the Profile screen
            
            // 2. Open the Order Submission Form window
            view.OrderSubmissionForm orderView = new view.OrderSubmissionForm();
            
            // 3. Hand control back to the UserController, passing the user data
            // (Make sure UserController has a constructor that accepts OrderSubmissionForm!)
            controllor.UserController userController = new controllor.UserController(orderView, currentUser);
            userController.open(); 
        }
    }
    class NavigateToShipmentsFromProfile implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); // Closes the Profile window completely
            
            view.SenderOrderCancellation shipmentsView = new view.SenderOrderCancellation();
            new controllor.Sender_shipment_controller(shipmentsView, currentUser).open();
        }
    }
    class NavigateToHistoryFromProfile implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); // Destroy the Profile window
            
            view.Sender_Order_History historyView = new view.Sender_Order_History();
            new controllor.HistoryController(historyView, currentUser).open();
        }
    }
}