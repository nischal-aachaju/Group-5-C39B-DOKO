/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class Manager_ProfileController {
    
    // Ensure 'view.Manager_profile' matches the exact name of your JFrame file!
    private final view.Manager_profileEdit managerProfile;
    private final userData currentUser;

    public Manager_ProfileController(view.Manager_profileEdit managerProfile, userData currentUser) {
        this.managerProfile = managerProfile;
        this.currentUser = currentUser;
        
        // Run this immediately to populate the text fields!
        initProfileSection();
        
        // Connect the navigation and pop-up buttons
        this.managerProfile.addDashboardListener(new OpenDashboardListener());
        this.managerProfile.addResetPasswordListener(new OpenResetPasswordListener());
        this.managerProfile.addLogoutListener(new LogoutListener());
        
        // Note: If you want to wire up the sidebar buttons on this page 
        // (ActiveOrders, AssignOrder, ManageUser, WorkLoad), add their listeners here!
    }

    public void open() {
        this.managerProfile.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.managerProfile);
        if (window != null) {
            window.dispose();
        } else {
            this.managerProfile.setVisible(false);
        }
    }

    // --- Profile Setup and Logic ---

    public void initProfileSection() {
        // 1. Lock the fields by default
        this.managerProfile.setProfileEditable(false);
        
        // 2. Set the top-right corner labels instantly
        this.managerProfile.setUsernameLabel(currentUser.getUsername());
        this.managerProfile.setRoleLabel(currentUser.getRole());
        
        // 3. Load the current user's data into the text fields
        this.managerProfile.setProfileData(
            String.valueOf(currentUser.getUserID()), 
            currentUser.getUsername(), 
            currentUser.getEmail(), 
            currentUser.getPhone(), 
            currentUser.getAddress()
        );
        
        // 4. Connect the action buttons
        this.managerProfile.addEditProfileListener(new EditProfileListener());
        this.managerProfile.addSaveProfileListener(new SaveProfileListener());
    }

    class EditProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            managerProfile.setProfileEditable(true);
        }
    }

    class SaveProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Grab the updated fields
            String newPhone = managerProfile.getUpdatedPhone();
            String newAddress = managerProfile.getUpdatedAddress();

            // 2. Send the update to the DAO
            DAO.userDAO dao = new DAO.userDAO();
            boolean success = dao.updateProfile(currentUser.getUserID(), newPhone, newAddress);

            // 3. Handle the result
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(managerProfile, "Manager Profile Updated Successfully!");
                
                // Update the memory object
                currentUser.setPhone(newPhone);
                currentUser.setAddress(newAddress);
                
                // Lock fields back down
                managerProfile.setProfileEditable(false);
            } else {
                javax.swing.JOptionPane.showMessageDialog(managerProfile, "Failed to update profile. Check database connection.");
            }
        }
    }

    // =========================================================================
    // NAVIGATION & POPUP LISTENERS
    // =========================================================================

    class OpenDashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the Manager Profile screen
            close(); 
            
            // 2. Route directly back to the Manager Dashboard
            view.Manager_Dashboard dashboardView = new view.Manager_Dashboard();
            
            // Assuming your controller for the manager dashboard is named ManagerController
            new controllor.ManagerController(dashboardView, currentUser).open();
        }
    }
    
    class OpenResetPasswordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Create the Reset Password View
            view.OTPBasedPasswordReset resetView = new view.OTPBasedPasswordReset();
            
            // 2. Pass it to our shared ResetPasswordController
            controllor.ResetPasswordController resetController = new controllor.ResetPasswordController(resetView, currentUser);
            
            // 3. Open the window on top of the Manager profile!
            resetController.open();
        }
    }

    class LogoutListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Close the Manager Profile screen
            close(); 
            
            // 2. Re-open the Login window securely
            view.login loginView = new view.login();
            new controllor.LoginController(loginView).open();
        }
    }
}