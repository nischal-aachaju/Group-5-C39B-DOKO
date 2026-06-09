package controllor;

import Model.userData;
import view.Sender_Dashboard; 
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class UserController {
    
    private final Sender_Dashboard userView;
    private final userData currentUser;

    public UserController(Sender_Dashboard userView, userData currentUser) {
        this.userView = userView;
        this.currentUser = currentUser;
        
        // 1. Set the Name and Role labels on the dashboard
        this.userView.setUsernameLabel(currentUser.getUsername());
        this.userView.setRoleLabel(currentUser.getRole());
        
        // 2. Connect the buttons
        this.userView.addLogoutListener(new LogoutListener());
        this.userView.addMyProfileListener(new OpenProfileListener());
    }

    public void open() {
        this.userView.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.userView);
        if (window != null) {
            window.dispose();
        } else {
            this.userView.setVisible(false);
        }
    }

    // --- Action Listeners ---
    
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
            close(); // Close the dashboard
            
            // Route to the Profile Screen
            view.Sender_profile profileView = new view.Sender_profile();
            controllor.ProfileController profileController = new controllor.ProfileController(profileView, currentUser);
            profileController.open();
        }
    }
}