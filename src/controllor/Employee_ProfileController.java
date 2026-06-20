package controllor;

import Model.userData;
import view.Employee_profile;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class Employee_ProfileController {
    
    private final Employee_profile employeeProfile;
    private final userData currentUser;

    public Employee_ProfileController(Employee_profile employeeProfile, userData currentUser) {
        this.employeeProfile = employeeProfile;
        this.currentUser = currentUser;
        
        // Run this immediately to populate the text fields!
        initProfileSection();
        
        // Connect the Dashboard button
        this.employeeProfile.addDashboardListener(new OpenDashboardListener());
        this.employeeProfile.addResetPasswordListener(new OpenResetPasswordListener());
        // (Note: If your Employee profile page has other sidebar buttons like "Manage Orders", 
        // you would add their listeners right here!)
    }

    public void open() {
        this.employeeProfile.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.employeeProfile);
        if (window != null) {
            window.dispose();
        } else {
            this.employeeProfile.setVisible(false);
        }
    }

    // --- Profile Setup and Logic ---

    public void initProfileSection() {
        // 1. Lock the fields by default
        this.employeeProfile.setProfileEditable(false);
        
        // --- Set the top-right corner labels instantly! ---
        this.employeeProfile.setUsernameLabel(currentUser.getUsername());
        this.employeeProfile.setRoleLabel(currentUser.getRole());
        
        // 2. Load the current user's data into the text fields
        this.employeeProfile.setProfileData(
            String.valueOf(currentUser.getUserID()), 
            currentUser.getUsername(), 
            currentUser.getEmail(), 
            currentUser.getPhone(), 
            currentUser.getAddress()
        );
        
        // 3. Connect the action buttons
        this.employeeProfile.addEditProfileListener(new EditProfileListener());
        this.employeeProfile.addSaveProfileListener(new SaveProfileListener());
        this.employeeProfile.addLogoutListener(new LogoutListener());
        this.employeeProfile.addMyShipmentsListener(new MyShipmentListener());
        this.employeeProfile.addManageOrdersListener(new OpenManageOrdersListener());
        this.employeeProfile.addOrdersHistoryListener(new OpenOrdersHistoryListener());
    }

    class EditProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            employeeProfile.setProfileEditable(true);
        }
    }

    class SaveProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // 1. Only grab the fields that are allowed to change
            String newPhone = employeeProfile.getUpdatedPhone();
            String newAddress = employeeProfile.getUpdatedAddress();

            // 2. Send the stripped-down update to the DAO
            DAO.userDAO dao = new DAO.userDAO();
            boolean success = dao.updateProfile(currentUser.getUserID(), newPhone, newAddress);

            // 3. Handle the result
            if (success) {
                javax.swing.JOptionPane.showMessageDialog(employeeProfile, "Profile Updated Successfully!");
                
                // Update the local object memory
                currentUser.setPhone(newPhone);
                currentUser.setAddress(newAddress);
                
                // Lock the fields back down
                employeeProfile.setProfileEditable(false);
            } else {
                javax.swing.JOptionPane.showMessageDialog(employeeProfile, "Failed to update profile. Check database connection.");
            }
        }
    }

    // =========================================================================
    // NAVIGATION LISTENERS
    // =========================================================================

    class OpenDashboardListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            
            // 1. Close the Profile screen
            close(); 
            
            // 2. Create the Employee Dashboard View
            view.Employee_Dashboard dashboardView = new view.Employee_Dashboard();
            
            // 3. Create the Employee Controller and pass the exact same user data back!
            controllor.EmployeeController employeeController = new controllor.EmployeeController(dashboardView, currentUser);
            
            // 4. Open the Dashboard!
            employeeController.open();
        }
    }

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
    class OpenResetPasswordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            // 1. Create the Reset Password View
            view.OTPBasedPasswordReset resetView = new view.OTPBasedPasswordReset();
            
            // 2. Pass it to the ResetPasswordController along with the currentUser
            controllor.ResetPasswordController resetController = new controllor.ResetPasswordController(resetView, currentUser);
            
            // 3. Open the window!
            resetController.open();
            
            // Note: We do NOT call close() here because we want the Reset Password 
            // screen to act like a popup that opens on top of their profile.
        }
    }

    class OpenOrdersHistoryListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.Employee_Order_History profileView = new view.Employee_Order_History();
            controllor.EmployeeOrderHistoryController profileController = new controllor.EmployeeOrderHistoryController(profileView, currentUser);
            profileController.open();
        }
    }
    
    class OpenManageOrdersListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.EmployeeOrderEdit profileView = new view.EmployeeOrderEdit();
            controllor.EmployeeOrderEditController profileController = new controllor.EmployeeOrderEditController(profileView, currentUser);
            profileController.open();
        }
    }
    
    class MyShipmentListener implements java.awt.event.ActionListener {
        @Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
            close(); 
            view.EmployeeOrderCancellation EmployeeOrder = new view.EmployeeOrderCancellation();
            controllor.EmployeeOrderCancellationController EmployeeOrderController = new controllor.EmployeeOrderCancellationController(EmployeeOrder, currentUser);
            EmployeeOrderController.open();
        }
    }
   
}
