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
        this.managerProfile.addManageUserListener(new OpenManageUserListener());
        this.managerProfile.addWorkloadListener(new OpenWorkloadListener() );
        this.managerProfile.addActiveOrdersListener(new OpenActiveOrdersListener() );
        this.managerProfile.addMyProfileListener(new OpenManagerProfileListener());
         this.managerProfile.addAssiggnedOrdersListener(new OpenAssiggnedrdersListener() );
         this.managerProfile.addManageOrdersListener(new OpenManageOrdersListener());



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
    
class OpenManageUserListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            
            // 2. Create the User Management View
            view.Useraccountmanagement manageUserView = new view.Useraccountmanagement();
            
            // 3. Pass it entirely to your dedicated Manage User Controller
            controllor.ManageUserController manageUserController = new controllor.ManageUserController(manageUserView, currentUser);
            
            // 4. Open the User Management page!
            manageUserController.open();
        }
}
class OpenWorkloadListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.Manager_Workload WorkloadView = new view.Manager_Workload();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerWorkloadController managerAssignOrderController = new controllor.ManagerWorkloadController(WorkloadView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerAssignOrderController.open();
        }
    }
class OpenActiveOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.Manager_active_orders activeorderView = new view.Manager_active_orders();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerActiveOrdersController managerAssignOrderController = new controllor.ManagerActiveOrdersController(activeorderView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerAssignOrderController.open();
        }
    }
class OpenAssiggnedrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.assignedorder assignedorderView = new view.assignedorder();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerAssignOrderController managerAssignOrderController = new controllor.ManagerAssignOrderController(assignedorderView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerAssignOrderController.open();
        }
    }
 class OpenManagerProfileListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the exact Manager Profile View
            view.Manager_profileEdit profileView = new view.Manager_profileEdit();
            
            // 3. Pass it entirely to your dedicated Manager Profile Controller
            controllor.Manager_ProfileController profileController = new controllor.Manager_ProfileController(profileView, currentUser);
            
            // 4. Open the profile page!
            profileController.open();
        }
    }
 class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            // 1. Close the current Manager Dashboard
            close(); 
            
            // 2. Create the Manager Order Edit View
            view.ManagerOrderEdit managerOrderEditView = new view.ManagerOrderEdit();
            
            // 3. Fixed spelling from "controller" to "controllor" to perfectly match your package structure
            controllor.ManagerOrderEditController managerOrderEditController = new controllor.ManagerOrderEditController(managerOrderEditView, currentUser);
            
            // 4. Open the Manager Order Edit page!
            managerOrderEditController.open();
        }
    }

}