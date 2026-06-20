package controllor;

import Model.userData;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class AdminProfileController {
    
    // Replace 'view.AdminProfile' with the exact name of your JFrame file!
    private final view.NewAdmin_Profile profileView;
    private final userData currentUser;

    public AdminProfileController(view.NewAdmin_Profile profileView, userData currentUser) {
        this.profileView = profileView;
        this.currentUser = currentUser;
        
        // 1. Setup Topbar
        this.profileView.setTopBar(currentUser.getUsername(), currentUser.getRole());
        
        // 2. Pre-fill the text fields with the logged-in user's data
        this.profileView.setProfileData(
            String.valueOf(currentUser.getUserID()), 
            currentUser.getUsername(), 
            currentUser.getEmail(), 
            currentUser.getPhone(), 
            currentUser.getAddress() // Assumes your userData model has getAddress()
        );
        
        // 3. Lock the form by default
        this.profileView.setFormEditable(false);
        
        // 4. Attach Listeners
        this.profileView.addEditListener(new EditProfileListener());
        this.profileView.addSaveListener(new SaveProfileListener());
        this.profileView.addResetPasswordListener(new OpenResetPasswordListener());
        
        this.profileView.addDashboardListener(new DashboardListener());
        this.profileView.addLogoutListener(new LogoutListener());
        this.profileView.addbranchNetwork(new branchNetworkListener());
        this.profileView.addPriceConfiguration(new PriceConfigurationListener());
        this.profileView.addManageOrdersListener(new ManageOrdersListener()); 
        this.profileView.addManageUserListener(new openManageUserListenerListener());
        this.profileView.addWorkloadListener(new openworkloadListener());
    }

    public void open() {
        this.profileView.setVisible(true);
        this.profileView.setLocationRelativeTo(null);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.profileView);
        if (window != null) {
            window.dispose();
        } else {
            this.profileView.setVisible(false);
        }
    }

    // =========================================================================
    // EDIT & SAVE LOGIC
    // =========================================================================

    class EditProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            profileView.setFormEditable(true); // Unlock Email, Phone, Address
        }
    }

    class SaveProfileListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String newEmail = profileView.getUpdatedEmail();
            String newPhone = profileView.getUpdatedPhone();
            String newAddress = profileView.getUpdatedAddress();
            
            if (newEmail.isEmpty() || newPhone.isEmpty() || newAddress.isEmpty()) {
                JOptionPane.showMessageDialog(profileView, "Fields cannot be empty!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            DAO.userDAO dao = new DAO.userDAO();
            boolean success = dao.updateUserProfile(currentUser.getUserID(), newEmail, newPhone, newAddress);
            
            if (success) {
                JOptionPane.showMessageDialog(profileView, "Profile successfully updated!");
                
                // Update the active currentUser object so changes reflect across the app
                currentUser.setEmail(newEmail);
                currentUser.setPhone(newPhone);
                currentUser.setAddress(newAddress);
                
                profileView.setFormEditable(false); // Lock it back down
            } else {
                JOptionPane.showMessageDialog(profileView, "Database error. Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // RESET PASSWORD ROUTING
    // =========================================================================

    class OpenResetPasswordListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            // 1. Create the Reset Password View
            view.OTPBasedPasswordReset resetView = new view.OTPBasedPasswordReset();
            
            // 2. Pass it to our shared ResetPasswordController
            controllor.ResetPasswordController resetController = new controllor.ResetPasswordController(resetView, currentUser);
            
            // 3. Open the window on top of the Manager profile!
            resetController.open();
        }
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    class DashboardListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            // Assuming your admin dashboard is Admin_Dashboard
            view.Admin_Dashboard dashboardView = new view.Admin_Dashboard();
            new controllor.AdminController(dashboardView, currentUser).open();
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
    class ManageOrdersListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
    
            view.AdminOrderEdit ManageOrder = new view.AdminOrderEdit();
            controllor.AdminManageOrderController moc = new controllor.AdminManageOrderController(ManageOrder, currentUser);
            moc.open();
        }
    }
    class branchNetworkListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
    
            view.adminBranchNetworks adminBranchOrder = new view.adminBranchNetworks();
            controllor.AdminBranchController aboc = new controllor.AdminBranchController(adminBranchOrder, currentUser);
            aboc.open();
        }
    }
    class PriceConfigurationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.priceConfiguration priceConfig = new view.priceConfiguration();
            controllor.PriceConfigController pcc = new controllor.PriceConfigController(priceConfig, currentUser);
            pcc.open();
        }
    } 
    class openworkloadListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.WorkLoad wl = new view.WorkLoad();
            controllor.AdminWorkloadController wlc = new controllor.AdminWorkloadController(wl, currentUser);
            wlc.open();
        }
    }
    class openManageUserListenerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            close();
            view.ManageUser uam = new view.ManageUser();
            controllor.AdminManageUserController wlc = new controllor.AdminManageUserController(uam, currentUser);
            wlc.open();
        }
    }
}
