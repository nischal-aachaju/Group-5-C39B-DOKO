/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package controllor;

import Model.userData;
import view.Manager_Dashboard;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;

public class ManagerController {
    
    private final Manager_Dashboard managerView;
    private final userData currentUser;

    // Notice we pass the loggedInUser data into this constructor!
    public ManagerController(Manager_Dashboard managerView, userData currentUser) {
        this.managerView = managerView;
        this.currentUser = currentUser;
        
        // 1. Instantly set the Name and Role labels on the screen
        this.managerView.setUsernameLabel(currentUser.getUsername());
        this.managerView.setRoleLabel(currentUser.getRole());
        
        // 2. Connect the logout button
        this.managerView.addLogoutListener(new LogoutListener());
    }

    public void open() {
        this.managerView.setVisible(true);
    }

    public void close() {
        Window window = SwingUtilities.getWindowAncestor(this.managerView);
        if (window != null) {
            window.dispose();
        } else {
            this.managerView.setVisible(false);
        }
    }

    // --- Action Listener for Logout Button ---
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
}